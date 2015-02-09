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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.activities.externaltool.utils.Tools;
import net.sf.taverna.t2.lang.ui.DeselectingButton;
import net.sf.taverna.t2.lang.ui.ReadOnlyTextArea;

/**
 * @author alanrw
 *
 */
public class StaticUrlPanel extends JPanel {
	
	private static final String STATIC_URL_DESCRIPTION = "The data at a URL can be downloaded and stored in the specified file.";
	private final List<ExternalToolStaticUrlViewer> staticUrlViewList;
	private int staticGridy = 1;

	private static String[] elementLabels = new String[] {"Copy from URL", "To file"};
	

	public StaticUrlPanel(final List<ExternalToolStaticUrlViewer> staticUrlViewList) {
	
	super(new BorderLayout());
	this.staticUrlViewList = staticUrlViewList;
	final JPanel staticEditPanel = new JPanel(new GridBagLayout());

	final GridBagConstraints staticConstraint = new GridBagConstraints();
	staticConstraint.insets = new Insets(5, 5, 5, 5);
	staticConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
	staticConstraint.gridx = 0;
	staticConstraint.gridy = 0;
	staticConstraint.weightx = 0.1;
	staticConstraint.fill = GridBagConstraints.BOTH;

	staticConstraint.gridx = 0;
	synchronized (staticUrlViewList) {
		for (ExternalToolStaticUrlViewer staticView : staticUrlViewList) {
			addStaticUrlViewer(this, staticEditPanel,
					staticView);
		}
	}

	this.add(new JScrollPane(staticEditPanel),
			BorderLayout.CENTER);

	JTextArea descriptionText = new ReadOnlyTextArea(
			STATIC_URL_DESCRIPTION);
	descriptionText.setEditable(false);
	descriptionText.setFocusable(false);
	descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));

	this.add(descriptionText, BorderLayout.NORTH);

	JButton addstaticPortButton = new DeselectingButton("Add URL",
			new AbstractAction() {
		// FIXME refactor this into a method
		public void actionPerformed(ActionEvent e) {

			ExternalToolStaticUrlViewer newViewer = new ExternalToolStaticUrlViewer();
			synchronized (staticUrlViewList) {
				staticUrlViewList.add(newViewer);
				addStaticUrlViewer(StaticUrlPanel.this, staticEditPanel,
						newViewer);
				staticEditPanel.revalidate();
				staticEditPanel.repaint();
			}
		}

	});
	JPanel buttonPanel = new JPanel(new BorderLayout());

	buttonPanel.add(addstaticPortButton, BorderLayout.EAST);

	this.add(buttonPanel, BorderLayout.SOUTH);

	}
	
	private void addStaticUrlViewer(final JPanel outerPanel,
			final JPanel panel, ExternalToolStaticUrlViewer viewer) {
		Tools.addViewer(panel,
				elementLabels,
				new JComponent[] {viewer.getContentField(), viewer.getValueField()},
				staticUrlViewList,
				viewer,
				outerPanel);
	}


}
