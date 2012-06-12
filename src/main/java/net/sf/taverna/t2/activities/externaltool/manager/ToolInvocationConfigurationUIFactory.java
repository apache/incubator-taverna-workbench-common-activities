/**
 *
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.util.List;

import javax.swing.JPanel;

import uk.org.taverna.configuration.Configurable;
import uk.org.taverna.configuration.ConfigurationUIFactory;

import net.sf.taverna.t2.activities.externaltool.configuration.ToolInvocationConfiguration;

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
