/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager.ssh;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;

import de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshNode;



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
