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
package net.sf.taverna.t2.activities.spreadsheet.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportConfiguration;
import net.sf.taverna.t2.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import net.sf.taverna.t2.activities.spreadsheet.views.SpreadsheetImportConfigView;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import net.sf.taverna.t2.workflowmodel.Dataflow;

/**
 * The configuration action for a SpreadsheetImport activity.
 * 
 * @author David Withers
 */
@SuppressWarnings("serial")
public class SpreadsheetImportActivityConfigurationAction extends
		ActivityConfigurationAction<SpreadsheetImportActivity, SpreadsheetImportConfiguration> {

	private static final String CONFIGURE = "Configure";

	private final Frame owner;

	public SpreadsheetImportActivityConfigurationAction(SpreadsheetImportActivity activity,
			Frame owner) {
		super(activity);
		putValue(NAME, CONFIGURE);
		this.owner = owner;
	}

	public void actionPerformed(ActionEvent e) {
		final SpreadsheetImportConfigView spreadsheetConfigView = new SpreadsheetImportConfigView(
				(SpreadsheetImportActivity) getActivity());
		final HelpEnabledDialog dialog = new HelpEnabledDialog(owner, SpreadsheetImportUIText
				.getString("SpreadsheetImportActivityConfigurationAction.dialogTitle"), true, null);
		final Dataflow owningDataflow = FileManager.getInstance()
		.getCurrentDataflow();
		dialog.add(spreadsheetConfigView);
		// dialog.setSize(500, 600);
		dialog.pack();

		spreadsheetConfigView.setOkAction(new AbstractAction(SpreadsheetImportUIText
				.getString("SpreadsheetImportActivityConfigurationAction.okButton")) {
			public void actionPerformed(ActionEvent arg0) {
				if (spreadsheetConfigView.isConfigurationChanged()) {
					ActivityConfigurationDialog.configureActivityStatic(owningDataflow, activity, spreadsheetConfigView.getConfiguration());
				}
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		spreadsheetConfigView.setCancelAction(new AbstractAction(SpreadsheetImportUIText
				.getString("SpreadsheetImportActivityConfigurationAction.canceButton")) {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}

		});
		dialog.setVisible(true);

	}
}
