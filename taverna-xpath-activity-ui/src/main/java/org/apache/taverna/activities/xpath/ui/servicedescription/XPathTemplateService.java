package org.apache.taverna.activities.xpath.ui.servicedescription;
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

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class XPathTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/xpath");

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = (ObjectNode) configuration.getJson();
		json.put("xpathExpression", "/");
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return XPathActivityIcon.getXPathActivityIcon();
	}

	public String getName() {
		return "XPath";
	}

	public String getDescription() {
		return "Service for point-and-click creation of XPath expressions for XML data";
	}

	public static ServiceDescription getServiceDescription() {
		XPathTemplateService gts = new XPathTemplateService();
		return gts.templateService;
	}

	public String getId() {
		return "http://www.taverna.org.uk/2010/services/xpath";
	}

    @Override
    public XPathTemplateService newInstance() {
        return new XPathTemplateService();
    }

}
