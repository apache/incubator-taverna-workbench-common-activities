/**
 * 
 */
package org.apache.taverna.activities.externaltool.views;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.taverna.activities.externaltool.desc.RuntimeEnvironmentConstraint;
import org.apache.taverna.activities.externaltool.desc.ScriptInput;
import org.apache.taverna.activities.externaltool.desc.ScriptInputUser;
import org.apache.taverna.activities.externaltool.desc.ScriptOutput;

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
