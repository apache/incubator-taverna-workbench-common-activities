package net.sf.taverna.t2.activities.rest.ui.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.apache.commons.lang.StringUtils;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.RESTActivity.DATA_FORMAT;
import net.sf.taverna.t2.activities.rest.RESTActivity.HTTP_METHOD;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;
import net.sf.taverna.t2.activities.rest.URISignatureHandler;
import net.sf.taverna.t2.activities.rest.URISignatureHandler.URISignatureParsingException;
import net.sf.taverna.t2.workbench.MainWindow;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

@SuppressWarnings("serial")
public class RESTActivityConfigurationPanel extends
		ActivityConfigurationPanel<RESTActivity, RESTActivityConfigurationBean> {
	private static final Icon infoIcon = new ImageIcon(
			RESTActivityConfigurationPanel.class.getResource("information.png"));
	
	private static final String VALID_NAME_REGEX = "[\\p{L}\\p{Digit}_]+";

	private RESTActivity activity;
	private RESTActivityConfigurationBean configBean;

	//private RESTActivityConfigurationPanel thisPanel;

	// GENERAL tab
	private JComboBox cbHTTPMethod; // HTTP method of this REST activity
	private JTextField tfURLSignature; // URL signature that determines its
										// input ports
	private JComboBox cbAccepts; // for Accepts header
	private JLabel jlContentTypeExplanation;
	private JLabel jlContentTypeExplanationPlaceholder;
	private JLabel jlContentType;
	private JLabel jlContentTypeLabelPlaceholder; // this placeholder label will
													// take up space of the
													// ContentType combo-box
													// when the latter is not
													// shown
	private JLabel jlContentTypeFieldPlaceholder;
	private JComboBox cbContentType; // for MIME type of data sent to the server
										// by POST / PUT methods
	private JLabel jlSendDataAs;
	private JComboBox cbSendDataAs;
	private JLabel jlSendDataAsLabelPlaceholder;
	private JLabel jlSendDataAsFieldPlaceholder;

	// ADVANCED tab
	private JCheckBox cbSendHTTPExpectHeader;
	private JCheckBox cbShowRedirectionOutputPort;
	private JCheckBox cbShowActualUrlPort;
	private JCheckBox cbShowResponseHeadersPort;
	private JCheckBox cbEscapeParameters;
	private JButton addHeaderButton;
	private JButton removeHeaderButton;
	private JTable httpHeadersTable;
	private HTTPHeadersTableModel httpHeadersTableModel;

	private String[] mediaTypes;

	public RESTActivityConfigurationPanel(RESTActivity activity) {
		//this.thisPanel = this;
		this.activity = activity;
		initGui();
	}

	protected void initGui() {
		removeAll();
		setLayout(new BorderLayout());

		// create view title
//		ShadedLabel slConfigurationLabel = new ShadedLabel(
//				"Configuration options for this REST service",
//				ShadedLabel.ORANGE);
//		JPanel jpConfigurationLabel = new JPanel(new GridLayout(1, 1));
//		jpConfigurationLabel.add(slConfigurationLabel);
//		jpConfigurationLabel.setBorder(BorderFactory.createEmptyBorder(8, 10,
//				0, 10));
//		add(jpConfigurationLabel, BorderLayout.NORTH);

		// create tabbed view
		JTabbedPane tpTabs = new JTabbedPane();
		tpTabs.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		tpTabs.add("General", createGeneralTab());
		tpTabs.add("Advanced", createAdvancedTab());
		add(tpTabs, BorderLayout.CENTER);

		// Populate fields from activity configuration bean
		refreshConfiguration();
	}

	private JPanel createGeneralTab() {
		JPanel jpGeneral = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		// All components to be anchored WEST
		c.anchor = GridBagConstraints.WEST;

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(7, 7, 3, 3);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		JLabel labelMethod = new JLabel("HTTP Method:", infoIcon, JLabel.LEFT);
		labelMethod
				.setToolTipText("<html>HTTP method determines how a request to the remote server will be made.<br><br>"
						+ "Supported HTTP methods are normally used for different purposes:<br>"
						+ "<b>GET</b> - to fetch data;<br>"
						+ "<b>POST</b> - to create new resources;<br>"
						+ "<b>PUT</b> - to update existing resources;<br>"
						+ "<b>DELETE</b> - to remove existing resources.<br><br>"
						+ "Documentation of the server that is about to be used may suggest the<br>"
						+ "HTTP method that should be used.</html>");
		jpGeneral.add(labelMethod, c);

		// the HTTP method combo-box will always contain the same values - it is
		// the selected
		// method which is important; therefore, can prepopulate as the set of
		// values is known
		c.gridx++;
		c.insets = new Insets(7, 3, 3, 7);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		cbHTTPMethod = new JComboBox(RESTActivity.HTTP_METHOD.values());
		cbHTTPMethod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean contentTypeSelEnabled = RESTActivity
						.hasMessageBodyInputPort((HTTP_METHOD) cbHTTPMethod
								.getSelectedItem());

				jlContentTypeExplanation.setVisible(contentTypeSelEnabled);
				jlContentType.setVisible(contentTypeSelEnabled);
				cbContentType.setVisible(contentTypeSelEnabled);
				jlSendDataAs.setVisible(contentTypeSelEnabled);
				cbSendDataAs.setVisible(contentTypeSelEnabled);

				jlContentTypeExplanationPlaceholder
						.setVisible(!contentTypeSelEnabled);
				jlContentTypeLabelPlaceholder
						.setVisible(!contentTypeSelEnabled);
				jlContentTypeFieldPlaceholder
						.setVisible(!contentTypeSelEnabled);
				jlSendDataAsLabelPlaceholder.setVisible(!contentTypeSelEnabled);
				jlSendDataAsFieldPlaceholder.setVisible(!contentTypeSelEnabled);
			}
		});
		jpGeneral.add(cbHTTPMethod, c);

		c.gridx = 0;
		c.gridy++;
		c.insets = new Insets(3, 7, 3, 3);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		JLabel labelString = new JLabel("URL Template:", infoIcon, JLabel.LEFT);
		labelString
				.setToolTipText("<html>URL template enables to define a URL with <b>configurable<br>"
						+ "parameters</b> that will be used to access a remote server.<br><br>"
						+ "The template may contain zero or more <b>parameters</b> - each<br>"
						+ "enclosed within curly braces <b>\"{\"</b> and <b>\"}\"</b>.<br>"
						+ "Taverna will automatically create an individual input port for<br>"
						+ "this activity for each parameter.<br><br>"
						+ "Values extracted from these input ports during the workflow<br>"
						+ "execution these will be used to replace the parameters to<br>"
						+ "produce complete URLs.<br><br>"
						+ "For example, if the URL template is configured as<br>"
						+ "\"<i>http://www.myexperiment.org/user.xml?id={userID}</i>\", a<br>"
						+ "single input port with the name \"<i>userID</i>\" will be created.</html>");
		labelString.setLabelFor(tfURLSignature);
		jpGeneral.add(labelString, c);

		c.gridx++;
		c.insets = new Insets(3, 3, 3, 7);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		tfURLSignature = new JTextField(40);
		tfURLSignature.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				tfURLSignature.selectAll();
			}

			public void focusLost(FocusEvent e) { /* do nothing */
			}
		});
		jpGeneral.add(tfURLSignature, c);

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy++;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(18, 7, 3, 7);
		JLabel jlAcceptsExplanation = new JLabel(
				"Preferred MIME type for data to be fetched from the remote server --");
		jpGeneral.add(jlAcceptsExplanation, c);
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy++;
		c.insets = new Insets(3, 7, 3, 3);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		JLabel jlAccepts = new JLabel("'Accept' header:", infoIcon, JLabel.LEFT);
		jlAccepts
				.setToolTipText("<html>Select a MIME type from the drop-down menu or type your own.<br>Select blank if you do not want this header to be set.</br>");
		jlAccepts.setLabelFor(cbAccepts);
		jpGeneral.add(jlAccepts, c);

		c.gridx++;
		c.insets = new Insets(3, 3, 3, 7);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		cbAccepts = new JComboBox(getMediaTypes());
		cbAccepts.setEditable(true);
		cbAccepts.getEditor().getEditorComponent().addFocusListener(
				new FocusListener() {
					public void focusGained(FocusEvent e) {
						cbAccepts.getEditor().selectAll();
					}

					public void focusLost(FocusEvent e) { /* do nothing */
					}
				});
		jpGeneral.add(cbAccepts, c);

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy++;
		c.insets = new Insets(18, 7, 3, 7);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		jlContentTypeExplanation = new JLabel(
				"MIME type of data that will be sent to the remote server --");
		jpGeneral.add(jlContentTypeExplanation, c);
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy++;
		c.insets = new Insets(3, 7, 3, 3);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		jlContentType = new JLabel("'Content-Type' header:", infoIcon,
				JLabel.LEFT);
		jlContentType
				.setToolTipText("<html>Select a MIME type from the drop-down menu or type your own.<br>Select blank if you do not want this header to be set.</html>");
		jlContentType.setLabelFor(cbContentType);
		jpGeneral.add(jlContentType, c);

		c.gridx++;
		c.insets = new Insets(3, 3, 3, 7);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		cbContentType = new JComboBox(getMediaTypes());
		cbContentType.setEditable(true);
		cbContentType.getEditor().getEditorComponent().addFocusListener(
				new FocusListener() {
					public void focusGained(FocusEvent e) {
						cbContentType.getEditor().selectAll();
					}

					public void focusLost(FocusEvent e) { /* do nothing */
					}
				});
		cbContentType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// change selection in the "Send data as" combo-box, based on
				// the selection of Content-Type
				String selectedContentType = (String) cbContentType
						.getSelectedItem();
				if (selectedContentType.startsWith("text")) {
					cbSendDataAs.setSelectedItem(DATA_FORMAT.String);
				} else {
					cbSendDataAs.setSelectedItem(DATA_FORMAT.Binary);
				}
			}
		});
		jpGeneral.add(cbContentType, c);

		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy++;
		c.insets = new Insets(18, 7, 3, 7);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		jlContentTypeExplanationPlaceholder = new JLabel();
		jlContentTypeExplanationPlaceholder
				.setPreferredSize(jlContentTypeExplanation.getPreferredSize());
		jpGeneral.add(jlContentTypeExplanationPlaceholder, c);
		c.gridwidth = 1;

		c.gridx = 0;
		c.gridy++;
		c.insets = new Insets(3, 7, 3, 3);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		jlContentTypeLabelPlaceholder = new JLabel();
		jlContentTypeLabelPlaceholder.setPreferredSize(jlContentType
				.getPreferredSize());
		jpGeneral.add(jlContentTypeLabelPlaceholder, c);

		c.gridx++;
		c.insets = new Insets(3, 3, 3, 7);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		jlContentTypeFieldPlaceholder = new JLabel();
		jlContentTypeFieldPlaceholder.setPreferredSize(cbContentType
				.getPreferredSize());
		jpGeneral.add(jlContentTypeFieldPlaceholder, c);

		c.gridx = 0;
		c.gridy++;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(3, 7, 8, 3);
		jlSendDataAs = new JLabel("Send data as:", infoIcon, JLabel.LEFT);
		jlSendDataAs
				.setToolTipText("Select the format for the data to be sent to the remote server");
		jlSendDataAs.setLabelFor(cbSendDataAs);
		jpGeneral.add(jlSendDataAs, c);

		c.gridx++;
		c.insets = new Insets(3, 3, 8, 7);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		cbSendDataAs = new JComboBox(RESTActivity.DATA_FORMAT.values());
		cbSendDataAs.setEditable(false);
		jpGeneral.add(cbSendDataAs, c);

		c.gridx = 0;
		c.gridy++;
		c.insets = new Insets(3, 7, 8, 3);
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		jlSendDataAsLabelPlaceholder = new JLabel();
		jlSendDataAsLabelPlaceholder.setPreferredSize(jlSendDataAs
				.getPreferredSize());
		jpGeneral.add(jlSendDataAsLabelPlaceholder, c);

		c.gridx++;
		c.insets = new Insets(3, 3, 8, 7);
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		jlSendDataAsFieldPlaceholder = new JLabel();
		jlSendDataAsFieldPlaceholder.setPreferredSize(cbSendDataAs
				.getPreferredSize());
		jpGeneral.add(jlSendDataAsFieldPlaceholder, c);
		
		JPanel finalPanel = new JPanel(new BorderLayout());
		finalPanel.add(jpGeneral, BorderLayout.NORTH);
		return (finalPanel);
	}

	private String[] getMediaTypes() {
		if (mediaTypes != null) {
			return mediaTypes;
		}
		List<String> types = new ArrayList<String>();
		InputStream typesStream = getClass().getResourceAsStream(
				"mediatypes.txt");
		try {
			// media types must be ASCII and can't have whitespace
			Scanner scanner = new Scanner(typesStream, "ascii");
			while (scanner.hasNext()) {
				types.add(scanner.next());
			}
			scanner.close();
		} finally {
			try {
				typesStream.close();
			} catch (IOException ex) {
			}
		}
		mediaTypes = types.toArray(new String[0]);

		return mediaTypes;
	}

	private JPanel createAdvancedTab() {
		JPanel jpAdvanced = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(8, 10, 2, 4);
		JLabel jlExpectHeaderInfoIcon = new JLabel(infoIcon);
		jlExpectHeaderInfoIcon
				.setToolTipText("<html>Ticking this checkbox may significantly improve performance when<br>"
						+ "large volumes of data are sent to the remote server and a redirect<br>"
						+ "from the original URL to the one specified by the server is likely.<br>"
						+ "<br>"
						+ "However, this checkbox <b>must not</b> be ticked to allow this activity<br>"
						+ "to post updates to Twitter.</html>");
		jpAdvanced.add(jlExpectHeaderInfoIcon, c);

		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(8, 0, 2, 8);
		cbSendHTTPExpectHeader = new JCheckBox(
				"Send HTTP Expect request-header field");
		jpAdvanced.add(cbSendHTTPExpectHeader, c);

		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 5, 4);
		JLabel jlShowRedirectionOutputPortInfoIcon = new JLabel(infoIcon);
		jlShowRedirectionOutputPortInfoIcon
				.setToolTipText("<html>\"Redirection\" output port displays the URL of the final redirect<br>"
						+ "that has yielded the output data on the \"Response Body\" port.</html>");
		jpAdvanced.add(jlShowRedirectionOutputPortInfoIcon, c);

		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(2, 0, 5, 8);
		cbShowRedirectionOutputPort = new JCheckBox(
				"Show \"Redirection\" output port");
		jpAdvanced.add(cbShowRedirectionOutputPort, c);
		
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 5, 4);
		JLabel jlShowActualUrlPortInfoIcon = new JLabel(infoIcon);
		jlShowActualUrlPortInfoIcon
				.setToolTipText("<html>\"Actual URL\" output port displays the URL used by the REST service<br>"
						+ "with the actual parameter values.</html>");
		jpAdvanced.add(jlShowActualUrlPortInfoIcon, c);

		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(2, 0, 5, 8);
		cbShowActualUrlPort = new JCheckBox(
				"Show \"Actual URL\" output port");
		jpAdvanced.add(cbShowActualUrlPort, c);
		
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 5, 4);
		JLabel jlShowResponseHeadersPortInfoIcon = new JLabel(infoIcon);
		jlShowResponseHeadersPortInfoIcon
				.setToolTipText("<html>\"Response headers\" output port displays the HTTP headers<br>"
						+ "received from the final (after redirection) HTTP call.</html>");
		jpAdvanced.add(jlShowResponseHeadersPortInfoIcon, c);

		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(2, 0, 5, 8);
		cbShowResponseHeadersPort = new JCheckBox(
				"Show \"Response headers\" output port");
		jpAdvanced.add(cbShowResponseHeadersPort, c);
		
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.insets = new Insets(2, 10, 5, 4);
		JLabel jlEscapeParametersInfoIcon = new JLabel(infoIcon);
		jlEscapeParametersInfoIcon
				.setToolTipText("<html>Determines if parameters you pass to form the full URL<br>" +
						" of the REST service will be URL-escaped.</html>");
		jpAdvanced.add(jlEscapeParametersInfoIcon, c);

		c.gridx++;
		c.weightx = 1.0;
		c.insets = new Insets(2, 0, 5, 8);
		cbEscapeParameters = new JCheckBox("Escape URL parameter values");
		jpAdvanced.add(cbEscapeParameters, c);
		
		c.gridx = 0;
		c.gridy++;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 10, 5, 4);
		JLabel jlHTTPHeadersInfoIcon = new JLabel(infoIcon);
		jlHTTPHeadersInfoIcon
				.setToolTipText("<html>Set additional HTTP headers</html>");
		jpAdvanced.add(jlHTTPHeadersInfoIcon, c);
		
		c.gridx = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(2, 10, 5, 4);
		addHeaderButton = new JButton("Add HTTP header");
		addHeaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				httpHeadersTableModel.addEmptyRow();
				httpHeadersTable.getSelectionModel().setSelectionInterval(httpHeadersTableModel.getRowCount() - 1, httpHeadersTableModel.getRowCount() - 1);			}
		});
		removeHeaderButton = new JButton("Remove HTTP header");
		removeHeaderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = httpHeadersTable.getSelectedRow();
				httpHeadersTableModel.removeRow(row);
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(addHeaderButton, FlowLayout.LEFT);
		buttonPanel.add(removeHeaderButton);
		jpAdvanced.add(buttonPanel, c);
		
		c.gridx = 1;
		c.gridy++;
		c.weightx = 0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 10, 5, 4);
		httpHeadersTableModel = new HTTPHeadersTableModel();
		httpHeadersTable = new JTable(httpHeadersTableModel);
		httpHeadersTable.setGridColor(Color.GRAY);
		httpHeadersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setVisibleRowCount(httpHeadersTable, 3);
		JScrollPane headersTableScrollPane = new JScrollPane(httpHeadersTable);
		jpAdvanced.add(headersTableScrollPane, c);

		// this JLabel makes the rest of the content of the panel to go to the
		// top of the tab
		// (instead of being centered)
