/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.RuntimeEnvironmentConstraint;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;

/**
 * @author alanrw
 *
 */
public class ExternalToolRuntimeEnvironmentViewer {
	
	private JTextField idField;
	private JComboBox relationSelector;

	public ExternalToolRuntimeEnvironmentViewer(String id, String relation) {
		this(id);
		idField.setText(id);
		relationSelector.setSelectedItem(relation);
	}

	public ExternalToolRuntimeEnvironmentViewer(String id) {
		this();
		idField.setText(id);	
	}
	
	public ExternalToolRuntimeEnvironmentViewer() {
		idField = new JTextField(20);
		relationSelector = new JComboBox(RuntimeEnvironmentConstraint.getAcceptedRelations());
		relationSelector.setSelectedItem(RuntimeEnvironmentConstraint.getDefaultRelation());			
	}

	public JTextField getIdField() {
		return idField;
	}

	public JComboBox getRelationSelector() {
		return relationSelector;
	}

	public String getId() {
		return idField.getText();
	}

	public String getRelation() {
		return (String) relationSelector.getSelectedItem();
	}

}
