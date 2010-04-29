/*******************************************************************************
 * Copyright (C) 2009 The University of Manchester   
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
package net.sf.taverna.t2.activities.spreadsheet.servicedescriptions;

import java.net.URI;

import javax.swing.Icon;

import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportConfiguration;
import net.sf.taverna.t2.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import net.sf.taverna.t2.servicedescriptions.AbstractTemplateService;
import net.sf.taverna.t2.servicedescriptions.ServiceDescription;

/**
 * Definition of the SpreadsheetImport spreadsheet import template service.
 * 
 * @author David Withers
 */
public class SpreadsheetImportTemplateService extends
		AbstractTemplateService<SpreadsheetImportConfiguration> {
	
	private static final String SERVICE_NAME = SpreadsheetImportUIText
			.getString("SpreadsheetImportTemplateService.serviceName");

	private static final URI providerId = URI
	.create("http://taverna.sf.net/2010/service-provider/spreadsheet");
	
	public String getName() {
		return SERVICE_NAME;
	}

	@Override
	public Class<SpreadsheetImportActivity> getActivityClass() {
		return SpreadsheetImportActivity.class;
	}

	@Override
	public SpreadsheetImportConfiguration getActivityConfiguration() {
		return new SpreadsheetImportConfiguration();
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

	public static ServiceDescription<SpreadsheetImportConfiguration> getServiceDescription() {
		SpreadsheetImportTemplateService bts = new SpreadsheetImportTemplateService();
		return bts.templateService;
	}

	public String getId() {
		return providerId.toString();
	}
}
