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

package org.apache.taverna.activities.beanshell.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.taverna.lang.ui.EditorKeySetUtil;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityPortConfiguration;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.DependencyConfigurationPanel;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ListConfigurationComponent;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.MultiPageActivityConfigurationPanel;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ScriptConfigurationComponent;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ValidatingTextField;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ValidatingTextGroup;
import uk.org.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Component for configuring a Beanshell activity.
 *
 * @author David Withers
 */
@SuppressWarnings("serial")
public class BeanshellConfigurationPanel extends MultiPageActivityConfigurationPanel {

	private ScriptConfigurationComponent scriptConfigurationComponent;
	private ValidatingTextGroup inputTextGroup, outputTextGroup;
	private DependencyConfigurationPanel dependencyConfigurationPanel;
	private File libDir;

	public BeanshellConfigurationPanel(Activity activity,
			ApplicationConfiguration applicationConfiguration) {
		super(activity);
		libDir = new File(applicationConfiguration.getApplicationHomeDir(), "lib");
		if (!libDir.exists()) {
			libDir.mkdir();
		}
		initialise();
	}

	@Override
	protected void initialise() {
		super.initialise();
		removeAllPages();
		addPage("Script", createScriptEditPanel());
		addPage("Input ports", createInputPanel());
		addPage("Output ports", createOutputPanel());
		addPage("Dependencies", createDependenciesPanel());
		setPreferredSize(new Dimension(600, 500));
	}

	@Override
	public void noteConfiguration() {
		setProperty("script", scriptConfigurationComponent.getScript());
		setProperty("classLoaderSharing", dependencyConfigurationPanel.getClassLoaderSharing());
		List<String> localDependencies = dependencyConfigurationPanel.getLocalDependencies();
		if (localDependencies == null || localDependencies.isEmpty()) {
			getJson().remove("localDependency");
		} else {
			ArrayNode localDependenciesArray = getJson().arrayNode();
			for (String localDependency : localDependencies) {
				localDependenciesArray.add(localDependency);
			}
			getJson().put("localDependency", localDependenciesArray);
		}
	}

	@Override
	public boolean checkValues() {
		return true;
	}

	private Component createScriptEditPanel() {
		Set<String> keywords = EditorKeySetUtil.loadKeySet(getClass().getResourceAsStream(
				"keys.txt"));
		Set<String> ports = new HashSet<>();
		for (InputActivityPort ip : getActivity().getInputPorts()) {
			ports.add(ip.getName());
		}
		for (OutputActivityPort op : getActivity().getOutputPorts()) {
			ports.add(op.getName());
		}
		scriptConfigurationComponent = new ScriptConfigurationComponent(getProperty("script"),
				keywords, ports, "Beanshell", ".bsh");
		return scriptConfigurationComponent;
	}

	private Component createInputPanel() {
		inputTextGroup = new ValidatingTextGroup();
		ListConfigurationComponent<ActivityPortConfiguration> inputPanel = new ListConfigurationComponent<ActivityPortConfiguration>(
				"Input Port", getInputPorts()) {
			@Override
			protected Component createItemComponent(ActivityPortConfiguration port) {
				return new PortComponent(port, inputTextGroup);
			}

			@Override
			protected ActivityPortConfiguration createDefaultItem() {
				return new ActivityPortConfiguration("in", 0);
			}
		};
		return inputPanel;
	}

	private Component createOutputPanel() {
		outputTextGroup = new ValidatingTextGroup();
		ListConfigurationComponent<ActivityPortConfiguration> inputPanel = new ListConfigurationComponent<ActivityPortConfiguration>(
				"Output Port", getOutputPorts()) {
			@Override
			protected Component createItemComponent(ActivityPortConfiguration port) {
				return new PortComponent(port, outputTextGroup);
			}

			@Override
			protected ActivityPortConfiguration createDefaultItem() {
				return new ActivityPortConfiguration("out", 0);
			}
		};
		return inputPanel;
	}

	private Component createDependenciesPanel() {
		String classLoaderSharing = getProperty("classLoaderSharing");
		List<String> localDependencies = new ArrayList<>();
		if (getJson().has("localDependency")) {
			for (JsonNode localDependency : getJson().get("localDependency")) {
				localDependencies.add(localDependency.textValue());
			}
		}
		dependencyConfigurationPanel = new DependencyConfigurationPanel(classLoaderSharing,
				localDependencies, libDir);
		return dependencyConfigurationPanel;
	}

	class PortComponent extends JPanel {

		private ValidatingTextField nameField;
		private SpinnerNumberModel depthModel;
		private final ValidatingTextGroup validatingTextGroup;

		public PortComponent(final ActivityPortConfiguration portConfiguration,
				ValidatingTextGroup validatingTextGroup) {
			this.validatingTextGroup = validatingTextGroup;

			nameField = new ValidatingTextField(portConfiguration.getName());
			nameField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					portConfiguration.setName(nameField.getText());
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					portConfiguration.setName(nameField.getText());
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					portConfiguration.setName(nameField.getText());
				}
			});
			validatingTextGroup.addValidTextComponent(nameField);
			depthModel = new SpinnerNumberModel(portConfiguration.getDepth(), 0, 100, 1);
			depthModel.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					portConfiguration.setDepth(depthModel.getNumber().intValue());
				}
			});

			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.WEST;
			add(new JLabel("Name"), c);
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			add(nameField, c);
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			add(new JLabel("Depth"), c);
			add(new JSpinner(depthModel), c);

		}

		public void removeNotify() {
			validatingTextGroup.removeTextComponent(nameField);
		}

	}

}
