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
package net.sf.taverna.t2.activities.localworker.partition;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.activities.localworker.query.LocalworkerActivityItem;
import net.sf.taverna.t2.partition.PropertyExtractorSPI;

/**
 * States what type of "things" a Local Worker can be queried for in the
 * Activity Tree. In this case it is "type" and "operation" - which return
 * "Localworker" and "actual-localworker-activity-type"
 * 
 * @author Ian Dunlop
 * 
 */
public class LocalworkerPropertyExtractor implements PropertyExtractorSPI {

	public Map<String, Object> extractProperties(Object target) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (target instanceof LocalworkerActivityItem) {
			LocalworkerActivityItem item = (LocalworkerActivityItem) target;
			map.put("type", item.getType());
			map.put("operation", item.getOperation());
			map.put("category", item.getCategory());
			map.put("provider",item.getProvider());
		}
		return map;
	}

}
