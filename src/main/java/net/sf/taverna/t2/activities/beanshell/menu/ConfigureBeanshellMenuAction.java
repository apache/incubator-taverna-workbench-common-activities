package net.sf.taverna.t2.activities.beanshell.menu;

import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import javax.swing.Action;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.beanshell.actions.BeanshellActivityConfigurationAction;

public class ConfigureBeanshellMenuAction extends
		AbstractConfigureActivityMenuAction<BeanshellActivity> {

	private static final String EDIT_BEANSHELL_SCRIPT = "Edit beanshell script";

	public ConfigureBeanshellMenuAction() {
		super(BeanshellActivity.class);
	}
	
	@Override
	protected Action createAction() {
		Activity a = findActivity();
		Action result = null;
		if (! (a instanceof LocalworkerActivity)) {
		result = new BeanshellActivityConfigurationAction(
				findActivity(), getParentFrame());
		result.putValue(Action.NAME, EDIT_BEANSHELL_SCRIPT);
		addMenuDots(result);
		}
		return result;
	}


}
