package net.sf.taverna.t2.activities.beanshell.servicedescriptions;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

public class BeanshellTemplateService extends
		AbstractTemplateService<BeanshellActivityConfigurationBean> {

	private static final String BEANSHELL = "Beanshell";

	public String getName() {
		return BEANSHELL;
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
	
	public static ServiceDescription getServiceDescription() {
		BeanshellTemplateService bts = new BeanshellTemplateService();
		return bts.templateService;
	}
}
