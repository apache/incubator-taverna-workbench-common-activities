import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jdom.JDOMException;

//import net.sf.taverna.t2.renderers.XMLTree;

public class XMLTreeTest
{
  public static void main(String[] args) throws IOException, JDOMException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
  {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

    
    String strDoc = 
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<services>" +
        "<parameters>" +
          "<filters/>" +
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
    
//    XMLTree tree = new XMLTree(strDoc);
//    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//    tree.addTreeSelectionListener(new TreeSelectionListener() {
//      public void valueChanged(TreeSelectionEvent e) {
//        JOptionPane.showMessageDialog(null, e.getPaths());
//      }
//    });
    
    JFrame frame = new JFrame();
//    frame.getContentPane().add(tree);
    frame.pack();
    frame.setPreferredSize(new Dimension(500, 500));
    frame.setVisible(true);
    
    
    
    
  }
   
}
