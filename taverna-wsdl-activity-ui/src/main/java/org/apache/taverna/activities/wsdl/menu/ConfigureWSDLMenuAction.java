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

package org.apache.taverna.activities.wsdl.menu;

import javax.swing.Action;

import org.apache.taverna.activities.wsdl.actions.WSDLActivityConfigureAction;
import org.apache.taverna.activities.wsdl.servicedescriptions.WSDLServiceDescription;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.ui.menu.ContextualMenuComponent;
import org.apache.taverna.ui.menu.MenuComponent;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;

public class ConfigureWSDLMenuAction extends AbstractConfigureActivityMenuAction implements
		MenuComponent, ContextualMenuComponent {

	private EditManager editManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private CredentialManager credentialManager;
	private FileManager fileManager;

	public ConfigureWSDLMenuAction() {
		super(WSDLServiceDescription.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		WSDLActivityConfigureAction configAction = new WSDLActivityConfigureAction(findActivity(),
				getParentFrame(), editManager, fileManager, activityIconManager,
				serviceDescriptionRegistry, credentialManager);
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

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry serviceDescriptionRegistry) {
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	public void setCredentialManager(CredentialManager credentialManager) {
		this.credentialManager = credentialManager;
	}

}
