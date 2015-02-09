package net.sf.taverna.t2.activities.rest.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
/**
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class RESTActivityConfigureAction extends ActivityConfigurationAction {

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ServiceRegistry serviceRegistry;

	public RESTActivityConfigureAction(Activity activity, Frame owner, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry, ServiceRegistry serviceRegistry) {
		super(activity, activityIconManager, serviceDescriptionRegistry);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.serviceRegistry = serviceRegistry;
	}

	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog currentDialog = ActivityConfigurationAction
				.getDialog(getActivity());

		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}

		RESTActivityConfigurationPanel panel = new RESTActivityConfigurationPanel(getActivity(), serviceRegistry);
		ActivityConfigurationDialog dialog = new ActivityConfigurationDialog(getActivity(), panel,
				editManager);

		ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);
	}

}
