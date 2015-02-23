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
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.activities.externaltool.utils.Tools;
import net.sf.taverna.t2.lang.ui.DeselectingButton;
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
			final String description, String addText) {
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
		
		final String[] elementLabels = new String[] {"Taverna port name",
				"Use port name for file",
				fileHeader,
				typeHeader
		};

		fileConstraint.gridx = 0;
		synchronized (viewList) {
			for (ExternalToolFileViewer outputView : viewList) {
				addFileViewer(viewList, this, fileEditPanel,
						outputView, elementLabels);
			}
		}
		JButton addFilePortButton = new DeselectingButton(addText,
				new AbstractAction() {
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
							newViewer, elementLabels);
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

		JPanel buttonPanel = new JPanel(new BorderLayout());

		buttonPanel.add(addFilePortButton, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
	
	}
	
	private void addFileViewer(final List<ExternalToolFileViewer> viewList,
			final JPanel outerPanel, final JPanel panel,
			ExternalToolFileViewer viewer, String[] elementLabels) {
		Tools.addViewer(panel,
				elementLabels,
				new JComponent[] {viewer.getNameField(), viewer.getValueFromField(), viewer.getValueField(), viewer.getTypeSelector()},
				viewList,
				viewer,
				outerPanel);
	}

}
