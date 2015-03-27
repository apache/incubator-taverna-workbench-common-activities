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

package org.apache.taverna.activities.wsdl.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.taverna.activities.wsdl.actions.WSDLActivityConfigureAction;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.scufl2.api.activity.Activity;

import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("serial")
public class WSDLActivityContextualView extends AbstractXMLSplitterActionView {

	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final CredentialManager credentialManager;
	private final FileManager fileManager;

	public WSDLActivityContextualView(Activity activity, EditManager editManager, FileManager fileManager,
			SelectionManager selectionManager, ActivityIconManager activityIconManager,
			ColourManager colourManager, CredentialManager credentialManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry) {
		super(activity, editManager, selectionManager, colourManager);
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.credentialManager = credentialManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	/**
	 * Gets the component from the {@link HTMLBasedActivityContextualView} and adds buttons to it
	 * allowing XML splitters to be added
	 */
	@Override
	public JComponent getMainFrame() {
		final JComponent mainFrame = super.getMainFrame();
		JPanel flowPanel = new JPanel(new FlowLayout());

		addInputSplitter(mainFrame, flowPanel);
		addOutputSplitter(mainFrame, flowPanel);

		mainFrame.add(flowPanel, BorderLayout.SOUTH);
		return mainFrame;
	}

	@Override
	public String getViewTitle() {
		return "WSDL-based service";
	}

	@Override
	protected String getRawTableRowsHtml() {
		JsonNode operation = getConfigBean().getJson().get("operation");
		String summary = "<tr><td>WSDL</td><td>" + operation.get("wsdl").textValue();
		summary += "</td></tr><tr><td>Operation</td><td>" + operation.get("name").textValue()
				+ "</td></tr>";
		boolean securityConfigured = getConfigBean().getJson().has("securityProfile");
		summary += "<tr><td>Secure</td><td>" + securityConfigured + "</td></tr>";
		summary += "</tr>";
		summary += describePorts();
		return summary;
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new WSDLActivityConfigureAction(getActivity(), owner, editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, credentialManager);
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
