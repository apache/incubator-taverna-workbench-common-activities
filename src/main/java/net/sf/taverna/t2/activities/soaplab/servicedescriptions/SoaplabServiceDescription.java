package net.sf.taverna.t2.activities.soaplab.servicedescriptions;

import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;
import net.sf.taverna.t2.activities.soaplab.query.SoaplabActivityItem;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

public class SoaplabServiceDescription extends ServiceDescription<SoaplabActivityConfigurationBean>{
	
	private final static String SOAPLAB = "Soaplab @ ";

	private String category;
	private String operation;
	private String url;
	
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
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
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<? extends Activity<SoaplabActivityConfigurationBean>> getActivityClass() {
		return SoaplabActivity.class;
	}

	@Override
	public SoaplabActivityConfigurationBean getActivityConfiguration() {
		SoaplabActivityConfigurationBean bean = new SoaplabActivityConfigurationBean();
		bean.setEndpoint(getUrl() + getOperation());
		return bean;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(SoaplabActivityItem.class
				.getResource("/soaplab.png"));
	}

	@Override
	public String getName() {
		return getOperation();
	}

	@Override
	public List<? extends Comparable> getPath() {
		List<String> result;
		result = Arrays.asList(SOAPLAB + getUrl(), getCategory());
		return result;
	}

	@Override
	public int hashCode() {
		return getPath().hashCode();
	}

	@Override
	public boolean isTemplateService() {
		// TODO Auto-generated method stub
		return false;
	}

}