//		c.gridx = 0;
//		c.gridy++;
//		c.weightx = 0;
//		c.weighty = 1.0;
//		c.insets = new Insets(0, 0, 0, 0);
//		JLabel jlSpacer = new JLabel();
//		jpAdvanced.add(jlSpacer, c);
		
		return (jpAdvanced);
	}
	
	/*
	 * Based on http://www.javalobby.org/java/forums/t19559.html
	 */
	public static void setVisibleRowCount(JTable table, int visibleRows){ 
	    int height = 0; 
	    for(int row = 0; row < visibleRows; row++) 
	        height += table.getRowHeight(row); 
	 
	    table.setPreferredScrollableViewportSize(new Dimension( 
	            table.getPreferredScrollableViewportSize().width, 
	            height)); 
	}


	/**
	 * Check that user values in the UI are valid.
	 */
	@Override
	public boolean checkValues() {
		// HTTP method is a fixed selection combo-box - no validation required

		// URL signature must be present and be valid
		String candidateURLSignature = tfURLSignature.getText().trim();
		if (candidateURLSignature == null
				|| candidateURLSignature.length() == 0) {
			JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
					"URL signature must not be empty",
					"REST Activity Configuration - Warning",
					JOptionPane.WARNING_MESSAGE);
			return (false);
		} else {
			try {
				// Test if any exceptions will be thrown - if not, proceed to
				// other validations
				URISignatureHandler.validate(candidateURLSignature);
			} catch (URISignatureParsingException e) {
				JOptionPane.showMessageDialog(MainWindow.getMainWindow(), e
						.getMessage(), "REST Activity Configuration - Warning",
						JOptionPane.WARNING_MESSAGE);
				return (false);
			}
						
			// Test if the URL string contains "unsafe" characters, i.e. characters 
			// that need URL-encoding.
			// From RFC 1738: "...Only alphanumerics [0-9a-zA-Z], the special 
			// characters "$-_.+!*'()," (not including the quotes) and reserved 
			// characters used for their reserved purposes may be 
			// used unencoded within a URL." 
			// Reserved characters are: ";/?:@&=" ..." (excluding quotes) and "%" used 
			// for escaping.
			// We do not warn the user if they have not properly enclosed parameter 
			// names in curly braces as this check is already being done elsewhere in the code.
			// We do not check the characters in parameter names either.
			try {
				// Test if any exceptions will be thrown - if not, proceed to
				// other validations
				URISignatureHandler.checkForUnsafeCharacters(candidateURLSignature);
			} catch (URISignatureParsingException e) {
				JOptionPane.showMessageDialog(MainWindow.getMainWindow(), e
						.getMessage(), "REST Activity Configuration - Warning",
						JOptionPane.WARNING_MESSAGE);
				return (false);
			}
			
			// Other HTTP headers configured must not have empty names
			ArrayList<String> otherHTTPHeaderNames = httpHeadersTableModel.getHTTPHeaderNames();
			for (String headerName : otherHTTPHeaderNames){
				if (headerName.equals("")){
					JOptionPane.showMessageDialog(MainWindow.getMainWindow(), "One of the HTTP header names is empty", "REST Activity Configuration - Warning",
							JOptionPane.WARNING_MESSAGE);
					return false;
				}
			}
			
			List<String> placeholders = URISignatureHandler
					.extractPlaceholders(candidateURLSignature);
			List<String> invalidPlaceHolders = new ArrayList<String>();
			for (String placeHolder : placeholders) {
				if (!placeHolder.matches(VALID_NAME_REGEX)) {
					invalidPlaceHolders.add(placeHolder);
				}
			}
			for (String headerValue : httpHeadersTableModel.getHTTPHeaderValues()) {
				List<String> placeHolders = URISignatureHandler
				.extractPlaceholders(headerValue);
				for (String placeHolder : placeHolders) {
				if (!placeHolder.matches(VALID_NAME_REGEX)) {
					invalidPlaceHolders.add(placeHolder);
				}
				}
			}
			if (!invalidPlaceHolders.isEmpty()) {
				String invalidPlaceHoldersString = StringUtils.join(invalidPlaceHolders, "\n");
				JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
						"The following placeholders are invalid:\n" + invalidPlaceHoldersString,
						"REST Activity Configuration - Error",
						JOptionPane.ERROR_MESSAGE);
				return false;				
			}
		}

		// check if Accept header value is at least not empty
