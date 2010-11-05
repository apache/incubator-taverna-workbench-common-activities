package net.sf.taverna.t2.activities.rest.ui.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import net.sf.taverna.t2.lang.ui.ShadedLabel;
import net.sf.taverna.t2.workbench.MainWindow;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

import net.sf.taverna.t2.activities.rest.RESTActivity;
import net.sf.taverna.t2.activities.rest.RESTActivityConfigurationBean;
import net.sf.taverna.t2.activities.rest.URISignatureHandler;
import net.sf.taverna.t2.activities.rest.RESTActivity.DATA_FORMAT;
import net.sf.taverna.t2.activities.rest.RESTActivity.HTTP_METHOD;
import net.sf.taverna.t2.activities.rest.URISignatureHandler.URISignatureParsingException;


@SuppressWarnings("serial")
public class RESTActivityConfigurationPanel	extends
		ActivityConfigurationPanel<RESTActivity, RESTActivityConfigurationBean>
{
  private static final Icon infoIcon = new ImageIcon(RESTActivityConfigurationPanel.class.getResource("information.png"));
  
	private RESTActivity activity;
	private RESTActivityConfigurationBean configBean;
	
	private RESTActivityConfigurationPanel thisPanel;
	
	// GENERAL tab
	private JComboBox cbHTTPMethod;           // HTTP method of this REST activity
	private JTextField tfURLSignature;        // URL signature that determines its input ports
	private JComboBox cbAccepts;              // for Accepts header
	private JLabel jlContentTypeExplanation;
	private JLabel jlContentTypeExplanationPlaceholder;
	private JLabel jlContentType;
	private JLabel jlContentTypeLabelPlaceholder;  // this placeholder label will take up space of the ContentType combo-box when the latter is not shown
	private JLabel jlContentTypeFieldPlaceholder;
	private JComboBox cbContentType;          // for MIME type of data sent to the server by POST / PUT methods
	private JLabel jlSendDataAs;
	private JComboBox cbSendDataAs;
	private JLabel jlSendDataAsLabelPlaceholder;
	private JLabel jlSendDataAsFieldPlaceholder;
	
	// ADVANCED tab
	private JCheckBox cbSendHTTPExpectHeader;
	private JCheckBox cbShowRedirectionOutputPort;
	

	public RESTActivityConfigurationPanel(RESTActivity activity) {
	  this.thisPanel = this;
		this.activity = activity;
		initGui();
	}
	

	protected void initGui()
	{
		removeAll();
		setLayout(new BorderLayout());
		
		// create view title
		ShadedLabel slConfigurationLabel = new ShadedLabel("Configuration options for this REST service", ShadedLabel.ORANGE);
    JPanel jpConfigurationLabel = new JPanel(new GridLayout(1,1));
    jpConfigurationLabel.add(slConfigurationLabel);
    jpConfigurationLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
    add(jpConfigurationLabel, BorderLayout.NORTH);
		
    // create tabbed view
		JTabbedPane tpTabs = new JTabbedPane();
		tpTabs.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		tpTabs.add("General", createGeneralTab());
		tpTabs.add("Advanced", createAdvancedTab());
		add(tpTabs, BorderLayout.CENTER);
		
		// Populate fields from activity configuration bean
		refreshConfiguration();
	}
	
	
	private JPanel createGeneralTab()
	{
	  JPanel jpGeneral = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(7, 7, 3, 3);
    JLabel labelMethod = new JLabel("HTTP Method:", infoIcon, JLabel.LEFT);
    labelMethod.setToolTipText("<html>HTTP method determines how a request to the remote server will be made.<br><br>" +
    		                             "Supported HTTP methods are normally used for different purposes:<br>" +
    		                             "<b>GET</b> - to fetch data;<br>" +
    		                             "<b>POST</b> - to create new resources;<br>" +
    		                             "<b>PUT</b> - to update existing resources;<br>" +
    		                             "<b>DELETE</b> - to remove existing resources.<br><br>" +
    		                             "Documentation of the server that is about to be used may suggest the<br>" +
    		                             "HTTP method that should be used.</html>");
    jpGeneral.add(labelMethod, c);
    
    // the HTTP method combo-box will always contain the same values - it is the selected
    // method which is important; therefore, can prepopulate as the set of values is known
    c.gridx++;
    c.insets = new Insets(7, 3, 3, 7);
    cbHTTPMethod = new JComboBox(RESTActivity.HTTP_METHOD.values());
    cbHTTPMethod.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boolean contentTypeSelEnabled = RESTActivity.hasMessageBodyInputPort((HTTP_METHOD)cbHTTPMethod.getSelectedItem());
        
        jlContentTypeExplanation.setVisible(contentTypeSelEnabled);
        jlContentType.setVisible(contentTypeSelEnabled);
        cbContentType.setVisible(contentTypeSelEnabled);
        jlSendDataAs.setVisible(contentTypeSelEnabled);
        cbSendDataAs.setVisible(contentTypeSelEnabled);
        
        jlContentTypeExplanationPlaceholder.setVisible(!contentTypeSelEnabled);
        jlContentTypeLabelPlaceholder.setVisible(!contentTypeSelEnabled);
        jlContentTypeFieldPlaceholder.setVisible(!contentTypeSelEnabled);
        jlSendDataAsLabelPlaceholder.setVisible(!contentTypeSelEnabled);
        jlSendDataAsFieldPlaceholder.setVisible(!contentTypeSelEnabled);
      }
    });
    jpGeneral.add(cbHTTPMethod, c);
    
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(3, 7, 3, 3);
    JLabel labelString = new JLabel("URL Template:", infoIcon, JLabel.LEFT);
    labelString.setToolTipText("<html>URL template enables to define a URL with <b>configurable<br>" +
                                     "parameters</b> that will be used to access a remote server.<br><br>" +
                                     "The template may contain zero or more <b>parameters</b> - each<br>" +
                                     "enclosed within curly braces <b>\"{\"</b> and <b>\"}\"</b>.<br>" +
                                     "Taverna will automatically create an individual input port for<br>" +
                                     "this activity for each parameter.<br><br>" +
                                     "Values extracted from these input ports during the workflow<br>" +
                                     "execution these will be used to replace the parameters to<br>" +
                                     "produce complete URLs.<br><br>" +
                                     "For example, if the URL template is configured as<br>" +
                                     "\"<i>http://www.myexperiment.org/user.xml?id={userID}</i>\", a<br>" +
                                     "single input port with the name \"<i>userID</i>\" will be created.</html>");
    labelString.setLabelFor(tfURLSignature);
    jpGeneral.add(labelString, c);
    
    c.gridx++;
    c.insets = new Insets(3, 3, 3, 7);
    tfURLSignature = new JTextField(50);
    tfURLSignature.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        tfURLSignature.selectAll();
      }
      public void focusLost(FocusEvent e) { /* do nothing */ }
    });
    jpGeneral.add(tfURLSignature, c);
    
    c.gridx = 0;
    c.gridwidth = 2;
    c.gridy++;
    c.insets = new Insets(18, 7, 3, 7);
    JLabel jlAcceptsExplanation = new JLabel("Preferred MIME type for data to be fetched from the remote server --");
    jpGeneral.add(jlAcceptsExplanation, c);
    c.gridwidth = 1;
    
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(3, 7, 3, 3);
    JLabel jlAccepts = new JLabel("'Accept' header:", infoIcon, JLabel.LEFT);
    jlAccepts.setToolTipText("Select a MIME type from the drop-down menu or type your own");
    jlAccepts.setLabelFor(cbAccepts);
    jpGeneral.add(jlAccepts, c);
    
    c.gridx++;
    c.insets = new Insets(3, 3, 3, 7);
    cbAccepts = new JComboBox(RESTActivity.MIME_TYPES);
    cbAccepts.setEditable(true);
    cbAccepts.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        cbAccepts.getEditor().selectAll();
      }
      public void focusLost(FocusEvent e) { /* do nothing */ }
    });
    jpGeneral.add(cbAccepts, c);
    
    
    c.gridx = 0;
    c.gridwidth = 2;
    c.gridy++;
    c.insets = new Insets(18, 7, 3, 7);
    jlContentTypeExplanation = new JLabel("MIME type of data that will be sent to the remote server --");
    jpGeneral.add(jlContentTypeExplanation, c);
    c.gridwidth = 1;
    
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(3, 7, 3, 3);
    jlContentType = new JLabel("'Content-Type' header:", infoIcon, JLabel.LEFT);
    jlContentType.setToolTipText("Select a MIME type from the drop-down menu or type your own");
    jlContentType.setLabelFor(cbContentType);
    jpGeneral.add(jlContentType, c);
    
    c.gridx++;
    c.insets = new Insets(3, 3, 3, 7);
    cbContentType = new JComboBox(RESTActivity.MIME_TYPES);
    cbContentType.setEditable(true);
    cbContentType.getEditor().getEditorComponent().addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {
        cbContentType.getEditor().selectAll();
      }
      public void focusLost(FocusEvent e) { /* do nothing */ }
    });
    cbContentType.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // change selection in the "Send data as" combo-box, based on the selection of Content-Type
        String selectedContentType = (String)cbContentType.getSelectedItem();
        if (selectedContentType.startsWith("text")) { cbSendDataAs.setSelectedItem(DATA_FORMAT.String); }
        else { cbSendDataAs.setSelectedItem(DATA_FORMAT.Binary); }
      }
    });
    jpGeneral.add(cbContentType, c);
    
    
    c.gridx = 0;
    c.gridwidth = 2;
    c.gridy++;
    c.insets = new Insets(18, 7, 3, 7);
    jlContentTypeExplanationPlaceholder = new JLabel();
    jlContentTypeExplanationPlaceholder.setPreferredSize(jlContentTypeExplanation.getPreferredSize());
    jpGeneral.add(jlContentTypeExplanationPlaceholder, c);
    c.gridwidth = 1;
    
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(3, 7, 3, 3);
    jlContentTypeLabelPlaceholder = new JLabel();
    jlContentTypeLabelPlaceholder.setPreferredSize(jlContentType.getPreferredSize());
    jpGeneral.add(jlContentTypeLabelPlaceholder, c);
    
    c.gridx++;
    c.insets = new Insets(3, 3, 3, 7);
    jlContentTypeFieldPlaceholder = new JLabel();
    jlContentTypeFieldPlaceholder.setPreferredSize(cbContentType.getPreferredSize());
    jpGeneral.add(jlContentTypeFieldPlaceholder, c);
    
    
    
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(3, 7, 8, 3);
    jlSendDataAs = new JLabel("Send data as:", infoIcon, JLabel.LEFT);
    jlSendDataAs.setToolTipText("Select the format for the data to be sent to the remote server");
    jlSendDataAs.setLabelFor(cbSendDataAs);
    jpGeneral.add(jlSendDataAs, c);
    
    c.gridx++;
    c.insets = new Insets(3, 3, 8, 7);
    cbSendDataAs = new JComboBox(RESTActivity.DATA_FORMAT.values());
    cbSendDataAs.setEditable(false);
    jpGeneral.add(cbSendDataAs, c);
    
    
    c.gridx = 0;
    c.gridy++;
    c.insets = new Insets(3, 7, 8, 3);
    jlSendDataAsLabelPlaceholder = new JLabel();
    jlSendDataAsLabelPlaceholder.setPreferredSize(jlSendDataAs.getPreferredSize());
    jpGeneral.add(jlSendDataAsLabelPlaceholder, c);
    
    c.gridx++;
    c.insets = new Insets(3, 3, 8, 7);
    jlSendDataAsFieldPlaceholder = new JLabel();
    jlSendDataAsFieldPlaceholder.setPreferredSize(cbSendDataAs.getPreferredSize());
    jpGeneral.add(jlSendDataAsFieldPlaceholder, c);
    
    return (jpGeneral);
	}
	
	
	private JPanel createAdvancedTab()
	{
	  JPanel jpAdvanced = new JPanel(new GridBagLayout());
	  GridBagConstraints c = new GridBagConstraints();
	  
	  c.gridx = 0;
	  c.gridy = 0;
	  c.anchor = GridBagConstraints.WEST;
	  c.fill = GridBagConstraints.BOTH;
	  c.insets = new Insets(8, 10, 2, 4);
	  JLabel jlExpectHeaderInfoIcon = new JLabel(infoIcon);
	  jlExpectHeaderInfoIcon.setToolTipText("<html>Ticking this checkbox may significantly improve performance when<br>" +
	                                              "large volumes of data are sent to the remote server and a redirect<br>" +
	                                              "from the original URL to the one specified by the server is likely.<br>" +
	                                              "<br>" +
	                                              "However, this checkbox <b>must not</b> be ticked to allow this activity<br>" +
	                                              "to post updates to Twitter.</html>");
	  jpAdvanced.add(jlExpectHeaderInfoIcon, c);
	  
	  c.gridx++;
	  c.weightx = 1.0;
	  c.insets = new Insets(8, 0, 2, 8);
	  cbSendHTTPExpectHeader = new JCheckBox("Send HTTP Expect request-header field");
	  jpAdvanced.add(cbSendHTTPExpectHeader, c);
	  
	  
	  c.gridx = 0;
	  c.gridy++;
	  c.weightx = 0;
	  c.insets = new Insets(2, 10, 5, 4);
	  JLabel jlShowRedirectionOutputPortInfoIcon = new JLabel(infoIcon);
	  jlShowRedirectionOutputPortInfoIcon.setToolTipText("<html>\"Redirection\" output port displays the URL of the final redirect<br>" +
	  		                                               "that has yielded the output data on the \"Response Body\" port.</html>");
	  jpAdvanced.add(jlShowRedirectionOutputPortInfoIcon, c);
	  
	  c.gridx++;
	  c.weightx = 1.0;
	  c.insets = new Insets(2, 0, 5, 8);
	  cbShowRedirectionOutputPort = new JCheckBox("Show \"Redirection\" output port");
	  jpAdvanced.add(cbShowRedirectionOutputPort, c);
	  
	  
	  // this JLabel makes the rest of the content of the panel to go to the top of the tab
	  // (instead of being centered)
	  c.gridx = 0;
	  c.gridy++;
	  c.weightx = 0;
	  c.weighty = 1.0;
	  c.insets = new Insets(0, 0, 0, 0);
	  JLabel jlSpacer = new JLabel();
	  jpAdvanced.add(jlSpacer, c);
	  
	  
	  return (jpAdvanced);
	}
	
	
	/**
	 * Check that user values in the UI are valid.
	 */
	@Override
	public boolean checkValues()
	{
	  // HTTP method is a fixed selection combo-box - no validation required
	  
	  // URL signature must be present and be valid
	  String candidateURLSignature = tfURLSignature.getText().trim();
	  if (candidateURLSignature == null || candidateURLSignature.length() == 0) {
	    JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
	        "URL signature must not be empty", "REST Activity Configuration - Warning", JOptionPane.WARNING_MESSAGE);
	    return (false);
	  }
	  else {
	    try {
	      // test if any exceptions will be thrown - if not, proceed to other validations
	      URISignatureHandler.validate(candidateURLSignature);
	    }
	    catch (URISignatureParsingException e) {
	      JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
	          e.getMessage(), "REST Activity Configuration - Warning", JOptionPane.WARNING_MESSAGE);
	      return (false);
	    }
	  }
	  
	  // check if Accept header value is at least not empty
	  Object candidateAcceptHeaderValue = cbAccepts.getSelectedItem();
	  if (candidateAcceptHeaderValue == null || ((String)candidateAcceptHeaderValue).length() == 0) {
	    JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
          "Accept header value must not be empty", "REST Activity Configuration - Warning", JOptionPane.WARNING_MESSAGE);
      return (false);
	  }
	  
	  // check if Content-Type header value is at least not empty - only do this for those HTTP
	  // methods which actually use this value; otherwise, it doesn't really matter, as the value
	  // will not be stored to the bean anyway
	  if (RESTActivity.hasMessageBodyInputPort((RESTActivity.HTTP_METHOD)cbHTTPMethod.getSelectedItem())) {
	    Object candidateContentTypeHeaderValue = cbContentType.getSelectedItem();
	    if (candidateContentTypeHeaderValue == null || ((String)candidateContentTypeHeaderValue).length() == 0) {
	      JOptionPane.showMessageDialog(MainWindow.getMainWindow(),
	          "Content-Type header value must not be empty", "REST Activity Configuration - Warning", JOptionPane.WARNING_MESSAGE);
	      return (false);
	    }
	  }
	  
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
	public boolean isConfigurationChanged()
	{
	  HTTP_METHOD originalHTTPMethod = configBean.getHttpMethod();
		String originalURLSignature = configBean.getUrlSignature();
		String originalAcceptsHeaderValue = configBean.getAcceptsHeaderValue();
		String originalContentType = configBean.getContentTypeForUpdates();
		DATA_FORMAT originalOutgoingDataFormat = configBean.getOutgoingDataFormat();
		boolean originalSendHTTPExpectRequestHeader = configBean.getSendHTTPExpectRequestHeader();
		boolean originalShowRedirectionOutputPort = configBean.getShowRedirectionOutputPort();
		
		boolean contentTypeHasNotChanged =
		            (originalContentType == null && ((String)cbContentType.getSelectedItem()).length() == 0)
		            ||
		            (originalContentType != null && originalContentType.equals((String)cbContentType.getSelectedItem()));
		
		// true (changed) unless all fields match the originals
		return ! (originalHTTPMethod == (HTTP_METHOD)cbHTTPMethod.getSelectedItem() &&
		          originalURLSignature.equals(tfURLSignature.getText()) &&
		          originalAcceptsHeaderValue.equals((String)cbAccepts.getSelectedItem()) &&
		          contentTypeHasNotChanged &&
		          originalOutgoingDataFormat == (DATA_FORMAT)cbSendDataAs.getSelectedItem() &&
		          originalSendHTTPExpectRequestHeader == cbSendHTTPExpectHeader.isSelected() &&
		          originalShowRedirectionOutputPort == cbShowRedirectionOutputPort.isSelected());
	}
	
	
	/**
	 * Prepare a new configuration bean from the UI, to be returned with getConfiguration()
	 */
	@Override
	public void noteConfiguration()
	{
	  configBean = new RESTActivityConfigurationBean();
		
		// safe to cast, as it's the type of values that have been placed there
		configBean.setHttpMethod((RESTActivity.HTTP_METHOD)cbHTTPMethod.getSelectedItem());
		configBean.setUrlSignature(tfURLSignature.getText().trim());
		configBean.setAcceptsHeaderValue((String)cbAccepts.getSelectedItem());
	  configBean.setContentTypeForUpdates((String)cbContentType.getSelectedItem());
	  configBean.setOutgoingDataFormat((DATA_FORMAT)cbSendDataAs.getSelectedItem());
	  configBean.setSendHTTPExpectRequestHeader(cbSendHTTPExpectHeader.isSelected());
	  configBean.setShowRedirectionOutputPort(cbShowRedirectionOutputPort.isSelected());
	}
	
	
	/**
	 * Update GUI from a changed configuration bean (perhaps by undo / redo).
	 */
	@Override
	public void refreshConfiguration()
	{
		configBean = activity.getConfiguration();
		
		cbHTTPMethod.setSelectedItem(configBean.getHttpMethod());
		tfURLSignature.setText(configBean.getUrlSignature());
		cbAccepts.setSelectedItem(configBean.getAcceptsHeaderValue());
	  cbContentType.setSelectedItem(configBean.getContentTypeForUpdates());
	  cbSendDataAs.setSelectedItem(configBean.getOutgoingDataFormat());
	  cbSendHTTPExpectHeader.setSelected(configBean.getSendHTTPExpectRequestHeader());
	  cbShowRedirectionOutputPort.setSelected(configBean.getShowRedirectionOutputPort());
	}
}
