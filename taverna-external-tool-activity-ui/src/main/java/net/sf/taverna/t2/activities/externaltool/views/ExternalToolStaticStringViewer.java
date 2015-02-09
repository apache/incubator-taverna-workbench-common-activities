/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInputStatic;

/**
 * @author alanrw
 *
 */
public class ExternalToolStaticStringViewer {
	
	ScriptInputStatic input;
	private JTextArea contentField = new JTextArea();
	private JTextField valueField;


	public ExternalToolStaticStringViewer(ScriptInputStatic input) {
		this();
		this.input = input;
			contentField.setText((String) input.getContent());
		valueField.setText(input.getTag());
	}

	public ExternalToolStaticStringViewer() {
		contentField = new JTextArea(5, 40);
		contentField.setText("");
		valueField = new JTextField(20);
		valueField.setText("");
	}

	public String getContent() {
		return contentField.getText();
	}

	public JTextArea getContentField() {
		return contentField;
	}


	public JTextField getValueField() {
		return valueField;
	}

	public String getValue() {
		return valueField.getText();
	}

}
