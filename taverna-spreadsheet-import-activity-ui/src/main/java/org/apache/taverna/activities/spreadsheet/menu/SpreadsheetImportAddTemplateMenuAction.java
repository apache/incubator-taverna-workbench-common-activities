/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.spreadsheet.menu;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.taverna.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import org.apache.taverna.activities.spreadsheet.servicedescriptions.SpreadsheetImportTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.ui.menu.AbstractMenuAction;
import org.apache.taverna.ui.menu.DesignOnlyAction;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.commons.services.ServiceRegistry;

/**
 * An action to add a spreadsheet import activity + a wrapping processor to the workflow.
 *
 * @author Alan R Williams
 */
@SuppressWarnings("serial")
public class SpreadsheetImportAddTemplateMenuAction extends AbstractMenuAction {

	private static final URI ADD_SPREADSHEET_IMPORT_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#graphMenuAddSpreadsheetImport");

	private static final URI INSERT = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#insert");

	private static String ADD_SPREADSHEET_IMPORT = SpreadsheetImportUIText
			.getString("SpreadsheetImportAddTemplateAction.addMenu");

	private EditManager editManager;
	private MenuManager menuManager;
	private SelectionManager selectionManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public SpreadsheetImportAddTemplateMenuAction() {
		super(INSERT, 700, ADD_SPREADSHEET_IMPORT_URI);
	}

	@Override
	protected Action createAction() {
		return new AddSpreadsheetImportMenuAction();
	}

	protected class AddSpreadsheetImportMenuAction extends AbstractAction implements
			DesignOnlyAction {
		AddSpreadsheetImportMenuAction() {
			super();
			putValue(SMALL_ICON,
					activityIconManager
							.iconForActivity(SpreadsheetImportTemplateService.ACTIVITY_TYPE));
			putValue(NAME, ADD_SPREADSHEET_IMPORT);
			putValue(SHORT_DESCRIPTION, ADD_SPREADSHEET_IMPORT);
			putValue(
					Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK
							| InputEvent.ALT_DOWN_MASK));

		}

		public void actionPerformed(ActionEvent e) {
			WorkflowView.importServiceDescription(serviceDescriptionRegistry
					.getServiceDescription(SpreadsheetImportTemplateService.ACTIVITY_TYPE), false,
					editManager, menuManager, selectionManager, serviceRegistry);
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

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
