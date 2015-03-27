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

package org.apache.taverna.activities.wsdl.menu;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.swing.Action;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.taverna.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import org.apache.taverna.activities.wsdl.actions.AddXMLOutputSplitterAction;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.wsdl.parser.TypeDescriptor;
import org.apache.taverna.wsdl.parser.UnknownOperationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * @author alanrw
 */
public abstract class AddXMLOutputSplitterMenuAction extends AbstractConfigureActivityMenuAction {

	private static final String ADD_XML_OUTPUT_SPLITTER = "Add XML Output Splitter";
	private EditManager editManager;
	private SelectionManager selectionManager;

	public AddXMLOutputSplitterMenuAction(URI activityType) {
		super(activityType);
	}

	@Override
	protected Action createAction() {
		AddXMLOutputSplitterAction configAction = new AddXMLOutputSplitterAction(
				findActivity(), null, editManager, selectionManager);
		Map<String, TypeDescriptor> descriptors;
		try {
			descriptors = configAction.getTypeDescriptors();
		} catch (UnknownOperationException | IOException | ParserConfigurationException
				| WSDLException | SAXException | JDOMException e) {
			return null;
		}
		if (!AbstractAddXMLSplitterAction.filterDescriptors(descriptors).isEmpty()) {
			configAction.putValue(Action.NAME, ADD_XML_OUTPUT_SPLITTER);
			addMenuDots(configAction);
			return configAction;
		} else {
			return null;
		}
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

}
