/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.servicedescriptions.ExternalToolServiceDescription;

/**
 * @author alanrw
 *
 */
public class EditablePanel extends JPanel {
	public EditablePanel(final ExternalToolConfigView view) {
		super(new FlowLayout());
		
		JButton update = new JButton(new AbstractAction("Update tool description") {

			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalToolActivityConfigurationBean bean = view.getConfiguration();
				String repositoryUrl = bean.getRepositoryUrl();
				String id = bean.getExternaltoolid();
				UseCaseDescription usecase = UseCaseEnumeration.readDescriptionFromUrl(
						repositoryUrl, id);
				if (usecase != null) {
					bean.setUseCaseDescription(usecase);
					view.refreshConfiguration(bean);
				} else {
					JOptionPane.showMessageDialog(view, "Unable to find tool description " + id, "Missing tool description", JOptionPane.ERROR_MESSAGE);
				}
			}});
		this.add(update);
		
		JButton makeEditable = new JButton("Edit tool description");
		makeEditable.setToolTipText("Edit the tool description");
		makeEditable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ExternalToolActivityConfigurationBean config = view.makeConfiguration();
				view.setEditable(true, config);
				
			}
		});
		this.add(makeEditable);	
		
	}

}
