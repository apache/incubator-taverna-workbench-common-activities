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
package net.sf.taverna.t2.activities.spreadsheet.menu;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.Action;
import javax.swing.KeyStroke;

import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity;
import net.sf.taverna.t2.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import net.sf.taverna.t2.activities.spreadsheet.servicedescriptions.SpreadsheetImportTemplateService;
import net.sf.taverna.t2.ui.menu.AbstractMenuAction;
import net.sf.taverna.t2.ui.menu.MenuManager;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.ui.DataflowSelectionManager;
import net.sf.taverna.t2.workbench.ui.workflowview.WorkflowView;
import net.sf.taverna.t2.workbench.views.graph.actions.DesignOnlyAction;
import net.sf.taverna.t2.workbench.views.graph.menu.InsertMenu;

import org.apache.log4j.Logger;

/**
 * An action to add a spreadsheet import activity + a wrapping processor to the workflow.
 *
 * @author Alan R Williams
 *
 */
@SuppressWarnings("serial")
public class SpreadsheetImportAddTemplateMenuAction extends AbstractMenuAction {

	private static final URI ADD_SPREADSHEET_IMPORT_URI = URI
	.create("http://taverna.sf.net/2008/t2workbench/menu#graphMenuAddSpreadsheetImport");

	private static Logger logger = Logger.getLogger(SpreadsheetImportAddTemplateMenuAction.class);

	private static String ADD_SPREADSHEET_IMPORT = SpreadsheetImportUIText
	.getString("SpreadsheetImportAddTemplateAction.addMenu");

	private EditManager editManager;

	private MenuManager menuManager;

	private DataflowSelectionManager dataflowSelectionManager;

	public SpreadsheetImportAddTemplateMenuAction(){
		super(InsertMenu.INSERT, 700, ADD_SPREADSHEET_IMPORT_URI);
	}

	@Override
	protected Action createAction() {
		return new AddSpreadsheetImportMenuAction();
	}

	protected class AddSpreadsheetImportMenuAction extends DesignOnlyAction {
		AddSpreadsheetImportMenuAction() {
			super();
			putValue(SMALL_ICON, ActivityIconManager.getInstance()
					.iconForActivity(new SpreadsheetImportActivity()));
			putValue(NAME, ADD_SPREADSHEET_IMPORT);
			putValue(SHORT_DESCRIPTION, ADD_SPREADSHEET_IMPORT);
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));

		}

		public void actionPerformed(ActionEvent e) {

			WorkflowView.importServiceDescription(SpreadsheetImportTemplateService.getServiceDescription(),
					false, editManager, menuManager, dataflowSelectionManager);

		}
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public void setDataflowSelectionManager(DataflowSelectionManager dataflowSelectionManager) {
		this.dataflowSelectionManager = dataflowSelectionManager;
	}

}

