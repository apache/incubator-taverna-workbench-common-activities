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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.manager.AddInvocationMechanismAction;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroup;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupAddedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManagerListener;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupRemovedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerUI;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismEditor;
import net.sf.taverna.t2.activities.externaltool.manager.MechanismEditsPanel;
import net.sf.taverna.t2.lang.ui.FileTools;
import net.sf.taverna.t2.lang.ui.KeywordDocument;
import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;
import net.sf.taverna.t2.lang.ui.LinePainter;
import net.sf.taverna.t2.lang.ui.NoWrapEditorKit;
import net.sf.taverna.t2.lang.ui.ReadOnlyTextArea;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.thoughtworks.xstream.XStream;

import de.uni_luebeck.inb.knowarc.usecases.RuntimeEnvironmentConstraint;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputStatic;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

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
		ActivityConfigurationPanel<ExternalToolActivity, ExternalToolActivityConfigurationBean> implements InvocationGroupManagerListener {

	private static final String STRING_REPLACEMENT_DESCRIPTION = "You can use a string replacement to " +
			"feed data into the service via an input port and have that data replace part of the " +
			"command.";

	private static final String FILE_INPUT_DESCRIPTION = "You can use a file input to feed data into " +
			"the service via an input port and have that data written to the specified file.";

	private static final String FILE_OUTPUT_DESCRIPTION = "You can use a file output to take the " +
			"content of a file produced by the tool and send it to an output port of the service.";

	private static final String FILE_LIST_DESCRIPTION = "If you feed a list of data into a file list " +
			"input, then each data item is written to a temporary file. A file is produced containing " +
			"the names of those temporary file. That index file can then be used as part of the tool " +
			"command.";

	private static final String RUNTIME_ENVIRONMENT_DESCRIPTION = "A runtime environment can be used " +
			"to state that the machine on which the command is executed must have certain software " +
			"installed. You can specify a description of the software and what constraint the " +
			"installed software must satisfy.";

	private static final String STATIC_STRING_DESCRIPTION = "A static string can be used to write a constant string to the specified file.";

	private static final String STATIC_URL_DESCRIPTION = "A static URL can be used to download data from a URL and store it in the specified file.";

	private static final String VALID_NAME_REGEX = "[\\p{L}\\p{Digit}_]+";

	private static final String LOCATION_DESCRIPTION = "The location specifies where the tool will be run.";

	private static Logger logger = Logger
			.getLogger(ExternalToolConfigView.class);

	/** The activity which this view describes */
	protected ExternalToolActivity activity;

	/** The configuration bean used to configure the activity */
	private ExternalToolActivityConfigurationBean configuration;

	private JTabbedPane tabbedPane = null;

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

	private List<ExternalToolRuntimeEnvironmentViewer> runtimeEnvironmentViewList = new ArrayList<ExternalToolRuntimeEnvironmentViewer>();

	private JComboBox invocationSelection = new JComboBox();

	private JTextField nameField = new JTextField(20);
	private JTextArea descriptionArea = new JTextArea(6, 40);

	private JEditorPane scriptTextArea;

	private JPanel invocationPanel;

	private static SAXBuilder builder = new SAXBuilder();

	private JCheckBox stdInCheckBox = new JCheckBox("Show STDIN");
	private JCheckBox stdOutCheckBox = new JCheckBox("Show STDOUT");
	private JCheckBox stdErrCheckBox = new JCheckBox("Show STDERR");
	
	private DefaultComboBoxModel invocationSelectionModel = new DefaultComboBoxModel();

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
		configuration = (ExternalToolActivityConfigurationBean) cloneBean(activity
				.getConfiguration());
		setLayout(new GridBagLayout());
		populateGroupList();
		invocationSelection.setModel(invocationSelectionModel);
		InvocationGroupManager.getInstance().addListener(this);
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

		if (!isFromRepository()) {
			UseCaseDescription useCaseDescription = configuration
					.getUseCaseDescription();

			nameField.setText(useCaseDescription.getUsecaseid());
			descriptionArea.setText(useCaseDescription.getDescription());
			stringReplacementViewList = new ArrayList<ExternalToolStringReplacementViewer>();
			inputFileViewList = new ArrayList<ExternalToolFileViewer>();
			fileListViewList = new ArrayList<ExternalToolFileViewer>();
			outputViewList = new ArrayList<ExternalToolFileViewer>();
			staticUrlViewList = new ArrayList<ExternalToolStaticUrlViewer>();
			staticStringViewList = new ArrayList<ExternalToolStaticStringViewer>();
			runtimeEnvironmentViewList = new ArrayList<ExternalToolRuntimeEnvironmentViewer>();

			for (Entry<String, ScriptInput> entry : useCaseDescription
					.getInputs().entrySet()) {
				String name = entry.getKey();
				ScriptInputUser si = (ScriptInputUser) entry.getValue();
				if (!si.isList() && !si.isFile() && !si.isTempFile()) {
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

			for (Entry<String, ScriptInput> entry : useCaseDescription
					.getInputs().entrySet()) {
				String name = entry.getKey();
				ScriptInputUser si = (ScriptInputUser) entry.getValue();
				if (!si.isList() && si.isFile()) {
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

			for (Entry<String, ScriptInput> entry : useCaseDescription
					.getInputs().entrySet()) {
				String name = entry.getKey();
				ScriptInputUser si = (ScriptInputUser) entry.getValue();
				if (si.isList() && si.isFile()) {
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

			for (Entry<String, ScriptOutput> entry : useCaseDescription
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

			for (ScriptInputStatic siss : useCaseDescription.getStatic_inputs()) {
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

			for (ScriptInputStatic sis : useCaseDescription.getStatic_inputs()) {
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

			for (RuntimeEnvironmentConstraint rec : useCaseDescription.getREs()) {
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
					});

			scriptTextArea = new JTextPane();
			new LinePainter(scriptTextArea);

			final KeywordDocument doc = new KeywordDocument(
					new HashSet<String>());
			// NOTE: Due to T2-1145 - always set editor kit BEFORE setDocument
			scriptTextArea.setEditorKit(new NoWrapEditorKit());
			scriptTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
			scriptTextArea.setDocument(doc);
			scriptTextArea.setText(useCaseDescription.getCommand());
			scriptTextArea.setCaretPosition(0);
			scriptTextArea.setPreferredSize(new Dimension(200, 100));

			tabbedPane.addTab("Command", createScriptPanel());
			tabbedPane.addTab("String replacements",
					createStringReplacementPanel());
			tabbedPane.addTab(
					"File inputs",
					createFilePanel(inputFileViewList, "To file", "File type",
							"in", FILE_INPUT_DESCRIPTION));
			tabbedPane.addTab(
					"File outputs",
					createFilePanel(outputViewList, "From file", "File type",
							"out", FILE_OUTPUT_DESCRIPTION));
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
			advancedTab.addTab(
					"File lists",
					createFilePanel(fileListViewList,
							"To file containing list", "Individual file type",
							"in", FILE_LIST_DESCRIPTION));
			advancedTab.addTab("Static strings", createStaticStringPanel());
			advancedTab.addTab("Static URLs", createStaticUrlPanel());
			advancedTab.addTab("Runtime environments",
					createRuntimeEnvironmentPanel(runtimeEnvironmentViewList));
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

		setPreferredSize(new Dimension(700, 500));
		this.validate();
	}

	public void whenOpened() {
		if (scriptTextArea != null) {
			scriptTextArea.requestFocus();
		}
	}

	private boolean isFromRepository() {
		String repositoryUrl = this.configuration.getRepositoryUrl();
		return (!this.configuration.isEdited() && (repositoryUrl != null) && !repositoryUrl
				.isEmpty());
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
			}
		});
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
		scriptEditPanel.add(new LineEnabledTextPanel(scriptTextArea),
				BorderLayout.CENTER);

		JButton loadScriptButton = new JButton("Load description");
		loadScriptButton.setToolTipText("Load tool description from a file");
		loadScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newScript = FileTools.readStringFromFile(
						ExternalToolConfigView.this, "Load tool description",
						".xml");
				if (newScript != null) {
					String errorMessage = null;
					try {
						Document doc = builder
								.build(new StringReader(newScript));
						UseCaseDescription newDescription = new UseCaseDescription(
								doc.getRootElement());
						configuration.setUseCaseDescription(newDescription);
						refreshConfiguration(configuration);
					} catch (JDOMException e1) {
						errorMessage = e1.getMessage();
					} catch (IOException e1) {
						errorMessage = e1.getMessage();
					} catch (Exception e1) {
						errorMessage = e1.getMessage();
					}
					if (errorMessage != null) {
						JOptionPane.showMessageDialog(null, errorMessage,
								"Tool description load error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		JButton saveScriptButton = new JButton("Save description");
		saveScriptButton.setToolTipText("Save the tool description to a file");
		saveScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLOutputter outputter = new XMLOutputter(Format
						.getPrettyFormat());
				FileTools.saveStringToFile(ExternalToolConfigView.this,
						"Save tool description", ".xml", outputter
								.outputString(makeConfiguration()
										.getUseCaseDescription()
										.writeToXMLElement()));
			}
		});

		JButton clearScriptButton = new JButton("Clear script");
		clearScriptButton.setToolTipText("Clear the script from the edit area");
		clearScriptButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clearScript();
			}

		});

		stdInCheckBox.setSelected(configuration.getUseCaseDescription()
				.isIncludeStdIn());
		stdOutCheckBox.setSelected(configuration.getUseCaseDescription()
				.isIncludeStdOut());
		stdErrCheckBox.setSelected(configuration.getUseCaseDescription()
				.isIncludeStdErr());

		JPanel streamPanel = new JPanel();
		streamPanel.setLayout(new FlowLayout());
		streamPanel.add(stdInCheckBox);
		streamPanel.add(stdOutCheckBox);
		streamPanel.add(stdErrCheckBox);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(loadScriptButton);
		buttonPanel.add(saveScriptButton);
		buttonPanel.add(clearScriptButton);

		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(streamPanel, BorderLayout.NORTH);
		subPanel.add(buttonPanel, BorderLayout.SOUTH);

		scriptEditPanel.add(subPanel, BorderLayout.SOUTH);

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
		outerInputPanel.setLayout(new BorderLayout());

		final JPanel inputEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints inputConstraint = new GridBagConstraints();

		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputEditPanel.add(new JLabel("Taverna port name"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("String to replace"), inputConstraint);

		inputConstraint.gridx = 0;
		synchronized (stringReplacementViewList) {
			for (ExternalToolStringReplacementViewer inputView : stringReplacementViewList) {
				addStringReplacementViewer(outerInputPanel, inputEditPanel,
						inputView);

			}
		}

		JTextArea descriptionText = new JTextArea(
				STRING_REPLACEMENT_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);

		outerInputPanel.add(descriptionText, BorderLayout.NORTH);
		outerInputPanel.add(new JScrollPane(inputEditPanel),
				BorderLayout.CENTER);
		JButton addInputPortButton = new JButton(new AbstractAction() {

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

				ExternalToolStringReplacementViewer newViewer = new ExternalToolStringReplacementViewer(
						name2);
				synchronized (stringReplacementViewList) {
					stringReplacementViewList.add(newViewer);
					addStringReplacementViewer(outerInputPanel, inputEditPanel,
							newViewer);
					inputEditPanel.revalidate();
					inputEditPanel.repaint();
				}

			}

		});

		addInputPortButton.setText("Add string replacement");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		buttonPanel.add(addInputPortButton, BorderLayout.EAST);

		outerInputPanel.add(buttonPanel, BorderLayout.SOUTH);

		return outerInputPanel;
	}

	private void addStringReplacementViewer(final JPanel outerPanel,
			final JPanel panel, ExternalToolStringReplacementViewer viewer) {
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
		panel.add(valueField, inputConstraint);
		inputConstraint.gridx++;

		final JButton removeButton = new JButton("Remove");
		final ExternalToolStringReplacementViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (stringReplacementViewList) {
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
		inputConstraint.gridy = ++stringReplacementGridy;

	}

	private JPanel createInvocationPanel() {
		invocationPanel = new JPanel();
		populateInvocationPanel();

		return invocationPanel;
	}

	private void populateInvocationPanel() {
		invocationPanel.removeAll();
		invocationPanel.setLayout(new BorderLayout());

		JTextArea descriptionText = new JTextArea(
				LOCATION_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		invocationPanel.add(descriptionText, BorderLayout.NORTH);

		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel invocationSelectionPanel = new JPanel(new GridLayout(1, 2));
		JLabel selectionLabel = new JLabel("Select a location:",
				SwingConstants.RIGHT);
		selectionLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
		invocationSelectionPanel.add(selectionLabel);

		invocationSelection.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList arg0,
					Object arg1, int arg2, boolean arg3, boolean arg4) {
				if (arg1 instanceof InvocationGroup) {
					return super.getListCellRendererComponent(arg0,
							((InvocationGroup) arg1).getInvocationGroupName(),
							arg2, arg3, arg4);
				}
				return super.getListCellRendererComponent(arg0, arg1, arg2,
						arg3, arg4);
			}
		});

		invocationSelectionPanel.add(invocationSelection);

		subPanel.add(invocationSelectionPanel, BorderLayout.NORTH);
		logger.info("used group is "
				+ configuration.getInvocationGroup().hashCode());
		invocationSelection.setSelectedItem(configuration.getInvocationGroup());

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton addLocation = new JButton(
				new AddInvocationMechanismAction(true) {
					public void actionPerformed(ActionEvent e) {
						super.actionPerformed(e);
						if (getNewGroup() != null) {
							invocationSelection.setSelectedItem(getNewGroup());
							InvocationMechanismEditor chosenEditor = null;
							for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry
									.getInstances()) {
								if (ime.canShow(getNewMechanism().getClass())) {
									chosenEditor = ime;
									break;
								}
							}
							if (chosenEditor != null) {
								chosenEditor.show(getNewMechanism());
								chosenEditor.setPreferredSize(new Dimension(
										550, 500));
								int answer = JOptionPane.showConfirmDialog(
										null, chosenEditor, "New location",
										JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
								if (answer == JOptionPane.OK_OPTION) {
									chosenEditor.updateInvocationMechanism();
									InvocationGroupManager
											.getInstance()
											.mechanismChanged(getNewMechanism());
								}
							}
						}
					}
				});
/*		JButton manageInvocation = new JButton(new AbstractAction(
				"Edit locations") {

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationManagerUI ui = InvocationManagerUI.getInstance();
				ui.showMechanismForGroup((InvocationGroup) invocationSelection
						.getSelectedItem());
				ui.setVisible(true);
			}
		}); */
		buttonPanel.add(addLocation);
/*		buttonPanel.add(manageInvocation); */
		subPanel.add(buttonPanel, BorderLayout.SOUTH);
		invocationPanel.add(subPanel, BorderLayout.CENTER);
		invocationPanel.repaint();
	}

	@Override
	public ExternalToolActivityConfigurationBean getConfiguration() {
		return configuration;
	}

	public void refreshConfiguration(
			ExternalToolActivityConfigurationBean config) {
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
			if (stringReplacementPortNames.contains(name)) {
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
				text += "String replacement tag \"%%" + name
						+ "%%\" is invalid\n";
				result = false;
			} else {
				stringReplacementTags.add(name);
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
			if (stringReplacementPortNames.contains(name)) {
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
			if (stringReplacementPortNames.contains(name)) {
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
			if (outputPortNames.contains(name)) {
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

	private JPanel createFilePanel(final List<ExternalToolFileViewer> viewList,
			String fileHeader, String typeHeader, final String portPrefix,
			final String description) {
		final JPanel outerFilePanel = new JPanel();
		outerFilePanel.setLayout(new BorderLayout());
		final JPanel fileEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints fileConstraint = new GridBagConstraints();
		fileConstraint.insets = new Insets(5, 5, 5, 5);
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
		synchronized (viewList) {
			for (ExternalToolFileViewer outputView : viewList) {
				addFileViewer(viewList, outerFilePanel, fileEditPanel,
						outputView);
			}
		}
		JButton addFilePortButton = new JButton(new AbstractAction() {
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

				ExternalToolFileViewer newViewer = new ExternalToolFileViewer(
						name2);
				synchronized (viewList) {
					viewList.add(newViewer);
					addFileViewer(viewList, outerFilePanel, fileEditPanel,
							newViewer);
					fileEditPanel.revalidate();
					fileEditPanel.repaint();
				}
			}

		});
		JTextArea descriptionText = new ReadOnlyTextArea(description);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));

		outerFilePanel.add(descriptionText, BorderLayout.NORTH);

		outerFilePanel.add(new JScrollPane(fileEditPanel), BorderLayout.CENTER);

		addFilePortButton.setText("Add Port");
		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addFilePortButton, BorderLayout.EAST);

		outerFilePanel.add(buttonPanel, BorderLayout.SOUTH);

		return outerFilePanel;
	}

	private void addFileViewer(final List<ExternalToolFileViewer> viewList,
			final JPanel outerPanel, final JPanel panel,
			ExternalToolFileViewer viewer) {
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
		panel.add(valueField, fileConstraint);
		fileConstraint.gridx++;

		final JComboBox typeSelector = viewer.getTypeSelector();
		panel.add(typeSelector, fileConstraint);

		fileConstraint.gridx++;
		final JButton removeButton = new JButton("Remove");
		final ExternalToolFileViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (viewList) {
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

	private JPanel createRuntimeEnvironmentPanel(
			final List<ExternalToolRuntimeEnvironmentViewer> viewList) {
		final JPanel outerREPanel = new JPanel(new BorderLayout());
		final JPanel runtimeEnvironmentEditPanel = new JPanel(
				new GridBagLayout());

		final GridBagConstraints reConstraint = new GridBagConstraints();
		reConstraint.insets = new Insets(5, 5, 5, 5);
		reConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		reConstraint.gridx = 0;
		reConstraint.gridy = 0;
		reConstraint.weightx = 0.1;
		reConstraint.fill = GridBagConstraints.BOTH;

		runtimeEnvironmentEditPanel.add(new JLabel("Environment description"),
				reConstraint);
		reConstraint.gridx++;
		runtimeEnvironmentEditPanel.add(new JLabel("Relationship"),
				reConstraint);
		reConstraint.gridx++;

		reConstraint.gridx = 0;
		synchronized (viewList) {
			for (ExternalToolRuntimeEnvironmentViewer outputView : viewList) {
				addEnvironmentViewer(viewList, outerREPanel,
						runtimeEnvironmentEditPanel, outputView);
			}
		}

		JTextArea descriptionText = new ReadOnlyTextArea(
				RUNTIME_ENVIRONMENT_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));

		outerREPanel.add(descriptionText, BorderLayout.NORTH);

		outerREPanel.add(new JScrollPane(runtimeEnvironmentEditPanel),
				BorderLayout.CENTER);

		JButton addRuntimeEnvironmentButton = new JButton(new AbstractAction() {
			// FIXME refactor this into a method
			public void actionPerformed(ActionEvent e) {

				ExternalToolRuntimeEnvironmentViewer newViewer = new ExternalToolRuntimeEnvironmentViewer();
				synchronized (viewList) {
					viewList.add(newViewer);
					addEnvironmentViewer(viewList, outerREPanel,
							runtimeEnvironmentEditPanel, newViewer);
					runtimeEnvironmentEditPanel.revalidate();
					runtimeEnvironmentEditPanel.repaint();
				}
			}

		});
		addRuntimeEnvironmentButton.setText("Add runtime environment");
		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addRuntimeEnvironmentButton, BorderLayout.EAST);

		outerREPanel.add(buttonPanel, BorderLayout.SOUTH);

		return outerREPanel;
	}

	private void addEnvironmentViewer(
			final List<ExternalToolRuntimeEnvironmentViewer> viewList,
			final JPanel outerPanel, final JPanel panel,
			ExternalToolRuntimeEnvironmentViewer viewer) {
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
		panel.add(relationSelector, fileConstraint);

		fileConstraint.gridx++;
		final JButton removeButton = new JButton("Remove");
		final ExternalToolRuntimeEnvironmentViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (viewList) {
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
		final JPanel outerStaticPanel = new JPanel(new BorderLayout());
		final JPanel staticEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints staticConstraint = new GridBagConstraints();
		staticConstraint.insets = new Insets(5, 5, 5, 5);
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
		synchronized (staticUrlViewList) {
			for (ExternalToolStaticUrlViewer staticView : staticUrlViewList) {
				addStaticUrlViewer(outerStaticPanel, staticEditPanel,
						staticView);
			}
		}

		outerStaticPanel.add(new JScrollPane(staticEditPanel),
				BorderLayout.CENTER);

		JTextArea descriptionText = new ReadOnlyTextArea(
				STATIC_URL_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));

		outerStaticPanel.add(descriptionText, BorderLayout.NORTH);

		JButton addstaticPortButton = new JButton(new AbstractAction() {
			// FIXME refactor this into a method
			public void actionPerformed(ActionEvent e) {

				ExternalToolStaticUrlViewer newViewer = new ExternalToolStaticUrlViewer();
				synchronized (staticUrlViewList) {
					staticUrlViewList.add(newViewer);
					addStaticUrlViewer(outerStaticPanel, staticEditPanel,
							newViewer);
					staticEditPanel.revalidate();
					staticEditPanel.repaint();
				}
			}

		});
		addstaticPortButton.setText("Add Static");
		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addstaticPortButton, BorderLayout.EAST);

		outerStaticPanel.add(buttonPanel, BorderLayout.SOUTH);

		return outerStaticPanel;
	}

	private void addStaticUrlViewer(final JPanel outerPanel,
			final JPanel panel, ExternalToolStaticUrlViewer viewer) {
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
		panel.add(valueField, staticConstraint);

		staticConstraint.gridx++;

		final JButton removeButton = new JButton("Remove");
		final ExternalToolStaticUrlViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (staticUrlViewList) {
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
		final JPanel outerStaticPanel = new JPanel(new BorderLayout());
		final JPanel staticEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints staticConstraint = new GridBagConstraints();
		staticConstraint.insets = new Insets(5, 5, 5, 5);
		staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		staticConstraint.gridx = 0;
		staticConstraint.gridy = 0;
		staticConstraint.weightx = 0.1;
		staticConstraint.fill = GridBagConstraints.BOTH;

		staticEditPanel.add(new JLabel("String to copy"), staticConstraint);
		staticConstraint.gridx++;
		staticEditPanel.add(new JLabel("To file"), staticConstraint);

		staticConstraint.gridx = 0;
		synchronized (staticUrlViewList) {
			for (ExternalToolStaticStringViewer staticView : staticStringViewList) {
				addStaticStringViewer(outerStaticPanel, staticEditPanel,
						staticView);
			}
		}

		JTextArea descriptionText = new ReadOnlyTextArea(
				STATIC_STRING_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		outerStaticPanel.add(descriptionText, BorderLayout.NORTH);

		outerStaticPanel.add(new JScrollPane(staticEditPanel),
				BorderLayout.CENTER);
		JButton addStaticStringButton = new JButton(new AbstractAction() {
			// FIXME refactor this into a method
			public void actionPerformed(ActionEvent e) {

				ExternalToolStaticStringViewer newViewer = new ExternalToolStaticStringViewer();
				synchronized (staticUrlViewList) {
					staticStringViewList.add(newViewer);
					addStaticStringViewer(outerStaticPanel, staticEditPanel,
							newViewer);
					staticEditPanel.revalidate();
					staticEditPanel.repaint();
				}
			}

		});
		addStaticStringButton.setText("Add static string");
		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addStaticStringButton, BorderLayout.EAST);

		outerStaticPanel.add(buttonPanel, BorderLayout.SOUTH);

		return outerStaticPanel;
	}

	private void addStaticStringViewer(final JPanel outerPanel,
			final JPanel panel, ExternalToolStaticStringViewer viewer) {
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
		panel.add(valuePanel, staticConstraint);

		staticConstraint.gridx++;

		final JPanel removePanel = new JPanel();
		removePanel.setLayout(new BorderLayout());
		final JButton removeButton = new JButton("Remove");
		removePanel.add(removeButton, BorderLayout.NORTH);
		final ExternalToolStaticStringViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (staticStringViewList) {
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
	
	private void populateGroupList() {
		InvocationGroup currentSelection = (InvocationGroup) invocationSelection.getSelectedItem();
		InvocationGroup[] groups = InvocationGroupManager.getInstance()
				.getInvocationGroups().toArray(new InvocationGroup[] {});
		Arrays.sort(groups, new Comparator<InvocationGroup>() {

			@Override
			public int compare(InvocationGroup arg0, InvocationGroup arg1) {
				return arg0.getInvocationGroupName().compareTo(
						arg1.getInvocationGroupName());
			}
		});
		invocationSelectionModel.removeAllElements();
		for (InvocationGroup group : groups) {
			invocationSelectionModel.addElement(group);
		}
		if (currentSelection != null) {
			invocationSelection.setSelectedItem(currentSelection);
		}
		
	}

	@Override
	public void invocationManagerChange(InvocationManagerEvent event) {
		if (event instanceof InvocationGroupRemovedEvent) {
			InvocationGroup removedGroup = ((InvocationGroupRemovedEvent) event).getRemovedGroup();
			if (invocationSelection.getSelectedItem().equals(removedGroup)) {
				invocationSelection.setSelectedItem(InvocationGroupManager.getInstance().getDefaultGroup());
			}
			invocationSelectionModel.removeElement(removedGroup);
		} else if (event instanceof InvocationGroupAddedEvent) {
			populateGroupList();
		}
	}

}
