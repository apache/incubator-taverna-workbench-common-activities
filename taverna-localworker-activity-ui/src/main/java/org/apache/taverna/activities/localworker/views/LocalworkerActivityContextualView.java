package org.apache.taverna.activities.localworker.views;

import java.awt.Frame;

import javax.swing.Action;

import org.apache.taverna.activities.localworker.actions.LocalworkerActivityConfigurationAction;
import org.apache.taverna.activities.localworker.servicedescriptions.LocalworkerServiceProvider;
import org.apache.taverna.servicedescriptions.ServiceDescriptionRegistry;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.configuration.app.ApplicationConfiguration;
import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.port.InputActivityPort;
import org.apache.taverna.scufl2.api.port.OutputActivityPort;

import com.fasterxml.jackson.databind.JsonNode;

@SuppressWarnings("serial")
public class LocalworkerActivityContextualView extends HTMLBasedActivityContextualView {

	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;
	private final ServiceDescriptionRegistry serviceDescriptionRegistry;

	private final ApplicationConfiguration applicationConfiguration;

	public LocalworkerActivityContextualView(Activity activity, EditManager editManager,
			FileManager fileManager, ColourManager colourManager,
			ActivityIconManager activityIconManager,
			ServiceDescriptionRegistry serviceDescriptionRegistry,
			ApplicationConfiguration applicationConfiguration) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
		this.serviceDescriptionRegistry = serviceDescriptionRegistry;
		this.applicationConfiguration = applicationConfiguration;
	}

	@Override
	protected String getRawTableRowsHtml() {
		StringBuilder html = new StringBuilder();
		html.append("<tr><th>Input Port Name</th><th>Depth</th></tr>");
		for (InputActivityPort inputActivityPort : getActivity().getInputPorts()) {
			html.append("<tr><td>" + inputActivityPort.getName() + "</td><td>");
			html.append(inputActivityPort.getDepth() + "</td></tr>");
		}
		html.append("<tr><th>Output Port Name</th><th>Depth</th></tr>");
		for (OutputActivityPort outputActivityPort : getActivity().getOutputPorts()) {
			html.append("<tr><td>" + outputActivityPort.getName() + "</td><td>");
			html.append(outputActivityPort.getDepth() + "</td></tr>");
		}
		return html.toString();
	}

	@Override
	public String getViewTitle() {
		String result = "";
		Configuration configuration = getConfigBean();
		JsonNode json = configuration.getJson();
		String workerName = LocalworkerServiceProvider.getServiceNameFromClassname(json.get(
				"localworkerName").textValue());
		if (json.get("isAltered").booleanValue()) {
			result = "Altered local worker service";
			if ((workerName != null) && !workerName.equals("")) {
				result += " - originally " + workerName;
			}
		} else {
			result = "Local worker service";
			if ((workerName != null) && !workerName.equals("")) {
				result += " - " + workerName;
			}
		}
		return result;
	}

	@Override
	public Action getConfigureAction(Frame owner) {
		return new LocalworkerActivityConfigurationAction(getActivity(), owner, editManager,
				fileManager, activityIconManager, serviceDescriptionRegistry,
				applicationConfiguration);
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
