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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;


/**
 * @author alanrw
 *
 */
public class GroupListPanel extends JPanel implements InvocationGroupManagerListener {
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	private JList groupList = new JList();

	public GroupListPanel() {
		super();
		manager.addListener(this);
		this.setLayout(new BorderLayout());
		
		this.add(new JLabel("Groups"), BorderLayout.NORTH);

		populateList();
		this.add(groupList, BorderLayout.CENTER);
		JPanel groupListButtonPanel = createButtonsPanel();
		this.add(groupListButtonPanel, BorderLayout.SOUTH);
	}

	private void populateList() {
		ArrayList<InvocationGroup> groups = new ArrayList<InvocationGroup>();
		groups.addAll(manager.getInvocationGroups());
		Collections.sort(groups, new Comparator<InvocationGroup>() {

			@Override
			public int compare(InvocationGroup o1, InvocationGroup o2) {
				return o1.getInvocationGroupName().compareTo(o2.getInvocationGroupName());
			}});
		groupList.setListData(groups.toArray());
		
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
		return result;
	}
	
	private JButton addInvocationGroupButton() {
		JButton result = new JButton();
		result.setAction(new AbstractAction("Add group") {

			@Override
			public void actionPerformed(ActionEvent e) {
				String groupName = JOptionPane.showInputDialog(null, "Group name", "Add group", JOptionPane.QUESTION_MESSAGE);
				if (groupName != null) {
					InvocationGroup newGroup = new InvocationGroup();
					newGroup.setInvocationGroupName(groupName);
					newGroup.setMechanism(manager.getDefaultMechanism());
					manager.addInvocationGroup(newGroup);
				}
			}});
		return result;
	}

	@Override
	public void change() {
		populateList();
		this.repaint();
	}

}
