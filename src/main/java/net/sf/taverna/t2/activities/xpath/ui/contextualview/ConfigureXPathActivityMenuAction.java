package net.sf.taverna.t2.activities.xpath.ui.contextualview;

import javax.swing.Action;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.activities.xpath.ui.config.XPathActivityConfigureAction;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * This action is responsible for enabling the contextual menu entry on processors that perform
 * XPathActivity'ies.
 *
 * NB! As a side-effect this also enables the pop-up with for configuration of the processor when it
 * is added to the workflow from the Service Panel.
 *
 * @author Sergejs Aleksejevs
 */
public class ConfigureXPathActivityMenuAction extends
		AbstractConfigureActivityMenuAction<XPathActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public ConfigureXPathActivityMenuAction() {
		super(XPathActivity.class);
	}

	@Override
	protected Action createAction() {
		XPathActivityConfigureAction configAction = new XPathActivityConfigureAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager);
		configAction.putValue(Action.NAME, "Configure XPath service");
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
