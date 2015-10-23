package org.apache.taverna.activities.xpath.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.ActivityConfigurationAction;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

/**
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
@SuppressWarnings("serial")
public class XPathActivityConfigureAction extends ActivityConfigurationAction {

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ServiceRegistry serviceRegistry;

	public XPathActivityConfigureAction(Activity activity, Frame owner,
			EditManager editManager, FileManager fileManager,
			ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry, ServiceRegistry serviceRegistry) {
		super(activity, activityIconManager, serviceDescriptionRegistry);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.serviceRegistry = serviceRegistry;
	}

	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog currentDialog = ActivityConfigurationAction.getDialog(getActivity());

		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}

		XPathActivityConfigurationPanelProvider panel = new XPathActivityConfigurationPanelProvider(
				getActivity(), serviceRegistry);
		ActivityConfigurationDialog dialog = new ActivityConfigurationDialog(
				getActivity(), panel, editManager);

		ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);
	}

}
