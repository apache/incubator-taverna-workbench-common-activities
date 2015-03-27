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

package org.apache.taverna.activities.beanshell.views;

import java.awt.Frame;

import javax.swing.Action;

import org.apache.taverna.activities.beanshell.actions.BeanshellActivityConfigurationAction;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import uk.org.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

/**
 * A simple non editable HTML table view over a {@link BeanshellActivity}.
 * Clicking on the configure button shows the editable {@link BeanshellConfigView}
 *
 * @author Ian Dunlop
 * @author Stuart Owen
 * @author David Withers
 */
@SuppressWarnings("serial")
public class BeanshellContextualView extends HTMLBasedActivityContextualView {

	private EditManager editManager;
	private FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final ApplicationConfiguration applicationConfiguration;

	public BeanshellContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ColourManager colourManager, ServiceDescriptionRegistry serviceDescriptionRegistry,
			ApplicationConfiguration applicationConfiguration) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.applicationConfiguration = applicationConfiguration;
		init();
	}

	private void init() {
	}

	@Override
	protected String getRawTableRowsHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<tr><th>Input Port Name</th><th>Depth</th></tr>");
		for (InputActivityPort inputActivityPort : getActivity().getInputPorts()) {
			html.append("<tr><td>" + inputActivityPort.getName() + "</td><td>");
			html.append(inputActivityPort.getDepth() + "</td></tr>");
		}
		html.append("<tr><th>Output Port Name</th><th>Depth</th></tr>");
		for (OutputActivityPort outputActivityPort : getActivity().getOutputPorts()) {
			html.append("<tr><td>" + outputActivityPort.getName() + "</td><td>");
			html.append(outputActivityPort.getDepth() + "</td></tr>");
		}
		return html.toString();
	}

	@Override
	public String getViewTitle() {
		return "Beanshell service";
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new BeanshellActivityConfigurationAction(getActivity(), owner, editManager,
				fileManager, activityIconManager, serviceDescriptionRegistry, applicationConfiguration);
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
