/**
 * 
 */
package net.sf.taverna.t2.activities.wsdl.menu;

import java.io.IOException;
import java.util.Map;

import javax.swing.Action;

import net.sf.taverna.t2.activities.wsdl.InputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.OutputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLInputSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLOutputSplitterAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

/**
 * @author alanrw
 *
 */
public abstract class AddXMLOutputSplitterMenuAction<ActivityClass extends Activity<?>> extends
		AbstractConfigureActivityMenuAction<ActivityClass> {

	private static final String ADD_XML_OUTPUT_SPLITTER = "Add XML Output Splitter";

	public AddXMLOutputSplitterMenuAction(Class<ActivityClass> activityClass) {
		super(activityClass);
	}

	@Override
	protected Action createAction() {
		Map<String, TypeDescriptor> descriptors;
		try {
			descriptors = ((OutputPortTypeDescriptorActivity) findActivity())
					.getTypeDescriptorsForOutputPorts();
		} catch (UnknownOperationException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		if (!AbstractAddXMLSplitterAction.filterDescriptors(descriptors)
				.isEmpty()) {
			AddXMLOutputSplitterAction configAction = new AddXMLOutputSplitterAction(
					( OutputPortTypeDescriptorActivity) findActivity(), null);
			configAction.putValue(Action.NAME, ADD_XML_OUTPUT_SPLITTER);
			addMenuDots(configAction);
			return configAction;
		} else {
			return null;
		}
	}

}
