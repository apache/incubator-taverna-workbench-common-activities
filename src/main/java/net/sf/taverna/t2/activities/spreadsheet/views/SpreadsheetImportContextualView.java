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
package net.sf.taverna.t2.activities.spreadsheet.views;

import java.awt.Frame;

import javax.swing.Action;

import net.sf.taverna.t2.activities.spreadsheet.actions.SpreadsheetImportActivityConfigurationAction;
import net.sf.taverna.t2.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import net.sf.taverna.t2.activities.spreadsheet.servicedescriptions.SpreadsheetImportActivityIcon;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import uk.org.taverna.commons.services.ServiceRegistry;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;

/**
 * A simple non editable HTML table view over a {@link SpreadsheetImportActivity}. Clicking on the
 * configure button shows the editable {@link SpreadsheetImportConfigView}
 *
 * @author David Withers
 */
public class SpreadsheetImportContextualView extends HTMLBasedActivityContextualView {

	private static final long serialVersionUID = 1L;
	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final ServiceRegistry serviceRegistry;

	public SpreadsheetImportContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ColourManager colourManager, ServiceDescriptionRegistry serviceDescriptionRegistry, ServiceRegistry serviceRegistry) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.serviceRegistry = serviceRegistry;
	}

	@Override
	protected String getRawTableRowsHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<tr><th>");
		html.append(SpreadsheetImportUIText
				.getString("SpreadsheetImportContextualView.inputPortName"));
		html.append("</th><th>");
		html.append(SpreadsheetImportUIText.getString("SpreadsheetImportContextualView.depth"));
		html.append("</th></tr>");
		for (InputActivityPort port : getActivity().getInputPorts()) {
			html.append("<tr><td>");
			html.append(port.getName());
			html.append("</td><td>");
			html.append(port.getDepth());
			html.append("</td></tr>");
		}
		html.append("<tr><th>");
		html.append(SpreadsheetImportUIText
				.getString("SpreadsheetImportContextualView.outputPortName"));
		html.append("</th><th>");
		html.append(SpreadsheetImportUIText.getString("SpreadsheetImportContextualView.depth"));
		html.append("</th></tr>");
		for (OutputActivityPort port : getActivity().getOutputPorts()) {
			html.append("<tr><td>");
			html.append(port.getName());
			html.append("</td><td>");
			html.append(port.getDepth());
			html.append("</td></tr>");
		}
		return html.toString();
	}

	@Override
	public String getViewTitle() {
		return SpreadsheetImportUIText.getString("SpreadsheetImportContextualView.activityName");
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new SpreadsheetImportActivityConfigurationAction(
				getActivity(), owner, editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, serviceRegistry);
	}

	@Override
	public String getBackgroundColour() {
		return SpreadsheetImportActivityIcon.SPREADSHEET_COLOUR_HTML;
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
