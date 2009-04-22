package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;
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
		callBack.status("About to parse soaplab:" + soaplab);
		try {
			List<SoaplabCategory> categories = SoaplabScavengerAgent.load(soaplab);
			for (SoaplabCategory cat : categories) {
				for (String service : cat.getServices()) {
					SoaplabServiceDescription item = new SoaplabServiceDescription();
					item.setCategory(cat.getCategory());
					item.setOperation(service);
					item.setUrl(soaplab);
					descriptions.add(item);
				}
			}
			callBack.partialResults(descriptions);
			callBack.finished();
		} catch (Exception e) { // anything else we did not expect
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
