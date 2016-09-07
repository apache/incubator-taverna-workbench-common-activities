/**
 *
 */
package org.apache.taverna.activities.externaltool.configuration;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
