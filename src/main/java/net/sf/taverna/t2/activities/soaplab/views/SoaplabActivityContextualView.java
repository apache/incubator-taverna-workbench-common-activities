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
package net.sf.taverna.t2.activities.soaplab.views;

import java.awt.Frame;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.swing.Action;
import javax.xml.namespace.QName;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.taverna.t2.activities.soaplab.actions.SoaplabActivityConfigurationAction;
import net.sf.taverna.t2.servicedescriptions.ServiceDescriptionRegistry;
import net.sf.taverna.t2.workbench.activityicons.ActivityIconManager;
import net.sf.taverna.t2.workbench.configuration.colour.ColourManager;
import net.sf.taverna.t2.workbench.edits.EditManager;
import net.sf.taverna.t2.workbench.file.FileManager;
import net.sf.taverna.t2.workbench.ui.actions.activity.HTMLBasedActivityContextualView;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.log4j.Logger;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;

import com.fasterxml.jackson.databind.JsonNode;

public class SoaplabActivityContextualView extends HTMLBasedActivityContextualView {

	private static Logger logger = Logger.getLogger(SoaplabActivityContextualView.class);

	private static final long serialVersionUID = -6470801873448104509L;

	private final EditManager editManager;

	private final FileManager fileManager;

	private final ActivityIconManager activityIconManager;

	private final ServiceDescriptionRegistry serviceDescriptionRegistry;

	public SoaplabActivityContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ActivityIconManager activityIconManager,
			ColourManager colourManager, ServiceDescriptionRegistry serviceDescriptionRegistry) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
	}

	@Override
	public String getViewTitle() {
		return "Soaplab service";
	}

	@Override
	protected String getRawTableRowsHtml() {
		Configuration configuration = getConfigBean();
		JsonNode json = configuration.getJson();
		String html = "<tr><td>Endpoint</td><td>" + json.get("endpoint").textValue() + "</td></tr>";
		html += "<tr><td>Polling interval</td><td>" + json.get("pollingInterval").asText()
				+ "</td></tr>";
		html += "<tr><td>Polling backoff</td><td>" + json.get("pollingBackoff").asText()
				+ "</td></tr>";
		html += "<tr><td>Polling interval max</td><td>" + json.get("pollingIntervalMax").asText()
				+ "</td></tr>";
		// html += "<tr><td>SOAPLAB Metadata</td><td>" + getMetadata()
		// + "</td></tr>";
		return html;
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new SoaplabActivityConfigurationAction(getActivity(), owner, editManager,
				fileManager, activityIconManager, serviceDescriptionRegistry);
	}

	private String getMetadata() {
		try {
			Configuration configuration = getConfigBean();
			JsonNode json = configuration.getJson();
			String endpoint = json.get("endpoint").textValue();
			Call call = (Call) new Service().createCall();
			call.setTimeout(new Integer(0));
			call.setTargetEndpointAddress(endpoint);
			call.setOperationName(new QName("describe"));
			String metadata = (String) call.invoke(new Object[0]);
			logger.info(metadata);
			// Old impl, returns a tree of the XML
			// ColXMLTree tree = new ColXMLTree(metadata);
			URL sheetURL = SoaplabActivityContextualView.class
					.getResource("/analysis_metadata_2_html.xsl");
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			logger.info(sheetURL.toString());
			Templates stylesheet = transformerFactory.newTemplates(new StreamSource(sheetURL
					.openStream()));
			Transformer transformer = stylesheet.newTransformer();
			StreamSource inputStream = new StreamSource(new ByteArrayInputStream(
					metadata.getBytes()));
			ByteArrayOutputStream transformedStream = new ByteArrayOutputStream();
			StreamResult result = new StreamResult(transformedStream);
			transformer.transform(inputStream, result);
			transformedStream.flush();
			transformedStream.close();
			// String summaryText = "<html><head>"
			// + WorkflowSummaryAsHTML.STYLE_NOBG + "</head>"
			// + transformedStream.toString() + "</html>";
			// JEditorPane metadataPane = new ColJEditorPane("text/html",
			// summaryText);
			// metadataPane.setText(transformedStream.toString());
			// // logger.info(transformedStream.toString());
			// JScrollPane jsp = new JScrollPane(metadataPane,
			// JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			// JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			// jsp.setPreferredSize(new Dimension(0, 0));
			// jsp.getVerticalScrollBar().setValue(0);
			return transformedStream.toString();
		} catch (Exception ex) {
			return "<font color=\"red\">Error</font><p>An exception occured while trying to fetch Soaplab metadata from the server. The error was :<pre>"
					+ ex.getMessage() + "</pre>";

		}
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
