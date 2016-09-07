
package org.apache.taverna.activities.externaltool.servicedescriptions;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.workbench.activityicons.ActivityIconSPI;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

/**
 * This class provides an icon for the use case activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolActivityIcon implements ActivityIconSPI {

	private static final String PROCESSOR_COLOUR_STRING = "#F28C55";

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

	public void setColourManager(ColourManager colourManager) {
		// set colour for XPath processors in the workflow diagram
		colourManager.setPreferredColour(ExternalToolActivity.class.getCanonicalName(),
				Color.decode(PROCESSOR_COLOUR_STRING));
	}

}
