/*******************************************************************************
 * Copyright (C) 2009 Hajo Nils Krabbenhoeft, INB, University of Luebeck   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/

package net.sf.taverna.t2.activities.externaltool.servicedescriptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.servicedescriptions.AbstractConfigurableServiceProvider;
import net.sf.taverna.t2.servicedescriptions.CustomizedConfigurePanelProvider;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;

/**
 * ExternalToolServiceProvider searches an use case repository XML for use case
 * descriptions.
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceProvider extends AbstractConfigurableServiceProvider<ExternalToolServiceProviderConfig>  implements
CustomizedConfigurePanelProvider<ExternalToolServiceProviderConfig>{

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/externaltool");
	
	public ExternalToolServiceProvider() {
		super(new ExternalToolServiceProviderConfig("http://taverna.nordugrid.org/sharedRepository/xml.php"));
	}

	public String getName() {
		return "Tool service";
	}

/*	public List<ExternalToolServiceProviderConfig> getDefaultConfigurations() {
		List<ExternalToolServiceProviderConfig> defaults = new ArrayList<ExternalToolServiceProviderConfig>();
		// Disabled until sensible set
//		defaults.add(new ExternalToolServiceProviderConfig("http://taverna.nordugrid.org/sharedRepository/xml.php"));
		return defaults;
	}*/

	public void findServiceDescriptionsAsync(FindServiceDescriptionsCallBack callBack) {
		String repositoryUrl = serviceProviderConfig.getRepositoryUrl();
		callBack.status("Parsing use case repository:" + repositoryUrl);
			// prepare a list of all use case descriptions which are stored in
			// the given repository URL
			List<UseCaseDescription> usecases = new ArrayList<UseCaseDescription> ();
			try {
				usecases = UseCaseEnumeration.readDescriptionsFromUrl(
						repositoryUrl);
			} catch (IOException e) {
				callBack.fail("Unable to read tool descriptions", e);
			}
			callBack.status("Found " + usecases.size() + " use cases:" + repositoryUrl);
			// convert all the UseCaseDescriptions in the XML file into
			// ExternalToolServiceDescription items
			List<ExternalToolServiceDescription> items = new ArrayList<ExternalToolServiceDescription>();
			for (UseCaseDescription usecase : usecases) {
				ExternalToolServiceDescription item = new ExternalToolServiceDescription();
				item.setRepositoryUrl(repositoryUrl);
				item.setExternaltoolid(usecase.getUsecaseid());
				item.setUseCaseDescription(usecase);
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
