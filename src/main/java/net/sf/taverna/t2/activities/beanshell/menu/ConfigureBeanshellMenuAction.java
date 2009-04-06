package net.sf.taverna.t2.activities.beanshell.menu;

import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

import javax.swing.Action;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.actions.BeanshellActivityConfigurationAction;

public class ConfigureBeanshellMenuAction extends
		AbstractConfigureActivityMenuAction<BeanshellActivity> {

	public ConfigureBeanshellMenuAction() {
		super(BeanshellActivity.class);
	}

	@Override
	protected Action createAction() {
		BeanshellActivityConfigurationAction configAction = new BeanshellActivityConfigurationAction(
				findActivity(), getParentFrame());
		addMenuDots(configAction);
		return configAction;
	}


}
