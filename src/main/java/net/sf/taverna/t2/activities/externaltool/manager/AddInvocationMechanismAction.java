package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;

import net.sf.taverna.t2.lang.ui.ValidatingUserInputDialog;
import net.sf.taverna.t2.spi.SPIRegistry;

public class AddInvocationMechanismAction extends AbstractAction {
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();
	
	protected static SPIRegistry<InvocationMechanismEditor> invocationMechanismEditorRegistry = new SPIRegistry(InvocationMechanismEditor.class);
	
	private InvocationMechanism newMechanism;
	private InvocationGroup newGroup;

	private final boolean addGroup;

	public InvocationMechanism getNewMechanism() {
		return newMechanism;
	}

	/**
	 * 
	 */

	public AddInvocationMechanismAction(boolean addGroup) {
		super("Add location");
		this.addGroup = addGroup;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		newMechanism = null;
		newGroup = null;
		
		Set<String> usedNames = new HashSet<String>();
		for (InvocationMechanism m : manager.getMechanisms()) {
			usedNames.add(m.getName());
		}
		if (addGroup) {
			for (InvocationGroup g : manager.getInvocationGroups()) {
				usedNames.add(g.getInvocationGroupName());
			}
		}

		MechanismPanel inputPanel = new MechanismPanel();
		
		ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
				"Add invocation location", inputPanel);
		vuid.addTextComponentValidation(inputPanel.getMechanismNameField(),
				"Set the location name.", usedNames,
				"Duplicate name.", "[\\p{L}\\p{Digit}_.]+",
				"Invalid location name.");
		vuid.addMessageComponent(inputPanel.getMechanismTypeSelector(), "Set the location type.");
		vuid.setSize(new Dimension(400, 250));

		if (vuid.show(null)) {
			String mechanismName = inputPanel.getMechanismName();
			String mechanismTypeName = inputPanel.getMechanismTypeName();
			InvocationMechanismEditor ime = findEditor(mechanismTypeName);
			newMechanism = ime.createMechanism(mechanismName);
			manager.addMechanism(newMechanism);
			if (addGroup) {
				newGroup = new InvocationGroup();
				newGroup.setInvocationGroupName(mechanismName);
				newGroup.setMechanism(newMechanism);
				manager.addInvocationGroup(newGroup);
			}
//			setSelectedMechanism(newMechanism);
		}
	}
	
	public InvocationGroup getNewGroup() {
		return newGroup;
	}

	protected InvocationMechanismEditor findEditor(String name) {
		for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry.getInstances()) {
			if (ime.getName().equals(name)) {
				return ime;
			}
		}
		return null;
	}

}