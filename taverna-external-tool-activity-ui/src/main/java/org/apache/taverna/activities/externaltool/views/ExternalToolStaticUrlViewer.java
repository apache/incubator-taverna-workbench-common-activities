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
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.taverna.activities.externaltool.desc.ScriptInput;
import org.apache.taverna.activities.externaltool.desc.ScriptInputStatic;
import org.apache.taverna.activities.externaltool.desc.ScriptInputUser;

/**
 * @author alanrw
 *
 */
public class ExternalToolStaticUrlViewer {
	
	ScriptInputStatic input;
	private JTextField contentField = new JTextField();
	private JTextField valueField;


	public ExternalToolStaticUrlViewer(ScriptInputStatic input) {
		this();
		this.input = input;
		contentField.setText(input.getUrl());
		valueField.setText(input.getTag());
	}

	public ExternalToolStaticUrlViewer() {
		contentField = new JTextField(40);
		contentField.setText("");
		valueField = new JTextField(20);
		valueField.setText("");
	}

	public String getContent() {
		return contentField.getText();
	}

	public JTextField getContentField() {
		return contentField;
	}


	public JTextField getValueField() {
		return valueField;
	}

	public String getValue() {
		return valueField.getText();
	}

}
