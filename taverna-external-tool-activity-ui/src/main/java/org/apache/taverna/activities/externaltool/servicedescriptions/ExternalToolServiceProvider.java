
package org.apache.taverna.activities.externaltool.servicedescriptions;
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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.AbstractConfigurableServiceProvider;
import org.apache.taverna.servicedescriptions.CustomizedConfigurePanelProvider;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.activities.externaltool.desc.ToolDescription;
import org.apache.taverna.activities.externaltool.desc.ToolDescriptionParser;

/**
 * ExternalToolServiceProvider searches an use case repository XML for use case
 * descriptions.
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceProvider extends AbstractConfigurableServiceProvider<ExternalToolServiceProviderConfig> 
                                        implements CustomizedConfigurePanelProvider<ExternalToolServiceProviderConfig> {

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/externaltool");
	
	public ExternalToolServiceProvider() {
		super(new ExternalToolServiceProviderConfig("http://taverna.nordugrid.org/sharedRepository/xml.php"));
	}

	public String getName() {
		return "Tool service";
	}

	public List<ExternalToolServiceProviderConfig> getDefaultConfigurations() {
		List<ExternalToolServiceProviderConfig> defaults = new ArrayList<ExternalToolServiceProviderConfig>();
		// Disabled until sensible set
//		defaults.add(new ExternalToolServiceProviderConfig("http://taverna.nordugrid.org/sharedRepository/xml.php"));
		return defaults;
	}

	public void findServiceDescriptionsAsync(FindServiceDescriptionsCallBack callBack) {
		String repositoryUrl = serviceProviderConfig.getRepositoryUrl();
		callBack.status("Parsing use case repository:" + repositoryUrl);
			// prepare a list of all use case descriptions which are stored in
			// the given repository URL
			List<ToolDescription> usecases = new ArrayList<UseCaseDescription> ();
			try {
				usecases = ToolDescriptionParser.readDescriptionsFromUrl(
						repositoryUrl);
			} catch (IOException e) {
				callBack.fail("Unable to read tool descriptions", e);
			}
			callBack.status("Found " + usecases.size() + " use cases:" + repositoryUrl);
			// convert all the ToolDescriptions in the XML file into
			// ExternalToolServiceDescription items
			List<ExternalToolServiceDescription> items = new ArrayList<ExternalToolServiceDescription>();
			for (ToolDescription usecase : usecases) {
				ExternalToolServiceDescription item = new ExternalToolServiceDescription();
				item.setRepositoryUrl(repositoryUrl);
				item.setExternaltoolid(usecase.getUsecaseid());
				item.setToolDescription(usecase);
				items.add(item);
			}
			// we dont have streaming data loading or partial results, so return
			// results and finish
			callBack.partialResults(items);
			callBack.finished();
	}

	@Override
	public String toString() {
		return getName() + " " + getConfiguration().getRepositoryUrl();
	}

	public Icon getIcon() {
	    return ExternalToolActivityIcon.getExternalToolIcon();
	}

	@Override
	protected List<? extends Object> getIdentifyingData() {
		List<String> result;
		// one can fully identify an use case repository by its URL
		result = Arrays.asList(getConfiguration().getRepositoryUrl());
		return result;
	}

	public void setServiceDescriptionRegistry(ServiceDescriptionRegistry registry) {
	}
	
	@SuppressWarnings("serial")
	public void createCustomizedConfigurePanel(final CustomizedConfigureCallBack<ExternalToolServiceProviderConfig> callBack) {
			
		AddExternalToolServiceDialog addWSDLServiceDialog = new AddExternalToolServiceDialog() {
				@Override
				protected void addRegistry(String externalToolURL) {
					
					ExternalToolServiceProviderConfig providerConfig = new ExternalToolServiceProviderConfig(externalToolURL);					
					callBack.newProviderConfiguration(providerConfig);
				}
			};
			addWSDLServiceDialog.setVisible(true);		
	}


	public String getId() {
		return providerId.toString();
	}
}
