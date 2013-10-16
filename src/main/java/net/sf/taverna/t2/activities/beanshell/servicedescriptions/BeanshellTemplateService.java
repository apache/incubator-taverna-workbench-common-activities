package net.sf.taverna.t2.activities.beanshell.servicedescriptions;

import java.net.URI;

import javax.swing.Icon;

import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import uk.org.taverna.scufl2.api.configurations.Configuration;

public class BeanshellTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	private static final String BEANSHELL = "Beanshell";

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/beanshell");

	public String getName() {
		return BEANSHELL;
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		configuration.getJsonAsObjectNode().put("script", "");
		configuration.getJsonAsObjectNode().put("classLoaderSharing", "workflow");
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return BeanshellActivityIcon.getBeanshellIcon();
	}

	@Override
	public String getDescription() {
		return "A service that allows Beanshell scripts, with dependencies on libraries";
	}

	public static ServiceDescription getServiceDescription() {
		BeanshellTemplateService bts = new BeanshellTemplateService();
		return bts.templateService;
	}

	public String getId() {
		return providerId.toString();
	}

}
