/*******************************************************************************
 ******************************************************************************/

package org.apache.taverna.activities.externaltool.views;

import java.util.Arrays;
import java.util.List;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

/**
 * ExternalToolActivityViewFactory produces an ExternalToolActivityContextualView to show
 * information for a use case activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolActivityViewFactory implements ContextualViewFactory<ExternalToolActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ColourManager colourManager;

	public boolean canHandle(Object object) {
		if (object instanceof ExternalToolActivity) {
			return true;
		}
		return false;
	}

	public List<ContextualView> getViews(ExternalToolActivity selection) {
		return Arrays.asList(new ContextualView[] { new ExternalToolActivityContextualView(
				selection, editManager, fileManager, colourManager, activityIconManager) });
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
