/*******************************************************************************
 * Copyright (C) 2012 The University of Manchester
 *
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.activities.beanshell.views;

import java.awt.Component;
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

import net.sf.taverna.t2.lang.ui.EditorKeySetUtil;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityPortConfiguration;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.DependencyConfigurationPanel;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ListConfigurationComponent;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.MultiPageActivityConfigurationPanel;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ScriptConfigurationComponent;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ValidatingTextField;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ValidatingTextGroup;
import uk.org.taverna.configuration.app.ApplicationConfiguration;
import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;

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
