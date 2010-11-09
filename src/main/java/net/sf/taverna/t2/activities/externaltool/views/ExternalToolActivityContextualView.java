/*******************************************************************************
 * Copyright (C) 2010 Hajo Nils Krabbenhoeft, INB, University of Luebeck   
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

package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.Frame;

import javax.swing.Action;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.actions.ExternalToolActivityConfigureAction;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * ExternalToolActivityContextualView displays the use case information in a HTML table.
 * Currently, this is only the use case ID.
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolActivityContextualView extends HTMLBasedActivityContextualView<ExternalToolActivityConfigurationBean> {
	private static final long serialVersionUID = 1L;

	public ExternalToolActivityContextualView(Activity<?> activity) {
		super(activity);
	}

	@Override
	protected String getRawTableRowsHtml() {
		String html = "<b>Externaltool: </b>" + getConfigBean().getExternaltoolid();
		return html;
	}

	@Override
	public String getViewTitle() {
		return "use case activity";
	}

	@Override
	public Action getConfigureAction(final Frame owner) {
		return new ExternalToolActivityConfigureAction((ExternalToolActivity) getActivity(), owner);
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
