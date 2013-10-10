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

import net.sf.taverna.t2.activities.spreadsheet.views.SpreadsheetImportConfigView;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.scufl2.api.activity.Activity;

/**
 * The configuration action for a SpreadsheetImport activity.
 *
 * @author David Withers
 */
@SuppressWarnings("serial")
public class SpreadsheetImportActivityConfigurationAction extends ActivityConfigurationAction {

	private static final String CONFIGURE = "Configure";

	private final EditManager editManager;

	private final FileManager fileManager;

	private final ServiceRegistry serviceRegistry;

	public SpreadsheetImportActivityConfigurationAction(Activity activity,
			Frame owner, EditManager editManager, FileManager fileManager,
			ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry, ServiceRegistry serviceRegistry) {
		super(activity, activityIconManager, serviceDescriptionRegistry);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.serviceRegistry = serviceRegistry;
		putValue(NAME, CONFIGURE);
	}

	public void actionPerformed(ActionEvent e) {
		final SpreadsheetImportConfigView spreadsheetConfigView = new SpreadsheetImportConfigView(
				getActivity(), serviceRegistry);
		final ActivityConfigurationDialog dialog = new ActivityConfigurationDialog(getActivity(),
				spreadsheetConfigView, editManager);

		ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);

	}
}
