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

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.taverna.activities.externaltool.desc.ScriptInput;
import org.apache.taverna.activities.externaltool.desc.ScriptInputUser;
import org.apache.taverna.activities.externaltool.desc.ScriptOutput;

/**
 * @author alanrw
 *
 */
public class ExternalToolFileViewer {
	
	private JTextField nameField;
	private String name;
	private JTextField valueField;
	private JCheckBox valueFromField;
	private JComboBox typeSelector;

	public ExternalToolFileViewer(String name, String value, boolean isBinary) {
		this(name);
		nameField.setText(name);
		if (!value.equals(name)) {
			valueFromField.setSelected(false);
			valueField.setText(value);
			valueField.setEnabled(true);
		}
		if (isBinary) {
			typeSelector.setSelectedItem("Binary");
		} else {
			typeSelector.setSelectedItem("Text");
		}
	}

	public ExternalToolFileViewer(final String name) {
		this.name = name;
		nameField = new JTextField(20);
		valueField = new JTextField(20);
		valueFromField = new JCheckBox(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (valueFromField.isSelected()) {
					valueField.setText("");
					valueField.setEnabled(false);
				} else {
					valueField.setText(getName());
					valueField.setEnabled(true);
				}
			}});
		valueFromField.setSelected(true);
		valueField.setEnabled(false);
		typeSelector = new JComboBox(new String[] {"Binary", "Text"});
		nameField.setText(name);
		typeSelector.setSelectedItem("Text");
		
	}

	public JTextField getNameField() {
		return nameField;
	}

	public JTextField getValueField() {
		return valueField;
	}

	public JComboBox getTypeSelector() {
		return typeSelector;
	}

	public String getName() {
		return nameField.getText();
	}

	public boolean isBinary() {
		return (typeSelector.getSelectedItem().equals("Binary"));
	}

	public String getValue() {
		if (valueFromField.isSelected()) {
			return getName();
		}
		return valueField.getText();
	}
	
	/**
	 * @return the valueFromField
	 */
	public JCheckBox getValueFromField() {
		return valueFromField;
	}


}
