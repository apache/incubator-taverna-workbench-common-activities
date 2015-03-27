/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.spreadsheet.servicedescriptions;

import java.awt.Color;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.taverna.workbench.activityicons.ActivityIconSPI;
import org.apache.taverna.workbench.configuration.colour.ColourManager;

/**
 * UI icon for the SpreadsheetImport Activity.
 *
 * @author David Withers
 */
public class SpreadsheetImportActivityIcon implements ActivityIconSPI {

	public static final String SPREADSHEET_COLOUR_HTML = "#40e0d0";
	public static final Color SPREADSHEET_COLOUR = Color.decode(SPREADSHEET_COLOUR_HTML);

	private static Icon icon = null;

	@Override
	public int canProvideIconScore(URI activityType) {
		if (SpreadsheetImportTemplateService.ACTIVITY_TYPE.equals(activityType))
			return DEFAULT_ICON + 1;
		else
			return NO_ICON;
	}

	@Override
	public Icon getIcon(URI activityType) {
		return getSpreadsheetImportIcon();
	}

	public static Icon getSpreadsheetImportIcon() {
		if (icon == null) {
			icon = new ImageIcon(
					SpreadsheetImportActivityIcon.class.getResource("/spreadsheet-import.png"));
		}
		return icon;
	}

	public void setColourManager(ColourManager colourManager) {
		colourManager.setPreferredColour(
				"http://ns.taverna.org.uk/2010/activity/spreadsheet-import",
				SPREADSHEET_COLOUR);
	}

}
