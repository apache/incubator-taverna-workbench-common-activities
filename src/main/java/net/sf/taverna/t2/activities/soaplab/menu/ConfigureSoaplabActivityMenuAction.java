package net.sf.taverna.t2.activities.soaplab.menu;

import javax.swing.Action;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.actions.SoaplabActivityConfigurationAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

public class ConfigureSoaplabActivityMenuAction extends
		AbstractConfigureActivityMenuAction<SoaplabActivity> {

	public ConfigureSoaplabActivityMenuAction() {
		super(SoaplabActivity.class);
	}
	
	@Override
	protected Action createAction() {
		SoaplabActivityConfigurationAction configAction = new SoaplabActivityConfigurationAction(
				findActivity(), getParentFrame());
		configAction.putValue(Action.NAME, SoaplabActivityConfigurationAction.CONFIGURE_SOAPLAB_ACTIVITY);
		addMenuDots(configAction);
		return configAction;
	}


}
