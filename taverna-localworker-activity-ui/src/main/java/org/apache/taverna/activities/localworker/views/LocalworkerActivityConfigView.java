package org.apache.taverna.activities.localworker.views;

import org.apache.taverna.activities.beanshell.views.BeanshellConfigurationPanel;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
public class LocalworkerActivityConfigView extends BeanshellConfigurationPanel {

	public LocalworkerActivityConfigView(Activity activity, ApplicationConfiguration applicationConfiguration) {
		super(activity, applicationConfiguration);
	}

	public boolean isConfigurationChanged() {
		boolean configurationChanged = super.isConfigurationChanged();
		if (configurationChanged) {
			getJson().put("isAltered", true);
		}
		return configurationChanged;
	}

}
