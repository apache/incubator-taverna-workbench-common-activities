package org.apache.taverna.activities.xpath.ui.menu;
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
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.taverna.activities.xpath.ui.servicedescription.XPathTemplateService;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.core.Workflow;

/**
 * An action to add a REST activity + a wrapping processor to the workflow.
 *
 * @author Alex Nenadic
 */
@SuppressWarnings("serial")
public class AddXPathTemplateAction extends AbstractContextualMenuAction {

	private static final String ADD_XPATH = "XPath";

	private static final URI insertSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/insert");

	private EditManager editManager;

	private MenuManager menuManager;

	private SelectionManager selectionManager;

	private ActivityIconManager activityIconManager;

	private ServiceRegistry serviceRegistry;

	public AddXPathTemplateAction() {
		super(insertSection, 1000);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && getContextualSelection().getSelection() instanceof Workflow;
	}

	@Override
	protected Action createAction() {

		return new AddXPathAction();
	}

	protected class AddXPathAction extends AbstractAction {
		AddXPathAction() {
			super(ADD_XPATH, activityIconManager
					.iconForActivity(XPathTemplateService.ACTIVITY_TYPE));
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
