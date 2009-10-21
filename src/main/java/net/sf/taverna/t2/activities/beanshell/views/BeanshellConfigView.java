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
package net.sf.taverna.t2.activities.beanshell.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.dependencyactivity.AbstractAsynchronousDependencyActivity;
import net.sf.taverna.t2.activities.dependencyactivity.AbstractAsynchronousDependencyActivity.ClassLoaderSharing;
import net.sf.taverna.t2.reference.ExternalReferenceSPI;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;
import net.sf.taverna.t2.workflowmodel.OutputPort;
import net.sf.taverna.t2.workflowmodel.Port;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityInputPort;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

import org.apache.log4j.Logger;
import org.syntax.jedit.JEditTextArea;
import org.syntax.jedit.tokenmarker.JavaTokenMarker;

/**
 * Provides the configurable view for a {@link BeanshellActivity} through it's
 * {@link BeanshellActivityConfigurationBean}. Has 3 main tabs - Script, Ports &
 * Dependencies. The {@link #inputViewList} contains the
 * {@link BeanshellInputViewer}s describing the input ports and
 * {@link #outputViewList} has the {@link BeanshellOutputViewer}s
 * 
 * @author Ian Dunlop
 * @author Alex Nenadic
 * @author Alan R Williams
 * 
 */
@SuppressWarnings("serial")
public class BeanshellConfigView extends ActivityConfigurationPanel<BeanshellActivity, BeanshellActivityConfigurationBean> {

	private static Logger logger = Logger.getLogger(BeanshellConfigView.class);

	
	/** The activity which this view describes */
	protected BeanshellActivity activity;
	
	/** The configuration bean used to configure the activity */
	private BeanshellActivityConfigurationBean configuration;
	
	///////// Beanshell properties that can be configured ////////
	/** The beanshell script */
	private JEditTextArea scriptText;
	
	/** A list of views over the input ports */
	private List<BeanshellInputViewer> inputViewList;
	
	/** A list of views over the output ports */
	private List<BeanshellOutputViewer> outputViewList;
	
	/** Classloader sharing policy */
	private ClassLoaderSharing classLoaderSharing;

	/** A list of local dependencies (JAR files) this activity depends on */ 
	private LinkedHashSet<String> localDependencies = new LinkedHashSet<String>();
	
	///////// End of beanshell properties that can be configured //
	
	/**
	 * Holds the state of the OK button in case a parent view wants to know
	 * whether the configuration is finished
	 */
	private ActionListener buttonClicked;
	
	/** Remembers where the next input should be placed in the view */
	private int inputGridy;
	
	/**
	 * An incremental name of newInputPort + this number is used to name new
	 * ports
	 */
	private int newInputPortNumber = 0;
	
	/**
	 * An incremental name of newOutputPort + this number is used to name new
	 * ports
	 */
	private int newOutputPortNumber = 0;
	
	/** Remembers where the next output should be placed in the view */
	private int outputGridy;
	
	/** Parent panel for the outputs */
	private JPanel outerOutputPanel;
	
	/** Have output ports been changed */
	private boolean outputsChanged = false;
	
	/** Parent panel for the inputs */
	private JPanel outerInputPanel;
	
	/** Have input ports been changed */
	private boolean inputsChanged = false;

	private JButton button;

	private boolean configChanged = false;

	private JTabbedPane tabbedPane = null;


	private JTabbedPane ports;

	/**
	 * Stores the {@link BeanshellActivity}, gets its
	 * {@link BeanshellActivityConfigurationBean}, sets the layout and calls
	 * {@link #initialise()} to get the view going
	 * 
	 * @param activity
	 *            the {@link BeanshellActivity} that the view is over
	 */
	public BeanshellConfigView(BeanshellActivity activity) {
		this.activity = activity;
		setLayout(new GridBagLayout());
		initialise();
	}

