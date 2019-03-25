package org.apache.taverna.activities.externaltool.actions;
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

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.views.ExternalToolConfigView;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.ActivityConfigurationAction;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

/**
 * This class implements an ActivityConfigurationAction to configure the ExternalToolActivity
 * plugin. The configuration action is called "Configure ToolDesc invocation" and is implemented in
 * the KnowARCConfigurationDialog inside the knowarc-tooldescs maven artifact.
 *
 * @author Hajo Nils Krabbenhoeft
 */
@SuppressWarnings("serial")
public class ExternalToolActivityConfigureAction extends
		ActivityConfigurationAction {// <ExternalToolActivity, ExternalToolActivityConfigurationBean> {

	private final Frame owner;
	private final EditManager editManager;
	private final FileManager fileManager;

	public ExternalToolActivityConfigureAction(ExternalToolActivity activity, Frame owner,
			EditManager editManager, FileManager fileManager, ActivityIconManager activityIconManager) {
		super(activity, activityIconManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		putValue(Action.NAME, "Configure tool invocation");
		this.owner = owner;
	}

	public void actionPerformed(ActionEvent e) {
		/*
		 * if (getActivity().getConfiguration() instanceof
		 * RegisteredExternalToolActivityConfigurationBean) { new KnowARCConfigurationDialog(owner,
		 * false, KnowARCConfigurationFactory.getConfiguration()).setVisible(true); } else
		 */{
			ActivityConfigurationDialog currentDialog = ActivityConfigurationAction
					.getDialog(getActivity());
			if (currentDialog != null) {
				currentDialog.toFront();
				return;
			}
			final ExternalToolConfigView externalToolConfigView = new ExternalToolConfigView(
					(ExternalToolActivity) getActivity());
			final ActivityConfigurationDialog<ExternalToolActivity, ExternalToolActivityConfigurationBean> dialog = new ActivityConfigurationDialog<ExternalToolActivity, ExternalToolActivityConfigurationBean>(
					getActivity(), externalToolConfigView, editManager, fileManager);

			ActivityConfigurationAction.setDialog(getActivity(), dialog, fileManager);
		}
	}
}
