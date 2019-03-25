/**
 * 
 */
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.lang.ui.DeselectingButton;
import org.apache.taverna.lang.ui.LineEnabledTextPanel;

import org.jdom.input.SAXBuilder;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;
import org.apache.taverna.activities.externaltool.desc.ToolDescriptionParser;


/**
 * @author alanrw
 *
 */
public class ScriptPanel extends JPanel {
	
	private static final String SCRIPT_DESCRIPTION = "Specify the commands that you want to run. You can use data arriving at an input port to replace parts of the command or to write to a file. You can also take data written to a file and send it to an output port.";
	static SAXBuilder builder = new SAXBuilder();
	private final JTextComponent scriptTextArea;
	
	public ScriptPanel(final ExternalToolConfigView view, JTextComponent scriptTextArea, JCheckBox stdInCheckBox, JCheckBox stdOutCheckBox, JCheckBox stdErrCheckBox, JTextField returnCodesField) {
		super();
		this.setLayout(new BorderLayout());
	
		JTextArea descriptionText = new JTextArea(
				SCRIPT_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		this.add(descriptionText, BorderLayout.NORTH);

		this.scriptTextArea = scriptTextArea;
		
		this.add(new LineEnabledTextPanel(scriptTextArea),
				BorderLayout.CENTER);
		

		ToolDescription toolDescription = view.getConfiguration().getToolDescription();
		stdInCheckBox.setSelected(toolDescription.isIncludeStdIn());
		stdOutCheckBox.setSelected(toolDescription.isIncludeStdOut());
		stdErrCheckBox.setSelected(toolDescription.isIncludeStdErr());
		returnCodesField.setText(toolDescription.getReturnCodesAsText());
		
		JPanel codesPanel = new JPanel(new FlowLayout());
		codesPanel.add(new JLabel("Valid return codes:"));
		codesPanel.add(returnCodesField);

		JPanel streamPanel = new JPanel(new FlowLayout());
		streamPanel.add(stdInCheckBox);
		streamPanel.add(stdOutCheckBox);
		streamPanel.add(stdErrCheckBox);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		if (view.isOriginallyFromRepository()) {
			JButton revertButton = new DeselectingButton("Revert to original description",
					new AbstractAction(){

				@Override
				public void actionPerformed(ActionEvent e) {
					ExternalToolActivityConfigurationBean bean = view.makeConfiguration();
					String repositoryUrl = bean.getRepositoryUrl();
					String id = bean.getExternaltoolid();
					ToolDescription tooldesc = null;
					try {
						tooldesc = ToolDescriptionParser.readDescriptionFromUrl(
							repositoryUrl, id);
					}
					catch (IOException ex) {
						// Already logged
					}
					if (tooldesc != null) {
						bean.setToolDescription(tooldesc);
						view.setEditable(false, bean);
					} else {
						JOptionPane.showMessageDialog(view, "Unable to find tool description " + id, "Missing tool description", JOptionPane.ERROR_MESSAGE);
					}
				}});
			revertButton.setToolTipText("Revert to the tool description from the repository");
			buttonPanel.add(revertButton);
		}
		JButton loadScriptButton = new DeselectingButton("Load description",
				new LoadDescriptionAction(this, view));
		loadScriptButton.setToolTipText("Load tool description from a file");

		JButton saveScriptButton = new DeselectingButton("Export description",
				new SaveDescriptionAction(this, view));
		saveScriptButton.setToolTipText("Export the tool description to a file");

		JButton clearScriptButton = new DeselectingButton("Clear script",
				new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				clearScript();
			}

		});
		clearScriptButton.setToolTipText("Clear the script from the edit area");

		buttonPanel.add(loadScriptButton);
		buttonPanel.add(saveScriptButton);
		buttonPanel.add(clearScriptButton);

		JPanel subPanel = new JPanel(new GridLayout(3,1));
		subPanel.add(codesPanel);
		subPanel.add(streamPanel);
		subPanel.add(buttonPanel);

		this.add(subPanel, BorderLayout.SOUTH);
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

}
