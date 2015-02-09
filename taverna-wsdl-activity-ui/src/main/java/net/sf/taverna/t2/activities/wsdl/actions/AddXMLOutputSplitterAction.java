/*******************************************************************************
 * Copyright (C) 2008 The University of Manchester
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
package net.sf.taverna.t2.activities.wsdl.actions;

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

import net.sf.taverna.t2.activities.wsdl.servicedescriptions.WSDLServiceDescription;
import net.sf.taverna.t2.activities.wsdl.xmlsplitter.AddXMLSplitterEdit;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.wsdl.parser.ArrayTypeDescriptor;
import net.sf.taverna.wsdl.parser.ComplexTypeDescriptor;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;
import net.sf.taverna.wsdl.parser.WSDLParser;
import net.sf.taverna.wsdl.xmlsplitter.XMLSplitterSerialisationHelper;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;

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
