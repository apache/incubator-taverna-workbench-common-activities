package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.Name;
import javax.swing.Icon;
import javax.wsdl.Operation;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.taverna.t2.activities.wsdl.WSDLActivityHealthChecker;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.servicedescriptions.events.RemovedProviderEvent;
import net.sf.taverna.t2.servicedescriptions.events.ServiceDescriptionRegistryEvent;
import net.sf.taverna.t2.servicedescriptions.impl.ServiceDescriptionRegistryImpl;
import net.sf.taverna.wsdl.parser.UnknownOperationException;
import net.sf.taverna.wsdl.parser.WSDLParser;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class WSDLServiceProvider extends
		AbstractConfigurableServiceProvider<WSDLServiceProviderConfig> implements
		CustomizedConfigurePanelProvider<WSDLServiceProviderConfig> {

	private static Logger logger = Logger.getLogger(WSDLServiceProvider.class);

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/wsdl");
	
	public static class FlushWSDLCacheOnRemovalObserver implements
			Observer<ServiceDescriptionRegistryEvent> {
		@Override
		public void notify(
				Observable<ServiceDescriptionRegistryEvent> registry,
				ServiceDescriptionRegistryEvent event) throws Exception {
			if (!(event instanceof RemovedProviderEvent)) {
				return;
			}
			RemovedProviderEvent removedProviderEvent = (RemovedProviderEvent) event;
			if (!(removedProviderEvent.getProvider() instanceof WSDLServiceProvider)) {
				return;
			}
			WSDLServiceProvider serviceProvider = (WSDLServiceProvider) removedProviderEvent
					.getProvider();
			URI wsdlLocation = serviceProvider.getConfiguration().getURI();
			WSDLParser.flushCache(wsdlLocation.toASCIIString());
			logger.info("Flushed cache for WSDL " + wsdlLocation);
		}
	}

	private static final String WSDL_SERVICE = "WSDL service";

	private static FlushWSDLCacheOnRemovalObserver flushObserver = new FlushWSDLCacheOnRemovalObserver();
	
	public WSDLServiceProvider() {
		super(new WSDLServiceProviderConfig("http://somehost/service?wsdl"));
	}

	@Override
	public String getName() {
		return WSDL_SERVICE;
	}

/*	public List<WSDLServiceProviderConfig> getDefaultConfigurations() {
		
		List<WSDLServiceProviderConfig> defaults = new ArrayList<WSDLServiceProviderConfig>();
		
		ServiceDescriptionRegistryImpl serviceRegistry = ServiceDescriptionRegistryImpl.getInstance();
		// If defaults have failed to load from a configuration file then load them here.
		if (!serviceRegistry.isDefaultSystemConfigurableProvidersLoaded()){
			
//			defaults.add(new WSDLServiceProviderConfig(
//					"http://www.ebi.ac.uk/xembl/XEMBL.wsdl"));
//			defaults.add(new WSDLServiceProviderConfig(
//					"http://soap.genome.jp/KEGG.wsdl"));
			// 2009-12-16: 503 server error
			defaults.add(new WSDLServiceProviderConfig(
							"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/soap/eutils.wsdl"));
			defaults.add(new WSDLServiceProviderConfig(
					"http://soap.bind.ca/wsdl/bind.wsdl"));
			defaults.add(new WSDLServiceProviderConfig(
					"http://www.ebi.ac.uk/ws/services/urn:Dbfetch?wsdl"));
		} // else return an empty list
		
		return defaults;
	}*/

	@Override
	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		
		URI wsdl = serviceProviderConfig.getURI();	
		
		callBack.status("Parsing wsdl:" + wsdl);
		WSDLParser parser = null;
		try {
			parser = new WSDLParser(wsdl.toASCIIString());
			List<Operation> operations = parser.getOperations();
			callBack.status("Found " + operations.size() + " WSDL operations:"
					+ wsdl);
			List<WSDLServiceDescription> items = new ArrayList<WSDLServiceDescription>();
			for (Operation op : operations) {
				WSDLServiceDescription item = new WSDLServiceDescription();
				try {
					String name = op.getName();
					item.setOperation(name);
					String use = parser.getUse(name);
					String style = parser.getStyle();
					if (!WSDLActivityHealthChecker.checkStyleAndUse(style, use)) {
						logger.warn("Unsupported style and use combination " + style + "/" + use + " for operation " + name + " from " + wsdl);
						continue;
					}
					item.setUse(use);
					item.setStyle(style);
					item.setURI(wsdl);
					item.setDescription(shortenDocumentation(name, parser.getOperationDocumentation(name)));
					items.add(item);
				} catch (UnknownOperationException e) {
					String message = "Encountered an unexpected operation name:"
							+ item.getOperation();
					callBack.fail(message, e);
				}
			}
			callBack.partialResults(items);
			callBack.finished();
		} catch (ParserConfigurationException e) {
			String message = "Error configuring the WSDL parser";
			callBack.fail(message, e);
		} catch (WSDLException e) {
			String message = "There was an error with the wsdl: " + wsdl;
			callBack.fail(message, e);
		} catch (IOException e) {
			String message = "There was an IO error parsing the wsdl: " + wsdl
					+ " Possible reason: the wsdl location was incorrect.";
			callBack.fail(message, e);
		} catch (SAXException e) {
			String message = "There was an error with the XML in the wsdl: "
					+ wsdl;
			callBack.fail(message, e);
		} catch (IllegalArgumentException e) { // a problem with the wsdl url
			String message = "There was an error with the wsdl: " + wsdl + " "
					+ "Possible reason: the wsdl location was incorrect.";
			callBack.fail(message, e);
		} catch (Exception e) { // anything else we did not expect
			String message = "There was an error with the wsdl: " + wsdl;
			callBack.fail(message, e);
		}
	}

	/**
	 * Maximum number of characters to show from an operation description
	 * string.
	 */
	private static final int MAX_LENGTH = 80;
	/**
	 * Characters that may not appear in an operation description string. The
	 * order matters; more strictly prohibited things should come first.
	 */
	private static final char[] PROHIBITED_CHARS = new char[] { '\u0000', '\n',
			'\r', '\f' };

	/**
	 * Create a shortened version of an operation description string.
	 * {@value #IS_HTML}
	 * 
	 * @param name
	 *            The name of the operation, to be taken into account when
	 *            calculating how much space to allow for the description.
	 * @param doc
	 *            The string to trim if necessary.
	 * @return The possibly-shortened string. An ellipsis may be added if
	 *         trimming has happened earlier.
	 */
	private String shortenDocumentation(String name, String doc) {
		if (doc.matches(".*<\\s*[a-zA-Z].*"))
			doc = doc.replaceAll("<.*?>", "");
		for (char c : PROHIBITED_CHARS) {
			int idx = doc.indexOf(c);
			if (idx >= 0)
				doc = doc.substring(0, idx - 1);
		}
		doc = doc.trim().replaceAll("\\s+", " ");
		int lenLimit = MAX_LENGTH - name.length();
		if (lenLimit < 1)
			return "\u2026";
		if (doc.length() > lenLimit)
			doc = doc.substring(0, lenLimit - 1) + "\u2026";
		return doc;
	}

	@Override
	public String toString() {
		return getName() + " " + getConfiguration().getURI();
	}

	@Override
	public Icon getIcon() {
		return WSDLActivityIcon.getWSDLIcon();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		List<String> result;
		result = Arrays.asList(getConfiguration().getURI().toString());
		return result;
	}
	
	/**
	 * Will be set by ServiceDescriptionRegistryImpl 
	 * 
	 * @param registry Registry this provider has been added to
	 */
	public void setServiceDescriptionRegistry(
			ServiceDescriptionRegistry registry) {
		synchronized (flushObserver) {
			// Add the (static common) observer if the registry does not have it			
			if (!registry.getObservers().contains(flushObserver)) {
				registry.addObserver(flushObserver);
			}
		}
	}

	@Override
	@SuppressWarnings("serial")
	public void createCustomizedConfigurePanel(final CustomizedConfigureCallBack<WSDLServiceProviderConfig> callBack) {
			
		AddWSDLServiceDialog addWSDLServiceDialog = new AddWSDLServiceDialog() {
				@Override
				protected void addRegistry(String wsdlURL) {
					
					WSDLServiceProviderConfig providerConfig = new WSDLServiceProviderConfig(wsdlURL);					
					callBack.newProviderConfiguration(providerConfig);
				}
			};
			addWSDLServiceDialog.setVisible(true);		
	}

	@Override
	public String getId() {
		return providerId.toString();
	}

	
	

}
