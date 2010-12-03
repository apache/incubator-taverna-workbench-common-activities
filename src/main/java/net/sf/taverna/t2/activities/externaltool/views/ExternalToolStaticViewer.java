/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputStatic;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;

/**
 * @author alanrw
 *
 */
public class ExternalToolStaticViewer {
	
	ScriptInputStatic input;
	private JComboBox typeSelector;
	private JTextArea contentField = new JTextArea();
	private JComboBox actionSelector;
	private JTextField valueField;


	public ExternalToolStaticViewer(ScriptInputStatic input) {
		this();
		this.input = input;
		if (input.url != null) {
			typeSelector.setSelectedItem("URL");
			contentField.setText(input.url);
		} else {
			typeSelector.setSelectedItem("Explicit");
			contentField.setText((String) input.content);
		}
		if (input.file) {
			actionSelector.setSelectedItem("File");
		} else {
			actionSelector.setSelectedItem("Replace");
		}
		valueField.setText(input.tag);
	}

	public ExternalToolStaticViewer() {
		typeSelector = new JComboBox(new String[] {"URL", "Explicit"});
		typeSelector.setSelectedItem("Explicit");
		contentField = new JTextArea(5, 40);
		contentField.setText("");
		actionSelector = new JComboBox(new String[] {"Replace", "File"});
		actionSelector.setSelectedItem("Replace");
		valueField = new JTextField(20);
		valueField.setText("");
	}

	public JComboBox getTypeSelector() {
		return typeSelector;
	}

	public boolean isURL() {
		return (typeSelector.getSelectedItem().equals("URL"));
	}

	public String getContent() {
		return contentField.getText();
	}

	public JTextArea getContentField() {
		return contentField;
	}

	public JComboBox getActionSelector() {
		return actionSelector;
	}

	public JTextField getValueField() {
		return valueField;
	}

	public boolean isReplace() {
		return actionSelector.getSelectedItem().equals("Replace");
	}

	public String getValue() {
		return valueField.getText();
	}

}
