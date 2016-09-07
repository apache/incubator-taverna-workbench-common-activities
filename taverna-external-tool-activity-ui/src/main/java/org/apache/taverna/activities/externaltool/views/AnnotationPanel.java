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
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author alanrw
 *
 */
public class AnnotationPanel extends JPanel {
	
	public AnnotationPanel(Component nameField, Component descriptionArea, Component groupField) {
		super();
		this.setLayout(new BorderLayout());
		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		namePanel.add(new JLabel("Name: "));
		namePanel.add(nameField);
		subPanel.add(namePanel, BorderLayout.NORTH);
		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		groupPanel.add(new JLabel("Group: "));
		groupPanel.add(groupField);
		subPanel.add(groupPanel, BorderLayout.SOUTH);
		this.add(subPanel, BorderLayout.NORTH);
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BorderLayout());
		descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
		descriptionPanel.add(descriptionArea, BorderLayout.CENTER);
		this.add(descriptionPanel, BorderLayout.CENTER);
	}

}
