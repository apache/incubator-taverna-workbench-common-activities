
package org.apache.taverna.activities.externaltool.servicedescriptions;
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

import org.apache.taverna.lang.beans.PropertyAnnotated;
import org.apache.taverna.lang.beans.PropertyAnnotation;

/**
 * ExternalToolServiceProviderConfig stores the URL of the use case repository XML file
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceProviderConfig extends PropertyAnnotated {
	private String repositoryUrl;

	public ExternalToolServiceProviderConfig() {
	}

	public ExternalToolServiceProviderConfig(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	@PropertyAnnotation(displayName = "Tool registry location", preferred = true)
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	@Override
	public String toString() {
		return repositoryUrl;
	}

}
