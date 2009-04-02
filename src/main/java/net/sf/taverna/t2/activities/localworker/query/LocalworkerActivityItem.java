/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.activities.localworker.query;


import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.taverna.raven.repository.BasicArtifact;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.activities.beanshell.BeanshellActivityConfigurationBean;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivity;
import net.sf.taverna.t2.activities.localworker.LocalworkerActivityConfigurationBean;
import net.sf.taverna.t2.partition.AbstractActivityItem;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityOutputPortDefinitionBean;

/**
 * Represents a Localworker activity on the Activity Tree/Partition which can be
 * dragged. It creates a Local Worker Activity through a
 * {@link BeanshellActivityConfigurationBean} populated with the script and
 * ports
 * 
 * @author Ian Dunlop
 * 
 */
public class LocalworkerActivityItem extends AbstractActivityItem {

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

	/**
	 * Returns a {@link LocalworkerActivityConfigurationBean} which represents
	 * this local worker
	 */
	@Override
	public Object getConfigBean() {
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

	/**
	 * Returns a {@link BeanshellActivity} which represents this local worker
	 */
	@Override
	public Activity<?> getUnconfiguredActivity() {
		Activity<?> activity = new LocalworkerActivity();
		return activity;
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
