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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.taverna.t2.activities.beanshell.servicedescriptions.BeanshellTemplateService;
import net.sf.taverna.t2.lang.ui.EditorKeySetUtil;
import net.sf.taverna.t2.lang.ui.FileTools;
import net.sf.taverna.t2.lang.ui.KeywordDocument;
import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;
import net.sf.taverna.t2.lang.ui.LinePainter;
import net.sf.taverna.t2.lang.ui.NoWrapEditorKit;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ListConfigurationComponent;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.MultiPageActivityConfigurationPanel;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ScriptConfigurationComponent;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ValidatingTextField;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ValidatingTextGroup;

import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.port.ActivityPort;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.api.port.OutputActivityPort;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.UnexpectedPropertyException;

/**
 * Component for configuring a Beanshell activity.
 *
 * @author David Withers
 */
public class BeanshellConfigurationPanel extends MultiPageActivityConfigurationPanel {

	private static final Logger logger = Logger.getLogger(BeanshellConfigurationPanel.class);
	private static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	private final ServiceDescription serviceDescription;

	private ScriptConfigurationComponent scriptConfigurationComponent;
	private List<PropertyResource> inputPortDefinitions, outputPortDefinitions;
	private ValidatingTextGroup inputTextGroup, outputTextGroup;

	public BeanshellConfigurationPanel(Activity activity, ServiceDescription serviceDescription) {
		super(activity);
		this.serviceDescription = serviceDescription;
		initialise();
	}

	@Override
	protected void initialise() {
		inputPortDefinitions = getPortDefinitions(activity.getInputPorts());
		outputPortDefinitions = getPortDefinitions(activity.getOutputPorts());
		removeAllPages();
		addPage("Script", createScriptEditPanel());
		addPage("Input ports", createInputPanel());
		addPage("Output ports", createOutputPanel());
		// addPage("Dependencies", createDependenciesPanel());
	}

	@Override
	public void noteConfiguration() {
		PropertyResource propertyResource = serviceDescription.getActivityConfiguration().getPropertyResource();
		setPropertyResource(propertyResource);
		setProperty("script", scriptConfigurationComponent.getScript());
		for (PropertyResource inputPortDefinition : inputPortDefinitions) {
			propertyResource.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#inputPortDefinition"), inputPortDefinition);
		}
		for (PropertyResource outputPortDefinition : outputPortDefinitions) {
			propertyResource.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#outputPortDefinition"), outputPortDefinition);
		}
	}

	@Override
	public boolean checkValues() {
		return true;
	}

	private Component createScriptEditPanel() {
		Set<String> keywords = EditorKeySetUtil.loadKeySet(getClass().getResourceAsStream("keys.txt"));
		scriptConfigurationComponent = new ScriptConfigurationComponent(getProperty("script"), keywords, "Beanshell", ".bsh");
		return scriptConfigurationComponent;
	}

	private Component createInputPanel() {
		inputTextGroup = new ValidatingTextGroup();
		ListConfigurationComponent<PropertyResource> inputPanel = new ListConfigurationComponent<PropertyResource>("Input Port", inputPortDefinitions) {
			@Override
			protected Component createItemComponent(PropertyResource portDefinition) {
				return new PortComponent(portDefinition, inputTextGroup);
			}

			@Override
			protected PropertyResource createDefaultItem() {
				PropertyResource portDefinition = new PropertyResource();
				portDefinition.setTypeURI(Scufl2Tools.PORT_DEFINITION.resolve("#InputPortDefinition"));
				portDefinition.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#name"), new PropertyLiteral("in"));
				portDefinition.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#depth"), new PropertyLiteral(0));
				return portDefinition;
			}
		};
		return inputPanel;
	}

