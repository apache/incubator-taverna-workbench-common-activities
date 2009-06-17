package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.wsdl.Operation;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.wsdl.parser.UnknownOperationException;
import net.sf.taverna.wsdl.parser.WSDLParser;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class WSDLServiceProvider extends
		AbstractConfigurableServiceProvider<WSDLServiceProviderConfig> {

	private static final String WSDL_SERVICE = "WSDL service";
	
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(WSDLServiceProvider.class);

	public WSDLServiceProvider() {
		super(new WSDLServiceProviderConfig("http://somehost/service?wsdl"));
	}

	public String getName() {
		return WSDL_SERVICE;
	}

	public List<WSDLServiceProviderConfig> getDefaultConfigurations() {
		List<WSDLServiceProviderConfig> defaults = new ArrayList<WSDLServiceProviderConfig>();
		// TODO: Defaults should come from a config/resource file
		defaults.add(new WSDLServiceProviderConfig(
		"http://www.ebi.ac.uk/xembl/XEMBL.wsdl"));
		defaults.add(new WSDLServiceProviderConfig(
		"http://soap.genome.jp/KEGG.wsdl"));
		defaults.add(new WSDLServiceProviderConfig(
				"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/soap/eutils.wsdl"));
		defaults.add(new WSDLServiceProviderConfig(
				"http://soap.bind.ca/wsdl/bind.wsdl"));
		defaults.add(new WSDLServiceProviderConfig(
				"http://www.ebi.ac.uk/ws/services/urn:Dbfetch?wsdl"));
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
			callBack.status("Found " + operations.size() + " WSDL operations:" + wsdl);
			List<WSDLServiceDescription> items = new ArrayList<WSDLServiceDescription>();
			for (Operation op : operations) {
				WSDLServiceDescription item = new WSDLServiceDescription();
				try {
					item.setOperation(op.getName());
					item.setUse(parser.getUse(op.getName()));
					item.setStyle(parser.getStyle());					
					item.setURI(wsdl);
					item.setDescription(parser.getOperationDocumentation(op.getName()));
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

}
