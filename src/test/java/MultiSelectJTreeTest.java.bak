import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;


public class MultiSelectJTreeTest extends JFrame
{
  public MultiSelectJTreeTest()
  {
    Container contentPane = this.getContentPane();
    
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
    JTree tree = new JTree(rootNode);
    
    DefaultMutableTreeNode child1 = new DefaultMutableTreeNode("child 1");
    rootNode.add(child1);
    DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("child 2");
    rootNode.add(child2);
    DefaultMutableTreeNode child3 = new DefaultMutableTreeNode("child 3");
    rootNode.add(child3);
    
    DefaultMutableTreeNode child1_1 = new DefaultMutableTreeNode("child 1_1");
    child1.add(child1_1);
    DefaultMutableTreeNode child1_2 = new DefaultMutableTreeNode("child 1_2");
    child1.add(child1_2);
    DefaultMutableTreeNode child1_3 = new DefaultMutableTreeNode("child 1_3");
    child1.add(child1_3);
    
    DefaultMutableTreeNode child2_1 = new DefaultMutableTreeNode("child 2_1");
    child2.add(child2_1);
    DefaultMutableTreeNode child2_2 = new DefaultMutableTreeNode("child 2_2");
    child2.add(child2_2);
    DefaultMutableTreeNode child2_3 = new DefaultMutableTreeNode("child 2_3");
    child2.add(child2_3);
    
    DefaultMutableTreeNode child3_1 = new DefaultMutableTreeNode("child 3_1");
    child3.add(child3_1);
    DefaultMutableTreeNode child3_2 = new DefaultMutableTreeNode("child 3_2");
    child3.add(child3_2);
    DefaultMutableTreeNode child3_3 = new DefaultMutableTreeNode("child 3_3");
    child3.add(child3_3);
    
    contentPane.add(tree);
    
    this.setPreferredSize(new Dimension(600, 600));
    this.pack();
    this.setLocationRelativeTo(null);
  }
  
  
  public static void main(String[] args) throws Exception
  {
    JFrame testFrame = new MultiSelectJTreeTest();
    testFrame.setVisible(true);
  }

}
