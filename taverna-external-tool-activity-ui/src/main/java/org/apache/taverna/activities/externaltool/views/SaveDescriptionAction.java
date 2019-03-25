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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.taverna.lang.ui.ExtensionFileFilter;
import org.apache.taverna.lang.ui.FileTools;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;
import org.apache.taverna.activities.externaltool.desc.ToolDescriptionParser;

final class SaveDescriptionAction extends AbstractAction {
	/**
	 * 
	 */
	private final ScriptPanel scriptPanel;
	private final ExternalToolConfigView view;
	
	private static Logger logger = Logger.getLogger(SaveDescriptionAction.class);

	private static XMLOutputter outputter = new XMLOutputter(Format
			.getPrettyFormat());

	SaveDescriptionAction(ScriptPanel scriptPanel, ExternalToolConfigView view) {
		this.scriptPanel = scriptPanel;
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		ToolDescription currentDescription = view.makeConfiguration().getToolDescription();
		String tooldescid = currentDescription.getTooldescid();
		String description = currentDescription.getDescription();
		String group = currentDescription.getGroup();
		if ((tooldescid == null) || tooldescid.isEmpty() || (description == null) || description.isEmpty() || (group == null) || group.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Please fill in the tool annotation and\nthen re-export the description", "Missing annotation", JOptionPane.PLAIN_MESSAGE, null);
			view.showAnnotationPanel();
		} else {
		saveStringToFile(this.scriptPanel,
				"Save tool description", ".xml", currentDescription);
		}
	}
	
	public static boolean saveStringToFile(Component parent, String dialogTitle, String extension, ToolDescription description) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(dialogTitle);

		fileChooser.resetChoosableFileFilters();
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		fileChooser.setFileFilter(new ExtensionFileFilter(new String[] { extension }));

		Preferences prefs = Preferences.userNodeForPackage(FileTools.class);
		String curDir = prefs
				.get("currentDir", System.getProperty("user.home"));
		fileChooser.setCurrentDirectory(new File(curDir));

		boolean tryAgain = true;
		while (tryAgain) {
			tryAgain = false;
			int returnVal = fileChooser.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				prefs.put("currentDir", fileChooser.getCurrentDirectory()
						.toString());
				File file = fileChooser.getSelectedFile();
				if (!file.getName().contains(".")) {
					String newName = file.getName() + extension;
					file = new File(file.getParentFile(), newName);
				}

				// TODO: Open in separate thread to avoid hanging UI
				try {
					List<ToolDescription> currentDescriptions;
					if (file.exists()) {
						currentDescriptions = ToolDescriptionParser.readDescriptionsFromStream(new FileInputStream(file));
					} else {
						currentDescriptions = new ArrayList<ToolDescription>();
					}
					Element overallElement = new Element("tooldescs");
					for (ToolDescription ud : currentDescriptions) {
						if (!ud.getTooldescid().equals(description.getTooldescid())) {
							overallElement.addContent(ud.writeToXMLElement());
						}
					}

					overallElement.addContent(description.writeToXMLElement());
					BufferedWriter out = new BufferedWriter(new FileWriter(file));
			        out.write(outputter.outputString(overallElement));
			        out.close();
					logger.info("Saved content by overwriting " + file);
					return true;
				} catch (IOException ex) {
					logger.warn("Could not save content to " + file, ex);
					JOptionPane.showMessageDialog(parent,
							"Could not save to " + file + ": \n\n"
									+ ex.getMessage(), "Warning",
							JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
		}
		return false;
	}
	
}