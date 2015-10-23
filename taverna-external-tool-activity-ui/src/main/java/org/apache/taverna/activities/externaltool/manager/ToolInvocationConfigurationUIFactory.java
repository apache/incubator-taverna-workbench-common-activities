/**
 *
 */
package org.apache.taverna.activities.externaltool.manager;

import java.util.List;

import javax.swing.JPanel;

import org.apache.taverna.configuration.Configurable;
import org.apache.taverna.configuration.ConfigurationUIFactory;

import org.apache.taverna.activities.externaltool.configuration.ToolInvocationConfiguration;
import org.apache.taverna.activities.externaltool.manager.MechanismCreator;

/**
 * @author alanrw
 *
 */
public class ToolInvocationConfigurationUIFactory implements ConfigurationUIFactory {

	private List<MechanismCreator> mechanismCreators;
	private List<InvocationMechanismEditor<?>> invocationMechanismEditors;

	private ToolInvocationConfigurationPanel configPanel;

	@Override
	public boolean canHandle(String uuid) {
		return uuid.equals(getConfigurable().getUUID());
	}

	@Override
	public Configurable getConfigurable() {
		return ToolInvocationConfiguration.getInstance();
	}

	@Override
	public JPanel getConfigurationPanel() {
		if (configPanel == null) {
			configPanel = new ToolInvocationConfigurationPanel(mechanismCreators,
					invocationMechanismEditors);
		}
		return configPanel;
	}

	public void setMechanismCreators(List<MechanismCreator> mechanismCreators) {
		this.mechanismCreators = mechanismCreators;
	}

	public void setInvocationMechanismEditors(
			List<InvocationMechanismEditor<?>> invocationMechanismEditors) {
		this.invocationMechanismEditors = invocationMechanismEditors;
	}

}
