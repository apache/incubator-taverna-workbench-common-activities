package net.sf.taverna.t2.activities.xpath.ui.config.xmltree;

import org.dom4j.Element;
import org.dom4j.Namespace;


/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityXMLTreeElementNode extends XPathActivityXMLTreeNode
{
  private Element associatedElement;
  
  public XPathActivityXMLTreeElementNode(Element associatedElement) {
    super(associatedElement, false);
    this.associatedElement = associatedElement;
  }
  
  public Element getAssociatedElement() {
    return associatedElement;
  }
  
  public String getTreeNodeDisplayLabel(boolean bIncludeValue, boolean bIncludeNamespace, boolean bUseStyling)
  {
    StringBuilder label = new StringBuilder();
    
    // add qualified element name
    label.append(this.associatedElement.getQualifiedName());
    
    // add element namespace
    if (bIncludeNamespace)
    {
      Namespace ns = this.associatedElement.getNamespace();
      
      label.append((bUseStyling ? "<font color=\"gray\">" : "") +
          " - xmlns" + (ns.getPrefix().length() > 0 ? (":" + ns.getPrefix()) : "") + "=\"" + 
          this.associatedElement.getNamespaceURI() +
          (bUseStyling ? "\"</font>" : ""));
    }
    
    // add element value
    if (bIncludeValue)
    {
      String elementTextValue = this.associatedElement.getTextTrim();
      
      if (elementTextValue != null && elementTextValue.length() > 0) {
        label.append((bUseStyling ? "<font color=\"gray\"> - </font><font color=\"blue\">" : "") +
                     truncateElementTextValue(stripAllHTML(elementTextValue)) +
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