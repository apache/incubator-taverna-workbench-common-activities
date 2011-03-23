/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.taverna.t2.spi.SPIRegistry;

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
		
		this.add(new JLabel("Mechanisms"), BorderLayout.NORTH);

		populateList();
		this.add(mechanismList, BorderLayout.CENTER);
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
	
	private static JPopupMenu addInvocationMechanismMenu = createInvocationMechanismMenu();

	private JButton addInvocationMechanismButton() {
		JButton result = new JButton();
		result.setAction(new AbstractAction("Add mechanism") {

			@Override
			public void actionPerformed(ActionEvent e) {
				addInvocationMechanismMenu.show(MechanismListPanel.this, 0, MechanismListPanel.this.getHeight());
			}});
		return result;
	}

	private static JPopupMenu createInvocationMechanismMenu() {
		JPopupMenu result = new JPopupMenu("Add mechanism");
		for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry.getInstances()) {
			result.add(new AddMechanismAction(ime.getName(), ime));
		}
		return result;
	}

	@Override
	public void change() {
		populateList();
		this.repaint();
	}
}
