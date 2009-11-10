/*******************************************************************************
 * Copyright (C) 2009 The University of Manchester   
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
package net.sf.taverna.t2.activities.spreadsheet.servicedescriptions;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;
import net.sf.taverna.t2.workbench.ui.impl.configuration.colour.ColourManager;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * UI icon for the SpreadsheetImport Activity.
 * 
 * @author David Withers
 */
public class SpreadsheetImportActivityIcon implements ActivityIconSPI {

	public static final String SPREADSHEET_COLOUR_HTML = "#40e0d0";
	public static final Color SPREADSHEET_COLOUR = Color.decode(SPREADSHEET_COLOUR_HTML);
	
	static {
		ColourManager.getInstance().setPreferredColour(
				"net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity", SPREADSHEET_COLOUR);
	}
	
	static Icon icon = null;

	public int canProvideIconScore(Activity<?> activity) {
		if (activity.getClass().getName().equals(SpreadsheetImportActivity.class.getName())) {
			return DEFAULT_ICON + 1;
		} else {
			return NO_ICON;
		}
	}

	public Icon getIcon(Activity<?> activity) {
		return getSpreadsheetImportIcon();
	}

	public static Icon getSpreadsheetImportIcon() {
		if (icon == null) {
			icon = new ImageIcon(SpreadsheetImportActivityIcon.class
					.getResource("/spreadsheet-import.png"));
		}
		return icon;
	}
}
