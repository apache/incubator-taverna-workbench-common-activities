/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.taverna.t2.lang.ui.ValidatingUserInputDialog;
import net.sf.taverna.t2.spi.SPIRegistry;
import net.sf.taverna.t2.workbench.design.ui.DataflowInputPortPanel;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;

/**
 * @author alanrw
 *
 */
public class MechanismListPanel extends JPanel implements InvocationGroupManagerListener {
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();
	
	private static SPIRegistry<InvocationMechanismEditor> invocationMechanismEditorRegistry = new SPIRegistry(InvocationMechanismEditor.class);
	
	private JList mechanismList = new JList();

	public MechanismListPanel() {
		super();
		manager.addListener(this);
		this.setLayout(new BorderLayout());
		
		this.add(new JLabel("Locations"), BorderLayout.NORTH);

		populateList();
		this.add(new JScrollPane(mechanismList), BorderLayout.CENTER);
		JPanel mechanismListButtonPanel = createButtonsPanel();
		this.add(mechanismListButtonPanel, BorderLayout.SOUTH);
	}
	
	private void populateList() {
		ArrayList<InvocationMechanism> mechanisms = new ArrayList<InvocationMechanism>();
		mechanisms.addAll(manager.getMechanisms());
		Collections.sort(mechanisms, new Comparator<InvocationMechanism>() {

			@Override
			public int compare(InvocationMechanism o1, InvocationMechanism o2) {
				return o1.getName().compareTo(o2.getName());
			}});
		mechanismList.setListData(mechanisms.toArray());
		
	}

	public void addListSelectionListener(
			ListSelectionListener listSelectionListener) {
		mechanismList.addListSelectionListener(listSelectionListener);
	}

	public InvocationMechanism getSelectedMechanism() {
		return (InvocationMechanism) mechanismList.getSelectedValue();
	}
	
	private JPanel createButtonsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		result.add(addInvocationMechanismButton());
		return result;
	}

	private JButton addInvocationMechanismButton() {
		JButton result = new JButton();
		result.setAction(new AbstractAction("Add location") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> usedMechanismNames = new HashSet<String>();
				for (InvocationMechanism m : manager.getMechanisms()) {
					usedMechanismNames.add(m.getName());
				}

				MechanismPanel inputPanel = new MechanismPanel();
				
				ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
						"Add invocation location", inputPanel);
				vuid.addTextComponentValidation(inputPanel.getMechanismNameField(),
						"Set the location name.", usedMechanismNames,
						"Duplicate mechanism name.", "[\\p{L}\\p{Digit}_.]+",
						"Invalid mechanism name.");
				vuid.addMessageComponent(inputPanel.getMechanismTypeSelector(), "Set the location type.");
				vuid.setSize(new Dimension(400, 250));

				if (vuid.show(null)) {
					String mechanismName = inputPanel.getMechanismName();
					String mechanismTypeName = inputPanel.getMechanismTypeName();
					InvocationMechanismEditor ime = findEditor(mechanismTypeName);
					InvocationMechanism newMechanism = ime.createMechanism(mechanismName);
					manager.addMechanism(newMechanism);
					setSelectedMechanism(newMechanism);
				}
			}});
		return result;
	}

	InvocationMechanismEditor findEditor(String name) {
		for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry.getInstances()) {
			if (ime.getName().equals(name)) {
				return ime;
			}
		}
		return null;
	}
	@Override
	public void change() {
		populateList();
		this.repaint();
	}

	public void setSelectedMechanism(InvocationMechanism m) {
		if (m == null) {
			mechanismList.clearSelection();
		} else {
			mechanismList.setSelectedValue(m, true);
		}
	}
}
