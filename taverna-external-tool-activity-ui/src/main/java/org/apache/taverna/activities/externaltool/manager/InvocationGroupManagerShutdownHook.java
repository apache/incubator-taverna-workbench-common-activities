/**
 * 
 */
package org.apache.taverna.activities.externaltool.manager;

import org.apache.taverna.activities.externaltool.manager.impl.InvocationGroupManagerImpl;

import org.apache.taverna.workbench.ShutdownSPI;

/**
 * @author alanrw
 *
 */
public class InvocationGroupManagerShutdownHook implements ShutdownSPI {

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.ShutdownSPI#positionHint()
	 */
	@Override
	public int positionHint() {
		return 710;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.ShutdownSPI#shutdown()
	 */
	@Override
	public boolean shutdown() {
		InvocationGroupManager manager = InvocationGroupManagerImpl.getInstance();
		manager.saveConfiguration();
		manager.persistInvocations();
		return true;
	}

}
