package net.sf.taverna.t2.activities.localworker.menu;

import javax.swing.Action;

import net.sf.taverna.t2.activities.beanshell.actions.BeanshellActivityConfigurationAction;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.actions.LocalworkerActivityConfigurationAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

public class ConfigureLocalworkerMenuAction extends
		AbstractConfigureActivityMenuAction<LocalworkerActivity> {

	private EditManager editManager;
	private FileManager fileManager;

	public ConfigureLocalworkerMenuAction() {
		super(LocalworkerActivity.class);
	}

	@Override
	protected Action createAction() {
		LocalworkerActivity a = findActivity();
		Action result = null;
			result = new LocalworkerActivityConfigurationAction(
					findActivity(), getParentFrame(), editManager, fileManager);
			result.putValue(Action.NAME, BeanshellActivityConfigurationAction.EDIT_BEANSHELL_SCRIPT);
			addMenuDots(result);
		return result;
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

}
