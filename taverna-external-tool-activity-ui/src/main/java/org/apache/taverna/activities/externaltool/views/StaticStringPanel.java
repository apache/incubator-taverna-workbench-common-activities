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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.taverna.activities.externaltool.utils.Tools;
import org.apache.taverna.lang.ui.DeselectingButton;
import org.apache.taverna.lang.ui.ReadOnlyTextArea;

/**
 * @author alanrw
 *
 */
public class StaticStringPanel extends JPanel {
	
	private static final String STATIC_STRING_DESCRIPTION = "A fixed string can be written to the specified file.";
	private final List<ExternalToolStaticStringViewer> staticStringViewList;
	
	int staticGridy = 1;
	
	private static String[] elementLabels = new String[] {"String to copy", "To file"};
	
	public StaticStringPanel(final List<ExternalToolStaticStringViewer> staticStringViewList) {
		super(new BorderLayout());
		this.staticStringViewList = staticStringViewList;
		final JPanel staticEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints staticConstraint = new GridBagConstraints();
		staticConstraint.insets = new Insets(5, 5, 5, 5);
		staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		staticConstraint.gridx = 0;
		staticConstraint.gridy = 0;
		staticConstraint.weightx = 0.1;
		staticConstraint.fill = GridBagConstraints.BOTH;

		staticConstraint.gridx = 0;
		synchronized (staticStringViewList) {
			for (ExternalToolStaticStringViewer staticView : staticStringViewList) {
				addStaticStringViewer(StaticStringPanel.this, staticEditPanel,
						staticView);
			}
		}

		JTextArea descriptionText = new ReadOnlyTextArea(
				STATIC_STRING_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		this.add(descriptionText, BorderLayout.NORTH);

		this.add(new JScrollPane(staticEditPanel),
				BorderLayout.CENTER);
		JButton addStaticStringButton = new DeselectingButton("Add string",
				new AbstractAction() {
			// FIXME refactor this into a method
			public void actionPerformed(ActionEvent e) {

				ExternalToolStaticStringViewer newViewer = new ExternalToolStaticStringViewer();
				synchronized (staticStringViewList) {
					staticStringViewList.add(newViewer);
					addStaticStringViewer(StaticStringPanel.this, staticEditPanel,
							newViewer);
					staticEditPanel.revalidate();
					staticEditPanel.repaint();
				}
			}

		});

		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addStaticStringButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
	
	}
	
	private void addStaticStringViewer(final JPanel outerPanel,
			final JPanel panel, ExternalToolStaticStringViewer viewer) {
		Tools.addViewer(panel,
				elementLabels,
				new JComponent[] {new JScrollPane(viewer.getContentField()), viewer.getValueField()},
				staticStringViewList,
				viewer,
				outerPanel);
	}


}
