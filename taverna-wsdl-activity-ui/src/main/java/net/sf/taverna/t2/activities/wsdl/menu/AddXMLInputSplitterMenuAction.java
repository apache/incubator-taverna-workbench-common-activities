/**
 *
 */
package net.sf.taverna.t2.activities.wsdl.menu;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import javax.swing.Action;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.xml.sax.SAXException;

import net.sf.taverna.t2.activities.wsdl.InputPortTypeDescriptorActivity;
import net.sf.taverna.t2.activities.wsdl.actions.AbstractAddXMLSplitterAction;
import net.sf.taverna.t2.activities.wsdl.actions.AddXMLInputSplitterAction;
import net.sf.taverna.t2.ui.menu.ContextualMenuComponent;
import net.sf.taverna.t2.ui.menu.MenuComponent;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.selection.SelectionManager;
import net.sf.taverna.wsdl.parser.TypeDescriptor;
import net.sf.taverna.wsdl.parser.UnknownOperationException;

/**
 * @author alanrw
 */
public abstract class AddXMLInputSplitterMenuAction extends AbstractConfigureActivityMenuAction
		implements MenuComponent, ContextualMenuComponent {

	private static final String ADD_XML_INPUT_SPLITTER = "Add XML Input Splitter";
	private EditManager editManager;
	private SelectionManager selectionManager;

	public AddXMLInputSplitterMenuAction(URI activityType) {
		super(activityType);
	}

	@Override
	protected Action createAction() {
		AddXMLInputSplitterAction configAction = new AddXMLInputSplitterAction(findActivity(),
				null, editManager, selectionManager);
		Map<String, TypeDescriptor> descriptors;
		try {
			descriptors = configAction.getTypeDescriptors();
		} catch (UnknownOperationException | IOException | ParserConfigurationException
				| WSDLException | SAXException | JDOMException e) {
			return null;
		}
		if (!AbstractAddXMLSplitterAction.filterDescriptors(descriptors).isEmpty()) {
			configAction.putValue(Action.NAME, ADD_XML_INPUT_SPLITTER);
			addMenuDots(configAction);
			return configAction;
		} else {
			return null;
		}
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

}
