/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

import net.sf.taverna.t2.lang.ui.ValidatingUserInputDialog;


/**
 * @author alanrw
 *
 */
public class GroupListPanel extends JPanel implements InvocationGroupManagerListener {
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	private JList groupList = new JList();
	private DefaultListModel groupListModel = new DefaultListModel();

	public GroupListPanel() {
		super();
		manager.addListener(this);
		this.setLayout(new BorderLayout());
		
		this.add(new JLabel("Groups"), BorderLayout.NORTH);

		populateList();
		groupList.setModel(groupListModel);
		groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane groupListPane = new JScrollPane(groupList);
		
		this.add(groupListPane, BorderLayout.CENTER);
		JPanel groupListButtonPanel = createButtonsPanel();
		this.add(groupListButtonPanel, BorderLayout.SOUTH);
	}

	private void populateList() {
		InvocationGroup currentSelection = (InvocationGroup) groupList.getSelectedValue();
		ArrayList<InvocationGroup> groups = new ArrayList<InvocationGroup>();
		groups.addAll(manager.getInvocationGroups());
		Collections.sort(groups, new Comparator<InvocationGroup>() {

			@Override
			public int compare(InvocationGroup o1, InvocationGroup o2) {
				return o1.getInvocationGroupName().compareTo(o2.getInvocationGroupName());
			}});
		groupListModel.clear();
		for (InvocationGroup g : groups) {
			groupListModel.addElement(g);
		}
		if (currentSelection != null) {
			groupList.setSelectedValue(currentSelection, true);
		}
	}
	
	public void addListSelectionListener(
			ListSelectionListener listSelectionListener) {
		groupList.addListSelectionListener(listSelectionListener);
	}

	public InvocationGroup getSelectedGroup() {
		return (InvocationGroup) groupList.getSelectedValue();
	}
	
	private JPanel createButtonsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		result.add(addInvocationGroupButton());
		result.add(removeInvocationGroupButton());
		return result;
	}
	
	private JButton removeInvocationGroupButton() {
		JButton result = new JButton(new AbstractAction("Remove group"){

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationGroup toRemove = (InvocationGroup) groupList.getSelectedValue();
				if (!toRemove.equals(manager.getDefaultGroup())) {
					manager.removeInvocationGroup(toRemove);
				}
				groupList.setSelectedValue(manager.getDefaultGroup(), true);
			}});
		return result;
	}
	
	private JButton addInvocationGroupButton() {
		JButton result = new JButton();
		result.setAction(new AbstractAction("Add group") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Set<String> usedGroupNames = new HashSet<String>();
				for (InvocationGroup g : manager.getInvocationGroups()) {
					usedGroupNames.add(g.getInvocationGroupName());
				}

				GroupPanel inputPanel = new GroupPanel();
				
				ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
						"Add invocation group", inputPanel);
				vuid.addTextComponentValidation(inputPanel.getGroupNameField(),
						"Set the group name.", usedGroupNames,
						"Duplicate group name.", "[\\p{L}\\p{Digit}_.]+",
						"Invalid group name.");
				vuid.setSize(new Dimension(400, 250));

				if (vuid.show(null)) {
					String groupName = inputPanel.getGroupName();
					InvocationGroup newGroup = new InvocationGroup();
					newGroup.setInvocationGroupName(groupName);
					newGroup.setMechanism(manager.getDefaultMechanism());
					manager.addInvocationGroup(newGroup);
					groupList.setSelectedValue(newGroup, true);
				}
			}});
		return result;
	}

	@Override
	public void invocationManagerChange(InvocationManagerEvent event) {
		if (event instanceof InvocationGroupRemovedEvent) {
			groupListModel.removeElement(((InvocationGroupRemovedEvent) event).getRemovedGroup());
		} else {
			populateList();
		}
	}

	public void setSelectedGroup(InvocationGroup selectedGroup) {
		groupList.setSelectedValue(selectedGroup, true);
	}

}