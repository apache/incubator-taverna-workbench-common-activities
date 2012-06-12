package net.sf.taverna.t2.activities.soaplab.menu;

import javax.swing.Action;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.actions.SoaplabActivityConfigurationAction;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

public class ConfigureSoaplabActivityMenuAction extends
		AbstractConfigureActivityMenuAction<SoaplabActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public ConfigureSoaplabActivityMenuAction() {
		super(SoaplabActivity.class);
	}

	@Override
	protected Action createAction() {
		SoaplabActivityConfigurationAction configAction = new SoaplabActivityConfigurationAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager);
		configAction.putValue(Action.NAME, SoaplabActivityConfigurationAction.CONFIGURE_SOAPLAB_ACTIVITY);
		addMenuDots(configAction);
		return configAction;
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

}
