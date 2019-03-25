package org.apache.taverna.activities.externaltool.menu;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.ui.menu.AbstractMenuAction;
import org.apache.taverna.ui.menu.DesignOnlyAction;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.workbench.views.graph.menu.InsertMenu;
import static org.apache.taverna.activities.externaltool.servicedescriptions.ExternalToolServiceDescription.TOOL_ACTIVITY_URI;
/**
 * An action to add a externaltool activity + a wrapping processor to the workflow.
 *
 * @author Alex Nenadic
 * @author alanrw
 *
 */
@SuppressWarnings("serial")
public class AddExternalToolMenuAction extends AbstractMenuAction {
	
	private static final String ADD_EXTERNAL_TOOL = "Tool";

	private static final URI ADD_EXTERNAL_TOOL_URI = URI
	.create("http://taverna.sf.net/2008/t2workbench/menu#graphMenuAddExternalTool");

	private static Logger logger = Logger
			.getLogger(AddExternalToolMenuAction.class);

	private EditManager editManager;
	private MenuManager menuManager;
	private SelectionManager selectionManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	private ActivityIconManager activityIconManager;

	public AddExternalToolMenuAction() {
		super(InsertMenu.INSERT, 900, ADD_EXTERNAL_TOOL_URI);
	}

	@Override
	protected Action createAction() {

		return new AddExternalToolAction();
	}

	protected class AddExternalToolAction extends AbstractAction implements DesignOnlyAction {


		AddExternalToolAction () {
			super ();
			putValue(SMALL_ICON, activityIconManager.iconForActivity(
					TOOL_ACTIVITY_URI));
			putValue(NAME, ADD_EXTERNAL_TOOL);
			putValue(SHORT_DESCRIPTION, "Tool");
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			
			getServiceDescriptionRegistry().getServiceDescription(TOOL_ACTIVITY_URI);			
			WorkflowView.importServiceDescription(getServiceDescriptionRegistry()
					.getServiceDescription(TOOL_ACTIVITY_URI), false,
					editManager, menuManager, selectionManager, getServiceRegistry());
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

	public ServiceDescriptionRegistry getServiceDescriptionRegistry() {
		return serviceDescriptionRegistry;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
