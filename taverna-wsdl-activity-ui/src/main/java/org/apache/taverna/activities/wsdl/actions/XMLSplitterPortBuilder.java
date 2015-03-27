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

import org.apache.taverna.wsdl.parser.ArrayTypeDescriptor;
import org.apache.taverna.wsdl.parser.BaseTypeDescriptor;
import org.apache.taverna.wsdl.parser.ComplexTypeDescriptor;
import org.apache.taverna.wsdl.parser.TypeDescriptor;
import org.apache.taverna.wsdl.xmlsplitter.XMLSplitterSerialisationHelper;

import org.jdom.Element;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.NamedSet;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputProcessorPort;
import org.apache.taverna.scufl2.api.profiles.ProcessorBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import org.apache.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;

/**
 * A helper class to facilitate in building XMLSplitter ports
 * from the type descriptor XML.
 *
 * @author Stuart Owen
 * @author David Withers
 */
public class XMLSplitterPortBuilder {

	public static void addPortsForInput(Element element, Activity activity, Processor processor,
			ProcessorBinding binding) {
		TypeDescriptor descriptor = XMLSplitterSerialisationHelper.extensionXMLToTypeDescriptor(element);
		addOutputPort("output", 0, activity, processor, binding);

		if (descriptor instanceof ComplexTypeDescriptor) {
			for (TypeDescriptor typeDescriptor : ((ComplexTypeDescriptor) descriptor).getElements()) {
				addInputPort(typeDescriptor.getName(), depthForDescriptor(typeDescriptor), activity, processor,
						binding);
			}
			NamedSet<InputActivityPort> inputPorts = activity.getInputPorts();
			for (TypeDescriptor typeDescriptor : ((ComplexTypeDescriptor) descriptor).getAttributes()) {
				String name = typeDescriptor.getName();
				if (inputPorts.containsName(name)) {
					name = "1" + name;
				}
				addInputPort(name, depthForDescriptor(typeDescriptor), activity, processor, binding);
			}
		} else if (descriptor instanceof ArrayTypeDescriptor) {
			addInputPort(descriptor.getName(), 1, activity, processor, binding);
		}
	}

	public static void addPortsForOutput(Element element, Activity activity, Processor processor,
			ProcessorBinding binding) {
		TypeDescriptor descriptor = XMLSplitterSerialisationHelper.extensionXMLToTypeDescriptor(element);
		addInputPort("input", 0, activity, processor, binding);

		if (descriptor instanceof ComplexTypeDescriptor) {
			for (TypeDescriptor typeDescriptor : ((ComplexTypeDescriptor) descriptor).getElements()) {
				addOutputPort(typeDescriptor.getName(), depthForDescriptor(typeDescriptor), activity, processor,
						binding);
			}
			NamedSet<OutputActivityPort> outputPorts = activity.getOutputPorts();
			for (TypeDescriptor typeDescriptor : ((ComplexTypeDescriptor) descriptor).getAttributes()) {
				String name = typeDescriptor.getName();
				if (outputPorts.containsName(name)) {
					name = "1" + name;
				}
				addOutputPort(name, depthForDescriptor(typeDescriptor), activity, processor, binding);
			}
		} else if (descriptor instanceof ArrayTypeDescriptor) {
			addOutputPort(descriptor.getName(), 1, activity, processor, binding);
		}
	}

	private static int depthForDescriptor(TypeDescriptor desc) {
		if (desc instanceof ArrayTypeDescriptor
				&& (!((ArrayTypeDescriptor) desc).isWrapped() || ((ArrayTypeDescriptor) desc)
						.getElementType() instanceof BaseTypeDescriptor)) {
			return 1;
		} else {
			return 0;
		}
	}

	private static void addOutputPort(String name, int depth, Activity activity,
			Processor processor, ProcessorBinding binding) {
		OutputActivityPort activityPort = new OutputActivityPort(activity, name);
		activityPort.setDepth(depth);
		activityPort.setGranularDepth(depth);
		OutputProcessorPort processorPort = new OutputProcessorPort(processor, name);
		processorPort.setDepth(depth);
		processorPort.setGranularDepth(depth);
		new ProcessorOutputPortBinding(binding, activityPort, processorPort);
	}

	private static void addInputPort(String name, int depth, Activity activity,
			Processor processor, ProcessorBinding binding) {
		InputActivityPort activityPort = new InputActivityPort(activity, name);
		activityPort.setDepth(depth);
		InputProcessorPort processorPort = new InputProcessorPort(processor, name);
		processorPort.setDepth(depth);
		new ProcessorInputPortBinding(binding, processorPort, activityPort);
	}

}
