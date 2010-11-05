package net.sf.taverna.t2.activities.xpath.ui.config;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * Auxiliary class that creates a JPanel with two labels and two text fields.
 * 
 * It can be used to be placed into a dialog made by JOptionPane to get two
 * input values, not just one.   
 * 
 * @author Sergejs Aleksejevs
 */
public class TwoFieldQueryPanel extends JPanel
{
  private JTextField tfFirstValue;
  private JTextField tfSecondValue;
  
  
  public TwoFieldQueryPanel(String firstFieldName, String secondFieldName)
  {
    this(null, firstFieldName, null, secondFieldName, null);
  }
  
  public TwoFieldQueryPanel(String message, String firstFieldName, String secondFieldName)
  {
    this(message, firstFieldName, null, secondFieldName, null);
  }
  
  public TwoFieldQueryPanel(String firstFieldName, String firstFieldDefaultValue,
                            String secondFieldName, String secondFieldDefaultValue)
  {
    this(null, firstFieldName, firstFieldDefaultValue, secondFieldName, secondFieldDefaultValue);
  }
  
  public TwoFieldQueryPanel(String message,
                            String firstFieldName, String firstFieldDefaultValue,
                            String secondFieldName, String secondFieldDefaultValue)
  {
    super();
    this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    
    this.setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    c.insets = new Insets(3,5,3,5);
    
    
    if (message != null && message.length() > 0)
    {
      c.gridwidth = 2;
      c.insets = new Insets(5, 5, 15, 5);
      this.add(new JLabel(message), c);
      
      c.gridwidth = 1;
      c.gridx = 0;
      c.gridy++;
      c.insets = new Insets(3,5,3,5);
    }
    
    
    this.add(new JLabel(firstFieldName), c);
    
    c.gridx++;
    c.weightx = 1.0;
    tfFirstValue = new JTextField(20);
    if (firstFieldDefaultValue != null) {
      tfFirstValue.setText(firstFieldDefaultValue);
    }
    tfFirstValue.selectAll();
    tfFirstValue.requestFocusInWindow();
    this.add(tfFirstValue, c);
    
    c.gridx = 0;
    c.gridy++;
    c.weightx = 0;
    this.add(new JLabel(secondFieldName), c);
    
    c.gridx++;
    c.weightx = 1.0;
    tfSecondValue = new JTextField(20);
    if (secondFieldDefaultValue != null) {
      tfSecondValue.setText(secondFieldDefaultValue);
    }
    tfSecondValue.selectAll();
    this.add(tfSecondValue, c);
  }
  
  
  /**
   * @return Trimmed value from the first text field. Guaranteed to be non-null.
   */
  public String getFirstValue() {
    return (tfFirstValue.getText().trim());
  }
  
  
  /**
   * @return Trimmed value from the second text field. Guaranteed to be non-null.
   */
  public String getSecondValue() {
    return (tfSecondValue.getText().trim());
  }
  
}
