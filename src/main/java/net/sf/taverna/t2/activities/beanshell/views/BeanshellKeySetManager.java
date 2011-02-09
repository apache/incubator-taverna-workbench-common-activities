/*******************************************************************************
 * Copyright (C) 2009 Ingo Wassink of University of Twente, Netherlands and
 * The University of Manchester   
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

/**
 * @author Ingo Wassink
 * @author Ian Dunlop
 * @author Alan R Williams
 */
package net.sf.taverna.t2.activities.beanshell.views;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Manager for reading key set file
 * 
 * @author WassinkI
 * 
 */
public class BeanshellKeySetManager {
	
	private static Logger logger = Logger.getLogger(BeanshellKeySetManager.class);


	private static Set<String> keySet;

	/**
	 * Method for getting the keyset
	 * 
	 * @return
	 */
	public static Set<String> getKeySet() {
		if (keySet == null) {
			loadKeySet();
		}
		return keySet;
	}

	private static synchronized void loadKeySet() {
		if (keySet != null)
			return;

		keySet = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(

							BeanshellKeySetManager.class
									.getResourceAsStream("keys.txt")));
			                                                     
			String line;
			while ((line = reader.readLine()) != null) {
				keySet.add(line.trim());
			}
			reader.close();
		} catch (Exception e) {
			logger.error(e);
		}

	}
}
