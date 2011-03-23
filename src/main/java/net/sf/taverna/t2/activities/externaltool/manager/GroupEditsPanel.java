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
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import de.uni_luebeck.inb.knowarc.grid.re.RuntimeEnvironmentConstraint;

/**
 * @author alanrw
 *
 */
public class GroupEditsPanel extends JPanel implements InvocationGroupManagerListener {
	
	private static Logger logger = Logger.getLogger(GroupEditsPanel.class);
	
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	private InvocationGroup group = null;
	
	private JList mechanismList = new JList();

	private JPanel buttonPanel;
	
	public GroupEditsPanel() {
		super();
		manager.addListener(this);
		this.setLayout(new BorderLayout());
		buttonPanel = createButtonPanel();

	}
	
	private JPanel createButtonPanel() {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		JButton cancelButton = new JButton(new AbstractAction("Cancel") {

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
					logger.info("Changing mechanism for " + group.getInvocationGroupName() + " from " + group.getMechanism().getName() + " to " + mechanism.getName());
					group.setMechanism(mechanism);
				}
			}
			
		});
		result.add(saveButton);
		return result;
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


	public void clear() {
		group = null;
		this.removeAll();
		this.repaint();
	}

	public void showGroup(InvocationGroup group) {
		clear();
		this.group = group;
		populateList();
		this.add(new JLabel(group.getInvocationGroupName()), BorderLayout.NORTH);
		this.add(mechanismList, BorderLayout.CENTER);
		mechanismList.setSelectedValue(group.getMechanism(), true);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}

	@Override
	public void change() {
		if ((group == null) || !manager.containsGroup(group)) {
			clear();
		}
		else {
			showGroup(group);
		}
	}

}
