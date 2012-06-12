package net.sf.taverna.t2.activities.rest.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

@SuppressWarnings("serial")
/**
 * @author Sergejs Aleksejevs
 */
public class RESTActivityConfigureAction extends
		ActivityConfigurationAction<RESTActivity, RESTActivityConfigurationBean> {

	private final EditManager editManager;
	private final FileManager fileManager;

	public RESTActivityConfigureAction(RESTActivity activity, Frame owner, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager) {
		super(activity, activityIconManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog<RESTActivity, RESTActivityConfigurationBean> currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());

		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}

		RESTActivityConfigurationPanel panel = new RESTActivityConfigurationPanel(getActivity());
		ActivityConfigurationDialog<RESTActivity, RESTActivityConfigurationBean> dialog = new ActivityConfigurationDialog<RESTActivity, RESTActivityConfigurationBean>(
				getActivity(), panel, editManager, fileManager);

		ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);
	}

}
