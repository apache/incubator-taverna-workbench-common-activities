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

package org.apache.taverna.activities.beanshell.menu;

import java.net.URI;

import org.apache.taverna.activities.beanshell.actions.BeanshellActivityConfigurationAction;
import org.apache.taverna.activities.beanshell.servicedescriptions.BeanshellTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

import javax.swing.Action;

import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;

public class ConfigureBeanshellMenuAction extends AbstractConfigureActivityMenuAction {

	public static final URI LOCALWORKER_ACTIVITY = URI
			.create("http://ns.taverna.org.uk/2010/activity/localworker");

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ApplicationConfiguration applicationConfiguration;

	public ConfigureBeanshellMenuAction() {
		super(BeanshellTemplateService.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		Activity a = findActivity();
		Action result = null;
		if (!(a.getType().equals(LOCALWORKER_ACTIVITY))) {
			result = new BeanshellActivityConfigurationAction(findActivity(), getParentFrame(),
					editManager, fileManager, activityIconManager, serviceDescriptionRegistry,
					applicationConfiguration);
			result.putValue(Action.NAME, BeanshellActivityConfigurationAction.EDIT_BEANSHELL_SCRIPT);
			addMenuDots(result);
		}
		return result;
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

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}

}
