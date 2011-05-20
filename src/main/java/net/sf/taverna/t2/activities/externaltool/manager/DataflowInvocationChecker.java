/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.manager;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivity;
import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.activities.externaltool.views.ExternalToolConfigView;
import net.sf.taverna.t2.workflowmodel.Dataflow;
import net.sf.taverna.t2.workflowmodel.Processor;
import net.sf.taverna.t2.workflowmodel.processor.activity.Activity;
import net.sf.taverna.t2.workflowmodel.processor.activity.NestedDataflow;

/**
 * @author alanrw
 *
 */
public class DataflowInvocationChecker {
	
	private HashMap<String, InvocationGroup> unknownGroups = new HashMap<String, InvocationGroup>();
	private HashMap<String, InvocationGroup> overloadedGroups = new HashMap<String, InvocationGroup>();

	private HashMap<String, InvocationMechanism> unknownMechanisms = new HashMap<String, InvocationMechanism>();
	private HashMap<String, InvocationMechanism> overloadedMechanisms = new HashMap<String, InvocationMechanism>();
	
	private HashSet<String> consideredGroupNames = new HashSet<String>();
	private HashSet<String> consideredMechanismNames = new HashSet<String>();
	
	private InvocationGroupManager manager = InvocationGroupManager.getInstance();
	
	private static Logger logger = Logger
	.getLogger(DataflowInvocationChecker.class);


	public DataflowInvocationChecker(Dataflow d) {
		checkInvocationsInDataflow(d);
	}

	private void checkInvocationsInDataflow(Dataflow d) {
		for (Processor p : d.getProcessors()) {
			for (Activity<?> a : p.getActivityList()) {
				if (a instanceof ExternalToolActivity) {
					checkExternalToolActivityConfigurationBean((ExternalToolActivityConfigurationBean) a.getConfiguration());
				} else if (a instanceof NestedDataflow) {
					checkInvocationsInDataflow(((NestedDataflow) a).getNestedDataflow());
				}
			}
		}
	}

	private void checkExternalToolActivityConfigurationBean(ExternalToolActivityConfigurationBean configuration) {
		InvocationGroup invocationGroup = configuration.getInvocationGroup();
		if (invocationGroup != null) {
			checkInvocationGroup(invocationGroup);
			invocationGroup.convertDetailsToMechanism();
			checkInvocationMechanism(invocationGroup.getMechanism());
		} else {
			configuration.convertDetailsToMechanism();
			InvocationMechanism invocationMechanism = configuration.getMechanism();
			checkInvocationMechanism(invocationMechanism);

		}
	}
	
	private void checkInvocationGroup(InvocationGroup invocationGroup) {
		String invocationGroupName = invocationGroup.getInvocationGroupName();
		if (!consideredGroupNames.contains(invocationGroupName)) {
			InvocationGroup existingGroup = manager.getInvocationGroup(invocationGroup.getInvocationGroupName());
			if (existingGroup == null) {
				logger.info("Unknown group " + invocationGroupName);
				unknownGroups.put(invocationGroupName, invocationGroup);
			} else if (!existingGroup.equals(invocationGroup)) {
				logger.info("Overloaded group " + invocationGroupName);
				overloadedGroups.put(invocationGroupName, invocationGroup);
			}
			consideredGroupNames.add(invocationGroupName);		
		}
	}
	
	private void checkInvocationMechanism(InvocationMechanism invocationMechanism) {
		String invocationMechanismName = invocationMechanism.getName();
		if (!consideredMechanismNames.contains(invocationMechanismName)) {
			InvocationMechanism existingMechanism = manager.getInvocationMechanism(invocationMechanismName);
			if (existingMechanism == null) {
				logger.info("Unknown mechanism " + invocationMechanismName);
				unknownMechanisms.put(invocationMechanismName, invocationMechanism);
			} else if (!existingMechanism.equals(invocationMechanism)) {
				logger.info("Overloaded mechanism " + invocationMechanismName);
				overloadedMechanisms.put(invocationMechanismName, invocationMechanism);
			}
			consideredMechanismNames.add(invocationMechanismName);			
		}		
	}

}
