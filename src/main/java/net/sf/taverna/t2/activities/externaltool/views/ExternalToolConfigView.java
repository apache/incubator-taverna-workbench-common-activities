/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.taverna.t2.activities.externaltool.AdHocExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.KnowARCConfigurationFactory;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;

import org.apache.log4j.Logger;
import org.jdom.Element;

import de.uni_luebeck.inb.knowarc.gui.KnowARCConfigurationDialog;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputStatic;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;

/**
 * Provides the configurable view for a {@link ExternalToolActivity} through it's
 * {@link ExternalToolActivityConfigurationBean}. Has 3 main tabs - Script, Ports &
 * Dependencies. The {@link #inputViewList} contains the
 * {@link ExternalToolInputViewer}s describing the input ports and
 * {@link #outputViewList} has the {@link ExternalToolOutputViewer}s
 * 
 * @author Ian Dunlop
 * @author Alex Nenadic
 * @author Alan R Williams
 * 
 */
@SuppressWarnings("serial")
public class ExternalToolConfigView extends ActivityConfigurationPanel<ExternalToolActivity, ExternalToolActivityConfigurationBean> {

	private static Logger logger = Logger.getLogger(ExternalToolConfigView.class);

	
	/** The activity which this view describes */
	protected ExternalToolActivity activity;
	
	/** The configuration bean used to configure the activity */
	private AdHocExternalToolActivityConfigurationBean configuration;
	
	private JTextField nameText = new JTextField(20);
	private JTextArea descriptionText = new JTextArea(6, 40);
	private JTextArea commandText = new JTextArea(6, 40);
		
	private JTabbedPane tabbedPane = null;


	private int inputGridy = 1;
	private List<ExternalToolInputViewer> inputViewList = new ArrayList<ExternalToolInputViewer>();

	private int outputGridy = 1;
	private List<ExternalToolOutputViewer> outputViewList = new ArrayList<ExternalToolOutputViewer>();
	
	private int staticGridy = 1;
	private List<ExternalToolStaticViewer> staticViewList = new ArrayList<ExternalToolStaticViewer>();
	
	/**
	 * An incremental name of newInputPort + this number is used to name new
	 * ports
	 */
	private int newInputPortNumber = 1;
	/**
	 * An incremental name of newOutputPort + this number is used to name new
	 * ports
	 */
	private int newOutputPortNumber = 1;

	private int newStaticNumber= 1;

	/**
	 * Stores the {@link ExternalToolActivity}, gets its
	 * {@link ExternalToolActivityConfigurationBean}, sets the layout and calls
	 * {@link #initialise()} to get the view going
	 * 
	 * @param activity
	 *            the {@link ExternalToolActivity} that the view is over
	 */
	public ExternalToolConfigView(ExternalToolActivity activity) {
		this.activity = activity;
		configuration = (AdHocExternalToolActivityConfigurationBean) activity.getConfiguration();
		setLayout(new GridBagLayout());
		initialise();
	}

	public void noteConfiguration() {			
			configuration = makeConfiguration();
	}

