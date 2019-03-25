
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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.manager.InvocationGroupManager;
import org.apache.taverna.activities.externaltool.manager.impl.InvocationGroupManagerImpl;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.activities.externaltool.desc.ToolDescription;
import static org.apache.taverna.activities.externaltool.servicedescriptions.ExternalToolServiceDescription.TOOL_ACTIVITY_URI;

/**
 * ExternalToolServiceDescription stores the repository URL and the use case id so
 * that it can create an ExternalToolActivityConfigurationBean
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceDescription extends ServiceDescription {

	private static Logger logger = Logger
	.getLogger(ExternalToolServiceDescription.class);

	
	private InvocationGroupManager invocationGroupManager;

	private String repositoryUrl;
	private String externaltoolid;
	private ToolDescription toolDescription;

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getExternaltoolid() {
		return externaltoolid;
	}

	public void setExternaltoolid(String externaltoolid) {
		this.externaltoolid = externaltoolid;
	}

	public Icon getIcon() {
		if (toolDescription != null) {
			String icon_url = toolDescription.getIcon_url();
			if ((icon_url != null) && !icon_url.isEmpty() && !icon_url.endsWith(".ico"))
				try {
					ImageIcon result = new ImageIcon(new URL(icon_url));
					if ((result != null) && (result.getIconHeight() != 0) && (result.getIconWidth() != 0)){
						return result;
					}
				} catch (MalformedURLException e) {
					logger.error("Problematic URL" + icon_url, e);
				}
		}
		return ExternalToolActivityIcon.getExternalToolIcon();
	}

	public Class<? extends Activity<ExternalToolActivityConfigurationBean>> getActivityClass() {
		return ExternalToolActivity.class;
	}

	public ExternalToolActivityConfigurationBean getActivityConfiguration() {
		ExternalToolActivityConfigurationBean bean = new ExternalToolActivityConfigurationBean();
		bean.setRepositoryUrl(repositoryUrl);
		bean.setExternaltoolid(externaltoolid);
		bean.setToolDescription(toolDescription);
		bean.setMechanism(getInvocationGroupManager().getDefaultMechanism());

		return bean;
	}

	public String getName() {
		return externaltoolid;
	}

	@SuppressWarnings("unchecked")
	public List<? extends Comparable> getPath() {
		List<String> result = new ArrayList<String>();
		result.add("Tools decribed @ " + repositoryUrl);
		String group = toolDescription.getGroup();
		if ((group != null) && !group.isEmpty()) {
			String[] groups = group.split(":");
			for (String g : groups) {
				result.add(g);
			}
		}
		return result;
	}

	protected List<Object> getIdentifyingData() {
		// we require use cases inside one XML file to have unique IDs, which
		// means every externaltool is uniquely identified by its repository URL and
		// its use case ID.
		return Arrays.<Object> asList(repositoryUrl, externaltoolid);
	}
	
	public String getDescription() {
		if (toolDescription != null) {
			String description = toolDescription.getDescription();
			if (description == null) {
				return "";
			}
			return description;
		}
		return "";
	}

	public void setToolDescription(ToolDescription tooldesc) {
		this.toolDescription = tooldesc;
	}

	@Override
	public URI getActivityType() {
		return TOOL_ACTIVITY_URI;
	}

	public InvocationGroupManager getInvocationGroupManager() {
		return invocationGroupManager;
	}

	public void setInvocationGroupManager(InvocationGroupManager invocationGroupManager) {
		this.invocationGroupManager = invocationGroupManager;
	}

}
