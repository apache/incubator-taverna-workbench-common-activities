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

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;

import org.apache.log4j.Logger;

import org.apache.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
public class XMLSplitterContextualView extends AbstractXMLSplitterActionView {

	public XMLSplitterContextualView(Activity activity,
			EditManager editManager, SelectionManager selectionManager, ColourManager colourManager) {
		super(activity, editManager, selectionManager, colourManager);
	}

	static Logger logger = Logger.getLogger(XMLSplitterContextualView.class);

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
		return "XML splitter";
	}

	@Override
	protected String getRawTableRowsHtml() {
		return describePorts();
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
