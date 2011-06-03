/**
 * 
 */
package net.sf.taverna.t2.activities.externaltool.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.taverna.t2.renderers.XMLTree;

import de.uni_luebeck.inb.knowarc.usecases.UseCaseDescription;

/**
 * @author alanrw
 *
 */
public class ToolXMLPanel extends JPanel {

	public ToolXMLPanel(UseCaseDescription useCaseDescription) {
		super(new BorderLayout());
		XMLTree xmlTree = new XMLTree(useCaseDescription.writeToXMLElement());
		this.add(new JScrollPane(xmlTree), BorderLayout.CENTER);
	}

	public void regenerateTree(UseCaseDescription useCaseDescription) {
		this.removeAll();
		XMLTree xmlTree = new XMLTree(useCaseDescription.writeToXMLElement());
		this.add(new JScrollPane(xmlTree), BorderLayout.CENTER);		
	}

}