	private Component createOutputPanel() {
		outputTextGroup = new ValidatingTextGroup();
		ListConfigurationComponent<PropertyResource> inputPanel = new ListConfigurationComponent<PropertyResource>("Output Port", outputPortDefinitions) {
			@Override
			protected Component createItemComponent(PropertyResource portDefinition) {
				return new PortComponent(portDefinition, outputTextGroup);
			}

			@Override
			protected PropertyResource createDefaultItem() {
				PropertyResource portDefinition = new PropertyResource();
				portDefinition.setTypeURI(Scufl2Tools.PORT_DEFINITION.resolve("#OutputPortDefinition"));
				portDefinition.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#name"), new PropertyLiteral("out"));
				portDefinition.addProperty(Scufl2Tools.PORT_DEFINITION.resolve("#depth"), new PropertyLiteral(0));
				return portDefinition;
			}
		};
		return inputPanel;
	}

	private Component createDependenciesPanel() {
		// TODO Auto-generated method stub
		return null;
	}

	class PortComponent extends JPanel {

		private ValidatingTextField nameField;
		private SpinnerNumberModel depthModel;
		private PropertyLiteral nameProperty;
		private PropertyLiteral depthProperty;
		private final ValidatingTextGroup validatingTextGroup;

		public PortComponent(PropertyResource portDefinition, ValidatingTextGroup validatingTextGroup) {
			this.validatingTextGroup = validatingTextGroup;
			try {
				SortedSet<PropertyLiteral> properties = portDefinition.getPropertiesAsLiterals(Scufl2Tools.PORT_DEFINITION.resolve("#name"));
				for (PropertyLiteral propertyLiteral : properties) {
					nameProperty = propertyLiteral;
					break;
				}
			} catch (UnexpectedPropertyException e) {
				logger.warn(e);
			}
			try {
				SortedSet<PropertyLiteral> properties = portDefinition.getPropertiesAsLiterals(Scufl2Tools.PORT_DEFINITION.resolve("#depth"));
				for (PropertyLiteral propertyLiteral : properties) {
					depthProperty = propertyLiteral;
					break;
				}
			} catch (UnexpectedPropertyException e) {
				logger.warn(e);
			}

			nameField = new ValidatingTextField(nameProperty.getLiteralValue());
			nameField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					nameProperty.setLiteralValue(nameField.getText());
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					nameProperty.setLiteralValue(nameField.getText());
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					nameProperty.setLiteralValue(nameField.getText());
				}
			});
			validatingTextGroup.addValidTextComponent(nameField);
			depthModel = new SpinnerNumberModel(depthProperty.getLiteralValueAsInt(), 0, 100, 1);
			depthModel.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					depthProperty.setLiteralValue(String.valueOf(depthModel.getNumber().intValue()));
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

	public static void main(String[] args) {
		final WorkflowBundle workflowBundle = new WorkflowBundle();
		Profile profile = new Profile();
		profile.setParent(workflowBundle);

		final Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#ConfigType"));
		configuration.setParent(profile);

		final Activity activity = new Activity();
		activity.setParent(profile);
		activity.setConfigurableType(ACTIVITY_TYPE);
		configuration.setConfigures(activity);

		ActivityPort activityPort = new InputActivityPort(activity, "in1");
		activityPort.setDepth(0);

		activityPort = new InputActivityPort(activity, "in2");
		activityPort.setDepth(0);

		activityPort = new OutputActivityPort(activity, "out1");
		activityPort.setDepth(0);

		final BeanshellConfigurationPanel panel = new BeanshellConfigurationPanel(activity, BeanshellTemplateService.getServiceDescription());

		JPanel testPanel = new JPanel(new BorderLayout());
		testPanel.add(panel, BorderLayout.CENTER);
		testPanel.add(new JButton(new AbstractAction("Test") {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.noteConfiguration();
				ActivityConfigurationDialog.configureActivityStatic(workflowBundle, panel.activity, panel.getPropertyResource(), null);
			}
		}), BorderLayout.SOUTH);

		JFrame frame = new JFrame();
		frame.setSize(400, 400);
		frame.add(testPanel);
		frame.setVisible(true);
	}
}
