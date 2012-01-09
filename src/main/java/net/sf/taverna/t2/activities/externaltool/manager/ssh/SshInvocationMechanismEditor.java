/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager.ssh;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;

import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismEditor;
import net.sf.taverna.t2.activities.externaltool.ssh.ExternalToolSshInvocationMechanism;
import net.sf.taverna.t2.lang.ui.DeselectingButton;
import de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshNode;
import de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshNodeFactory;

/**
 * @author alanrw
 *
 */
public final class SshInvocationMechanismEditor extends
		InvocationMechanismEditor<ExternalToolSshInvocationMechanism> {
	
	private ArrayList<ExternalToolSshNodeViewer> nodeViewers = new ArrayList<ExternalToolSshNodeViewer>();
	private int inputGridy = 0;
	
	private ExternalToolSshInvocationMechanism mechanism = null;
	
	private static Insets insets = new Insets(1,5,1,5);
	
	private static CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5), BorderFactory.createLineBorder(Color.BLACK, 1));

	@Override
	public boolean canShow(Class<?> c) {
		return ExternalToolSshInvocationMechanism.class.isAssignableFrom(c);
	}

	@Override
	public void show(ExternalToolSshInvocationMechanism invocationMechanism) {
		mechanism = invocationMechanism;
		this.removeAll();
		inputGridy = 1;
		final JPanel innerPanel = new JPanel(new GridBagLayout());

		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputConstraint.gridx = 0;
			nodeViewers.clear();
			for (SshNode node : invocationMechanism.getNodes()) {
				ExternalToolSshNodeViewer nodeViewer = new ExternalToolSshNodeViewer(node);
				addNodeViewer(this, innerPanel, nodeViewer);
			}

		this.setLayout(new GridBagLayout());
		GridBagConstraints outerPanelConstraint = new GridBagConstraints();
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 0;
		outerPanelConstraint.weightx = 0.1;
		outerPanelConstraint.weighty = 0.1;
		outerPanelConstraint.fill = GridBagConstraints.BOTH;
		this.add(new JScrollPane(innerPanel),
				outerPanelConstraint);
		outerPanelConstraint.weighty = 0;
		final JButton addHostButton = new DeselectingButton("Add host",
				new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				ExternalToolSshNodeViewer newViewer = new ExternalToolSshNodeViewer();

					addNodeViewer(SshInvocationMechanismEditor.this, innerPanel, newViewer);
					innerPanel.revalidate();
					innerPanel.repaint();
			}

		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());

		JPanel filler = new JPanel();
		outerPanelConstraint.weightx = 0.1;
		outerPanelConstraint.weighty = 0;
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 0;

		buttonPanel.add(filler, outerPanelConstraint);

		outerPanelConstraint.weightx = 0;
		outerPanelConstraint.weighty = 0;
		outerPanelConstraint.gridx = 1;
		outerPanelConstraint.gridy = 0;

		buttonPanel.add(addHostButton, outerPanelConstraint);

		outerPanelConstraint.weightx = 0;
		outerPanelConstraint.weighty = 0;
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 1;
		outerPanelConstraint.fill = GridBagConstraints.BOTH;
		this.add(buttonPanel, outerPanelConstraint);
	}

	protected void addNodeViewer(final JPanel result, final JPanel innerPanel,
			ExternalToolSshNodeViewer viewer) {
		final JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(border);
		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.insets = insets;
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputConstraint.gridy = 0 ;
		inputConstraint.gridx = 0;
		
		subPanel.add(new JLabel("Host: "), inputConstraint);
		final JTextField hostnameField = viewer.getHostnameField();
		inputConstraint.gridx++;
		subPanel.add(hostnameField, inputConstraint);

		inputConstraint.gridy++ ;
		inputConstraint.gridx = 0;
		subPanel.add(new JLabel("Port: "), inputConstraint);
		final JTextField portField = viewer.getPortField();
		inputConstraint.gridx++;
		subPanel.add(portField ,inputConstraint);
		
		inputConstraint.gridy++ ;
		inputConstraint.gridx = 0;
		subPanel.add(new JLabel("Working directory: "), inputConstraint);
		final JTextField directoryField = viewer.getDirectoryField();
		inputConstraint.gridx++;
		subPanel.add(directoryField ,inputConstraint);
		
		inputConstraint.gridy++ ;
		inputConstraint.gridx = 0;
		subPanel.add(new JLabel("Link command: "), inputConstraint);
		final JTextField linkCommandField = viewer.getLinkCommandField();
		inputConstraint.gridx++;
		subPanel.add(linkCommandField ,inputConstraint);

		inputConstraint.gridy++ ;
		inputConstraint.gridx = 0;
		subPanel.add(new JLabel("Copy command: "), inputConstraint);
		final JTextField copyCommandField = viewer.getCopyCommandField();
		inputConstraint.gridx++;
		subPanel.add(copyCommandField ,inputConstraint);

		inputConstraint.gridy++ ;
		inputConstraint.gridx = 0;
		subPanel.add(new JLabel("Fetch data: "), inputConstraint);
		inputConstraint.gridx++;
		final JCheckBox retrieveDataField = viewer.getRetrieveDataField();
		subPanel.add(retrieveDataField ,inputConstraint);

		inputConstraint.gridy++ ;
		inputConstraint.gridx = 1;
		inputConstraint.fill = GridBagConstraints.NONE;
		inputConstraint.anchor = GridBagConstraints.EAST;
		final ExternalToolSshNodeViewer v = viewer;
		final JButton removeButton = new DeselectingButton("Remove",
				new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(nodeViewers) {
					nodeViewers.remove(v);
				}
				innerPanel.remove(subPanel);
				innerPanel.revalidate();
				innerPanel.repaint();
				result.revalidate();
				result.repaint();
			}

		});
		subPanel.add(removeButton, inputConstraint);
		
		inputConstraint.gridy = ++inputGridy;
		innerPanel.add(subPanel, inputConstraint);

		nodeViewers.add(viewer);
		inputGridy++;		
	}

	private List<SshNode> getNodeList() {
		List<SshNode> result = new ArrayList<SshNode>();
		for (ExternalToolSshNodeViewer viewer : nodeViewers) {
			SshNode node = SshNodeFactory.getInstance().getSshNode(viewer.getHostname(), viewer.getPort(), viewer.getDirectory());
			node.setLinkCommand(viewer.getLinkCommand());
			node.setCopyCommand(viewer.getCopyCommand());
			node.setRetrieveData(viewer.getRetrieveDataField().isSelected());
			result.add(node);
		}
		return result;
	}

	@Override
	public ExternalToolSshInvocationMechanism updateInvocationMechanism() {
		mechanism.setNodes(getNodeList());
		return mechanism;
	}

	@Override
	public InvocationMechanism createMechanism(String mechanismName) {
		ExternalToolSshInvocationMechanism result = new ExternalToolSshInvocationMechanism();
		result.setName(mechanismName);
		return result;
	}

	@Override
	public String getName() {
		return ("SSH");
	}

}
