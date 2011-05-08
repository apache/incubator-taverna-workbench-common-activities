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
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sf.taverna.t2.spi.SPIRegistry;
import net.sf.taverna.t2.workbench.design.ui.DataflowInputPortPanel;
import net.sf.taverna.t2.workflowmodel.DataflowInputPort;

/**
 * @author alanrw
 *
 */
public class MechanismListPanel extends JPanel implements InvocationGroupManagerListener {
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();
	
	private JList mechanismList = new JList();

	public MechanismListPanel() {
		super();
		mechanismList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		result.add(removeInvocationMechanismButton());
		return result;
	}

	private JButton removeInvocationMechanismButton() {
		JButton result = new JButton(new AbstractAction("Remove location"){

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationMechanism toRemove = (InvocationMechanism) mechanismList.getSelectedValue();
				if (!toRemove.equals(manager.getDefaultMechanism())) {
					manager.removeMechanism(toRemove);
					mechanismList.setSelectedValue(manager.getDefaultMechanism(), true);
				}
			}});
		return result;
	}
	
	private JButton addInvocationMechanismButton() {
		JButton result = new JButton();
		result.setAction(new AddInvocationMechanismAction(false) {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				setSelectedMechanism(getNewMechanism());
			}
		});
		return result;
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
