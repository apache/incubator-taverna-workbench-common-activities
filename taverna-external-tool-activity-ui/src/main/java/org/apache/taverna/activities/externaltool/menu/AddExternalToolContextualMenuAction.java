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
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.servicedescriptions.ExternalToolTemplateServiceDescription;
import org.apache.taverna.ui.menu.AbstractContextualMenuAction;
import org.apache.taverna.ui.menu.MenuManager;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.selection.SelectionManager;
import org.apache.taverna.workbench.ui.workflowview.WorkflowView;
import org.apache.taverna.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

/**
 * An action to add an external tool + a wrapping processor to the workflow.
 *
 * @author Alex Nenadic
 * @author Alan Williamns
 *
 */
@SuppressWarnings("serial")
public class AddExternalToolContextualMenuAction extends AbstractContextualMenuAction {

	private static final String ADD_EXTERNAL_TOOL = "Tool";

	private static final URI insertSection = URI
			.create("http://taverna.sf.net/2009/contextMenu/insert");

	private static Logger logger = Logger.getLogger(AddExternalToolMenuAction.class);

	private EditManager editManager;

	private MenuManager menuManager;

	private SelectionManager selectionManager;

	private ActivityIconManager activityIconManager;

	public AddExternalToolContextualMenuAction() {
		super(insertSection, 900);
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && getContextualSelection().getSelection() instanceof Dataflow;
	}

	@Override
	protected Action createAction() {

		return new AddExternalToolAction();
	}

	protected class AddExternalToolAction extends AbstractAction {
		AddExternalToolAction() {
			super(ADD_EXTERNAL_TOOL, activityIconManager.iconForActivity(
					new ExternalToolActivity()));
		}

		public void actionPerformed(ActionEvent e) {
			WorkflowView.importServiceDescription(
					ExternalToolTemplateServiceDescription.getServiceDescription(), false,
					editManager, menuManager, selectionManager);
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

}
