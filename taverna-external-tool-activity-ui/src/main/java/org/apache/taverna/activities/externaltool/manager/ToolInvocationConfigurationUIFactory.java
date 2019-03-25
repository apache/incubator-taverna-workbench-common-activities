/**
 *
 */
package org.apache.taverna.activities.externaltool.manager;
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

import java.util.List;

import javax.swing.JPanel;

import org.apache.taverna.configuration.Configurable;
import org.apache.taverna.configuration.ConfigurationManager;
import org.apache.taverna.configuration.ConfigurationUIFactory;

import org.apache.taverna.activities.externaltool.configuration.ToolInvocationConfiguration;
import org.apache.taverna.activities.externaltool.manager.MechanismCreator;

/**
 * @author alanrw
 *
 */
public class ToolInvocationConfigurationUIFactory implements ConfigurationUIFactory {

	private List<MechanismCreator> mechanismCreators;
	private List<InvocationMechanismEditor<?>> invocationMechanismEditors;
	private ToolInvocationConfigurationPanel configPanel;
	private ConfigurationManager configurationManager;
	private InvocationGroupManager invocationGroupManager;

	@Override
	public boolean canHandle(String uuid) {
		return uuid.equals(getConfigurable().getUUID());
	}

	@Override
	public Configurable getConfigurable() {
		return new ToolInvocationConfiguration(configurationManager);
	}

	@Override
	public JPanel getConfigurationPanel() {
		if (configPanel == null) {
			configPanel = new ToolInvocationConfigurationPanel(mechanismCreators,
					invocationMechanismEditors, getInvocationGroupManager());
		}
		return configPanel;
	}

	public void setMechanismCreators(List<MechanismCreator> mechanismCreators) {
		this.mechanismCreators = mechanismCreators;
	}

	public void setInvocationMechanismEditors(
			List<InvocationMechanismEditor<?>> invocationMechanismEditors) {
		this.invocationMechanismEditors = invocationMechanismEditors;
	}

	public ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public void setConfigurationManager(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
	}

	public InvocationGroupManager getInvocationGroupManager() {
		return invocationGroupManager;
	}

	public void setInvocationGroupManager(InvocationGroupManager invocationGroupManager) {
		this.invocationGroupManager = invocationGroupManager;
	}

}
