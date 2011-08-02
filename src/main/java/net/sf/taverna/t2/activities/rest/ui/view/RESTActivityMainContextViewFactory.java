package net.sf.taverna.t2.activities.rest.ui.view;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class RESTActivityMainContextViewFactory implements
		ContextualViewFactory<RESTActivity> {

	private EditManager editManager;
	private FileManager fileManager;

	public boolean canHandle(Object selection) {
		return selection instanceof RESTActivity;
	}

	public List<ContextualView> getViews(RESTActivity selection) {
		return Arrays.<ContextualView>asList(new RESTActivityMainContextualView(selection, editManager, fileManager));
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

}
