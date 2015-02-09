package net.sf.taverna.t2.activities.xpath.ui.config.xmltree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.taverna.t2.activities.xpath.ui.config.XPathActivityConfigurationPanel;
import net.sf.taverna.t2.activities.xpath.ui.servicedescription.XPathActivityIcon;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.XPath;


/**
 * 
 * @author Sergejs Aleksejevs
 */
public class XPathActivityXMLTree extends JTree
{
  private XPathActivityXMLTree instanceOfSelf;
  private XPathActivityXMLTreeRenderer treeRenderer;
  
  private JPopupMenu contextualMenu;
  
  private TreeSelectionListener[] allSelectionListeners;
  private XPathActivityXMLTreeSelectionHandler xmlTreeSelectionHandler;
  
  /**
   * 
   */
  private XPathActivityConfigurationPanel parentConfigPanel;

  private Document documentUsedToPopulateTree;
  
  /**
   *  holds value of the current XPath expression obtained from 
   *  the combination of nodes selected in the XML tree 
   */
  private XPath currentXPathExpression;
  
  private Map<String,String> currentXPathNamespaces;
  
  
  
  private XPathActivityXMLTree(XPathActivityXMLTreeNode root, Document documentUsedToPopulateTree, 
      boolean bIncludeElementValues, boolean bIncludeElementNamespaces, XPathActivityConfigurationPanel parentConfigPanel)
  {
    super(root);
    
    this.instanceOfSelf = this;
    this.allSelectionListeners = new TreeSelectionListener[0];
    
    this.parentConfigPanel = parentConfigPanel;
    
    this.documentUsedToPopulateTree = documentUsedToPopulateTree;
    this.currentXPathExpression = null;
    this.currentXPathNamespaces = new HashMap<String,String>();
    this.prepopulateNamespaceMap();
    
    
    // custom renderer of the nodes in the XML tree
    this.treeRenderer = new XPathActivityXMLTreeRenderer(bIncludeElementValues, bIncludeElementNamespaces);
    this.setCellRenderer(treeRenderer);
    
    
    // add listener to handle various selections of nodes in the tree 
    this.xmlTreeSelectionHandler = new XPathActivityXMLTreeSelectionHandler(parentConfigPanel, this);
    this.addTreeSelectionListener(xmlTreeSelectionHandler);
    
    
    // --- CONTEXTUAL MENU FOR EXPANDING / COLLAPSING THE TREE ---
    
    // create popup menu for expanding / collapsing all nodes in the tree
    JMenuItem miExpandAll = new JMenuItem("Expand all", XPathActivityIcon.getIconById(XPathActivityIcon.XML_TREE_EXPAND_ALL_ICON));
    miExpandAll.setToolTipText("Expand all nodes in the filtering tree");
    miExpandAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < getRowCount(); i++) {
          instanceOfSelf.expandRow(i);
        }
      }
    });
    JMenuItem miCollapseAll = new JMenuItem("Collapse all", XPathActivityIcon.getIconById(XPathActivityIcon.XML_TREE_COLLAPSE_ALL_ICON));
    miCollapseAll.setToolTipText("Collapse all expanded nodes in the filtering tree");
    miCollapseAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        for (int i = getRowCount() - 1; i >= 0; i--) {
          instanceOfSelf.collapseRow(i);
        }
      }
    });
    
    // populate the popup menu with created menu items
    contextualMenu = new JPopupMenu();
    contextualMenu.add(miExpandAll);
    contextualMenu.add(miCollapseAll);
    
    // mouse events may cause the contextual menu to be shown - adding a listener
    this.addMouseListener(new MouseAdapter()
    {
      public void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
          contextualMenu.show(instanceOfSelf, e.getX(), e.getY());
        }
      }
      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          // another way a popup menu may be called on different systems
          contextualMenu.show(instanceOfSelf, e.getX(), e.getY());
        }
      }
    });
    
  }
  
  
  /**
   * Pre-populates namespace map with the namespaced declared in the root
   * node of the XML document, which was used to populate the tree.
   */
  private void prepopulateNamespaceMap()
  {
    Document doc = this.getDocumentUsedToPopulateTree();
    Element root = doc.getRootElement();
    
    // get opening tag of the root node
    String rootAsXML = root.asXML().substring(0, root.asXML().indexOf(">"));
    
    // split the opening tag into tokens (all attributes are separated by a space)
    String[] rootTokens = rootAsXML.split(" ");
    
    // for each attribute check if that's a namespace declaration
    for (String token : rootTokens) {
      if (token.startsWith("xmlns"))
      {
        String[] namespacePrefixAndURI = token.split("=");
        
        // a prefix is either given explicitly, or an empty one will be used
        String prefix = namespacePrefixAndURI[0].indexOf(":") == -1 ?
                        "" :
                        namespacePrefixAndURI[0].split(":")[1];
        
        // URI is the value of the XML attribute, so need to strip out surrounding quotes
        String URI = namespacePrefixAndURI[1].replaceAll("\"", "");
        
        // now add the details of the current namespace to the map
        this.addNamespaceToXPathMap(new Namespace(prefix, URI));
      }
    }
  }


  protected XPathActivityConfigurationPanel getParentConfigPanel() {
    return parentConfigPanel;
  }
  
  public XPathActivityXMLTreeSelectionHandler getXMLTreeSelectionHandler() {
    return xmlTreeSelectionHandler;
  }
  
  public Document getDocumentUsedToPopulateTree() {
    return documentUsedToPopulateTree;
  }
  
  public XPath getCurrentXPathExpression() {
    return currentXPathExpression;
  }
  protected void setCurrentXPathExpression(XPath xpathExpression) {
    this.currentXPathExpression = xpathExpression;
  }
  
  
  public Map<String,String> getCurrentXPathNamespaces() {
    return currentXPathNamespaces;
  }
  
  
  
  protected void removeAllSelectionListeners()
  {
    this.allSelectionListeners = this.getTreeSelectionListeners();
    for (TreeSelectionListener listener : this.allSelectionListeners) {
      this.removeTreeSelectionListener(listener);
    }
  }
  
  protected void restoreAllSelectionListeners()
  {
    for (TreeSelectionListener listener : this.allSelectionListeners) {
      this.addTreeSelectionListener(listener);
    }
  }
  
  
  
  /**
   * Creates an instance of the XML tree from provided XML data.
   * 
   * @param xmlData XML document in the form of a <code>String</code> to
   *        derive the tree from.
   * @param bIncludeAttributesIntoTree
   * @param bIncludeValuesIntoTree
   * @param bIncludeElementNamespacesIntoTree
   * @param parentConfigPanel
   * @return
   * @throws DocumentException if <code>xmlData</code> does not
   *                           contain a valid XML document. 
   * 
   */
  public static XPathActivityXMLTree createFromXMLData(String xmlData, boolean bIncludeAttributesIntoTree,
                   boolean bIncludeValuesIntoTree, boolean bIncludeElementNamespacesIntoTree,
                   XPathActivityConfigurationPanel parentConfigPanel) throws DocumentException
  {
    // ----- XML DOCUMENT PARSING -----
    // try to parse the XML document - the next line will throw an exception if
    // the document is not well-formed; proceed otherwise
    Document doc = DocumentHelper.parseText(xmlData);
    Element rootElement = doc.getRootElement();
    
    
    // ----- POPULATE XML TREE -----
    XPathActivityXMLTreeElementNode rootNode = new XPathActivityXMLTreeElementNode(rootElement);
    populate(rootNode, rootElement, bIncludeAttributesIntoTree);
    
    return (new XPathActivityXMLTree(rootNode, doc, bIncludeValuesIntoTree, bIncludeElementNamespacesIntoTree, parentConfigPanel));
  }
  
  
  /**
   * Worker method for populating the tree recursively from a list of Elements.
   * 
   * @param node
   * @param element
   */
  private static void populate(DefaultMutableTreeNode node, Element element,
                               boolean bIncludeAttributesIntoTree)
  {
    Iterator<Element> elementIterator = element.elements().iterator();
    while (elementIterator.hasNext()) {
      Element childElement = elementIterator.next();
      XPathActivityXMLTreeElementNode childNode = new XPathActivityXMLTreeElementNode(childElement);
      node.add(childNode);
      
      // recursively repeat for all children of the current child element
      populate(childNode, childElement, bIncludeAttributesIntoTree);
    }
    
    
    // add attributes of the element as its children, if necessary
    if (bIncludeAttributesIntoTree) {
      List<Attribute> attributes = element.attributes();
      for (Attribute attribute : attributes) {
        node.add(new XPathActivityXMLTreeAttributeNode(attribute));
      }
    }
  }
  
  
  // ---------------- RESPONDING TO REQUESTS TO CHANGE APPEARANCE OF EXISTING TREE -----------------
  
  /**
   * NB! May be inefficient, as this solution re-generates the whole tree from
   *     stored XML document and replaces the root node of itself with a newly
   *     generated root node (that will be populated with updated children,
   *     according to the new values of options).
   *  
   *     However, this is a simple solution that will work for now.
   * 
   * @param bIncludeAttributes
   * @param bIncludeValues
   * @param bIncludeNamespaces
   */
  public void refreshFromExistingDocument(boolean bIncludeAttributes, boolean bIncludeValues, boolean bIncludeNamespaces)
  {
    this.setEnabled(false);
    removeAllSelectionListeners();
    
    // store expansion and selection state of the XML tree
    // see documentation for restoreExpandedPaths() for more details
    //
    // stored paths to expanded nodes are quite reliable, as paths are recorded;
    // stored selected rows are less reliable, as only indices are kept -- however,
    // the tree is re-created from the same document, so ordering/number of nodes
    // cannot change (apart from attributes that may be added / removed - the attributes
    // appear after other child nodes of some node in the tree, therefore only their
    // selection could be affected)
    HashMap<String,ArrayList<String>> toExpand = new HashMap<String,ArrayList<String>>();
    ArrayList<Integer> toSelect = new ArrayList<Integer>();
    for( int i = 1; i < this.getRowCount(); i++) {
      if( this.isExpanded(i) ) {
        TreePath path = this.getPathForRow(i);
        String parentPath = path.getParentPath().toString();
        ArrayList<String> values = toExpand.get(parentPath);
        if(values == null) {
          values = new ArrayList<String>();
        }
        values.add(path.getLastPathComponent().toString());
        toExpand.put(parentPath, values);
      }
      if (this.isRowSelected(i)) {
        toSelect.add(i);
      }
    }
    
    
    // update presentation options
    this.treeRenderer.setIncludeElementValues(bIncludeValues);
    this.treeRenderer.setIncludeElementNamespaces(bIncludeNamespaces);
    
    // re-create the root node of the tree and replace the old one with it
    Element rootElement = this.documentUsedToPopulateTree.getRootElement();
    XPathActivityXMLTreeNode newRootNode = new XPathActivityXMLTreeElementNode(rootElement);
    populate(newRootNode, rootElement, bIncludeAttributes);
    ((DefaultTreeModel)this.getModel()).setRoot(newRootNode);
    
    
    // restore previous state of the tree from saved values
    restoreExpandedPaths(toExpand, this.getPathForRow(0));
    restoreSelectedPaths(toSelect);
    
    this.restoreAllSelectionListeners();
    this.setEnabled(true);
  }
  
  
  /**
   * This method can only reliably work when the tree is re-generated from the same
   * XML document, so that number / order of nodes would not change.
   * 
   * @param toSelect List of indices of rows to re-select after tree was re-generated.
   */
  private void restoreSelectedPaths(ArrayList<Integer> toSelect)
  {
    if (toSelect == null || toSelect.isEmpty()) return;
    
    // something definitely needs to be selected, so include root element into selection
    this.addSelectionRow(0);
    
    // select all stored rows
    for (Integer value : toSelect) {
      this.addSelectionRow(value);
    }
  }



  /**
   * Taken from: <a href="http://java.itags.org/java-core-gui-apis/58504/">http://java.itags.org/java-core-gui-apis/58504/</a>
   * 
   * This method recursively expands all previously stored paths.
   * Works under assumption that the name of the root node did not change.
   * Otherwise, it can handle changed structure of the tree.
   * 
   * To achieve its goal, it cannot simply use stored TreePath from your the original tree,
   * since the paths are invalid after the tree is refreshed. Instead, a HashMap which links
   * a String representation of the parent tree path to all expanded child node names is used.
   * 
   * @param toExpand Map which links a String representation of the parent tree path to all
   *                 expanded child node names is used.
   * @param rootPath Path to root node.
   */
  void restoreExpandedPaths(HashMap<String,ArrayList<String>> toExpand, TreePath rootPath)
  {
    ArrayList<String> values = toExpand.remove(rootPath.toString());
    if (values == null) return;
    
    int row = this.getRowForPath(rootPath);
    for (String value : values)
    {
      TreePath nextMatch = this.getNextMatch(value, row, Position.Bias.Forward);
      this.expandPath(nextMatch);
      if (toExpand.containsKey(nextMatch.toString())) {
        restoreExpandedPaths(toExpand, nextMatch);
      }
    }
  }
  
  
  
  // ---------------- TREE SELECTION MODEL + XPath GENERATION -----------------
  
  
  protected String generateXPathFromTreePath(TreePath path)
  {
    StringBuilder xpath = new StringBuilder();
    
    for (String leg : generateXPathFromTreePathAsLegList(path)) {
      xpath.append(leg);
    }
    
    return (xpath.toString());
  }
  
  
  protected List<String> generateXPathFromTreePathAsLegList(TreePath path)
  {
    List<String> pathLegs = new LinkedList<String>();
    
    TreePath parentPath = path;
    for (int i = 0; i < path.getPathCount(); i++)
    {
      XPathActivityXMLTreeNode lastXMLTreeNodeInThisPath = (XPathActivityXMLTreeNode)parentPath.getLastPathComponent();
      pathLegs.add(0, this.getXMLTreeNodeEffectiveQualifiedNameAsXPathLeg(lastXMLTreeNodeInThisPath));
      
      parentPath = parentPath.getParentPath();
    }
    
    return (pathLegs);
  }
  
  
  protected String getXMLTreeNodeEffectiveQualifiedNameAsXPathLeg(XPathActivityXMLTreeNode node)
  {
    QName qname = node.getNodeQName();
    String effectiveNamespacePrefix = addNamespaceToXPathMap(qname.getNamespace());
    
    return("/" +
           (node.isAttribute() ? "@" : "") +
           (effectiveNamespacePrefix.length() > 0 ? (effectiveNamespacePrefix + ":") : "") +
           qname.getName());
  }
  
  
  
  private String addNamespaceToXPathMap(Namespace namespace) 
  {
    // EMTPY PREFIX
    if (namespace.getPrefix().length() == 0) {
      if (namespace.getURI().length() == 0) {
        // DEFAULT NAMESPACE with no URI - nothing to worry about
        return "";
      }
      else {
        // DEFAULT NAMESPACE WITH NO PREFIX, BUT URI IS KNOWN
        return (addNamespaceToXPathMap(new Namespace("default", namespace.getURI())));
      }
    }
    
    // NEW NON-EMPTY PREFIX
    if (!this.currentXPathNamespaces.containsKey(namespace.getPrefix())) {
      this.currentXPathNamespaces.put(namespace.getPrefix(), namespace.getURI());
      return (namespace.getPrefix());
    }
    
    // EXISTING NON-EMPTY PREFIX AND THE SAME URI - NO NEED TO ADD AGAIN
    else if (this.currentXPathNamespaces.get(namespace.getPrefix()).equals(namespace.getURI())) {
      return (namespace.getPrefix());
    }
    
    // EXISTING NON-EMPTY PREFIX, BUT DIFFERENT URI
    else {
      String repeatedPrefix = namespace.getPrefix();
      
      int i = 0;
      while (this.currentXPathNamespaces.containsKey(repeatedPrefix + i)) {
        // check if current alternative prefix wasn't yet applied to current URI
        if (this.currentXPathNamespaces.get(repeatedPrefix + i).equals(namespace.getURI())) {
          return (repeatedPrefix + i);
        }
        else {
          // still another URI for the same prefix, keep trying to increase the ID in the prefix
          i++;
        }
      }
      
      String modifiedPrefix = repeatedPrefix + i;
      this.currentXPathNamespaces.put(modifiedPrefix, namespace.getURI());
      return (modifiedPrefix);
    }
  }
  
  
  // ----------------------- Tree Cell Renderer --------------------------
  
  /**
   * 
   * @author Sergejs Aleksejevs
   */
  private class XPathActivityXMLTreeRenderer extends DefaultTreeCellRenderer
  {
    private boolean bIncludeElementValues;
    private boolean bIncludeElementNamespaces;
    
    public XPathActivityXMLTreeRenderer(boolean bIncludeElementValues, boolean bIncludeElementNamespaces) {
      super();
      this.bIncludeElementValues = bIncludeElementValues;
      this.bIncludeElementNamespaces = bIncludeElementNamespaces;
    }
    
    
    public boolean getIncludeElementValues() {
      return bIncludeElementValues;
    }
    public void setIncludeElementValues(boolean bIncludeElementValues) {
      this.bIncludeElementValues = bIncludeElementValues;
    }
    
    public boolean getIncludeElementNamespaces() {
      return bIncludeElementNamespaces;
    }
    public void setIncludeElementNamespaces(boolean bIncludeElementNamespaces) {
      this.bIncludeElementNamespaces = bIncludeElementNamespaces;
    }
    
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean selected, boolean expanded, boolean leaf, int row,
        boolean hasFocus)
    {
      // obtain the default rendering, we'll then customize it
      Component defaultRendering = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
      
      // it is most likely that the default rendering will be a JLabel, check just to be safe
      if (defaultRendering instanceof JLabel)
      {
        JLabel defaultRenderedLabel = ((JLabel)defaultRendering);
        
        // ---------- CHOOSE APPROPRIATE ICON FOR THE NODE ------------
        if (row == 0) {
          // set the icon for the XML tree root node
          defaultRenderedLabel.setIcon(XPathActivityIcon.getIconById(XPathActivityIcon.XML_TREE_ROOT_ICON));
        }
        else {
          // set the icon for the XML tree node
          if (value instanceof XPathActivityXMLTreeNode && 
              ((XPathActivityXMLTreeNode)value).isAttribute())
          {
            defaultRenderedLabel.setIcon(XPathActivityIcon.getIconById(XPathActivityIcon.XML_TREE_ATTRIBUTE_ICON));
          }
          else {
            defaultRenderedLabel.setIcon(XPathActivityIcon.getIconById(XPathActivityIcon.XML_TREE_NODE_ICON));
          }
        }
        
        
        // ----------- CHOOSE THE DISPLAY TITLE FOR THE NODE ------------
        if (value instanceof XPathActivityXMLTreeNode) {
          defaultRenderedLabel.setText(((XPathActivityXMLTreeNode)value).getTreeNodeDisplayLabel(
              this.bIncludeElementValues, this.bIncludeElementNamespaces, true));
        }
      }
      
      return (defaultRendering);
    }
    
  }
}
