/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sf.taverna.t2.lang.ui.ExtensionFileFilter;
import net.sf.taverna.t2.lang.ui.FileTools;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;

final class SaveDescriptionAction extends AbstractAction {
	/**
	 * 
	 */
	private final ScriptPanel scriptPanel;
	private final ExternalToolConfigView view;
	
	private static Logger logger = Logger.getLogger(SaveDescriptionAction.class);

	private static XMLOutputter outputter = new XMLOutputter(Format
			.getPrettyFormat());

	SaveDescriptionAction(ScriptPanel scriptPanel, ExternalToolConfigView view) {
		this.scriptPanel = scriptPanel;
		this.view = view;
	}

	public void actionPerformed(ActionEvent e) {
		UseCaseDescription currentDescription = view.makeConfiguration().getUseCaseDescription();
		String usecaseid = currentDescription.getUsecaseid();
		String description = currentDescription.getDescription();
		String group = currentDescription.getGroup();
		if ((usecaseid == null) || usecaseid.isEmpty() || (description == null) || description.isEmpty() || (group == null) || group.isEmpty()) {
			JOptionPane.showMessageDialog(view, "Please fill in the tool annotation and\nthen re-export the description", "Missing annotation", JOptionPane.PLAIN_MESSAGE, null);
			view.showAnnotationPanel();
		} else {
		saveStringToFile(this.scriptPanel,
				"Save tool description", ".xml", currentDescription);
		}
	}
	
	public static boolean saveStringToFile(Component parent, String dialogTitle, String extension, UseCaseDescription description) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(dialogTitle);

		fileChooser.resetChoosableFileFilters();
		fileChooser.setAcceptAllFileFilterUsed(true);
		
		fileChooser.setFileFilter(new ExtensionFileFilter(new String[] { extension }));

		Preferences prefs = Preferences.userNodeForPackage(FileTools.class);
		String curDir = prefs
				.get("currentDir", System.getProperty("user.home"));
		fileChooser.setCurrentDirectory(new File(curDir));

		boolean tryAgain = true;
		while (tryAgain) {
			tryAgain = false;
			int returnVal = fileChooser.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				prefs.put("currentDir", fileChooser.getCurrentDirectory()
						.toString());
				File file = fileChooser.getSelectedFile();
				if (!file.getName().contains(".")) {
					String newName = file.getName() + extension;
					file = new File(file.getParentFile(), newName);
				}

				// TODO: Open in separate thread to avoid hanging UI
				try {
					List<UseCaseDescription> currentDescriptions;
					if (file.exists()) {
						currentDescriptions = UseCaseEnumeration.readDescriptionsFromStream(new FileInputStream(file));
					} else {
						currentDescriptions = new ArrayList<UseCaseDescription>();
					}
					Element overallElement = new Element("usecases");
					for (UseCaseDescription ud : currentDescriptions) {
						if (!ud.getUsecaseid().equals(description.getUsecaseid())) {
							overallElement.addContent(ud.writeToXMLElement());
						}
					}

					overallElement.addContent(description.writeToXMLElement());
					BufferedWriter out = new BufferedWriter(new FileWriter(file));
			        out.write(outputter.outputString(overallElement));
			        out.close();
					logger.info("Saved content by overwriting " + file);
					return true;
				} catch (IOException ex) {
					logger.warn("Could not save content to " + file, ex);
					JOptionPane.showMessageDialog(parent,
							"Could not save to " + file + ": \n\n"
									+ ex.getMessage(), "Warning",
							JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
		}
		return false;
	}
	
}