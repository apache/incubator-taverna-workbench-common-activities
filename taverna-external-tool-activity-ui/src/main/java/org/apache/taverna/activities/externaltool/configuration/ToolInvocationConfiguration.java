/**
 *
 */
package org.apache.taverna.activities.externaltool.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.configuration.AbstractConfigurable;


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

	@Override
	public String getCategory() {
		return "general";
	}

	@Override
	public Map<String, String> getDefaultPropertyMap() {
		if (defaultPropertyMap == null) {
			defaultPropertyMap = new HashMap<String, String>();
		}
		return defaultPropertyMap;
	}

	@Override
	public String getDisplayName() {
		return "Tool invocation";
	}

	@Override
	public String getFilePrefix() {
		return "ToolInvocation";
	}

	@Override
	public String getUUID() {
		return "B611F5C2-EB49-479E-B01A-7F3F56E6918A";
	}

}
