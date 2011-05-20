/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;

/**
 * @author alanrw
 *
 */
public class EditablePanel extends JPanel {
	public EditablePanel(final ExternalToolConfigView view) {
		super();
		JButton makeEditable = new JButton("Edit tool description");
		makeEditable.setToolTipText("Edit the tool description");
		makeEditable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				view.makeEditable();
				
			}
		});
		this.add(makeEditable);	
	}

}
