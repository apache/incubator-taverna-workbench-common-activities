/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.servicedescriptions;

import java.net.URI;
import java.util.UUID;

import javax.swing.Icon;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.manager.InvocationGroupManager;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;

/**
 * @author alanrw
 *
 */
public class ExternalToolTemplateServiceDescription extends
		AbstractTemplateService<ExternalToolActivityConfigurationBean> {
	
	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/external-tool");
	
	private static final String EXTERNAL_TOOL = "Tool";
	
	private static InvocationGroupManager manager = InvocationGroupManager.getInstance();

	@Override
	public Class<? extends Activity<ExternalToolActivityConfigurationBean>> getActivityClass() {
		return ExternalToolActivity.class;
	}

	@Override
	public ExternalToolActivityConfigurationBean getActivityConfiguration() {
		ExternalToolActivityConfigurationBean result = new ExternalToolActivityConfigurationBean();
		result.setExternaltoolid(UUID.randomUUID().toString());
		result.setUseCaseDescription(new UseCaseDescription(""));
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
