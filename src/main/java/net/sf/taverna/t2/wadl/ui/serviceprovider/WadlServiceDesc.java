package net.sf.taverna.t2.wadl.ui.serviceprovider;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;
import net.sf.taverna.t2.activities.rest.ui.servicedescription.RESTActivityIcon;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

import org.apache.log4j.Logger;

public class WadlServiceDesc extends ServiceDescription<RESTActivityConfigurationBean> {
	
	private static Logger logger = Logger.getLogger(WadlServiceDesc.class);
	
	private RESTActivityConfigurationBean configBean;

	private String uri;

	private List<String> pathSegments;

	public WadlServiceDesc(String uri, List<String> pathSegments, RESTActivityConfigurationBean configBean) {
		this.uri = uri;
		this.pathSegments = pathSegments;
		this.configBean = configBean;
	}
	
	/**
	 * The subclass of Activity which should be instantiated when adding a service
	 * for this description 
	 */
	@Override
	public Class<? extends Activity<RESTActivityConfigurationBean>> getActivityClass() {
		return RESTActivity.class;
	}

	/**
	 * The configuration bean which is to be used for configuring the instantiated activity.
	 * Making this bean will typically require some of the fields set on this service
	 * description, like an endpoint URL or method name. 
	 * 
	 */
	@Override
	public RESTActivityConfigurationBean getActivityConfiguration() {
		RESTActivityConfigurationBean result = new RESTActivityConfigurationBean();
		result.setHttpMethod(configBean.getHttpMethod());
		result.setAcceptsHeaderValue(configBean.getAcceptsHeaderValue());
		result.setContentTypeForUpdates(configBean.getContentTypeForUpdates());
		result.setUrlSignature(configBean.getUrlSignature());
		result.setOutgoingDataFormat(configBean.getOutgoingDataFormat());
		result.setSendHTTPExpectRequestHeader(configBean.getSendHTTPExpectRequestHeader());
		result.setShowRedirectionOutputPort(configBean.getShowRedirectionOutputPort());
		result.setShowActualUrlPort(configBean.getShowActualUrlPort());
		result.setShowResponseHeadersPort(configBean.getShowResponseHeadersPort());
		result.setEscapeParameters(configBean.getEscapeParameters());
		result.setOtherHTTPHeaders(new ArrayList<ArrayList<String>>(configBean.getOtherHTTPHeaders()));
		return result;
	}

	/**
	 * An icon to represent this service description in the service palette.
	 */
	@Override
	public Icon getIcon() {
		return RESTActivityIcon.getRESTActivityIcon();
	}

	/**
	 * The display name that will be shown in service palette and will
	 * be used as a template for processor name when added to workflow.
	 */
	@Override
	public String getName() {
		return configBean.getHttpMethod().toString() + " " + configBean.getUrlSignature();
	}

	/**
	 * The path to this service description in the service palette. Folders
	 * will be created for each element of the returned path.
	 */
	@Override
	public List<String> getPath() {
		// For deeper paths you may return several strings
		ArrayList<String> result = new ArrayList<String>();
		result.add("WADL: " + this.getUri().toString());
		result.addAll(pathSegments);
		return result;
	}

	/**
	 * Return a list of data values uniquely identifying this service
	 * description (to avoid duplicates). Include only primary key like fields,
	 * ie. ignore descriptions, icons, etc.
	 */
	@Override
	protected List<? extends Object> getIdentifyingData() {
		return Arrays.<Object>asList(this.getUri(), configBean.getHttpMethod(), configBean.getUrlSignature());
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
}
