package net.sf.taverna.t2.activities.xpath.ui.contextualview;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

/**
 *
 * @author Sergejs Aleksejevs
 */
public class XPathActivityMainContextViewFactory implements
		ContextualViewFactory<XPathActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public boolean canHandle(Object selection) {
		return selection instanceof XPathActivity;
	}

	public List<ContextualView> getViews(XPathActivity selection) {
		return Arrays
				.<ContextualView> asList(new XPathActivityMainContextualView(
						selection, editManager, fileManager, activityIconManager));
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

}
