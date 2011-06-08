/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import net.sf.taverna.t2.activities.externaltool.ExternalToolActivityConfigurationBean;
import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;

import org.jdom.input.SAXBuilder;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;
import de.uni_luebeck.inb.knowarc.usecases.UseCaseEnumeration;


/**
 * @author alanrw
 *
 */
public class ScriptPanel extends JPanel {
	
	static SAXBuilder builder = new SAXBuilder();
	private final JTextComponent scriptTextArea;
	
	public ScriptPanel(final ExternalToolConfigView view, JTextComponent scriptTextArea, JCheckBox stdInCheckBox, JCheckBox stdOutCheckBox, JCheckBox stdErrCheckBox, JTextField returnCodesField) {
		super();
		this.scriptTextArea = scriptTextArea;
		
		this.setLayout(new BorderLayout());
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
			JButton revertButton = new JButton(new AbstractAction("Revert to original description"){

				@Override
				public void actionPerformed(ActionEvent e) {
					ExternalToolActivityConfigurationBean bean = view.makeConfiguration();
					String repositoryUrl = bean.getRepositoryUrl();
					String id = bean.getExternaltoolid();
					UseCaseDescription usecase = UseCaseEnumeration.readDescriptionFromUrl(
							repositoryUrl, id);
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
		JButton loadScriptButton = new JButton("Load description");
		loadScriptButton.setToolTipText("Load tool description from a file");
		loadScriptButton.addActionListener(new LoadDescriptionAction(this, view));

		JButton saveScriptButton = new JButton("Export description");
		saveScriptButton.setToolTipText("Export the tool description to a file");
		saveScriptButton.addActionListener(new SaveDescriptionAction(this, view));

		JButton clearScriptButton = new JButton("Clear script");
		clearScriptButton.setToolTipText("Clear the script from the edit area");
		clearScriptButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				clearScript();
			}

		});
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
