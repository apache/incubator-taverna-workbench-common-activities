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

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.taverna.activities.externaltool.desc.RuntimeEnvironmentConstraint;
import org.apache.taverna.activities.externaltool.desc.ScriptInput;
import org.apache.taverna.activities.externaltool.desc.ScriptInputUser;
import org.apache.taverna.activities.externaltool.desc.ScriptOutput;

/**
 * @author alanrw
 *
 */
public class ExternalToolRuntimeEnvironmentViewer {
	
	private JTextField idField;
	private JComboBox relationSelector;

	public ExternalToolRuntimeEnvironmentViewer(String id, String relation) {
		this(id);
		idField.setText(id);
		relationSelector.setSelectedItem(relation);
	}

	public ExternalToolRuntimeEnvironmentViewer(String id) {
		this();
		idField.setText(id);	
	}
	
	public ExternalToolRuntimeEnvironmentViewer() {
		idField = new JTextField(20);
		relationSelector = new JComboBox(RuntimeEnvironmentConstraint.getAcceptedRelations());
		relationSelector.setSelectedItem(RuntimeEnvironmentConstraint.getDefaultRelation());			
	}

	public JTextField getIdField() {
		return idField;
	}

	public JComboBox getRelationSelector() {
		return relationSelector;
	}

	public String getId() {
		return idField.getText();
	}

	public String getRelation() {
		return (String) relationSelector.getSelectedItem();
	}

}
