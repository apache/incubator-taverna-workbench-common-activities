/*******************************************************************************
 * Copyright (C) 2007-2009 The University of Manchester
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
package org.apache.taverna.activities.externaltool.menu;

import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.servicedescriptions.ExternalToolTemplateServiceDescription;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * An action to add an external tool + a wrapping processor to the workflow.
 *
 * @author Alex Nenadic
 * @author Alan Williamns
 *
 */
@SuppressWarnings("serial")
public class AddExternalToolContextualMenuAction extends AbstractContextualMenuAction {

	private static final String ADD_EXTERNAL_TOOL = "Tool";

	private static final URI insertSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/insert");

	private static Logger logger = Logger.getLogger(AddExternalToolMenuAction.class);

	private EditManager editManager;

	private MenuManager menuManager;

	private SelectionManager selectionManager;

	private ActivityIconManager activityIconManager;

	public AddExternalToolContextualMenuAction() {
		super(insertSection, 900);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && getContextualSelection().getSelection() instanceof Dataflow;
	}

	@Override
	protected Action createAction() {

		return new AddExternalToolAction();
	}

	protected class AddExternalToolAction extends AbstractAction {
		AddExternalToolAction() {
			super(ADD_EXTERNAL_TOOL, activityIconManager.iconForActivity(
					new ExternalToolActivity()));
		}

		public void actionPerformed(ActionEvent e) {
			WorkflowView.importServiceDescription(
					ExternalToolTemplateServiceDescription.getServiceDescription(), false,
					editManager, menuManager, selectionManager);
		}
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

}
