package net.sf.taverna.t2.activities.beanshell.menu;

import java.net.URI;

import net.sf.taverna.t2.activities.beanshell.actions.BeanshellActivityConfigurationAction;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

import javax.swing.Action;

import uk.org.taverna.scufl2.api.activity.Activity;

public class ConfigureBeanshellMenuAction extends AbstractConfigureActivityMenuAction {

	private static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/beanshell");
	public static final URI LOCALWORKER_ACTIVITY = URI.create("http://ns.taverna.org.uk/2010/activity/localworker");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;

	public ConfigureBeanshellMenuAction() {
		super(ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		Activity a = findActivity();
		Action result = null;
		if (!(a.getConfigurableType().equals(LOCALWORKER_ACTIVITY))) {
			result = new BeanshellActivityConfigurationAction(
					findActivity(), getParentFrame(), editManager, fileManager, activityIconManager, serviceDescriptionRegistry);
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

}
