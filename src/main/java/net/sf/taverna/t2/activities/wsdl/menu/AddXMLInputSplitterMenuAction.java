/**
 * 
 */
package net.sf.taverna.t2.activities.wsdl.menu;

import java.io.IOException;
import java.util.Map;

import javax.swing.Action;

import net.sf.taverna.t2.activities.wsdl.InputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLInputSplitterAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

/**
 * @author alanrw
 *
 */
public abstract class AddXMLInputSplitterMenuAction<ActivityClass extends Activity<?>> extends
		AbstractConfigureActivityMenuAction<ActivityClass> {

	private static final String ADD_XML_INPUT_SPLITTER = "Add XML Input Splitter";

	public AddXMLInputSplitterMenuAction(Class<ActivityClass> activityClass) {
		super(activityClass);
	}

	@Override
	protected Action createAction() {
		Map<String, TypeDescriptor> descriptors;
		try {
			descriptors = ((InputPortTypeDescriptorActivity) findActivity())
					.getTypeDescriptorsForInputPorts();
		} catch (UnknownOperationException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		if (!AbstractAddXMLSplitterAction.filterDescriptors(descriptors)
				.isEmpty()) {
			AddXMLInputSplitterAction configAction = new AddXMLInputSplitterAction(
					( InputPortTypeDescriptorActivity) findActivity(), null);
			configAction.putValue(Action.NAME, ADD_XML_INPUT_SPLITTER);
			addMenuDots(configAction);
			return configAction;
		} else {
			return null;
		}
	}

}
