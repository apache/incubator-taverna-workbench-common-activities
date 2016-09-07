package org.apache.taverna.activities.localworker.views;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Frame;

import javax.swing.Action;

import org.apache.taverna.activities.localworker.actions.LocalworkerActivityConfigurationAction;
import org.apache.taverna.activities.localworker.servicedescriptions.LocalworkerServiceProvider;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("serial")
public class LocalworkerActivityContextualView extends HTMLBasedActivityContextualView {

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;

	private final ApplicationConfiguration applicationConfiguration;

	public LocalworkerActivityContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ColourManager colourManager,
			ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry,
			ApplicationConfiguration applicationConfiguration) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.applicationConfiguration = applicationConfiguration;
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
		String result = "";
		Configuration configuration = getConfigBean();
		JsonNode json = configuration.getJson();
		String workerName = LocalworkerServiceProvider.getServiceNameFromClassname(json.get(
				"localworkerName").textValue());
		if (json.get("isAltered").booleanValue()) {
			result = "Altered local worker service";
			if ((workerName != null) && !workerName.equals("")) {
				result += " - originally " + workerName;
			}
		} else {
			result = "Local worker service";
			if ((workerName != null) && !workerName.equals("")) {
				result += " - " + workerName;
			}
		}
		return result;
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new LocalworkerActivityConfigurationAction(getActivity(), owner, editManager,
				fileManager, activityIconManager, serviceDescriptionRegistry,
				applicationConfiguration);
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
