package net.sf.taverna.t2.activities.rest.ui.view;

import javax.swing.Action;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.ui.config.RESTActivityConfigureAction;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * This action is responsible for enabling the contextual menu entry on processors that perform
 * RESTActivity'ies.
 *
 * NB! As a side-effect this also enables the pop-up with for configuration of the processor when it
 * is added to the workflow from the Service Panel.
 *
 * @author Sergejs Aleksejevs
 */
public class ConfigureRESTActivityMenuAction extends
		AbstractConfigureActivityMenuAction<RESTActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public ConfigureRESTActivityMenuAction() {
		super(RESTActivity.class);
	}

	@Override
	protected Action createAction() {
		RESTActivityConfigureAction configAction = new RESTActivityConfigureAction(findActivity(),
				getParentFrame(), editManager, fileManager, activityIconManager);
		configAction.putValue(Action.NAME, "Configure REST service");
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
