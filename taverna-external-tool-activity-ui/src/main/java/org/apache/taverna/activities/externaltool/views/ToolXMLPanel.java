/**
 * 
 */
package org.apache.taverna.activities.externaltool.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.taverna.renderers.impl.XMLTree;

import org.apache.taverna.activities.externaltool.desc.ToolDescription;

/**
 * @author alanrw
 *
 */
public class ToolXMLPanel extends JPanel {

	public ToolXMLPanel(ToolDescription useCaseDescription) {
		super(new BorderLayout());
		XMLTree xmlTree = new XMLTree(useCaseDescription.writeToXMLElement());
		this.add(new JScrollPane(xmlTree), BorderLayout.CENTER);
	}

	public void regenerateTree(ToolDescription useCaseDescription) {
		this.removeAll();
		XMLTree xmlTree = new XMLTree(useCaseDescription.writeToXMLElement());
		this.add(new JScrollPane(xmlTree), BorderLayout.CENTER);		
	}

}
