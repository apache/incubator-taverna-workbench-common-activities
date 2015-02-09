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

import net.sf.taverna.t2.lang.beans.PropertyAnnotated;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;

/**
 * ExternalToolServiceProviderConfig stores the URL of the use case repository XML file
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolServiceProviderConfig extends PropertyAnnotated {
	private String repositoryUrl;

	public ExternalToolServiceProviderConfig() {
	}

	public ExternalToolServiceProviderConfig(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	@PropertyAnnotation(displayName = "Tool registry location", preferred = true)
	public String getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	@Override
	public String toString() {
		return repositoryUrl;
	}

}
