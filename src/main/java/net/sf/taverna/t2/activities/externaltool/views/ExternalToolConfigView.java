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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroup;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManagerListener;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerUI;
import net.sf.taverna.t2.activities.externaltool.manager.action.InvocationManagerAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;

import org.apache.log4j.Logger;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputStatic;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

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
	private ExternalToolActivityConfigurationBean configuration;
	
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
	
	private JComboBox invocationSelection;


	private JPanel invocationPanel;

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
		configuration =  activity.getConfiguration();
		setLayout(new GridBagLayout());
		initialise();
	}

	public void noteConfiguration() {			
			configuration = makeConfiguration();
	}

	private ExternalToolActivityConfigurationBean makeConfiguration() {
		ExternalToolActivityConfigurationBean newConfiguration = new ExternalToolActivityConfigurationBean();
		UseCaseDescription ucd = new UseCaseDescription(UUID.randomUUID().toString());
		newConfiguration.setUseCaseDescription(ucd);
		ucd.setUsecaseid(configuration.getUseCaseDescription().getUsecaseid());

		ucd.setUsecaseid(nameText.getText());
		ucd.setDescription(descriptionText.getText());
		ucd.setCommand(commandText.getText());
		synchronized(inputViewList) {
			ucd.getInputs().clear();
			ucd.getTags().clear();
		for (ExternalToolInputViewer viewer : inputViewList) {
			ScriptInputUser si = new ScriptInputUser();
			si.setBinary(viewer.isBinary());
			si.setList(viewer.isList());
			if (viewer.isList()) {
				si.setList(true);
				si.setConcatenate(true);
			}
			if (viewer.isReplace()) {
				si.setTag(viewer.getValue());
				si.setTempFile(false);
				si.setFile(false);
				ucd.getTags().add(si.getTag());
			} else if (viewer.isFile()) {
				si.setTag(viewer.getValue());
				si.setTempFile(false);
				si.setFile(true);
			} else if (viewer.isTempFile()) {
				si.setTag(viewer.getValue());
				si.setTempFile(true);
				si.setFile(true);
				ucd.getTags().add(si.getTag());
			}
			
			ucd.getInputs().put(viewer.getName(), si);
		}
		}
		synchronized(outputViewList) {
			ucd.getOutputs().clear();
			for (ExternalToolOutputViewer viewer : outputViewList) {
				ScriptOutput so = new ScriptOutput();
				so.setBinary(viewer.isBinary());
				so.setPath(viewer.getValue());
				ucd.getOutputs().put(viewer.getName(), so);
			}
			}
		synchronized(staticViewList) {
			ucd.getStatic_inputs().clear();
			for (ExternalToolStaticViewer viewer : staticViewList) {
				ScriptInputStatic sis = new ScriptInputStatic();
				if (viewer.isURL()) {
					sis.setUrl(viewer.getContent());
				} else {
					sis.setContent(viewer.getContent());
				}
				if (viewer.isReplace()) {
					sis.setTag(viewer.getValue());
					sis.setTempFile(false);
					sis.setFile(false);
					ucd.getTags().add(sis.getTag());
				} else {
					sis.setTag(viewer.getValue());
					sis.setTempFile(false);
					sis.setFile(true);
				}
				ucd.getStatic_inputs().add(sis);
			}
			}
        InvocationGroup group =  (InvocationGroup) invocationSelection.getSelectedItem();
		newConfiguration.setInvocationGroup(group);
		return newConfiguration;
	}
	
	private String stripDescription(UseCaseDescription desc) {
		String descString = desc.toString();
		return (descString.substring(descString.indexOf("[")));
	}

	public boolean isConfigurationChanged() {
		ExternalToolActivityConfigurationBean newBean = makeConfiguration();
		boolean basicUseCaseDescriptionChanged =
			!stripDescription(newBean.getUseCaseDescription()).equals(stripDescription(configuration.getUseCaseDescription()));
		logger.info(newBean.getUseCaseDescription().toString());
		logger.info(configuration.getUseCaseDescription().toString());
		if (basicUseCaseDescriptionChanged) {
			logger.info("Basic use case description changed");
		}
		InvocationGroup group =  (InvocationGroup) invocationSelection.getSelectedItem();
		boolean invocationChanged = !group.equals(configuration.getInvocationGroup());
		return basicUseCaseDescriptionChanged || invocationChanged;
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
		configuration =  activity.getConfiguration();
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
				.getUseCaseDescription().getInputs().entrySet()) {
			final ExternalToolInputViewer inputView  = new ExternalToolInputViewer(entry.getKey(), (ScriptInputUser) entry.getValue());
			inputViewList.add(inputView);
		}
		Collections.sort(inputViewList, new Comparator<ExternalToolInputViewer>(){

			@Override
			public int compare(ExternalToolInputViewer o1,
					ExternalToolInputViewer o2) {
				return o1.getName().compareTo(o2.getName());
			}});

		for (Entry<String,ScriptOutput> entry : configuration
				.getUseCaseDescription().getOutputs().entrySet()) {
			final ExternalToolOutputViewer outputView  = new ExternalToolOutputViewer(entry.getKey(), (ScriptOutput) entry.getValue());
			outputViewList .add(outputView);
		}
		Collections.sort(outputViewList, new Comparator<ExternalToolOutputViewer>(){

			@Override
			public int compare(ExternalToolOutputViewer o1,
					ExternalToolOutputViewer o2) {
				return o1.getName().compareTo(o2.getName());
			}});

		for (ScriptInputStatic sis : configuration
				.getUseCaseDescription().getStatic_inputs()) {
			final ExternalToolStaticViewer staticView  = new ExternalToolStaticViewer(sis);
			staticViewList.add(staticView);
		}
		Collections.sort(staticViewList, new Comparator<ExternalToolStaticViewer>(){

			@Override
			public int compare(ExternalToolStaticViewer o1,
					ExternalToolStaticViewer o2) {
				return o1.getContent().compareTo(o2.getContent());
			}});

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
		
			nameText.setText(configuration.getUseCaseDescription().getUsecaseid());
		scriptEditPanel.add(new JLabel("Name:"), gc);
		gc.gridx = 1;
		scriptEditPanel.add(nameText, gc);
		
			descriptionText.setText(configuration.getUseCaseDescription().getDescription());
		gc.gridx = 0;
		gc.gridy = 1;
		scriptEditPanel.add(new JLabel("Description:"), gc);
		gc.gridx = 1;
		scriptEditPanel.add(descriptionText, gc);
		
			commandText.setText(configuration.getUseCaseDescription().getCommand());
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
			addInputViewer(outerInputPanel, inputEditPanel, inputView);

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
					addInputViewer(outerInputPanel, inputEditPanel, newViewer);
					inputEditPanel.revalidate();
					inputEditPanel.repaint();
				}

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
	
	private void addInputViewer(final JPanel outerPanel, final JPanel panel, ExternalToolInputViewer viewer) {
		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputConstraint.gridy = inputGridy;
		inputConstraint.gridx = 0;
		
		final JTextField nameField = viewer.getNameField();
		panel.add(nameField, inputConstraint);
		inputConstraint.gridx++;

		final JComboBox depthSelector = viewer
				.getDepthSelector();
		panel.add(depthSelector, inputConstraint);
		inputConstraint.gridx++;
		
		final JComboBox actionSelector = viewer.getActionSelector();
		panel.add(actionSelector, inputConstraint);
		inputConstraint.gridx++;
		
		final JTextField valueField = viewer.getValueField();
		panel.add(valueField ,inputConstraint);
		inputConstraint.gridx++;
		
		final JComboBox typeSelector = viewer.getTypeSelector();
		panel.add(typeSelector, inputConstraint);
		inputConstraint.gridx++;
		
		final JButton removeButton = new JButton("Remove");
		final ExternalToolInputViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(inputViewList) {
					inputViewList.remove(v);
				}
				panel.remove(nameField);
				panel.remove(depthSelector);
				panel.remove(actionSelector);
				panel.remove(valueField);
				panel.remove(typeSelector);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, inputConstraint);
		inputConstraint.gridy = ++ inputGridy;
		panel.add(new JSeparator(), inputConstraint);
		inputGridy++;
		
	}

	private JPanel createInvocationPanel() {
		invocationPanel = new JPanel();
		populateInvocationPanel();
		InvocationGroupManager.getInstance().addListener(new InvocationGroupManagerListener() {

			@Override
			public void change() {
				populateInvocationPanel();
			}});
		
		return invocationPanel;
	}
	
	private void populateInvocationPanel() {
		invocationPanel.removeAll();
		invocationPanel.setLayout(new BorderLayout());
		invocationSelection = new JComboBox();

		for (InvocationGroup group : InvocationGroupManager.getInstance().getInvocationGroups()) {
			invocationSelection.addItem(group);
		}

		invocationPanel.add(invocationSelection, BorderLayout.NORTH);
		invocationSelection.setSelectedItem(configuration.getInvocationGroup());
		
		JButton manageInvocation = new JButton(new AbstractAction("Manage invocations"){

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationManagerUI ui = InvocationManagerUI.getInstance();
				ui.setVisible(true);
			}});
		invocationPanel.add(manageInvocation, BorderLayout.SOUTH);
		invocationPanel.repaint();
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
				addOutputViewer(outerOutputPanel, outputEditPanel, outputView);
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
						addOutputViewer(outerOutputPanel, outputEditPanel, newViewer);
						outputEditPanel.revalidate();
						outputEditPanel.repaint();
					}	
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
	
	private void addOutputViewer(final JPanel outerPanel, final JPanel panel, ExternalToolOutputViewer viewer) {
		final GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outputConstraint.weightx = 0.1;
		outputConstraint.fill = GridBagConstraints.BOTH;

		outputConstraint.gridy = outputGridy;
		outputConstraint.gridx = 0;
		final JTextField nameField = viewer.getNameField();
		panel.add(nameField, outputConstraint);
		
		outputConstraint.weightx = 0.0;
		outputConstraint.gridx++;

		final JTextField valueField = viewer.getValueField();
		panel.add(valueField ,outputConstraint);
		outputConstraint.gridx++;
		
		final JComboBox typeSelector = viewer.getTypeSelector();
		panel.add(typeSelector ,outputConstraint);
		
		outputConstraint.gridx++;
		final JButton removeButton = new JButton("Remove");
		final ExternalToolOutputViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(outputViewList) {
					outputViewList.remove(v);
				}
				panel.remove(nameField);
				panel.remove(valueField);
				panel.remove(typeSelector);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, outputConstraint);
		outputGridy++;
		
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
				addStaticViewer(outerStaticPanel, staticEditPanel, staticView);
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
						addStaticViewer(outerStaticPanel, staticEditPanel, newViewer);
						staticEditPanel.revalidate();
						staticEditPanel.repaint();
					}
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
	
	private void addStaticViewer(final JPanel outerPanel, final JPanel panel, ExternalToolStaticViewer viewer) {
		final GridBagConstraints staticConstraint = new GridBagConstraints();
		staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		staticConstraint.weightx = 0.1;
		staticConstraint.fill = GridBagConstraints.BOTH;

		staticConstraint.gridy = staticGridy;
		staticConstraint.gridx = 0;
		staticConstraint.weightx = 0.1;

		final JComboBox typeSelector = viewer.getTypeSelector();
		panel.add(typeSelector ,staticConstraint);
		
		staticConstraint.gridx++;
		
		final JTextArea contentField = viewer.getContentField();
		panel.add(contentField, staticConstraint);
		
		staticConstraint.gridx++;

		final JComboBox actionSelector = viewer.getActionSelector();
		panel.add(actionSelector, staticConstraint);
		
		staticConstraint.gridx++;
		final JTextField valueField = viewer.getValueField();
		panel.add(valueField ,staticConstraint);
		
		staticConstraint.gridx++;
		
		final JButton removeButton = new JButton("Remove");
		final ExternalToolStaticViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(staticViewList) {
					staticViewList.remove(v);
				}
				panel.remove(typeSelector);
				panel.remove(contentField);
				panel.remove(actionSelector);
				panel.remove(valueField);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, staticConstraint);
		staticGridy++;
		
	}


}
