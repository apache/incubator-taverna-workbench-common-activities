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

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.taverna.lang.ui.FileTools;

import org.jdom.Document;
import org.jdom.JDOMException;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;
import org.apache.taverna.activities.externaltool.desc.ToolDescriptionParser;

final class LoadDescriptionAction extends AbstractAction {
	/**
	 * 
	 */
	private final ScriptPanel scriptPanel;
	private final ExternalToolConfigView view;

	LoadDescriptionAction(ScriptPanel scriptPanel, ExternalToolConfigView view) {
		this.scriptPanel = scriptPanel;
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		String descriptionsString = FileTools.readStringFromFile(
				this.scriptPanel, "Load tool description",
				".xml");
		if (descriptionsString != null) {
			String errorMessage = null;
			try {
				Document doc = ScriptPanel.builder
						.build(new StringReader(descriptionsString));
				List<ToolDescription> descriptions = ToolDescriptionParser.readDescriptionsFromStream(new StringBufferInputStream(descriptionsString));
				if (descriptions.isEmpty()) {
					JOptionPane.showMessageDialog(this.scriptPanel, "No tool descriptions found", "File content", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (descriptions.size() == 1) {
					view.getConfiguration().setToolDescription(descriptions.get(0));
					view.refreshConfiguration(view.getConfiguration());
					return;
				}
				
				List<String> descriptionNames = new ArrayList();
				for (ToolDescription ud : descriptions) {
					descriptionNames.add(ud.getTooldescid());
				}
				Collections.sort(descriptionNames);
				String chosenName = (String) JOptionPane.showInputDialog(this.scriptPanel, "Please select a tool description",
						"Select tool description", JOptionPane.PLAIN_MESSAGE, null, descriptionNames.toArray(), descriptionNames.get(0));
				if (chosenName != null) {
					for (ToolDescription ud : descriptions) {
						if (ud.getTooldescid().equals(chosenName)) {
							view.getConfiguration().setToolDescription(ud);
							view.refreshConfiguration(view.getConfiguration());
							return;
							
						}
					}
				}
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
}