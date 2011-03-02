/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;

/**
 * @author alanrw
 *
 */
public class ExternalToolOutputViewer {
	
	ScriptOutput output;
	private JTextField nameField;
	private String name;
	private JTextField valueField;
	private JComboBox typeSelector;

	public ExternalToolOutputViewer(String name, ScriptOutput output) {
		this(name);
		this.output = output;
		nameField.setText(name);
		valueField.setText(output.getPath());
		if (output.isBinary()) {
			typeSelector.setSelectedItem("Binary");
		} else {
			typeSelector.setSelectedItem("Text");
		}
	}

	public ExternalToolOutputViewer(String name) {
		this.name = name;
		nameField = new JTextField(20);
		valueField = new JTextField(20);
		typeSelector = new JComboBox(new String[] {"Binary", "Text"});
		nameField.setText(name);
		valueField.setText(name);
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
		return valueField.getText();
	}

}
