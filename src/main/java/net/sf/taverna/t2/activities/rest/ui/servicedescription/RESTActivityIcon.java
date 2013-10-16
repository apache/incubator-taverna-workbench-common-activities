package net.sf.taverna.t2.activities.rest.ui.servicedescription;

import java.awt.Color;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;

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
