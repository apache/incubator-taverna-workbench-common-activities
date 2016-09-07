/*******************************************************************************
 ******************************************************************************/

package org.apache.taverna.activities.externaltool.menu;

import javax.swing.Action;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.actions.ExternalToolActivityConfigureAction;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

/**
 * This class adds the plugin configuration action to the context menu of every use case activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class ConfigureExternalToolMenuAction extends
		AbstractConfigureActivityMenuAction<ExternalToolActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public ConfigureExternalToolMenuAction() {
		super(ExternalToolActivity.class);
	}

	@Override
	protected Action createAction() {
		ExternalToolActivityConfigureAction configAction = new ExternalToolActivityConfigureAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager);
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
