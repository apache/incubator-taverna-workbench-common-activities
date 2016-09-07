package org.apache.taverna.activities.xpath.ui.contextualview;
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

import org.apache.taverna.activities.xpath.ui.config.XPathActivityConfigureAction;
import org.apache.taverna.activities.xpath.ui.servicedescription.XPathTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.activitytools.AbstractConfigureActivityMenuAction;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.services.ServiceRegistry;

/**
 * This action is responsible for enabling the contextual menu entry on processors that perform
 * XPathActivity'ies.
 * NB! As a side-effect this also enables the pop-up with for configuration of the processor when it
 * is added to the workflow from the Service Panel.
 *
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class ConfigureXPathActivityMenuAction extends AbstractConfigureActivityMenuAction {

	private EditManager editManager;
	private FileManager fileManager;
	private ActivityIconManager activityIconManager;
	private ServiceDescriptionRegistry serviceDescriptionRegistry;
	private ServiceRegistry serviceRegistry;

	public ConfigureXPathActivityMenuAction() {
		super(XPathTemplateService.ACTIVITY_TYPE);
	}

	@Override
	protected Action createAction() {
		XPathActivityConfigureAction configAction = new XPathActivityConfigureAction(
				findActivity(), getParentFrame(), editManager, fileManager, activityIconManager,
				serviceDescriptionRegistry, serviceRegistry);
		configAction.putValue(Action.NAME, "Configure XPath service");
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

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
