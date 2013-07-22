package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.xml.rpc.ServiceException;

import net.sf.taverna.t2.activities.soaplab.Soap;
import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;

import org.apache.log4j.Logger;

public class SoaplabServiceProvider extends
		AbstractConfigurableServiceProvider<SoaplabServiceProviderConfig> {

	// To avoid hammering the soaplab service
	private static final int DELAY_MS = 100;
	private static final int DESCRIPTION_UPDATE_INTERVAL_MS = 2000;

	private static Logger logger = Logger
			.getLogger(SoaplabServiceProvider.class);

	private static final String SOAPLAB_SERVICE = "Soaplab service";
	private static final boolean FIND_DETAILS = false;

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/soaplab");

	private ServiceDescriptionRegistry serviceDescriptionRegistry;

	public SoaplabServiceProvider() {
		super(new SoaplabServiceProviderConfig(
				"http://somehost/soaplab/services/"));
	}

	public void findServiceDescriptionsAsync(
			FindServiceDescriptionsCallBack callBack) {
		List<SoaplabServiceDescription> descriptions = findSoaplabServices(callBack);
		if (descriptions == null) {
			return;
		}
		callBack.partialResults(descriptions);

		if (FIND_DETAILS) {
			if (findSoaplabDetails(descriptions, callBack)) {
				callBack.finished();
			}
		} else {
			callBack.finished();
		}
	}

	public List<SoaplabServiceProviderConfig> getDefaultConfigurations() {

		List<SoaplabServiceProviderConfig> defaults = new ArrayList<SoaplabServiceProviderConfig>();

		// If defaults have failed to load from a configuration file then load them here.
		if (!serviceDescriptionRegistry.isDefaultSystemConfigurableProvidersLoaded()){
			defaults.add(new SoaplabServiceProviderConfig(
			"http://wsembnet.vital-it.ch/soaplab2-axis/services/"));
		} // else return an empty list

		return defaults;
	}

	public Icon getIcon() {
		return SoaplabActivityIcon.getSoaplabIcon();
	}

	public String getName() {
		return SOAPLAB_SERVICE;
	}

	@SuppressWarnings("unchecked")
	protected boolean findSoaplabDetails(
			List<SoaplabServiceDescription> descriptions,
			FindServiceDescriptionsCallBack callBack) {
		Date lastUpdate = new Date();
		// We'll fetch more details and update the descriptions in the
		// background
		List<SoaplabServiceDescription> updatedDescriptions = new ArrayList<SoaplabServiceDescription>();
		for (SoaplabServiceDescription serviceDescription : descriptions) {
			try {
				Date now = new Date();
				if (now.getTime() - lastUpdate.getTime() > DESCRIPTION_UPDATE_INTERVAL_MS) {
					if (!updatedDescriptions.isEmpty()) {
						callBack.partialResults(updatedDescriptions);
						updatedDescriptions = new ArrayList<SoaplabServiceDescription>();
					}
					lastUpdate = now;
				}
				Thread.sleep(DELAY_MS);
				URI soaplabEndpoint = serviceProviderConfig.getEndpoint();
				Map info = (Map) Soap.callWebService(soaplabEndpoint
						.toASCIIString()
						+ "/" + serviceDescription.getOperation(),
						"getAnalysisType"); // Get the description element from
				// the map
				String description = (String) info.get("description");
				if (description != null) {
					serviceDescription.setDescription(description);
				}
				updatedDescriptions.add(serviceDescription);
				String type = (String) info.get("type");
				if (type != null) {
					serviceDescription.setTypes(Arrays.asList(type.split(",")));
				}
			} catch (ClassCastException e) {
				logger.warn("Can't read descriptions for soaplab service "
						+ serviceDescription, e);
				callBack.warning("Can't read descriptions for soaplab service "
						+ serviceDescription.getOperation());
			} catch (ServiceException e) {
				logger.warn("Can't read descriptions for soaplab service "
						+ serviceDescription, e);
				callBack.warning("Can't read descriptions for soaplab service "
						+ serviceDescription.getOperation());
			} catch (RemoteException e) {
				logger.warn("Can't read descriptions for soaplab service "
						+ serviceDescription, e);
				callBack.warning("Can't read descriptions for soaplab service "
						+ serviceDescription.getOperation());
			} catch (InterruptedException ex) {
				callBack.fail("Thread was interrupted", ex);
				return false;
			}
		}
		if (!updatedDescriptions.isEmpty()) {
			callBack.partialResults(updatedDescriptions);
		}
		return true;
	}

	protected List<SoaplabServiceDescription> findSoaplabServices(
			FindServiceDescriptionsCallBack callBack) {
		List<SoaplabServiceDescription> descriptions = new ArrayList<SoaplabServiceDescription>();
		URI soaplabEndpoint = serviceProviderConfig.getEndpoint();
		callBack.status("Connecting to Soaplab:" + soaplabEndpoint);
		List<SoaplabCategory> categories;
		try {
			categories = SoaplabScavengerAgent.load(soaplabEndpoint
					.toASCIIString());
		} catch (MissingSoaplabException ex) {
			String message = "There was an error with the soaplab: "
					+ soaplabEndpoint;
			callBack.fail(message, ex);
			return null;
		}
		for (SoaplabCategory cat : categories) {
			for (String service : cat.getServices()) {
				SoaplabServiceDescription item = new SoaplabServiceDescription();
				item.setCategory(cat.getCategory());
				item.setOperation(service);
				item.setEndpoint(soaplabEndpoint);
				descriptions.add(item);
			}
		}
		return descriptions;
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		List<String> result;
		result = Arrays.asList(getConfiguration().getEndpoint().toString());
		return result;
	}

	public String getId() {
		return providerId.toString();
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

}
