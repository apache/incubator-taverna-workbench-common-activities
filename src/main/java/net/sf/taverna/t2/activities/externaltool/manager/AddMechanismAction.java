package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class AddMechanismAction extends AbstractAction {
	
	private final InvocationMechanismEditor ime;

	public AddMechanismAction(String name, InvocationMechanismEditor ime) {
		super(name);
		this.ime = ime;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String mechanismName = JOptionPane.showInputDialog(null, "Mechanism name", "Add mechanism", JOptionPane.QUESTION_MESSAGE);
		if (mechanismName != null) {
			InvocationGroupManager.getInstance().addMechanism(ime.createMechanism(mechanismName));
		}
	}

}
