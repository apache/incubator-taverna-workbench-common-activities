package net.sf.taverna.t2.activities.xpath.ui.config.xmltree;

import javax.swing.tree.DefaultMutableTreeNode;

import org.dom4j.QName;


/**
 * 
 * @author Sergejs Aleksejevs
 */
public abstract class XPathActivityXMLTreeNode extends DefaultMutableTreeNode
{
  protected static final int DISPLAY_LABEL_MAX_LENGTH = 200;
  
  private final boolean isAttribute;

  public XPathActivityXMLTreeNode(Object userObject, boolean isAttribute)
  {
    super(userObject);
    this.isAttribute = isAttribute;
  }
  
  public boolean isAttribute() {
    return (isAttribute);
  }
  
  
  public QName getNodeQName() {
    if (this.isAttribute()) {
      return (((XPathActivityXMLTreeAttributeNode)this).getAssociatedAttribute().getQName());
    }
    else {
      return (((XPathActivityXMLTreeElementNode)this).getAssociatedElement().getQName());
    }
  }
  
  
  public String getTreeNodeDisplayLabel(boolean bIncludeValue, boolean bIncludeElementNamespace, boolean bUseStyling)
  {
    if (this.isAttribute()) {
      return (((XPathActivityXMLTreeAttributeNode)this).getTreeNodeDisplayLabel(bIncludeValue, bUseStyling));
    }
    else {
      return (((XPathActivityXMLTreeElementNode)this).getTreeNodeDisplayLabel(bIncludeValue, bIncludeElementNamespace, bUseStyling));
    }
  }
  
  
  protected String truncateElementTextValue(String textValue)
  {
    if (textValue != null && textValue.length() > DISPLAY_LABEL_MAX_LENGTH) {
      textValue = textValue.substring(0, DISPLAY_LABEL_MAX_LENGTH) + "(...)";
    }
    return (textValue);
  }
  
  
  /**
   * Tiny helper to strip out all HTML tags. This will not leave any HTML tags
   * at all (so that the content can be displayed in DialogTextArea - and the
   * like - components. This helps to present HTML content inside JAVA easier.
   */
  public static String stripAllHTML(String source) {
        // don't do anything if not string is provided
        if (source == null)
          return ("");

        // need to preserve at least all line breaks
        // (ending and starting paragraph also make a line break)
        source = source.replaceAll("</p>[\r\n]*<p>", "<br>");
        source = source.replaceAll("\\<br/?\\>", "\n\n");

        // strip all HTML
        source = source.replaceAll("\\<.*?\\>", ""); // any HTML tags
        source = source.replaceAll("&\\w{1,4};", ""); // this is for things like "&nbsp;", "&gt;", etc

        return (source);
  }
  
}