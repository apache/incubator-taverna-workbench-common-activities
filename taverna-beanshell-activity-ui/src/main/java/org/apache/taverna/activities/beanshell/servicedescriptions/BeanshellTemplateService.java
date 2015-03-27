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

package org.apache.taverna.activities.beanshell.servicedescriptions;

import java.net.URI;

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider;
import org.apache.taverna.scufl2.api.configurations.Configuration;

public class BeanshellTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/beanshell");

	private static final String BEANSHELL = "Beanshell";

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/beanshell");

	public String getName() {
		return BEANSHELL;
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		configuration.getJsonAsObjectNode().put("script", "");
		configuration.getJsonAsObjectNode().put("classLoaderSharing", "workflow");
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return BeanshellActivityIcon.getBeanshellIcon();
	}

	@Override
	public String getDescription() {
		return "A service that allows Beanshell scripts, with dependencies on libraries";
	}

	public static ServiceDescription getServiceDescription() {
		BeanshellTemplateService bts = new BeanshellTemplateService();
		return bts.templateService;
	}

	public String getId() {
		return providerId.toString();
	}

    @Override
    public ServiceDescriptionProvider newInstance() {
        return new BeanshellTemplateService();
    }

}
