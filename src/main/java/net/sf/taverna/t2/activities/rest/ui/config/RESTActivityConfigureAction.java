package net.sf.taverna.t2.activities.rest.ui.config;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import net.sf.taverna.t2.workbench.ui.actions.activity.ActivityConfigurationAction;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationDialog;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;

@SuppressWarnings("serial")
/**
 * @author Sergejs Aleksejevs
 */
public class RESTActivityConfigureAction extends	ActivityConfigurationAction<RESTActivity, RESTActivityConfigurationBean>
{

	public RESTActivityConfigureAction(RESTActivity activity, Frame owner) {
		super(activity);
	}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e)
	{
		ActivityConfigurationDialog<RESTActivity,RESTActivityConfigurationBean> currentDialog =
		  ActivityConfigurationAction.getDialog(getActivity());
		
		if (currentDialog != null) {
			currentDialog.toFront();
			return;
		}
		
		RESTActivityConfigurationPanel panel = new RESTActivityConfigurationPanel(getActivity());
		ActivityConfigurationDialog<RESTActivity,RESTActivityConfigurationBean> dialog =
		  new ActivityConfigurationDialog<RESTActivity, RESTActivityConfigurationBean>(getActivity(), panel);

		ActivityConfigurationAction.setDialog(getActivity(), dialog);
	}

}
