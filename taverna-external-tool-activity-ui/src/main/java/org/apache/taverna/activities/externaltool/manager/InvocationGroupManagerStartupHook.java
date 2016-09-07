package org.apache.taverna.activities.externaltool.manager;

import org.apache.taverna.activities.externaltool.manager.impl.InvocationGroupManagerImpl;
import org.apache.taverna.workbench.StartupSPI;

/**
 * Load previously saved workflow ids that were scheduled to be deleted before
 * previous Taverna shutdown, and initiate deletion of them now.
 * 
 * @see StoreRunIdsToDeleteLaterShutdownHook
 * @see DatabaseCleanup
 * 
 * @author Stian Soiland-Reyes
 * 
 */
public class InvocationGroupManagerStartupHook implements StartupSPI {

	public int positionHint() {
		return 900;
	}

	public boolean startup() {
		InvocationGroupManagerImpl.getInstance().loadInvocations();
		return true;
	}

}
