package net.sf.taverna.t2.activities.xpath.ui.contextualview;

import java.util.Arrays;
import java.util.List;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ContextualViewFactory;

import net.sf.taverna.t2.activities.xpath.XPathActivity;

/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityMainContextViewFactory implements
		ContextualViewFactory<XPathActivity>
{

	public boolean canHandle(Object selection) {
		return selection instanceof XPathActivity;
	}

	public List<ContextualView> getViews(XPathActivity selection) {
		return Arrays.<ContextualView>asList(new XPathActivityMainContextualView(selection));
	}
	
}
