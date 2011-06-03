/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;

import org.jdom.input.SAXBuilder;


/**
 * @author alanrw
 *
 */
public class ScriptPanel extends JPanel {
	
	static SAXBuilder builder = new SAXBuilder();
	private final JTextComponent scriptTextArea;
	
	public ScriptPanel(final ExternalToolConfigView view, JTextComponent scriptTextArea, JCheckBox stdInCheckBox, JCheckBox stdOutCheckBox, JCheckBox stdErrCheckBox) {
		super();
		this.scriptTextArea = scriptTextArea;
		
		this.setLayout(new BorderLayout());
		this.add(new LineEnabledTextPanel(scriptTextArea),
				BorderLayout.CENTER);

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

		stdInCheckBox.setSelected(view.getConfiguration().getUseCaseDescription()
				.isIncludeStdIn());
		stdOutCheckBox.setSelected(view.getConfiguration().getUseCaseDescription()
				.isIncludeStdOut());
		stdErrCheckBox.setSelected(view.getConfiguration().getUseCaseDescription()
				.isIncludeStdErr());

		JPanel streamPanel = new JPanel();
		streamPanel.setLayout(new FlowLayout());
		streamPanel.add(stdInCheckBox);
		streamPanel.add(stdOutCheckBox);
		streamPanel.add(stdErrCheckBox);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());
		buttonPanel.add(loadScriptButton);
		buttonPanel.add(saveScriptButton);
		buttonPanel.add(clearScriptButton);

		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.add(streamPanel, BorderLayout.NORTH);
		subPanel.add(buttonPanel, BorderLayout.SOUTH);

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
