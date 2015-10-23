/*******************************************************************************
 * Copyright (C) 2007-2009 The University of Manchester
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
package org.apache.taverna.activities.xpath.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.taverna.activities.xpath.ui.servicedescription.XPathTemplateService;
import org.apache.taverna.ui.menu.AbstractMenuAction;
import org.apache.taverna.ui.menu.DesignOnlyAction;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.services.ServiceRegistry;

/**
 * An action to add a REST activity + a wrapping processor to the workflow.
 *
 * @author Alex Nenadic
 * @author alanrw
 */
@SuppressWarnings("serial")
public class AddXPathTemplateMenuAction extends AbstractMenuAction {

	private static final String ADD_XPATH = "XPath";

	private static final URI INSERT = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#insert");

	private static final URI ADD_XPATH_URI = URI
			.create("http://taverna.sf.net/2008/t2workbench/menu#graphMenuAddXPath");

	private EditManager editManager;

	private MenuManager menuManager;

	private SelectionManager selectionManager;

	private ActivityIconManager activityIconManager;

	private ServiceRegistry serviceRegistry;

	public AddXPathTemplateMenuAction() {
		super(INSERT, 1000, ADD_XPATH_URI);
	}

	@Override
	protected Action createAction() {
		return new AddXPathMenuAction();
	}

	protected class AddXPathMenuAction extends AbstractAction implements DesignOnlyAction {
		AddXPathMenuAction() {
			super();
			putValue(SMALL_ICON,
					activityIconManager.iconForActivity(XPathTemplateService.ACTIVITY_TYPE));
			putValue(NAME, ADD_XPATH);
			putValue(SHORT_DESCRIPTION, "XPath service");
			putValue(
					Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.SHIFT_DOWN_MASK
							| InputEvent.ALT_DOWN_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			WorkflowView.importServiceDescription(XPathTemplateService.getServiceDescription(),
					false, editManager, menuManager, selectionManager, serviceRegistry);
		}
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setMenuManager(MenuManager menuManager) {
		this.menuManager = menuManager;
	}

	public void setSelectionManager(SelectionManager selectionManager) {
		this.selectionManager = selectionManager;
	}

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
