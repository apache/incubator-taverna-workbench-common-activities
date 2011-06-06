/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * @author alanrw
 *
 */
public class StringReplacementPanel extends JPanel {
	
	private static final String STRING_REPLACEMENT_DESCRIPTION = "You can use a string replacement to " +
	"feed data into the service via an input port and have that data replace part of the " +
	"command.";
	private final List<ExternalToolStringReplacementViewer> stringReplacementViewList;
	private int stringReplacementGridy = 1;
	private final ExternalToolConfigView view;
	
	public StringReplacementPanel(final ExternalToolConfigView view, final List<ExternalToolStringReplacementViewer> stringReplacementViewList) {
		super(new BorderLayout());
		this.view = view;
		this.stringReplacementViewList = stringReplacementViewList;

		final JPanel inputEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints inputConstraint = new GridBagConstraints();

		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputEditPanel.add(new JLabel("Taverna port name"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("Replace port name"), inputConstraint);
		inputConstraint.gridx++;
		inputEditPanel.add(new JLabel("String to replace"), inputConstraint);

		inputConstraint.gridx = 0;
		synchronized (stringReplacementViewList) {
			for (ExternalToolStringReplacementViewer inputView : stringReplacementViewList) {
				addStringReplacementViewer(this, inputEditPanel,
						inputView);

			}
		}

		JTextArea descriptionText = new JTextArea(
				STRING_REPLACEMENT_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);

		this.add(descriptionText, BorderLayout.NORTH);
		this.add(new JScrollPane(inputEditPanel),
				BorderLayout.CENTER);
		JButton addInputPortButton = new JButton(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {

				int portNumber = 1;
				String name2 = "in" + portNumber++;
				boolean nameExists = true;
				while (nameExists == true) {
					nameExists = view.portNameExists(name2);
					if (nameExists) {
						name2 = "in" + portNumber++;
					}
				}

				ExternalToolStringReplacementViewer newViewer = new ExternalToolStringReplacementViewer(
						name2);
				synchronized (stringReplacementViewList) {
					stringReplacementViewList.add(newViewer);
					addStringReplacementViewer(StringReplacementPanel.this, inputEditPanel,
							newViewer);
					inputEditPanel.revalidate();
					inputEditPanel.repaint();
				}

			}

		});

		addInputPortButton.setText("Add string replacement");
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		buttonPanel.add(addInputPortButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addStringReplacementViewer(final JPanel outerPanel,
			final JPanel panel, ExternalToolStringReplacementViewer viewer) {
		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputConstraint.gridy = stringReplacementGridy ;
		inputConstraint.gridx = 0;

		final JTextField nameField = viewer.getNameField();
		panel.add(nameField, inputConstraint);
		inputConstraint.gridx++;
		
		final JCheckBox valueFromField = viewer.getValueFromField();
		panel.add(valueFromField, inputConstraint);
		inputConstraint.gridx++;

		final JTextField valueField = viewer.getValueField();
		panel.add(valueField, inputConstraint);
		inputConstraint.gridx++;

		final JButton removeButton = new JButton("Remove");
		final ExternalToolStringReplacementViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (stringReplacementViewList) {
					stringReplacementViewList.remove(v);
				}
				panel.remove(nameField);
				panel.remove(valueFromField);
				panel.remove(valueField);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, inputConstraint);
		inputConstraint.gridy = ++stringReplacementGridy;

	}


}
