package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.net.URI;

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

public class SoaplabServiceProviderConfig extends PropertyAnnotated {

	private URI endpoint;
	
	public SoaplabServiceProviderConfig() {
	}

	public SoaplabServiceProviderConfig(String endpointURI) {
		this.setEndpoint(URI.create(endpointURI));
	}

	@PropertyAnnotation(displayName = "Soaplab location", preferred = true)
	public URI getEndpoint() {
		return endpoint;
	}

	public String toString() {
		return getEndpoint().toString();
	}

	public void setEndpoint(URI endpoint) {
		this.endpoint = endpoint;
	}
	
}
