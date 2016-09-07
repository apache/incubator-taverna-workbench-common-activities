/**
 * 
 */
package org.apache.taverna.activities.externaltool.manager.ssh;
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

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.apache.taverna.activities.externaltool.manager.InvocationMechanism;
import org.apache.taverna.activities.externaltool.ssh.SshNode;




/**
 * @author alanrw
 *
 */
public class ExternalToolSshNodeViewer {
	
	private JTextField hostnameField;
	private JTextField portField;
	private JTextField directoryField;
	private JTextField linkCommandField;
	private JTextField copyCommandField;
	private JCheckBox retrieveDataField;

	public ExternalToolSshNodeViewer(SshNode node) {
		this();
		hostnameField.setText(node.getHost());
		portField.setText(Integer.toString(node.getPort()));
		if (node.getDirectory() != null) {
			directoryField.setText(node.getDirectory());
		} else {
			directoryField.setText("");
		}
		if (node.getLinkCommand() != null) {
			linkCommandField.setText(node.getLinkCommand());
		} else {
			linkCommandField.setText("");
		}
		if (node.getCopyCommand() != null) {
			copyCommandField.setText(node.getCopyCommand());
		} else {
			copyCommandField.setText("");
		}
		retrieveDataField.setSelected(node.isRetrieveData());
	}

	public ExternalToolSshNodeViewer() {
		hostnameField = new JTextField(30);
		hostnameField.setText(SshNode.DEFAULT_HOST);
		portField = new JTextField(3);
		portField.setText("" + SshNode.DEFAULT_PORT);
		directoryField = new JTextField(30);
		directoryField.setText(SshNode.DEFAULT_DIRECTORY);
		linkCommandField = new JTextField(30);
		linkCommandField.setText(InvocationMechanism.UNIX_LINK);
		copyCommandField = new JTextField(30);
		copyCommandField.setText(InvocationMechanism.UNIX_COPY);
		retrieveDataField = new JCheckBox();
	}

	public JTextField getHostnameField() {
		return hostnameField;
	}

	public JTextField getPortField() {
		return portField;
	}
	
	public JTextField getDirectoryField() {
		return directoryField;
	}

	public JTextField getLinkCommandField() {
		return linkCommandField;
	}

	public JTextField getCopyCommandField() {
		return copyCommandField;
	}

	public String getHostname() {
		return hostnameField.getText();
	}

	public int getPort() {
		return Integer.parseInt(portField.getText());
	}
	
	public String getDirectory() {
		return directoryField.getText();
	}
	
	public String getLinkCommand() {
		return linkCommandField.getText();
	}
	
	public String getCopyCommand() {
		return copyCommandField.getText();
	}

	/**
	 * @return the retrieveDataField
	 */
	public JCheckBox getRetrieveDataField() {
		return retrieveDataField;
	}
}
