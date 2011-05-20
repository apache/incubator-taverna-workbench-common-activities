/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.manager.AddInvocationMechanismAction;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroup;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismAddedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismEditor;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismRemovedEvent;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;

/**
 * @author alanrw
 *
 */
public class InvocationPanel extends JPanel implements Observer<InvocationManagerEvent> {
	
	private static final String LOCATION_DESCRIPTION = "The location specifies where the tool will be run.";
	private final JComboBox invocationSelection;
	
	private DefaultComboBoxModel invocationSelectionModel = new DefaultComboBoxModel();

	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	public InvocationPanel(ExternalToolActivityConfigurationBean configuration) {
		super();
		manager.addObserver(this);
		invocationSelection = new JComboBox();
		populateMechanismList();
		invocationSelection.setModel(invocationSelectionModel);
		populateInvocationPanel(configuration);
	}
	
	private void populateMechanismList() {
		InvocationMechanism currentSelection = (InvocationMechanism) invocationSelection.getSelectedItem();
		InvocationMechanism[] mechanisms = InvocationGroupManager.getInstance()
				.getMechanisms().toArray(new InvocationMechanism[] {});
		Arrays.sort(mechanisms, new Comparator<InvocationMechanism>() {

			@Override
			public int compare(InvocationMechanism arg0, InvocationMechanism arg1) {
				return arg0.getName().compareTo(
						arg1.getName());
			}
		});
		invocationSelectionModel.removeAllElements();
		for (InvocationMechanism mechanism : mechanisms) {
			invocationSelectionModel.addElement(mechanism);
		}
		if (currentSelection != null) {
			invocationSelection.setSelectedItem(currentSelection);
		}
		
	}

	
	private void populateInvocationPanel(ExternalToolActivityConfigurationBean configuration) {
		this.removeAll();
		this.setLayout(new BorderLayout());

		JTextArea descriptionText = new JTextArea(
				LOCATION_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		this.add(descriptionText, BorderLayout.NORTH);
		
		ButtonGroup mechanismOrGroup = new ButtonGroup();

		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel locationPanel = new JPanel(new BorderLayout());
		
		JPanel invocationSelectionPanel = new JPanel(new GridLayout(1, 2));
		JRadioButton locationSelected = new JRadioButton("Select a location");
		mechanismOrGroup.add(locationSelected);
		locationSelected.setSelected(configuration.getInvocationGroup() == null);
		locationSelected.setBorder(new EmptyBorder(10, 10, 10, 10));
		invocationSelectionPanel.add(locationSelected);

		invocationSelection.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList arg0,
					Object arg1, int arg2, boolean arg3, boolean arg4) {
				if (arg1 instanceof InvocationGroup) {
					return super.getListCellRendererComponent(arg0,
							((InvocationGroup) arg1).getInvocationGroupName(),
							arg2, arg3, arg4);
				}
				return super.getListCellRendererComponent(arg0, arg1, arg2,
						arg3, arg4);
			}
		});

		invocationSelection.setEnabled(locationSelected.isSelected());
		invocationSelectionPanel.add(invocationSelection);

		locationPanel.add(invocationSelectionPanel, BorderLayout.CENTER);

		invocationSelection.setSelectedItem(configuration.getMechanism());

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton addLocation = new JButton(
				new AddInvocationMechanismAction(true) {
					public void actionPerformed(ActionEvent e) {
						super.actionPerformed(e);
						if (getNewGroup() != null) {
							invocationSelection.setSelectedItem(getNewMechanism());
							InvocationMechanismEditor chosenEditor = null;
							for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry
									.getInstances()) {
								if (ime.canShow(getNewMechanism().getClass())) {
									chosenEditor = ime;
									break;
								}
							}
							if (chosenEditor != null) {
								chosenEditor.show(getNewMechanism());
								chosenEditor.setPreferredSize(new Dimension(
										550, 500));
								int answer = JOptionPane.showConfirmDialog(
										null, chosenEditor, "New location",
										JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
								if (answer == JOptionPane.OK_OPTION) {
									chosenEditor.updateInvocationMechanism();
									InvocationGroupManager
											.getInstance()
											.mechanismChanged(getNewMechanism());
								}
							}
						}
					}
				});
