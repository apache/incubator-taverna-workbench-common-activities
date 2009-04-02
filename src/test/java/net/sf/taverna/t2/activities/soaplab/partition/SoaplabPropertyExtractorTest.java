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
package net.sf.taverna.t2.activities.soaplab.partition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import net.sf.taverna.t2.activities.soaplab.query.SoaplabActivityItem;
import net.sf.taverna.t2.partition.PropertyExtractorSPI;
import net.sf.taverna.t2.partition.PropertyExtractorSPIRegistry;

import org.junit.Test;

public class SoaplabPropertyExtractorTest {

	@Test
	public void testSPI() {
		List<PropertyExtractorSPI> instances = PropertyExtractorSPIRegistry.getInstance().getInstances();
		assertTrue("There should be more than one instance found",instances.size()>0);
		boolean found = false;
		for (PropertyExtractorSPI spi : instances) {
			if (spi instanceof SoaplabPropertyExtractor) {
				found=true;
				break;
			}
		}
		assertTrue("A WSDLPropertyExtractor should have been found",found);
	}
	
	@Test
	public void testExtractProperties() {
		SoaplabActivityItem item = new SoaplabActivityItem();
		item.setCategory("CATEGORY");
		item.setOperation("OPERATION");
		item.setUrl("URL");
		Map<String,Object> props = new SoaplabPropertyExtractor().extractProperties(item);
		assertEquals("missing or incorrect property","CATEGORY",props.get("category"));
		assertEquals("missing or incorrect property","OPERATION",props.get("operation"));
		assertEquals("missing or incorrect property","URL",props.get("url"));
		assertEquals("missing or incorrect property","Soaplab",props.get("type"));
	}

}
