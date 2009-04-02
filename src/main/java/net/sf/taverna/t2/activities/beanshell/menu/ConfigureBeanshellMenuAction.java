package net.sf.taverna.t2.activities.beanshell.menu;

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
		BeanshellActivityConfigurationAction configAction = new BeanshellActivityConfigurationAction(findActivity(),
				getParentFrame());
		configAction.putValue(Action.NAME, configAction.getValue(Action.NAME) + "...");
		return configAction;
	}

}
