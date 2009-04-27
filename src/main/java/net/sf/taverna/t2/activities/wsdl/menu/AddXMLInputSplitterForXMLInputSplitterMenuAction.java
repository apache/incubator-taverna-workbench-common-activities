package net.sf.taverna.t2.activities.wsdl.menu;

import java.io.IOException;
import java.util.Map;

import javax.swing.Action;

import net.sf.taverna.t2.activities.wsdl.InputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.WSDLActivity;
import net.sf.taverna.t2.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLInputSplitterAction;
import net.sf.taverna.t2.activities.wsdl.xmlsplitter.XMLInputSplitterActivity;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.activities.wsdl.InputPortTypeDescriptorActivity;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

public class AddXMLInputSplitterForXMLInputSplitterMenuAction extends
			AddXMLInputSplitterMenuAction<XMLInputSplitterActivity> {

	public AddXMLInputSplitterForXMLInputSplitterMenuAction() {
		super(XMLInputSplitterActivity.class);
	}

}
