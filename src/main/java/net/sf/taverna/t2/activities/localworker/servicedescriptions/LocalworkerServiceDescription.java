package net.sf.taverna.t2.activities.localworker.servicedescriptions;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.query.LocalworkerActivityItem;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

public class LocalworkerServiceDescription extends ServiceDescription<BeanshellActivityConfigurationBean>{
	
	private static final String LOCALWORKER = "Local services";

	private String script;
	private List<ActivityOutputPortDefinitionBean> outputPorts;
	private List<ActivityInputPortDefinitionBean> inputPorts;
	private String operation;
	private String category;
	private String provider;
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
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
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
		
		// FIXME needs some mime types from the annotations (done as strings
		// inside the port at the moment)
		return bean;
	}

	@Override
	public Icon getIcon() {
		return new ImageIcon(LocalworkerActivityItem.class
				.getResource("/localworker.png"));
	}

	@Override
	public String getName() {
		return operation;
	}

	@Override
	public List<? extends Comparable> getPath() {
		List<String> result =
		Arrays.asList (LOCALWORKER, category);
		return result;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTemplateService() {
		// TODO Auto-generated method stub
		return false;
	}

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
	public String toString() {
		return operation;
	}	
}
