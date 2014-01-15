package net.sf.taverna.t2.activities.xpath.ui.config;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import net.sf.taverna.t2.activities.xpath.XPathActivityConfigurationBean;
import net.sf.taverna.t2.activities.xpath.ui.config.xmltree.TableCellListener;
import net.sf.taverna.t2.activities.xpath.ui.config.xmltree.XPathActivityXMLTree;
import net.sf.taverna.t2.activities.xpath.ui.servicedescription.XPathActivityIcon;
import net.sf.taverna.t2.workbench.icons.WorkbenchIcons;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.XPathException;

/**
 * @author Sergejs Aleksejevs
 */
@SuppressWarnings("serial")
public class XPathActivityConfigurationPanel extends JPanel {

	private Logger logger = Logger.getLogger(XPathActivityConfigurationPanel.class);

	// --- CONSTANTS ---
	public static final int MAX_NUMBER_OF_MATCHING_NODES_TO_HIGHLIGHT_IN_THE_TREE = 100;

	private static final Color INACTIVE_PANEL_BACKGROUND_COLOR = new Color(215,
			215, 215);

	private static final String EXAMPLE_XML_PROMPT = "Paste example XML here...";

	private static final String XPATH_XML_DOCUMENT_DIR_PROPERTY="XPathXMLDocumentDir";

	private XPathActivityConfigurationPanel thisPanel;

	// --- COMPONENTS FOR ACTIVITY CONFIGURATION PANEL ---
	private JPanel jpActivityConfiguration;

	private JPanel jpLeft;
	private JPanel jpRight;

	private JToggleButton bShowXMLTreeSettings;
	private Popup xmlTreeSettingsMenu;
	private long xmlTreeSettingsMenuLastShownAt;
	private JButton bGenerateXPathExpression;
	private JPanel jpXMLTreeSettingsMenuContents;
	private JCheckBoxMenuItem miIncludeAttributes;
	private JCheckBoxMenuItem miIncludeValues;
	private JCheckBoxMenuItem miIncludeNamespaces;

	private JTextArea taSourceXML;
	private JButton bLoadXMLDocument;
	private JButton bParseXML;
	private XPathActivityXMLTree xmlTree;
	private JScrollPane spXMLTreePlaceholder;

	// --- COMPONENTS FOR XPATH EDITING PANEL ---
	private JLabel jlXPathExpressionStatus;
	private JLabel jlXPathExpression;
	private JTextField tfXPathExpression;
	private Map<String, String> xpathNamespaceMap;
	private JButton bRunXPath;

	private JLabel jlShowHideNamespaceMappings;
	private JTable jtXPathNamespaceMappings;
	private JButton bAddMapping;
	private JButton bRemoveMapping;
	private JPanel jpNamespaceMappingsWithButton;

	// --- COMPONENTS FOR XPATH TESTING PANEL ---
	private JPanel jpXPathTesting;

	private JTextField tfExecutedXPathExpression;
	private JTextField tfMatchingElementCount;

	private JTabbedPane tpExecutedXPathExpressionResults;
	private JTextArea taExecutedXPathExpressionResultsAsText;
	private JScrollPane spExecutedXPathExpressionResultsAsText;
	private JTextArea taExecutedXPathExpressionResultsAsXML;
	private JScrollPane spExecutedXPathExpressionResultsAsXML;

	public XPathActivityConfigurationPanel() {
		this.thisPanel = this;

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.50;
		c.insets = new Insets(0, 10, 10, 10);
		this.jpActivityConfiguration = createActivityConfigurationPanel();
		this.add(this.jpActivityConfiguration, c);

		c.gridy++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.insets = new Insets(0, 10, 0, 10);
		this.add(new JSeparator(), c);

		// XPath expression editing panel
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.05;
		c.insets = new Insets(5, 10, 5, 10);
		this.add(createXPathExpressionEditingPanel(), c);

		c.gridy++;
		;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.insets = new Insets(0, 10, 0, 10);
		this.add(new JSeparator(), c);

		// XPath expression testing panel
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.35;
		c.insets = new Insets(5, 10, 0, 10);
		this.jpXPathTesting = createXPathExpressionTestingPanel();
		this.add(this.jpXPathTesting, c);
	}

	private JPanel createActivityConfigurationPanel() {
		JPanel jpConfig = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// text area for example XML document
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1.0;
		c.insets = new Insets(5, 0, 0, 5);
		taSourceXML = new JTextArea(10, 30);
		taSourceXML
				.setToolTipText("<html>Use this text area to paste or load an example XML document.<br>"
						+ "This document can then be parsed by clicking the button<br>"
						+ "with a green arrow in order to see its tree structure.</html>");
		taSourceXML.setText(EXAMPLE_XML_PROMPT);
		taSourceXML.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				taSourceXML.selectAll();
			}

