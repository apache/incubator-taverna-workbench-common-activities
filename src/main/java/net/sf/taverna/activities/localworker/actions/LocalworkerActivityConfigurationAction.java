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
package net.sf.taverna.activities.localworker.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.views.LocalworkerActivityConfigView;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.annotationbeans.HostInstitution;
import net.sf.taverna.t2.lang.ui.ModelMap;
import net.sf.taverna.t2.workbench.ModelMapConstants;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;
import net.sf.taverna.t2.workbench.helper.Helper;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.EditException;
import net.sf.taverna.t2.workflowmodel.EditsRegistry;

/**
 * The {@link LocalworkerActivity}s have pre-defined scripts, ports etc in a
 * serialised form on disk. So if the user wants to change them they have to do
 * so at own risk.
 * 
 * @author Ian Dunlop
 * 
 */
@SuppressWarnings("serial")
public class LocalworkerActivityConfigurationAction extends
		ActivityConfigurationAction<LocalworkerActivity, BeanshellActivityConfigurationBean> {

	private static Logger logger = Logger.getLogger(LocalworkerActivityConfigurationAction.class);

	private final Frame owner;

	public LocalworkerActivityConfigurationAction(LocalworkerActivity activity,
			Frame owner) {
		super(activity);
		this.owner = owner;
	}

	/**
	 * If the localworker has not been changed it pops up a {@link JOptionPane}
	 * warning the user that they change things at their own risk. Otherwise
	 * just show the config view
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// tell the use that this is a local worker and if they change it then
		// to be careful
		final LocalworkerActivityConfigView localworkerConfigView = new LocalworkerActivityConfigView(
				(LocalworkerActivity) getActivity());
		final HelpEnabledDialog frame = new HelpEnabledDialog(owner, "LocalWorker Activity Configuration", true, null);
		frame.add(localworkerConfigView);
		frame.setSize(500, 600);

		localworkerConfigView.setButtonClickedListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// FIXME when the user clicks config on a local worker it should
				// add it as a user defined one to the activity palette
				frame.setVisible(false);
				if (localworkerConfigView.isConfigurationChanged()) {
					configureActivity(localworkerConfigView.getConfiguration());
					addAnnotation();
				}
			}

		});

		Object[] options = { "Continue", "Cancel" };
		if (!checkAnnotations()) {
			int n = JOptionPane
					.showOptionDialog(
							frame,
							"Changing the properties of a Local Worker may affect its behaviour. Do you want to continue?",
							"WARNING", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, // do not use a
							// custom Icon
							options, options[0]);

			if (n == 0) {
				// continue was clicked so prepare for config
				frame.setVisible(true);
			} else {
				// do nothing
			}
		} else {
			frame.setVisible(true);			
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
							getActivity(), hostInstitutionAnnotation));
			logger.info("Check Annotations gave " + checkAnnotations());
		} catch (EditException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Checks if the user has altered the local worker ie. an annotation has
	 * been added to it
	 * 
	 * @return
	 */
	private boolean checkAnnotations() {
		for (AnnotationChain chain : getActivity().getAnnotations()) {
			for (AnnotationAssertion<?> assertion : chain.getAssertions()) {
				Object detail = assertion.getDetail();
				System.out.println(detail.getClass().getName());
				if (detail instanceof HostInstitution) {
					return true;
				}
			}
		}
		return false;
	}

}
