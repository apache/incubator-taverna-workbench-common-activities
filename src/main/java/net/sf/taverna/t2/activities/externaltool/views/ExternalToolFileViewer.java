/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import net.sf.taverna.t2.lang.ui.SanitisingDocumentFilter;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;

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
		SanitisingDocumentFilter.addFilterToComponent(nameField);
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
