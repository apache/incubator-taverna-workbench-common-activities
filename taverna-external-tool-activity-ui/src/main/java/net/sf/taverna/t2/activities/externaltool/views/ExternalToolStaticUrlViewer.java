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
public class ExternalToolStaticUrlViewer {
	
	ScriptInputStatic input;
	private JTextField contentField = new JTextField();
	private JTextField valueField;


	public ExternalToolStaticUrlViewer(ScriptInputStatic input) {
		this();
		this.input = input;
		contentField.setText(input.getUrl());
		valueField.setText(input.getTag());
	}

	public ExternalToolStaticUrlViewer() {
		contentField = new JTextField(40);
		contentField.setText("");
		valueField = new JTextField(20);
		valueField.setText("");
	}

	public String getContent() {
		return contentField.getText();
	}

	public JTextField getContentField() {
		return contentField;
	}


	public JTextField getValueField() {
		return valueField;
	}

	public String getValue() {
		return valueField.getText();
	}

}
