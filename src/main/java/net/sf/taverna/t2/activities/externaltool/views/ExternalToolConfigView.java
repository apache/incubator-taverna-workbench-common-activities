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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroup;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManagerListener;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerUI;
import net.sf.taverna.t2.lang.ui.FileTools;
import net.sf.taverna.t2.lang.ui.KeywordDocument;
import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;
import net.sf.taverna.t2.lang.ui.LinePainter;
import net.sf.taverna.t2.lang.ui.NoWrapEditorKit;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import de.uni_luebeck.inb.knowarc.grid.re.RuntimeEnvironmentConstraint;
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
 * {@link #outputViewList} has the {@link ExternalToolFileViewer}s
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
		
	private JTabbedPane tabbedPane = null;

	private int stringReplacementGridy = 1;
	private List<ExternalToolStringReplacementViewer> stringReplacementViewList =
		new ArrayList<ExternalToolStringReplacementViewer>();
	
	private List<ExternalToolFileViewer> inputFileViewList = new ArrayList<ExternalToolFileViewer>();
	
	private List<ExternalToolFileViewer> fileListViewList = new ArrayList<ExternalToolFileViewer>();
	
	private int inputGridy = 1;

	private int outputGridy = 1;
	private List<ExternalToolFileViewer> outputViewList = new ArrayList<ExternalToolFileViewer>();
	
	private int staticGridy = 1;
	private List<ExternalToolStaticUrlViewer> staticUrlViewList = new ArrayList<ExternalToolStaticUrlViewer>();
	
	private List<ExternalToolStaticStringViewer> staticStringViewList = new ArrayList<ExternalToolStaticStringViewer> ();
	
	private List<ExternalToolRuntimeEnvironmentViewer> runtimeEnvironmentViewList =
		new ArrayList<ExternalToolRuntimeEnvironmentViewer>();
	
	private JComboBox invocationSelection;
	
	private JTextField nameField = new JTextField(20);
	private JTextArea descriptionArea = new JTextArea(6,40);

	private JEditorPane scriptTextArea;

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
		initialise(configuration);
	}

	public void noteConfiguration() {			
			configuration = makeConfiguration();
	}

	private ExternalToolActivityConfigurationBean makeConfiguration() {
		ExternalToolActivityConfigurationBean newConfiguration = (ExternalToolActivityConfigurationBean) cloneBean(configuration);

		if (!isFromRepository()) {
			UseCaseDescription ucd = newConfiguration.getUseCaseDescription();

			ucd.setUsecaseid(nameField.getText());
			ucd.setDescription(descriptionArea.getText());
			ucd.setCommand(scriptTextArea.getText());

			ucd.getInputs().clear();
			ucd.getTags().clear();
			synchronized (fileListViewList) {
				for (ExternalToolFileViewer viewer : fileListViewList) {
					ScriptInputUser si = new ScriptInputUser();
					si.setBinary(viewer.isBinary());
					si.setList(true);
					si.setTag(viewer.getValue());
					si.setTempFile(false);
					si.setFile(true);
					ucd.getInputs().put(viewer.getName(), si);
				}
			}

			synchronized (stringReplacementViewList) {
				for (ExternalToolStringReplacementViewer viewer : stringReplacementViewList) {
					ScriptInputUser si = new ScriptInputUser();
					si.setBinary(false);
					si.setList(false);
					si.setTag(viewer.getValue());
					si.setTempFile(false);
					si.setFile(false);
					ucd.getTags().add(si.getTag());
					ucd.getInputs().put(viewer.getName(), si);
				}
			}

			synchronized (inputFileViewList) {
				for (ExternalToolFileViewer viewer : inputFileViewList) {
					ScriptInputUser si = new ScriptInputUser();
					si.setBinary(viewer.isBinary());
					si.setList(false);
					si.setTag(viewer.getValue());
					si.setTempFile(false);
					si.setFile(true);
					ucd.getInputs().put(viewer.getName(), si);
				}
			}

			synchronized (outputViewList) {
				ucd.getOutputs().clear();
				for (ExternalToolFileViewer viewer : outputViewList) {
					ScriptOutput so = new ScriptOutput();
					so.setBinary(viewer.isBinary());
					so.setPath(viewer.getValue());
					ucd.getOutputs().put(viewer.getName(), so);
				}
			}
			ucd.getStatic_inputs().clear();
			synchronized (staticStringViewList) {
				for (ExternalToolStaticStringViewer viewer : staticStringViewList) {
					ScriptInputStatic sis = new ScriptInputStatic();
					sis.setContent(viewer.getContent());
					sis.setTag(viewer.getValue());
					sis.setTempFile(false);
					sis.setFile(true);
					ucd.getStatic_inputs().add(sis);
				}
			}
			synchronized (staticUrlViewList) {
				for (ExternalToolStaticUrlViewer viewer : staticUrlViewList) {
					ScriptInputStatic sis = new ScriptInputStatic();
					sis.setUrl(viewer.getContent());
					sis.setTag(viewer.getValue());
					sis.setTempFile(false);
					sis.setFile(true);
					ucd.getStatic_inputs().add(sis);
				}
			}

			synchronized (runtimeEnvironmentViewList) {
				ucd.getREs().clear();
				for (ExternalToolRuntimeEnvironmentViewer viewer : runtimeEnvironmentViewList) {
					RuntimeEnvironmentConstraint newConstraint = new RuntimeEnvironmentConstraint(
							viewer.getId(), viewer.getRelation());
					ucd.getREs().add(newConstraint);
				}
			}
		}
		InvocationGroup group = (InvocationGroup) invocationSelection
				.getSelectedItem();
		newConfiguration.setInvocationGroup(group);
		return newConfiguration;
	}
	
	public boolean isConfigurationChanged() {
		String configurationString = convertBeanToString(activity.getConfiguration());
		return (!convertBeanToString(makeConfiguration()).equals(configurationString));
	}

	/**
	 * Adds a {@link JButton} which handles the reconfiguring of the
	 * {@link ExternalToolActivity} through the altered
	 * {@link ExternalToolActivityConfigurationBean}. Sets up the initial tabs -
	 * Script (also sets the initial value), Ports & Dependencies and their
	 * initial values through {@link #setDependencies()},
	 * {@link #getPortPanel()}
	 */
	private void initialise(ExternalToolActivityConfigurationBean configuration) {
		CSH
				.setHelpIDString(
						this,
						"net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ExternalToolConfigView");
		this.configuration =  configuration;
		setBorder(javax.swing.BorderFactory.createTitledBorder(null, null,
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Lucida Grande", 1, 12)));

		tabbedPane = new JTabbedPane();
		
		if (!isFromRepository()){
			UseCaseDescription useCaseDescription = configuration
			.getUseCaseDescription();

			nameField.setText(useCaseDescription.getUsecaseid());
			descriptionArea.setText(useCaseDescription.getDescription());
		stringReplacementViewList = new ArrayList<ExternalToolStringReplacementViewer>();
		inputFileViewList = new ArrayList<ExternalToolFileViewer> ();
		fileListViewList = new ArrayList<ExternalToolFileViewer> ();
		outputViewList = new ArrayList<ExternalToolFileViewer> ();
		staticUrlViewList = new ArrayList<ExternalToolStaticUrlViewer> ();
		staticStringViewList = new ArrayList<ExternalToolStaticStringViewer> ();
		runtimeEnvironmentViewList = new ArrayList<ExternalToolRuntimeEnvironmentViewer>();
		
		for (Entry<String,ScriptInput> entry : useCaseDescription.getInputs().entrySet()) {
			String name = entry.getKey();
			ScriptInputUser si = (ScriptInputUser) entry.getValue();
			if (!si.isList() && !si.isFile() && !si.isTempFile()) {
				final ExternalToolStringReplacementViewer inputView  = new ExternalToolStringReplacementViewer(name, si);
				stringReplacementViewList.add(inputView);				
			}
			
		}
		Collections.sort(stringReplacementViewList, new Comparator<ExternalToolStringReplacementViewer>(){

			@Override
			public int compare(ExternalToolStringReplacementViewer o1,
					ExternalToolStringReplacementViewer o2) {
				return o1.getName().compareTo(o2.getName());
			}});

		for (Entry<String,ScriptInput> entry : useCaseDescription.getInputs().entrySet()) {
			String name = entry.getKey();
			ScriptInputUser si = (ScriptInputUser) entry.getValue();
			if (!si.isList() && si.isFile()) {
				final ExternalToolFileViewer inputView  = new ExternalToolFileViewer(name,
						si.getTag(), si.isBinary());
				inputFileViewList.add(inputView);				
			}
			
		}
		Collections.sort(inputFileViewList, new Comparator<ExternalToolFileViewer>(){

			@Override
			public int compare(ExternalToolFileViewer o1,
					ExternalToolFileViewer o2) {
				return o1.getName().compareTo(o2.getName());
			}});
		
		for (Entry<String,ScriptInput> entry : useCaseDescription.getInputs().entrySet()) {
			String name = entry.getKey();
			ScriptInputUser si = (ScriptInputUser) entry.getValue();
			if (si.isList() && si.isFile()) {
				final ExternalToolFileViewer inputView  = new ExternalToolFileViewer(name,
						si.getTag(), si.isBinary());
				fileListViewList.add(inputView);				
			}
			
		}
		Collections.sort(fileListViewList, new Comparator<ExternalToolFileViewer>(){

			@Override
			public int compare(ExternalToolFileViewer o1,
					ExternalToolFileViewer o2) {
				return o1.getName().compareTo(o2.getName());
			}});

		for (Entry<String,ScriptOutput> entry : useCaseDescription.getOutputs().entrySet()) {
			ScriptOutput so = entry.getValue();
			final ExternalToolFileViewer outputView  = new ExternalToolFileViewer(entry.getKey(), so.getPath(), so.isBinary());
			outputViewList .add(outputView);
		}
		Collections.sort(outputViewList, new Comparator<ExternalToolFileViewer>(){

			@Override
			public int compare(ExternalToolFileViewer o1,
					ExternalToolFileViewer o2) {
				return o1.getName().compareTo(o2.getName());
			}});

		for (ScriptInputStatic siss : useCaseDescription.getStatic_inputs()) {
			if ((siss.getUrl() == null) && siss.isFile()) {
				final ExternalToolStaticStringViewer staticView  = new ExternalToolStaticStringViewer(siss);
				staticStringViewList.add(staticView);
			}
		}
		Collections.sort(staticStringViewList, new Comparator<ExternalToolStaticStringViewer>(){

			@Override
			public int compare(ExternalToolStaticStringViewer o1,
					ExternalToolStaticStringViewer o2) {
				return o1.getContent().compareTo(o2.getContent());
			}});
		
		for (ScriptInputStatic sis : useCaseDescription.getStatic_inputs()) {
			if ((sis.getUrl() != null) && sis.isFile()) {
				final ExternalToolStaticUrlViewer staticView  = new ExternalToolStaticUrlViewer(sis);
				staticUrlViewList.add(staticView);
			}
		}
		Collections.sort(staticUrlViewList, new Comparator<ExternalToolStaticUrlViewer>(){

			@Override
			public int compare(ExternalToolStaticUrlViewer o1,
					ExternalToolStaticUrlViewer o2) {
				return o1.getContent().compareTo(o2.getContent());
			}});

		for (RuntimeEnvironmentConstraint rec : useCaseDescription.getREs()) {
			final ExternalToolRuntimeEnvironmentViewer newView  = new ExternalToolRuntimeEnvironmentViewer(rec.getID(), rec.getRelation());
			runtimeEnvironmentViewList.add(newView);
		}
		Collections.sort(runtimeEnvironmentViewList, new Comparator<ExternalToolRuntimeEnvironmentViewer>(){

			@Override
			public int compare(ExternalToolRuntimeEnvironmentViewer o1,
					ExternalToolRuntimeEnvironmentViewer o2) {
				return o1.getId().compareTo(o2.getId());
			}});

		scriptTextArea = new JTextPane();
		new LinePainter(scriptTextArea);
		
		final KeywordDocument doc = new KeywordDocument(new HashSet<String>());
		// NOTE: Due to T2-1145 - always set editor kit BEFORE setDocument
		scriptTextArea.setEditorKit( new NoWrapEditorKit() );
		scriptTextArea.setFont(new Font("Monospaced",Font.PLAIN,14));
		scriptTextArea.setDocument(doc);
		scriptTextArea.setText(useCaseDescription.getCommand());
		scriptTextArea.setCaretPosition(0);
		scriptTextArea.setPreferredSize(new Dimension(200, 100));

		tabbedPane.addTab("Script", createScriptPanel());
		tabbedPane.addTab("String replacements", createStringReplacementPanel());
		tabbedPane.addTab("File inputs", createFilePanel(inputFileViewList, "To file", "File type", "in"));
		tabbedPane.addTab("File outputs", createFilePanel(outputViewList, "From file", "File type", "out"));
		JPanel advancedPanel = new JPanel();
		advancedPanel.setLayout(new GridBagLayout());
		GridBagConstraints advancedConstraint = new GridBagConstraints();
		advancedConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		advancedConstraint.gridx = 0;
		advancedConstraint.gridy = 0;

		advancedConstraint.fill = GridBagConstraints.BOTH;
		advancedConstraint.weighty = 0.1;
		advancedConstraint.weightx = 0.1;
		JTabbedPane advancedTab = new JTabbedPane();
		advancedTab.addTab("Annotation", createAnnotationPanel());
		advancedTab.addTab("File lists", createFilePanel(fileListViewList, "To file containing list", "Individual file type", "in"));
		advancedTab.addTab("Static strings", createStaticStringPanel());
		advancedTab.addTab("Static URLs", createStaticUrlPanel());
		advancedTab.addTab("Runtime environments", createRuntimeEnvironmentPanel(runtimeEnvironmentViewList));
		advancedPanel.add(advancedTab, advancedConstraint);
		tabbedPane.addTab("Advanced", advancedPanel);
		}
		tabbedPane.addTab("Location", createInvocationPanel());
		if (isFromRepository()) {
			tabbedPane.addTab("Edit", createEditablePanel());
		}
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
	
	   public void whenOpened() {
			scriptTextArea.requestFocus();
		    }

	
	private boolean isFromRepository() {
		String repositoryUrl = this.configuration.getRepositoryUrl();
		return (!this.configuration.isEdited() && (repositoryUrl != null) && !repositoryUrl.isEmpty());
	}
	
	private JPanel createEditablePanel() {
		JPanel result = new JPanel();
		JButton makeEditable = new JButton("Edit tool description");
		makeEditable.setToolTipText("Edit the tool description");
		makeEditable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExternalToolActivityConfigurationBean newConfig = (ExternalToolActivityConfigurationBean) cloneBean(configuration);
				newConfig.setEdited(true);
				refreshConfiguration(newConfig);
			}});
		result.add(makeEditable);
		return result;
	}
	
	private JPanel createAnnotationPanel() {
		JPanel result = new JPanel();
		result.setLayout(new BorderLayout());
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		namePanel.add(new JLabel("Name: "));
		namePanel.add(nameField);
		result.add(namePanel, BorderLayout.NORTH);
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BorderLayout());
		descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
		descriptionPanel.add(descriptionArea, BorderLayout.CENTER);
		result.add(descriptionPanel, BorderLayout.CENTER);
		return result;
	}

	private JPanel createScriptPanel() {
		JPanel scriptEditPanel = new JPanel(new BorderLayout());
		scriptEditPanel.add(new LineEnabledTextPanel(scriptTextArea), BorderLayout.CENTER);
		
		JButton loadScriptButton = new JButton("Load description");
		loadScriptButton.setToolTipText("Load tool description from a file");
		loadScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String newScript = FileTools.readStringFromFile(ExternalToolConfigView.this, "Load tool description", ".xml");
				if (newScript != null) {
					XStream xstream = new XStream();
					xstream.setClassLoader(configuration.getClass().getClassLoader());
					refreshConfiguration((ExternalToolActivityConfigurationBean) xstream.fromXML(newScript));
				}
			}
		});

		JButton saveScriptButton = new JButton("Save description");
		saveScriptButton.setToolTipText("Save the tool description to a file");
		saveScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileTools.saveStringToFile(ExternalToolConfigView.this, "Save tool description", ".xml", convertBeanToString(makeConfiguration()));
			}
		});

		JButton clearScriptButton = new JButton("Clear script");
		clearScriptButton
				.setToolTipText("Clear the script from the edit area");
		clearScriptButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clearScript();
			}

		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(loadScriptButton);
		buttonPanel.add(saveScriptButton);
		buttonPanel.add(clearScriptButton);
		
		scriptEditPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		return scriptEditPanel;

		
	}
	
	/**
	 * Method for clearing the script
	 * 
	 */
	private void clearScript() {
		if (JOptionPane.showConfirmDialog(this,
				"Do you really want to clear the tool description?",
				"Clearing the tool description", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			scriptTextArea.setText("");
		}

	}

	private JPanel createStringReplacementPanel() {
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

		inputEditPanel.add(new JLabel("Taverna port name"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("String to replace"), inputConstraint);


		inputConstraint.gridx = 0;
		synchronized(stringReplacementViewList) {
		for (ExternalToolStringReplacementViewer inputView : stringReplacementViewList) {
			addStringReplacementViewer(outerInputPanel, inputEditPanel, inputView);

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

				int portNumber = 1;
				String name2 = "in" + portNumber++;
				boolean nameExists = true;
				while (nameExists == true) {
					nameExists = portNameExists(name2);
					if (nameExists) {
						name2 = "in" + portNumber++;
					}
				}
				
				ExternalToolStringReplacementViewer newViewer = new ExternalToolStringReplacementViewer(name2);
				synchronized(stringReplacementViewList) {
					stringReplacementViewList.add(newViewer);
					addStringReplacementViewer(outerInputPanel, inputEditPanel, newViewer);
					inputEditPanel.revalidate();
					inputEditPanel.repaint();
				}

			}

		});
		addInputPortButton.setText("Add string replacement");
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
	
	private void addStringReplacementViewer(final JPanel outerPanel, final JPanel panel, ExternalToolStringReplacementViewer viewer) {
		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputConstraint.gridy = stringReplacementGridy;
		inputConstraint.gridx = 0;
		
		final JTextField nameField = viewer.getNameField();
		panel.add(nameField, inputConstraint);
		inputConstraint.gridx++;
		
		final JTextField valueField = viewer.getValueField();
		panel.add(valueField ,inputConstraint);
		inputConstraint.gridx++;
		
		final JButton removeButton = new JButton("Remove");
		final ExternalToolStringReplacementViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(stringReplacementViewList) {
					stringReplacementViewList.remove(v);
				}
				panel.remove(nameField);
				panel.remove(valueField);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, inputConstraint);
		inputConstraint.gridy = ++ stringReplacementGridy;
		panel.add(new JSeparator(), inputConstraint);
		stringReplacementGridy++;
		
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
		
		JButton manageInvocation = new JButton(new AbstractAction("Manage locations"){

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
	
	public void refreshConfiguration(ExternalToolActivityConfigurationBean config) {
		int visibleTab = -1;
		if (tabbedPane != null) {
			visibleTab = tabbedPane.getSelectedIndex();
		}
		this.removeAll();
		initialise(config);
		if (visibleTab != -1) {
			tabbedPane.setSelectedIndex(visibleTab);
		}
	}

	@Override
	public void refreshConfiguration() {
		refreshConfiguration(activity.getConfiguration());
	}

	@Override
	public boolean checkValues() {
		boolean result = true;
		return result;
	}
	

	/**
	 * Check the proposed port name against the set of ports
	 * 
	 * @return
	 */
	private boolean portNameExists(String name) {
		for (ExternalToolFileViewer v : inputFileViewList) {
			if (name.equals(v.getName())) {
					return true;
			}
		}
		for (ExternalToolFileViewer v : fileListViewList) {
			if (name.equals(v.getName())) {
					return true;
			}
		}
		for (ExternalToolStringReplacementViewer v : stringReplacementViewList) {
			if (name.equals(v.getName())) {
				return true;
			}			
		}
		for (ExternalToolFileViewer v : outputViewList) {
			if (name.equals(v.getName())) {
				return true;
			}			
		}
		return false;
	}

	private JPanel createFilePanel(final List<ExternalToolFileViewer> viewList, String fileHeader, String typeHeader, final String portPrefix) {
		final JPanel outerFilePanel = new JPanel();
			final JPanel fileEditPanel = new JPanel(new GridBagLayout());
	
			final GridBagConstraints fileConstraint = new GridBagConstraints();
			fileConstraint.insets = new Insets(5,5,5,5);
			fileConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			fileConstraint.gridx = 0;
			fileConstraint.gridy = 0;
			fileConstraint.weightx = 0.1;
			fileConstraint.fill = GridBagConstraints.BOTH;
	
			fileEditPanel.add(new JLabel("Taverna port name"), fileConstraint);
			fileConstraint.gridx++;
			fileEditPanel.add(new JLabel(fileHeader), fileConstraint);	
			fileConstraint.gridx++;
			fileEditPanel.add(new JLabel(typeHeader), fileConstraint);	
	
			fileConstraint.gridx = 0;
			synchronized(viewList) {
			for (ExternalToolFileViewer outputView : viewList) {
				addFileViewer(viewList, outerFilePanel, fileEditPanel, outputView);
			}
			}
			outerFilePanel.setLayout(new GridBagLayout());
			GridBagConstraints outerPanelConstraint = new GridBagConstraints();
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 0;
			outerPanelConstraint.weightx = 0.1;
			outerPanelConstraint.weighty = 0.1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerFilePanel.add(new JScrollPane(fileEditPanel),
					outerPanelConstraint);
			outerPanelConstraint.weighty = 0;
			JButton addFilePortButton = new JButton(new AbstractAction() {
				// FIXME refactor this into a method
				public void actionPerformed(ActionEvent e) {
					
					int portNumber = 1;
	
					String name2 = portPrefix + portNumber++;
					boolean nameExists = true;
					while (nameExists == true) {
						nameExists = portNameExists(name2);
						if (nameExists) {
							name2 = portPrefix + portNumber++;
						}
					}
					
					ExternalToolFileViewer newViewer = new ExternalToolFileViewer(name2);
					synchronized(viewList) {
						viewList.add(newViewer);
						addFileViewer(viewList, outerFilePanel, fileEditPanel, newViewer);
						fileEditPanel.revalidate();
						fileEditPanel.repaint();
					}	
				}
	
			});
			addFilePortButton.setText("Add Port");
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
	
			buttonPanel.add(addFilePortButton, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerFilePanel.add(buttonPanel, outerPanelConstraint);
	
			return outerFilePanel;
		}
	
	private void addFileViewer(final List<ExternalToolFileViewer> viewList, final JPanel outerPanel, final JPanel panel, ExternalToolFileViewer viewer) {
		final GridBagConstraints fileConstraint = new GridBagConstraints();
		fileConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		fileConstraint.weightx = 0.1;
		fileConstraint.fill = GridBagConstraints.BOTH;

		fileConstraint.gridy = outputGridy;
		fileConstraint.gridx = 0;
		final JTextField nameField = viewer.getNameField();
		panel.add(nameField, fileConstraint);
		
		fileConstraint.weightx = 0.0;
		fileConstraint.gridx++;

		final JTextField valueField = viewer.getValueField();
		panel.add(valueField ,fileConstraint);
		fileConstraint.gridx++;
		
		final JComboBox typeSelector = viewer.getTypeSelector();
		panel.add(typeSelector ,fileConstraint);
		
		fileConstraint.gridx++;
		final JButton removeButton = new JButton("Remove");
		final ExternalToolFileViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(viewList) {
					viewList.remove(v);
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
		panel.add(removeButton, fileConstraint);
		outputGridy++;
		
	}
	
	private JPanel createRuntimeEnvironmentPanel(final List<ExternalToolRuntimeEnvironmentViewer> viewList) {
		final JPanel outerFilePanel = new JPanel();
			final JPanel runtimeEnvironmentEditPanel = new JPanel(new GridBagLayout());
	
			final GridBagConstraints fileConstraint = new GridBagConstraints();
			fileConstraint.insets = new Insets(5,5,5,5);
			fileConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			fileConstraint.gridx = 0;
			fileConstraint.gridy = 0;
			fileConstraint.weightx = 0.1;
			fileConstraint.fill = GridBagConstraints.BOTH;
	
			runtimeEnvironmentEditPanel.add(new JLabel("Environment description"), fileConstraint);
			fileConstraint.gridx++;
			runtimeEnvironmentEditPanel.add(new JLabel("Relationship"), fileConstraint);	
			fileConstraint.gridx++;
	
	
			fileConstraint.gridx = 0;
			synchronized(viewList) {
			for (ExternalToolRuntimeEnvironmentViewer outputView : viewList) {
				addEnvironmentViewer(viewList, outerFilePanel, runtimeEnvironmentEditPanel, outputView);
			}
			}
			outerFilePanel.setLayout(new GridBagLayout());
			GridBagConstraints outerPanelConstraint = new GridBagConstraints();
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 0;
			outerPanelConstraint.weightx = 0.1;
			outerPanelConstraint.weighty = 0.1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerFilePanel.add(new JScrollPane(runtimeEnvironmentEditPanel),
					outerPanelConstraint);
			outerPanelConstraint.weighty = 0;
			JButton addRuntimeEnvironmentButton = new JButton(new AbstractAction() {
				// FIXME refactor this into a method
				public void actionPerformed(ActionEvent e) {
					
					ExternalToolRuntimeEnvironmentViewer newViewer = new ExternalToolRuntimeEnvironmentViewer();
					synchronized(viewList) {
						viewList.add(newViewer);
						addEnvironmentViewer(viewList, outerFilePanel, runtimeEnvironmentEditPanel, newViewer);
						runtimeEnvironmentEditPanel.revalidate();
						runtimeEnvironmentEditPanel.repaint();
					}	
				}
	
			});
			addRuntimeEnvironmentButton.setText("Add runtime environment");
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
	
			buttonPanel.add(addRuntimeEnvironmentButton, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerFilePanel.add(buttonPanel, outerPanelConstraint);
	
			return outerFilePanel;
		}
	
	private void addEnvironmentViewer(final List<ExternalToolRuntimeEnvironmentViewer> viewList, final JPanel outerPanel, final JPanel panel, ExternalToolRuntimeEnvironmentViewer viewer) {
		final GridBagConstraints fileConstraint = new GridBagConstraints();
		fileConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		fileConstraint.weightx = 0.1;
		fileConstraint.fill = GridBagConstraints.BOTH;

		fileConstraint.gridy = outputGridy;
		fileConstraint.gridx = 0;
		final JTextField idField = viewer.getIdField();
		panel.add(idField, fileConstraint);
		
		fileConstraint.weightx = 0.0;
		fileConstraint.gridx++;

		final JComboBox relationSelector = viewer.getRelationSelector();
		panel.add(relationSelector ,fileConstraint);
		
		fileConstraint.gridx++;
		final JButton removeButton = new JButton("Remove");
		final ExternalToolRuntimeEnvironmentViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(viewList) {
					viewList.remove(v);
				}
				panel.remove(idField);
				panel.remove(relationSelector);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, fileConstraint);
		outputGridy++;
		
	}


	private JPanel createStaticUrlPanel() {
		final JPanel outerStaticPanel = new JPanel();
			final JPanel staticEditPanel = new JPanel(new GridBagLayout());
	
			final GridBagConstraints staticConstraint = new GridBagConstraints();
			staticConstraint.insets = new Insets(5,5,5,5);
			staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			staticConstraint.gridx = 0;
			staticConstraint.gridy = 0;
			staticConstraint.weightx = 0.1;
			staticConstraint.fill = GridBagConstraints.BOTH;
	
			staticEditPanel.add(new JLabel("Copy from URL"), staticConstraint);	
			staticConstraint.gridx++;
			staticEditPanel.add(new JLabel("To file"), staticConstraint);	
			staticConstraint.gridx++;
	
			staticConstraint.gridx = 0;
			synchronized(staticUrlViewList) {
			for (ExternalToolStaticUrlViewer staticView : staticUrlViewList) {
				addStaticUrlViewer(outerStaticPanel, staticEditPanel, staticView);
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
	
					ExternalToolStaticUrlViewer newViewer = new ExternalToolStaticUrlViewer();
					synchronized(staticUrlViewList) {
						staticUrlViewList.add(newViewer);
						addStaticUrlViewer(outerStaticPanel, staticEditPanel, newViewer);
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
	
	private void addStaticUrlViewer(final JPanel outerPanel, final JPanel panel, ExternalToolStaticUrlViewer viewer) {
		final GridBagConstraints staticConstraint = new GridBagConstraints();
		staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		staticConstraint.weightx = 0.1;
		staticConstraint.fill = GridBagConstraints.BOTH;

		staticConstraint.gridy = staticGridy;
		staticConstraint.gridx = 0;
		staticConstraint.weightx = 0.1;
		
		final JTextField contentField = viewer.getContentField();
		panel.add(contentField, staticConstraint);
		
		staticConstraint.gridx++;
		final JTextField valueField = viewer.getValueField();
		panel.add(valueField ,staticConstraint);
		
		staticConstraint.gridx++;
		
		final JButton removeButton = new JButton("Remove");
		final ExternalToolStaticUrlViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(staticUrlViewList) {
					staticUrlViewList.remove(v);
				}
				panel.remove(contentField);
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

	private JPanel createStaticStringPanel() {
		final JPanel outerStaticPanel = new JPanel();
			final JPanel staticEditPanel = new JPanel(new GridBagLayout());
	
			final GridBagConstraints staticConstraint = new GridBagConstraints();
			staticConstraint.insets = new Insets(5,5,5,5);
			staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			staticConstraint.gridx = 0;
			staticConstraint.gridy = 0;
			staticConstraint.weightx = 0.1;
			staticConstraint.fill = GridBagConstraints.BOTH;
	
			staticEditPanel.add(new JLabel("String to copy"), staticConstraint);	
			staticConstraint.gridx++;
			staticEditPanel.add(new JLabel("To file"), staticConstraint);
	
			staticConstraint.gridx = 0;
			synchronized(staticUrlViewList) {
			for (ExternalToolStaticStringViewer staticView : staticStringViewList) {
				addStaticStringViewer(outerStaticPanel, staticEditPanel, staticView);
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
			JButton addStaticStringButton = new JButton(new AbstractAction() {
				// FIXME refactor this into a method
				public void actionPerformed(ActionEvent e) {
	
					ExternalToolStaticStringViewer newViewer = new ExternalToolStaticStringViewer();
					synchronized(staticUrlViewList) {
						staticStringViewList.add(newViewer);
						addStaticStringViewer(outerStaticPanel, staticEditPanel, newViewer);
						staticEditPanel.revalidate();
						staticEditPanel.repaint();
					}
				}
	
			});
			addStaticStringButton.setText("Add static string");
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
	
			buttonPanel.add(addStaticStringButton, outerPanelConstraint);
	
			outerPanelConstraint.weightx = 0;
			outerPanelConstraint.weighty = 0;
			outerPanelConstraint.gridx = 0;
			outerPanelConstraint.gridy = 1;
			outerPanelConstraint.fill = GridBagConstraints.BOTH;
			outerStaticPanel.add(buttonPanel, outerPanelConstraint);
	
			return outerStaticPanel;
		}
	
	private void addStaticStringViewer(final JPanel outerPanel, final JPanel panel, ExternalToolStaticStringViewer viewer) {
		final GridBagConstraints staticConstraint = new GridBagConstraints();
		staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		staticConstraint.weightx = 0.1;
		staticConstraint.fill = GridBagConstraints.BOTH;

		staticConstraint.gridy = staticGridy;
		staticConstraint.gridx = 0;
		staticConstraint.weightx = 0.1;
		
		final JTextArea contentField = viewer.getContentField();
		panel.add(contentField, staticConstraint);

		staticConstraint.gridx++;
		
		final JPanel valuePanel = new JPanel();
		valuePanel.setLayout(new BorderLayout());
		final JTextField valueField = viewer.getValueField();
		valuePanel.add(valueField, BorderLayout.NORTH);
		panel.add(valuePanel ,staticConstraint);
		
		staticConstraint.gridx++;
		
		final JPanel removePanel = new JPanel();
		removePanel.setLayout(new BorderLayout());
		final JButton removeButton = new JButton("Remove");
		removePanel.add(removeButton, BorderLayout.NORTH);
		final ExternalToolStaticStringViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(staticStringViewList) {
					staticStringViewList.remove(v);
				}
				panel.remove(contentField);
				panel.remove(valuePanel);
				panel.remove(removePanel);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removePanel, staticConstraint);
		staticGridy++;
		
	}

}
