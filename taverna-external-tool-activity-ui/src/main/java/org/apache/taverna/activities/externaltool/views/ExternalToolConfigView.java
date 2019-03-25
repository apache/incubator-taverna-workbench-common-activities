package org.apache.taverna.activities.externaltool.views;
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.help.CSH;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.ExternalToolActivityHealthChecker;
import org.apache.taverna.activities.externaltool.utils.Tools;
import org.apache.taverna.lang.ui.KeywordDocument;
import org.apache.taverna.lang.ui.LinePainter;
import org.apache.taverna.lang.ui.NoWrapEditorKit;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import org.apache.log4j.Logger;

import org.apache.taverna.activities.externaltool.desc.ScriptInput;
import org.apache.taverna.activities.externaltool.desc.ScriptInputStatic;
import org.apache.taverna.activities.externaltool.desc.ScriptInputUser;
import org.apache.taverna.activities.externaltool.desc.ScriptOutput;
import org.apache.taverna.activities.externaltool.desc.ToolDescription;

/**
 * Provides the configurable view for a {@link ExternalToolActivity} through
 * it's {@link ExternalToolActivityConfigurationBean}. Has 3 main tabs - Script,
 * Ports & Dependencies. The {@link #inputViewList} contains the
 * {@link ExternalToolInputViewer}s describing the input ports and
 * {@link #outputViewList} has the {@link ExternalToolFileViewer}s
 * 
 * @author Ian Dunlop
 * @author Alex Nenadic
 * @author Alan R Williams
 * 
 */
