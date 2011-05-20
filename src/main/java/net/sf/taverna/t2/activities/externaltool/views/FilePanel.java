/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.lang.ui.ReadOnlyTextArea;

/**
 * @author alanrw
 *
 */
public class FilePanel extends JPanel {
	
	private int outputGridy = 1;
	private final ExternalToolConfigView view;
	
	public FilePanel(final ExternalToolConfigView view,
			final List<ExternalToolFileViewer> viewList,
			String fileHeader, String typeHeader, final String portPrefix,
			final String description) {
		super();
		this.view = view;
		this.setLayout(new BorderLayout());
		final JPanel fileEditPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints fileConstraint = new GridBagConstraints();
		fileConstraint.insets = new Insets(5, 5, 5, 5);
		fileConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		fileConstraint.gridx = 0;
		fileConstraint.gridy = 0;
		fileConstraint.weightx = 0.1;
		fileConstraint.fill = GridBagConstraints.BOTH;

		fileEditPanel.add(new JLabel("Taverna port name"), fileConstraint);
		fileConstraint.gridx++;
		fileEditPanel.add(new JLabel(fileHeader), fileConstraint);
		fileConstraint.gridx++;
		fileEditPanel.add(new JLabel(typeHeader), fileConstraint);

		fileConstraint.gridx = 0;
		synchronized (viewList) {
			for (ExternalToolFileViewer outputView : viewList) {
				addFileViewer(viewList, this, fileEditPanel,
						outputView);
			}
		}
		JButton addFilePortButton = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				int portNumber = 1;

				String name2 = portPrefix + portNumber++;
				boolean nameExists = true;
				while (nameExists == true) {
					nameExists = view.portNameExists(name2);
					if (nameExists) {
						name2 = portPrefix + portNumber++;
					}
				}

				ExternalToolFileViewer newViewer = new ExternalToolFileViewer(
						name2);
				synchronized (viewList) {
					viewList.add(newViewer);
					addFileViewer(viewList, FilePanel.this, fileEditPanel,
							newViewer);
					fileEditPanel.revalidate();
					fileEditPanel.repaint();
				}
			}

		});
		JTextArea descriptionText = new ReadOnlyTextArea(description);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));

		this.add(descriptionText, BorderLayout.NORTH);

		this.add(new JScrollPane(fileEditPanel), BorderLayout.CENTER);

		addFilePortButton.setText("Add Port");
		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addFilePortButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
	
	}
	
	private void addFileViewer(final List<ExternalToolFileViewer> viewList,
			final JPanel outerPanel, final JPanel panel,
			ExternalToolFileViewer viewer) {
		final GridBagConstraints fileConstraint = new GridBagConstraints();
		fileConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		fileConstraint.weightx = 0.1;
		fileConstraint.fill = GridBagConstraints.BOTH;

		fileConstraint.gridy = outputGridy;
		fileConstraint.gridx = 0;
		final JTextField nameField = viewer.getNameField();
		panel.add(nameField, fileConstraint);

		fileConstraint.weightx = 0.0;
		fileConstraint.gridx++;

		final JTextField valueField = viewer.getValueField();
		panel.add(valueField, fileConstraint);
		fileConstraint.gridx++;

		final JComboBox typeSelector = viewer.getTypeSelector();
		panel.add(typeSelector, fileConstraint);

		fileConstraint.gridx++;
		final JButton removeButton = new JButton("Remove");
		final ExternalToolFileViewer v = viewer;
		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (viewList) {
					viewList.remove(v);
				}
				panel.remove(nameField);
				panel.remove(valueField);
				panel.remove(typeSelector);
				panel.remove(removeButton);
				panel.revalidate();
				panel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		panel.add(removeButton, fileConstraint);
		outputGridy++;

	}

}
