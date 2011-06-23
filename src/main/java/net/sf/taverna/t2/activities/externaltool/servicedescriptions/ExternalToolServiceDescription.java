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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.activities.externaltool.views.ExternalToolConfigView;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

/**
 * ExternalToolServiceDescription stores the repository URL and the use case id so
 * that it can create an ExternalToolActivityConfigurationBean
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceDescription extends ServiceDescription<ExternalToolActivityConfigurationBean> {
	
	private static Logger logger = Logger
	.getLogger(ExternalToolServiceDescription.class);

	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	private String repositoryUrl;
	private String externaltoolid;
	private UseCaseDescription useCaseDescription;

	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public String getExternaltoolid() {
		return externaltoolid;
	}

	public void setExternaltoolid(String externaltoolid) {
		this.externaltoolid = externaltoolid;
	}

	public Icon getIcon() {
		if (useCaseDescription != null) {
			String icon_url = useCaseDescription.getIcon_url();
			if ((icon_url != null) && !icon_url.isEmpty() && !icon_url.endsWith(".ico"))
				try {
					ImageIcon result = new ImageIcon(new URL(icon_url));
					if ((result != null) && (result.getIconHeight() != 0) && (result.getIconWidth() != 0)){
						return result;
					}
				} catch (MalformedURLException e) {
					logger.error("Problematic URL" + icon_url, e);
				}
		}
		return ExternalToolActivityIcon.getExternalToolIcon();
	}

	public Class<? extends Activity<ExternalToolActivityConfigurationBean>> getActivityClass() {
		return ExternalToolActivity.class;
	}

	public ExternalToolActivityConfigurationBean getActivityConfiguration() {
		ExternalToolActivityConfigurationBean bean = new ExternalToolActivityConfigurationBean();
		bean.setRepositoryUrl(repositoryUrl);
		bean.setExternaltoolid(externaltoolid);
		bean.setUseCaseDescription(useCaseDescription);
		bean.setMechanism(manager.getDefaultMechanism());

		return bean;
	}

	public String getName() {
		return externaltoolid;
	}

	@SuppressWarnings("unchecked")
	public List<? extends Comparable> getPath() {
		List<String> result = new ArrayList<String>();
		result.add("Tools decribed @ " + repositoryUrl);
		String group = useCaseDescription.getGroup();
		if ((group != null) && !group.isEmpty()) {
			String[] groups = group.split(":");
			for (String g : groups) {
				result.add(g);
			}
		}
		return result;
	}

	protected List<Object> getIdentifyingData() {
		// we require use cases inside one XML file to have unique IDs, which
		// means every externaltool is uniquely identified by its repository URL and
		// its use case ID.
		return Arrays.<Object> asList(repositoryUrl, externaltoolid);
	}
	
	public String getDescription() {
		if (useCaseDescription != null) {
			String description = useCaseDescription.getDescription();
			if (description == null) {
				return "";
			}
			return description;
		}
		return "";
	}

	public void setUseCaseDescription(UseCaseDescription usecase) {
		this.useCaseDescription = usecase;
	}

}
