/*******************************************************************************
 * Copyright (C) 2010 Hajo Nils Krabbenhoeft, INB, University of Luebeck
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/

package net.sf.taverna.t2.activities.externaltool.views;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

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
