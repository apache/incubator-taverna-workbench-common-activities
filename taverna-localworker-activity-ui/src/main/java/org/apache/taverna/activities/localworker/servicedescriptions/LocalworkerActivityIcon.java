package org.apache.taverna.activities.localworker.servicedescriptions;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.taverna.workbench.activityicons.ActivityIconSPI;

/**
 *
 * @author Alex Nenadic
 * @author David Withers
 */
public class LocalworkerActivityIcon implements ActivityIconSPI {

	private static Icon icon;

	public int canProvideIconScore(URI activityType) {
		if (LocalworkerServiceDescription.ACTIVITY_TYPE.equals(activityType))
			return DEFAULT_ICON + 1;
		else
			return NO_ICON;
	}

	public Icon getIcon(URI activityType) {
		return getLocalworkerIcon();
	}

	public static Icon getLocalworkerIcon() {
		if (icon == null) {
			icon = new ImageIcon(LocalworkerActivityIcon.class
					.getResource("/localworker.png"));
		}
		return icon;
	}
}

