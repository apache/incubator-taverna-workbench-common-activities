package net.sf.taverna.t2.activities.xpath.ui.contextualview;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import net.sf.taverna.t2.workbench.ui.views.contextualviews.ContextualView;

import net.sf.taverna.t2.activities.xpath.XPathActivity;
import net.sf.taverna.t2.activities.xpath.XPathActivityConfigurationBean;
import net.sf.taverna.t2.activities.xpath.ui.config.XPathActivityConfigureAction;

@SuppressWarnings("serial")
/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityMainContextualView extends ContextualView
{
  private XPathActivityMainContextualView thisContextualView;
	private final XPathActivity activity;
	
	private JPanel jpMainPanel;
	private JTextField tfXPathExpression;
	
	private DefaultTableModel xpathNamespaceMappingsTableModel;
	private JTable jtXPathNamespaceMappings;
	private JScrollPane spXPathNamespaceMappings;
	
	
	public XPathActivityMainContextualView(XPathActivity activity) {
	  this.thisContextualView = this;
		this.activity = activity;
		initView();
	}
	
	
	@Override
	public JComponent getMainFrame()
	{
		jpMainPanel = new JPanel(new GridBagLayout());
		jpMainPanel.setBorder(
		    BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 2, 4, 2),
            BorderFactory.createEmptyBorder()
//            BorderFactory.createLineBorder(ColourManager.getInstance().getPreferredColour(XPathActivity.class.getCanonicalName()), 2)  // makes a thin border with the colour of the processor
        ));
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.weighty = 0;
		
		
		// --- XPath Expression ---
		
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		JLabel jlXPathExpression = new JLabel("XPath Expression:");
		jlXPathExpression.setFont(jlXPathExpression.getFont().deriveFont(Font.BOLD));
		jpMainPanel.add(jlXPathExpression, c);
		
		c.gridx++;
		c.weightx = 1.0;
		tfXPathExpression = new JTextField();
		tfXPathExpression.setEditable(false);
		jpMainPanel.add(tfXPathExpression, c);
		
		
		// --- Label to Show/Hide Mapping Table ---
    
		
		final JLabel jlShowHideNamespaceMappings = new JLabel("Show namespace mappings...");
    jlShowHideNamespaceMappings.setForeground(Color.BLUE);
    jlShowHideNamespaceMappings.setCursor(new Cursor(Cursor.HAND_CURSOR));
    jlShowHideNamespaceMappings.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        spXPathNamespaceMappings.setVisible(!spXPathNamespaceMappings.isVisible());
        jlShowHideNamespaceMappings.setText((spXPathNamespaceMappings.isVisible() ? "Hide" : "Show") + " namespace mappings...");
        thisContextualView.revalidate();
      }
    });
    
    c.gridx = 0;
    c.gridy++;
    c.gridwidth = 2;
    c.weightx = 1.0;
    c.weighty = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    jpMainPanel.add(jlShowHideNamespaceMappings, c);
		
		
    // --- Namespace Mapping Table ---
		
		xpathNamespaceMappingsTableModel = new DefaultTableModel() {
      /**
       * No cells should be editable
       */
      public boolean isCellEditable(int rowIndex,int columnIndex) {
        return (false);
      }
    };
    xpathNamespaceMappingsTableModel.addColumn("Namespace Prefix");
    xpathNamespaceMappingsTableModel.addColumn("Namespace URI");
		
		jtXPathNamespaceMappings = new JTable();
		jtXPathNamespaceMappings.setModel(xpathNamespaceMappingsTableModel);
		jtXPathNamespaceMappings.setPreferredScrollableViewportSize(new Dimension(200, 90));
		// TODO - next line is to be enabled when Taverna is migrated to Java 1.6; for now it's fine to run without this
    //   jtXPathNamespaceMappings.setFillsViewportHeight(true);  // makes sure that when the dedicated area is larger than the table, the latter is stretched vertically to fill the empty space
		
		jtXPathNamespaceMappings.getColumnModel().getColumn(0).setPreferredWidth(20);  // set relative sizes of columns
		jtXPathNamespaceMappings.getColumnModel().getColumn(1).setPreferredWidth(300);
		
		c.gridy++;
		spXPathNamespaceMappings = new JScrollPane(jtXPathNamespaceMappings);
		spXPathNamespaceMappings.setVisible(false);
		jpMainPanel.add(spXPathNamespaceMappings, c);
		
		
		// populate the view with values
		refreshView();
		
		return jpMainPanel;
	}

	@Override
	/**
	 * This is the title of the contextual view - shown in the list of other available
	 * views (even when this contextual view is collapsed).
	 */
	public String getViewTitle() {
		XPathActivityConfigurationBean configuration = activity.getConfiguration();
		return "XPath Service Details";
	}

	/**
	 * Typically called when the activity configuration has changed.
	 */
	@Override
	public void refreshView()
	{
		XPathActivityConfigurationBean configBean = activity.getConfiguration();
		
		// Set XPath Expression
		tfXPathExpression.setText("" + configBean.getXpathExpression());
		
		// Populate Namespace Mappings
		xpathNamespaceMappingsTableModel.getDataVector().removeAllElements();
    for (Map.Entry<String,String> mapping : configBean.getXpathNamespaceMap().entrySet()) {
      xpathNamespaceMappingsTableModel.addRow(new Object[] {mapping.getKey(), mapping.getValue()});
    }
	}

	/**
	 * View position hint
	 */
	@Override
	public int getPreferredPosition() {
		// want to be on top, as it's the main contextual view for this activity
		return 100;
	}
	
	@Override
	public Action getConfigureAction(final Frame owner) {
	  // "Configure" button appears because of this action being returned
		return new XPathActivityConfigureAction(activity, owner);
	}

}
