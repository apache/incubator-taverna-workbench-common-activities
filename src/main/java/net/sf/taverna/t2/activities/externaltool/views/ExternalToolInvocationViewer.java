/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.ExternalToolInvocationConfigurationBean;

/**
 * @author alanrw
 *
 */
public abstract class ExternalToolInvocationViewer {
	
	public abstract String getName();
	
	public abstract boolean canShow(Class<?> c);
	
	public abstract JPanel show(ExternalToolInvocationConfigurationBean<?> invocationBean);
	
	public String toString() {
		return getName();
	}

	public abstract boolean invocationChanged(ExternalToolInvocationConfigurationBean invocationBean);
	
	public abstract ExternalToolInvocationConfigurationBean getInvocationConfiguration();

}
