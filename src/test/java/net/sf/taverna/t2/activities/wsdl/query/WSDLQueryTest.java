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
package net.sf.taverna.t2.activities.wsdl.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class WSDLQueryTest {

	private static String wsdlUrl;
	@BeforeClass
	public static void setup() {
		wsdlUrl=WSDLQueryTest.class.getResource("/kegg.wsdl").toExternalForm();
	}
	
	
	@Test
	public void testDoQuery() {
		WSDLQuery q = new WSDLQuery(wsdlUrl);
		q.doQuery();
		assertEquals("The query should be 69 items",69,q.size());
		WSDLActivityItem i = (WSDLActivityItem)q.toArray()[0];
		assertEquals("The type shoudl be WSDL","WSDL",i.getType());
		assertEquals("The use should be encoded","encoded",i.getUse());
		assertEquals("The style should be RPC","rpc",i.getStyle());
		assertNotNull("The operation should be set",i.getOperation());
		assertTrue("The operation should be have some content",i.getOperation().length()>2);
	}
	
}
