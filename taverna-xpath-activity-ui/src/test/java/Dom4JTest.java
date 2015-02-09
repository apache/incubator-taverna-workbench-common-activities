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