			public void focusLost(FocusEvent e) { /* do nothing */
			}
		});
		taSourceXML.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				// make sure that it is only allowed to "parse example XML"
				// when something is actually present in the text area
				bParseXML.setEnabled(taSourceXML.getText().trim().length() > 0
						&& !taSourceXML.getText().trim().equals(
								EXAMPLE_XML_PROMPT));
			}
		});
		jpLeft = new JPanel(new GridLayout(1, 1));
		jpLeft.add(new JScrollPane(taSourceXML));
		jpConfig.add(jpLeft, c);

		// button to parse example XML document

		c.gridx++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 0, 0);
		bParseXML = new JButton(
				XPathActivityIcon
						.getIconById(XPathActivityIcon.XPATH_ACTIVITY_CONFIGURATION_PARSE_XML_ICON));
		bParseXML
				.setToolTipText("Parse example XML document and generate its tree structure");
		bParseXML.setEnabled(false);
		bParseXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseXML();
			}
		});
		jpConfig.add(bParseXML, c);

		// placeholder for XML tree (will be replaced by a real tree when the
		// parsing is done)

		c.gridx++;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.5;
		c.weighty = 1.0;
		c.insets = new Insets(5, 5, 0, 0);
		JTextArea taXMLTreePlaceholder = new JTextArea(10, 30);
		taXMLTreePlaceholder
				.setToolTipText("<html>This area will show tree structure of the example XML after you<br>"
						+ "paste it into the space on the left-hand side and press 'Parse'<br>"
						+ "button with the green arrow.</html>");
		taXMLTreePlaceholder.setEditable(false);
		taXMLTreePlaceholder.setBackground(INACTIVE_PANEL_BACKGROUND_COLOR);
		spXMLTreePlaceholder = new JScrollPane(taXMLTreePlaceholder);
		jpRight = new JPanel(new GridLayout(1, 1));
		jpRight.add(spXMLTreePlaceholder);
		jpConfig.add(jpRight, c);

		// Button to load XML document from a file
		
		bLoadXMLDocument = new JButton("Load XML from file", WorkbenchIcons.openIcon);	
		bLoadXMLDocument.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				Preferences prefs = Preferences.userNodeForPackage(getClass());
				String curDir = prefs.get(XPATH_XML_DOCUMENT_DIR_PROPERTY, System.getProperty("user.home"));
				fileChooser.setDialogTitle("Select file to load XML from");
				fileChooser.setFileFilter(new FileFilter() {  
					public boolean accept(File f) {
				        return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
				    }
				    
				    public String getDescription() {
				        return ".xml files";
				    }
				});
				fileChooser.setCurrentDirectory(new File(curDir));		
				int returnVal = fileChooser.showOpenDialog(((JButton) e
						.getSource()).getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					prefs.put(XPATH_XML_DOCUMENT_DIR_PROPERTY, fileChooser
							.getCurrentDirectory().toString());
					File file = fileChooser.getSelectedFile();
					// Read the contents of a file into a string
					// and set the value of the XML document text area to it
					FileInputStream fis = null;
					try{
						byte[] fileBytes = new byte[(int)file.length()];
						fis = new FileInputStream(file);
						fis.read(fileBytes);
						String xmlDocument = new String(fileBytes, "UTF-8");
						setSourceXML(xmlDocument);
					}
					catch(Exception ex){
						logger.error("An error occured while trying to read the XML document from file " + file.getAbsolutePath(), ex);
						JOptionPane.showMessageDialog(
								((JButton) e.getSource()).getParent(), 
								"There was an error while trying to read the file", 
								"XPath Activity", 
								JOptionPane.ERROR_MESSAGE);
					}
					finally{
						try {
							fis.close();
						} catch (IOException e1) {
							// Ignore
						}
					}

				}
			}
		});
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 0, 0, 5);
		c.anchor = GridBagConstraints.EAST;
		jpConfig.add(bLoadXMLDocument, c);
		
		// settings for the view of XML tree from example XML document

		miIncludeAttributes = new JCheckBoxMenuItem("Show XML node attributes");
		miIncludeAttributes.setSelected(true);
		miIncludeAttributes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshXMLTreeUI();
			}
		});

		miIncludeValues = new JCheckBoxMenuItem(
				"Show values of XML elements and attributes");
		miIncludeValues.setSelected(true);
		miIncludeValues.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshXMLTreeUI();
			}
		});

		miIncludeNamespaces = new JCheckBoxMenuItem(
				"Show namespaces of XML elements");
		miIncludeNamespaces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshXMLTreeUI();
			}
		});

		jpXMLTreeSettingsMenuContents = new JPanel();
		jpXMLTreeSettingsMenuContents.setBorder(BorderFactory
				.createRaisedBevelBorder());
		jpXMLTreeSettingsMenuContents.setLayout(new BoxLayout(
				jpXMLTreeSettingsMenuContents, BoxLayout.Y_AXIS));
		jpXMLTreeSettingsMenuContents.add(miIncludeAttributes);
		jpXMLTreeSettingsMenuContents.add(miIncludeValues);
		jpXMLTreeSettingsMenuContents.add(miIncludeNamespaces);

		bShowXMLTreeSettings = new JToggleButton("Show XML tree settings...",
				XPathActivityIcon.getIconById(XPathActivityIcon.UNFOLD_ICON));
		bShowXMLTreeSettings.setSelectedIcon(XPathActivityIcon
				.getIconById(XPathActivityIcon.FOLD_ICON));
		bShowXMLTreeSettings.setEnabled(false);
		bShowXMLTreeSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (xmlTreeSettingsMenu == null) {
					xmlTreeSettingsMenuLastShownAt = System.currentTimeMillis();

					Point parentPosition = bShowXMLTreeSettings
							.getLocationOnScreen();
					xmlTreeSettingsMenu = PopupFactory.getSharedInstance()
							.getPopup(
									bShowXMLTreeSettings,
									jpXMLTreeSettingsMenuContents,
									parentPosition.x,
									parentPosition.y
											+ bShowXMLTreeSettings.getHeight());
					xmlTreeSettingsMenu.show();
				} else {
					bShowXMLTreeSettings.setSelected(false);
				}
			}
		});
		
		bGenerateXPathExpression = new JButton("Generate XPath expression",
				XPathActivityIcon
						.getIconById(XPathActivityIcon.XML_TREE_NODE_ICON));
		bGenerateXPathExpression.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			     updateXPathEditingPanelValues();	
			}	
		});
		
		JPanel xmlTreeButtonPanel = new JPanel();
		xmlTreeButtonPanel.add(bGenerateXPathExpression);
		xmlTreeButtonPanel.add(bShowXMLTreeSettings);
		
		c.gridx = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(5, 0, 0, 0);
		c.anchor = GridBagConstraints.EAST;
		jpConfig.add(xmlTreeButtonPanel, c);

		// register a new listener for all AWT mouse events - this will be used
		// to identify clicks outside of the XML tree popup menu and the toggle
		// button used to show/hide it
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			public void eventDispatched(AWTEvent event) {
				if (event instanceof MouseEvent && xmlTreeSettingsMenu != null) {
					MouseEvent e = (MouseEvent) event;
					if (e.getClickCount() > 0
							&& (e.getWhen() - xmlTreeSettingsMenuLastShownAt) > 100) {
						// convert a point where mouse click was made from
						// relative coordinates of the source component
						// to the coordinates of the panel that represents the
						// contents of the popup menu
						Point clickRelativeToOverlay = SwingUtilities
								.convertPoint((Component) e.getSource(), e
										.getPoint(),
										jpXMLTreeSettingsMenuContents);

						Area areaOfPopupPanelAndToggleButton = new Area(
								jpXMLTreeSettingsMenuContents.getBounds());

						// only hide the popup menu if a click was made outside
						// of the calculated area --
						// plus not on one of the associated toggle buttons
						if (!areaOfPopupPanelAndToggleButton
								.contains(clickRelativeToOverlay)) {
							xmlTreeSettingsMenu.hide();
							bShowXMLTreeSettings.setSelected(false);

							// if the popup menu was dismissed by a click on the
							// toggle button that
							// has made it visible, this timer makes sure that
							// this click doesn't
							// re-show the popup menu
							new Timer(100, new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									((Timer) e.getSource()).stop();
									xmlTreeSettingsMenu = null;
								}
							}).start();

						}
					}
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);

		return (jpConfig);
	}

	private JPanel createXPathExpressionEditingPanel() {
		this.jlXPathExpressionStatus = new JLabel();

		this.jlXPathExpression = new JLabel("XPath expression");

		this.bRunXPath = new JButton("Run XPath");
		this.bRunXPath.setEnabled(false);
		this.bRunXPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runXPath(true);
			}
		});

		this.tfXPathExpression = new JTextField(30);
		this.tfXPathExpression.setPreferredSize(new Dimension(0, this.bRunXPath
				.getPreferredSize().height));
		this.tfXPathExpression.setMinimumSize(new Dimension(0, this.bRunXPath
				.getPreferredSize().height));
		this.tfXPathExpression.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				validateXPathAndUpdateUI();
			}
		});
		this.tfXPathExpression.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (bRunXPath.isEnabled()) {
						// it is safe to check that ENTER key may execute the
						// XPath expression if the
						// "Run XPath" button is enabled, as expression
						// validation is responsible for
						// enabling / disabling the button as the expression
						// changes
						runXPath(true);
					}
				}
			}

			public void keyReleased(KeyEvent e) { /* not in use */
			}

			public void keyTyped(KeyEvent e) { /* not in use */
			}
		});

		JPanel jpXPath = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		jpXPath.add(jlXPathExpressionStatus);

		c.gridx++;
		c.weightx = 0.0;
		c.insets = new Insets(0, 10, 0, 0);
		jpXPath.add(jlXPathExpression, c);
		
		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(0, 10, 0, 10);
		jpXPath.add(tfXPathExpression, c);

		c.gridx++;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		jpXPath.add(bRunXPath, c);

		c.gridx = 2;
		c.gridy++;
		c.weightx = 1.0;
		c.weighty = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 10, 0, 10);
		jlShowHideNamespaceMappings = new JLabel("Show namespace mappings...");
		jlShowHideNamespaceMappings.setForeground(Color.BLUE);
		jlShowHideNamespaceMappings.setCursor(new Cursor(Cursor.HAND_CURSOR));
		jlShowHideNamespaceMappings.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				jpNamespaceMappingsWithButton
						.setVisible(!jpNamespaceMappingsWithButton.isVisible());
				jlShowHideNamespaceMappings
						.setText((jpNamespaceMappingsWithButton.isVisible() ? "Hide"
								: "Show")
								+ " namespace mappings...");
				thisPanel.validate();
			}
		});
		jpXPath.add(jlShowHideNamespaceMappings, c);

		// namespace mapping table
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Namespace Prefix");
		tableModel.addColumn("Namespace URI");

		jtXPathNamespaceMappings = new JTable();
		jtXPathNamespaceMappings.setModel(tableModel);
		// ((DefaultCellEditor)jtXPathNamespaceMappings.getDefaultEditor(String.class)).setClickCountToStart(1);
		// // TODO - enable if one-click-to-start-editing behaviour is required
		// TODO - next line is to be enabled when Taverna is migrated to Java
		// 1.6; for now it's fine to run without this
		// jtXPathNamespaceMappings.setFillsViewportHeight(true); // makes sure
		// that when the dedicated area is larger than the table, the latter is
		// stretched vertically to fill the empty space
		jtXPathNamespaceMappings
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // only one row can be selected at a time
		jtXPathNamespaceMappings
				.setPreferredScrollableViewportSize(new Dimension(200, 50)); // NB! this prevents the table from occupying most of the space in the panel when screen is maximized
		jtXPathNamespaceMappings.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					removeNamespaceMapping();
				}
			}
		});

		TableCellListener cellListener = new TableCellListener(
				jtXPathNamespaceMappings, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						TableCellListener tcl = (TableCellListener) e
								.getSource();

						if (tcl.getColumn() == 0) {
							// prefix was modified
							String newPrefix = (String) tcl.getNewValue();
							if (xpathNamespaceMap.containsKey(newPrefix)) {
								// such prefix already exists - change won't be
								// saved
								JOptionPane
										.showMessageDialog(
												thisPanel,
												"Cannot update namespace prefix: "
														+ "updated value already exists",
												"XPath Activity",
												JOptionPane.WARNING_MESSAGE);
							} else {
								// update the map with the new prefix for the
								// same URI value
								String oldPrefix = (String) tcl.getOldValue();
								xpathNamespaceMap.put(newPrefix,
										xpathNamespaceMap.remove(oldPrefix));
							}
						} else {
							// simple case - just the URI value has changed:
							// just overwrite the value in the namespace map
							String prefixOfUpdatedURI = (String) jtXPathNamespaceMappings
									.getModel().getValueAt(tcl.getRow(), 0);
							xpathNamespaceMap.put(prefixOfUpdatedURI,
									(String) tcl.getNewValue());
						}

						// either way - reload from the local map (map could be
						// not updated if the validation didn't succeed)
						reloadNamespaceMappingTableFromLocalMap();
					}
				});

		jtXPathNamespaceMappings.getColumnModel().getColumn(0)
				.setPreferredWidth(20); // set relative sizes of columns
		jtXPathNamespaceMappings.getColumnModel().getColumn(1)
				.setPreferredWidth(300);

		JScrollPane spXPathNamespaceMappings = new JScrollPane(
				jtXPathNamespaceMappings);
		spXPathNamespaceMappings.setAlignmentY(TOP_ALIGNMENT);
		spXPathNamespaceMappings.setMinimumSize(new Dimension(200, 50)); // makes the table to have at least two rows visible in all cases - no matter how small the parent panel is

		bAddMapping = new JButton("Add Mapping");
		bAddMapping.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNamespaceMapping();
			}
		});

		bRemoveMapping = new JButton("Remove Mapping");
		bRemoveMapping.setEnabled(false);
		bRemoveMapping.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeNamespaceMapping();
			}
		});

		bAddMapping.setMinimumSize(bRemoveMapping.getPreferredSize()); // make sure that the 'Add Mapping' button is of the same size as 'Remove Mapping'
															
		bAddMapping.setPreferredSize(bRemoveMapping.getPreferredSize()); // -- both are required to achieve desired behaviour when window is resized / namespace mapping table is enabled/disabled 												

		bRunXPath.setMinimumSize(bRemoveMapping.getPreferredSize()); // do the same for 'Run XPath' button
		
		bRunXPath.setPreferredSize(bRemoveMapping.getPreferredSize());

		JPanel jpAddRemoveButtons = new JPanel();
		jpAddRemoveButtons.setLayout(new GridBagLayout());
		GridBagConstraints cAddRemove = new GridBagConstraints();
		cAddRemove.gridx = 0;
		cAddRemove.gridy = 0;
		cAddRemove.weightx = 1.0;
		cAddRemove.anchor = GridBagConstraints.NORTH;
		cAddRemove.fill = GridBagConstraints.HORIZONTAL;
		jpAddRemoveButtons.add(bAddMapping, cAddRemove);
		cAddRemove.gridy++;
		cAddRemove.weighty = 1.0;
		cAddRemove.insets = new Insets(2, 0, 0, 0);
		jpAddRemoveButtons.add(bRemoveMapping, cAddRemove);

		jpNamespaceMappingsWithButton = new JPanel();
		jpNamespaceMappingsWithButton.setVisible(false);
		jpNamespaceMappingsWithButton.setLayout(new BorderLayout(10, 0));
		jpNamespaceMappingsWithButton.add(spXPathNamespaceMappings,
				BorderLayout.CENTER);
		jpNamespaceMappingsWithButton
				.add(jpAddRemoveButtons, BorderLayout.EAST);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(5, 0, 0, 0);
		jpXPath.add(jpNamespaceMappingsWithButton, c);

		// initialise some values / tooltips
		resetXPathEditingPanel();

		return (jpXPath);
	}

	protected void addNamespaceMapping() {
		TwoFieldQueryPanel queryPanel = new TwoFieldQueryPanel(
				"Namespace prefix:", "Namespace URI:");
		int result = JOptionPane.showConfirmDialog(this, queryPanel,
				"XPath Activity - Create new namespace mapping",
				JOptionPane.OK_CANCEL_OPTION);

		if (result == JOptionPane.OK_OPTION) {
			boolean bInvalidMapping = true;
			do {
				bInvalidMapping = queryPanel.getFirstValue().length() == 0
						|| queryPanel.getSecondValue().length() == 0
						|| xpathNamespaceMap.containsKey(queryPanel
								.getFirstValue());

				if (bInvalidMapping) {
					queryPanel = new TwoFieldQueryPanel(
							"<html><center><font color=\"red\">ERROR: you must "
									+ "enter values for both namespace prefix and URI. Prefix must be<br>"
									+ "unique in the mapping table - duplicates are not allowed!</font></center></html>",
							"Namespace prefix:", queryPanel.getFirstValue(),
							"Namespace URI:", queryPanel.getSecondValue());
					result = JOptionPane.showConfirmDialog(this, queryPanel,
							"XPath Activity - Create new namespace mapping",
							JOptionPane.OK_CANCEL_OPTION);
				}
			} while (bInvalidMapping && result == JOptionPane.OK_OPTION);

			if (result == JOptionPane.OK_OPTION && !bInvalidMapping) {
				// the value appears to be valid and OK was pressed - create new
				// mapping
				this.xpathNamespaceMap.put(queryPanel.getFirstValue(),
						queryPanel.getSecondValue());
				reloadNamespaceMappingTableFromLocalMap();
			}
		}
	}

	protected void removeNamespaceMapping() {
		int selectedRow = jtXPathNamespaceMappings.getSelectedRow();
		if (selectedRow != -1) {
			// some row is selected - need to delete it and refresh table's UI
			// (but first stop editing to avoid
			// problems with cell editor trying to store an edited value after
			// edited row has been deleted)
			if (jtXPathNamespaceMappings.getCellEditor() != null) {
				jtXPathNamespaceMappings.getCellEditor().stopCellEditing();
			}
			xpathNamespaceMap.remove(jtXPathNamespaceMappings.getValueAt(
					selectedRow, 0));
			reloadNamespaceMappingTableFromLocalMap();

			// select another row in the table
			int rowCount = jtXPathNamespaceMappings.getRowCount();
			if (rowCount > 0) {
				if (selectedRow < jtXPathNamespaceMappings.getRowCount()) {
					// select the row that followed the one that was deleted
					jtXPathNamespaceMappings.getSelectionModel()
							.setSelectionInterval(selectedRow, selectedRow);
				} else {
					// last row in the table was deleted - select the one that
					// is the new last row
					jtXPathNamespaceMappings.getSelectionModel()
							.setSelectionInterval(rowCount - 1, rowCount - 1);
				}
			}
		} else {
			JOptionPane.showMessageDialog(thisPanel,
					"Please select a mapping to delete in the table first!",
					"XPath Activity", JOptionPane.WARNING_MESSAGE);
		}
	}

	private JPanel createXPathExpressionTestingPanel() {
		JPanel jpTesting = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 10);
		jpTesting.add(new JLabel("Executed XPath expression:"), c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 10, 10);
		tfExecutedXPathExpression = new JTextField();
		tfExecutedXPathExpression.setEditable(false);
		tfExecutedXPathExpression.setBorder(null);
		jpTesting.add(tfExecutedXPathExpression, c);

		c.gridx = 0;
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 5, 10);
		jpTesting.add(new JLabel("Number of matching nodes:"), c);

		c.gridx++;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 0;
		c.insets = new Insets(0, 0, 5, 10);
		tfMatchingElementCount = new JTextField();
		tfMatchingElementCount.setEditable(false);
		tfMatchingElementCount.setBorder(null);
		jpTesting.add(tfMatchingElementCount, c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		tpExecutedXPathExpressionResults = new JTabbedPane();
		jpTesting.add(tpExecutedXPathExpressionResults, c);

		taExecutedXPathExpressionResultsAsText = new JTextArea();
		taExecutedXPathExpressionResultsAsText.setEditable(false);
		spExecutedXPathExpressionResultsAsText = new JScrollPane(
				taExecutedXPathExpressionResultsAsText);
		spExecutedXPathExpressionResultsAsText.setPreferredSize(new Dimension(
				200, 60));
		spExecutedXPathExpressionResultsAsText.setBorder(BorderFactory
				.createLineBorder(INACTIVE_PANEL_BACKGROUND_COLOR, 3));
		tpExecutedXPathExpressionResults.add("Results as text",
				spExecutedXPathExpressionResultsAsText);

		taExecutedXPathExpressionResultsAsXML = new JTextArea();
		taExecutedXPathExpressionResultsAsXML.setEditable(false);
		spExecutedXPathExpressionResultsAsXML = new JScrollPane(
				taExecutedXPathExpressionResultsAsXML);
		spExecutedXPathExpressionResultsAsXML.setPreferredSize(new Dimension(
				200, 60));
		spExecutedXPathExpressionResultsAsXML.setBorder(BorderFactory
				.createLineBorder(INACTIVE_PANEL_BACKGROUND_COLOR, 3));
		tpExecutedXPathExpressionResults.add("Results as XML",
				spExecutedXPathExpressionResultsAsXML);

		// initialise some values / tooltips
		resetXPathTestingPanel();

		return (jpTesting);
	}

	protected void parseXML() {
		String xmlData = taSourceXML.getText();

		try {
			xmlTree = XPathActivityXMLTree.createFromXMLData(xmlData,
					miIncludeAttributes.isSelected(), miIncludeValues
							.isSelected(), miIncludeNamespaces.isSelected(),
					this);
			xmlTree
					.setToolTipText("<html>This is a tree structure of the XML document that you have pasted.<br><br>"
							+ "Clicking on the nodes in this tree will automatically generate a<br>"
							+ "corresponding XPath expression. Multiple <b>identical</b> nodes can<br>"
							+ "be selected at once - in this case <b>wildcards</b> will be used in the<br>"
							+ "generated XPath expression to if selected nodes have different<br>"
							+ "ancestors. Other nodes that match the generated XPath expression<br>"
							+ "will also be selected in the tree.<br><br>"
							+ "Contextual menu provides convenience methods for expanding or<br>"
							+ "collapsing the tree." + "</html>");
			xmlTree.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JScrollPane spXMLTree = new JScrollPane(xmlTree);
			spXMLTree.setPreferredSize(spXMLTreePlaceholder.getPreferredSize());
			jpRight.removeAll();
			jpRight.add(spXMLTree);

			// all successful - enable options to modify the tree
			this.bShowXMLTreeSettings.setEnabled(true);

			// data structures inside the XML tree were reset (as the tree was
			// re-created) -
			// now reset the UI to the initial state as well
			resetXPathEditingPanel();
			resetXPathTestingPanel();

			// XML tree has pre-populated the namespace map with the namespaces
			// specified in the
			// root element of the tree - load these values
			updateXPathEditingPanelValues();

			this.validate();
			this.repaint();
		} catch (DocumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
					"XPath Activity", JOptionPane.ERROR_MESSAGE);
			this.taSourceXML.requestFocusInWindow();
			return;
		}
	}

	/**
	 * Makes the {@link XPathActivityXMLTree} to refresh its UI from the
	 * original XML document that was used to create it in first place.
	 * 
	 * The reason for using this method is to apply new options to the way the
	 * tree is rendered - e.g. attributes shown/hidden in the tree, values and
	 * namespaces shown/hidden, etc.
	 */
	protected void refreshXMLTreeUI() {
		this.xmlTree.refreshFromExistingDocument(this.miIncludeAttributes
				.isSelected(), this.miIncludeValues.isSelected(),
				this.miIncludeNamespaces.isSelected());
	}

	/**
	 * Initialises XPath Editing panel: -- resets XPath expression that is being
	 * shown; -- resets local copy of namespace map; -- resets UI of namespace
	 * mapping table;
	 */
	private void resetXPathEditingPanel() {
		tfXPathExpression.setText("");
		validateXPathAndUpdateUI();

		// clear the local copy of namespace map
		xpathNamespaceMap = new HashMap<String, String>();

		// clear the namespace mapping table and reload the data from the map
		DefaultTableModel tableModel = (DefaultTableModel) jtXPathNamespaceMappings
				.getModel();
		tableModel.getDataVector().removeAllElements();
	}

	/**
	 * Initialises XPath testing panel which shows results of executing current
	 * XPath expression against the example XML - the panel is returned to the
	 * way it looks when it is first loaded.
	 */
	private void resetXPathTestingPanel() {
		this.tfExecutedXPathExpression.setText("--");
		this.tfMatchingElementCount.setText("--");

		this.taExecutedXPathExpressionResultsAsText.setText("");
		this.taExecutedXPathExpressionResultsAsText
				.setBackground(INACTIVE_PANEL_BACKGROUND_COLOR);

		this.taExecutedXPathExpressionResultsAsXML.setText("");
		this.taExecutedXPathExpressionResultsAsXML
				.setBackground(INACTIVE_PANEL_BACKGROUND_COLOR);
	}

	public void updateXPathEditingPanelValues() {
		if (xmlTree.getCurrentXPathExpression() != null) {
			tfXPathExpression.setText(xmlTree.getCurrentXPathExpression()
					.getText());
		}

		// clear the local copy of namespace map and update it with all values
		// from
		// the map in XML tree instance (which was apparently just re-generated
		// on user request)
		xpathNamespaceMap.clear();
		xpathNamespaceMap.putAll(xmlTree.getCurrentXPathNamespaces());

		// clear the namespace mapping table and reload the data from the map
		reloadNamespaceMappingTableFromLocalMap();
	}

	protected void reloadNamespaceMappingTableFromLocalMap() {
		// clear the namespace mapping table and reload the data from the map
		DefaultTableModel tableModel = (DefaultTableModel) jtXPathNamespaceMappings
				.getModel();
		tableModel.getDataVector().removeAllElements();
		for (Map.Entry<String, String> mapping : this.xpathNamespaceMap
				.entrySet()) {
			tableModel.addRow(new Object[] { mapping.getKey(),
					mapping.getValue() });
		}

		bRemoveMapping.setEnabled(this.xpathNamespaceMap.entrySet().size() > 0);

		repaint();
	}

	private String getXPathValidationErrorMessage() {
		try {
			// try to parse the XPath expression...
			DocumentHelper.createXPath(tfXPathExpression.getText().trim());
			// ...success
			return ("");
		} catch (InvalidXPathException e) {
			// ...failed to parse the XPath expression: notify of the error
			return (e.getMessage());
		}
	}

	/**
	 * Validates the current XPath expression and updates UI accordingly: --
	 * XPath status icon is updated; -- tooltip for the icon explains the
	 * status; -- 'Run XPath' button is enabled/disabled depending on validity
	 * of XPath expression and existence of example data in the XML tree
	 */
	protected void validateXPathAndUpdateUI() {
		String candidatePath = tfXPathExpression.getText();
		int xpathStatus = XPathActivityConfigurationBean
				.validateXPath(candidatePath);

		switch (xpathStatus) {
		case XPathActivityConfigurationBean.XPATH_VALID:
			// success: expression is correct
			jlXPathExpressionStatus.setIcon(XPathActivityIcon
					.getIconById(XPathActivityIcon.XPATH_STATUS_OK_ICON));
			jlXPathExpressionStatus
					.setToolTipText("Current XPath expression is well-formed and valid");

			// could allow to execute against example XML, with only condition:
			// XML tree must be populated
			// (that is, there should be something to run the expression
			// against)
			if (xmlTree != null) {
				this.bRunXPath.setEnabled(true);
				this.bRunXPath
						.setToolTipText("<html>Evaluate current XPath expression against the XML document<br>"
								+ "whose structure is shown in the tree view above.</html>");
			} else {
				this.bRunXPath.setEnabled(false);
				this.bRunXPath
						.setToolTipText("<html>No XML document to evaluate the current XPath expression against.<br><br>"
								+ "Paste some example XML into the area in the top-left section of the<br>"
								+ "window, then parse it by clicking on the button with the green arrow<br>"
								+ "in order to test your XPath expression.</html>");
			}
			break;

		case XPathActivityConfigurationBean.XPATH_EMPTY:
			// no XPath expression - can't tell if it is correct + nothing to
			// execute
			jlXPathExpressionStatus.setIcon(XPathActivityIcon
					.getIconById(XPathActivityIcon.XPATH_STATUS_UNKNOWN_ICON));
			jlXPathExpressionStatus
					.setToolTipText("<html>There is no XPath expression to validate.<br><br>"
							+ "<b>Hint:</b> select something in the tree view showing the structure<br>"
							+ "of the XML document that you have pasted (or type the XPath<br>"
							+ "expression manually).</html>");
			this.bRunXPath.setEnabled(false);
			this.bRunXPath.setToolTipText("No XPath expression to execute");
			break;

		case XPathActivityConfigurationBean.XPATH_INVALID:
			// failed to parse the XPath expression: notify of the error
			jlXPathExpressionStatus.setIcon(XPathActivityIcon
					.getIconById(XPathActivityIcon.XPATH_STATUS_ERROR_ICON));
			jlXPathExpressionStatus
					.setToolTipText(getXPathValidationErrorMessage());

			this.bRunXPath.setEnabled(false);
			this.bRunXPath
					.setToolTipText("Cannot execute invalid XPath expression");
			break;
		}

	}

	/**
	 * Executes the current XPath expression against the current XML tree.
	 * 
	 * @param displayResults
	 *            <code>true</code> to execute and display results in the XPath
	 *            activity configuration panel (this happens when the 'Run
	 *            XPath' button is clicked);<br/>
	 *            <false> to run the expression quietly and simply return the
	 *            number of matching nodes.
	 * @return Number of nodes in the XML tree that match the current XPath
	 *         expression. (Or <code>-1</code> if an error has occurred during
	 *         the execution -- error messages will only be shown if
	 *         <code>displayResults == true</code>).
	 */
	public int runXPath(boolean displayResults) {
		// ----- RUNNING THE XPath EXPRESSION -----
		XPath expr = null;
		try {
			expr = DocumentHelper.createXPath(this.tfXPathExpression.getText());
			expr.setNamespaceURIs(this.xpathNamespaceMap);
		} catch (InvalidXPathException e) {
			if (displayResults) {
				JOptionPane
						.showMessageDialog(
								thisPanel,
								"Incorrect XPath Expression\n\n"
										+ "Please check the expression if you have manually modified it;\n"
										+ "Alternatively, try to select another node from the XML tree.\n\n"
										+ "------------------------------------------------------------------------------------\n\n"
										+ "XPath processing library reported the following error:\n"
										+ e.getMessage(), "XPath Activity",
								JOptionPane.ERROR_MESSAGE);
			}
			return (-1);
		}

		Document doc = xmlTree.getDocumentUsedToPopulateTree();
		List<Node> matchingNodes = null;
		int matchingNodeCount = -1;
		try {
			matchingNodes = expr.selectNodes(doc);
			matchingNodeCount = matchingNodes.size();
		} catch (XPathException e) {
			if (displayResults) {
				JOptionPane
						.showMessageDialog(
								thisPanel,
								"Unexpected error has occurred while executing the XPath expression.\n\n"
										+ "If you have manually modified the XPath expression and/or namespace mappings,\n"
										+ "please check you changes. Alternatively, make your selection in the XML tree and\n"
										+ "a correct XPath expression with corresponding namespace mapping will be generated.\n\n"
										+ "-------------------------------------------------------------------------------------------------------------\n\n"
										+ "XPath processing library reported the following error:\n"
										+ e.getMessage(), "XPath Activity",
								JOptionPane.ERROR_MESSAGE);
			}
			return (-1);
		}

		// ----- DISPLAYING THE RESULTS -----
		if (displayResults) {
			tfExecutedXPathExpression.setText(expr.getText());
			tfMatchingElementCount.setText("" + matchingNodeCount);

			StringBuffer outNodesText = new StringBuffer();
			StringBuffer outNodesXML = new StringBuffer();
			for (Object o : matchingNodes) {
				if (o instanceof Node) {
					Node n = (Node) o;
					if (n.getStringValue() != null
							&& n.getStringValue().length() > 0) {
						outNodesText.append(n.getStringValue() + "\n");
					}
					outNodesXML.append(n.asXML() + "\n");
				}
				else {
					outNodesText.append(o.toString());
				}
			}

			// tpExecutedXPathExpressionResults.setSelectedIndex(0); // open the
			// first tab (should be the one with textual results) // TODO -
			// enable if needed

			taExecutedXPathExpressionResultsAsText.setText(outNodesText
					.toString());
			taExecutedXPathExpressionResultsAsText.setBackground(Color.WHITE);
			taExecutedXPathExpressionResultsAsText.setCaretPosition(0);
			spExecutedXPathExpressionResultsAsText.setBorder(BorderFactory
					.createLineBorder(Color.WHITE, 3));

			taExecutedXPathExpressionResultsAsXML.setText(outNodesXML
					.toString());
			taExecutedXPathExpressionResultsAsXML.setBackground(Color.WHITE);
			taExecutedXPathExpressionResultsAsXML.setCaretPosition(0);
			spExecutedXPathExpressionResultsAsXML.setBorder(BorderFactory
					.createLineBorder(Color.WHITE, 3));
		}

		return (matchingNodeCount);
	}

	protected void setSourceXML(String xmlData) {
		this.taSourceXML.setText(xmlData);
	}

	protected String getCurrentXPathExpression() {
		return (this.tfXPathExpression.getText().trim());
	}

	protected void setCurrentXPathExpression(String xpathExpression) {
		this.tfXPathExpression.setText(xpathExpression);
	}

	protected Map<String, String> getCurrentXPathNamespaceMap() {
		return (this.xpathNamespaceMap);
	}

	/**
	 * This method doesn't simply set a reference to the passed map, but rather
	 * performs a shallow copy of values.
	 * 
	 * This is because the method is used during configuration panel's
	 * initialisation from the values that are held in the configuration bean.
	 * In case of simple reference assignment, any changes made to map in the
	 * configuration panel are also taking effect on the same map - referenced
	 * from the configuration bean, which leads to undesired behaviour.
	 */
	protected void setCurrentXPathNamespaceMapValues(
			Map<String, String> xpathNamespaceMap) {
		this.xpathNamespaceMap.clear();
		this.xpathNamespaceMap.putAll(xpathNamespaceMap);
	}

	protected XPathActivityXMLTree getCurrentXMLTree() {
		return (this.xmlTree);
	}

	/**
	 * For testing
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new XPathActivityConfigurationPanel());
		frame.pack();
		frame.setSize(new Dimension(900, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
