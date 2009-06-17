package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public class SoaplabServiceDescription extends
		ServiceDescription<SoaplabActivityConfigurationBean> {

	private final static String SOAPLAB = "Soaplab @ ";

	private String category;
	private String operation;
	private URI endpoint;
	private List<String> types;

	private String name;

	public List<String> getTypes() {
		return types;
	}
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category
	 *            the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation
	 *            the operation to set
	 */
	public void setOperation(final String operation) {
		this.operation = operation;
		
		String name = operation;
		int finalColon = operation.lastIndexOf(":");
		if (finalColon != -1) {
			name = operation.substring(finalColon + 1);
		}
		int finalDot = operation.lastIndexOf(".");
		if (finalDot != -1) {
			name = operation.substring(finalDot + 1);
		}
		setName(name);
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public Class<? extends Activity<SoaplabActivityConfigurationBean>> getActivityClass() {
		return SoaplabActivity.class;
	}

	@Override
	public SoaplabActivityConfigurationBean getActivityConfiguration() {
		SoaplabActivityConfigurationBean bean = new SoaplabActivityConfigurationBean();
		bean.setEndpoint(getEndpoint().toASCIIString() + getOperation());
		return bean;
	}

	@Override
	public Icon getIcon() {
		return SoaplabActivityIcon.getSoaplabIcon();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getPath() {
		List<String> path = new ArrayList<String>();
		path.add(SOAPLAB + getEndpoint());
		path.add(getCategory());
		// Don't use getTypes() - as we end up
		// with double entries.. 
		return path;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public void setEndpoint(URI endpoint) {
		this.endpoint = endpoint;
	}

	public URI getEndpoint() {
		return endpoint;
	}

	@Override
	protected List<Object> getIdentifyingData() {
		return Arrays.<Object>asList(getEndpoint(), getOperation());
	}

}
