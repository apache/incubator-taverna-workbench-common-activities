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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.taverna.activities.wsdl.servicedescriptions.WSDLServiceDescription;
import org.apache.taverna.workbench.edits.CompoundEdit;
import org.apache.taverna.workbench.edits.Edit;
import org.apache.taverna.workbench.edits.EditException;
import org.apache.taverna.workflow.edits.AddChildEdit;
import org.apache.taverna.workflow.edits.AddDataLinkEdit;
import org.apache.taverna.workflow.edits.AddProcessorEdit;
import org.apache.taverna.wsdl.parser.ArrayTypeDescriptor;
import org.apache.taverna.wsdl.parser.TypeDescriptor;
import org.apache.taverna.wsdl.xmlsplitter.XMLSplitterSerialisationHelper;

import org.jdom.Element;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.core.DataLink;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.Profile;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.taverna.activities.wsdl.xmlsplitter.XMLSplitterConfigurationBeanBuilder;

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
//				String wrappedType = new XMLOutputter().outputString(element);

				splitter = new Activity();
				splitter.setType(INPUT_SPLITTER_TYPE);
				splitterConfiguration = new Configuration();
				splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
				splitterConfiguration.setConfigures(splitter);
//				((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

				JsonNode bean = XMLSplitterConfigurationBeanBuilder.buildBeanForInput(element);
				splitterConfiguration.setJson(bean);

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
//				String wrappedType = new XMLOutputter().outputString(element);

				splitter = new Activity();
				splitter.setType(OUTPUT_SPLITTER_TYPE);
				splitterConfiguration = new Configuration();
				splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
				splitterConfiguration.setConfigures(splitter);
//				((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

				JsonNode bean = XMLSplitterConfigurationBeanBuilder.buildBeanForOutput(element);
				splitterConfiguration.setJson(bean);

				XMLSplitterPortBuilder.addPortsForOutput(element, splitter, splitterProcessor,
						processorBinding);

			} else if (activity.getType().equals(WSDLServiceDescription.ACTIVITY_TYPE)) {
				if (isInput) {
					Element element = XMLSplitterSerialisationHelper
							.typeDescriptorToExtensionXML(typeDescriptor);
//					String wrappedType = new XMLOutputter().outputString(element);

					splitter = new Activity();
					splitter.setType(WSDLServiceDescription.INPUT_SPLITTER_TYPE);
					splitterConfiguration = new Configuration();
					splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
					splitterConfiguration.setConfigures(splitter);
//					((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

					JsonNode bean = XMLSplitterConfigurationBeanBuilder.buildBeanForInput(element);
					splitterConfiguration.setJson(bean);

					XMLSplitterPortBuilder.addPortsForInput(element, splitter, splitterProcessor,
							processorBinding);

				} else {
					Element element = XMLSplitterSerialisationHelper
							.typeDescriptorToExtensionXML(typeDescriptor);
//					String wrappedType = new XMLOutputter().outputString(element);

					splitter = new Activity();
					splitter.setType(WSDLServiceDescription.OUTPUT_SPLITTER_TYPE);
					splitterConfiguration = new Configuration();
					splitterConfiguration.setType(SPLITTER_CONFIG_TYPE);
					splitterConfiguration.setConfigures(splitter);
//					((ObjectNode) splitterConfiguration.getJson()).put("wrappedType", wrappedType);

					JsonNode bean = XMLSplitterConfigurationBeanBuilder.buildBeanForOutput(element);
					splitterConfiguration.setJson(bean);

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
