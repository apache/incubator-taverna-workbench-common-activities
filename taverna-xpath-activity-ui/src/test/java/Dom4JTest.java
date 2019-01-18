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
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.XPath;


public class Dom4JTest
{
  private static void testDom4j() throws Exception
  {
    String strDoc = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<services>" +
        "<parameters>" +
          "<filters>" +
          "</filters>" +
          "<query urlKey=\"q\"></query>" +
          "<sortBy urlKey=\"sort_by\" urlValue=\"created\">Created at date</sortBy>" +
          "<sortOrder urlKey=\"sort_order\" urlValue=\"desc\">Descending</sortOrder>" +
          "<page urlKey=\"page\">1</page>" +
          "<pageSize urlKey=\"per_page\">10</pageSize>" +
        "</parameters>" +
        "<statistics>" +
          "<pages>170</pages>" +
          "<results>1695</results>" +
          "<total>1695</total>" +
        "</statistics>" +
        "<results>" +
          "<services>" +
            "<service name=\"1\" />" +
            "<service name=\"2\" />" +
            "<service name=\"3\" />" +
            "<service name=\"4\" />" +
          "</services>" +
        "</results>" +
      "</services>";
    
    
    Document doc = DocumentHelper.parseText(strDoc);
    
//    // Pretty print the document to System.out
//    OutputFormat format = OutputFormat.createPrettyPrint();
//    XMLWriter writer = new XMLWriter( System.out, format );
//    writer.write( doc );

    
    XPath expr = DocumentHelper.createXPath("//pages");
    
    List<Node> matchingNodes = expr.selectNodes(doc);
    
//    List<Node> matchingNodes = doc.selectNodes("/services/parameters");
    
    
//    System.out.println("\n\n");
    System.out.println(matchingNodes.size());
    for (Node n : matchingNodes) {
      System.out.println(n.asXML());
    }
  }
  
  /**
   * @param args
   * @throws Exception 
   */ 
  public static void main(String[] args) throws Exception {
    testDom4j();
  }

}
