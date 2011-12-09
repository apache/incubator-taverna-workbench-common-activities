package net.sf.taverna.t2.activities.wsdl.views;

import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sf.taverna.t2.activities.wsdl.InputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.OutputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLInputSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLOutputSplitterAction;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public abstract class AbstractXMLSplitterActionView<BeanType> extends
		HTMLBasedActivityContextualView<BeanType> {

	private static Logger logger = Logger
			.getLogger(AbstractXMLSplitterActionView.class);

	public AbstractXMLSplitterActionView(Activity<?> activity) {
		super(activity);
	}

	protected void addOutputSplitter(final JComponent mainFrame,
			JPanel flowPanel) {
		if (getActivity() instanceof OutputPortTypeDescriptorActivity) {
			Map<String, TypeDescriptor> descriptors;
			try {
				descriptors = ((OutputPortTypeDescriptorActivity) getActivity())
						.getTypeDescriptorsForOutputPorts();
				if (!AbstractAddXMLSplitterAction
						.filterDescriptors(descriptors).isEmpty()) {
					AddXMLOutputSplitterAction outputSplitterAction = new AddXMLOutputSplitterAction(
							(OutputPortTypeDescriptorActivity) getActivity(),
							mainFrame);
					flowPanel.add(new JButton(outputSplitterAction));
				}
			} catch (UnknownOperationException e) {
				logger.warn("Could not find operation for " + getActivity(), e);
			} catch (IOException e) {
				logger
						.warn("Could not read definition for " + getActivity(),
								e);
			}
		}
	}

	protected void addInputSplitter(final JComponent mainFrame, JPanel flowPanel) {
		if (getActivity() instanceof InputPortTypeDescriptorActivity) {
			Map<String, TypeDescriptor> descriptors;
			try {
				descriptors = ((InputPortTypeDescriptorActivity) getActivity())
						.getTypeDescriptorsForInputPorts();
				if (!AbstractAddXMLSplitterAction
						.filterDescriptors(descriptors).isEmpty()) {
					AddXMLInputSplitterAction inputSplitterAction = new AddXMLInputSplitterAction(
							(InputPortTypeDescriptorActivity) getActivity(),
							mainFrame);
					flowPanel.add(new JButton(inputSplitterAction));
				}
			} catch (UnknownOperationException e) {
				logger.warn("Could not find operation for " + getActivity(), e);
			} catch (IOException e) {
				logger
						.warn("Could not read definition for " + getActivity(),
								e);
			}
		}
	}

	protected String describePorts() {
		StringBuilder html = new StringBuilder();

		if (!getActivity().getInputPorts().isEmpty()) {
			html.append("<tr><th colspan='2' align='left'>Inputs</th></tr>");
			for (ActivityInputPort port : getActivity().getInputPorts()) {
				TypeDescriptor descriptor=null;
				if (getActivity() instanceof InputPortTypeDescriptorActivity) {
					try {
						descriptor = ((InputPortTypeDescriptorActivity) getActivity())
								.getTypeDescriptorForInputPort(port.getName());
						
					} catch (UnknownOperationException e) {
						logger.warn(
								"Could not find operation for " + getActivity(), e);
					} catch (IOException e) {
						logger.warn("Could not read definition for "
								+ getActivity(), e);
					}
				}
				if (descriptor==null) {
					html.append(describePort(port));
				}
				else {
					html.append(describePort(port, descriptor));
				}
				
				
			}
		}
		
		if (!getActivity().getOutputPorts().isEmpty()) {
			html.append("<tr><th colspan='2' align='left'>Outputs</th></tr>");
			for (OutputPort port : getActivity().getOutputPorts()) {
				TypeDescriptor descriptor=null;
				if (getActivity() instanceof OutputPortTypeDescriptorActivity)
				{
					try {
						descriptor = ((OutputPortTypeDescriptorActivity) getActivity())
								.getTypeDescriptorForOutputPort(port.getName());
					} catch (UnknownOperationException e) {
						logger.warn(
								"Could not find operation for " + getActivity(), e);
					} catch (IOException e) {
						logger.warn("Could not read definition for "
								+ getActivity(), e);
					}
				}
				if (descriptor==null) {
					html.append(describePort(port));
				}
				else {
					html.append(describePort(port, descriptor));
				}
			}
		}

		return html.toString();
	}

	
	private String describePort(Port port, TypeDescriptor descriptor) {
		String html = "<tr><td>"+port.getName()+"</td><td>";
		if (descriptor!=null && descriptor.isOptional()) {
			html += "<em>optional</em><br>";
		}
		html+="Depth:"+port.getDepth()+"<br>";
		if (descriptor != null ) {
            html+="<code>"+descriptor.getQname().toString()+"</code><br>";
            if (descriptor.getDocumentation() != null && !descriptor.getDocumentation().isEmpty()){
                html += "<p>"+descriptor.getDocumentation()+"</p>";
            }
        }

		html+="</td></tr>";
		return html;
	}
	
	private String describePort(Port port) {
		String html = "<tr><td>"+port.getName()+"</td><td>";
		html+="Depth:"+port.getDepth()+"<br>";
		html+="</td></tr>";
		return html;
	}

}