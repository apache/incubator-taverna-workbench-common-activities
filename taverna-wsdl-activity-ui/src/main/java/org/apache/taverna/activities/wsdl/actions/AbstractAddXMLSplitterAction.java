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

package org.apache.taverna.activities.wsdl.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.taverna.workbench.edits.EditException;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.wsdl.parser.ArrayTypeDescriptor;
import org.apache.taverna.wsdl.parser.ComplexTypeDescriptor;
import org.apache.taverna.wsdl.parser.TypeDescriptor;
import org.apache.taverna.wsdl.parser.UnknownOperationException;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.profiles.Profile;

/**
 * Abstract superclass of {@link AddXMLOutputSplitterAction} and
 * {@link AddXMLInputSplitterAction}.
 * <p>
 * Pops up a {@link JOptionPane} with the names of all the wsdl ports. The one
 * that is selected is added as an input/output splitter to the currently open
 * dataflow using the {@link AddXMLSplitterEdit}
 *
 * @author Ian Dunlop
 * @author Stian Soiland-Reyes
 * @author Stuart Owen
 *
 */
@SuppressWarnings("serial")
public abstract class AbstractAddXMLSplitterAction extends AbstractAction {

	private static Logger logger = Logger.getLogger(AddXMLOutputSplitterAction.class);

	protected Scufl2Tools scufl2Tools = new Scufl2Tools();

	protected JComponent owner;
	protected final Activity activity;
	protected final EditManager editManager;
	protected final SelectionManager selectionManager;

	public AbstractAddXMLSplitterAction(Activity activity,
			JComponent owner, EditManager editManager, SelectionManager selectionManager) {
		this.activity = activity;
		this.owner = owner;
		this.editManager = editManager;
		this.selectionManager = selectionManager;
	}

	public void actionPerformed(ActionEvent ev) {
		List<String> possibilities;
		Map<String, TypeDescriptor> typeDescriptors;
		try {
			typeDescriptors = getTypeDescriptors();
		} catch (UnknownOperationException ex) {
			logger.error("Can't find operation for activity "
					+ activity, ex);
			return;
		} catch (IOException | ParserConfigurationException | WSDLException | SAXException | JDOMException ex) {
			logger.error("Can't read definition for activity "
					+ activity, ex);
			return;
		}

		typeDescriptors = filterDescriptors(typeDescriptors);

		possibilities = new ArrayList<String>(typeDescriptors.keySet());
		if (possibilities.isEmpty()) {
			logger.warn("No type descriptors found for activity " + activity);
			return;
		}
		Collections.sort(possibilities);

		String portName = (String) JOptionPane.showInputDialog(owner,
				"Select the port to add the splitter to",
				"Add output XML splitter", JOptionPane.PLAIN_MESSAGE, null,
				possibilities.toArray(), possibilities.get(0));

		Workflow workflow = selectionManager.getSelectedWorkflow();
		Profile profile = selectionManager.getSelectedProfile();
		TypeDescriptor typeDescriptorForPort = typeDescriptors
				.get(portName);

		if (typeDescriptorForPort instanceof ArrayTypeDescriptor
				|| typeDescriptorForPort instanceof ComplexTypeDescriptor) {
			AddXMLSplitterEdit edit = new AddXMLSplitterEdit(workflow, profile,
					activity, typeDescriptorForPort, portName, isInput());
			try {
				editManager.doDataflowEdit(workflow.getParent(), edit);
			} catch (EditException ex) {
				logger.error("Could not perform edit to add " + portName, ex);
			}
		} else {
			logger.warn("Unknown typedescriptor for " + portName);
		}
	}

	public static Map<String, TypeDescriptor> filterDescriptors(
			Map<String, TypeDescriptor> descriptors) {
		Map<String, TypeDescriptor> filtered = new HashMap<String, TypeDescriptor>();
		for (Entry<String, TypeDescriptor> entry : descriptors.entrySet()) {
			TypeDescriptor descriptor = entry.getValue();
			if (descriptor.getMimeType().contains("'text/xml'")) {
				filtered.put(entry.getKey(), descriptor);
			}
		}
		return filtered;
	}

	protected abstract boolean isInput();

	public abstract Map<String, TypeDescriptor> getTypeDescriptors()
			throws UnknownOperationException, IOException, ParserConfigurationException, WSDLException, SAXException, JDOMException;

	public void setOwner(JComponent owner) {
		this.owner = owner;
	}
}