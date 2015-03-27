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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.security.credentialmanager.CMException;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.servicedescriptions.ServiceDescription;

import org.apache.log4j.Logger;

import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class WSDLServiceDescription extends ServiceDescription {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/wsdl");
	public static final URI INPUT_SPLITTER_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/xml-splitter/in");
	public static final URI OUTPUT_SPLITTER_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/xml-splitter/out");

	private static final String WSDL = "WSDL @ ";

	private String use;
	private URI uri;
	private String style;
	private String operation;
	private final CredentialManager credentialManager;

	private static Logger logger = Logger.getLogger(WSDLServiceDescription.class);

	public WSDLServiceDescription(CredentialManager credentialManager) {
		this.credentialManager = credentialManager;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public URI getURI() {
		return uri;
	}

	public void setURI(URI url) {
		this.uri = url;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getType() {
		return "WSDL";
	}

	@Override
	public String toString() {
		return operation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Icon getIcon() {
		return WSDLActivityIcon.getWSDLIcon();
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = (ObjectNode) configuration.getJson();
		ObjectNode operation = json.objectNode();
		json.put("operation", operation);
		operation.put("wsdl", getURI().toString());
		operation.put("name", getOperation());
		return configuration;
	}

	public String getName() {
		return getOperation();
	}

	public List<? extends Comparable<?>> getPath() {
		return Collections.singletonList(WSDL + getURI());
	}

	protected List<Object> getIdentifyingData() {
		return Arrays.<Object> asList(getURI(), getOperation());
	}

	@Override
	public boolean isTemplateService() {
		return needsSecurity();
	}

	protected boolean needsSecurity() {
		if (credentialManager == null) {
			// We don't know if it needs security or not
			return false;
		}
		// A match is a good indicator that security configuration is needed
		try {
			return credentialManager.hasUsernamePasswordForService(getURI());
		} catch (CMException e) {
			logger.warn("Could not check if credential manager has username/password for " + getURI(), e);
			return false;
		}
	}


}
