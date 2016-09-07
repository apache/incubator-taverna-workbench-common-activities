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