/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.configuration;

import java.util.HashMap;
import java.util.Map;

import net.sf.taverna.t2.workbench.configuration.AbstractConfigurable;

/**
 * @author alanrw
 *
 */
public class ToolInvocationConfiguration extends AbstractConfigurable {
	
	private static ToolInvocationConfiguration instance;
	
	private Map<String, String> defaultPropertyMap;
	
	public static ToolInvocationConfiguration getInstance() {
		if (instance == null) {
			instance = new ToolInvocationConfiguration();
		}
		return instance;		
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.Configurable#getCategory()
	 */
	@Override
	public String getCategory() {
		return "general";
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.Configurable#getDefaultPropertyMap()
	 */
	@Override
	public Map<String, String> getDefaultPropertyMap() {
		if (defaultPropertyMap == null) {
			defaultPropertyMap = new HashMap<String, String>();
		}
		return defaultPropertyMap;
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.Configurable#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Tool invocation";
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.Configurable#getFilePrefix()
	 */
	@Override
	public String getFilePrefix() {
		return "ToolInvocation";
	}

	/* (non-Javadoc)
	 * @see net.sf.taverna.t2.workbench.configuration.Configurable#getUUID()
	 */
	@Override
	public String getUUID() {
		return "B611F5C2-EB49-479E-B01A-7F3F56E6918A";
	}

}
