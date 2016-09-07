/*******************************************************************************
 ******************************************************************************/

package org.apache.taverna.activities.externaltool.servicedescriptions;

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
