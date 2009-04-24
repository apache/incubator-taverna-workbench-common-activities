package net.sf.taverna.t2.activities.soaplab.menu;

import javax.swing.Action;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.actions.SoaplabActivityConfigurationAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

public class ConfigureSoaplabActivityMenuAction extends
		AbstractConfigureActivityMenuAction<SoaplabActivity> {

	private static final String CONFIGURE_SOAPLAB_ACTIVITY = "Configure Soaplab";

	public ConfigureSoaplabActivityMenuAction() {
		super(SoaplabActivity.class);
	}
	
	@Override
	protected Action createAction() {
		SoaplabActivityConfigurationAction configAction = new SoaplabActivityConfigurationAction(
				findActivity(), getParentFrame());
		configAction.putValue(Action.NAME, CONFIGURE_SOAPLAB_ACTIVITY);
		addMenuDots(configAction);
		return configAction;
	}


}
