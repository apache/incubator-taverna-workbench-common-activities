/*******************************************************************************
 ******************************************************************************/

package org.apache.taverna.activities.externaltool.servicedescriptions;

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
