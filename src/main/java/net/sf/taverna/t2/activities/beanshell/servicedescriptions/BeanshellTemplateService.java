package net.sf.taverna.t2.activities.beanshell.servicedescriptions;

import java.net.URI;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

public class BeanshellTemplateService extends
		AbstractTemplateService<BeanshellActivityConfigurationBean> {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	private static final String BEANSHELL = "Beanshell";

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/beanshell");

	public String getName() {
		return BEANSHELL;
	}

	@Override
	public URI getActivityURI() {
		return URI.create(BeanshellActivity.URI);
	}

	@Override
	public Class<BeanshellActivity> getActivityClass() {
		return BeanshellActivity.class;
	}

	@Override
	public BeanshellActivityConfigurationBean getActivityConfiguration() {
		return new BeanshellActivityConfigurationBean();
	}

	@Override
	public Icon getIcon() {
		return BeanshellActivityIcon.getBeanshellIcon();
	}

	@Override
	public String getDescription() {
		return "A service that allows Beanshell scripts, with dependencies on libraries";
	}

	@SuppressWarnings("unchecked")
	public static ServiceDescription getServiceDescription() {
		BeanshellTemplateService bts = new BeanshellTemplateService();
		return bts.templateService;
	}

	public String getId() {
		return providerId.toString();
	}

}
