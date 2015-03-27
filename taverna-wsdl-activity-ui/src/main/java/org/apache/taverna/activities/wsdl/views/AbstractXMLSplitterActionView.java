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

import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.taverna.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import org.apache.taverna.activities.wsdl.actions.AddXMLInputSplitterAction;
import org.apache.taverna.activities.wsdl.actions.AddXMLOutputSplitterAction;
import org.apache.taverna.activities.wsdl.servicedescriptions.WSDLServiceDescription;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.wsdl.parser.TypeDescriptor;
import org.apache.taverna.wsdl.parser.UnknownOperationException;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.port.DepthPort;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

@SuppressWarnings("serial")
public abstract class AbstractXMLSplitterActionView extends HTMLBasedActivityContextualView {

	private static Logger logger = Logger.getLogger(AbstractXMLSplitterActionView.class);
	protected final EditManager editManager;
	protected final SelectionManager selectionManager;
	protected AbstractAddXMLSplitterAction splitterAction;

	public AbstractXMLSplitterActionView(Activity activity, EditManager editManager,
			SelectionManager selectionManager, ColourManager colourManager) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.selectionManager = selectionManager;
		if (getActivity().getType().equals(WSDLServiceDescription.OUTPUT_SPLITTER_TYPE)) {
			splitterAction = new AddXMLOutputSplitterAction(getActivity(),
					null, editManager, selectionManager);
		} else if (getActivity().getType().equals(WSDLServiceDescription.INPUT_SPLITTER_TYPE)) {
			splitterAction = new AddXMLInputSplitterAction(getActivity(),
					null, editManager, selectionManager);
		}
		super.initView();
	}

	@Override
	public void initView() {
	}

	protected void addOutputSplitter(final JComponent mainFrame, JPanel flowPanel) {
		if (getActivity().getType().equals(WSDLServiceDescription.OUTPUT_SPLITTER_TYPE)) {
			try {
				Map<String, TypeDescriptor> descriptors = splitterAction.getTypeDescriptors();
				if (!AbstractAddXMLSplitterAction.filterDescriptors(descriptors).isEmpty()) {
					flowPanel.add(new JButton(splitterAction));
				}
			} catch (UnknownOperationException | IOException | ParserConfigurationException
					| WSDLException | SAXException | JDOMException e) {
				logger.warn("Could not find type descriptors for " + getActivity(), e);
			}
		}
	}

	protected void addInputSplitter(final JComponent mainFrame, JPanel flowPanel) {
		if (getActivity().getType().equals(WSDLServiceDescription.INPUT_SPLITTER_TYPE)) {
			try {
				Map<String, TypeDescriptor> descriptors = splitterAction.getTypeDescriptors();
				if (!AbstractAddXMLSplitterAction.filterDescriptors(descriptors).isEmpty()) {
					splitterAction.setOwner(mainFrame);
					flowPanel.add(new JButton(splitterAction));
				}
			} catch (UnknownOperationException | IOException | ParserConfigurationException
					| WSDLException | SAXException | JDOMException e) {
				logger.warn("Could not find type descriptors for " + getActivity(), e);
			}
		}
	}

	protected String describePorts() {
		StringBuilder html = new StringBuilder();

		if (!getActivity().getInputPorts().isEmpty()) {
			html.append("<tr><th colspan='2' align='left'>Inputs</th></tr>");
			for (InputActivityPort port : getActivity().getInputPorts()) {
				TypeDescriptor descriptor = null;
				if (getActivity().getType().equals(WSDLServiceDescription.INPUT_SPLITTER_TYPE)) {
					try {
						descriptor = splitterAction.getTypeDescriptors().get(port.getName());
					} catch (UnknownOperationException | IOException | ParserConfigurationException
							| WSDLException | SAXException | JDOMException e) {
						logger.warn("Could not find type descriptors for " + getActivity(), e);
					}
				}
				if (descriptor == null) {
					html.append(describePort(port));
				} else {
					html.append(describePort(port, descriptor));
				}

			}
		}

		if (!getActivity().getOutputPorts().isEmpty()) {
			html.append("<tr><th colspan='2' align='left'>Outputs</th></tr>");
			for (OutputActivityPort port : getActivity().getOutputPorts()) {
				TypeDescriptor descriptor = null;
				if (getActivity().getType().equals(WSDLServiceDescription.OUTPUT_SPLITTER_TYPE)) {
					try {
						descriptor = splitterAction.getTypeDescriptors().get(port.getName());
					} catch (UnknownOperationException | IOException | ParserConfigurationException
							| WSDLException | SAXException | JDOMException e) {
						logger.warn("Could not find type descriptors for " + getActivity(), e);
					}
				}
				if (descriptor == null) {
					html.append(describePort(port));
				} else {
					html.append(describePort(port, descriptor));
				}
			}
		}

		return html.toString();
	}

	private String describePort(DepthPort port, TypeDescriptor descriptor) {
		String html = "<tr><td>" + port.getName() + "</td><td>";
		if (descriptor != null && descriptor.isOptional()) {
			html += "<em>optional</em><br>";
		}
		html+="Depth:"+port.getDepth()+"<br>";
		if (descriptor != null ) {
            html+="<code>"+descriptor.getQname().toString()+"</code><br>";
//            if (descriptor.getDocumentation() != null && !descriptor.getDocumentation().isEmpty()){
//                html += "<p>"+descriptor.getDocumentation()+"</p>";
//            }
        }

		html+="</td></tr>";
		return html;
	}

	private String describePort(DepthPort port) {
		String html = "<tr><td>" + port.getName() + "</td><td>";
		html += "Depth:" + port.getDepth() + "<br>";
		html += "</td></tr>";
		return html;
	}

}