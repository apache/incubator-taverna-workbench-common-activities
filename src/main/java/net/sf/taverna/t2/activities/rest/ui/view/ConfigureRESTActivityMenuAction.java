package net.sf.taverna.t2.activities.rest.ui.view;

import javax.swing.Action;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.ui.config.RESTActivityConfigureAction;
import net.sf.taverna.t2.workbench.activitytools.AbstractConfigureActivityMenuAction;

/**
 * This action is responsible for enabling the contextual menu entry
 * on processors that perform RESTActivity'ies.
 * 
 * NB! As a side-effect this also enables the pop-up with for configuration
 * of the processor when it is added to the workflow from the Service Panel. 
 * 
 * @author Sergejs Aleksejevs
 */
public class ConfigureRESTActivityMenuAction extends
    AbstractConfigureActivityMenuAction<RESTActivity>
{

  public ConfigureRESTActivityMenuAction() {
    super(RESTActivity.class);
  }
  
  @Override
  protected Action createAction()
  {
    RESTActivityConfigureAction configAction = new RESTActivityConfigureAction(
        findActivity(), getParentFrame());
    configAction.putValue(Action.NAME, "Configure REST service");
    addMenuDots(configAction);
    return configAction;
  }

}
