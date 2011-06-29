/*******************************************************************************
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
 ******************************************************************************/
package net.sf.taverna.t2.activities.rest.ui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.URI;

import javax.swing.Action;
import javax.swing.KeyStroke;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.ui.servicedescription.GenericRESTTemplateService;
import net.sf.taverna.t2.ui.menu.AbstractMenuAction;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.ui.workflowview.WorkflowView;
import net.sf.taverna.t2.workbench.views.graph.actions.DesignOnlyAction;
import net.sf.taverna.t2.workbench.views.graph.menu.InsertMenu;

import org.apache.log4j.Logger;

/**
 * An action to add a REST activity + a wrapping processor to the workflow.
 * 
 * @author Alex Nenadic
 * @author alanrw
 * 
 */
@SuppressWarnings("serial")
public class AddRESTTemplateMenuAction extends AbstractMenuAction {

	private static final String ADD_REST = "REST";

	private static final URI ADD_REST_URI = URI
	.create("http://taverna.sf.net/2008/t2workbench/menu#graphMenuAddREST");

	private static Logger logger = Logger
			.getLogger(AddRESTTemplateMenuAction.class);

	public AddRESTTemplateMenuAction() {
		super(InsertMenu.INSERT, 500, ADD_REST_URI);
	}

	@Override
	protected Action createAction() {

		return new AddRESTMenuAction();
	}
	
	protected class AddRESTMenuAction extends DesignOnlyAction {
		AddRESTMenuAction () {
			super ();
			putValue(SMALL_ICON, ActivityIconManager.getInstance().iconForActivity(
					new RESTActivity()));
			putValue(NAME, ADD_REST);	
			putValue(SHORT_DESCRIPTION, "REST service");	
			putValue(Action.ACCELERATOR_KEY,
					KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		}

		public void actionPerformed(ActionEvent e) {
			WorkflowView.importServiceDescription(GenericRESTTemplateService.getServiceDescription(),
			false);
		}
	}

}
