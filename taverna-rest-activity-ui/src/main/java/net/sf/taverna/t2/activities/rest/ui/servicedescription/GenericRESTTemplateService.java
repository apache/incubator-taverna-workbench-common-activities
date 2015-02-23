package net.sf.taverna.t2.activities.rest.ui.servicedescription;

import java.net.URI;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import uk.org.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Sergejs Aleksejevs
 * @author David Withers
 */
public class GenericRESTTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI.create("http://ns.taverna.org.uk/2010/activity/rest");

	private static final String REST = "REST";

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = (ObjectNode) configuration.getJson();
		ObjectNode requestNode = json.objectNode();

		requestNode.put("httpMethod", RESTActivity.HTTP_METHOD.GET.name());
		requestNode.put("absoluteURITemplate", "http://www.uniprot.org/uniprot/{id}.xml");

		ArrayNode headersNode = requestNode.arrayNode();
		headersNode.addObject().put("header", "Accept").put("value", "application/xml");
		headersNode.addObject().put("header", "Content-Type").put("value", "application/xml");

		requestNode.set("headers", headersNode);
		json.set("request", requestNode);
		json.put("outgoingDataFormat", RESTActivity.DATA_FORMAT.String.name());
		json.put("showRedirectionOutputPort", false);
		json.put("showActualURLPort", false);
		json.put("showResponseHeadersPort", false);
		json.put("escapeParameters", true);
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return RESTActivityIcon.getRESTActivityIcon();
	}

	public String getName() {
		return REST;
	}

	public String getDescription() {
		return "A generic REST service that can handle all HTTP methods";
	}

	public static ServiceDescription getServiceDescription() {
		GenericRESTTemplateService gts = new GenericRESTTemplateService();
		return gts.templateService;
	}

	public String getId() {
		return "http://www.taverna.org.uk/2010/services/rest";
	}

}
