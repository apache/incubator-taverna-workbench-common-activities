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
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractAction;
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

import org.apache.log4j.Logger;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityHealthChecker;
import net.sf.taverna.t2.activities.externaltool.manager.AddInvocationMechanismAction;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroup;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupAddedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupRemovedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerUI;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismAddedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismEditor;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismRemovedEvent;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.workflowmodel.processor.activity.AbstractActivity;

/**
 * @author alanrw
 *
 */
public class InvocationPanel extends JPanel implements Observer<InvocationManagerEvent> {
	
	private static final String LOCATION_DESCRIPTION = "The location specifies where the tool will be run.";
	private final JComboBox mechanismSelection;
	private final JComboBox groupSelection;
	
	private DefaultComboBoxModel mechanismSelectionModel = new DefaultComboBoxModel();
	private DefaultComboBoxModel groupSelectionModel = new DefaultComboBoxModel();

	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();
	
	private static Logger logger = Logger
	.getLogger(InvocationPanel.class);
	
	private JRadioButton unmanagedLocation;
	private JRadioButton groupSelected;
	private JRadioButton mechanismSelected;
	private JButton addMechanism;
	private JButton manageInvocation;
	private ButtonGroup mechanismOrGroup;
	private ExternalToolActivityConfigurationBean configuration;
	
	private ActionListener radioChangeListener;
	
	boolean unmanagedShown = false;

