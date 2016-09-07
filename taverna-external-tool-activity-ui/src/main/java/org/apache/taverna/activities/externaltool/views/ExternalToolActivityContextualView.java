
package org.apache.taverna.activities.externaltool.views;

import java.awt.Frame;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.Action;

import org.apache.taverna.activities.externaltool.ExternalToolActivity;
import org.apache.taverna.activities.externaltool.ExternalToolActivityConfigurationBean;
import org.apache.taverna.activities.externaltool.actions.ExternalToolActivityConfigureAction;
import org.apache.taverna.activities.externaltool.servicedescriptions.ExternalToolActivityIcon;
import org.apache.taverna.workbench.activityicons.ActivityIconManager;
import org.apache.taverna.workbench.configuration.colour.ColourManager;
import org.apache.taverna.workbench.edits.EditManager;
import org.apache.taverna.workbench.file.FileManager;
import org.apache.taverna.workbench.ui.actions.activity.HTMLBasedActivityContextualView;
import org.apache.taverna.workflowmodel.processor.activity.Activity;
import org.apache.taverna.activities.externaltool.desc.ScriptInput;
import org.apache.taverna.activities.externaltool.desc.ScriptInputStatic;
import org.apache.taverna.activities.externaltool.desc.ScriptOutput;
import org.apache.taverna.activities.externaltool.desc.ToolDescription;

/**
 * ExternalToolActivityContextualView displays the use case information in a HTML table. Currently,
 * this is only the use case ID.
 *
 * @author Hajo Nils Krabbenhoeft
 */
public class ExternalToolActivityContextualView extends
		HTMLBasedActivityContextualView<ExternalToolActivityConfigurationBean> {
	private static final long serialVersionUID = 1L;
	private final EditManager editManager;
	private final FileManager fileManager;
	private final ActivityIconManager activityIconManager;

	public ExternalToolActivityContextualView(Activity<?> activity, EditManager editManager,
			FileManager fileManager, ColourManager colourManager, ActivityIconManager activityIconManager) {
		super(activity, colourManager);
		this.editManager = editManager;
		this.fileManager = fileManager;
		this.activityIconManager = activityIconManager;
	}

	@Override
	protected String getRawTableRowsHtml() {
		String html = "";
		ExternalToolActivityConfigurationBean bean = getConfigBean();
		String repositoryUrl = bean.getRepositoryUrl();
		if ((repositoryUrl == null) || repositoryUrl.isEmpty()) {
			repositoryUrl = "<b>Not specified</b>";
		}
		html += "<tr><td>Repository URL</td><td>" + repositoryUrl + "</td></tr>";

		String id = bean.getExternaltoolid();
		if ((id == null) || id.isEmpty()) {
			id = "<b>Not specified</b>";
		}
		html += "<tr><td>Id</td><td>" + id + "</td></tr>";

		ToolDescription useCaseDescription = bean.getUseCaseDescription();
		String name = useCaseDescription.getUsecaseid();
		if ((name == null) || name.isEmpty()) {
			name = "<b>Not specified</b>";
		}
		html += "<tr><td>Name</td><td>" + name + "</td></tr>";

		Map<String, ScriptInput> stringReplacements = new TreeMap<String, ScriptInput>();
		Map<String, ScriptInput> fileInputs = new TreeMap<String, ScriptInput>();

		for (Entry<String, ScriptInput> entry : useCaseDescription.getInputs().entrySet()) {
			String key = entry.getKey();
			ScriptInput value = entry.getValue();
			if (value.isFile()) {
				fileInputs.put(key, value);
			} else if (value.isTempFile()) {
				// Nothing
			} else {
				stringReplacements.put(key, value);
			}
		}

		if (!stringReplacements.isEmpty()) {
			html += "<tr><td colspan=2 align=center><b>String replacements</b></td></tr>";
			html += "<tr><td><b>Port name</b></td><td><b>Replaces</b></td></tr>";
			for (String siName : stringReplacements.keySet()) {
				html += "<tr><td>" + siName + "</td>";
				ScriptInput si = stringReplacements.get(siName);
				html += "<td>%%" + si.getTag() + "%%</td>";

				html += "</tr>";
			}
		}

		if (!fileInputs.isEmpty()) {
			html += "<tr><td colspan=2 align=center><b>File inputs</b></td></tr>";
			html += "<tr><td><b>Port name</b></td><td><b>To file</b></td></tr>";
			for (String siName : fileInputs.keySet()) {
				html += "<tr><td>" + siName + "</td>";
				ScriptInput si = fileInputs.get(siName);
				html += "<td>" + si.getTag() + "</td>";

				html += "</tr>";
			}
		}

		List<ScriptInputStatic> staticInputs = useCaseDescription.getStatic_inputs();
		if (!staticInputs.isEmpty()) {
			html += "<tr><td colspan=2 align=center><b>Static inputs</b></td></tr>";
			html += "<tr><td><b>Type</b></td><td><b>To file</b></td></tr>";
			for (ScriptInputStatic si : staticInputs) {
				if (si.getUrl() != null) {
					html += "<td><b>URL</b></td>";
				} else {
					html += "<td><b>Explicit content</b></td>";
				}
				if (si.isFile()) {
					html += "<td>" + si.getTag() + "</td>";
				}
				html += "</tr>";
			}
		}
		Map<String, ScriptOutput> outputs = useCaseDescription.getOutputs();
		if (!outputs.isEmpty()) {
			html += "<tr><td colspan=2 align=center><b>File outputs</b></td></tr>";
			html += "<tr><td><b>Port name</b></td><td><b>From file</b></td></tr>";
			for (String soName : outputs.keySet()) {
				html += "<tr><td>" + soName + "</td>";
				ScriptOutput so = outputs.get(soName);
				html += "<td>" + so.getPath() + "</td>";
				html += "</tr>";
			}
		}
		return html;
	}

	@Override
	public String getViewTitle() {
		return "Tool service";
	}

	@Override
	public Action getConfigureAction(final Frame owner) {
		return new ExternalToolActivityConfigureAction((ExternalToolActivity) getActivity(), owner,
				editManager, fileManager, activityIconManager);
	}

	public String getBackgroundColour() {

		return ExternalToolActivityIcon.getColourString();
	}

	@Override
	public int getPreferredPosition() {
		return 100;
	}

}
