package net.sf.taverna.t2.activities.wsdl.actions;
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

import net.sf.taverna.t2.workbench.edits.EditException;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.wsdl.parser.ArrayTypeDescriptor;
import net.sf.taverna.wsdl.parser.ComplexTypeDescriptor;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;

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