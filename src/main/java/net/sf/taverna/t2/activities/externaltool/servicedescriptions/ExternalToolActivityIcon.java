/*******************************************************************************
 * Copyright (C) 2009 Hajo Nils Krabbenhoeft, INB, University of Luebeck   
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


package net.sf.taverna.t2.activities.externaltool.servicedescriptions;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;
import net.sf.taverna.t2.workbench.ui.impl.configuration.colour.ColourManager;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * This class provides an icon for the use case activity.
 * 
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolActivityIcon implements ActivityIconSPI{
	
	 private static final String PROCESSOR_COLOUR_STRING = "#F28C55";

	static {
		    // set colour for XPath processors in the workflow diagram
		    ColourManager.getInstance().setPreferredColour(
		        ExternalToolActivity.class.getCanonicalName(), Color.decode(PROCESSOR_COLOUR_STRING));
		  }

	
	private static Icon icon;
	
	public int canProvideIconScore(Activity<?> activity) {
		if (activity.getClass().getName().equals(ExternalToolActivity.class.getName()))
			return DEFAULT_ICON + 1;
		else
			return NO_ICON;
	}

	public Icon getIcon(Activity<?> activity) {
		return getExternalToolIcon();
	}
	
	public static Icon getExternalToolIcon() {
		if (icon == null) {
			icon = new ImageIcon(ExternalToolActivityIcon.class.getResource("/externaltool.png"));
		}
		return icon;
	}
	
	public static String getColourString() {
		return PROCESSOR_COLOUR_STRING;
	}
}



