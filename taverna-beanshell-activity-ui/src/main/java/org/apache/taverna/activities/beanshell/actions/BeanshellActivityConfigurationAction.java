/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.beanshell.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import org.apache.taverna.activities.beanshell.views.BeanshellConfigurationPanel;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.ActivityConfigurationAction;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;

@SuppressWarnings("serial")
public class BeanshellActivityConfigurationAction extends ActivityConfigurationAction {

	public static final String EDIT_BEANSHELL_SCRIPT = "Edit beanshell script";
	private final EditManager editManager;
	private final FileManager fileManager;
	private final ApplicationConfiguration applicationConfiguration;

	public BeanshellActivityConfigurationAction(Activity activity, Frame owner,
			EditManager editManager, FileManager fileManager,
			ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry,
			ApplicationConfiguration applicationConfiguration) {
		super(activity, activityIconManager, serviceDescriptionRegistry);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.applicationConfiguration = applicationConfiguration;
		putValue(NAME, EDIT_BEANSHELL_SCRIPT);
	}

	public void actionPerformed(ActionEvent e) {
		ActivityConfigurationDialog currentDialog = ActivityConfigurationAction.getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		final BeanshellConfigurationPanel beanshellConfigView = new BeanshellConfigurationPanel(
				getActivity(), applicationConfiguration);
		final ActivityConfigurationDialog dialog = new ActivityConfigurationDialog(getActivity(),
				beanshellConfigView, editManager);

		ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);

	}

}
