/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import net.sf.taverna.t2.lang.ui.FileTools;
import net.sf.taverna.t2.lang.ui.LineEnabledTextPanel;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

/**
 * @author alanrw
 *
 */
public class ScriptPanel extends JPanel {
	
	private static SAXBuilder builder = new SAXBuilder();
	private final JTextComponent scriptTextArea;
	
	public ScriptPanel(final ExternalToolConfigView view, JTextComponent scriptTextArea, JCheckBox stdInCheckBox, JCheckBox stdOutCheckBox, JCheckBox stdErrCheckBox) {
		super();
		this.scriptTextArea = scriptTextArea;
		
		this.setLayout(new BorderLayout());
		this.add(new LineEnabledTextPanel(scriptTextArea),
				BorderLayout.CENTER);

		JButton loadScriptButton = new JButton("Load description");
		loadScriptButton.setToolTipText("Load tool description from a file");
		loadScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newScript = FileTools.readStringFromFile(
						ScriptPanel.this, "Load tool description",
						".xml");
				if (newScript != null) {
					String errorMessage = null;
					try {
						Document doc = builder
								.build(new StringReader(newScript));
						UseCaseDescription newDescription = new UseCaseDescription(
								doc.getRootElement());
						view.getConfiguration().setUseCaseDescription(newDescription);
						view.refreshConfiguration(view.getConfiguration());
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
		});

		JButton saveScriptButton = new JButton("Save description");
		saveScriptButton.setToolTipText("Save the tool description to a file");
		saveScriptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				XMLOutputter outputter = new XMLOutputter(Format
						.getPrettyFormat());
				FileTools.saveStringToFile(ScriptPanel.this,
						"Save tool description", ".xml", outputter
								.outputString(view.makeConfiguration()
										.getUseCaseDescription()
										.writeToXMLElement()));
			}
		});

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
