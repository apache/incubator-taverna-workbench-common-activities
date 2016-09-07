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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;
import org.apache.taverna.activities.externaltool.desc.ToolDescriptionParser;

import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.utils.Tools;
import org.apache.taverna.lang.ui.DeselectingButton;

/**
 * @author alanrw
 *
 */
public class EditablePanel extends JPanel {
	public EditablePanel(final ExternalToolConfigView view) {
		super(new FlowLayout());
		
		JButton update = new DeselectingButton("Update tool description",
				new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalToolActivityConfigurationBean bean = view.getConfiguration();
				String repositoryUrl = bean.getRepositoryUrl();
				String id = bean.getExternaltoolid();
				ToolDescription usecase = null;
				try {
					usecase = ToolDescriptionParser.readDescriptionFromUrl(
						repositoryUrl, id);
				}
				catch (IOException ex) {
					// Already logged
				}
				if (usecase != null) {
					bean.setToolDescription(usecase);
					view.refreshConfiguration(bean);
				} else {
					JOptionPane.showMessageDialog(view, "Unable to find tool description " + id, "Missing tool description", JOptionPane.ERROR_MESSAGE);
				}
			}});
		this.add(update);
		
		JButton makeEditable = new DeselectingButton("Edit tool description",
				new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExternalToolActivityConfigurationBean config = view.makeConfiguration();
				view.setEditable(true, config);
				
			}
		});
		makeEditable.setToolTipText("Edit the tool description");
		if (Tools.areAllUnderstood(view.getConfiguration().getToolDescription().getInputs())) {
		this.add(makeEditable);
		}
		
	}

}
