package org.apache.taverna.activities.xpath.ui.servicedescription;

import java.net.URI;

import javax.swing.Icon;

import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class XPathTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/xpath");

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = (ObjectNode) configuration.getJson();
		json.put("xpathExpression", "/");
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return XPathActivityIcon.getXPathActivityIcon();
	}

	public String getName() {
		return "XPath";
	}

	public String getDescription() {
		return "Service for point-and-click creation of XPath expressions for XML data";
	}

	public static ServiceDescription getServiceDescription() {
		XPathTemplateService gts = new XPathTemplateService();
		return gts.templateService;
	}

	public String getId() {
		return "http://www.taverna.org.uk/2010/services/xpath";
	}

    @Override
    public XPathTemplateService newInstance() {
        return new XPathTemplateService();
    }

}
