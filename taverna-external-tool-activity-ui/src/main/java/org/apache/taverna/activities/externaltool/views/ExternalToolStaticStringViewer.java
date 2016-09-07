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

import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.taverna.activities.externaltool.desc.ScriptInputStatic;


/**
 * @author alanrw
 *
 */
public class ExternalToolStaticStringViewer {
	
	ScriptInputStatic input;
	private JTextArea contentField = new JTextArea();
	private JTextField valueField;


	public ExternalToolStaticStringViewer(ScriptInputStatic input) {
		this();
		this.input = input;
			contentField.setText((String) input.getContent());
		valueField.setText(input.getTag());
	}

	public ExternalToolStaticStringViewer() {
		contentField = new JTextArea(5, 40);
		contentField.setText("");
		valueField = new JTextField(20);
		valueField.setText("");
	}

	public String getContent() {
		return contentField.getText();
	}

	public JTextArea getContentField() {
		return contentField;
	}


	public JTextField getValueField() {
		return valueField;
	}

	public String getValue() {
		return valueField.getText();
	}

}
