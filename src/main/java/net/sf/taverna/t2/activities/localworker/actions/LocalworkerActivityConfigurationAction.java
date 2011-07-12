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
package net.sf.taverna.t2.activities.localworker.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.views.LocalworkerActivityConfigView;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.annotationbeans.HostInstitution;
import net.sf.taverna.t2.lang.ui.ModelMap;
import net.sf.taverna.t2.workbench.ModelMapConstants;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;
import net.sf.taverna.t2.workbench.helper.Helper;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;
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
	
	public static final String EDIT_LOCALWORKER_SCRIPT = "Edit beanshell script";

	private final Frame owner;

	public LocalworkerActivityConfigurationAction(LocalworkerActivity activity,
			Frame owner) {
		super(activity);
		putValue(Action.NAME, EDIT_LOCALWORKER_SCRIPT);
		this.owner = owner;
	}

	/**
	 * If the localworker has not been changed it pops up a {@link JOptionPane}
	 * warning the user that they change things at their own risk. Otherwise
	 * just show the config view
	 */
	public void actionPerformed(ActionEvent e) {
		Object[] options = { "Continue", "Cancel" };
		if (!activity.isAltered()) {
			int n = JOptionPane
					.showOptionDialog(
							null,
							"Changing the properties of a Local Worker may affect its behaviour. Do you want to continue?",
							"WARNING", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, // do not use a
							// custom Icon
							options, options[0]);

			if (n == 0) {
				// continue was clicked so prepare for config
				openDialog();
			} else {
				// do nothing
			}
		} else {
			openDialog();			
		}
	}

	private void openDialog() {
		ActivityConfigurationDialog<LocalworkerActivity, LocalworkerActivityConfigurationBean> currentDialog = ActivityConfigurationAction.getDialog(getActivity());
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		final LocalworkerActivity activity = (LocalworkerActivity) getActivity();
		final LocalworkerActivityConfigView localworkerConfigView =
			new LocalworkerActivityConfigView(activity);
		final ActivityConfigurationDialog dialog =
			new ActivityConfigurationDialog (getActivity(), localworkerConfigView);
		ActivityConfigurationAction.setDialog(getActivity(), dialog);			

		
	}
}
