package org.apache.taverna.activities.localworker.menu;

import javax.swing.Action;

import org.apache.taverna.configuration.app.ApplicationConfiguration;

import org.apache.taverna.activities.beanshell.actions.BeanshellActivityConfigurationAction;
import org.apache.taverna.activities.localworker.actions.LocalworkerActivityConfigurationAction;
import org.apache.taverna.activities.localworker.servicedescriptions.LocalworkerServiceDescription;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.ui.menu.ContextualMenuComponent;
import org.apache.taverna.ui.menu.MenuComponent;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

public class ConfigureLocalworkerMenuAction extends AbstractConfigureActivityMenuAction implements
		MenuComponent, ContextualMenuComponent {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ApplicationConfiguration applicationConfiguration;

	public ConfigureLocalworkerMenuAction() {
		super(LocalworkerServiceDescription.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		Action result = null;
		result = new LocalworkerActivityConfigurationAction(findActivity(), getParentFrame(),
				editManager, fileManager, activityIconManager, serviceDescriptionRegistry,
				applicationConfiguration);
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

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

}
