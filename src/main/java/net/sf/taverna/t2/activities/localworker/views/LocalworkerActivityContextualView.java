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

import java.awt.Frame;

import javax.swing.Action;

import net.sf.taverna.activities.localworker.actions.LocalworkerActivityConfigurationAction;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.servicedescriptions.LocalworkerServiceProvider;
import net.sf.taverna.t2.annotation.AnnotationAssertion;
import net.sf.taverna.t2.annotation.AnnotationChain;
import net.sf.taverna.t2.annotation.annotationbeans.HostInstitution;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

@SuppressWarnings("serial")
public class LocalworkerActivityContextualView extends
		HTMLBasedActivityContextualView<LocalworkerActivityConfigurationBean> {

	public LocalworkerActivityContextualView(Activity<?> activity) {
		super(activity);
	}

	@Override
	protected String getRawTableRowsHtml() {
		String html = "<tr><th>Input Port Name</th><th>Depth</th></tr>";
		for (ActivityInputPortDefinitionBean bean : getConfigBean()
				.getInputPortDefinitions()) {
			html = html + "<tr><td>" + bean.getName() + "</td><td>"
					+ bean.getDepth() + "</td></tr>";
		}
		html = html
				+ "<tr><th>Output Port Name</th><th>Depth</th></tr>";
		for (ActivityOutputPortDefinitionBean bean : getConfigBean()
				.getOutputPortDefinitions()) {
			html = html + "<tr></td>" + bean.getName() + "</td><td>"
					+ bean.getDepth() + "</td>" //<td>" + bean.getGranularDepth()
					//+ "</td>" 
					+ "</tr>";
		}
		return html;
	}

	@Override
	public String getViewTitle() {
		LocalworkerActivity localActivity = (LocalworkerActivity) getActivity();
		String workerName = LocalworkerServiceProvider.getServiceNameFromClassname(localActivity.getConfiguration().getLocalworkerName());
		if (localActivity.isAltered()) {
			// this is a user defined localworker so use the correct name
			return "Altered local worker activity - originally " +
				workerName;
		} else {

			return "Local worker activity - " + workerName;
		}
	}

	private boolean checkAnnotations() {
		for (AnnotationChain chain : getActivity().getAnnotations()) {
			for (AnnotationAssertion<?> assertion : chain.getAssertions()) {
				Object detail = assertion.getDetail();
				if (detail instanceof HostInstitution) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new LocalworkerActivityConfigurationAction(
				(LocalworkerActivity) getActivity(), owner);
	}

}
