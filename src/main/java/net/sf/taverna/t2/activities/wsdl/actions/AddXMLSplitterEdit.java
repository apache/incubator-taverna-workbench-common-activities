/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester
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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.sf.taverna.t2.activities.wsdl.servicedescriptions.WSDLServiceDescription;
import net.sf.taverna.t2.workbench.edits.CompoundEdit;
import net.sf.taverna.t2.workbench.edits.Edit;
import net.sf.taverna.t2.workbench.edits.EditException;
import net.sf.taverna.t2.workflow.edits.AddChildEdit;
import net.sf.taverna.t2.workflow.edits.AddDataLinkEdit;
import net.sf.taverna.t2.workflow.edits.AddProcessorEdit;
import net.sf.taverna.wsdl.parser.ArrayTypeDescriptor;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.xmlsplitter.XMLSplitterSerialisationHelper;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.core.DataLink;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class AddXMLSplitterEdit implements Edit<Workflow> {

	public static final URI INPUT_SPLITTER_TYPE = URI
			.create("http://ns.taverna.org.uk/2010/activity/xml-splitter/in");
	public static final URI OUTPUT_SPLITTER_TYPE = URI
			.create("http://ns.taverna.org.uk/2010/activity/xml-splitter/out");
	public static final URI SPLITTER_CONFIG_TYPE = URI
			.create("http://ns.taverna.org.uk/2010/activity/xml-splitter#Config");

	private Scufl2Tools scufl2Tools = new Scufl2Tools();

	private final Workflow workflow;
	private final Profile profile;
	private final Activity activity;
	private TypeDescriptor typeDescriptor;
	private final String portName;
	private final boolean isInput;

	private CompoundEdit compoundEdit1 = null;
	private Edit<?> linkUpEdit;
	private boolean applied = false;

	public AddXMLSplitterEdit(Workflow workflow, Profile profile, Activity activity,
			TypeDescriptor typeDescriptor, String portName, boolean isInput) {
		this.workflow = workflow;
		this.profile = profile;
		this.activity = activity;
		this.typeDescriptor = typeDescriptor;
		this.portName = portName;
		this.isInput = isInput;
	}

	@Override
	public Workflow doEdit() throws EditException {
		if (applied) {
			throw new EditException("Edit has already been applied!");
		}

		Activity splitter = null;
		Configuration splitterConfiguration = null;
		String sourcePortName = "";
		Processor sourceProcessor = null;

		String sinkPortName = "";
		Processor sinkProcessor = null;

		Processor activityProcessor = null;
		List<ProcessorBinding> processorBindingsToActivity = scufl2Tools
				.processorBindingsToActivity(activity);
		for (ProcessorBinding processorBinding : processorBindingsToActivity) {
			activityProcessor = processorBinding.getBoundProcessor();
			break;
		}
		if (activityProcessor == null) {
			throw new EditException("Cannot find the processor that the activity belongs to");
		}

		String displayName = portName;
		if (portName.equals("parameters")) {
			displayName = isInput ? "input" : "output";
		}
		String processorName = activityProcessor.getName();
		String candidateName;
		if (displayName.startsWith(processorName)) {
			// No need to make GetRequest_GetRequestResponse
			candidateName = displayName;
		} else {
			// Combine with processor name
			String displayProcessorName;
			if (activity.getType().equals(INPUT_SPLITTER_TYPE)
					|| activity.getType().equals(OUTPUT_SPLITTER_TYPE)) {
				// For splitters on splitters - avoid adding up blah_bluh_blih_more_stuff
				String[] processorNameSplit = processorName.replace("_input", "")
						.replace("_output", "").split("_");
				displayProcessorName = processorNameSplit[processorNameSplit.length - 1];
			} else {
				displayProcessorName = activityProcessor.getName();
			}
			candidateName = displayProcessorName + "_" + displayName;
		}

		Processor splitterProcessor = new Processor();
		splitterProcessor.setName(candidateName);

		CrossProduct crossProduct = new CrossProduct();
		crossProduct.setParent(splitterProcessor.getIterationStrategyStack());

		ProcessorBinding processorBinding = new ProcessorBinding();
		processorBinding.setBoundProcessor(splitterProcessor);

		try {
			if (activity.getType().equals(INPUT_SPLITTER_TYPE)) {
				if (!isInput) {
					throw new EditException(
							"Can only add an input splitter to another input splitter");
				}
				if (typeDescriptor instanceof ArrayTypeDescriptor
						&& !((ArrayTypeDescriptor) typeDescriptor).isWrapped()) {
					typeDescriptor = ((ArrayTypeDescriptor) typeDescriptor).getElementType();
				}

				Element element = XMLSplitterSerialisationHelper
						.typeDescriptorToExtensionXML(typeDescriptor);
				String wrappedType = new XMLOutputter().outputString(element);

				splitter = new Activity();
				splitter.setType(INPUT_SPLITTER_TYPE);
				splitterConfiguration = new Configuration();
				splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
				splitterConfiguration.setConfigures(splitter);
				((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

				XMLSplitterPortBuilder.addPortsForInput(element, splitter, splitterProcessor,
						processorBinding);

			} else if (activity.getType().equals(OUTPUT_SPLITTER_TYPE)) {
				if (isInput) {
					throw new EditException(
							"Can only add an output splitter to another output splitter");
				}
				if (typeDescriptor instanceof ArrayTypeDescriptor
						&& !((ArrayTypeDescriptor) typeDescriptor).isWrapped()) {
					typeDescriptor = ((ArrayTypeDescriptor) typeDescriptor).getElementType();
				}

				Element element = XMLSplitterSerialisationHelper
						.typeDescriptorToExtensionXML(typeDescriptor);
				String wrappedType = new XMLOutputter().outputString(element);

				splitter = new Activity();
				splitter.setType(OUTPUT_SPLITTER_TYPE);
				splitterConfiguration = new Configuration();
				splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
				splitterConfiguration.setConfigures(splitter);
				((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

				XMLSplitterPortBuilder.addPortsForOutput(element, splitter, splitterProcessor,
						processorBinding);

			} else if (activity.getType().equals(WSDLServiceDescription.ACTIVITY_TYPE)) {
				if (isInput) {
					Element element = XMLSplitterSerialisationHelper
							.typeDescriptorToExtensionXML(typeDescriptor);
					String wrappedType = new XMLOutputter().outputString(element);

					splitter = new Activity();
					splitter.setType(WSDLServiceDescription.INPUT_SPLITTER_TYPE);
					splitterConfiguration = new Configuration();
					splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
					splitterConfiguration.setConfigures(splitter);
					((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

					XMLSplitterPortBuilder.addPortsForInput(element, splitter, splitterProcessor,
							processorBinding);

				} else {
					Element element = XMLSplitterSerialisationHelper
							.typeDescriptorToExtensionXML(typeDescriptor);
					String wrappedType = new XMLOutputter().outputString(element);

					splitter = new Activity();
					splitter.setType(WSDLServiceDescription.OUTPUT_SPLITTER_TYPE);
					splitterConfiguration = new Configuration();
					splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
					splitterConfiguration.setConfigures(splitter);
					((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

					XMLSplitterPortBuilder.addPortsForOutput(element, splitter, splitterProcessor,
							processorBinding);
				}
			} else {
				throw new EditException(
						"The activity type is not suitable for adding xml processing processors");
			}
		} catch (Exception e) {
			throw new EditException(
					"An error occured whilst tyring to add an XMLSplitter to the activity:"
							+ activity, e);
		}

		if (isInput) {
			sourcePortName = "output";
			sinkPortName = portName;
			sinkProcessor = activityProcessor;
			sourceProcessor = splitterProcessor;
		} else {
			sourcePortName = portName;
			sinkPortName = "input";
			sinkProcessor = splitterProcessor;
			sourceProcessor = activityProcessor;
		}

		processorBinding.setBoundActivity(splitter);

		List<Edit<?>> editList = new ArrayList<Edit<?>>();
		editList.add(new AddChildEdit<Profile>(profile, splitter));
		editList.add(new AddChildEdit<Profile>(profile, splitterConfiguration));
		editList.add(new AddChildEdit<Profile>(profile, processorBinding));
		editList.add(new AddProcessorEdit(workflow, splitterProcessor));

		compoundEdit1 = new CompoundEdit(editList);
		compoundEdit1.doEdit();

		List<Edit<?>> linkUpEditList = new ArrayList<Edit<?>>();

		OutputProcessorPort source = sourceProcessor.getOutputPorts().getByName(sourcePortName);
		InputProcessorPort sink = sinkProcessor.getInputPorts().getByName(sinkPortName);

		if (source == null)
			throw new EditException("Unable to find the source port when linking up "
					+ sourcePortName + " to " + sinkPortName);
		if (sink == null)
			throw new EditException("Unable to find the sink port when linking up "
					+ sourcePortName + " to " + sinkPortName);

		DataLink dataLink = new DataLink();
		dataLink.setReceivesFrom(source);
		dataLink.setSendsTo(sink);
		linkUpEditList.add(new AddDataLinkEdit(workflow, dataLink));

		linkUpEdit = new CompoundEdit(linkUpEditList);
		linkUpEdit.doEdit();
		applied = true;
		return workflow;
	}

	@Override
	public void undo() {
		if (!applied) {
			throw new RuntimeException("Attempt to undo edit that was never applied");
		}
		if (linkUpEdit.isApplied())
			linkUpEdit.undo();
		if (compoundEdit1.isApplied())
			compoundEdit1.undo();
		applied = false;
	}

	@Override
	public boolean isApplied() {
		return applied;
	}

	@Override
	public Object getSubject() {
		return workflow;
	}

}
