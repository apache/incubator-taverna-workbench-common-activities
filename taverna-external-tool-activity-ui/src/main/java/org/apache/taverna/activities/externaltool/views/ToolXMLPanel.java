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

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.taverna.renderers.impl.XMLTree;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;

/**
 * @author alanrw
 *
 */
public class ToolXMLPanel extends JPanel {

	public ToolXMLPanel(ToolDescription toolDescription) {
		super(new BorderLayout());
		XMLTree xmlTree = new XMLTree(toolDescription.writeToXMLElement());
		this.add(new JScrollPane(xmlTree), BorderLayout.CENTER);
	}

	public void regenerateTree(ToolDescription toolDescription) {
		this.removeAll();
		XMLTree xmlTree = new XMLTree(toolDescription.writeToXMLElement());
		this.add(new JScrollPane(xmlTree), BorderLayout.CENTER);		
	}

}
