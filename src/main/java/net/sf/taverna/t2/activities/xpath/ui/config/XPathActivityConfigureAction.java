package net.sf.taverna.t2.activities.xpath.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.activities.xpath.XPathActivityConfigurationBean;

@SuppressWarnings("serial")
/**
 * @author Sergejs Aleksejevs
 */
public class XPathActivityConfigureAction extends ActivityConfigurationAction<XPathActivity, XPathActivityConfigurationBean>
{

	public XPathActivityConfigureAction(XPathActivity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e)
	{
		ActivityConfigurationDialog<XPathActivity,XPathActivityConfigurationBean> currentDialog =
		  ActivityConfigurationAction.getDialog(getActivity());
		
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		
		XPathActivityConfigurationPanelProvider panel = new XPathActivityConfigurationPanelProvider(getActivity());
		ActivityConfigurationDialog<XPathActivity, XPathActivityConfigurationBean> dialog =
		  new ActivityConfigurationDialog<XPathActivity, XPathActivityConfigurationBean>(getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);
	}

}
