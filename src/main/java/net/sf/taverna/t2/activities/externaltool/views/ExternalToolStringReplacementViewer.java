/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import net.sf.taverna.t2.lang.ui.SanitisingDocumentFilter;

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
	private JCheckBox valueFromField;

	public ExternalToolStringReplacementViewer(String name, ScriptInputUser input) {
		this(name);
		this.input = input;
		nameField.setText(name);
		if (!input.getTag().equals(name)) {
			valueFromField.setSelected(false);
			valueField.setText(PERCENTS + input.getTag() + PERCENTS);
			valueField.setEnabled(true);
		}
	}

	public ExternalToolStringReplacementViewer(String name) {
		this.name = name;
		nameField = new JTextField(20);
		nameField.setText(name);
		SanitisingDocumentFilter.addFilterToComponent(nameField);
		valueField = new JTextField(20);
		valueFromField = new JCheckBox(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (valueFromField.isSelected()) {
					valueField.setText("");
					valueField.setEnabled(false);
				} else {
					valueField.setText(PERCENTS + getName() + PERCENTS);
					valueField.setEnabled(true);
				}
			}});
		valueFromField.setSelected(true);
		valueField.setEnabled(false);
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
		if (valueFromField.isSelected()) {
			return getName();
		}
		String enteredValue = valueField.getText();

		Matcher m = p.matcher(enteredValue);
		String result = "";
		if (m.find()) {
			result = m.group();
		}
		return result;
	}

	/**
	 * @return the valueFromField
	 */
	public JCheckBox getValueFromField() {
		return valueFromField;
	}

}
