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
import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.actions.ExternalToolActivityConfigureAction;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputStatic;
import de.uni_luebeck.inb.knowarc.usecases.ScriptOutput;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

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
		String html = "";
		ExternalToolActivityConfigurationBean bean = getConfigBean();
		String repositoryUrl = bean.getRepositoryUrl();
		if ((repositoryUrl == null) || repositoryUrl.isEmpty()){
		    repositoryUrl = "<b>Not specified</b>";
		}
		html += "<tr><td>Repository URL</td><td>" + repositoryUrl + "</td></tr>";

		String id = bean.getExternaltoolid();
		if ((id == null) || id.isEmpty()){
		    id = "<b>Not specified</b>";
		}
		html += "<tr><td>Id</td><td>" + id + "</td></tr>";
		
		UseCaseDescription useCaseDescription = bean.getUseCaseDescription();
		String name = useCaseDescription.getUsecaseid();
		if ((name == null) || name.isEmpty()){
		    name = "<b>Not specified</b>";
		}
		html += "<tr><td>Name</td><td>" + name + "</td></tr>";
		
		Map<String, ScriptInput> inputs = useCaseDescription.getInputs();
		if (!inputs.isEmpty()) {
		    html += "<tr><td colspan=2 align=center><b>Inputs</b></td></tr>";
		    html += "<tr><td><b>Port name</b></td><td><b>Action</b></td></tr>";
		    for (String siName : inputs.keySet()) {
			html += "<tr><td>" + siName + "</td>";
			ScriptInput si = inputs.get(siName);
			if (si.isFile()) {
			    html += "<td>File: " + si.getTag() + "</td>";
			} else if (si.isTempFile()) {
			    html += "<td>Temporary file</td>";
			} else {
			    html += "<td>Replaces: %%" + si.getTag() + "%%</td>";
			}
			html += "</tr>";
		    }
		}
		List<ScriptInputStatic> staticInputs = useCaseDescription.getStatic_inputs();
		if (!staticInputs.isEmpty()) {
		    html += "<tr><td colspan=2 align=center><b>Static inputs</b></td></tr>";
		    html += "<tr><td><b>Type</b></td><td><b>Action</b></td></tr>";
		    for (ScriptInputStatic si : staticInputs) {
			if (si.getUrl() != null) {
			    html += "<td><b>URL</b></td>";
			} else {
			    html += "<td><b>Explicit content</b></td>";
			}
			if (si.isFile()) {
			    html += "<td>File: " + si.getTag() + "</td>";
			} else if (si.isTempFile()) {
			    html += "<td>Temporary file</td>";
			} else {
			    html += "<td>Replaces: %%" + si.getTag() + "%%</td>";
			}
			html += "</tr>";
		    }
		}
		Map<String, ScriptOutput> outputs = useCaseDescription.getOutputs();
		if (!outputs.isEmpty()) {
		    html += "<tr><td colspan=2 align=center><b>Outputs</b></td></tr>";
		    html += "<tr><td><b>Port name</b></td><td><b>File</b></td></tr>";
		    for (String soName : outputs.keySet()) {
			html += "<tr><td>" + soName + "</td>";
			ScriptOutput so = outputs.get(soName);
			html += "<td>" + so.getPath() + "</td>";
			html += "</tr>";
		    }
		}
		return html;
	}

	@Override
	public String getViewTitle() {
		return "External Tool service";
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
