/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.activities.externaltool.manager.ssh.ExternalToolSshNodeViewer;
import net.sf.taverna.t2.activities.externaltool.utils.Tools;
import net.sf.taverna.t2.lang.ui.DeselectingButton;

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
	
	private static Insets insets = new Insets(1,5,1,5);
	
	private static String[] elementLabels = new String[] {"Taverna port name", "Replace port name", "String to replace"};
	
	private static CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5), BorderFactory.createLineBorder(Color.BLACK, 1));

	
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

		inputConstraint.gridx = 0;
		synchronized (stringReplacementViewList) {
			for (ExternalToolStringReplacementViewer inputView : stringReplacementViewList) {
				addStringReplacementViewer(this, inputEditPanel,
						inputView, elementLabels);

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
		JButton addInputPortButton = new DeselectingButton("Add string replacement",
				new AbstractAction() {

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
							newViewer, elementLabels);
					inputEditPanel.revalidate();
					inputEditPanel.repaint();
				}

			}

		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());

		buttonPanel.add(addInputPortButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addStringReplacementViewer(final JPanel outerPanel,
			final JPanel innerPanel, final ExternalToolStringReplacementViewer viewer, String[] elementLabels) {
		Tools.addViewer(innerPanel,
				elementLabels,
				new JComponent[] {viewer.getNameField(), viewer.getValueFromField(), viewer.getValueField()},
				stringReplacementViewList,
				viewer,
				outerPanel);
	}


}
