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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXException;

import org.apache.taverna.activities.wsdl.servicedescriptions.WSDLServiceDescription;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.wsdl.parser.ArrayTypeDescriptor;
import org.apache.taverna.wsdl.parser.ComplexTypeDescriptor;
import org.apache.taverna.wsdl.parser.TypeDescriptor;
import org.apache.taverna.wsdl.parser.UnknownOperationException;
import org.apache.taverna.wsdl.parser.WSDLParser;
import org.apache.taverna.wsdl.xmlsplitter.XMLSplitterSerialisationHelper;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;

/**
 * Pops up a {@link JOptionPane} with the names of all the wsdl ports. The one
 * that is selected is added as an output splitter to the currently open
 * dataflow using the {@link AddXMLSplitterEdit}
 *
 * @author Ian Dunlop
 * @author Stian Soiland-Reyes
 */
@SuppressWarnings("serial")
public class AddXMLOutputSplitterAction extends AbstractAddXMLSplitterAction {

	public AddXMLOutputSplitterAction(Activity activity, JComponent owner, EditManager editManager,
			SelectionManager selectionManager) {
		super(activity, owner, editManager, selectionManager);
		putValue(NAME, "Add output XML splitter");

	}

	@Override
	public Map<String, TypeDescriptor> getTypeDescriptors() throws UnknownOperationException, IOException, ParserConfigurationException, WSDLException, SAXException, JDOMException {
		Map<String, TypeDescriptor> descriptors = new HashMap<String, TypeDescriptor>();
		Configuration configuration = scufl2Tools.configurationFor(activity, selectionManager.getSelectedProfile());
		if (activity.getType().equals(WSDLServiceDescription.ACTIVITY_TYPE)) {
			String wsdlLocation = configuration.getJson().get("operation").get("wsdl").textValue();
			String operationName = configuration.getJson().get("operation").get("name").textValue();
			List<TypeDescriptor> inputDescriptors = new WSDLParser(wsdlLocation)
					.getOperationOutputParameters(operationName);
			for (TypeDescriptor descriptor : inputDescriptors) {
				descriptors.put(descriptor.getName(), descriptor);
			}
		} else if (activity.getType().equals(WSDLServiceDescription.OUTPUT_SPLITTER_TYPE)) {
			String wrappedType = configuration.getJson().get("wrappedType").textValue();
			Element element = new SAXBuilder().build(new StringReader(wrappedType)).getRootElement();
			TypeDescriptor typeDescriptor = XMLSplitterSerialisationHelper.extensionXMLToTypeDescriptor(element);
			if (typeDescriptor instanceof ComplexTypeDescriptor) {
				for (TypeDescriptor desc : ((ComplexTypeDescriptor) typeDescriptor)
						.getElements()) {
					descriptors.put(desc.getName(), desc);
				}
			}
			else if (typeDescriptor instanceof ArrayTypeDescriptor) {
				TypeDescriptor desc = ((ArrayTypeDescriptor)typeDescriptor).getElementType();
				descriptors.put(typeDescriptor.getName(), desc);
			}
		}
		return descriptors;
	}

	@Override
	protected boolean isInput() {
		return false;
	}

}
