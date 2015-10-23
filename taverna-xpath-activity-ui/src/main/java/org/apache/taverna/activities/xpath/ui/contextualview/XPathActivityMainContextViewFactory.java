package org.apache.taverna.activities.xpath.ui.contextualview;

import java.util.Arrays;
import java.util.List;

import org.apache.taverna.activities.xpath.ui.servicedescription.XPathTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

/**
 * @author Sergejs Aleksejevs
 */
public class XPathActivityMainContextViewFactory implements ContextualViewFactory<Activity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public boolean canHandle(Object selection) {
		return selection instanceof Activity
				&& ((Activity) selection).getType()
						.equals(XPathTemplateService.ACTIVITY_TYPE);
	}

	public List<ContextualView> getViews(Activity selection) {
		return Arrays.<ContextualView> asList(new XPathActivityMainContextualView(selection,
				editManager, fileManager, activityIconManager, serviceDescriptionRegistry,
				serviceRegistry));
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
