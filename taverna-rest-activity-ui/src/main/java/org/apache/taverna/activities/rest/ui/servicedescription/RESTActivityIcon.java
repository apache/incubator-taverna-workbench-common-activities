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

package org.apache.taverna.activities.rest.ui.servicedescription;

import java.awt.Color;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.taverna.workbench.activityicons.ActivityIconSPI;
import org.apache.taverna.workbench.configuration.colour.ColourManager;

/**
 *
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class RESTActivityIcon implements ActivityIconSPI {
	private static final Color PROCESSOR_COLOUR = Color.decode("#7AAFFF");

	private static ImageIcon icon;

	public int canProvideIconScore(URI activityType) {
		if (GenericRESTTemplateService.ACTIVITY_TYPE.equals(activityType))
			return DEFAULT_ICON + 1;
		else
			return NO_ICON;
	}

	public Icon getIcon(URI activityType) {
		return getRESTActivityIcon();
	}

	public static Icon getRESTActivityIcon() {
		if (icon == null) {
			synchronized (RESTActivityIcon.class) {
				if (icon == null) {
					try {
						icon = new ImageIcon(
								RESTActivityIcon.class.getResource("service_type_rest.png"));
					} catch (NullPointerException e) {
						/* icon wasn't found - do nothing, but no icon will be available */
					}
				}
			}
		}
		return (icon);
	}

	public void setColourManager(ColourManager colourManager) {
		// set colour for REST processors in the workflow diagram
		colourManager.setPreferredColour(GenericRESTTemplateService.ACTIVITY_TYPE.toString(), PROCESSOR_COLOUR);
	}

}
