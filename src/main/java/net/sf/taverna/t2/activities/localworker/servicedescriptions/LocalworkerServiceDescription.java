package net.sf.taverna.t2.activities.localworker.servicedescriptions;

import static org.apache.log4j.Logger.getLogger;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.help.BadIDException;
import javax.swing.Icon;

import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivityConfigurationBean;
import net.sf.taverna.t2.lang.beans.PropertyAnnotation;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workbench.helper.HelpCollator;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

import org.apache.log4j.Logger;

public class LocalworkerServiceDescription extends ServiceDescription<BeanshellActivityConfigurationBean>{
	
	private final Logger logger = getLogger(LocalworkerServiceDescription.class);

	private static final String LOCALWORKER = ServiceDescription.LOCAL_SERVICES;

	private String script;
	private List<ActivityOutputPortDefinitionBean> outputPorts;
	private List<ActivityInputPortDefinitionBean> inputPorts;
	private String operation;
	private String category;
	private String provider;
	
	private String localworkerName;
	public String getLocalworkerName() {
		return localworkerName;
	}

	public void setLocalworkerName(String localworkerName) {
		this.localworkerName = localworkerName;
	}

	private List<String> dependencies; // from old code - this property is not in use any more
	private LinkedHashSet<BasicArtifact> artifactDependencies;
	// Note that localworkers have no local dependencies, only artifatct ones

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return "Localworker";
	}

	@Override
	public Class<? extends Activity<BeanshellActivityConfigurationBean>> getActivityClass() {
		return LocalworkerActivity.class;
	}

	@Override
	public BeanshellActivityConfigurationBean getActivityConfiguration() {
		// TODO Auto-generated method stub
		// different bean for each type of localworker, get xml version of bean
		// and create BeanshellConfig
		LocalworkerActivityConfigurationBean bean = new LocalworkerActivityConfigurationBean();
				
		bean.setScript(this.script);
		bean.setInputPortDefinitions(this.inputPorts);
		bean.setOutputPortDefinitions(this.outputPorts);
		bean.setDependencies(this.dependencies); // this field is deprecated (replaced by artifactDependencies), but for backward compatibility we leave it here
		// Local worker only has artifact dependencies
		bean.setArtifactDependencies(artifactDependencies);
		bean.setLocalworkerName(getLocalworkerName());
		
		// FIXME needs some mime types from the annotations (done as strings
		// inside the port at the moment)
		return bean;
	}

	@Override
	public Icon getIcon() {
		return LocalworkerActivityIcon.getLocalworkerIcon();
	}

	@Override
	public String getName() {
		if (operation == null) {
			logger.error("Service description has no name");
		}
		return operation;
	}

	@Override
	public List<? extends Comparable> getPath() {
		List<String> result =
		Arrays.asList (LOCALWORKER, category);
		return result;
	}

	
	@PropertyAnnotation(hidden=true)
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setOutputPorts(
			List<ActivityOutputPortDefinitionBean> outputPortBeans) {
		this.outputPorts = outputPortBeans;
	}

	public List<ActivityOutputPortDefinitionBean> getOutputPorts() {
		return outputPorts;
	}

	public void setInputPorts(
			List<ActivityInputPortDefinitionBean> inputPortBeans) {
		this.inputPorts = inputPortBeans;
	}

	public List<ActivityInputPortDefinitionBean> getInputPorts() {
		return inputPorts;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		if (operation == null) {
			logger.error("Operation set to null");
		}
		this.operation = operation;
	}
	///////////// From old code //////////////////
	/**
	 * Returns the dependencies.
	 *
	 * @return the dependencies
	 */
	public List<String> getDependencies() {
		return dependencies;
	}

	/**
	 * Sets the dependencies.
	 *
	 * @param dependencies the new dependencies
	 */
	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}
	///////////// From old code /////////////////
	/**
	 * @return the artifactDependencies
	 */
	public LinkedHashSet<BasicArtifact> getArtifactDependencies() {
		return artifactDependencies;
	}
	
	/**
	 * @param artifactDependencies the artifactDependencies to set
	 */
	public void setArtifactDependencies(LinkedHashSet<BasicArtifact> artifactDependencies) {
		this.artifactDependencies = artifactDependencies;
	}

	@Override
	protected List<Object> getIdentifyingData() {
		return Arrays.<Object>asList(getScript());
	}
	
	public String getHelpId() {
		return (super.getHelpId() + "-" + getLocalworkerName());
	}

}
