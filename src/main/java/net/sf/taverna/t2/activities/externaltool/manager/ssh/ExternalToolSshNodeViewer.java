/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager.ssh;

import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshNode;



/**
 * @author alanrw
 *
 */
public class ExternalToolSshNodeViewer {
	
	private JTextField hostnameField;
	private JTextField portField;
	private JTextField directoryField;

	public ExternalToolSshNodeViewer(SshNode node) {
		hostnameField = new JTextField(node.getHost(), 30);
		portField = new JTextField(Integer.toString(node.getPort()), 3);
		directoryField = new JTextField(30);
		if (node.getDirectory() != null) {
			directoryField.setText(node.getDirectory());
		}
	}

	public ExternalToolSshNodeViewer() {
		this(new SshNode());
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

	public String getHostname() {
		return hostnameField.getText();
	}

	public int getPort() {
		return Integer.parseInt(portField.getText());
	}
	
	public String getDirectory() {
		return directoryField.getText();
	}

}
