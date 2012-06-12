package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.wsdl.Operation;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.taverna.t2.activities.wsdl.WSDLActivityHealthChecker;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
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

	private CredentialManager credentialManager;

	public static class FlushWSDLCacheOnRemovalObserver implements
			Observer<ServiceDescriptionRegistryEvent> {
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

	private ServiceDescriptionRegistry serviceDescriptionRegistry;

	public WSDLServiceProvider() {
		super(new WSDLServiceProviderConfig("http://somehost/service?wsdl"));
	}

	public String getName() {
		return WSDL_SERVICE;
	}

	public List<WSDLServiceProviderConfig> getDefaultConfigurations() {

		List<WSDLServiceProviderConfig> defaults = new ArrayList<WSDLServiceProviderConfig>();

		// If defaults have failed to load from a configuration file then load them here.
		if (!serviceDescriptionRegistry.isDefaultSystemConfigurableProvidersLoaded()){
			defaults.add(new WSDLServiceProviderConfig(
					"http://www.ebi.ac.uk/xembl/XEMBL.wsdl"));
			defaults.add(new WSDLServiceProviderConfig(
					"http://soap.genome.jp/KEGG.wsdl"));
			// 2009-12-16: 503 server error
			defaults.add(new WSDLServiceProviderConfig(
							"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/soap/eutils.wsdl"));
			defaults.add(new WSDLServiceProviderConfig(
					"http://soap.bind.ca/wsdl/bind.wsdl"));
			defaults.add(new WSDLServiceProviderConfig(
					"http://www.ebi.ac.uk/ws/services/urn:Dbfetch?wsdl"));
		} // else return an empty list

		return defaults;
	}

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
				WSDLServiceDescription item = new WSDLServiceDescription(credentialManager);
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
					item.setDescription(parser.getOperationDocumentation(name));
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

	@Override
	public String toString() {
		return getName() + " " + getConfiguration().getURI();
	}

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
	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		synchronized (flushObserver) {
			// Add the (static common) observer if the registry does not have it
			if (!serviceDescriptionRegistry.getObservers().contains(flushObserver)) {
				serviceDescriptionRegistry.addObserver(flushObserver);
			}
		}
	}

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

	public String getId() {
		return providerId.toString();
	}

	public void setCredentialManager(CredentialManager credentialManager) {
		this.credentialManager = credentialManager;
	}

}
