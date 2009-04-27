package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.rpc.ServiceException;

import net.sf.taverna.t2.activities.soaplab.Soap;
import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;
import net.sf.taverna.t2.activities.soaplab.query.MissingSoaplabException;
import net.sf.taverna.t2.activities.soaplab.query.SoaplabActivityItem;
import net.sf.taverna.t2.activities.soaplab.query.SoaplabCategory;
import net.sf.taverna.t2.activities.soaplab.query.SoaplabScavengerAgent;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

import org.apache.log4j.Logger;

public class SoaplabServiceProvider extends AbstractConfigurableServiceProvider<SoaplabServiceProviderConfig>{

	public SoaplabServiceProvider() {
		super(new SoaplabServiceProviderConfig("http://somehost/soaplab/services/"));
	}

	private static final String SOAPLAB_SERVICE = "Soaplab service";
	private static Logger logger = Logger.getLogger(SoaplabServiceProvider.class);

	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		List<ServiceDescription<SoaplabActivityConfigurationBean>> descriptions = new ArrayList<ServiceDescription<SoaplabActivityConfigurationBean>>();
		String soaplab = serviceProviderConfig.getUrl();
		callBack.status("Connecting to Soaplab:" + soaplab);
		try {
			List<SoaplabCategory> categories = SoaplabScavengerAgent.load(soaplab);
			for (SoaplabCategory cat : categories) {
				for (String service : cat.getServices()) {
					SoaplabServiceDescription item = new SoaplabServiceDescription();
					item.setCategory(cat.getCategory());
					item.setOperation(service);
					item.setUrl(soaplab);
					descriptions.add(item);

					Map info;
					try {
						info = (Map) Soap.callWebService(soaplab + "/" + service, "getAnalysisType");
						// Get the description element from the map
						String description = (String) info.get("description");
						if (description != null) {
							item.setTextualDescription(description);
						}
					} catch (ServiceException e) {
						callBack.warning(e.getMessage());
					} catch (RemoteException e) {
						callBack.warning(e.getMessage());
					}
				}
			}
			callBack.partialResults(descriptions);
			callBack.finished();
		} catch (MissingSoaplabException e) {
			String message = "There was an error with the soaplab: " + soaplab;
			callBack.fail(message, e);
		}
		
	}

	public Icon getIcon() {
		return new ImageIcon(SoaplabActivityItem.class
				.getResource("/soaplab.png"));
	}

	public String getName() {
		return SOAPLAB_SERVICE;
	}

	public List<SoaplabServiceProviderConfig> getDefaultConfigurations() {
		List<SoaplabServiceProviderConfig> defaults = new ArrayList<SoaplabServiceProviderConfig>();
		// TODO: Defaults should come from a config/resource file
		defaults.add(new SoaplabServiceProviderConfig(
				"http://www.ebi.ac.uk/soaplab/services/"));
		return defaults;
	}



}
