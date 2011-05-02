/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;

/**
 * @author alanrw
 *
 */
public class ExternalToolStringReplacementViewer {
	
	private static Pattern p = Pattern.compile("\\w+");
	private static final String PERCENTS = "%%";
	ScriptInput input;
	private JTextField nameField;
	private String name;
	private JTextField valueField;

	public ExternalToolStringReplacementViewer(String name, ScriptInputUser input) {
		this(name);
		this.input = input;
		nameField.setText(name);
		valueField.setText(PERCENTS + input.getTag() + PERCENTS);
	}

	public ExternalToolStringReplacementViewer(String name) {
		this.name = name;
		nameField = new JTextField(20);
		nameField.setText(name);
		valueField = new JTextField(20);
		valueField.setText(PERCENTS + name + PERCENTS);
	}

	public JTextField getNameField() {
		return nameField;
	}
	
	public JTextField getValueField() {
		return valueField;
	}

	public String getName() {
		return nameField.getText();
	}

	public String getValue() {
		String enteredValue = valueField.getText();

		Matcher m = p.matcher(enteredValue);
		String result = "";
		if (m.find()) {
			result = m.group();
		}
		return result;
	}

}
