/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import de.uni_luebeck.inb.knowarc.usecases.RuntimeEnvironmentConstraint;

import net.sf.taverna.t2.activities.externaltool.InvocationCreator;
import net.sf.taverna.t2.spi.SPIRegistry;

/**
 * @author alanrw
 *
 */
public class MechanismEditsPanel extends JPanel {
	
private static SPIRegistry<InvocationMechanismEditor> invocationMechanismEditorRegistry = new SPIRegistry(InvocationMechanismEditor.class);
	
private static Logger logger = Logger.getLogger(MechanismEditsPanel.class);

private InvocationMechanism mechanism = null;

	private InvocationMechanismEditor chosenEditor;
	
	private JPanel buttonPanel;
	
	public MechanismEditsPanel(JButton closeButton) {
		this.setLayout(new BorderLayout());
		buttonPanel = createButtonPanel(closeButton);
		logger.info("Mechanism Edits Panel created");
	}

	private JPanel createButtonPanel(JButton closeButton) {
		JPanel result = new JPanel();
		result.setLayout(new FlowLayout());
		JButton cancelButton = new JButton(new AbstractAction("Revert") {

			@Override
			public void actionPerformed(ActionEvent e) {
				MechanismEditsPanel.this.showMechanism(mechanism);
			}});
		result.add(cancelButton);
		JButton saveButton = new JButton(new AbstractAction("Update") {

			@Override
			public void actionPerformed(ActionEvent e) {
				logger.info("Trying to update invocation mechanism");
				chosenEditor.updateInvocationMechanism();
				InvocationGroupManager.getInstance().mechanismChanged(mechanism);
			}
			
		});
		result.add(saveButton);
		result.add(closeButton);
		return result;
	}

	public void clear() {
		mechanism = null;
		chosenEditor = null;
		this.removeAll();
		this.repaint();
	}

	public void showMechanism(InvocationMechanism mechanism) {
		clear();
		this.mechanism = mechanism;
		chosenEditor = null;
		for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry.getInstances()) {
			if (ime.canShow(mechanism.getClass())) {
				chosenEditor = ime;
				break;
			}
		}
		if (chosenEditor != null) {
			this.add(new JLabel(chosenEditor.getName()), BorderLayout.NORTH);
			this.add(chosenEditor, BorderLayout.CENTER);
			chosenEditor.show(mechanism);
		}
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.validate();
		this.repaint();
	}

}
