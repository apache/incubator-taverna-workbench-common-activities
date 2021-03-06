package org.apache.taverna.activities.localworker.servicedescriptions;
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

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.JsonNode;

public class LocalworkerServiceDescription extends ServiceDescription {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/localworker");

	private static final String LOCALWORKER = ServiceDescription.LOCAL_SERVICES;

	private JsonNode json;
	private String operation;
	private String category;
	private String provider;
	private String localworkerName;

	public JsonNode getJson() {
		return json;
	}

	public void setJson(JsonNode json) {
		this.json = json;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getLocalworkerName() {
		return localworkerName;
	}

	public void setLocalworkerName(String localworkerName) {
		this.localworkerName = localworkerName;
	}

	public String getType() {
		return "Localworker";
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		configuration.setJson(getJson());
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return LocalworkerActivityIcon.getLocalworkerIcon();
	}

	@Override
	public String getName() {
		return operation;
	}

	@Override
	public List<? extends Comparable<?>> getPath() {
		List<String> result =
		Arrays.asList (LOCALWORKER, category);
		return result;
	}

	@Override
	protected List<Object> getIdentifyingData() {
		return Arrays.<Object>asList(getJson());
	}

}
