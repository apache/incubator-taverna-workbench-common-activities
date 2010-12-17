/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views.grid;

import javax.swing.JPanel;

import net.sf.taverna.t2.activities.externaltool.ExternalToolInvocationConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.grid.ExternalToolGridInvocationConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.views.ExternalToolInvocationViewer;

/**
 * @author alanrw
 *
 */
public final class ExternalToolGridInvocationViewer extends
		ExternalToolInvocationViewer {

	@Override
	public boolean canShow(Class<?> c) {
		return ExternalToolGridInvocationConfigurationBean.class.isAssignableFrom(c);
	}

	@Override
	public String getName() {
		return "Grid";
	}

	@Override
	public JPanel show(
			ExternalToolInvocationConfigurationBean<?> invocationBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean invocationChanged(
			ExternalToolInvocationConfigurationBean invocationBean) {
		return !(invocationBean instanceof ExternalToolGridInvocationConfigurationBean);
	}

	@Override
	public ExternalToolInvocationConfigurationBean getInvocationConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
