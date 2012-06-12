package net.sf.taverna.t2.activities.xpath.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.activities.xpath.XPathActivityConfigurationBean;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

@SuppressWarnings("serial")
/**
 * @author Sergejs Aleksejevs
 */
public class XPathActivityConfigureAction extends
		ActivityConfigurationAction<XPathActivity, XPathActivityConfigurationBean> {

	private final EditManager editManager;
	private final FileManager fileManager;

	public XPathActivityConfigureAction(XPathActivity activity, Frame owner,
			EditManager editManager, FileManager fileManager,
			ActivityIconManager activityIconManager) {
		super(activity, activityIconManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog<XPathActivity, XPathActivityConfigurationBean> currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());

		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}

		XPathActivityConfigurationPanelProvider panel = new XPathActivityConfigurationPanelProvider(
				getActivity());
		ActivityConfigurationDialog<XPathActivity, XPathActivityConfigurationBean> dialog = new ActivityConfigurationDialog<XPathActivity, XPathActivityConfigurationBean>(
				getActivity(), panel, editManager, fileManager);

		ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);
	}

}
