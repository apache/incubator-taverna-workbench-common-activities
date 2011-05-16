/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import de.uni_luebeck.inb.knowarc.usecases.RuntimeEnvironmentConstraint;

import net.sf.taverna.t2.activities.externaltool.servicedescriptions.ExternalToolActivityIcon;

/**
 * @author alanrw
 *
 */
public class InvocationManagerUI extends JFrame {
	
	private static Logger logger = Logger.getLogger(InvocationManagerUI.class);

	private static InvocationManagerUI INSTANCE = null;
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	private MechanismEditsPanel mechanismEditsPanel;

	private MechanismListPanel mechanismListPanel;

	private GroupListPanel groupsListPanel;

	private GroupEditsPanel groupEditsPanel;
	
	private JTabbedPane tabbedPane;
	
	private JPanel mechanismsPanel;

	public static InvocationManagerUI getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new InvocationManagerUI();
		}
		return INSTANCE;
	}
	
	private InvocationManagerUI() {
		getContentPane().setLayout(new BorderLayout());
		
		tabbedPane = new JTabbedPane();
		JPanel groupsPanel = createGroupsPanel();
		mechanismsPanel = createMechanismsPanel();
		tabbedPane.add("Groups", groupsPanel);
		tabbedPane.add("Locations", mechanismsPanel);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		// Handle application close
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				closeFrame();
			}
		});
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		pack();
		// Centre the frame in the centre of the screen
		setLocationRelativeTo(null);

		// Set the frame's icon
		setIconImage( ((ImageIcon) ExternalToolActivityIcon.getExternalToolIcon()).getImage());

		// Set the frame's title
		setTitle("Invocation Manager");
		
		groupsListPanel.setSelectedGroup(manager.getDefaultGroup());

		this.setMinimumSize(new Dimension(700, 400));
		logger.info("UI created");
	}

	protected void closeFrame() {
		manager.saveConfiguration();
		setVisible(false);
		dispose();		
	}

	private JPanel createGroupsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new GridLayout(1,2));
		groupsListPanel = new GroupListPanel();
		result.add(groupsListPanel);
		groupEditsPanel = new GroupEditsPanel(createCloseButton());
		groupsListPanel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				InvocationGroup group = groupsListPanel.getSelectedGroup();
				groupEditsPanel.showGroup(group);
			}});
		
		result.add(groupEditsPanel);
		return result;
	}

	private JPanel createMechanismsPanel() {
		JPanel result = new JPanel();
		result.setLayout(new GridLayout(1,2));
		mechanismListPanel = new MechanismListPanel();
		result.add(mechanismListPanel);
		mechanismEditsPanel = new MechanismEditsPanel(createCloseButton());
		
		mechanismListPanel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				InvocationMechanism mechanism = mechanismListPanel.getSelectedMechanism();
				if (mechanism == null) {
					mechanismEditsPanel.clear();
				} else {
					mechanismEditsPanel.showMechanism(mechanism);
				}
			}});
		groupsListPanel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				InvocationGroup group = groupsListPanel.getSelectedGroup();
				if (group != null) {
					InvocationMechanism m = group.getMechanism();
					if (m != null) {
						mechanismListPanel.setSelectedMechanism(m);
						return;
					}
				}
				mechanismListPanel.setSelectedMechanism(null);
			}});
		result.add(mechanismEditsPanel);
		return result;
	}

	public void showMechanismForGroup(InvocationGroup selectedGroup) {
		groupsListPanel.setSelectedGroup(selectedGroup);
		tabbedPane.setSelectedComponent(mechanismsPanel);
	}
	
	private JButton createCloseButton() {
		JButton result = new JButton(new AbstractAction("Close") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				closeFrame();
			}});
		return result;
	}

}