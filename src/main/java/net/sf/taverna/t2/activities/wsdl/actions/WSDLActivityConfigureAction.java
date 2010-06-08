/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester   
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
package net.sf.taverna.t2.activities.wsdl.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import net.sf.taverna.t2.activities.wsdl.WSDLActivity;
import net.sf.taverna.t2.activities.wsdl.WSDLActivityConfigurationBean;
import net.sf.taverna.t2.activities.wsdl.views.WSDLActivityConfigurationView;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
import net.sf.taverna.t2.workflowmodel.Dataflow;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class WSDLActivityConfigureAction
		extends
		ActivityConfigurationAction<WSDLActivity, WSDLActivityConfigurationBean> {

	private final Frame owner;
	private static Logger logger = Logger
			.getLogger(WSDLActivityConfigureAction.class);

	public WSDLActivityConfigureAction(WSDLActivity activity, Frame owner) {
		super(activity);
		putValue(Action.NAME, "Configure security");
		this.owner = owner;
	}

	public void actionPerformed(ActionEvent e) {

		// Should clone it
		WSDLActivityConfigurationBean bean = getActivity().getConfiguration();

		Dataflow owningDataflow = FileManager.getInstance()
		.getCurrentDataflow();
		
		/*WSSecurityProfileChooser wsSecurityProfileChooser = new WSSecurityProfileChooser(
				owner);
		if (wsSecurityProfileChooser.isInitialised()) {
			wsSecurityProfileChooser.setVisible(true);
		}

		WSSecurityProfile wsSecurityProfile = wsSecurityProfileChooser
				.getWSSecurityProfile();
		String profileString;
		if (wsSecurityProfile != null) { // user did not cancel
			profileString = wsSecurityProfile.getWSSecurityProfileString();
			logger.info("WSSecurityProfile string read as:" + profileString);
			bean.setSecurityProfile(profileString);
			ActivityConfigurationDialog.configureActivity(owningDataflow, getActivity(), bean);
		}*/
		
		WSDLActivityConfigurationView configDialog = new WSDLActivityConfigurationView(owner, bean);
		configDialog.setLocationRelativeTo(owner);
		configDialog.setVisible(true);
		
		// Get the new bean after configuration
		WSDLActivityConfigurationBean newBean = configDialog.getNewBean();
		
		if (newBean == null){ // user cancelled
			return;	
		}
				
		// Has anything changed in the configuration bean?
		if (bean.getSecurityProfile() == null){
			if (newBean.getSecurityProfile() != null){ // config changed
				logger.info("WSDL activity configuration: Old security profile: null");
				logger.info("WSDL activity configuration: New security profile: " +  newBean.getSecurityProfile());
				ActivityConfigurationDialog.configureActivityStatic(owningDataflow, getActivity(), newBean);
			}
		}
		else{
			if (!bean.getSecurityProfile().equals(newBean.getSecurityProfile())){ // config changed
				logger.info("WSDL activity configuration: Old security profile: " +  bean.getSecurityProfile());
				logger.info("WSDL activity configuration: New security profile: " +  newBean.getSecurityProfile());
				ActivityConfigurationDialog.configureActivityStatic(owningDataflow, getActivity(), newBean);
			}
		}
	}

}