@SuppressWarnings("serial")
public class ExternalToolConfigView
		extends
		ActivityConfigurationPanel {
	
	private static final Color LINE_COLOR = new Color(225,225,225);

	private static final String FILE_INPUT_DESCRIPTION = "You can use a file input to feed data into " +
			"the service via an input port and have that data written to the specified file.";

	private static final String FILE_OUTPUT_DESCRIPTION = "You can use a file output to take the " +
			"content of a file produced by the tool and send it to an output port of the service.";

	private static final String FILE_LIST_DESCRIPTION = "If you feed a list of data into a file list " +
			"input, then each data item is written to a temporary file. A file is produced containing " +
			"the names of those temporary file. That index file can then be used as part of the tool " +
			"command.";

	private static final String VALID_NAME_REGEX = "[\\p{L}\\p{Digit}_]+";

	private static Logger logger = Logger
			.getLogger(ExternalToolConfigView.class);

	/** The activity which this view describes */
	protected ExternalToolActivity activity;

	/** The configuration bean used to configure the activity */
	private ExternalToolActivityConfigurationBean configuration;

	private JTabbedPane tabbedPane = null;
	private JPanel advancedPanel = null;
	private JTabbedPane advancedTab = null;
	private AnnotationPanel annotationPanel = null;
	
	private int stringReplacementGridy = 1;
	private List<ExternalToolStringReplacementViewer> stringReplacementViewList = new ArrayList<ExternalToolStringReplacementViewer>();

	private List<ExternalToolFileViewer> inputFileViewList = new ArrayList<ExternalToolFileViewer>();

	private List<ExternalToolFileViewer> fileListViewList = new ArrayList<ExternalToolFileViewer>();

	private int inputGridy = 1;

	private int outputGridy = 1;
	private List<ExternalToolFileViewer> outputViewList = new ArrayList<ExternalToolFileViewer>();

	private int staticGridy = 1;
	private List<ExternalToolStaticUrlViewer> staticUrlViewList = new ArrayList<ExternalToolStaticUrlViewer>();

	private List<ExternalToolStaticStringViewer> staticStringViewList = new ArrayList<ExternalToolStaticStringViewer>();

/*	private List<ExternalToolRuntimeEnvironmentViewer> runtimeEnvironmentViewList = new ArrayList<ExternalToolRuntimeEnvironmentViewer>();
*/

	private JTextField nameField = new JTextField(20);
	private JTextField groupField = new JTextField(20);
	private JTextArea descriptionArea = new JTextArea(6, 40);

	private JEditorPane scriptTextArea;

	private InvocationPanel invocationPanel;

	private JCheckBox stdInCheckBox = new JCheckBox("Show STDIN");
	private JCheckBox stdOutCheckBox = new JCheckBox("Show STDOUT");
	private JCheckBox stdErrCheckBox = new JCheckBox("Show STDERR");
	
	private JTextField returnCodesField = new JTextField(20);

	/**
	 * Stores the {@link ExternalToolActivity}, gets its
	 * {@link ExternalToolActivityConfigurationBean}, sets the layout and calls
	 * {@link #initialise()} to get the view going
	 * 
	 * @param activity2
	 *            the {@link ExternalToolActivity} that the view is over
	 */
	public ExternalToolConfigView(Activity activity) {
		this.activity = activity;
		ExternalToolActivityHealthChecker.updateLocation(activity.getConfiguration());
		configuration = (ExternalToolActivityConfigurationBean) cloneBean(activity
				.getConfiguration());
		setLayout(new GridBagLayout());
		initialise(configuration);
	}

	public void noteConfiguration() {
		configuration = makeConfiguration();
	}

	public ExternalToolActivityConfigurationBean makeConfiguration() {
		ExternalToolActivityConfigurationBean newConfiguration = (ExternalToolActivityConfigurationBean) cloneBean(configuration);
		ExternalToolActivityHealthChecker.updateLocation(newConfiguration);
		

		if (!isFromRepository()) {
			ToolDescription ucd = newConfiguration.getToolDescription();

			ucd.setTooldescid(nameField.getText());
			if (groupField.getText().isEmpty()) {
				ucd.setGroup(null);
			} else {
				ucd.setGroup(groupField.getText());
			}
			ucd.setDescription(descriptionArea.getText());
			ucd.setCommand(scriptTextArea.getText());
			ucd.setReturnCodesAsText(returnCodesField.getText());
			ucd.setIncludeStdIn(stdInCheckBox.isSelected());
			ucd.setIncludeStdOut(stdOutCheckBox.isSelected());
			ucd.setIncludeStdErr(stdErrCheckBox.isSelected());

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

/*			synchronized (runtimeEnvironmentViewList) {
				ucd.getREs().clear();
				for (ExternalToolRuntimeEnvironmentViewer viewer : runtimeEnvironmentViewList) {
					RuntimeEnvironmentConstraint newConstraint = new RuntimeEnvironmentConstraint(
							viewer.getId(), viewer.getRelation());
					ucd.getREs().add(newConstraint);
				}
			}*/
		}
		invocationPanel.fillInConfiguration(newConfiguration);

		return newConfiguration;
	}

	public boolean isConfigurationChanged() {
		String configurationString = convertBeanToString(activity
				.getConfiguration());
		return (!convertBeanToString(makeConfiguration()).equals(
				configurationString));
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
		CSH.setHelpIDString(
				this,
				"net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ExternalToolConfigView");
		this.configuration = configuration;
		setBorder(javax.swing.BorderFactory.createTitledBorder(null, null,
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION,
				new java.awt.Font("Lucida Grande", 1, 12)));

		tabbedPane = new JTabbedPane();
		
		if (invocationPanel != null) {
			invocationPanel.stopObserving();
		}

		if (!isFromRepository()) {
			ToolDescription toolDescription = configuration
					.getToolDescription();

			nameField.setText(toolDescription.getTooldescid());
			if (toolDescription.getGroup() != null) {
				groupField.setText(toolDescription.getGroup());
			}
			descriptionArea.setText(toolDescription.getDescription());
			stringReplacementViewList = new ArrayList<ExternalToolStringReplacementViewer>();
			inputFileViewList = new ArrayList<ExternalToolFileViewer>();
			fileListViewList = new ArrayList<ExternalToolFileViewer>();
			outputViewList = new ArrayList<ExternalToolFileViewer>();
			staticUrlViewList = new ArrayList<ExternalToolStaticUrlViewer>();
			staticStringViewList = new ArrayList<ExternalToolStaticStringViewer>();
/*			runtimeEnvironmentViewList = new ArrayList<ExternalToolRuntimeEnvironmentViewer>();*/

			for (Entry<String, ScriptInput> entry : toolDescription
					.getInputs().entrySet()) {
				String name = entry.getKey();
				ScriptInputUser si = (ScriptInputUser) entry.getValue();
				if (Tools.isStringReplacement(si)) {
					final ExternalToolStringReplacementViewer inputView = new ExternalToolStringReplacementViewer(
							name, si);
					stringReplacementViewList.add(inputView);
				}

			}
			Collections.sort(stringReplacementViewList,
					new Comparator<ExternalToolStringReplacementViewer>() {

						@Override
						public int compare(
								ExternalToolStringReplacementViewer o1,
								ExternalToolStringReplacementViewer o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});

			for (Entry<String, ScriptInput> entry : toolDescription
					.getInputs().entrySet()) {
				String name = entry.getKey();
				ScriptInputUser si = (ScriptInputUser) entry.getValue();
				if (Tools.isInputFile(si)) {
					final ExternalToolFileViewer inputView = new ExternalToolFileViewer(
							name, si.getTag(), si.isBinary());
					inputFileViewList.add(inputView);
				}

			}
			Collections.sort(inputFileViewList,
					new Comparator<ExternalToolFileViewer>() {

						@Override
						public int compare(ExternalToolFileViewer o1,
								ExternalToolFileViewer o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});

			for (Entry<String, ScriptInput> entry : toolDescription
					.getInputs().entrySet()) {
				String name = entry.getKey();
				ScriptInputUser si = (ScriptInputUser) entry.getValue();
				if (Tools.isFileList(si)) {
					final ExternalToolFileViewer inputView = new ExternalToolFileViewer(
							name, si.getTag(), si.isBinary());
					fileListViewList.add(inputView);
				}

			}
			Collections.sort(fileListViewList,
					new Comparator<ExternalToolFileViewer>() {

						@Override
						public int compare(ExternalToolFileViewer o1,
								ExternalToolFileViewer o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});

			for (Entry<String, ScriptOutput> entry : toolDescription
					.getOutputs().entrySet()) {
				ScriptOutput so = entry.getValue();
				final ExternalToolFileViewer outputView = new ExternalToolFileViewer(
						entry.getKey(), so.getPath(), so.isBinary());
				outputViewList.add(outputView);
			}
			Collections.sort(outputViewList,
					new Comparator<ExternalToolFileViewer>() {

						@Override
						public int compare(ExternalToolFileViewer o1,
								ExternalToolFileViewer o2) {
							return o1.getName().compareTo(o2.getName());
						}
					});

			for (ScriptInputStatic siss : toolDescription.getStatic_inputs()) {
				if ((siss.getUrl() == null) && siss.isFile()) {
					final ExternalToolStaticStringViewer staticView = new ExternalToolStaticStringViewer(
							siss);
					staticStringViewList.add(staticView);
				}
			}
			Collections.sort(staticStringViewList,
					new Comparator<ExternalToolStaticStringViewer>() {

						@Override
						public int compare(ExternalToolStaticStringViewer o1,
								ExternalToolStaticStringViewer o2) {
							return o1.getContent().compareTo(o2.getContent());
						}
					});

			for (ScriptInputStatic sis : toolDescription.getStatic_inputs()) {
				if ((sis.getUrl() != null) && sis.isFile()) {
					final ExternalToolStaticUrlViewer staticView = new ExternalToolStaticUrlViewer(
							sis);
					staticUrlViewList.add(staticView);
				}
			}
			Collections.sort(staticUrlViewList,
					new Comparator<ExternalToolStaticUrlViewer>() {

						@Override
						public int compare(ExternalToolStaticUrlViewer o1,
								ExternalToolStaticUrlViewer o2) {
							return o1.getContent().compareTo(o2.getContent());
						}
					});

/*			for (RuntimeEnvironmentConstraint rec : toolDescription.getREs()) {
				final ExternalToolRuntimeEnvironmentViewer newView = new ExternalToolRuntimeEnvironmentViewer(
						rec.getID(), rec.getRelation());
				runtimeEnvironmentViewList.add(newView);
			}
			Collections.sort(runtimeEnvironmentViewList,
					new Comparator<ExternalToolRuntimeEnvironmentViewer>() {

						@Override
						public int compare(
								ExternalToolRuntimeEnvironmentViewer o1,
								ExternalToolRuntimeEnvironmentViewer o2) {
							return o1.getId().compareTo(o2.getId());
						}
					});*/

			scriptTextArea = new JTextPane();
			new LinePainter(scriptTextArea, LINE_COLOR);

			final KeywordDocument doc = new KeywordDocument(
					new HashSet<String>());
			// NOTE: Due to T2-1145 - always set editor kit BEFORE setDocument
			scriptTextArea.setEditorKit(new NoWrapEditorKit());
			scriptTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
			scriptTextArea.setDocument(doc);
			scriptTextArea.setText(toolDescription.getCommand());
			scriptTextArea.setCaretPosition(0);
			scriptTextArea.setPreferredSize(new Dimension(200, 100));

			tabbedPane.addTab("Command", new ScriptPanel(this, scriptTextArea, stdInCheckBox, stdOutCheckBox, stdErrCheckBox, returnCodesField));
			tabbedPane.addTab("String replacements",
					new StringReplacementPanel(this, stringReplacementViewList));
			tabbedPane.addTab(
					"File inputs",
					new FilePanel(this, inputFileViewList, "To file", "File type",
							"in", FILE_INPUT_DESCRIPTION, "Add file input"));
			tabbedPane.addTab(
					"File outputs",
					new FilePanel(this, outputViewList, "From file", "File type",
							"out", FILE_OUTPUT_DESCRIPTION, "Add file output"));
			advancedPanel = new JPanel();
			advancedPanel.setLayout(new GridBagLayout());
			GridBagConstraints advancedConstraint = new GridBagConstraints();
			advancedConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
			advancedConstraint.gridx = 0;
			advancedConstraint.gridy = 0;

			advancedConstraint.fill = GridBagConstraints.BOTH;
			advancedConstraint.weighty = 0.1;
			advancedConstraint.weightx = 0.1;
			advancedTab = new JTabbedPane();
			advancedTab.addTab("Strings", new StaticStringPanel(staticStringViewList));
			advancedTab.addTab("URLs", new StaticUrlPanel(staticUrlViewList));
			advancedTab.addTab(
					"File lists",
					new FilePanel(this, fileListViewList,
							"To file containing list", "Individual file type",
							"in", FILE_LIST_DESCRIPTION, "Add file list"));
			annotationPanel = new AnnotationPanel(nameField, descriptionArea, groupField);
			advancedTab.addTab("Annotation", annotationPanel);
			final ToolXMLPanel toolXMLPanel = new ToolXMLPanel(configuration.getToolDescription());
			advancedTab.addTab("XML", toolXMLPanel);
			advancedTab.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if (advancedTab.getSelectedComponent() == toolXMLPanel) {
						toolXMLPanel.regenerateTree(makeConfiguration().getToolDescription());
					}
				}});
			tabbedPane.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					if ((tabbedPane.getSelectedComponent() == advancedPanel) &&
							(advancedTab.getSelectedComponent() == toolXMLPanel)) {
						toolXMLPanel.regenerateTree(makeConfiguration().getToolDescription());						
					}
				}
				
			});
