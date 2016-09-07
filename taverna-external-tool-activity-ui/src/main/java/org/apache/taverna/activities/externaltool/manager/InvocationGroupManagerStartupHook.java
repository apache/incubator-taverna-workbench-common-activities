package org.apache.taverna.activities.externaltool.manager;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
