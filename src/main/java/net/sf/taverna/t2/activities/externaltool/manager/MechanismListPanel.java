/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;

/**
 * @author alanrw
 *
 */
public class MechanismListPanel extends JPanel implements Observer<InvocationManagerEvent> {
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();
	
	private JList mechanismList = new JList();
	private DefaultListModel mechanismListModel = new DefaultListModel();

	public MechanismListPanel() {
		super();
		mechanismList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mechanismList.setModel(mechanismListModel);
		manager.addObserver(this);
		this.setLayout(new BorderLayout());
		
		this.add(new JLabel("Locations"), BorderLayout.NORTH);

		populateList();
		this.add(new JScrollPane(mechanismList), BorderLayout.CENTER);
		JPanel mechanismListButtonPanel = createButtonsPanel();
		this.add(mechanismListButtonPanel, BorderLayout.SOUTH);
	}
	
	private void populateList() {
		InvocationMechanism currentSelection = (InvocationMechanism) mechanismList.getSelectedValue();
		ArrayList<InvocationMechanism> mechanisms = new ArrayList<InvocationMechanism>();
		mechanisms.addAll(manager.getMechanisms());
		Collections.sort(mechanisms, new Comparator<InvocationMechanism>() {

			@Override
			public int compare(InvocationMechanism o1, InvocationMechanism o2) {
				return o1.getName().compareTo(o2.getName());
			}});
		mechanismListModel.clear();
		for (InvocationMechanism m : mechanisms) {
			mechanismListModel.addElement(m);
		}
		if (currentSelection != null) {
			mechanismList.setSelectedValue(currentSelection, true);
		}
		
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
		result.setAction(new AddInvocationMechanismAction() {
			public void actionPerformed(ActionEvent e) {
				super.actionPerformed(e);
				setSelectedMechanism(getNewMechanism());
			}
		});
		return result;
	}

	public void setSelectedMechanism(InvocationMechanism m) {
		if (m == null) {
			mechanismList.clearSelection();
		} else {
			mechanismList.setSelectedValue(m, true);
		}
	}

	@Override
	public void notify(Observable<InvocationManagerEvent> sender,
			InvocationManagerEvent message) throws Exception {
		if (message instanceof InvocationMechanismRemovedEvent) {
			mechanismListModel.removeElement(((InvocationMechanismRemovedEvent) message).getRemovedMechanism());
		} else if (message instanceof InvocationMechanismAddedEvent) {
			populateList();
		}
	}
}
