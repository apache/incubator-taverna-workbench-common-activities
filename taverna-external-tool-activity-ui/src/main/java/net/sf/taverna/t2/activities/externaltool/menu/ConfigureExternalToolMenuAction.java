/*******************************************************************************
 * Copyright (C) 2010 Hajo Nils Krabbenhoeft, spratpix GmbH & Co. KG
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

package net.sf.taverna.t2.activities.externaltool.menu;

import javax.swing.Action;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.actions.ExternalToolActivityConfigureAction;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;

/**
 * This class adds the plugin configuration action to the context menu of every use case activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class ConfigureExternalToolMenuAction extends
		AbstractConfigureActivityMenuAction<ExternalToolActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public ConfigureExternalToolMenuAction() {
		super(ExternalToolActivity.class);
	}

	@Override
	protected Action createAction() {
		ExternalToolActivityConfigureAction configAction = new ExternalToolActivityConfigureAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager);
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

}
