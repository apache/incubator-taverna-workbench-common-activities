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

import net.sf.taverna.t2.activities.wsdl.xmlsplitter.AddXMLSplitterEdit;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.wsdl.parser.ArrayTypeDescriptor;
import net.sf.taverna.wsdl.parser.ComplexTypeDescriptor;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

import org.apache.log4j.Logger;

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
@SuppressWarnings("unchecked")
public abstract class AbstractAddXMLSplitterAction<ActivityType> extends AbstractAction {

	private static Logger logger = Logger
			.getLogger(AddXMLOutputSplitterAction.class);
	private EditManager editManager = EditManager.getInstance();
	protected final JComponent owner;
	protected final ActivityType activity;

	public AbstractAddXMLSplitterAction(ActivityType activity,
			JComponent owner) {
		this.activity = activity;
		this.owner = owner;
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
		} catch (IOException ex) {
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

		Dataflow currentDataflow = FileManager.getInstance()
				.getCurrentDataflow();
		TypeDescriptor typeDescriptorForOutputPort = typeDescriptors
				.get(portName);

		if (typeDescriptorForOutputPort instanceof ArrayTypeDescriptor
				|| typeDescriptorForOutputPort instanceof ComplexTypeDescriptor) {
			AddXMLSplitterEdit edit = new AddXMLSplitterEdit(currentDataflow,
					(Activity)activity, portName, isInput());
			try {
				editManager.doDataflowEdit(currentDataflow, edit);
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

	protected abstract Map<String, TypeDescriptor> getTypeDescriptors()
			throws UnknownOperationException, IOException;
}