	public void noteConfiguration() {
			// Set the new configuration
			List<ActivityInputPortDefinitionBean> inputBeanList = new ArrayList<ActivityInputPortDefinitionBean>();
			for (BeanshellInputViewer inputView : inputViewList) {
				ActivityInputPortDefinitionBean activityInputPortDefinitionBean = new ActivityInputPortDefinitionBean();
				activityInputPortDefinitionBean
						.setHandledReferenceSchemes(inputView.getBean()
								.getHandledReferenceSchemes());
				activityInputPortDefinitionBean.setMimeTypes(inputView
						.getBean().getMimeTypes());
				activityInputPortDefinitionBean
						.setTranslatedElementType(inputView.getBean()
								.getTranslatedElementType());
				activityInputPortDefinitionBean
						.setAllowsLiteralValues((Boolean) inputView
								.getLiteralSelector().getSelectedItem());
				activityInputPortDefinitionBean
						.setDepth((Integer) inputView.getDepthSpinner()
								.getValue());
				activityInputPortDefinitionBean.setName(inputView
						.getNameField().getText());
				inputBeanList.add(activityInputPortDefinitionBean);
			}

			List<ActivityOutputPortDefinitionBean> outputBeanList = new ArrayList<ActivityOutputPortDefinitionBean>();
			for (BeanshellOutputViewer outputView : outputViewList) {
				ActivityOutputPortDefinitionBean activityOutputPortDefinitionBean = new ActivityOutputPortDefinitionBean();
				activityOutputPortDefinitionBean
						.setDepth((Integer) outputView.getDepthSpinner()
								.getValue());
				
//				activityOutputPortDefinitionBean
//						.setGranularDepth((Integer) outputView
//								.getGranularDepthSpinner().getValue());
				
				// NOTE: Granular depth must match output depth because we return
				// the full lists right away
				activityOutputPortDefinitionBean
						.setGranularDepth(activityOutputPortDefinitionBean
								.getDepth());
				
				
				activityOutputPortDefinitionBean.setName(outputView
						.getNameField().getText());
				activityOutputPortDefinitionBean.setMimeTypes(new ArrayList<String>());

				outputBeanList.add(activityOutputPortDefinitionBean);
			}
			
			BeanshellActivityConfigurationBean newConfiguration =
				(BeanshellActivityConfigurationBean) cloneBean (configuration);
			newConfiguration.setScript(scriptText
					.getText());
			newConfiguration
					.setInputPortDefinitions(inputBeanList);
			newConfiguration
					.setOutputPortDefinitions(outputBeanList);
			
			newConfiguration.setClassLoaderSharing(classLoaderSharing);
			newConfiguration.setLocalDependencies(localDependencies);
			newConfiguration.setArtifactDependencies(new LinkedHashSet<BasicArtifact>());
			configuration = newConfiguration;
			inputsChanged = false;
			outputsChanged = false;
	}

	public boolean isConfigurationChanged() {
		String newScriptText = scriptText.getText();
		String configText = configuration.getScript();
		return !((!inputsChanged)
		&& (!outputsChanged)
		&& scriptText.getText().equals(configuration.getScript()) 
		&& classLoaderSharing.equals(configuration.getClassLoaderSharing())
		&& localDependencies.equals(configuration.getLocalDependencies()));
	}

