package org.apache.taverna.activities.xpath.ui.config.xmltree;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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