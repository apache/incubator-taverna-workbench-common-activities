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

package org.apache.taverna.activities.wsdl.views;

import static org.junit.Assert.assertNull;

import org.junit.Before;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestWSDLActivityContextualView {

	Activity a;

	@Before
	public void setUp() throws Exception {
		a=new Activity();
		Configuration configuration = new Configuration();
		ObjectNode json = (ObjectNode) configuration.getJson();
		ObjectNode operation = json.objectNode();
		operation.put("name", "getReport");
		json.set("operation", operation);
		String wsdlUrl=TestWSDLActivityContextualView.class.getResource("/GMService.wsdl").toExternalForm();
		operation.put("wsdl", wsdlUrl);
		configuration.setConfigures(a);
	}

	public void testConfigurationAction() {
		WSDLActivityContextualView view = new WSDLActivityContextualView(a, null, null, null, null, null, null, null);
		assertNull("WSDL has no configure action, so should be null",view.getConfigureAction(null));
	}
}
