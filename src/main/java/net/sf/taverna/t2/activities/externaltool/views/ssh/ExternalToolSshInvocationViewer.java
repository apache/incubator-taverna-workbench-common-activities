/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views.ssh;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import de.uni_luebeck.inb.knowarc.usecases.invocation.ssh.SshNode;

import net.sf.taverna.t2.activities.externaltool.ExternalToolInvocationConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.ssh.ExternalToolSshInvocationConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.views.ExternalToolInvocationViewer;

/**
 * @author alanrw
 *
 */
public final class ExternalToolSshInvocationViewer extends
		ExternalToolInvocationViewer {
	
	private List<ExternalToolSshNodeViewer> nodeViewers = new ArrayList<ExternalToolSshNodeViewer> ();
	private int inputGridy = 1;

	@Override
	public boolean canShow(Class<?> c) {
		return ExternalToolSshInvocationConfigurationBean.class.isAssignableFrom(c);
	}

	@Override
	public String getName() {
		return "Ssh";
	}

	@Override
	public JPanel show(ExternalToolInvocationConfigurationBean<?> invocationBean) {
		final JPanel result = new JPanel();
		final JPanel innerPanel = new JPanel(new GridBagLayout());
//		inputEditPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
//				.createEtchedBorder(), "Inputs"));

		final GridBagConstraints inputConstraint = new GridBagConstraints();
//		inputConstraint.insets = new Insets(5,5,5,5);
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.gridx = 0;
		inputConstraint.gridy = 0;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		innerPanel.add(new JLabel("Hostname"), inputConstraint);
		inputConstraint.gridx++;
		innerPanel.add(new JLabel("Port"), inputConstraint);
		inputConstraint.gridx++;

		inputConstraint.gridx = 0;
		if (invocationBean instanceof ExternalToolSshInvocationConfigurationBean) {
			nodeViewers.clear();
			for (SshNode node : ((ExternalToolSshInvocationConfigurationBean) invocationBean).getSshWorkerNodes()) {
				ExternalToolSshNodeViewer nodeViewer = new ExternalToolSshNodeViewer(node);
				addNodeViewer(result, innerPanel, nodeViewer);
			}
		}
		result.setLayout(new GridBagLayout());
		GridBagConstraints outerPanelConstraint = new GridBagConstraints();
		outerPanelConstraint.gridx = 0;
		outerPanelConstraint.gridy = 0;
		outerPanelConstraint.weightx = 0.1;
		outerPanelConstraint.weighty = 0.1;
		outerPanelConstraint.fill = GridBagConstraints.BOTH;
		result.add(new JScrollPane(innerPanel),
				outerPanelConstraint);
		outerPanelConstraint.weighty = 0;
		JButton addHostButton = new JButton(new AbstractAction() {
			public void actionPerformed(ActionEvent e) {

				ExternalToolSshNodeViewer newViewer = new ExternalToolSshNodeViewer();

					addNodeViewer(result, innerPanel, newViewer);
					innerPanel.revalidate();
					innerPanel.repaint();

			}

		});
		addHostButton.setText("Add host");
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
		result.add(buttonPanel, outerPanelConstraint);

		return result;

	}

	protected void addNodeViewer(final JPanel result, final JPanel innerPanel,
			ExternalToolSshNodeViewer viewer) {
		final GridBagConstraints inputConstraint = new GridBagConstraints();
		inputConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		inputConstraint.weightx = 0.1;
		inputConstraint.fill = GridBagConstraints.BOTH;

		inputConstraint.gridy = inputGridy ;
		inputConstraint.gridx = 0;
		
		final JTextField hostnameField = viewer.getHostnameField();
		innerPanel.add(hostnameField, inputConstraint);
		inputConstraint.gridx++;

		final JTextField portField = viewer.getPortField();
		innerPanel.add(portField ,inputConstraint);
		inputConstraint.gridx++;
		
		
		final JButton removeButton = new JButton("Remove");
		final ExternalToolSshNodeViewer v = viewer;
		innerPanel.add(removeButton, inputConstraint);
		inputConstraint.gridy = ++ inputGridy;
		inputConstraint.gridx = 0;
		final JSeparator separator = new JSeparator();
		innerPanel.add(separator, inputConstraint);

		removeButton.addActionListener(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized(nodeViewers) {
					nodeViewers.remove(v);
				}
				innerPanel.remove(hostnameField);
				innerPanel.remove(portField);
				innerPanel.remove(removeButton);
				innerPanel.remove(separator);
				innerPanel.revalidate();
				innerPanel.repaint();
				result.revalidate();
				result.repaint();
			}

		});

		nodeViewers.add(viewer);
		inputGridy++;		
	}

	@Override
	public boolean invocationChanged(
			ExternalToolInvocationConfigurationBean invocationBean) {
		if (!(invocationBean instanceof ExternalToolSshInvocationConfigurationBean)) {
			return true;
		}
		ExternalToolSshInvocationConfigurationBean sshBean = (ExternalToolSshInvocationConfigurationBean) invocationBean;
		return !(sshBean.equals(getInvocationConfiguration()));
	}
	
	private List<SshNode> getNodeList() {
		List<SshNode> result = new ArrayList<SshNode>();
		for (ExternalToolSshNodeViewer viewer : nodeViewers) {
			SshNode node = new SshNode();
			node.setHost(viewer.getHostname());
			node.setPort(viewer.getPort());
			result.add(node);
		}
		return result;
	}

	@Override
	public ExternalToolInvocationConfigurationBean getInvocationConfiguration() {
		ExternalToolSshInvocationConfigurationBean result = new ExternalToolSshInvocationConfigurationBean();
		result.setSshWorkerNodes(getNodeList());
		return result;
	}

}
