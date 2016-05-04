/**
 * 
 */
package org.apache.taverna.activities.externaltool.servicedescriptions;

import java.net.URI;
import java.util.UUID;

import javax.swing.Icon;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.manager.InvocationGroupManager;
import org.apache.taverna.activities.externaltool.manager.impl.InvocationGroupManagerImpl;
import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.workflowmodel.processor.activity.Activity;

/**
 * @author alanrw
 *
 */
public class ExternalToolTemplateServiceDescription extends
		AbstractTemplateService<ExternalToolActivityConfigurationBean> {
	
	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/external-tool");
	
	private static final String EXTERNAL_TOOL = "Tool";
	
	private static InvocationGroupManager manager = InvocationGroupManagerImpl.getInstance();

	@Override
	public Class<? extends Activity<ExternalToolActivityConfigurationBean>> getActivityClass() {
		return ExternalToolActivity.class;
	}

	@Override
	public ExternalToolActivityConfigurationBean getActivityConfiguration() {
		ExternalToolActivityConfigurationBean result = new ExternalToolActivityConfigurationBean();
		result.setExternaltoolid(UUID.randomUUID().toString());
		result.setToolDescription(new UseCaseDescription(""));
		result.setMechanism(manager.getDefaultMechanism());
		return result;
	}

	@Override
	public Icon getIcon() {
		return ExternalToolActivityIcon.getExternalToolIcon();
	}
	
	@Override
	public String getDescription() {
		return "A service that allows tools to be used as services";	
	}
	
	@SuppressWarnings("unchecked")
	public static ServiceDescription getServiceDescription() {
		ExternalToolTemplateServiceDescription bts = new ExternalToolTemplateServiceDescription();
		return bts.templateService;
	}



	@Override
	public String getId() {
		return providerId.toString();
	}

	@Override
	public String getName() {
		return EXTERNAL_TOOL;
	}

}
