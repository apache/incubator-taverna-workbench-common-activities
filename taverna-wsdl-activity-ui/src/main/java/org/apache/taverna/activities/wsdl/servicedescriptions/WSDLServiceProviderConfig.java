/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.wsdl.servicedescriptions;

import java.net.URI;

import org.apache.taverna.lang.beans.PropertyAnnotated;
import org.apache.taverna.lang.beans.PropertyAnnotation;

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
