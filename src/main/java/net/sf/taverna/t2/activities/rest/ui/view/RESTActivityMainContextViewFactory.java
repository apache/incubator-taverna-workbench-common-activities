package net.sf.taverna.t2.activities.rest.ui.view;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

public class RESTActivityMainContextViewFactory implements ContextualViewFactory<RESTActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ColourManager colourManager;

	public boolean canHandle(Object selection) {
		return selection instanceof RESTActivity;
	}

	public List<ContextualView> getViews(RESTActivity selection) {
		return Arrays.<ContextualView> asList(new RESTActivityMainContextualView(selection,
				editManager, fileManager, activityIconManager, colourManager));
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

	public void setColourManager(ColourManager colourManager) {
		this.colourManager = colourManager;
	}

}
