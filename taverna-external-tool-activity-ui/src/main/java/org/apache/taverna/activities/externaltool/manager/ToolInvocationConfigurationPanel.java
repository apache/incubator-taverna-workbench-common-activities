/**
 *
 */
package org.apache.taverna.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.apache.taverna.activities.externaltool.manager.InvocationGroup;
import org.apache.taverna.activities.externaltool.manager.InvocationManagerEvent;
import org.apache.taverna.activities.externaltool.manager.InvocationMechanism;
import org.apache.taverna.activities.externaltool.manager.MechanismCreator;

import org.apache.taverna.activities.externaltool.manager.impl.InvocationGroupManagerImpl;
import org.apache.taverna.lang.observer.Observable;
import org.apache.taverna.lang.observer.Observer;
import org.apache.taverna.lang.ui.DeselectingButton;
import org.apache.taverna.lang.ui.ValidatingUserInputDialog;
import org.apache.taverna.workbench.helper.Helper;

/**
 * @author alanrw
 *
 */
public class ToolInvocationConfigurationPanel extends JPanel implements
		Observer<InvocationManagerEvent> {

	public static final String HEADER_TEXT = "A tool can be set to run at an explicit location (e.g. on a specificic machine or one of a set of machines). Alternatively, it can be set to run at a symbolic location, which means the tool will then be run at the explicit location pointed to by the symbolic location.";

	private static InvocationGroupManagerImpl manager = InvocationGroupManagerImpl.getInstance();

	private final List<InvocationMechanismEditor<?>> invocationMechanismEditors;

	private JTextArea headerText;

	private static String EXPLICIT_LOCATIONS = "explicit locations";
	private static String SYMBOLIC_LOCATIONS = "symbolic locations";

	private List<MechanismCreator> mechanismCreators;

	JList locationList = new JList();

	DefaultListModel groupListModel = new DefaultListModel();
	DefaultListModel mechanismListModel = new DefaultListModel();
	JComboBox locationTypeCombo = new JComboBox(new String[] { EXPLICIT_LOCATIONS,
			SYMBOLIC_LOCATIONS });

	public ToolInvocationConfigurationPanel(List<MechanismCreator> mechanismCreators,
			List<InvocationMechanismEditor<?>> invocationMechanismEditors) {
		super();
		this.mechanismCreators = mechanismCreators;
		this.invocationMechanismEditors = invocationMechanismEditors;
		manager.addObserver(this);

		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		headerText = new JTextArea(HEADER_TEXT);
		headerText.setLineWrap(true);
		headerText.setWrapStyleWord(true);
		headerText.setEditable(false);
		headerText.setFocusable(false);
		headerText.setBorder(new EmptyBorder(10, 10, 10, 10));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 0, 10, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(headerText, gbc);

		JPanel locationPanel = new JPanel(new BorderLayout());
		JPanel subPanel = new JPanel(new FlowLayout());
		JLabel modify = new JLabel("Modify:");

		locationTypeCombo.setSelectedItem(EXPLICIT_LOCATIONS);
		locationTypeCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switchList();
			}
		});
		subPanel.add(modify);
		subPanel.add(locationTypeCombo);

		populateLists();
		switchList();
		locationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		locationList.setCellRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus) {
				Object toShow = value;
				if (value instanceof InvocationGroup) {
					InvocationGroup invocationGroup = (InvocationGroup) value;
					toShow = invocationGroup.getName() + "  -->  "
							+ invocationGroup.getMechanismName();
				}
				return super.getListCellRendererComponent(list, toShow, index, isSelected,
						cellHasFocus);
			}
		});
		locationPanel.add(new JScrollPane(locationList), BorderLayout.CENTER);
		locationPanel.add(subPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton helpButton = new DeselectingButton("Help", new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				Helper.showHelp(ToolInvocationConfigurationPanel.this);
			}
		});

		buttonPanel.add(helpButton);

		buttonPanel.add(addLocationButton());
		buttonPanel.add(removeLocationButton());
		buttonPanel.add(editLocationButton());
		locationPanel.add(buttonPanel, BorderLayout.SOUTH);

		gbc.gridy++;
		gbc.weighty = 1;

		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.insets = new Insets(10, 0, 0, 0);
		this.add(locationPanel, gbc);
	}

	private void switchList() {
		if (isShowingGroups()) {
			locationList.setModel(groupListModel);
		} else {
			locationList.setModel(mechanismListModel);
		}
	}

	private void populateLists() {
		poopulateGroupList();
		populateMechanismList();
	}

	private void populateMechanismList() {
		Object currentSelection = locationList.getSelectedValue();
		ArrayList<InvocationMechanism> mechanisms = new ArrayList<InvocationMechanism>();
		mechanisms.addAll(manager.getMechanisms());
		Collections.sort(mechanisms, new Comparator<InvocationMechanism>() {

			@Override
			public int compare(InvocationMechanism o1, InvocationMechanism o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		mechanismListModel.clear();
		for (InvocationMechanism m : mechanisms) {
			mechanismListModel.addElement(m);
		}
		if ((currentSelection != null) && !isShowingGroups()) {
			locationList.setSelectedValue(currentSelection, true);
		}
	}

	private void poopulateGroupList() {
		Object currentSelection = locationList.getSelectedValue();
		ArrayList<InvocationGroup> groups = new ArrayList<InvocationGroup>();
		groups.addAll(manager.getInvocationGroups());
		Collections.sort(groups, new Comparator<InvocationGroup>() {

			@Override
			public int compare(InvocationGroup o1, InvocationGroup o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		groupListModel.clear();
		for (InvocationGroup g : groups) {
			groupListModel.addElement(g);
		}
		if ((currentSelection != null) && isShowingGroups()) {
			locationList.setSelectedValue(currentSelection, true);
		}
	}

	private boolean isShowingGroups() {
		return (locationTypeCombo.getSelectedItem().equals(SYMBOLIC_LOCATIONS));
	}

	private JButton addLocationButton() {
		final JButton result = new DeselectingButton("Add", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isShowingGroups()) {
					Set<String> usedGroupNames = new HashSet<String>();
					for (InvocationGroup g : manager.getInvocationGroups()) {
						usedGroupNames.add(g.getName());
					}

					GroupPanel inputPanel = new GroupPanel(mechanismListModel.toArray());

					ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
							"Add symbolic location", inputPanel);
					vuid.addTextComponentValidation(inputPanel.getGroupNameField(),
							"Set the symbolic location name.", usedGroupNames,
							"Duplicate symbolic location name.", "[\\p{L}\\p{Digit}_.]+",
							"Invalid symbolic location name.");
					vuid.setSize(new Dimension(400, 250));

					if (vuid.show(ToolInvocationConfigurationPanel.this)) {
						String groupName = inputPanel.getGroupName();
						InvocationGroup newGroup = new InvocationGroup(mechanismCreators);
						newGroup.setName(groupName);
						newGroup.setMechanism(inputPanel.getSelectedMechanism());
						manager.addInvocationGroup(newGroup);
						locationList.setSelectedValue(newGroup, true);
					}
				} else {
					Set<String> usedNames = new HashSet<String>();
					for (InvocationMechanism m : manager.getMechanisms()) {
						usedNames.add(m.getName());
					}

					MechanismPanel inputPanel = new MechanismPanel(invocationMechanismEditors);

					ValidatingUserInputDialog vuid = new ValidatingUserInputDialog(
							"Add explicit location", inputPanel);
					vuid.addTextComponentValidation(inputPanel.getMechanismNameField(),
							"Set the explicit location name.", usedNames,
							"Duplicate explicit location name.", "[\\p{L}\\p{Digit}_.]+",
							"Invalid explicit location name.");
					vuid.addMessageComponent(inputPanel.getMechanismTypeSelector(),
							"Set the location name and type.");
					vuid.setSize(new Dimension(400, 250));

					if (vuid.show(ToolInvocationConfigurationPanel.this)) {
						String mechanismName = inputPanel.getMechanismName();
						String mechanismTypeName = inputPanel.getMechanismTypeName();
						InvocationMechanismEditor ime = findEditor(mechanismTypeName);
						InvocationMechanism newMechanism = ime.createMechanism(mechanismName);
						manager.addMechanism(newMechanism);
						ime.show(newMechanism);
						ime.setPreferredSize(new Dimension(550, 500));
						int answer = JOptionPane.showConfirmDialog(
								ToolInvocationConfigurationPanel.this, ime,
								"New explicit location", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, null);
						if (answer == JOptionPane.OK_OPTION) {
							ime.updateInvocationMechanism();
							InvocationGroupManagerImpl.getInstance().mechanismChanged(newMechanism);
						}
						locationList.setSelectedValue(newMechanism, true);
					}
				}
			}
		});
		return result;
	}

	private JButton removeLocationButton() {
		JButton result = new DeselectingButton("Remove", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isShowingGroups()) {
					InvocationGroup toRemove = (InvocationGroup) locationList.getSelectedValue();
					if ((toRemove != null) && !toRemove.equals(manager.getDefaultGroup())) {
						manager.removeInvocationGroup(toRemove);
					}
					locationList.setSelectedValue(manager.getDefaultGroup(), true);
				} else {
					InvocationMechanism toRemove = (InvocationMechanism) locationList
							.getSelectedValue();
					if ((toRemove != null) && !toRemove.equals(manager.getDefaultMechanism())) {
						manager.removeMechanism(toRemove);
						locationList.setSelectedValue(manager.getDefaultMechanism(), true);
					}
				}
			}
		});
		return result;
	}

	private JButton editLocationButton() {
		final JButton result = new DeselectingButton("Edit", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (isShowingGroups()) {
					InvocationGroup toEdit = (InvocationGroup) locationList.getSelectedValue();
					if (toEdit != null) {
						InvocationMechanism chosenMechanism = (InvocationMechanism) JOptionPane
								.showInputDialog(ToolInvocationConfigurationPanel.this,
										"Select an explicit location", "Edit symbolic location",
										JOptionPane.PLAIN_MESSAGE, null,
										mechanismListModel.toArray(), toEdit.getMechanism());
						if (chosenMechanism != null) {
							toEdit.setMechanism(chosenMechanism);
							manager.groupChanged(toEdit);
						}
					}
				} else {
					InvocationMechanism toEdit = (InvocationMechanism) locationList
							.getSelectedValue();
					if (toEdit != null) {
						InvocationMechanismEditor ime = findEditor(toEdit.getClass());
						ime.show(toEdit);
						ime.setPreferredSize(new Dimension(550, 500));
						int answer = JOptionPane.showConfirmDialog(
								ToolInvocationConfigurationPanel.this, ime,
								"Edit explicit location", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.PLAIN_MESSAGE, null);
						if (answer == JOptionPane.OK_OPTION) {
							ime.updateInvocationMechanism();
							InvocationGroupManagerImpl.getInstance().mechanismChanged(toEdit);
						}
					}
				}
			}
		});
		return result;
	}

	protected InvocationMechanismEditor findEditor(String name) {
		for (InvocationMechanismEditor ime : invocationMechanismEditors) {
			if (ime.getName().equalsIgnoreCase(name)) {
				return ime;
			}
		}
		return null;
	}

	protected InvocationMechanismEditor findEditor(Class c) {
		for (InvocationMechanismEditor ime : invocationMechanismEditors) {
			if (ime.canShow(c)) {
				return ime;
			}
		}
		return null;
	}

	@Override
	public void notify(Observable<InvocationManagerEvent> arg0, InvocationManagerEvent arg1)
			throws Exception {
		if (SwingUtilities.isEventDispatchThread()) {
			populateLists();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					populateLists();
				}
			});
		}
	}
}
