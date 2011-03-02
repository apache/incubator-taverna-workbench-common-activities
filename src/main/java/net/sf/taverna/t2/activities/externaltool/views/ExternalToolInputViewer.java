/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;

/**
 * @author alanrw
 *
 */
public class ExternalToolInputViewer {
	
	ScriptInput input;
	private JTextField nameField;
	private String name;
	private JComboBox depthSelector;
	private JComboBox actionSelector;
	private JTextField valueField;
	private JComboBox typeSelector;

	public ExternalToolInputViewer(String name, ScriptInputUser input) {
		this(name);
		this.input = input;
		nameField.setText(name);
		if (input.isList()) {
			depthSelector.setSelectedItem("List");
		} else {
			depthSelector.setSelectedItem("Single");
		}
		if (input.isFile()) {
			actionSelector.setSelectedItem("File");
		} else if (input.isTempFile()) {
			actionSelector.setSelectedItem("Temporary file");
		} else {
			actionSelector.setSelectedItem("Replace");
		}
		valueField.setText(input.getTag());
		if (input.isBinary()) {
			typeSelector.setSelectedItem("Binary");
		} else {
			typeSelector.setSelectedItem("Text");
		}
	}

	public ExternalToolInputViewer(String name) {
		this.name = name;
		nameField = new JTextField(20);
		nameField.setText(name);
		depthSelector = new JComboBox(new String[] {"Single", "List"});
		depthSelector.setSelectedItem("Single");
		actionSelector = new JComboBox(new String[] {"Replace", "File", "Temporary file"});
		actionSelector.setSelectedItem("Replace");
		valueField = new JTextField(20);
		valueField.setText(name);
		typeSelector = new JComboBox(new String[] {"Binary", "Text"});
		typeSelector.setSelectedItem("Text");
		
	}

	public JTextField getNameField() {
		return nameField;
	}

	public JComboBox getDepthSelector() {
		return depthSelector;
	}

	public JComboBox getActionSelector() {
		return actionSelector;
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

	public boolean isList() {
		return (depthSelector.getSelectedItem().equals("List"));
	}

	public boolean isReplace() {
		return (actionSelector.getSelectedItem().equals("Replace"));
	}

	public boolean isFile() {
		return (actionSelector.getSelectedItem().equals("File"));
	}

	public boolean isTempFile() {
		return (actionSelector.getSelectedItem().equals("Temporary file"));
	}

	public String getValue() {
		return valueField.getText();
	}

}
