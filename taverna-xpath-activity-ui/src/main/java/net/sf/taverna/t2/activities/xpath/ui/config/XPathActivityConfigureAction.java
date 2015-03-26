package net.sf.taverna.t2.activities.xpath.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import org.apache.taverna.commons.services.ServiceRegistry;
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
