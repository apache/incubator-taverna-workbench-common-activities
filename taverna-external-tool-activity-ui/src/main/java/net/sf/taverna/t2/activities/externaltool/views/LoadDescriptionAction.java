/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import net.sf.taverna.t2.lang.ui.FileTools;

import org.jdom.Document;
import org.jdom.JDOMException;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;

final class LoadDescriptionAction extends AbstractAction {
	/**
	 * 
	 */
	private final ScriptPanel scriptPanel;
	private final ExternalToolConfigView view;

	LoadDescriptionAction(ScriptPanel scriptPanel, ExternalToolConfigView view) {
		this.scriptPanel = scriptPanel;
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		String descriptionsString = FileTools.readStringFromFile(
				this.scriptPanel, "Load tool description",
				".xml");
		if (descriptionsString != null) {
			String errorMessage = null;
			try {
				Document doc = ScriptPanel.builder
						.build(new StringReader(descriptionsString));
				List<UseCaseDescription> descriptions = UseCaseEnumeration.readDescriptionsFromStream(new StringBufferInputStream(descriptionsString));
				if (descriptions.isEmpty()) {
					JOptionPane.showMessageDialog(this.scriptPanel, "No tool descriptions found", "File content", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (descriptions.size() == 1) {
					view.getConfiguration().setUseCaseDescription(descriptions.get(0));
					view.refreshConfiguration(view.getConfiguration());
					return;
				}
				
				List<String> descriptionNames = new ArrayList();
				for (UseCaseDescription ud : descriptions) {
					descriptionNames.add(ud.getUsecaseid());
				}
				Collections.sort(descriptionNames);
				String chosenName = (String) JOptionPane.showInputDialog(this.scriptPanel, "Please select a tool description",
						"Select tool description", JOptionPane.PLAIN_MESSAGE, null, descriptionNames.toArray(), descriptionNames.get(0));
				if (chosenName != null) {
					for (UseCaseDescription ud : descriptions) {
						if (ud.getUsecaseid().equals(chosenName)) {
							view.getConfiguration().setUseCaseDescription(ud);
							view.refreshConfiguration(view.getConfiguration());
							return;
							
						}
					}
				}
			} catch (JDOMException e1) {
				errorMessage = e1.getMessage();
			} catch (IOException e1) {
				errorMessage = e1.getMessage();
			} catch (Exception e1) {
				errorMessage = e1.getMessage();
			}
			if (errorMessage != null) {
				JOptionPane.showMessageDialog(null, errorMessage,
						"Tool description load error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}