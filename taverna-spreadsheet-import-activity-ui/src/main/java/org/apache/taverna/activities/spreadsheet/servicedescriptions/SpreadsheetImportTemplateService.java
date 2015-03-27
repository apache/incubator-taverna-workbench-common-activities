/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.spreadsheet.servicedescriptions;

import java.net.URI;

import javax.swing.Icon;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.taverna.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import org.apache.taverna.servicedescriptions.AbstractTemplateService;
import org.apache.taverna.servicedescriptions.ServiceDescription;
import org.apache.taverna.servicedescriptions.ServiceDescriptionProvider;
import org.apache.taverna.scufl2.api.configurations.Configuration;

/**
 * Definition of the SpreadsheetImport spreadsheet import template service.
 *
 * @author David Withers
 */
public class SpreadsheetImportTemplateService extends AbstractTemplateService {

	public static final URI ACTIVITY_TYPE = URI
			.create("http://ns.taverna.org.uk/2010/activity/spreadsheet-import");

	private static final String SERVICE_NAME = SpreadsheetImportUIText
			.getString("SpreadsheetImportTemplateService.serviceName");

	private static final URI providerId = URI
			.create("http://taverna.sf.net/2010/service-provider/spreadsheet");

	public String getName() {
		return SERVICE_NAME;
	}

	@Override
	public URI getActivityType() {
		return ACTIVITY_TYPE;
	}

	@Override
	public Configuration getActivityConfiguration() {
		Configuration configuration = new Configuration();
		configuration.setType(ACTIVITY_TYPE.resolve("#Config"));
		ObjectNode json = (ObjectNode) configuration.getJson();
		json.put("columnRange", json.objectNode().put("start", 0).put("end", 1));
		json.put("rowRange", json.objectNode().put("start", 0).put("end", -1));
		json.put("emptyCellValue", "");
		json.put("allRows", true);
		json.put("excludeFirstRow", false);
		json.put("ignoreBlankRows", false);
		json.put("emptyCellPolicy", "EMPTY_STRING");
		json.put("outputFormat", "PORT_PER_COLUMN");
		json.put("csvDelimiter", ",");
		return configuration;
	}

	@Override
	public Icon getIcon() {
		return SpreadsheetImportActivityIcon.getSpreadsheetImportIcon();
	}

	@Override
	public String getDescription() {
		return SpreadsheetImportUIText
				.getString("SpreadsheetImportTemplateService.serviceDescription");
	}

	public static ServiceDescription getServiceDescription() {
		SpreadsheetImportTemplateService bts = new SpreadsheetImportTemplateService();
		return bts.templateService;
	}

	public String getId() {
		return providerId.toString();
	}

    @Override
    public ServiceDescriptionProvider newInstance() {
        return new SpreadsheetImportTemplateService();
    }
}
