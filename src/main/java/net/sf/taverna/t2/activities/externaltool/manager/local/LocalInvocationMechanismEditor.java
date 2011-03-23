/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager.local;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.local.ExternalToolLocalInvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanism;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationMechanismEditor;

/**
 * @author alanrw
 *
 */
public final class LocalInvocationMechanismEditor extends
		InvocationMechanismEditor<ExternalToolLocalInvocationMechanism> {

	private ExternalToolLocalInvocationMechanism invocationMechanism;

	@Override
	public boolean canShow(Class<?> c) {
		return ExternalToolLocalInvocationMechanism.class.isAssignableFrom(c);
	}

	@Override
	public String getName() {
		return ("Local");
	}

	@Override
	public void show(ExternalToolLocalInvocationMechanism invocationMechanism) {
		this.invocationMechanism = invocationMechanism;
		this.removeAll();
		this.add(new JLabel("External tool will run on local machine"));
	}

	@Override
	public ExternalToolLocalInvocationMechanism updateInvocationMechanism() {
		return invocationMechanism;
	}

	@Override
	public InvocationMechanism createMechanism(String mechanismName) {
		ExternalToolLocalInvocationMechanism result = new ExternalToolLocalInvocationMechanism();
		result.setName(mechanismName);
		return(result);
	}

}
