package org.apache.taverna.activities.xpath.ui.config;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;

import org.apache.taverna.activities.xpath.XPathActivityConfigurationBean;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;
import org.apache.taverna.services.ServiceRegistry;
import org.apache.taverna.scufl2.api.activity.Activity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 *
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
@SuppressWarnings("serial")
public class XPathActivityConfigurationPanelProvider extends ActivityConfigurationPanel {

	private XPathActivityConfigurationPanel configPanel;
	private final ServiceRegistry serviceRegistry;

	public XPathActivityConfigurationPanelProvider(Activity activity, ServiceRegistry serviceRegistry) {
		super(activity);
		this.serviceRegistry = serviceRegistry;
		initialise();
	}

	@Override
	protected void initialise() {
		super.initialise();
		removeAll();
		setLayout(new BorderLayout());

		// create actual contents of the config panel
		this.configPanel = new XPathActivityConfigurationPanel();
		add(configPanel, BorderLayout.CENTER);

		// place the whole configuration panel into a raised area, so that
		// automatically added 'Apply' / 'Close' buttons visually apply to
		// the whole of the panel, not just part of it
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(12, 12, 2, 12),
				BorderFactory.createRaisedBevelBorder()));

		// Populate fields from activity configuration bean
		refreshConfiguration();
	}

	/**
	 * Prepare a new configuration bean from the UI, to be returned with
	 * getConfiguration()
	 */
	@Override
	public void noteConfiguration() {
		if (configPanel.getCurrentXMLTree() != null) {
			setProperty("exampleXmlDocument", configPanel.getCurrentXMLTree()
					.getDocumentUsedToPopulateTree().asXML());
		}
		setProperty("xpathExpression", configPanel.getCurrentXPathExpression());

		Map<String, String> xPathNamespaceMap = configPanel.getCurrentXPathNamespaceMap();
		if (xPathNamespaceMap.isEmpty()) {
			json.remove("xpathNamespaceMap");
		} else {
			ArrayNode namespaceMapNode = json.arrayNode();
			for (Entry<String, String> namespaceMapping : xPathNamespaceMap.entrySet()) {
				namespaceMapNode.addObject().put("prefix", namespaceMapping.getKey()).put("uri", namespaceMapping.getValue());
			}
			json.set("xpathNamespaceMap", namespaceMapNode);
		}

		configureInputPorts(serviceRegistry);
		configureOutputPorts(serviceRegistry);
}

	/**
	 * Check that user values in the UI are valid.
	 */
	@Override
	public boolean checkValues() {
		// the only validity condition is the correctness of the XPath
		// expression -- so checking that
		int xpathExpressionStatus = XPathActivityConfigurationBean.validateXPath(this.configPanel
				.getCurrentXPathExpression());

		// show an explicit warning message to explain the problem
		if (xpathExpressionStatus == XPathActivityConfigurationBean.XPATH_EMPTY) {
			JOptionPane.showMessageDialog(this, "XPath expression should not be empty",
					"XPath Activity", JOptionPane.WARNING_MESSAGE);
		} else if (xpathExpressionStatus == XPathActivityConfigurationBean.XPATH_INVALID) {
			JOptionPane.showMessageDialog(this,
					"<html><center>XPath expression is invalid - hover the mouse over the XPath status<br>"
							+ "icon to get more information</center></html>", "XPath Activity",
					JOptionPane.WARNING_MESSAGE);
		}

		return (xpathExpressionStatus == XPathActivityConfigurationBean.XPATH_VALID);
	}

	/**
	 * Update GUI from a changed configuration bean (perhaps by undo / redo).
	 */
	@Override
	public void refreshConfiguration() {
		if (json.has("exampleXmlDocument")) {
			configPanel.setSourceXML(getProperty("exampleXmlDocument"));
			configPanel.parseXML();
		}

		configPanel.setCurrentXPathExpression(getProperty("xpathExpression"));

		Map<String, String> xpathNamespaceMap = new HashMap<>();
		if (json.has("xpathNamespaceMap")) {
			for (JsonNode namespaceMapping : json.get("xpathNamespaceMap")) {
				xpathNamespaceMap.put(namespaceMapping.get("prefix").asText(), namespaceMapping.get("uri").asText());
			}
		}
		configPanel.setCurrentXPathNamespaceMapValues(xpathNamespaceMap);
		configPanel.reloadNamespaceMappingTableFromLocalMap();

		// if the XML tree was populated, (re-)run the XPath expression
		// and restore selection of nodes in the tree, if possible
		if (configPanel.getCurrentXMLTree() != null) {
			configPanel.runXPath(true);

			// convert the XPath expression into the required list form;
			// discard the first 'leg', as it's a side effect of
			// "String.split()" -
			// non-existent string to the left of the first "/"
			String[] xpathLegs = getProperty("xpathExpression").split("/");
			List<String> xpathLegList = new ArrayList<String>();
			for (int i = 1; i < xpathLegs.length; i++) {
				xpathLegList.add("/" + xpathLegs[i]);
			}

			// if nothing was obtained, we should be looking at the root node -
			// but add the actual expression as it was, just in case
			if (xpathLegList.size() == 0) {
				xpathLegList.add(configPanel.getCurrentXPathExpression());
			}

			// invoke selection handler of the XML tree to do the job
			configPanel.getCurrentXMLTree().getXMLTreeSelectionHandler()
					.selectAllNodesThatMatchTheCurrentXPath(xpathLegList, null);
		}

	}
}
