/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.activities.localworker.views;

import net.sf.taverna.t2.activities.beanshell.views.BeanshellConfigView;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.annotation.annotationbeans.HostInstitution;
import net.sf.taverna.t2.lang.ui.ModelMap;
import net.sf.taverna.t2.workbench.ModelMapConstants;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;

import org.apache.log4j.Logger;


@SuppressWarnings("serial")
public class LocalworkerActivityConfigView extends BeanshellConfigView{

	private static Logger logger = Logger.getLogger(LocalworkerActivityConfigView.class);

	public LocalworkerActivityConfigView(LocalworkerActivity activity) {
		super(activity);
		initLocalworker();
	}

	private void initLocalworker() {
	}
	
	public void noteConfiguration() {
		if (isConfigurationChanged()) {
			super.noteConfiguration();
			addAnnotation();
		}
	}
	
	/**
	 * Annotate the Activity with the name of the Institution or person who
	 * created the activity. Useful for Localworkers that have been altered by a
	 * user
	 */
	private void addAnnotation() {
		// FIXME use a more useful name or a different type of annotation, this
		// is just here as a marker so that
		// the colour manager works
		HostInstitution hostInstitutionAnnotation = new HostInstitution();
		hostInstitutionAnnotation.setText("UserNameHere");

		try {
			// force the dataflow view to update with the annotation added,
			// therefore triggering the localworker to be coloured as a
			// beanshell
			EditManager.getInstance().doDataflowEdit(
					(Dataflow) ModelMap.getInstance().getModel(
							ModelMapConstants.CURRENT_DATAFLOW),
					EditsRegistry.getEdits().getAddAnnotationChainEdit(
							activity, hostInstitutionAnnotation));
			ActivityIconManager.getInstance().resetIcon(activity);
		} catch (EditException e) {
			logger.error(e);
		}
	}

}
