/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.ssh.ExternalToolSshInvocationMechanism;

/**
 * @author alanrw
 *
 */
public abstract class InvocationMechanismEditor<T extends InvocationMechanism> extends JPanel {
	
	public abstract String getName();

	public abstract boolean canShow(Class<?> c);

	public abstract void show(T invocationMechanism);

	public abstract T updateInvocationMechanism();

	public abstract InvocationMechanism createMechanism(String mechanismName);
	
	public boolean isSingleton() {
		return false;
	}

}
