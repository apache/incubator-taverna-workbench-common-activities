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