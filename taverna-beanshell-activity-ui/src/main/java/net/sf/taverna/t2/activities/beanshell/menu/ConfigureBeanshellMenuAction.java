package net.sf.taverna.t2.activities.beanshell.menu;

import java.net.URI;

import net.sf.taverna.t2.activities.beanshell.actions.BeanshellActivityConfigurationAction;
import net.sf.taverna.t2.activities.beanshell.servicedescriptions.BeanshellTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

import javax.swing.Action;

import uk.org.taverna.configuration.app.ApplicationConfiguration;
import uk.org.taverna.scufl2.api.activity.Activity;

public class ConfigureBeanshellMenuAction extends AbstractConfigureActivityMenuAction {

	public static final URI LOCALWORKER_ACTIVITY = URI
			.create("http://ns.taverna.org.uk/2010/activity/localworker");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ApplicationConfiguration applicationConfiguration;

	public ConfigureBeanshellMenuAction() {
		super(BeanshellTemplateService.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		Activity a = findActivity();
		Action result = null;
		if (!(a.getType().equals(LOCALWORKER_ACTIVITY))) {
			result = new BeanshellActivityConfigurationAction(findActivity(), getParentFrame(),
					editManager, fileManager, activityIconManager, serviceDescriptionRegistry,
					applicationConfiguration);
			result.putValue(Action.NAME, BeanshellActivityConfigurationAction.EDIT_BEANSHELL_SCRIPT);
			addMenuDots(result);
		}
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
