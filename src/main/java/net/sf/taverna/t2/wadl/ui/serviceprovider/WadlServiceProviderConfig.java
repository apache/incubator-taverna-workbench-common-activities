package net.sf.taverna.t2.wadl.ui.serviceprovider;

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;

public class WadlServiceProviderConfig extends PropertyAnnotated {
	
	private String uriString = "http://example.com";

	public WadlServiceProviderConfig(String wadlURL) {
		this.uriString = wadlURL;
	}

	/**
	 * @return the uri
	 */
	public final String getUri() {
		return uriString;
	}

	/**
	 * @param uri the uri to set
	 */
	public final void setUri(String uri) {
		this.uriString = uri;
	}

}
