
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

import javax.swing.Action;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.actions.ExternalToolActivityConfigureAction;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

/**
 * This class adds the plugin configuration action to the context menu of every use case activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class ConfigureExternalToolMenuAction extends
		AbstractConfigureActivityMenuAction<ExternalToolActivity> {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;

	public ConfigureExternalToolMenuAction() {
		super(ExternalToolActivity.class);
	}

	@Override
	protected Action createAction() {
		ExternalToolActivityConfigureAction configAction = new ExternalToolActivityConfigureAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager);
		addMenuDots(configAction);
		return configAction;
	}

	public void setEditManager(EditManager editManager) {
		this.editManager = editManager;
	}

	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}

	public void setActivityIconManager(ActivityIconManager activityIconManager) {
		this.activityIconManager = activityIconManager;
	}

}
