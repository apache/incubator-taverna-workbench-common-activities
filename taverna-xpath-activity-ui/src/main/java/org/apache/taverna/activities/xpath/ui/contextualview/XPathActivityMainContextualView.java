package org.apache.taverna.activities.xpath.ui.contextualview;
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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.taverna.activities.xpath.ui.config.XPathActivityConfigureAction;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.views.contextualviews.ContextualView;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
@SuppressWarnings("serial")
public class XPathActivityMainContextualView extends ContextualView {

	private final Scufl2Tools scufl2Tools = new Scufl2Tools();

	private XPathActivityMainContextualView thisContextualView;

	private final Activity activity;

	private JPanel jpMainPanel;
	private JTextField tfXPathExpression;

	private DefaultTableModel xpathNamespaceMappingsTableModel;
	private JTable jtXPathNamespaceMappings;
	private JScrollPane spXPathNamespaceMappings;
	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;
	private final ServiceRegistry serviceRegistry;

	public XPathActivityMainContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry, ServiceRegistry serviceRegistry) {
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.serviceRegistry = serviceRegistry;
		this.thisContextualView = this;
		this.activity = activity;
		initView();
	}

	@Override
	public JComponent getMainFrame() {
		jpMainPanel = new JPanel(new GridBagLayout());
		jpMainPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(4, 2, 4, 2), BorderFactory.createEmptyBorder()));

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.weighty = 0;

		// --- XPath Expression ---

		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		JLabel jlXPathExpression = new JLabel("XPath Expression:");
		jlXPathExpression.setFont(jlXPathExpression.getFont().deriveFont(Font.BOLD));
		jpMainPanel.add(jlXPathExpression, c);

		c.gridx++;
		c.weightx = 1.0;
		tfXPathExpression = new JTextField();
		tfXPathExpression.setEditable(false);
		jpMainPanel.add(tfXPathExpression, c);

		// --- Label to Show/Hide Mapping Table ---

		final JLabel jlShowHideNamespaceMappings = new JLabel("Show namespace mappings...");
		jlShowHideNamespaceMappings.setForeground(Color.BLUE);
		jlShowHideNamespaceMappings.setCursor(new Cursor(Cursor.HAND_CURSOR));
		jlShowHideNamespaceMappings.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				spXPathNamespaceMappings.setVisible(!spXPathNamespaceMappings.isVisible());
				jlShowHideNamespaceMappings.setText((spXPathNamespaceMappings.isVisible() ? "Hide"
						: "Show") + " namespace mappings...");
				thisContextualView.revalidate();
			}
		});

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		jpMainPanel.add(jlShowHideNamespaceMappings, c);

		// --- Namespace Mapping Table ---

		xpathNamespaceMappingsTableModel = new DefaultTableModel() {
			/**
			 * No cells should be editable
			 */
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return (false);
			}
		};
		xpathNamespaceMappingsTableModel.addColumn("Namespace Prefix");
		xpathNamespaceMappingsTableModel.addColumn("Namespace URI");

		jtXPathNamespaceMappings = new JTable();
		jtXPathNamespaceMappings.setModel(xpathNamespaceMappingsTableModel);
		jtXPathNamespaceMappings.setPreferredScrollableViewportSize(new Dimension(200, 90));
		// TODO - next line is to be enabled when Taverna is migrated to Java
		// 1.6; for now it's fine to run without this
		// jtXPathNamespaceMappings.setFillsViewportHeight(true); // makes sure
		// that when the dedicated area is larger than the table, the latter is
		// stretched vertically to fill the empty space

		jtXPathNamespaceMappings.getColumnModel().getColumn(0).setPreferredWidth(20); // set
																						// relative
																						// sizes of
																						// columns
		jtXPathNamespaceMappings.getColumnModel().getColumn(1).setPreferredWidth(300);

		c.gridy++;
		spXPathNamespaceMappings = new JScrollPane(jtXPathNamespaceMappings);
		spXPathNamespaceMappings.setVisible(false);
		jpMainPanel.add(spXPathNamespaceMappings, c);

		// populate the view with values
		refreshView();

		return jpMainPanel;
	}

	@Override
	/**
	 * This is the title of the contextual view - shown in the list of other available
	 * views (even when this contextual view is collapsed).
	 */
	public String getViewTitle() {
		return "XPath Service Details";
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView() {
		Configuration configuration = scufl2Tools.configurationFor(activity, activity.getParent());
		JsonNode json = configuration.getJson();

		// Set XPath Expression
		tfXPathExpression.setText(json.get("xpathExpression").asText());

		// Populate Namespace Mappings
		xpathNamespaceMappingsTableModel.getDataVector().removeAllElements();
		if (json.has("xpathNamespaceMap")) {
			for (JsonNode mapping : json.get("xpathNamespaceMap")) {
				xpathNamespaceMappingsTableModel.addRow(new Object[] {
						mapping.get("prefix").asText(), mapping.get("uri").asText() });
			}
		}
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// want to be on top, as it's the main contextual view for this activity
		return 100;
	}

	@Override
	public Action getConfigureAction(final Frame owner) {
		// "Configure" button appears because of this action being returned
		return new XPathActivityConfigureAction(activity, owner, editManager, fileManager,
				activityIconManager, serviceDescriptionRegistry, serviceRegistry);
	}

}