	private AdHocExternalToolActivityConfigurationBean makeConfiguration() {
		AdHocExternalToolActivityConfigurationBean newConfiguration = new AdHocExternalToolActivityConfigurationBean();
		Element newNode = new Element("program");
		newNode.setAttribute("name", nameText.getText());
		newNode.setAttribute("description", descriptionText.getText());
		newNode.setAttribute("command", commandText.getText());
		synchronized(inputViewList) {
		for (ExternalToolInputViewer viewer : inputViewList) {
			Element inputNode = new Element("input");
			inputNode.setAttribute("name", viewer.getName());
			if (viewer.isBinary()) {
				inputNode.setAttribute("binary", "true");
			}
			if (viewer.isList()) {
				inputNode.setAttribute("list", "true");
				inputNode.setAttribute("concatenate", "true");
			}
			if (viewer.isReplace()) {
				Element subElem = new Element("replace");
				subElem.setAttribute("tag", viewer.getValue());
				inputNode.addContent(subElem);
			} else if (viewer.isFile()) {
				Element subElem = new Element("file");
				subElem.setAttribute("path", viewer.getValue());
				inputNode.addContent(subElem);				
			} else if (viewer.isTempFile()) {
				Element subElem = new Element("tempfile");
				subElem.setAttribute("tag", viewer.getValue());
				inputNode.addContent(subElem);								
			}
			
			newNode.addContent(inputNode);
		}
		}
		synchronized(outputViewList) {
			for (ExternalToolOutputViewer viewer : outputViewList) {
				Element outputNode = new Element("output");
				outputNode.setAttribute("name", viewer.getName());
				if (viewer.isBinary()) {
					outputNode.setAttribute("binary", "true");
				}
				Element subElem = new Element("fromfile");
					subElem.setAttribute("path", viewer.getValue());
					outputNode.addContent(subElem);				
				newNode.addContent(outputNode);
			}
			}
		synchronized(staticViewList) {
			for (ExternalToolStaticViewer viewer : staticViewList) {
				Element staticNode = new Element("static");
				Element contentNode = new Element("content");
				if (viewer.isURL()) {
					contentNode.setAttribute("url", viewer.getContent());
				} else {
					contentNode.addContent(viewer.getContent());
				}
				if (viewer.isReplace()) {
					Element subElem = new Element("replace");
					subElem.setAttribute("tag", viewer.getValue());
					staticNode.addContent(subElem);
				} else {
					Element subElem = new Element("file");
					subElem.setAttribute("path", viewer.getValue());
					staticNode.addContent(subElem);				
				}
				staticNode.addContent(contentNode);
				
				newNode.addContent(staticNode);
			}
			}
		newConfiguration.setProgramNode(newNode);
		return newConfiguration;
	}

	public boolean isConfigurationChanged() {
		if (configuration.getProgramNode() == null) {
			return true;
		}
		return !makeConfiguration().getProgramText().equals(configuration.getProgramText());
	}

