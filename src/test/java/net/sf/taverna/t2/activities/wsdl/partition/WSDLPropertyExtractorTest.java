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
package net.sf.taverna.t2.activities.wsdl.partition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.datatransfer.Transferable;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.wsdl.query.WSDLActivityItem;
import net.sf.taverna.t2.partition.ActivityItem;
import net.sf.taverna.t2.partition.PropertyExtractorSPI;
import net.sf.taverna.t2.partition.PropertyExtractorSPIRegistry;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.junit.Test;

public class WSDLPropertyExtractorTest {

	@Test
	public void testSPI() {
		List<PropertyExtractorSPI> instances = PropertyExtractorSPIRegistry.getInstance().getInstances();
		assertTrue("There should be more than one instance found",instances.size()>0);
		boolean found = false;
		for (PropertyExtractorSPI spi : instances) {
			if (spi instanceof WSDLPropertyExtractor) {
				found=true;
				break;
			}
		}
		assertTrue("A WSDLPropertyExtractor should have been found",found);
	}
	
	@Test
	public void testExtractProperties() {
		WSDLActivityItem item = new WSDLActivityItem();
		item.setUse("USE");
		item.setStyle("STYLE");
		item.setOperation("OPERATION");
		item.setUrl("URL");
		Map<String,Object> props = new WSDLPropertyExtractor().extractProperties(item);
		assertEquals("missing or incorrect property","USE",props.get("use"));
		assertEquals("missing or incorrect property","STYLE",props.get("style"));
		assertEquals("missing or incorrect property","OPERATION",props.get("operation"));
		assertEquals("missing or incorrect property","URL",props.get("url"));
		assertEquals("missing or incorrect property","WSDL",props.get("type"));
	}
	
	@Test
	public void testExtractPropertiesNotWSDL() {
		ActivityItem item = new ActivityItem() {

			public Transferable getActivityTransferable() {
				// TODO Auto-generated method stub
				return null;
			}

			public Icon getIcon() {
				// TODO Auto-generated method stub
				return null;
			}

			public int compareTo(ActivityItem o) {
				// TODO Auto-generated method stub
				return 0;
			}

			public Object getConfigBean() {
				// TODO Auto-generated method stub
				return null;
			}

			public Activity<?> getUnconfiguredActivity() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		Map<String,Object> props = new WSDLPropertyExtractor().extractProperties(item);
		assertNotNull("A map should have been returned, even though its empty",props);
		assertEquals("There should be no properties",0,props.size());
	}
}
