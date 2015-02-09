/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager.local;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.taverna.t2.activities.externaltool.local.ExternalToolLocalInvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismEditor;

/**
 * @author alanrw
 *
 */
public final class LocalInvocationMechanismEditor extends
		InvocationMechanismEditor<ExternalToolLocalInvocationMechanism> {

	private ExternalToolLocalInvocationMechanism invocationMechanism;
	
	private JTextField directoryField = new JTextField(30);
	
	private JTextField shellPrefixField = new JTextField(30);
	
	private JTextField linkCommandField = new JTextField(30);
	
	private JCheckBox retrieveDataField = new JCheckBox();
	

	@Override
	public boolean canShow(Class<?> c) {
		return ExternalToolLocalInvocationMechanism.class.isAssignableFrom(c);
	}

	@Override
	public String getName() {
		return ("Local");
	}

	@Override
	public void show(ExternalToolLocalInvocationMechanism invocationMechanism) {
		this.invocationMechanism = invocationMechanism;
		this.removeAll();
		final JPanel innerPanel = new JPanel(new GridBagLayout());
		final GridBagConstraints inputConstraint = new GridBagConstraints();
//		inputConstraint.insets = new Insets(5,5,5,5);
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;
		innerPanel.add(new JLabel("Working directory: "), inputConstraint);
		inputConstraint.gridx++;
		directoryField.setText(invocationMechanism.getDirectory());
		innerPanel.add(directoryField, inputConstraint);
		inputConstraint.gridx = 0;
		inputConstraint.gridy++;
		innerPanel.add(new JLabel("Shell: "), inputConstraint);
		inputConstraint.gridx++;
		shellPrefixField.setText(invocationMechanism.getShellPrefix());
		innerPanel.add(shellPrefixField, inputConstraint);
		
		inputConstraint.gridx = 0;
		inputConstraint.gridy++;
		innerPanel.add(new JLabel("Link command: "), inputConstraint);
		inputConstraint.gridx++;
		linkCommandField.setText(invocationMechanism.getLinkCommand());
		innerPanel.add(linkCommandField, inputConstraint);
		
		inputConstraint.gridx = 0;
		inputConstraint.gridy++;
		innerPanel.add(new JLabel("Fetch data: "), inputConstraint);
		inputConstraint.gridx++;
		retrieveDataField.setSelected(invocationMechanism.isRetrieveData());
		innerPanel.add(retrieveDataField, inputConstraint);
		
		this.add(innerPanel);
	}

	@Override
	public ExternalToolLocalInvocationMechanism updateInvocationMechanism() {
		if ((directoryField.getText() == null) || (directoryField.getText().length() == 0)) {
			invocationMechanism.setDirectory(null);
		} else {
			invocationMechanism.setDirectory(directoryField.getText());
		}
		if ((shellPrefixField.getText() == null) || (shellPrefixField.getText().length() == 0)) {
			invocationMechanism.setShellPrefix(null);
		} else {
			invocationMechanism.setShellPrefix(shellPrefixField.getText());
		}
		if ((shellPrefixField.getText() == null) || (shellPrefixField.getText().length() == 0)) {
			invocationMechanism.setShellPrefix(null);
		} else {
			invocationMechanism.setShellPrefix(shellPrefixField.getText());
		}
		if ((linkCommandField.getText() == null) || (linkCommandField.getText().length() == 0)) {
			invocationMechanism.setLinkCommand(null);
		} else {
			invocationMechanism.setLinkCommand(linkCommandField.getText());
		}
		invocationMechanism.setRetrieveData(retrieveDataField.isSelected());
		return invocationMechanism;
	}

	@Override
	public InvocationMechanism createMechanism(String mechanismName) {
		ExternalToolLocalInvocationMechanism result = new ExternalToolLocalInvocationMechanism();
		result.setName(mechanismName);
		return(result);
	}

	public boolean isSingleton() {
		return true;
	}
}
