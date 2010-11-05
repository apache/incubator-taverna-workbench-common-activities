package net.sf.taverna.t2.activities.xpath.ui.contextualview;

import javax.swing.Action;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.activities.xpath.ui.config.XPathActivityConfigureAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

/**
 * This action is responsible for enabling the contextual menu entry
 * on processors that perform XPathActivity'ies.
 * 
 * NB! As a side-effect this also enables the pop-up with for configuration
 * of the processor when it is added to the workflow from the Service Panel. 
 * 
 * @author Sergejs Aleksejevs
 */
public class ConfigureXPathActivityMenuAction extends
    AbstractConfigureActivityMenuAction<XPathActivity>
{

  public ConfigureXPathActivityMenuAction() {
    super(XPathActivity.class);
  }
  
  @Override
  protected Action createAction()
  {
    XPathActivityConfigureAction configAction = new XPathActivityConfigureAction(
        findActivity(), getParentFrame());
    configAction.putValue(Action.NAME, "Configure XPath service");
    addMenuDots(configAction);
    return configAction;
  }

}
