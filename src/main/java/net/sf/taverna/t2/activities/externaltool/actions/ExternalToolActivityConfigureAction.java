/*******************************************************************************
 * Copyright (C) 2009 Hajo Nils Krabbenhoeft, INB, University of Luebeck   
 * modified 2010 Hajo Nils Krabbenhoeft, spratpix GmbH & Co. KG
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

package net.sf.taverna.t2.activities.externaltool.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.Action;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.views.ExternalToolConfigView;
import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

/**
 * This class implements an ActivityConfigurationAction to configure the
 * ExternalToolActivity plugin. The configuration action is called
 * "Configure UseCase invocation" and is implemented in the
 * KnowARCConfigurationDialog inside the knowarc-usecases maven artifact.
 * 
 * @author Hajo Nils Krabbenhoeft
 */
@SuppressWarnings("serial")
public class ExternalToolActivityConfigureAction extends ActivityConfigurationAction<ExternalToolActivity, ExternalToolActivityConfigurationBean> {

	private final Frame owner;

	public ExternalToolActivityConfigureAction(ExternalToolActivity activity, Frame owner) {
		super(activity);
		putValue(Action.NAME, "Configure tool invocation");
		this.owner = owner;
	}

	public void actionPerformed(ActionEvent e) {
/*		if (getActivity().getConfiguration() instanceof RegisteredExternalToolActivityConfigurationBean) {
			new KnowARCConfigurationDialog(owner, false, KnowARCConfigurationFactory.getConfiguration()).setVisible(true);
		} else*/ {
			ActivityConfigurationDialog currentDialog = ActivityConfigurationAction.getDialog(getActivity());
			if (currentDialog != null) {
				currentDialog.toFront();
				return;
			}
			final ExternalToolConfigView externalToolConfigView = new ExternalToolConfigView((ExternalToolActivity)getActivity());
			final ActivityConfigurationDialog<ExternalToolActivity, ExternalToolActivityConfigurationBean> dialog =
				new ActivityConfigurationDialog<ExternalToolActivity, ExternalToolActivityConfigurationBean>(getActivity(), externalToolConfigView);

			ActivityConfigurationAction.setDialog(getActivity(), dialog);	
		}
	}
}
