/*******************************************************************************
 * Copyright (C) 2009 The University of Manchester
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
package net.sf.taverna.t2.activities.spreadsheet.menu;

import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;

import net.sf.taverna.t2.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import net.sf.taverna.t2.activities.spreadsheet.servicedescriptions.SpreadsheetImportTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.ui.menu.AbstractContextualMenuAction;
import net.sf.taverna.t2.ui.menu.MenuManager;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.t2.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.commons.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.core.Workflow;

/**
 * An action to add a spreadsheet import activity + a wrapping processor to the workflow.
 *
 * @author David Withers
 */
@SuppressWarnings("serial")
public class SpreadsheetImportAddTemplateAction extends AbstractContextualMenuAction {

	private static final URI insertSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/insert");

	private EditManager editManager;
	private MenuManager menuManager;
	private SelectionManager selectionManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public SpreadsheetImportAddTemplateAction() {
		super(insertSection, 700);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && getContextualSelection().getSelection() instanceof Workflow;
	}

	@Override
	protected Action createAction() {

		AbstractAction action = new AbstractAction(
				SpreadsheetImportUIText.getString("SpreadsheetImportAddTemplateAction.addMenu"),
				activityIconManager.iconForActivity(SpreadsheetImportTemplateService.ACTIVITY_TYPE)) {

			public void actionPerformed(ActionEvent e) {
				WorkflowView.importServiceDescription(serviceDescriptionRegistry
						.getServiceDescription(SpreadsheetImportTemplateService.ACTIVITY_TYPE), false,
						editManager, menuManager, selectionManager, serviceRegistry);
			}

		};

		return action;
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

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
