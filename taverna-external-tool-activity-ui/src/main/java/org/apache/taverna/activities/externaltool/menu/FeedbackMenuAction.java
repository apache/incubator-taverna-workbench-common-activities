
package org.apache.taverna.activities.externaltool.menu;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.apache.taverna.ui.menu.AbstractMenuAction;

import org.apache.log4j.Logger;

/**
 * This class adds the feedback item to the context menu of every use case
 * activity.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class FeedbackMenuAction extends AbstractMenuAction {

	private static Logger logger = Logger.getLogger(FeedbackMenuAction.class);


	private static final URI feedbackSection = URI.create("http://taverna.sf.net/2009/contextMenu/configure");

	public FeedbackMenuAction() {
		super(feedbackSection, 51);
	}

	protected Action createAction() {
	    // final ImageIcon icon = KnowARCConfigurationFactory.getConfiguration().getIcon();
		return new SendFeedbackAction("Send Feedback...", null);
	}

	private final class SendFeedbackAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private static final String errTitle = "Could not open web browser for feedback:";
		private static final String feedbackUrl = "http://www.taverna.org.uk/about/contact-us/feedback?product=ExternalToolService";

		private SendFeedbackAction(String name, Icon icon) {
			super(name, icon);
		}

		public void actionPerformed(ActionEvent e) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(URI.create(feedbackUrl));
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, feedbackUrl + "\n" + e1.getLocalizedMessage(), errTitle, JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Go to " + feedbackUrl, errTitle, JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
