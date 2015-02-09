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
package net.sf.taverna.t2.activities.wsdl.views;

import static org.junit.Assert.assertNull;

import org.junit.Before;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestWSDLActivityContextualView {

	Activity a;

	@Before
	public void setUp() throws Exception {
		a=new Activity();
		Configuration configuration = new Configuration();
		ObjectNode json = (ObjectNode) configuration.getJson();
		ObjectNode operation = json.objectNode();
		operation.put("name", "getReport");
		json.set("operation", operation);
		String wsdlUrl=TestWSDLActivityContextualView.class.getResource("/GMService.wsdl").toExternalForm();
		operation.put("wsdl", wsdlUrl);
		configuration.setConfigures(a);
	}

	public void testConfigurationAction() {
		WSDLActivityContextualView view = new WSDLActivityContextualView(a, null, null, null, null, null, null, null);
		assertNull("WSDL has no configure action, so should be null",view.getConfigureAction(null));
	}
}
