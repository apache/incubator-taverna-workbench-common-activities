package net.sf.taverna.t2.activities.xpath.ui.servicedescription;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconSPI;
import net.sf.taverna.t2.workbench.ui.impl.configuration.colour.ColourManager;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityIcon implements ActivityIconSPI
{
  private static final Color PROCESSOR_COLOUR = Color.decode("#E6FF5E");
  
  
  // --- LOCATIONS OF ICONS USED IN THE XPath ACTIVITY ---
  
  private static final String FAMFAMFAM_SILK_PATH = "famfamfam_silk/";
  private static final String FOLDS_PATH = "folds/";
  
  public static final String XPATH_ACTIVITY_ICON = FAMFAMFAM_SILK_PATH + "page_white_code.png";
  public static final String XPATH_ACTIVITY_CONFIGURATION_PARSE_XML_ICON = "arrow_right.png";
  
  public static final String XML_TREE_ROOT_ICON = FAMFAMFAM_SILK_PATH + "page_white_code.png";
  public static final String XML_TREE_NODE_ICON = FAMFAMFAM_SILK_PATH + "tag.png";
  public static final String XML_TREE_ATTRIBUTE_ICON = "xpath_attribute.png";
  
  public static final String XML_TREE_EXPAND_ALL_ICON = FAMFAMFAM_SILK_PATH + "text_linespacing.png";
  public static final String XML_TREE_COLLAPSE_ALL_ICON = FAMFAMFAM_SILK_PATH + "text_linespacing (collapse).png";
  
  public static final String XPATH_STATUS_OK_ICON = FAMFAMFAM_SILK_PATH + "accept.png";
  public static final String XPATH_STATUS_ERROR_ICON = FAMFAMFAM_SILK_PATH + "exclamation.png";
  public static final String XPATH_STATUS_UNKNOWN_ICON = FAMFAMFAM_SILK_PATH + "help.png";
  
  public static final String FOLD_ICON = FOLDS_PATH + "fold.png";
  public static final String UNFOLD_ICON = FOLDS_PATH + "unfold.png";
  
  // ------
  
  private static ImageIcon icon;
  
  
  static {
    // set colour for XPath processors in the workflow diagram
    ColourManager.getInstance().setPreferredColour(
        XPathActivity.class.getCanonicalName(), PROCESSOR_COLOUR);
  }
  
  public int canProvideIconScore(Activity<?> activity)
  {
    if (activity.getClass().getName().equals(XPathActivity.class.getName()))
      return DEFAULT_ICON + 1;
    else
      return NO_ICON;
  }

  public Icon getIcon(Activity<?> activity) {
    return (getXPathActivityIcon());
  }

  public static Icon getXPathActivityIcon() {
    if (icon == null) {
      synchronized(XPathActivityIcon.class) {
        if (icon == null) {
          try {
            icon = new ImageIcon(XPathActivityIcon.class.getResource(XPATH_ACTIVITY_ICON));
          }
          catch (NullPointerException e) {
            /* icon wasn't found - do nothing, but no icon will be available */
          }
        }
      }
    }
    return (icon);
  }
  
  
  public static Icon getIconById(String iconID) {
    try {
      return (new ImageIcon(XPathActivityIcon.class.getResource(iconID)));
    }
    catch (NullPointerException e) {
      // requested icon wasn't found - just return null
      return (null);
    }
  }

}
