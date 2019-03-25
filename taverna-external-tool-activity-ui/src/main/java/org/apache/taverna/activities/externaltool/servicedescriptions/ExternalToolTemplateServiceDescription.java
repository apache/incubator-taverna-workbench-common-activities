/**
 * 
 */
package org.apache.taverna.activities.externaltool.servicedescriptions;
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

import java.net.URI;
import java.util.UUID;

import javax.swing.Icon;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.manager.InvocationGroupManager;
import org.apache.taverna.activities.externaltool.manager.impl.InvocationGroupManagerImpl;
import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

/**
 * @author alanrw
 *
 */
public class ExternalToolTemplateServiceDescription extends
		AbstractTemplateService<ExternalToolActivityConfigurationBean> {
	
	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/external-tool");
	
	private static final String EXTERNAL_TOOL = "Tool";
	
	private static InvocationGroupManager manager = InvocationGroupManagerImpl.getInstance();

	@Override
	public Class<? extends Activity<ExternalToolActivityConfigurationBean>> getActivityClass() {
		return ExternalToolActivity.class;
	}

	@Override
	public ExternalToolActivityConfigurationBean getActivityConfiguration() {
		ExternalToolActivityConfigurationBean result = new ExternalToolActivityConfigurationBean();
		result.setExternaltoolid(UUID.randomUUID().toString());
		result.setToolDescription(new ToolDescription(""));
		result.setMechanism(manager.getDefaultMechanism());
		return result;
	}

	@Override
	public Icon getIcon() {
		return ExternalToolActivityIcon.getExternalToolIcon();
	}
	
	@Override
	public String getDescription() {
		return "A service that allows tools to be used as services";	
	}
	
	@SuppressWarnings("unchecked")
	public static ServiceDescription getServiceDescription() {
		ExternalToolTemplateServiceDescription bts = new ExternalToolTemplateServiceDescription();
		return bts.templateService;
	}



	@Override
	public String getId() {
		return providerId.toString();
	}

	@Override
	public String getName() {
		return EXTERNAL_TOOL;
	}

}
