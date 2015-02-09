/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.lang.ui.DeselectingButton;
import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;

import org.jdom.input.SAXBuilder;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;


/**
 * @author alanrw
 *
 */
public class ScriptPanel extends JPanel {
	
	private static final String SCRIPT_DESCRIPTION = "Specify the commands that you want to run. You can use data arriving at an input port to replace parts of the command or to write to a file. You can also take data written to a file and send it to an output port.";
	static SAXBuilder builder = new SAXBuilder();
	private final JTextComponent scriptTextArea;
	
	public ScriptPanel(final ExternalToolConfigView view, JTextComponent scriptTextArea, JCheckBox stdInCheckBox, JCheckBox stdOutCheckBox, JCheckBox stdErrCheckBox, JTextField returnCodesField) {
		super();
		this.setLayout(new BorderLayout());
	
		JTextArea descriptionText = new JTextArea(
				SCRIPT_DESCRIPTION);
		descriptionText.setEditable(false);
		descriptionText.setFocusable(false);
		descriptionText.setBorder(new EmptyBorder(5, 5, 10, 5));
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		this.add(descriptionText, BorderLayout.NORTH);

		this.scriptTextArea = scriptTextArea;
		
		this.add(new LineEnabledTextPanel(scriptTextArea),
				BorderLayout.CENTER);
		

		UseCaseDescription useCaseDescription = view.getConfiguration().getUseCaseDescription();
		stdInCheckBox.setSelected(useCaseDescription.isIncludeStdIn());
		stdOutCheckBox.setSelected(useCaseDescription.isIncludeStdOut());
		stdErrCheckBox.setSelected(useCaseDescription.isIncludeStdErr());
		returnCodesField.setText(useCaseDescription.getReturnCodesAsText());
		
		JPanel codesPanel = new JPanel(new FlowLayout());
		codesPanel.add(new JLabel("Valid return codes:"));
		codesPanel.add(returnCodesField);

		JPanel streamPanel = new JPanel(new FlowLayout());
		streamPanel.add(stdInCheckBox);
		streamPanel.add(stdOutCheckBox);
		streamPanel.add(stdErrCheckBox);

		JPanel buttonPanel = new JPanel(new FlowLayout());
		if (view.isOriginallyFromRepository()) {
			JButton revertButton = new DeselectingButton("Revert to original description",
					new AbstractAction(){

				@Override
				public void actionPerformed(ActionEvent e) {
					ExternalToolActivityConfigurationBean bean = view.makeConfiguration();
					String repositoryUrl = bean.getRepositoryUrl();
					String id = bean.getExternaltoolid();
					UseCaseDescription usecase = null;
					try {
						usecase = UseCaseEnumeration.readDescriptionFromUrl(
							repositoryUrl, id);
					}
					catch (IOException ex) {
						// Already logged
					}
					if (usecase != null) {
						bean.setUseCaseDescription(usecase);
						view.setEditable(false, bean);
					} else {
						JOptionPane.showMessageDialog(view, "Unable to find tool description " + id, "Missing tool description", JOptionPane.ERROR_MESSAGE);
					}
				}});
			revertButton.setToolTipText("Revert to the tool description from the repository");
			buttonPanel.add(revertButton);
		}
		JButton loadScriptButton = new DeselectingButton("Load description",
				new LoadDescriptionAction(this, view));
		loadScriptButton.setToolTipText("Load tool description from a file");

		JButton saveScriptButton = new DeselectingButton("Export description",
				new SaveDescriptionAction(this, view));
		saveScriptButton.setToolTipText("Export the tool description to a file");

		JButton clearScriptButton = new DeselectingButton("Clear script",
				new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				clearScript();
			}

		});
		clearScriptButton.setToolTipText("Clear the script from the edit area");

		buttonPanel.add(loadScriptButton);
		buttonPanel.add(saveScriptButton);
		buttonPanel.add(clearScriptButton);

		JPanel subPanel = new JPanel(new GridLayout(3,1));
		subPanel.add(codesPanel);
		subPanel.add(streamPanel);
		subPanel.add(buttonPanel);

		this.add(subPanel, BorderLayout.SOUTH);
	}

	/**
	 * Method for clearing the script
	 * 
	 */
	private void clearScript() {
		if (JOptionPane.showConfirmDialog(this,
				"Do you really want to clear the tool description?",
				"Clearing the tool description", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			scriptTextArea.setText("");
		}

	}

}
