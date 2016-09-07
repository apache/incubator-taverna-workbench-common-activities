package org.apache.taverna.activities.externaltool.manager;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * UI for creating/editing dataflow input ports.
 *
 * @author David Withers
 */
public class MechanismPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField mechanismNameField;

	private JComboBox mechanismTypeSelector;

	public MechanismPanel(List<InvocationMechanismEditor<?>> invocationMechanismEditors) {
		super(new GridBagLayout());

		mechanismNameField = new JTextField();


		setBorder(new EmptyBorder(10, 10, 10, 10));

		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 10;
		add(new JLabel("Name:"), constraints);

		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.ipadx = 0;
		constraints.weightx = 1d;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(mechanismNameField, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0d;
		constraints.fill = GridBagConstraints.NONE;
		constraints.ipadx = 10;
		constraints.insets = new Insets(10, 0, 0, 0);
		add(new JLabel("Type:"), constraints);

		mechanismTypeSelector = new JComboBox();
		for (InvocationMechanismEditor<?> ime : invocationMechanismEditors) {
			if (!ime.isSingleton()) {
				mechanismTypeSelector.addItem(ime.getName());
			}
		}
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.ipadx = 0;
		add(mechanismTypeSelector, constraints);


	}

	/**
	 * Returns the portNameField.
	 *
	 * @return the portNameField
	 */
	public JTextField getMechanismNameField() {
		return mechanismNameField;
	}

	/**
	 * Returns the port name.
	 *
	 * @return the port name
	 */
	public String getMechanismName() {
		return mechanismNameField.getText();
	}

	public String getMechanismTypeName() {
		return (String) mechanismTypeSelector.getSelectedItem();
	}

	public Component getMechanismTypeSelector() {
		return mechanismTypeSelector;
	}


}
