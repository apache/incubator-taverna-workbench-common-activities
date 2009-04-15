/*********************************************************************
 * Copyright (C) 2007-2009 The University of Manchester   
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
 **********************************************************************/
package net.sf.taverna.t2.activities.wsdl.servicedescriptions;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.wsdl.WSDLActivity;
import net.sf.taverna.t2.activities.wsdl.WSDLActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public class WSDLServiceDescription implements ServiceDescription<WSDLActivityConfigurationBean> {

	private static final String WSDL = "WSDL @ ";
	
	private String use;
	private URI uri;
	private String style;
	private String operation;

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public URI getURI() {
		return uri;
	}

	public void setURI(URI url) {
		this.uri = url;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getType() {
		return "WSDL";
	}

	@Override
	public String toString() {
		return operation;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	
	public Icon getIcon() {
		return new ImageIcon(getClass().getResource("/wsdl.png"));
	}
	

	public Class<? extends Activity<WSDLActivityConfigurationBean>> getActivityClass() {
		return WSDLActivity.class;
	}

	public WSDLActivityConfigurationBean getActivityConfiguration() {
		WSDLActivityConfigurationBean bean = new WSDLActivityConfigurationBean();
		bean.setWsdl(getURI().toASCIIString());
		bean.setOperation(getOperation());
		return bean;	}

	public String getName() {
		return getOperation();
	}

	@SuppressWarnings("unchecked")
	public List<? extends Comparable> getPath() {
		return Collections.singletonList(WSDL + getURI());
	}

	public boolean isTemplateService() {		
		return false;
	}

}
