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

import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;

import org.apache.log4j.Logger;

import de.uni_luebeck.inb.knowarc.usecases.RuntimeEnvironmentConstraint;

/**
 * @author alanrw
 *
 */
public class GroupEditsPanel extends JPanel implements Observer<InvocationManagerEvent> {
	
	private static Logger logger = Logger.getLogger(GroupEditsPanel.class);
	
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	private InvocationGroup group = null;
	
	private JList mechanismList = new JList();
	
	private DefaultListModel mechanismListModel = new DefaultListModel();

	private JPanel buttonPanel;
	
	public GroupEditsPanel(JButton closeButton) {
		super();
		manager.addObserver(this);
		this.setLayout(new BorderLayout());
		buttonPanel = createButtonPanel(closeButton);
		initialise();
	}
	
	private void initialise() {
		this.clear();
		populateList();
		mechanismList.setModel(mechanismListModel);
		this.add(new JLabel("Associated location"), BorderLayout.NORTH);
		JScrollPane mechanismListPane = new JScrollPane(mechanismList);
		this.add(mechanismListPane, BorderLayout.CENTER);
		if ((group != null) && manager.containsGroup(group)) {
			mechanismList.setSelectedValue(group.getMechanism(), true);
		}
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	private JPanel createButtonPanel(JButton closeButton) {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		JButton cancelButton = new JButton(new AbstractAction("Revert") {

			@Override
			public void actionPerformed(ActionEvent e) {
				GroupEditsPanel.this.showGroup(group);
			}});
		result.add(cancelButton);
		JButton saveButton = new JButton(new AbstractAction("Update") {

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationMechanism mechanism = (InvocationMechanism) mechanismList.getSelectedValue();
				if ((mechanism != null) && (group != null)) {
					logger.info("Changing mechanism for " + group.getName() + " from " + group.getMechanism().getName() + " to " + mechanism.getName());
					group.setMechanism(mechanism);
					manager.groupChanged(group);
				}
			}
			
		});
		result.add(saveButton);
		result.add(closeButton);
		return result;
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


	public void clear() {
		group = null;
		this.removeAll();
		this.repaint();
	}

	public void showGroup(InvocationGroup group) {
		this.group = group;
		if (group == null) {
			mechanismList.clearSelection();
		} else {
			mechanismList.setSelectedValue(group.getMechanism(), true);
		}
	}

	@Override
	public void notify(Observable<InvocationManagerEvent> sender,
			InvocationManagerEvent message) throws Exception {
		if (message instanceof InvocationMechanismRemovedEvent) {
			InvocationMechanism removedMechanism = ((InvocationMechanismRemovedEvent) message).getRemovedMechanism();
			InvocationMechanism currentSelection = (InvocationMechanism) mechanismList.getSelectedValue();
			mechanismListModel.removeElement(removedMechanism);
			if (currentSelection.equals(removedMechanism)) {
				showGroup(this.group);
			}
		} else if (message instanceof InvocationMechanismAddedEvent) {
			populateList();
		}
	}

}
