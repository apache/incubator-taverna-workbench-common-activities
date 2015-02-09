package net.sf.taverna.t2.activities.xpath.ui.config.xmltree;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sf.taverna.t2.activities.xpath.ui.config.XPathActivityConfigurationPanel;

import org.dom4j.DocumentHelper;


/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityXMLTreeSelectionHandler implements TreeSelectionListener
{
  private final XPathActivityXMLTree theTree;
  private final XPathActivityConfigurationPanel parentConfigPanel;


  public XPathActivityXMLTreeSelectionHandler(XPathActivityConfigurationPanel parentConfigPanel,
                  XPathActivityXMLTree tree)
  {
    this.parentConfigPanel = parentConfigPanel;
    this.theTree = tree;
  }
  
  
  public void valueChanged(TreeSelectionEvent e)
  {
    // get the newly made selection
    TreePath newSelectedPath = e.getNewLeadSelectionPath();
    
    // NB! Safety check - sometimes the container of the XML tree will remove all selections,
    //     in such case this listener is not supposed to perform any action -> terminate 
    if (newSelectedPath == null) return;
    
    
    // --- XPath GENERATION ---
    
    // get the XPath expression for the new selection + taking into consideration all previous ones
    List<String> wildcardedXPath = generateWildcardedXPathExpression(newSelectedPath);
    
    // assemble the xpath expression as one string
    StringBuilder xpath = new StringBuilder();
    for (String leg : wildcardedXPath) {
      xpath.append(leg);
    }
    theTree.setCurrentXPathExpression(DocumentHelper.createXPath(xpath.toString()));
    theTree.getCurrentXPathExpression().setNamespaceURIs(theTree.getCurrentXPathNamespaces());
    
    
    // --- UPDATE CONFIG PANEL ---
    // (with new values for XPath expression and namespace mappings)
    
    // inform the parent activity configuration panel to update the XPath expression in the UI
    /* We do not update the XPath expression after changes in selection in the XML tree - we
     * now have a button to explicitly do that.
     * theTree.getParentConfigPanel().updateXPathEditingPanelValues();
     */
    
    // --- SELECTION ---
    selectAllNodesThatMatchTheCurrentXPath(wildcardedXPath, newSelectedPath);
  }
  
  
  /**
   * Selects all nodes that match the <code>wildcardedXPath</code> expression.
   * 
   * Keyboard focus is set to remain on the "deepest" (e.g. furthest from root)
   * element of the <code>lastSelectedPath</code>. 
   * 
   * @param wildcardedXPath List of strings, where each string contains one "leg" of the XPath expression
   *                        (e.g. a string starting with a "/" and containing the name of one node of the tree).
   * 
   * @param lastSelectedPath The path that was last selected in the tree (normally,
   *                         because of this selection {@link XPathActivityXMLTreeSelectionHandler#valueChanged(TreeSelectionEvent)}
   *                         was executed and this method was started as a part of that.
   */
  public void selectAllNodesThatMatchTheCurrentXPath(List<String> wildcardedXPath, TreePath lastSelectedPath)
  {
    // first of all - calculate the number of nodes that match this XPath
    // expression in the XML tree
    int numberOfMatchingNodes = parentConfigPanel.runXPath(false);
    
    
    // store all tree selection listeners in order to temporarily remove them;
    // this is necessary as selection modifications will be made here -- don't
    // want any listeners to respond to these new events
    theTree.removeAllSelectionListeners();
    
    
    // remove all previous selections - safest way to get the new ones correctly
    theTree.clearSelection();
    
    
    if (numberOfMatchingNodes <= XPathActivityConfigurationPanel.MAX_NUMBER_OF_MATCHING_NODES_TO_HIGHLIGHT_IN_THE_TREE)
    {
      // find all nodes that match the XPath expression
      List<XPathActivityXMLTreeNode> matchingNodes = new ArrayList<XPathActivityXMLTreeNode>();
      findAllNodesThatMatchWildcardedXPath(
          (XPathActivityXMLTreeNode)theTree.getModel().getRoot(),
          wildcardedXPath.subList(1, wildcardedXPath.size()),
          matchingNodes);
      
      // obtain and select TreePaths for each of the matching nodes
      for (XPathActivityXMLTreeNode matchingNode : matchingNodes) {
        TreeNode[] pathAsObjects = ((DefaultTreeModel)theTree.getModel()).getPathToRoot(matchingNode);
        TreePath path = new TreePath(pathAsObjects);
        selectTreePathAndAllItsAncestors(path);
      }
    }
    else {
      JOptionPane.showMessageDialog(parentConfigPanel,
          "Current XPath expression matches " + numberOfMatchingNodes + " nodes in the XML tree.\n" +
          "The XPath Activity is unable to highlight all these nodes in the tree due to\n" +
          "performance reasons.\n\n" +
          "The XPath Activity will still work correctly - both during the workflow execution\n" +
          "and if 'Run XPath' button is clicked to run this expression against the example XML.",
          "XPath Activity", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
    // make sure the keyboard focus stays on the actual node that was clicked on -
    // no direct way to do this, so simply de-select and re-select again
    if (lastSelectedPath != null) {
      theTree.removeSelectionPath(lastSelectedPath);
      theTree.addSelectionPath(lastSelectedPath);
    }
    
    // restore all previously stored selection listeners
    theTree.restoreAllSelectionListeners();
  }
  
  
  
  /**
   * This cannot work for XPath expressions that were modified manually -
   * only works for the type generated by the click in the XML tree. 
   * 
   * @param nodeToStartAt
   * @param xpathLegs From <code>nodeToStartAt</code>.
   * @param matchingNodes
   */
  private void findAllNodesThatMatchWildcardedXPath(XPathActivityXMLTreeNode nodeToStartAt, 
                  List<String> xpathLegs, List<XPathActivityXMLTreeNode> matchingNodes)
  {
    // some of the input data is missing, just return...
    if (nodeToStartAt == null || xpathLegs == null || matchingNodes == null) {
      return;
    }
    
    // no XPath expression to match against the 'nodeToStartAt', therefore
    // we've "found" the macthing node: 'nodeToStartAt'
    if (xpathLegs.size() == 0) {
      matchingNodes.add(nodeToStartAt);
      return;
    }
    
    // standard case - there is something to match, proceed as normal
    Enumeration<XPathActivityXMLTreeNode> startNodeChildren = nodeToStartAt.children();
    while (startNodeChildren.hasMoreElements()) {
      XPathActivityXMLTreeNode child = startNodeChildren.nextElement();
      
      if (xpathLegs.get(0).equals("/*") ||
          xpathLegs.get(0).equals(this.theTree.getXMLTreeNodeEffectiveQualifiedNameAsXPathLeg(child)))
      {
        // this node matches current section of the XPath expression
        if (xpathLegs.size() == 1) {
          // no more sections in the XPath leg list to match, so this child
          // node is the one we were looking for - add to the result
          matchingNodes.add(child);
        }
        else {
          // ...or process its children recursively
          findAllNodesThatMatchWildcardedXPath(child, xpathLegs.subList(1, xpathLegs.size()), matchingNodes);
        }
      }
    }
  }


  private List<String> generateWildcardedXPathExpression(TreePath newSelectedPath)
  {
    // look through previous selection to find paths of the same length, as the newly selected one
    List<TreePath> pathsOfSameLength = new ArrayList<TreePath>();
    TreePath[] previouslySelectedPaths = theTree.getSelectionPaths();
    for (TreePath path : previouslySelectedPaths) {
      if (path.getPathCount() == newSelectedPath.getPathCount()) {
        pathsOfSameLength.add(path);
      }
    }
    
    // if there were found any paths of the same length, we have a "wildcard" situation
    List<String> wildcardXPathLegs = theTree.generateXPathFromTreePathAsLegList(newSelectedPath);
    
    if (pathsOfSameLength.size() > 0)
    {
      // it's okay to use just the first path - TODO: explain that this is because of previous comparisons
      List<String> firstMatchingLengthPathLegs = theTree.generateXPathFromTreePathAsLegList(pathsOfSameLength.get(0));
      
      int pathLength = wildcardXPathLegs.size();
      
      // only use wildcards if the last segments of both paths are identical
      if (wildcardXPathLegs.get(pathLength - 1).equals(firstMatchingLengthPathLegs.get(pathLength - 1)))
      {
        // continue all the way to the last segment, but don't touch it
        for (int i = 0; i < wildcardXPathLegs.size() - 1; i++)
        {
          if (!wildcardXPathLegs.get(i).equals(firstMatchingLengthPathLegs.get(i))) {
            // set wildcard
            // TODO - make wildcard a constant
            // TODO - may need to make the wildcard to have a namespace? (e.g. "/default:*" instead of simply "/*")
            wildcardXPathLegs.set(i, "/*"); // definitely an element, not an attribute (as not the last segment in the path)
          }
        }
      }
    }
    
    return (wildcardXPathLegs);
  }
  
  
  
  private void selectTreePathAndAllItsAncestors(TreePath path)
  {
    // select all ancestors of that path
    TreePath pathToSelect = path;
    for (int i = 0; i < path.getPathCount(); i++)
    {
      pathToSelect = pathToSelect.getParentPath();
      theTree.addSelectionPath(pathToSelect);
    }
    
    // select the specified path itself
    //
    // NB! important to do this after the ancestors, so that the supplied
    // path is the one that retains the keyboard focus after this method terminates
    theTree.addSelectionPath(path);
  }
  
  
}
