package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.net.URI;

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

public class WSDLServiceProviderConfig extends PropertyAnnotated {
	private URI uri;
	
	public WSDLServiceProviderConfig() {
	}

	public WSDLServiceProviderConfig(String uri) {
		this.uri = URI.create(uri);
	}

	@PropertyAnnotation(displayName = "WSDL location", preferred = true)
	public URI getURI() {
		return uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	@Override
	public String toString() {
		return getURI().toASCIIString();
	}
	
}
