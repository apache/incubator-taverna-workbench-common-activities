/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author alanrw
 *
 */
public class AnnotationPanel extends JPanel {
	
	public AnnotationPanel(Component nameField, Component descriptionArea, Component groupField) {
		super();
		this.setLayout(new BorderLayout());
		JPanel subPanel = new JPanel(new BorderLayout());
		JPanel namePanel = new JPanel();
		namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		namePanel.add(new JLabel("Name: "));
		namePanel.add(nameField);
		subPanel.add(namePanel, BorderLayout.NORTH);
		JPanel groupPanel = new JPanel();
		groupPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		groupPanel.add(new JLabel("Group: "));
		groupPanel.add(groupField);
		subPanel.add(groupPanel, BorderLayout.SOUTH);
		this.add(subPanel, BorderLayout.NORTH);
		JPanel descriptionPanel = new JPanel();
		descriptionPanel.setLayout(new BorderLayout());
		descriptionPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
		descriptionPanel.add(descriptionArea, BorderLayout.CENTER);
		this.add(descriptionPanel, BorderLayout.CENTER);
	}

}