// We now allow blank values for the 'Accept' header
//		Object candidateAcceptHeaderValue = cbAccepts.getSelectedItem();
//		if (candidateAcceptHeaderValue == null
//				|| ((String) candidateAcceptHeaderValue).length() == 0) {
//			JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
//					"Accept header value must not be empty",
//					"REST Activity Configuration - Warning",
//					JOptionPane.WARNING_MESSAGE);
//			return (false);
//		}

		// check if Content-Type header value is at least not empty - only do
		// this for those HTTP
		// methods which actually use this value; otherwise, it doesn't really
		// matter, as the value
		// will not be stored to the bean anyway
// We now allow blank values for the 'Content-Type' header
//		if (RESTActivity
//				.hasMessageBodyInputPort((RESTActivity.HTTP_METHOD) cbHTTPMethod
//						.getSelectedItem())) {
//			Object candidateContentTypeHeaderValue = cbContentType
//					.getSelectedItem();
//			if (candidateContentTypeHeaderValue == null
//					|| ((String) candidateContentTypeHeaderValue).length() == 0) {
//				JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
//						"Content-Type header value must not be empty",
//						"REST Activity Configuration - Warning",
//						JOptionPane.WARNING_MESSAGE);
//				return (false);
//			}
//		}

		// All valid, return true
		return true;
	}


	/**
	 * Return configuration bean generated from user interface last time
	 * noteConfiguration() was called.
	 */
	@Override
	public RESTActivityConfigurationBean getConfiguration() {
		// Should already have been made by noteConfiguration()
		return configBean;
	}

	/**
	 * Check if the user has changed the configuration from the original
	 */
	@Override
	public boolean isConfigurationChanged() {
		HTTP_METHOD originalHTTPMethod = configBean.getHttpMethod();
		String originalURLSignature = configBean.getUrlSignature();
		String originalAcceptsHeaderValue = configBean.getAcceptsHeaderValue();
		String originalContentType = configBean.getContentTypeForUpdates();
		DATA_FORMAT originalOutgoingDataFormat = configBean
				.getOutgoingDataFormat();
		boolean originalSendHTTPExpectRequestHeader = configBean
				.getSendHTTPExpectRequestHeader();
		boolean originalShowRedirectionOutputPort = configBean
				.getShowRedirectionOutputPort();
		boolean originalShowActualUrlPort = configBean
		.getShowActualUrlPort();
		boolean originalShowResponseHeadersPort = configBean.getShowResponseHeadersPort();
		boolean originalEscapeParameters = configBean.getEscapeParameters();

		boolean contentTypeHasNotChanged = (originalContentType == null && ((String) cbContentType
				.getSelectedItem()).length() == 0)
				|| (originalContentType != null && originalContentType
						.equals((String) cbContentType.getSelectedItem()));

		// Check if other HTTP header fields or their values have changed
		boolean otherHTTPHeadersNotChanged = true;
		// List of 2-element lists containing header name and value
		ArrayList<ArrayList<String>> originalOtherHTTPHeaders = configBean.getOtherHTTPHeaders();
		ArrayList<ArrayList<String>> otherHTTPHeaders = httpHeadersTableModel.getHTTPHeaderData();
		ListComparator listComparator = new ListComparator();
		// Sort the list rows by the first element in the row (which is HTTP header name)
		Collections.sort(originalOtherHTTPHeaders, listComparator);
		Collections.sort(otherHTTPHeaders, listComparator);
		if (originalOtherHTTPHeaders.size() != otherHTTPHeaders.size()){
			otherHTTPHeadersNotChanged = false; // if size differs we do not have to check anything else
		}
		else{
			// Lists are sorted by HTTP header names now, we can just iterate over any
			for (int i = 0; i < otherHTTPHeaders.size() ; i++){
				// Compare the header names
				if (!otherHTTPHeaders.get(i).get(0).equals(originalOtherHTTPHeaders.get(i).get(0))){
					otherHTTPHeadersNotChanged = false;
					break;
				}
				// Compare the header values
				if (!otherHTTPHeaders.get(i).get(1).equals(originalOtherHTTPHeaders.get(i).get(1))){
					otherHTTPHeadersNotChanged = false;
					break;
				}
			}
		}
		
		// true (changed) unless all fields match the originals
		return !(originalHTTPMethod == (HTTP_METHOD) cbHTTPMethod
				.getSelectedItem()
				&& originalURLSignature.equals(tfURLSignature.getText())
				&& originalAcceptsHeaderValue.equals((String) cbAccepts
						.getSelectedItem())
				&& contentTypeHasNotChanged
				&& originalOutgoingDataFormat == (DATA_FORMAT) cbSendDataAs
						.getSelectedItem()
				&& originalSendHTTPExpectRequestHeader == cbSendHTTPExpectHeader.isSelected() 
				&& originalShowRedirectionOutputPort == cbShowRedirectionOutputPort.isSelected()
				&& originalShowActualUrlPort == cbShowActualUrlPort.isSelected()
				&& originalShowResponseHeadersPort == cbShowResponseHeadersPort.isSelected()
				&& originalEscapeParameters == cbEscapeParameters.isSelected()
				&& otherHTTPHeadersNotChanged);
		
	}

	/**
	 * Prepare a new configuration bean from the UI, to be returned with
	 * getConfiguration()
	 */
	@Override
	public void noteConfiguration() {
		configBean = new RESTActivityConfigurationBean();

		// safe to cast, as it's the type of values that have been placed there
		configBean.setHttpMethod((RESTActivity.HTTP_METHOD) cbHTTPMethod
				.getSelectedItem());
		configBean.setUrlSignature(tfURLSignature.getText().trim());
		configBean.setAcceptsHeaderValue((String) cbAccepts.getSelectedItem());
		configBean.setContentTypeForUpdates((String) cbContentType
				.getSelectedItem());
		configBean.setOutgoingDataFormat((DATA_FORMAT) cbSendDataAs
				.getSelectedItem());
		configBean.setSendHTTPExpectRequestHeader(cbSendHTTPExpectHeader
				.isSelected());
		configBean.setShowRedirectionOutputPort(cbShowRedirectionOutputPort
				.isSelected());
		configBean.setShowActualUrlPort(cbShowActualUrlPort.isSelected());
		configBean.setShowResponseHeadersPort(cbShowResponseHeadersPort.isSelected());

		configBean.setEscapeParameters(cbEscapeParameters.isSelected());
		configBean.setOtherHTTPHeaders(httpHeadersTableModel.getHTTPHeaderData());
	}

	/**
	 * Update GUI from a changed configuration bean (perhaps by undo / redo).
	 */
	@Override
	public void refreshConfiguration() {
		configBean = activity.getConfiguration();

		cbHTTPMethod.setSelectedItem(configBean.getHttpMethod());
		tfURLSignature.setText(configBean.getUrlSignature());
		tfURLSignature.setCaretPosition(0);
		cbAccepts.setSelectedItem(configBean.getAcceptsHeaderValue());
		cbContentType.setSelectedItem(configBean.getContentTypeForUpdates());
		cbSendDataAs.setSelectedItem(configBean.getOutgoingDataFormat());
		cbSendHTTPExpectHeader.setSelected(configBean
				.getSendHTTPExpectRequestHeader());
		cbShowRedirectionOutputPort.setSelected(configBean
				.getShowRedirectionOutputPort());
		cbShowActualUrlPort.setSelected(configBean.getShowActualUrlPort());
		cbShowResponseHeadersPort.setSelected(configBean.getShowResponseHeadersPort());
		cbEscapeParameters.setSelected(configBean.getEscapeParameters());
		httpHeadersTableModel.setHTTPHeaderData(configBean.getOtherHTTPHeaders());
	}
	
	/*
	 * Compares two lists of strings by doing string comparison on their first element.
	 */
	private class ListComparator implements Comparator<ArrayList<String>>{

		@Override
		public int compare(ArrayList<String> list1, ArrayList<String> list2) {
			// lists should not be empty, but ...
			if (list1.isEmpty()){
				if (list2.isEmpty()){
					return 0;
				}
				else{
					return 1;
				}
			}
			else{
				if (list2.isEmpty()){
					return -1;
				}
				else{
					return list1.get(0).compareTo(list2.get(0));
				}			
				
			}
		}
		
	}

}
