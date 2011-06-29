/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.utils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;

import de.uni_luebeck.inb.knowarc.usecases.ScriptInput;
import de.uni_luebeck.inb.knowarc.usecases.ScriptInputUser;

import net.sf.taverna.t2.lang.ui.DeselectingButton;

/**
 * @author alanrw
 *
 */
public class Tools {
	
	private static CompoundBorder border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5), BorderFactory.createLineBorder(Color.BLACK, 1));
	
	private static Insets insets = new Insets(5,5,5,5);
	
	public static void addViewer(final JPanel innerPanel, String[] labels, JComponent[] elements,
			final List viewerList, final Object viewer, final JPanel outerPanel) {
		final JPanel subPanel = new JPanel();
		subPanel.setLayout(new GridBagLayout());
		subPanel.setBorder(border);
		
		final GridBagConstraints labelConstraint = new GridBagConstraints();
		labelConstraint.insets = insets;
		labelConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		labelConstraint.fill = GridBagConstraints.BOTH;
		labelConstraint.gridy = 0;
		labelConstraint.gridx = 0;
		labelConstraint.weightx = 0;

		final GridBagConstraints elementConstraint = new GridBagConstraints();
		elementConstraint.insets = insets;
		elementConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		elementConstraint.fill = GridBagConstraints.BOTH;
		elementConstraint.gridy = 0;
		elementConstraint.gridx = 1;
		elementConstraint.weightx = 1.0;
		
		final GridBagConstraints removeConstraint = new GridBagConstraints();
		removeConstraint.insets = insets;
		removeConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		removeConstraint.fill = GridBagConstraints.BOTH;
		removeConstraint.gridx = 1;
		removeConstraint.weightx = 0;
		removeConstraint.fill = GridBagConstraints.NONE;
		removeConstraint.anchor = GridBagConstraints.EAST;
		
		final GridBagConstraints subPanelConstraint = new GridBagConstraints();
		subPanelConstraint.insets = insets;
		subPanelConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		subPanelConstraint.fill = GridBagConstraints.BOTH;
		subPanelConstraint.gridx = 1;
//		subPanelConstraint.gridy = ++stringReplacementGridy;
		subPanelConstraint.weightx = 1.00;
		subPanelConstraint.fill = GridBagConstraints.HORIZONTAL;
		subPanelConstraint.anchor = GridBagConstraints.WEST;		
		
		for (int i = 0; i < labels.length; i++) {
			subPanel.add(new JLabel(labels[i] + ":"), labelConstraint);
			subPanel.add(elements[i], elementConstraint);
			labelConstraint.gridy++;
			elementConstraint.gridy++;
		}
		
		removeConstraint.gridy = labelConstraint.gridy + 1;
		final JButton removeButton = new DeselectingButton("Remove",
				new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				synchronized (viewerList) {
					viewerList.remove(viewer);
				}
				innerPanel.remove(subPanel);
				innerPanel.revalidate();
				innerPanel.repaint();
				outerPanel.revalidate();
				outerPanel.repaint();
			}

		});
		subPanel.add(removeButton, removeConstraint);
		innerPanel.add(subPanel, subPanelConstraint);
	}
	
	public static boolean isStringReplacement(ScriptInputUser si) {
		return !si.isList() && !si.isFile() && !si.isTempFile();
	}
	
	public static boolean isInputFile(ScriptInputUser si) {
		return !si.isList() && si.isFile();
	}

	public static boolean isFileList(ScriptInputUser si) {
		return si.isList() && si.isFile();
	}
	
	public static boolean isUnderstood(ScriptInputUser si) {
		return isStringReplacement(si) || isInputFile(si) || isFileList(si);
	}
	
	public static boolean areAllUnderstood(Map<String, ScriptInput> inputs) {
		for (ScriptInput si : inputs.values()) {
			if ((si instanceof ScriptInputUser) && !isUnderstood((ScriptInputUser) si)) {
				return false;
			}
		}
		return true;
	}
}
