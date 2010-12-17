/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views.local;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.ExternalToolInvocationConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.local.ExternalToolLocalInvocationConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.views.ExternalToolInvocationViewer;

/**
 * @author alanrw
 *
 */
public final class ExternalToolLocalInvocationViewer extends
		ExternalToolInvocationViewer {

	private ExternalToolLocalInvocationConfigurationBean invocationBean;

	@Override
	public boolean canShow(Class<?> c) {
		return ExternalToolLocalInvocationConfigurationBean.class.isAssignableFrom(c);
	}

	@Override
	public String getName() {
		return ("Local");
	}

	@Override
	public JPanel show(ExternalToolInvocationConfigurationBean<?> invocationBean) {
		if (invocationBean instanceof ExternalToolLocalInvocationConfigurationBean) {
			this.invocationBean = (ExternalToolLocalInvocationConfigurationBean) invocationBean;
		} else {
			this.invocationBean = new ExternalToolLocalInvocationConfigurationBean();
		}
		JPanel result = new JPanel();
		result.add(new JLabel("External tool will run on local machine"));
		return result;
	}

	@Override
	public boolean invocationChanged(
			ExternalToolInvocationConfigurationBean invocationBean) {
		return (! (invocationBean instanceof ExternalToolLocalInvocationConfigurationBean));
	}

	@Override
	public ExternalToolInvocationConfigurationBean getInvocationConfiguration() {
		return this.invocationBean;
	}

}
