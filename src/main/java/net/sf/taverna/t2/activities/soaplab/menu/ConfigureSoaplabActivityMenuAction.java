package net.sf.taverna.t2.activities.soaplab.menu;

import javax.swing.Action;

import net.sf.taverna.t2.activities.soaplab.actions.SoaplabActivityConfigurationAction;
import net.sf.taverna.t2.activities.soaplab.servicedescriptions.SoaplabServiceDescription;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.ui.menu.ContextualMenuComponent;
import net.sf.taverna.t2.ui.menu.MenuComponent;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

public class ConfigureSoaplabActivityMenuAction extends AbstractConfigureActivityMenuAction
		implements MenuComponent, ContextualMenuComponent {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;

	public ConfigureSoaplabActivityMenuAction() {
		super(SoaplabServiceDescription.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		SoaplabActivityConfigurationAction configAction = new SoaplabActivityConfigurationAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager,
				serviceDescriptionRegistry);
		configAction.putValue(Action.NAME,
				SoaplabActivityConfigurationAction.CONFIGURE_SOAPLAB_ACTIVITY);
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

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

}
