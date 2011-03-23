/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.InvocationCreator;
import net.sf.taverna.t2.spi.SPIRegistry;

/**
 * @author alanrw
 *
 */
public class MechanismEditsPanel extends JPanel {
	
private static SPIRegistry<InvocationMechanismEditor> invocationMechanismEditorRegistry = new SPIRegistry(InvocationMechanismEditor.class);
	
	private InvocationMechanism mechanism = null;

	private InvocationMechanismEditor chosenEditor;
	
	private JPanel buttonPanel;
	
	public MechanismEditsPanel() {
		this.setLayout(new BorderLayout());
		buttonPanel = createButtonPanel();
		System.err.println("Mechanism Edits Panel created");
	}

	private JPanel createButtonPanel() {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		JButton cancelButton = new JButton(new AbstractAction("Cancel") {

			@Override
			public void actionPerformed(ActionEvent e) {
				MechanismEditsPanel.this.showMechanism(mechanism);
			}});
		result.add(cancelButton);
		JButton saveButton = new JButton(new AbstractAction("Update") {

			@Override
			public void actionPerformed(ActionEvent e) {
				chosenEditor.updateInvocationMechanism();
			}
			
		});
		result.add(saveButton);
		return result;
	}

	public void clear() {
		mechanism = null;
		chosenEditor = null;
		this.removeAll();
		this.repaint();
	}

	public void showMechanism(InvocationMechanism mechanism) {
		clear();
		this.mechanism = mechanism;
		chosenEditor = null;
		for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry.getInstances()) {
			if (ime.canShow(mechanism.getClass())) {
				chosenEditor = ime;
				break;
			}
		}
		if (chosenEditor != null) {
			this.add(new JLabel(chosenEditor.getName()), BorderLayout.NORTH);
			this.add(chosenEditor, BorderLayout.CENTER);
			chosenEditor.show(mechanism);
		}
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.validate();
		this.repaint();
	}

}
