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

package org.apache.taverna.activities.rest.ui.servicedescription;

import java.net.URI;

import javax.swing.Icon;

import org.apache.taverna.activities.rest.RESTActivity;
import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider;

/**
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class GenericRESTTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/rest");

	private static final String REST = "REST";

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = (ObjectNode) configuration.getJson();
		ObjectNode requestNode = json.objectNode();

		requestNode.put("httpMethod", RESTActivity.HTTP_METHOD.GET.name());
		requestNode.put("absoluteURITemplate", "http://www.uniprot.org/uniprot/{id}.xml");

		ArrayNode headersNode = requestNode.arrayNode();
		headersNode.addObject().put("header", "Accept").put("value", "application/xml");
		headersNode.addObject().put("header", "Content-Type").put("value", "application/xml");

		requestNode.set("headers", headersNode);
		json.set("request", requestNode);
		json.put("outgoingDataFormat", RESTActivity.DATA_FORMAT.String.name());
		json.put("showRedirectionOutputPort", false);
		json.put("showActualURLPort", false);
		json.put("showResponseHeadersPort", false);
		json.put("escapeParameters", true);
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return RESTActivityIcon.getRESTActivityIcon();
	}

	public String getName() {
		return REST;
	}

	public String getDescription() {
		return "A generic REST service that can handle all HTTP methods";
	}

	public static ServiceDescription getServiceDescription() {
		GenericRESTTemplateService gts = new GenericRESTTemplateService();
		return gts.templateService;
	}

	public String getId() {
		return "http://www.taverna.org.uk/2010/services/rest";
	}

    @Override
    public ServiceDescriptionProvider newInstance() {
        return new GenericRESTTemplateService();
    }

}