	public InvocationPanel(ExternalToolActivityConfigurationBean configuration) {
		super();
		manager.addObserver(this);
		
		mechanismSelection = new JComboBox();
		populateMechanismList();
		mechanismSelection.setModel(mechanismSelectionModel);
		
		groupSelection = new JComboBox();
		populateGroupList();
		groupSelection.setModel(groupSelectionModel);
		populateInvocationPanel(configuration);
		
		radioChangeListener = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (unmanagedShown && unmanagedLocation.isSelected()) {
					setUnmanagedLocationSelectability(true);
					setMechanismSelectability(false);
					setGroupSelectability(false);
					return;
				}
				if (mechanismSelected.isSelected()) {
					if (unmanagedShown) {
						setUnmanagedLocationSelectability(false);
					}
					setMechanismSelectability(true);
					setGroupSelectability(false);
					return;
				}
				if (unmanagedShown) {
					setUnmanagedLocationSelectability(false);
				}
				setMechanismSelectability(false);
				setGroupSelectability(true);
				return;			}
			
		};
		if (unmanagedShown) {
			unmanagedLocation.addActionListener(radioChangeListener);
		}
		groupSelected.addActionListener(radioChangeListener);
		mechanismSelected.addActionListener(radioChangeListener);
	}
	
	private void populateMechanismList() {
		InvocationMechanism currentSelection = (InvocationMechanism) mechanismSelection.getSelectedItem();
		InvocationMechanism[] mechanisms = InvocationGroupManager.getInstance()
				.getMechanisms().toArray(new InvocationMechanism[] {});
		Arrays.sort(mechanisms, new Comparator<InvocationMechanism>() {

			@Override
			public int compare(InvocationMechanism arg0, InvocationMechanism arg1) {
				return arg0.getName().compareTo(
						arg1.getName());
			}
		});
		mechanismSelectionModel.removeAllElements();
		for (InvocationMechanism mechanism : mechanisms) {
			mechanismSelectionModel.addElement(mechanism);
			logger.info("Added mechanism " + mechanism.hashCode());
		}
		if (currentSelection != null) {
			mechanismSelection.setSelectedItem(currentSelection);
		}
		
	}

	private void populateGroupList() {
		InvocationGroup currentSelection = (InvocationGroup) groupSelection.getSelectedItem();
		InvocationGroup[] groups = InvocationGroupManager.getInstance()
				.getInvocationGroups().toArray(new InvocationGroup[] {});
		Arrays.sort(groups, new Comparator<InvocationGroup>() {

			@Override
			public int compare(InvocationGroup arg0, InvocationGroup arg1) {
				return arg0.getName().compareTo(
						arg1.getName());
			}
		});
		groupSelectionModel.removeAllElements();
		for (InvocationGroup group : groups) {
			groupSelectionModel.addElement(group);
			logger.info("Added group " + group.hashCode());
		}
		if (currentSelection != null) {
			groupSelection.setSelectedItem(currentSelection);
		}
		
	}

	
	private void populateInvocationPanel(ExternalToolActivityConfigurationBean configuration) {
		this.configuration = configuration;
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
		
		mechanismOrGroup = new ButtonGroup();

		JPanel subPanel = new JPanel(new GridLayout(3,1));
		
		if (isUnmanaged(configuration)) {
			createUnmanagedLocation(subPanel);
			unmanagedShown = true;
		}
		
		createMechanismPanel(subPanel);
		
		createGroupPanel(subPanel);
		
		initializeSelectability();
		this.add(subPanel, BorderLayout.CENTER);
		this.repaint();
	}

	private boolean isUnmanaged(
			ExternalToolActivityConfigurationBean configuration2) {
		return (!ExternalToolActivityHealthChecker.updateLocation(configuration2));
	}

	private void initializeSelectability() {
		if (isUnmanaged(configuration)) {
			unmanagedLocation.setSelected(true);
			setUnmanagedLocationSelectability(true);
			setMechanismSelectability(false);
			setGroupSelectability(false);
			return;
		}
		if (configuration.getInvocationGroup() == null) {
			mechanismSelected.setSelected(true);
			if (unmanagedShown) {
				setUnmanagedLocationSelectability(false);
			}
			setMechanismSelectability(true);
			setGroupSelectability(false);
			return;
		}
		groupSelected.setSelected(true);
		if (unmanagedShown) {
			setUnmanagedLocationSelectability(false);
		}
		setMechanismSelectability(false);
		setGroupSelectability(true);
		return;
	}

	private void setGroupSelectability(boolean b) {
		groupSelection.setEnabled(b);
		manageInvocation.setEnabled(b);
	}

	private void setMechanismSelectability(boolean b) {
		mechanismSelection.setEnabled(b);
		addMechanism.setEnabled(b);
	}

	private void setUnmanagedLocationSelectability(boolean b) {
		// Nothing to do
	}

	private void createGroupPanel(JPanel subPanel) {
		JPanel groupPanel = new JPanel(new BorderLayout());
		
		JPanel groupSelectionPanel = new JPanel(new GridLayout(1, 2));
		groupSelected = new JRadioButton("Select a group");
		mechanismOrGroup.add(groupSelected);
		groupSelected.setBorder(new EmptyBorder(10, 10, 10, 10));
		groupSelectionPanel.add(groupSelected);

		groupSelection.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList arg0,
					Object arg1, int arg2, boolean arg3, boolean arg4) {
				if (arg1 instanceof InvocationGroup) {
					return super.getListCellRendererComponent(arg0,
							((InvocationGroup) arg1).getName(),
							arg2, arg3, arg4);
				}
				return super.getListCellRendererComponent(arg0, arg1, arg2,
						arg3, arg4);
			}
		});

		groupSelectionPanel.add(groupSelection); 

		groupPanel.add(groupSelectionPanel, BorderLayout.CENTER);
		
		if (configuration.getInvocationGroup() != null) {
			groupSelection.setSelectedItem(configuration.getInvocationGroup());
		} else {
			groupSelection.setSelectedItem(manager.getDefaultGroup());
		}

		JPanel groupButtonPanel = new JPanel(new FlowLayout());
		manageInvocation = new JButton(new AbstractAction(
				"Edit locations") {

			@Override
			public void actionPerformed(ActionEvent e) {
				InvocationManagerUI ui = InvocationManagerUI.getInstance();
				ui.setVisible(true);
			}
		}); 
		groupButtonPanel.add(manageInvocation); 
		groupPanel.add(groupButtonPanel, BorderLayout.SOUTH);
		subPanel.add(groupPanel);
	}

	private void createMechanismPanel(JPanel subPanel) {
		JPanel mechanismPanel = new JPanel(new BorderLayout());
		JPanel mechanismSelectionPanel = new JPanel(new GridLayout(1, 3));
		mechanismSelected = new JRadioButton("Select a location");
		mechanismOrGroup.add(mechanismSelected);
		mechanismSelected.setBorder(new EmptyBorder(10, 10, 10, 10));
		mechanismSelectionPanel.add(mechanismSelected);

		mechanismSelection.setRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList arg0,
					Object arg1, int arg2, boolean arg3, boolean arg4) {
				if (arg1 instanceof InvocationMechanism) {
					return super.getListCellRendererComponent(arg0,
							((InvocationMechanism) arg1).getName(),
							arg2, arg3, arg4);
				}
				return super.getListCellRendererComponent(arg0, arg1, arg2,
						arg3, arg4);
			}
		});

		mechanismSelectionPanel.add(mechanismSelection);

		mechanismPanel.add(mechanismSelectionPanel, BorderLayout.CENTER);
		if (configuration.getMechanism() != null) {
			mechanismSelection.setSelectedItem(configuration.getMechanism());
		} else {
			mechanismSelection.setSelectedItem(manager.getDefaultMechanism());
		}

		JPanel buttonPanel = new JPanel(new FlowLayout());
		addMechanism = new JButton(
				new AddInvocationMechanismAction() {
					public void actionPerformed(ActionEvent e) {
						super.actionPerformed(e);
						if (getNewMechanism() != null) {
							mechanismSelection.setSelectedItem(getNewMechanism());
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
		buttonPanel.add(addMechanism);
		mechanismPanel.add(buttonPanel, BorderLayout.SOUTH);
		subPanel.add(mechanismPanel);

	}

	private void createUnmanagedLocation(JPanel subPanel) {
		unmanagedLocation = new JRadioButton("Continue using unmanaged location");
		subPanel.add(unmanagedLocation);
		mechanismOrGroup.add(unmanagedLocation);
	}

	@Override
	public void notify(Observable<InvocationManagerEvent> sender,
			InvocationManagerEvent message) throws Exception {
		if (message instanceof InvocationMechanismRemovedEvent) {
			InvocationMechanism removedMechanism = ((InvocationMechanismRemovedEvent) message).getRemovedMechanism();
			InvocationMechanism replacementMechanism = ((InvocationMechanismRemovedEvent) message).getReplacementMechanism();
			if (mechanismSelection.getSelectedItem().equals(removedMechanism)) {
				mechanismSelection.setSelectedItem(replacementMechanism);
			}
			mechanismSelectionModel.removeElement(removedMechanism);
		} else if (message instanceof InvocationMechanismAddedEvent) {
			populateMechanismList();
		}
		else if (message instanceof InvocationGroupRemovedEvent) {
			InvocationGroup removedGroup = ((InvocationGroupRemovedEvent) message).getRemovedGroup();
			InvocationGroup replacementGroup = ((InvocationGroupRemovedEvent) message).getReplacementGroup();
			if (groupSelection.getSelectedItem().equals(removedGroup)) {
				groupSelection.setSelectedItem(replacementGroup);
			}
			groupSelectionModel.removeElement(removedGroup);
		} else if (message instanceof InvocationGroupAddedEvent) {
			populateGroupList();
		}
	}

	public void fillInConfiguration(
			ExternalToolActivityConfigurationBean newConfiguration) {
		if (unmanagedShown && unmanagedLocation.isSelected()) {
			return;
		}
		if (mechanismSelected.isSelected()) {
			newConfiguration.setMechanism((InvocationMechanism) mechanismSelection.getSelectedItem());
			return;
		}
		newConfiguration.setInvocationGroup((InvocationGroup) groupSelection.getSelectedItem());	
	}
}
