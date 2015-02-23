/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester
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
package net.sf.taverna.t2.activities.wsdl.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sf.taverna.t2.activities.wsdl.actions.WSDLActivityConfigureAction;
import net.sf.taverna.t2.security.credentialmanager.CredentialManager;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import uk.org.taverna.scufl2.api.activity.Activity;

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
