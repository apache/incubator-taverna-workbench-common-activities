package net.sf.taverna.t2.activities.xpath.ui.servicedescription;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.activities.xpath.XPathActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathTemplateService extends AbstractTemplateService<XPathActivityConfigurationBean>
{
  public XPathTemplateService ()
  {
    super();
    /*
      // TODO - re-enable this if it is necessary to add another folder inside "Service templates" in the Service Panel
      templateService = new AbstractTemplateService.TemplateServiceDescription() {
        public List<String> getPath() {
            return Arrays.asList(SERVICE_TEMPLATES, "XPath");
        }
      };
    */
  }
  
  @Override
  public Class<XPathActivity> getActivityClass() {
    return XPathActivity.class;
  }
  
  @Override
  /**
   * Default values for this template service are provided in this method.
   */
  public XPathActivityConfigurationBean getActivityConfiguration()
  {
    return (XPathActivityConfigurationBean.getDefaultInstance());
  }
  
  @Override
  public Icon getIcon() {
    return XPathActivityIcon.getXPathActivityIcon();
  }
  
  public String getName() {
    return "XPath Service";
  }
  
  public String getDescription() {
    return "Service for point-and-click creation of XPath expressions for XML data";
  }
  
	@SuppressWarnings("unchecked")
	public static ServiceDescription getServiceDescription() {
		XPathTemplateService gts = new XPathTemplateService();
		return gts.templateService;
	}


  public String getId() {
    return "http://www.taverna.org.uk/2010/services/xpath";
  }
  
}
