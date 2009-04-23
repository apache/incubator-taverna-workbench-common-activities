package net.sf.taverna.t2.activities.beanshell.menu;

import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

import javax.swing.Action;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.actions.BeanshellActivityConfigurationAction;

public class ConfigureBeanshellMenuAction extends
		AbstractConfigureActivityMenuAction<BeanshellActivity> {

	private static final String EDIT_BEANSHELL_SCRIPT = "Edit beanshell script";

	public ConfigureBeanshellMenuAction() {
		super(BeanshellActivity.class);
	}
	
	@Override
	protected Action createAction() {
		BeanshellActivityConfigurationAction configAction = new BeanshellActivityConfigurationAction(
				findActivity(), getParentFrame());
		configAction.putValue(Action.NAME, EDIT_BEANSHELL_SCRIPT);
		addMenuDots(configAction);
		return configAction;
	}


}