	/**
	 * Adds a {@link JButton} which handles the reconfiguring of the
	 * {@link BeanshellActivity} through the altered
	 * {@link BeanshellActivityConfigurationBean}. Sets up the initial tabs -
	 * Script (also sets the initial value), Ports & Dependencies and their
	 * initial values through {@link #setDependencies()},
	 * {@link #getPortPanel()}
	 */
	private void initialise() {
		CSH
				.setHelpIDString(
						this,
						"net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.BeanshellConfigView");
		configuration = activity.getConfiguration();
		inputViewList = new ArrayList<BeanshellInputViewer>();
		outputViewList = new ArrayList<BeanshellOutputViewer>();
		classLoaderSharing = configuration.getClassLoaderSharing();
		localDependencies.addAll(configuration.getLocalDependencies());
		setBorder(javax.swing.BorderFactory.createTitledBorder(null, null,
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Lucida Grande", 1, 12)));
		final BeanshellActivityConfigurationBean configBean = activity
				.getConfiguration();

		JPanel scriptEditPanel = new JPanel(new BorderLayout());

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Script", scriptEditPanel);
		tabbedPane.addTab("Ports", getPortPanel());

		tabbedPane.addTab("Dependencies", getDependenciesPanel());

		GridBagConstraints outerConstraint = new GridBagConstraints();
		outerConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outerConstraint.gridx = 0;
		outerConstraint.gridy = 0;

		outerConstraint.fill = GridBagConstraints.BOTH;
		outerConstraint.weighty = 0.1;
		outerConstraint.weightx = 0.1;
		add(tabbedPane, outerConstraint);

		scriptText = new JEditTextArea();
		scriptText.setText(configBean.getScript());
		scriptText.setTokenMarker(new JavaTokenMarker());
		scriptText.setCaretPosition(0);
		scriptText.setPreferredSize(new Dimension(0, 0));
		scriptEditPanel.add(new JScrollPane(scriptText), BorderLayout.CENTER);
		setPreferredSize(new Dimension(500,500));
		inputsChanged = false;
		outputsChanged = false;
		this.validate();
	}

/**
 * Returns a panel where local and artifact dependencies and classloading policy can be set.
 */
	private JPanel getDependenciesPanel(){
		
		JPanel dependenciesPanel = new JPanel();
		dependenciesPanel.setLayout(new BoxLayout(dependenciesPanel, BoxLayout.PAGE_AXIS));
		
		// Create panel with classloading options
		JPanel classloadingPanel = new ClassloadingPanel();
		// Create panel for selecting jar files
		JPanel jarFilesPanel = new JarFilesPanel();
        
		dependenciesPanel.add(classloadingPanel);
		dependenciesPanel.add(Box.createRigidArea(new Dimension(0,10)));
		dependenciesPanel.add(jarFilesPanel);
		dependenciesPanel.add(Box.createRigidArea(new Dimension(0,10)));

		return dependenciesPanel;
	}
	
	// Panel containing classloading options
	private class ClassloadingPanel extends JPanel {
		
		// Classloading option 'workflow'
		private static final String WORKFLOW = "Shared for whole workflow";
		// Classloading option 'system'
		private static final String SYSTEM = "System classloader";

		// Combobox with classloading options
		private JComboBox jcbClassloadingOption;
		// Classloading option descriptions
		private HashMap<String, String> classloadingDescriptions;
		// JLabel with classloading option description
		private JLabel jlClassloadingDescription;

		// Panel containing a list of possible classloading options which users can select from
		private ClassloadingPanel(){
			super(new GridBagLayout());
			jcbClassloadingOption = new JComboBox(
					new String[] {WORKFLOW,SYSTEM});
			// Set the current classlaoding option based on the configuration bean
			if (configuration.getClassLoaderSharing() == (AbstractAsynchronousDependencyActivity.ClassLoaderSharing.workflow)){
				jcbClassloadingOption.setSelectedItem(WORKFLOW);
			}
			else if (configuration.getClassLoaderSharing() == (AbstractAsynchronousDependencyActivity.ClassLoaderSharing.system)){
				jcbClassloadingOption.setSelectedItem(SYSTEM);
			}
			
			jcbClassloadingOption.addActionListener(new ActionListener(){
				// Fires up when combobox selection changes
				public void actionPerformed(ActionEvent e) {
					jlClassloadingDescription.setText(classloadingDescriptions
							.get(((JComboBox) e.getSource()).getSelectedItem()));
					if (((JComboBox) e.getSource()).getSelectedItem().equals(
							WORKFLOW)) {
						classLoaderSharing = AbstractAsynchronousDependencyActivity.ClassLoaderSharing.workflow;
					}
					else if (((JComboBox) e.getSource()).getSelectedItem().equals(
							SYSTEM)) {
						classLoaderSharing = AbstractAsynchronousDependencyActivity.ClassLoaderSharing.system;
					}
				}
			});
			//jcbClassloadingOption.setEnabled(false);
			
			classloadingDescriptions = new HashMap<String, String>();
			classloadingDescriptions.put(WORKFLOW, "<html><small>"
					+ "Classes are shared across the whole workflow (with any processor<br>"
					+ "also selecting this option), but are reinitialised for each workflow run.<br>"
					+ "This might be needed if a processor passes objects to another, or <br>"
					+ "state is shared within static members of loaded classes."
					+ "</small></html>");
			classloadingDescriptions.put(SYSTEM, "<html><small><p>"
					+ "The (global) system classloader is used, any dependencies defined here are<br>"
					+ "made available globally on the first run. Note that if you are NOT using<br>"
					+ "the defaulf Taverna BootstrapClassLoader, any settings here will be disregarded."
					+ "</p><p>"
					+ "This is mainly useful if you are using JNI-based libraries. Note that <br>"
					+ "for JNI you also have to specify <code>-Djava.library.path</code> and <br>"
					+ "probably your operating system's dynamic library search path<br>"
					+ "<code>LD_LIBRARY_PATH</code> / <code>DYLD_LIBRARY_PATH</code> / <code>PATH</code> </p></small></html>");
			
			// Set the current classlaoding description based on the item selected in the combobox
			jlClassloadingDescription = new JLabel(classloadingDescriptions
					.get(jcbClassloadingOption.getSelectedItem()));
			
			// Add components to the ClassloadingPanel
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.insets = new Insets(10,0,0,0);
			add(new JLabel("Classloader persistence"), c);
			c.insets = new Insets(0,0,0,0);
			add(jcbClassloadingOption, c);
			c.insets = new Insets(0,30,0,0);
			add(jlClassloadingDescription, c);
		}
	}
	
	// Panel for users to add local JAR dependencies (contains a list of jar files which users can select from)
	private class JarFilesPanel extends JPanel {
		private JLabel warning =
			new JLabel(
				"<html><center<font color='red'>"
					+ "Warning: Depending on local libraries makes this workflow<br>"
					+ "difficult or impossible to run for other users. Try depending<br>"
					+ "on artifacts from a public repository if possible.</font></center></html>");

		private JarFilesPanel() {
			super();
			setMinimumSize(new Dimension(400, 150));
			setLayout(new BorderLayout());
			setBorder(new EmptyBorder(0,10,0,10));
			
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.PAGE_AXIS));
			JLabel label = new JLabel("Local JAR files");
			if (!BeanshellActivity.libDir.exists())
				BeanshellActivity.libDir.mkdir();
			JLabel libLabel = new JLabel("<html><small>" + BeanshellActivity.libDir.getAbsolutePath()
					+ "</small></html>");
			labelPanel.add(label);
			labelPanel.add(libLabel);

			add(labelPanel, BorderLayout.NORTH);
			add(new JScrollPane(jarFiles(),
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

			warning.setVisible(false);
			// We'll skip the warning until we actually have support 
			// for artifacts
			//add(warning);
			updateWarning();
		}

		private void updateWarning() {
			// Show warning if there is any local dependencies
			warning.setVisible(!configuration.getLocalDependencies().isEmpty());
		}

		public JPanel jarFiles() {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

			// Make so it's there so the user can add stuff to it
			// List of all jar files in the lib directory
			List<String> jarFiles =
				Arrays.asList(BeanshellActivity.libDir.list(new BeanshellActivity.FileExtFilter(".jar")));
			// We also add the list of jars that may have been configured sometime before
			// but are now not present in the lib directory for some reason
			Set<String> missingLocalDeps =
				new HashSet<String>(configuration.getLocalDependencies());
			missingLocalDeps.removeAll(jarFiles);
			// jarFiles and missingLocalDeps now contain two sets of files that do not intersect
			List<String> jarFilesList = new ArrayList<String>();
			// Put them all together
			jarFilesList.addAll(jarFiles);
			jarFilesList.addAll(missingLocalDeps);		
			Collections.sort(jarFilesList);
						
			if (jarFilesList.isEmpty()) {
				panel.add(new JLabel("<html><small>To depend on a JAR file, "
					+ "copy it to the above-mentioned folder.</small></html>"));
				return panel;
			}
			
			for (String jarFile : jarFilesList) {
				JCheckBox checkBox = new JCheckBox(jarFile);
				// Has it already been selected in some previous configuring?
				checkBox.setSelected(configuration.getLocalDependencies().contains(jarFile));
				checkBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						JCheckBox box = (JCheckBox) e.getSource();
						if (e.getStateChange() == ItemEvent.SELECTED) {
							localDependencies.add(box.getText());
						} else if (e.getStateChange() == ItemEvent.DESELECTED) {
							localDependencies.remove(box.getText());
						}
						updateWarning();
					}
				});
				panel.add(checkBox);
				// The jar may not be in the lib directory, so warn the user
				if (!new File(BeanshellActivity.libDir, jarFile).exists()) {
					checkBox.setForeground(Color.RED);
					checkBox.setText(checkBox.getText() + " (missing file!)");
				}
			}
			return panel;
		}
	}
	

	/**
	 * Creates a {@link JTabbedPane} with the Output and Input ports
	 * 
	 * @return a {@link JTabbedPane} with the ports
	 */
	private JTabbedPane getPortPanel() {
		ports = new JTabbedPane();

		JPanel portEditPanel = new JPanel(new GridLayout(0, 2));

		GridBagConstraints panelConstraint = new GridBagConstraints();
		panelConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		panelConstraint.gridx = 0;
		panelConstraint.gridy = 0;
		panelConstraint.weightx = 0.1;
		panelConstraint.weighty = 0.1;
		panelConstraint.fill = GridBagConstraints.BOTH;

		JScrollPane inputScroller = new JScrollPane(getInputPanel());
		portEditPanel.add(inputScroller, panelConstraint);

		panelConstraint.gridy = 1;
		ports.add("Input ports", inputScroller);
		JScrollPane outputScroller = new JScrollPane(getOutputPanel());
		portEditPanel.add(outputScroller, panelConstraint);
		ports.add("Output ports", outputScroller);

		return ports;
	}

	/**
	 * Loops through the {@link ActivityInputPortDefinitionBean} in the
	 * {@link BeanshellActivityConfigurationBean} and creates a
	 * {@link BeanshellInputViewer} for each one. Displays the name and a
	 * {@link JSpinner} to change the depth for each one and a {@link JButton}
	 * to remove it. Currently the individual components from a
	 * {@link BeanshellInputViewer} are added rather than the
	 * {@link BeanshellInputViewer} itself
	 * 
	 * @return panel containing the view over the input ports
	 */
	private JPanel getInputPanel() {
		final JPanel inputEditPanel = new JPanel(new GridBagLayout());
		inputEditPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Inputs"));

		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputEditPanel.add(new JLabel("Name"), inputConstraint);
		inputConstraint.gridx = 1;
		inputEditPanel.add(new JLabel("Depth"), inputConstraint);

		inputGridy = 1;
		inputConstraint.gridx = 0;
		for (ActivityInputPortDefinitionBean inputBean : configuration
				.getInputPortDefinitions()) {
			// FIXME refactor this into a method
			inputConstraint.gridy = inputGridy;
			final BeanshellInputViewer beanshellInputViewer = new BeanshellInputViewer(
					inputBean, true);
			inputViewList.add(beanshellInputViewer);
			inputConstraint.gridx = 0;
			final JTextField nameField = beanshellInputViewer.getNameField();
			nameField.getDocument().addDocumentListener(new DocumentListener(){
				public void changedUpdate(DocumentEvent e) {
		            //Plain text components don't fire these events.					
				}
				public void insertUpdate(DocumentEvent e) {
					inputsChanged = true;
				}
				public void removeUpdate(DocumentEvent e) {		
					inputsChanged = true;
				}
			});
			inputConstraint.weightx = 0.1;
			inputEditPanel.add(nameField, inputConstraint);
			inputConstraint.weightx = 0.0;
			inputConstraint.gridx = 1;
			final JSpinner depthSpinner = beanshellInputViewer
					.getDepthSpinner();
			depthSpinner.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					inputsChanged = true;
				}
			});
			inputEditPanel.add(depthSpinner, inputConstraint);
			inputConstraint.gridx = 2;
			final JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {
					
					inputsChanged = true;

					inputViewList.remove(beanshellInputViewer);
					inputEditPanel.remove(nameField);
					inputEditPanel.remove(depthSpinner);
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
		outerInputPanel = new JPanel();
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

				inputsChanged = true;

				ActivityInputPortDefinitionBean bean = new ActivityInputPortDefinitionBean();
				bean.setAllowsLiteralValues(true);
				bean.setDepth(0);
				List<Class<? extends ExternalReferenceSPI>> handledReferenceSchemes = new ArrayList<Class<? extends ExternalReferenceSPI>>();
				// handledReferenceSchemes.add(FileReference.class);
				bean.setHandledReferenceSchemes(handledReferenceSchemes);
				List<String> mimeTypes = new ArrayList<String>();
				mimeTypes.add("text/plain");
				bean.setMimeTypes(mimeTypes);

				String name2 = "in" + newInputPortNumber;
				boolean nameExists = true;
				while (nameExists == true) {
					nameExists = inputPortNameExists(name2, activity
							.getInputPorts());
					if (nameExists) {
						newInputPortNumber++;
						name2 = "in" + newInputPortNumber;
					}
				}

				bean.setName(name2);
				newInputPortNumber++;
				bean.setTranslatedElementType(String.class);
				inputConstraint.gridy = inputGridy;
				final BeanshellInputViewer beanshellInputViewer = new BeanshellInputViewer(
						bean, true);
				inputViewList.add(beanshellInputViewer);
				inputConstraint.weightx = 0.1;
				inputConstraint.gridx = 0;
				final JTextField nameField = beanshellInputViewer
						.getNameField();
				inputEditPanel.add(nameField, inputConstraint);
				inputConstraint.weightx = 0;
				inputConstraint.gridx = 1;
				final JSpinner depthSpinner = beanshellInputViewer
						.getDepthSpinner();
				inputEditPanel.add(depthSpinner, inputConstraint);
				inputConstraint.gridx = 2;
				final JButton removeButton = new JButton("Remove");
				removeButton.addActionListener(new AbstractAction() {

					public void actionPerformed(ActionEvent e) {
						inputViewList.remove(beanshellInputViewer);
						inputEditPanel.remove(nameField);
						inputEditPanel.remove(depthSpinner);
						inputEditPanel.remove(removeButton);
						inputEditPanel.revalidate();
						inputEditPanel.repaint();
						outerInputPanel.revalidate();
						outerInputPanel.repaint();
					}

				});
				inputEditPanel.add(removeButton, inputConstraint);
				inputEditPanel.revalidate();
				inputEditPanel.repaint();

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

	/**
	 * Loops through the {@link ActivityInputPortDefinitionBean} in the
	 * {@link BeanshellActivityConfigurationBean} and creates a
	 * {@link BeanshellOutputViewer} for each one. Displays the name and a
	 * {@link JSpinner} to change the depth and granular depth for each one and
	 * a {@link JButton} to remove it. Currently the individual components from
	 * a {@link BeanshellOutputViewer} are added rather than the
	 * {@link BeanshellOutputViewer} itself
	 * 
	 * @return the panel containing the view of the output ports
	 */
	private JPanel getOutputPanel() {
		// mimes = new JPanel();
		final JPanel outputEditPanel = new JPanel(new GridBagLayout());
		outputEditPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Outputs"));

		final GridBagConstraints outputConstraint = new GridBagConstraints();
		outputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outputConstraint.gridx = 0;
		outputConstraint.gridy = 0;
		outputConstraint.weightx = 0.1;
		outputConstraint.weighty = 0.1;
		outputConstraint.fill = GridBagConstraints.BOTH;
		outputConstraint.weighty = 0;
		outputEditPanel.add(new JLabel("Name"), outputConstraint);
		outputConstraint.gridx = 1;
		outputEditPanel.add(new JLabel("Depth"), outputConstraint);
		// outputConstraint.gridx = 2;
		// outputEditPanel.add(new JLabel("GranularDepth"), outputConstraint);

		outputGridy = 1;
		outputConstraint.gridx = 0;
		for (ActivityOutputPortDefinitionBean outputBean : configuration
				.getOutputPortDefinitions()) {
			// FIXME refactor this into a method
			outputConstraint.gridy = outputGridy;
			final BeanshellOutputViewer beanshellOutputViewer = new BeanshellOutputViewer(
					outputBean, true);
			outputViewList.add(beanshellOutputViewer);
			outputConstraint.gridx = 0;
			outputConstraint.weightx = 0.1;
			final JTextField nameField = beanshellOutputViewer.getNameField();
			nameField.getDocument().addDocumentListener(new DocumentListener(){
				public void changedUpdate(DocumentEvent e) {
		            //Plain text components don't fire these events.					
				}
				public void insertUpdate(DocumentEvent e) {
					outputsChanged = true;
				}
				public void removeUpdate(DocumentEvent e) {		
					outputsChanged = true;
				}
			});
			outputEditPanel.add(nameField, outputConstraint);
			outputConstraint.weightx = 0;
			outputConstraint.gridx = 1;
			final JSpinner depthSpinner = beanshellOutputViewer
					.getDepthSpinner();
			depthSpinner.addChangeListener(new ChangeListener(){

				public void stateChanged(ChangeEvent e) {
					outputsChanged = true;
				}
			});
			outputEditPanel.add(depthSpinner, outputConstraint);
			outputConstraint.gridx = 2;

			final JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(new AbstractAction() {

				public void actionPerformed(ActionEvent e) {

					outputsChanged = true;

					outputViewList.remove(beanshellOutputViewer);
					outputEditPanel.remove(nameField);
					outputEditPanel.remove(depthSpinner);
					// outputEditPanel.remove(granularDepthSpinner);
					// outputEditPanel.remove(addMimeButton);
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
		outerOutputPanel = new JPanel();
		outerOutputPanel.setLayout(new GridBagLayout());
		GridBagConstraints outerPanelConstraint = new GridBagConstraints();
		// outerPanelConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outerPanelConstraint.fill = GridBagConstraints.BOTH;
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 0;
		outerPanelConstraint.weightx = 0.1;
		outerPanelConstraint.weighty = 0.1;
		outerOutputPanel.add(new JScrollPane(outputEditPanel),
				outerPanelConstraint);
		outerPanelConstraint.weighty = 0;
		JButton addOutputPortButton = new JButton(new AbstractAction() {
			// FIXME refactor this into a method
			public void actionPerformed(ActionEvent e) {
				try {
					
					outputsChanged = true;

					ActivityOutputPortDefinitionBean bean = new ActivityOutputPortDefinitionBean();
					bean.setDepth(0);
					bean.setGranularDepth(0);
					List<String> mimeTypes = new ArrayList<String>();
					mimeTypes.add("text/plain");
					bean.setMimeTypes(mimeTypes);
					String name2 = "out" + newOutputPortNumber;
					boolean nameExists = true;
					while (nameExists == true) {
						nameExists = outputPortNameExists(name2, activity
								.getOutputPorts());
						if (nameExists) {
							newOutputPortNumber++;
							name2 = "out" + newOutputPortNumber;
						}
					}
					bean.setName(name2);
					final BeanshellOutputViewer beanshellOutputViewer = new BeanshellOutputViewer(
							bean, true);
					outputViewList.add(beanshellOutputViewer);
					outputConstraint.gridy = outputGridy;
					outputConstraint.gridx = 0;
					final JTextField nameField = beanshellOutputViewer
							.getNameField();
					outputConstraint.weightx = 0.1;
					outputEditPanel.add(nameField, outputConstraint);
					outputConstraint.gridx = 1;
					outputConstraint.weightx = 0;
					final JSpinner depthSpinner = beanshellOutputViewer
							.getDepthSpinner();
					outputEditPanel.add(depthSpinner, outputConstraint);
					outputConstraint.gridx = 2;

					final JButton removeButton = new JButton("Remove");
					removeButton.addActionListener(new AbstractAction() {

						public void actionPerformed(ActionEvent e) {
							outputViewList.remove(beanshellOutputViewer);
							outputEditPanel.remove(nameField);
							outputEditPanel.remove(depthSpinner);
							// outputEditPanel.remove(granularDepthSpinner);
							outputEditPanel.remove(removeButton);
							// outputEditPanel.remove(addMimeButton);
							outputEditPanel.revalidate();
							outputEditPanel.repaint();
							outerOutputPanel.revalidate();
							outerOutputPanel.repaint();
						}

					});
					outputEditPanel.add(removeButton, outputConstraint);
					outputEditPanel.revalidate();
					newOutputPortNumber++;

					outputGridy++;
				} catch (Exception e1) {
					// throw it, log it??
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
		outerPanelConstraint.gridx = 1;
		outerPanelConstraint.gridy = 0;

		return outerOutputPanel;
	}

	public void setButtonClickedListener(ActionListener listener) {
		buttonClicked = listener;
	}

	/**
	 * Calls
	 * {@link BeanshellActivity#configure(BeanshellActivityConfigurationBean)}
	 * using a {@link BeanshellActivityConfigurationBean} set with the new
	 * values in the view. After setting the values it uses the
	 * {@link #buttonClicked} {@link ActionListener} to tell any listeners that
	 * the new values have been set (primarily used to tell any parent
	 * components to remove the frames containing this panel)
	 * 
	 * @return the action which occurs when the OK button is clicked
	 */
	private AbstractAction getOKAction() {
		return new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				
				// Check if anything has actually changed from the previous configuration
				if (! ((!inputsChanged)
						&& (!outputsChanged)
						&& scriptText.getText().equals(configuration.getScript()) 
						&& classLoaderSharing.equals(configuration.getClassLoaderSharing())
						&& localDependencies.equals(configuration.getLocalDependencies()))) {
					configChanged = true;
				} else {
					// Exit
					configChanged = false;
					buttonClicked.actionPerformed(e);
				}
				
						
				
				buttonClicked.actionPerformed(e);
			}

		};
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

	@Override
	public BeanshellActivityConfigurationBean getConfiguration() {
		return configuration;
	}

	@Override
	public void refreshConfiguration() {
		int visibleTab = -1;
		int subTab = -1;
		if (tabbedPane != null) {
			visibleTab = tabbedPane.getSelectedIndex();
			logger.info("VisibleTab is " + visibleTab);
			if (tabbedPane.getTitleAt(visibleTab).equals("Ports")) {
				subTab = ports.getSelectedIndex();
			}
		}
		this.removeAll();
		initialise();
		if (visibleTab != -1) {
			tabbedPane.setSelectedIndex(visibleTab);
			if (subTab != -1) {
				ports.setSelectedIndex(subTab);
			}
		}
	}

	@Override
	public boolean checkValues() {
		// TODO Not yet implemented
		return true;
	}

}
