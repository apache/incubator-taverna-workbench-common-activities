package org.apache.taverna.activities.xpath.ui.contextualview;

import javax.swing.Action;

import org.apache.taverna.activities.xpath.ui.config.XPathActivityConfigureAction;
import org.apache.taverna.activities.xpath.ui.servicedescription.XPathTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.commons.services.ServiceRegistry;

/**
 * This action is responsible for enabling the contextual menu entry on processors that perform
 * XPathActivity'ies.
 * NB! As a side-effect this also enables the pop-up with for configuration of the processor when it
 * is added to the workflow from the Service Panel.
 *
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class ConfigureXPathActivityMenuAction extends AbstractConfigureActivityMenuAction {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public ConfigureXPathActivityMenuAction() {
		super(XPathTemplateService.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		XPathActivityConfigureAction configAction = new XPathActivityConfigureAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager,
				serviceDescriptionRegistry, serviceRegistry);
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

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
