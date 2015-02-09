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

import net.sf.taverna.wsdl.parser.ArrayTypeDescriptor;
import net.sf.taverna.wsdl.parser.BaseTypeDescriptor;
import net.sf.taverna.wsdl.parser.ComplexTypeDescriptor;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.xmlsplitter.XMLSplitterSerialisationHelper;

import org.jdom.Element;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputProcessorPort;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorInputPortBinding;
import uk.org.taverna.scufl2.api.profiles.ProcessorOutputPortBinding;

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
