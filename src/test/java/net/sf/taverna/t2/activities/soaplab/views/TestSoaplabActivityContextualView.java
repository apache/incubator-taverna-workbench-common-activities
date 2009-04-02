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
package net.sf.taverna.t2.activities.soaplab.views;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;
import net.sf.taverna.t2.activities.soaplab.actions.SoaplabActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactoryRegistry;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TestSoaplabActivityContextualView {

	Activity<?> a;
		
	@Before
	public void setup() throws Exception {
		a=new SoaplabActivity();
		SoaplabActivityConfigurationBean sb = new SoaplabActivityConfigurationBean();
		sb.setEndpoint("http://www.ebi.ac.uk/soaplab/services/edit.seqret");
		((SoaplabActivity)a).configure(sb);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@Ignore("Integration test")
	public void testDisovery() throws Exception {
		ContextualViewFactory factory = ContextualViewFactoryRegistry.getInstance().getViewFactoryForObject(a);
		assertTrue("Factory should be SoaplabActivityViewFactory",factory instanceof SoaplabActivityViewFactory);
		ContextualView view = factory.getView(a);
		assertTrue("The view should be SoaplabActivityContextualView",view instanceof SoaplabActivityContextualView);
	}
	
	@Test
	@Ignore("Integration test")
	public void testConfigureAction() throws Exception {
		ContextualView view = new SoaplabActivityContextualView(a);
		assertNotNull("the action should not be null",view.getConfigureAction(null));
		assertTrue("The action should be a SoaplabAcitivyConfigurationAction",view.getConfigureAction(null) instanceof SoaplabActivityConfigurationAction);
	}
	
	private void run() throws Exception
	{
		setup();
		ContextualView view = new SoaplabActivityContextualView(a);
		view.setVisible(true);
	}
	
	public static void main(String[] args) throws Exception {
		new TestSoaplabActivityContextualView().run();
	}
}
