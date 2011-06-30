/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityHealthChecker;
import net.sf.taverna.t2.activities.externaltool.configuration.ToolInvocationConfiguration;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroup;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupAddedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupRemovedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationManagerEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismAddedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismRemovedEvent;
import net.sf.taverna.t2.activities.externaltool.manager.ToolInvocationConfigurationPanel;
import net.sf.taverna.t2.lang.observer.Observable;
import net.sf.taverna.t2.lang.observer.Observer;
import net.sf.taverna.t2.lang.ui.DeselectingButton;
import net.sf.taverna.t2.workbench.ui.impl.configuration.ui.T2ConfigurationFrame;

import org.apache.log4j.Logger;

/**
 * @author alanrw
 *
 */
public class InvocationPanel extends JPanel implements Observer<InvocationManagerEvent> {
	
	private static final String LOCATION_DESCRIPTION = ToolInvocationConfigurationPanel.HEADER_TEXT;
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
		descriptionText.setRows(3);
		this.add(descriptionText, BorderLayout.NORTH);
		
		JPanel innerPanel = new JPanel(new BorderLayout());
		
		mechanismOrGroup = new ButtonGroup();

		JPanel subPanel = new JPanel(new GridLayout(4,1));
		
		if (isUnmanaged(configuration)) {
			createUnmanagedLocation(subPanel);
			unmanagedShown = true;
		}
		
		subPanel.add(createMechanismPanel());
		
		subPanel.add(createGroupPanel());
		
		subPanel.add(createButtonPanel());
		
		innerPanel.add(subPanel, BorderLayout.NORTH);
		innerPanel.add(new JPanel(), BorderLayout.CENTER);
		
		initializeSelectability();
		this.add(innerPanel, BorderLayout.CENTER);
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
	}

	private void setMechanismSelectability(boolean b) {
		mechanismSelection.setEnabled(b);
	}

	private void setUnmanagedLocationSelectability(boolean b) {
		// Nothing to do
	}

	private JPanel createGroupPanel() {
		JPanel groupPanel = new JPanel(new BorderLayout());
		
		JPanel groupSelectionPanel = new JPanel(new GridLayout(1, 2));
		groupSelected = new JRadioButton("Select a symbolic location");
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

		return groupPanel;
	}

	private JPanel createMechanismPanel() {
		JPanel mechanismPanel = new JPanel(new BorderLayout());
		JPanel mechanismSelectionPanel = new JPanel(new GridLayout(1, 3));
		mechanismSelected = new JRadioButton("Select an explicit location");
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
		return mechanismPanel;

	}
	
	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout());
		manageInvocation = new DeselectingButton("Manage locations",
				new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				T2ConfigurationFrame.showConfiguration(ToolInvocationConfiguration.getInstance().getDisplayName());
			}});
		buttonPanel.add(manageInvocation); 
		return buttonPanel;		
	}

	private void createUnmanagedLocation(JPanel subPanel) {
		unmanagedLocation = new JRadioButton("Continue using unmanaged location");
		subPanel.add(unmanagedLocation);
		mechanismOrGroup.add(unmanagedLocation);
	}

	private void handleInvocationManagerMessage(InvocationManagerEvent message) {
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
	
	@Override
	public void notify(Observable<InvocationManagerEvent> sender,
			final InvocationManagerEvent message) throws Exception {
		if (SwingUtilities.isEventDispatchThread()) {
			handleInvocationManagerMessage(message);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					handleInvocationManagerMessage(message);
				}
			});
		}	}

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

	public void stopObserving() {
		manager.removeObserver(this);
	}
}
