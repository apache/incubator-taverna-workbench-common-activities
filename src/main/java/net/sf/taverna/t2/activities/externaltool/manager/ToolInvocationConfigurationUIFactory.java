/**
 *
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.util.List;

import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.configuration.ToolInvocationConfiguration;
import net.sf.taverna.t2.workbench.configuration.Configurable;
import net.sf.taverna.t2.workbench.configuration.ConfigurationUIFactory;

/**
 * @author alanrw
 *
 */
public class ToolInvocationConfigurationUIFactory implements
		ConfigurationUIFactory {

	private List<MechanismCreator> mechanismCreators;

	private ToolInvocationConfigurationPanel configPanel;

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.ConfigurationUIFactory#canHandle(java.lang.String)
	 */
	@Override
	public boolean canHandle(String uuid) {
		return uuid.equals(getConfigurable().getUUID());
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.ConfigurationUIFactory#getConfigurable()
	 */
	@Override
	public Configurable getConfigurable() {
		return ToolInvocationConfiguration.getInstance();
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.ConfigurationUIFactory#getConfigurationPanel()
	 */
	@Override
	public JPanel getConfigurationPanel() {
		if (configPanel == null) {
			configPanel = new ToolInvocationConfigurationPanel(mechanismCreators);
		}
		return configPanel;
	}

	public void setMechanismCreators(List<MechanismCreator> mechanismCreators) {
		this.mechanismCreators = mechanismCreators;
	}

}
