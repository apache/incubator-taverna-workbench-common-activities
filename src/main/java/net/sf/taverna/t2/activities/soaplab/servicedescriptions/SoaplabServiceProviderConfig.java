package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.net.URI;

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

public class SoaplabServiceProviderConfig extends PropertyAnnotated {

	private String url;
	
	public SoaplabServiceProviderConfig() {
	}

	public SoaplabServiceProviderConfig(String url) {
		this.url = url;
	}

	@PropertyAnnotation(displayName = "Soaplab location", preferred = true)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String toString() {
		return getUrl();
	}
	
}