	/**
	 * Adds a {@link JButton} which handles the reconfiguring of the
	 * {@link ExternalToolActivity} through the altered
	 * {@link ExternalToolActivityConfigurationBean}. Sets up the initial tabs -
	 * Script (also sets the initial value), Ports & Dependencies and their
	 * initial values through {@link #setDependencies()},
	 * {@link #getPortPanel()}
	 */
	private void initialise() {
		CSH
				.setHelpIDString(
						this,
						"net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ExternalToolConfigView");
		configuration = (AdHocExternalToolActivityConfigurationBean) activity.getConfiguration();
		setBorder(javax.swing.BorderFactory.createTitledBorder(null, null,
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Lucida Grande", 1, 12)));

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Script", createScriptPanel());

		inputViewList = new ArrayList<ExternalToolInputViewer> ();
		outputViewList = new ArrayList<ExternalToolOutputViewer> ();
		staticViewList = new ArrayList<ExternalToolStaticViewer> ();

		for (Entry<String,ScriptInput> entry : configuration
				.getUseCaseDescription().inputs.entrySet()) {
			final ExternalToolInputViewer inputView  = new ExternalToolInputViewer(entry.getKey(), (ScriptInputUser) entry.getValue());
			inputViewList .add(inputView);
		}

		for (Entry<String,ScriptOutput> entry : configuration
				.getUseCaseDescription().outputs.entrySet()) {
			final ExternalToolOutputViewer outputView  = new ExternalToolOutputViewer(entry.getKey(), (ScriptOutput) entry.getValue());
			outputViewList .add(outputView);
		}

		for (ScriptInputStatic sis : configuration
				.getUseCaseDescription().static_inputs) {
			final ExternalToolStaticViewer staticView  = new ExternalToolStaticViewer(sis);
			staticViewList.add(staticView);
		}

		tabbedPane.addTab("Input ports", createInputPanel());
		tabbedPane.addTab("Output ports", createOutputPanel());
		tabbedPane.addTab("Statics", createStaticPanel());
		tabbedPane.addTab("Invocation", createInvocationPanel());
		
		GridBagConstraints outerConstraint = new GridBagConstraints();
		outerConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outerConstraint.gridx = 0;
		outerConstraint.gridy = 0;

		outerConstraint.fill = GridBagConstraints.BOTH;
		outerConstraint.weighty = 0.1;
		outerConstraint.weightx = 0.1;
		add(tabbedPane, outerConstraint);

		setPreferredSize(new Dimension(600,500));
		this.validate();
	}

	private JPanel createScriptPanel() {
		JPanel scriptEditPanel = new JPanel(new BorderLayout());
		scriptEditPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		
		if (configuration.getProgramNode() != null) {
			nameText.setText(configuration.getUseCaseDescription().usecaseid);
		}
		scriptEditPanel.add(new JLabel("Name:"), gc);
		gc.gridx = 1;
		scriptEditPanel.add(nameText, gc);
		
		if (configuration.getProgramNode() != null) {
			descriptionText.setText(configuration.getUseCaseDescription().description);
		}
		gc.gridx = 0;
		gc.gridy = 1;
		scriptEditPanel.add(new JLabel("Description:"), gc);
		gc.gridx = 1;
		scriptEditPanel.add(descriptionText, gc);
		
		if (configuration.getProgramNode() != null) {
			commandText.setText(configuration.getUseCaseDescription().getCommand());
		}
		gc.gridx = 0;
		gc.gridy = 2;
		scriptEditPanel.add(new JLabel("Command:"), gc);
		gc.gridx = 1;
		scriptEditPanel.add(commandText, gc);
		
		return scriptEditPanel;

		
	}
	
	private JPanel createInputPanel() {
		 final JPanel outerInputPanel = new JPanel();

		final JPanel inputEditPanel = new JPanel(new GridBagLayout());
//		inputEditPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
//				.createEtchedBorder(), "Inputs"));

		final GridBagConstraints inputConstraint = new GridBagConstraints();
//		inputConstraint.insets = new Insets(5,5,5,5);
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputEditPanel.add(new JLabel("Name"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("Type"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("Action"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("Value"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("Type"), inputConstraint);


		inputConstraint.gridx = 0;
		synchronized(inputViewList) {
		for (ExternalToolInputViewer inputView : inputViewList) {
			// FIXME refactor this into a method
			inputConstraint.gridy = inputGridy;
			inputConstraint.gridx = 0;
			final JTextField nameField = inputView.getNameField();
			inputConstraint.weightx = 0.1;
			inputEditPanel.add(nameField, inputConstraint);
			
			inputConstraint.weightx = 0.0;
			inputConstraint.gridx++;

			final JComboBox depthSelector = inputView
					.getDepthSelector();
			inputEditPanel.add(depthSelector, inputConstraint);
			
			inputConstraint.gridx++;
			final JComboBox actionSelector = inputView.getActionSelector();
			inputEditPanel.add(actionSelector, inputConstraint);
			
			inputConstraint.gridx++;
			final JTextField valueField = inputView.getValueField();
			inputEditPanel.add(valueField ,inputConstraint);
			
			inputConstraint.gridx++;
			final JComboBox typeSelector = inputView.getTypeSelector();
			inputEditPanel.add(typeSelector, inputConstraint);
			
			inputConstraint.gridx++;
			final JButton removeButton = new JButton("Remove");
			final ExternalToolInputViewer v = inputView;
			removeButton.addActionListener(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					synchronized(inputViewList) {
						inputViewList.remove(v);
					}
					inputEditPanel.remove(nameField);
					inputEditPanel.remove(depthSelector);
					inputEditPanel.remove(actionSelector);
					inputEditPanel.remove(valueField);
					inputEditPanel.remove(typeSelector);
					inputEditPanel.remove(removeButton);
					inputEditPanel.revalidate();
					inputEditPanel.repaint();
					outerInputPanel.revalidate();
					outerInputPanel.repaint();
				}

			});
			inputEditPanel.add(removeButton, inputConstraint);
			inputGridy++;
		}
		}
		outerInputPanel.setLayout(new GridBagLayout());
		GridBagConstraints outerPanelConstraint = new GridBagConstraints();
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 0;
		outerPanelConstraint.weightx = 0.1;
		outerPanelConstraint.weighty = 0.1;
		outerPanelConstraint.fill = GridBagConstraints.BOTH;
		outerInputPanel.add(new JScrollPane(inputEditPanel),
				outerPanelConstraint);
		outerPanelConstraint.weighty = 0;
		JButton addInputPortButton = new JButton(new AbstractAction() {
			// FIXME refactor this into a method
			public void actionPerformed(ActionEvent e) {

				String name2 = "in" + newInputPortNumber++;
				boolean nameExists = true;
				while (nameExists == true) {
					nameExists = inputPortNameExists(name2, activity
							.getInputPorts());
					if (nameExists) {
						name2 = "in" + newInputPortNumber++;
					}
				}
				
				ExternalToolInputViewer newViewer = new ExternalToolInputViewer(name2);
				synchronized(inputViewList) {
					inputViewList.add(newViewer);
				}
				// FIXME refactor this into a method
				inputConstraint.gridy = inputGridy;
				inputConstraint.gridx = 0;
				final JTextField nameField = newViewer.getNameField();
				inputConstraint.weightx = 0.1;
				inputEditPanel.add(nameField, inputConstraint);
				
				inputConstraint.weightx = 0.0;
				inputConstraint.gridx++;

				final JComboBox depthSelector = newViewer
						.getDepthSelector();
				inputEditPanel.add(depthSelector, inputConstraint);
				
				inputConstraint.gridx++;
				final JComboBox actionSelector = newViewer.getActionSelector();
				inputEditPanel.add(actionSelector, inputConstraint);
				
				inputConstraint.gridx++;
				final JTextField valueField = newViewer.getValueField();
				inputEditPanel.add(valueField ,inputConstraint);
				
				inputConstraint.gridx++;
				final JComboBox typeSelector = newViewer.getTypeSelector();
				inputEditPanel.add(typeSelector, inputConstraint);
				
				inputConstraint.gridx++;
				final JButton removeButton = new JButton("Remove");
				final ExternalToolInputViewer v = newViewer;
				removeButton.addActionListener(new AbstractAction() {

					public void actionPerformed(ActionEvent e) {
						synchronized(inputViewList) {
							inputViewList.remove(v);
						}
						inputEditPanel.remove(nameField);
						inputEditPanel.remove(depthSelector);
						inputEditPanel.remove(actionSelector);
						inputEditPanel.remove(valueField);
						inputEditPanel.remove(typeSelector);
						inputEditPanel.remove(removeButton);
						inputEditPanel.revalidate();
						inputEditPanel.repaint();
						outerInputPanel.revalidate();
						outerInputPanel.repaint();
					}

				});
				inputEditPanel.add(removeButton, inputConstraint);
				inputEditPanel.revalidate();

				inputGridy++;
			}

		});
		addInputPortButton.setText("Add Port");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());

		JPanel filler = new JPanel();
		outerPanelConstraint.weightx = 0.1;
		outerPanelConstraint.weighty = 0;
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 0;

		buttonPanel.add(filler, outerPanelConstraint);

		outerPanelConstraint.weightx = 0;
		outerPanelConstraint.weighty = 0;
		outerPanelConstraint.gridx = 1;
		outerPanelConstraint.gridy = 0;

		buttonPanel.add(addInputPortButton, outerPanelConstraint);

		outerPanelConstraint.weightx = 0;
		outerPanelConstraint.weighty = 0;
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 1;
		outerPanelConstraint.fill = GridBagConstraints.BOTH;
		outerInputPanel.add(buttonPanel, outerPanelConstraint);

		return outerInputPanel;
	}

	private JPanel createInvocationPanel() {
		final JPanel invocationPanel = new JPanel();
		invocationPanel.add(new JButton(new AbstractAction("Configure invocation") {

			@Override
			public void actionPerformed(ActionEvent e) {
				new KnowARCConfigurationDialog(null, false, KnowARCConfigurationFactory.getConfiguration()).setVisible(true);
				}}));
		return invocationPanel;
	}
	
	@Override
	public ExternalToolActivityConfigurationBean getConfiguration() {
		return configuration;
	}

	@Override
	public void refreshConfiguration() {
		int visibleTab = -1;
		if (tabbedPane != null) {
			visibleTab = tabbedPane.getSelectedIndex();
		}
		this.removeAll();
		initialise();
		if (visibleTab != -1) {
			tabbedPane.setSelectedIndex(visibleTab);
		}
	}

	@Override
	public boolean checkValues() {
		boolean result = true;
		return result;
	}
	
	/**
	 * Check the proposed port name against the set of input ports that the
	 * activity has
	 * 
	 * @param name
	 * @param set
	 * @return
	 */
	private boolean inputPortNameExists(String name, Set<ActivityInputPort> set) {
		for (Port port : set) {
			if (name.equals(port.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check the proposed port name against the set of output ports that the
	 * activity has
	 * 
	 * @param name
	 * @param set
	 * @return
	 */
	private boolean outputPortNameExists(String name, Set<OutputPort> set) {
		for (Port port : set) {
			if (name.equals(port.getName())) {
				return true;
			}
		}
		return false;
	}

	private JPanel createOutputPanel() {
		final JPanel outerOutputPanel = new JPanel();
			final JPanel outputEditPanel = new JPanel(new GridBagLayout());
	
			final GridBagConstraints outputConstraint = new GridBagConstraints();
			outputConstraint.insets = new Insets(5,5,5,5);
			outputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			outputConstraint.gridx = 0;
			outputConstraint.gridy = 0;
			outputConstraint.weightx = 0.1;
			outputConstraint.fill = GridBagConstraints.BOTH;
	
			outputEditPanel.add(new JLabel("Name"), outputConstraint);
			outputConstraint.gridx++;
			outputEditPanel.add(new JLabel("From file"), outputConstraint);	
			outputConstraint.gridx++;
			outputEditPanel.add(new JLabel("Type"), outputConstraint);	
	
			outputConstraint.gridx = 0;
			synchronized(outputViewList) {
			for (ExternalToolOutputViewer outputView : outputViewList) {
				// FIXME refactor this into a method
				outputConstraint.gridy = outputGridy;
				outputConstraint.gridx = 0;
				final JTextField nameField = outputView.getNameField();
				outputConstraint.weightx = 0.1;
				outputEditPanel.add(nameField, outputConstraint);
				
				outputConstraint.weightx = 0.0;
				outputConstraint.gridx++;
	
				final JTextField valueField = outputView.getValueField();
				outputEditPanel.add(valueField ,outputConstraint);
				outputConstraint.gridx++;
				
				final JComboBox typeSelector = outputView.getTypeSelector();
				outputEditPanel.add(typeSelector ,outputConstraint);
				
				outputConstraint.gridx++;
				final JButton removeButton = new JButton("Remove");
				final ExternalToolOutputViewer v = outputView;
				removeButton.addActionListener(new AbstractAction() {
	
					public void actionPerformed(ActionEvent e) {
						synchronized(outputViewList) {
							outputViewList.remove(v);
						}
						outputEditPanel.remove(nameField);
						outputEditPanel.remove(valueField);
						outputEditPanel.remove(typeSelector);
						outputEditPanel.remove(removeButton);
						outputEditPanel.revalidate();
						outputEditPanel.repaint();
						outerOutputPanel.revalidate();
						outerOutputPanel.repaint();
					}
	
				});
				outputEditPanel.add(removeButton, outputConstraint);
				outputGridy++;
			}
			}
			outerOutputPanel.setLayout(new GridBagLayout());
			GridBagConstraints outerPanelConstraint = new GridBagConstraints();
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 0;
			outerPanelConstraint.weightx = 0.1;
			outerPanelConstraint.weighty = 0.1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerOutputPanel.add(new JScrollPane(outputEditPanel),
					outerPanelConstraint);
			outerPanelConstraint.weighty = 0;
			JButton addOutputPortButton = new JButton(new AbstractAction() {
				// FIXME refactor this into a method
				public void actionPerformed(ActionEvent e) {
	
					String name2 = "out" + newOutputPortNumber++;
					boolean nameExists = true;
					while (nameExists == true) {
						nameExists = outputPortNameExists(name2, activity
								.getOutputPorts());
						if (nameExists) {
							name2 = "out" + newOutputPortNumber++;
						}
					}
					
					ExternalToolOutputViewer newViewer = new ExternalToolOutputViewer(name2);
					synchronized(outputViewList) {
						outputViewList.add(newViewer);
					}
					outputConstraint.gridy = outputGridy;
					outputConstraint.gridx = 0;
					final JTextField nameField = newViewer.getNameField();
					outputConstraint.weightx = 0.1;
					outputEditPanel.add(nameField, outputConstraint);
					
					outputConstraint.weightx = 0.0;
					outputConstraint.gridx++;
		
					final JTextField valueField = newViewer.getValueField();
					outputEditPanel.add(valueField ,outputConstraint);
					
					outputConstraint.gridx++;
					final JComboBox typeSelector = newViewer.getTypeSelector();
					outputEditPanel.add(typeSelector ,outputConstraint);
					
					outputConstraint.gridx++;
					final JButton removeButton = new JButton("Remove");
					final ExternalToolOutputViewer v = newViewer;
					removeButton.addActionListener(new AbstractAction() {
		
						public void actionPerformed(ActionEvent e) {
							synchronized(outputViewList) {
								outputViewList.remove(v);
							}
							outputEditPanel.remove(nameField);
							outputEditPanel.remove(valueField);
							outputEditPanel.remove(typeSelector);
							outputEditPanel.remove(removeButton);
							outputEditPanel.revalidate();
							outputEditPanel.repaint();
							outerOutputPanel.revalidate();
							outerOutputPanel.repaint();
						}
		
					});
					outputEditPanel.add(removeButton, outputConstraint);
					outputEditPanel.revalidate();
	
					outputGridy++;
				}
	
			});
			addOutputPortButton.setText("Add Port");
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
	
			JPanel filler = new JPanel();
			outerPanelConstraint.weightx = 0.1;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 0;
	
			buttonPanel.add(filler, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 1;
			outerPanelConstraint.gridy = 0;
	
			buttonPanel.add(addOutputPortButton, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerOutputPanel.add(buttonPanel, outerPanelConstraint);
	
			return outerOutputPanel;
		}

	private JPanel createStaticPanel() {
		final JPanel outerStaticPanel = new JPanel();
			final JPanel staticEditPanel = new JPanel(new GridBagLayout());
	
			final GridBagConstraints staticConstraint = new GridBagConstraints();
			staticConstraint.insets = new Insets(5,5,5,5);
			staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			staticConstraint.gridx = 0;
			staticConstraint.gridy = 0;
			staticConstraint.weightx = 0.1;
			staticConstraint.fill = GridBagConstraints.BOTH;
	
			staticEditPanel.add(new JLabel("Type"), staticConstraint);	
			staticConstraint.gridx++;
			staticEditPanel.add(new JLabel("Content"), staticConstraint);	
			staticConstraint.gridx++;
			staticEditPanel.add(new JLabel("Action"), staticConstraint);
			staticConstraint.gridx++;
			staticEditPanel.add(new JLabel("Value"), staticConstraint);
	
			staticConstraint.gridx = 0;
			synchronized(staticViewList) {
			for (ExternalToolStaticViewer staticView : staticViewList) {
				// FIXME refactor this into a method
				staticConstraint.gridy = staticGridy;
				staticConstraint.gridx = 0;
				staticConstraint.weightx = 0.1;

				final JComboBox typeSelector = staticView.getTypeSelector();
				staticEditPanel.add(typeSelector ,staticConstraint);
				
				staticConstraint.gridx++;
				
				final JTextArea contentField = staticView.getContentField();
				staticEditPanel.add(contentField, staticConstraint);
				
				staticConstraint.gridx++;

				final JComboBox actionSelector = staticView.getActionSelector();
				staticEditPanel.add(actionSelector, staticConstraint);
				
				staticConstraint.gridx++;
				final JTextField valueField = staticView.getValueField();
				staticEditPanel.add(valueField ,staticConstraint);
				
				staticConstraint.gridx++;
				
				final JButton removeButton = new JButton("Remove");
				final ExternalToolStaticViewer v = staticView;
				removeButton.addActionListener(new AbstractAction() {
	
					public void actionPerformed(ActionEvent e) {
						synchronized(staticViewList) {
							staticViewList.remove(v);
						}
						staticEditPanel.remove(typeSelector);
						staticEditPanel.remove(contentField);
						staticEditPanel.remove(actionSelector);
						staticEditPanel.remove(valueField);
						staticEditPanel.remove(removeButton);
						staticEditPanel.revalidate();
						staticEditPanel.repaint();
						outerStaticPanel.revalidate();
						outerStaticPanel.repaint();
					}
	
				});
				staticEditPanel.add(removeButton, staticConstraint);
				staticGridy++;
			}
			}
			outerStaticPanel.setLayout(new GridBagLayout());
			GridBagConstraints outerPanelConstraint = new GridBagConstraints();
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 0;
			outerPanelConstraint.weightx = 0.1;
			outerPanelConstraint.weighty = 0.1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerStaticPanel.add(new JScrollPane(staticEditPanel),
					outerPanelConstraint);
			outerPanelConstraint.weighty = 0;
			JButton addstaticPortButton = new JButton(new AbstractAction() {
				// FIXME refactor this into a method
				public void actionPerformed(ActionEvent e) {
	
					ExternalToolStaticViewer newViewer = new ExternalToolStaticViewer();
					synchronized(staticViewList) {
						staticViewList.add(newViewer);
					}
					staticConstraint.gridy = staticGridy;
					staticConstraint.gridx = 0;
					staticConstraint.weightx = 0.1;
					final JComboBox typeSelector = newViewer.getTypeSelector();
					staticEditPanel.add(typeSelector ,staticConstraint);
					
					staticConstraint.gridx++;
					
					final JTextArea contentField = newViewer.getContentField();
					staticEditPanel.add(contentField, staticConstraint);
					
					staticConstraint.gridx++;
					final JComboBox actionSelector = newViewer.getActionSelector();
					staticEditPanel.add(actionSelector, staticConstraint);
					
					staticConstraint.gridx++;
					final JTextField valueField = newViewer.getValueField();
					staticEditPanel.add(valueField ,staticConstraint);
					
					staticConstraint.gridx++;
					final JButton removeButton = new JButton("Remove");
					final ExternalToolStaticViewer v = newViewer;
					removeButton.addActionListener(new AbstractAction() {
		
						public void actionPerformed(ActionEvent e) {
							synchronized(staticViewList) {
								staticViewList.remove(v);
							}
							staticEditPanel.remove(typeSelector);
							staticEditPanel.remove(contentField);
							staticEditPanel.remove(actionSelector);
							staticEditPanel.remove(valueField);
							staticEditPanel.remove(removeButton);
							staticEditPanel.revalidate();
							staticEditPanel.repaint();
							outerStaticPanel.revalidate();
							outerStaticPanel.repaint();
						}
		
					});
					staticEditPanel.add(removeButton, staticConstraint);
					staticGridy++;
					staticEditPanel.revalidate();
	
					staticGridy++;
				}
	
			});
			addstaticPortButton.setText("Add Static");
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
	
			JPanel filler = new JPanel();
			outerPanelConstraint.weightx = 0.1;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 0;
	
			buttonPanel.add(filler, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 1;
			outerPanelConstraint.gridy = 0;
	
			buttonPanel.add(addstaticPortButton, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerStaticPanel.add(buttonPanel, outerPanelConstraint);
	
			return outerStaticPanel;
		}


}
