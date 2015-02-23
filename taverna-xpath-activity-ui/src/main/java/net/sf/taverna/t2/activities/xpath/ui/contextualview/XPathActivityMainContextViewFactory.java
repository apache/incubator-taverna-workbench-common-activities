package net.sf.taverna.t2.activities.xpath.ui.contextualview;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.activities.xpath.ui.servicedescription.XPathTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;
import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.scufl2.api.activity.Activity;

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
