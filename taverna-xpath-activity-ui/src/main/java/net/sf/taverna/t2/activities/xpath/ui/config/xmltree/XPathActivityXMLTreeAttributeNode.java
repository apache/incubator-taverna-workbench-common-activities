package net.sf.taverna.t2.activities.xpath.ui.config.xmltree;

import org.dom4j.Attribute;

/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityXMLTreeAttributeNode extends XPathActivityXMLTreeNode
{
  private Attribute associatedAttribute;
  
  public XPathActivityXMLTreeAttributeNode(Attribute associatedAttribute) {
    super(associatedAttribute, true);
    this.associatedAttribute = associatedAttribute;
  }
  
  public Attribute getAssociatedAttribute() {
    return associatedAttribute;
  }
  
  public String getTreeNodeDisplayLabel(boolean bIncludeValue, boolean bUseStyling)
  {
    StringBuilder label = new StringBuilder();
    
    // add qualified attribute name (possibly) with styling
    label.append((bUseStyling ? "<font color=\"purple\">" : "") +
                 this.associatedAttribute.getQualifiedName() +
                 (bUseStyling ? "</font>" : ""));
    
    // add attribute value
    if (bIncludeValue)
    {
      String attributeTextValue = this.associatedAttribute.getText();
      
      if (attributeTextValue != null && attributeTextValue.length() > 0) {
        label.append((bUseStyling ? "<font color=\"gray\"> - </font><font color=\"green\">" : "") +
                     truncateElementTextValue(stripAllHTML(attributeTextValue)) +
                     (bUseStyling ? "</font>" : ""));
      }
    }
    
    if (bUseStyling) {
      label.insert(0, "<html>");
      label.append("</html>");
    }
    
    return (label.toString());
  }
}