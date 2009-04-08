package net.sf.taverna.t2.activities.beanshell.servicedescriptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.beanshell.query.BeanshellActivityItem;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionProvider;

public class BeanshellServiceDescriptions implements ServiceDescriptionProvider {

	private BeanshellDescription beanshellConfiguration = new BeanshellDescription();

	public Collection<BeanshellDescription> getServiceDescriptions() {
		return Collections.singleton(beanshellConfiguration);
	}

	public class BeanshellDescription implements
			ServiceDescription<BeanshellActivityConfigurationBean> {

		public Icon getIcon() {
			return BeanshellActivityItem.getBeanshellIcon();
		}

		public String getName() {
			return "Beanshell";
		}

		public List<String> getPath() {
			return Arrays.asList(SERVICE_TEMPLATES);
		}

		public boolean isTemplateService() {
			return true;
		}

		public Class<BeanshellActivity> getActivityClass() {
			return BeanshellActivity.class;
		}

		public BeanshellActivityConfigurationBean getActivityConfiguration() {
			return new BeanshellActivityConfigurationBean();
		}
	}
}