/*		JButton manageInvocation = new JButton(new AbstractAction(
				"Edit locations") {

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationManagerUI ui = InvocationManagerUI.getInstance();
				ui.showMechanismForGroup((InvocationGroup) invocationSelection
						.getSelectedItem());
				ui.setVisible(true);
			}
		}); */
		addLocation.setEnabled(locationSelected.isSelected());
		buttonPanel.add(addLocation);
/*		buttonPanel.add(manageInvocation); */
		locationPanel.add(buttonPanel, BorderLayout.SOUTH);
		subPanel.add(locationPanel, BorderLayout.NORTH);
		
		JPanel groupPanel = new JPanel(new BorderLayout());
		
		JPanel groupSelectionPanel = new JPanel(new GridLayout(1, 2));
		JRadioButton groupSelected = new JRadioButton("Select a group");
		mechanismOrGroup.add(groupSelected);
		groupSelected.setSelected(configuration.getInvocationGroup() != null);
		groupSelected.setBorder(new EmptyBorder(10, 10, 10, 10));
		groupSelectionPanel.add(groupSelected);
/*
		invocationSelection.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList arg0,
					Object arg1, int arg2, boolean arg3, boolean arg4) {
				if (arg1 instanceof InvocationGroup) {
					return super.getListCellRendererComponent(arg0,
							((InvocationGroup) arg1).getInvocationGroupName(),
							arg2, arg3, arg4);
				}
				return super.getListCellRendererComponent(arg0, arg1, arg2,
						arg3, arg4);
			}
		});

		invocationSelection.setEnabled(locationSelected.isSelected());
		invocationSelectionPanel.add(invocationSelection); */

		groupPanel.add(groupSelectionPanel, BorderLayout.CENTER);

/*		invocationSelection.setSelectedItem(configuration.getMechanism());*/
/*
		JPanel groupButtonPanel = new JPanel(new FlowLayout());
		JButton addGroup = new JButton(
				new AddInvocationGroupAction(true) {
					public void actionPerformed(ActionEvent e) {
						super.actionPerformed(e);
						if (getNewGroup() != null) {
							invocationSelection.setSelectedItem(getNewMechanism());
							InvocationMechanismEditor chosenEditor = null;
							for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry
									.getInstances()) {
								if (ime.canShow(getNewMechanism().getClass())) {
									chosenEditor = ime;
									break;
								}
							}
							if (chosenEditor != null) {
								chosenEditor.show(getNewMechanism());
								chosenEditor.setPreferredSize(new Dimension(
										550, 500));
								int answer = JOptionPane.showConfirmDialog(
										null, chosenEditor, "New location",
										JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null);
								if (answer == JOptionPane.OK_OPTION) {
									chosenEditor.updateInvocationMechanism();
									InvocationGroupManager
											.getInstance()
											.mechanismChanged(getNewMechanism());
								}
							}
						}
					}
				});*/
/*		JButton manageInvocation = new JButton(new AbstractAction(
				"Edit locations") {

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationManagerUI ui = InvocationManagerUI.getInstance();
				ui.showMechanismForGroup((InvocationGroup) invocationSelection
						.getSelectedItem());
				ui.setVisible(true);
			}
		}); */
//		addLocation.setEnabled(locationSelected.isSelected());
//		groupButtonPanel.add(addLocation);
/*		buttonPanel.add(manageInvocation); */
//		groupPanel.add(buttonPanel, BorderLayout.SOUTH);
		subPanel.add(groupPanel, BorderLayout.SOUTH);
		

		this.add(subPanel, BorderLayout.CENTER);
		this.repaint();
	}

	@Override
	public void notify(Observable<InvocationManagerEvent> sender,
			InvocationManagerEvent message) throws Exception {
		if (message instanceof InvocationMechanismRemovedEvent) {
			InvocationMechanism removedMechanism = ((InvocationMechanismRemovedEvent) message).getRemovedMechanism();
			if (invocationSelection.getSelectedItem().equals(removedMechanism)) {
				invocationSelection.setSelectedItem(InvocationGroupManager.getInstance().getDefaultMechanism());
			}
			invocationSelectionModel.removeElement(removedMechanism);
		} else if (message instanceof InvocationMechanismAddedEvent) {
			populateMechanismList();
		}
	}

	public InvocationMechanism getSelectedMechanism() {
		return (InvocationMechanism) invocationSelection.getSelectedItem();
	}
}