/*			advancedTab.addTab("Runtime environments",
					createRuntimeEnvironmentPanel(runtimeEnvironmentViewList));*/
			advancedPanel.add(advancedTab, advancedConstraint);
			tabbedPane.addTab("Advanced", advancedPanel);
		}
		invocationPanel = new InvocationPanel(configuration);
		
		tabbedPane.addTab("Location", invocationPanel);
		if (isFromRepository()) {
			tabbedPane.addTab("Edit", new EditablePanel(this));
		}
		GridBagConstraints outerConstraint = new GridBagConstraints();
		outerConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outerConstraint.gridx = 0;
		outerConstraint.gridy = 0;

		outerConstraint.fill = GridBagConstraints.BOTH;
		outerConstraint.weighty = 0.1;
		outerConstraint.weightx = 0.1;
		add(tabbedPane, outerConstraint);

		setPreferredSize(new Dimension(700, 500));
		this.validate();
	}

	public void whenOpened() {
		if (scriptTextArea != null) {
			scriptTextArea.requestFocus();
		}
	}

	private boolean isFromRepository() {
		return (!this.configuration.isEdited() && isOriginallyFromRepository());
	}
	
	public boolean isOriginallyFromRepository() {
		String repositoryUrl = this.configuration.getRepositoryUrl();
		return ((repositoryUrl != null) && !repositoryUrl
				.isEmpty());
		
	}


	@Override
	public ExternalToolActivityConfigurationBean getConfiguration() {
		return configuration;
	}

	public void refreshConfiguration(
			ExternalToolActivityConfigurationBean config) {
		int visibleTab = -1;
		int secondaryTab = -1;
		if (tabbedPane != null) {
			visibleTab = tabbedPane.getSelectedIndex();
			if (tabbedPane.getSelectedComponent().equals(advancedTab)) {
				secondaryTab = advancedTab.getSelectedIndex();
			}
		}
		this.removeAll();
		initialise(config);
		if (visibleTab != -1) {
			tabbedPane.setSelectedIndex(visibleTab);
		}
		if (secondaryTab != -1) {
			advancedTab.setSelectedIndex(secondaryTab);
		}
	}
	
	public void showAnnotationPanel() {
		tabbedPane.setSelectedComponent(advancedPanel);
		advancedTab.setSelectedComponent(annotationPanel);
	}

	@Override
	public void refreshConfiguration() {
		refreshConfiguration(activity.getConfiguration());
	}

	static Pattern tagPattern = Pattern.compile("%%([^%]*)%%");

	@Override
	/**
	 * Need to check that the script contains the string replacements and only them - done
	 * 
	 * Need to check the input port names are valid and unique - done
	 * Need to check the output port names are valid and unique - done
	 * 
	 * Need to check the input files and static files are unique - done
	 * Need to check the file names are valid
	 * Need to check the URLs are valid
	 * Need to check the replacement tags are unique - done
	 */
	public boolean checkValues() {
		if (isFromRepository()) {
			return true;
		}
		boolean result = true;
		String text = "";
		Set<String> stringReplacementPortNames = new HashSet<String>();
		Set<String> stringReplacementTags = new HashSet<String>();
		for (ExternalToolStringReplacementViewer v : stringReplacementViewList) {
			String name = v.getName();
			if (name.equalsIgnoreCase("stdin") || name.equalsIgnoreCase("stdout") || name.equalsIgnoreCase("stderr")) {
				text += "A string replacement port has a reserved name \"" + name + "\"\n";
				result = false;
			}
			else if (stringReplacementPortNames.contains(name)) {
				text += "Two string replacement ports have the name \"" + name
						+ "\"\n";
				result = false;
			} else if (!name.matches(VALID_NAME_REGEX)) {
				text += "String replacement port name \"" + name
						+ "\" is invalid\n";
				result = false;
			} else {
				stringReplacementPortNames.add(name);
			}

			String tag = v.getValue();
			if (stringReplacementTags.contains(tag)) {
				text += "Two string replacement ports replace \"%%" + tag
						+ "%%\"\n";
				result = false;
			} else if (!tag.matches(VALID_NAME_REGEX)) {
				text += "String replacement tag \"%%" + tag
						+ "%%\" is invalid\n";
				result = false;
			} else {
				stringReplacementTags.add(tag);
			}
		}

		Matcher m = tagPattern.matcher(scriptTextArea.getText());
		Set<String> tags = new HashSet<String>();
		while (m.find()) {
			String tag = m.group(1);
			if (tag != null) {
				if (tag.isEmpty()) {
					text += "The command contains an empty tag i.e. %%%%\n";
					result = false;
				} else {
					if (!tag.matches(VALID_NAME_REGEX)) {
						text += "The command contains an invalid tag \"%%"
								+ tag + "\"%%\n";
						result = false;
					}
					if (!stringReplacementTags.contains(tag)) {
						text += "There is no string replacement for %%" + tag
								+ "%%\n";
						result = false;
					} else {
						tags.add(tag);
					}
				}
			}
		}

		for (String tag : stringReplacementTags) {
			if (!tags.contains(tag)) {
				text += "String replacement for %%" + tag
						+ "%% is not used in the command\n";
				result = false;
			}
		}

		Set<String> inputFilePortNames = new HashSet<String>();
		Set<String> inputFileNames = new HashSet<String>();
		for (ExternalToolFileViewer v : inputFileViewList) {
			String name = v.getName();
			if (name.equalsIgnoreCase("stdin") || name.equalsIgnoreCase("stdout") || name.equalsIgnoreCase("stderr")) {
				text += "An input file port has a reserved name \"" + name + "\"\n";
				result = false;
			}
			else if (stringReplacementPortNames.contains(name)) {
				text += "A string replacement port and an input file port have the name \""
						+ name + "\"\n";
				result = false;
			} else if (inputFilePortNames.contains(name)) {
				text += "Two file input ports have the name \"" + name + "\"\n";
				result = false;
			} else if (!name.matches(VALID_NAME_REGEX)) {
				text += "File input port name \"" + name + "\" is invalid\n";
				result = false;
			} else {
				inputFilePortNames.add(name);
			}

			String fileName = v.getValue();
			if (inputFileNames.contains(fileName)) {
				text += "Two file inputs ports write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else {
				inputFileNames.add(fileName);
			}
		}

		Set<String> fileListPortNames = new HashSet<String>();
		Set<String> fileListFileNames = new HashSet<String>();
		for (ExternalToolFileViewer v : fileListViewList) {
			String name = v.getName();
			if (name.equalsIgnoreCase("stdin") || name.equalsIgnoreCase("stdout") || name.equalsIgnoreCase("stderr")) {
				text += "A file list port has a reserved name \"" + name + "\"\n";
				result = false;
			} else if (stringReplacementPortNames.contains(name)) {
				text += "A string replacement port and a file list port have the name \""
						+ name + "\"\n";
				result = false;
			} else if (inputFilePortNames.contains(name)) {
				text += "A file input port and a file list port have the name \""
						+ name + "\"\n";
				result = false;
			} else if (fileListPortNames.contains(name)) {
				text += "Two file list ports have the name \"" + name + "\"\n";
				result = false;
			} else if (!name.matches(VALID_NAME_REGEX)) {
				text += "File list port name \"" + name + "\" is invalid\n";
				result = false;
			} else {
				fileListPortNames.add(name);
			}

			String fileName = v.getValue();
			if (fileListFileNames.contains(fileName)) {
				text += "Two file list ports write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else if (inputFileNames.contains(fileName)) {
				text += "A file input port and a file list port write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else {
				fileListFileNames.add(fileName);
			}
		}

		Set<String> staticStringFileNames = new HashSet<String>();
		for (ExternalToolStaticStringViewer v : staticStringViewList) {
			String fileName = v.getValue();
			if (staticStringFileNames.contains(fileName)) {
				text += "Two static strings write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else if (inputFileNames.contains(fileName)) {
				text += "A file input port and a static string write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else if (fileListFileNames.contains(fileName)) {
				text += "A file list port and a static string write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else {
				staticStringFileNames.add(fileName);
			}
		}

		Set<String> staticUrlFileNames = new HashSet<String>();
		for (ExternalToolStaticUrlViewer v : staticUrlViewList) {
			String fileName = v.getValue();
			if (staticUrlFileNames.contains(fileName)) {
				text += "Two static URLss write to the same file \"" + fileName
						+ "\"\n";
				result = false;
			} else if (inputFileNames.contains(fileName)) {
				text += "A file input port and a static URL write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else if (fileListFileNames.contains(fileName)) {
				text += "A file list port and a static URL write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else if (staticStringFileNames.contains(fileName)) {
				text += "A static string and a static URL write to the same file \""
						+ fileName + "\"\n";
				result = false;
			} else {
				staticUrlFileNames.add(fileName);
			}
		}
		Set<String> outputPortNames = new HashSet<String>();
		for (ExternalToolFileViewer v : outputViewList) {
			String name = v.getName();
			if (name.equalsIgnoreCase("stdin") || name.equalsIgnoreCase("stdout") || name.equalsIgnoreCase("stderr")) {
				text += "An output port has a reserved name \"" + name + "\"\n";
				result = false;
			} else if (outputPortNames.contains(name)) {
				text += "Two output file ports have the name \"" + name
						+ "\"\n";
				result = false;
			} else if (!name.matches(VALID_NAME_REGEX)) {
				text += "Output file port name \"" + name + "\" is invalid\n";
				result = false;
			} else {
				outputPortNames.add(name);
			}
		}
		if (!result) {
			JOptionPane.showMessageDialog(this, text, "Problems",
					JOptionPane.ERROR_MESSAGE);
		}
		return result;
	}

	/**
	 * Check the proposed port name against the set of ports
	 * 
	 * @return
	 */
	public boolean portNameExists(String name) {
		if (name.equalsIgnoreCase("stdin") || name.equalsIgnoreCase("stdout") || name.equalsIgnoreCase("stderr")) {
			return true;
		}
		
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


	public void setEditable(boolean editable, ExternalToolActivityConfigurationBean config) {
		ExternalToolActivityConfigurationBean newConfig = (ExternalToolActivityConfigurationBean) cloneBean(config);
		ExternalToolActivityHealthChecker.updateLocation(newConfig);
		newConfig.setEdited(editable);
		refreshConfiguration(newConfig);		
	}
	
	public void whenClosed() {
		if (invocationPanel != null) {
			invocationPanel.stopObserving();
		}
	}

}
