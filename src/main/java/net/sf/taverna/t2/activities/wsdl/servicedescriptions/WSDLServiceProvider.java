package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.wsdl.Operation;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.taverna.t2.activities.wsdl.WSDLActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceDescriptionProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.wsdl.parser.UnknownOperationException;
import net.sf.taverna.wsdl.parser.WSDLParser;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

public class WSDLServiceProvider
		extends
		AbstractConfigurableServiceDescriptionProvider<WSDLServiceProviderConfig> {

	private static Logger logger = Logger.getLogger(WSDLServiceProvider.class);

	public List<ServiceDescription<WSDLActivityConfigurationBean>> getServiceDescriptions() {
		List<ServiceDescription<WSDLActivityConfigurationBean>> descriptions = new ArrayList<ServiceDescription<WSDLActivityConfigurationBean>>();
		URI wsdl = serviceProviderConfig.getURI();
		logger.info("About to parse wsdl:" + wsdl);
		WSDLParser parser = null;
		try {
			parser = new WSDLParser(wsdl.toASCIIString());
			List<Operation> operations = parser.getOperations();
			for (Operation op : operations) {
				WSDLServiceDescription item = new WSDLServiceDescription();
				try {
					item.setOperation(op.getName());
					item.setUse(parser.getUse(item.getOperation()));
					item.setStyle(parser.getStyle());
					item.setURI(wsdl);					
					descriptions.add(item);
				} catch (UnknownOperationException e) {
					String message = "Encountered an unexpected operation name:"
							+ item.getOperation();
					logger.error(message, e);
					JOptionPane.showMessageDialog(null, message, "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (ParserConfigurationException e) {
			String message = "Error configuring the WSDL parser";
			logger.error(message, e);
			JOptionPane.showMessageDialog(null, message, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (WSDLException e) {
			String message = "There was an error with the wsdl: " + wsdl;
			logger.error(message, e);
			JOptionPane.showMessageDialog(null, message, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			String[] message = new String[] {
					"There was an IO error parsing the wsdl: " + wsdl,
					"Possible reason: the wsdl location was incorrect." };
			logger.error(message, e);
			JOptionPane.showMessageDialog(null, message, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (SAXException e) {
			String message = "There was an error with the XML in the wsdl: "
					+ wsdl;
			logger.error(message, e);
			JOptionPane.showMessageDialog(null, message, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (IllegalArgumentException e) { // a problem with the wsdl url
			String[] message = new String[] {
					"There was an error with the wsdl: " + wsdl,
					"Possible reason: the wsdl location was incorrect." };
			logger.error(message, e);
			JOptionPane.showMessageDialog(null, message, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) { // anything else we did not expect
			String message = "There was an error with the wsdl: " + wsdl;
			logger.error(message, e);
			JOptionPane.showMessageDialog(null, message, "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		logger.info("Finished parsing WSDL:" + wsdl);
		return descriptions;
	}

	public List<WSDLServiceProviderConfig> getDefaultConfigurations() {
		List<WSDLServiceProviderConfig> defaults = new ArrayList<WSDLServiceProviderConfig>();
		defaults.add(new WSDLServiceProviderConfig(
				"http://soap.genome.jp/KEGG.wsdl"));
		defaults.add(new WSDLServiceProviderConfig(
				"http://soap.bind.ca/wsdl/bind.wsdl"));
		return defaults;
	}

}
