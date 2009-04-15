package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.net.URI;

public class WSDLServiceProviderConfig {
	private URI uri;

	public WSDLServiceProviderConfig() {
	}

	public WSDLServiceProviderConfig(String uri) {
		this.uri = URI.create(uri);
	}

	public URI getURI() {
		return uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}
}
