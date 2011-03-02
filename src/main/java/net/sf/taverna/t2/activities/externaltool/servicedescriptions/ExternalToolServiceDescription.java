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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.RegisteredExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * ExternalToolServiceDescription stores the repository URL and the use case id so
 * that it can create an ExternalToolActivityConfigurationBean
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceDescription extends ServiceDescription<ExternalToolActivityConfigurationBean> {

	private String repositoryUrl;
	private String externaltoolid;

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
		return ExternalToolActivityIcon.getExternalToolIcon();
	}

	public Class<? extends Activity<ExternalToolActivityConfigurationBean>> getActivityClass() {
		return ExternalToolActivity.class;
	}

	public ExternalToolActivityConfigurationBean getActivityConfiguration() {
		RegisteredExternalToolActivityConfigurationBean bean = new RegisteredExternalToolActivityConfigurationBean();
		bean.setRepositoryUrl(repositoryUrl);
		bean.setExternaltoolid(externaltoolid);
		return bean;
	}

	public String getName() {
		return externaltoolid;
	}

	@SuppressWarnings("unchecked")
	public List<? extends Comparable> getPath() {
		return Collections.singletonList("ExternalTool @ " + repositoryUrl);
	}

	protected List<Object> getIdentifyingData() {
		// we require use cases inside one XML file to have unique IDs, which
		// means every externaltool is uniquely identified by its repository URL and
		// its use case ID.
		return Arrays.<Object> asList(repositoryUrl, externaltoolid);
	}

}
