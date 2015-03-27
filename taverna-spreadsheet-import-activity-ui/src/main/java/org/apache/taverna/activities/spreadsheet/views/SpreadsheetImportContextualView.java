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

package org.apache.taverna.activities.spreadsheet.views;

import java.awt.Frame;

import javax.swing.Action;

import org.apache.taverna.activities.spreadsheet.actions.SpreadsheetImportActivityConfigurationAction;
import org.apache.taverna.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import org.apache.taverna.activities.spreadsheet.servicedescriptions.SpreadsheetImportActivityIcon;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.commons.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

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
