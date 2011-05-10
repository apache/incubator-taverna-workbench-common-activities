/*******************************************************************************
 * Copyright (C) 2007-2008 The University of Manchester   
 * 
 *  Modifications to the initial code base are copyright of their
 *  respective authors, or their employers as appropriate.
 * 
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *    
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *    
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 ******************************************************************************/
package net.sf.taverna.t2.activities.wsdl.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.Dialog;

//import org.apache.log4j.Logger;

import net.sf.taverna.t2.activities.wsdl.WSDLActivityConfigurationBean;
import net.sf.taverna.t2.activities.wsdl.security.SecurityProfiles;
import net.sf.taverna.t2.lang.ui.DialogTextArea;
import net.sf.taverna.t2.workbench.MainWindow;
import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;
import net.sf.taverna.t2.workbench.ui.credentialmanager.CredentialManagerUI;

/**
 * Configuration dialog for WSDL activity.
 * 
 * @author Alex Nenadic
 *
 */
@SuppressWarnings("serial")
public class WSDLActivityConfigurationView extends HelpEnabledDialog implements ItemListener{

	private WSDLActivityConfigurationBean oldBean;
	private WSDLActivityConfigurationBean newBean;
	
	private ButtonGroup buttonGroup;
	private JRadioButton noSecurityRadioButton;
	private JLabel noSecurityLabel;
	private JRadioButton httpSecurityAuthNRadioButton;
	private JLabel httpSecurityAuthNLabel;
	private JRadioButton wsSecurityAuthNRadioButton;
	private JLabel wsSecurityAuthNLabel;

	// Password types
	private final String PLAINTEXT_PASSWORD = "Plaintext password";	
	private final String DIGEST_PASSWORD = "Digest password";
	private String[] passwordTypes = new String[]{PLAINTEXT_PASSWORD, DIGEST_PASSWORD};
	private String[] tooltips = new String[]{ "Password will be sent in plaintext (which is OK if service is using HTTPS)", 
			"Password will be digested (cryptographically hashed) before sending"};
	private JComboBox passwordTypeComboBox;
	private JCheckBox addTimestampCheckBox;
	private JButton setHttpUsernamePasswordButton;
	private JButton setWsdlUsernamePasswordButton;
		
	//private  Logger logger = Logger.getLogger(WSDLActivityConfigurationView.class);

	public WSDLActivityConfigurationView(WSDLActivityConfigurationBean bean){
    	super(MainWindow.getMainWindow(), "Web service configuration", true, null); //create a modal dialog
    	this.oldBean = bean;
    	newBean = copy(oldBean);
    	initComponents();
	}
	
	public WSDLActivityConfigurationView(Frame owner, WSDLActivityConfigurationBean bean){
    	super(owner, "WSDL service configuration", true, null); //create a modal dialog
    	this.oldBean = bean;
    	newBean = copy(oldBean);
    	initComponents();
	}

	/**
	 * Creates a copy of the bean.
	 */
	private WSDLActivityConfigurationBean copy(
			WSDLActivityConfigurationBean oldBean) {

		WSDLActivityConfigurationBean newBean = new WSDLActivityConfigurationBean();
		newBean.setOperation(oldBean.getOperation());
		newBean.setWsdl(oldBean.getWsdl());
		newBean.setSecurityProfile(oldBean.getSecurityProfile());
		return newBean;
	}

	private void initComponents() {
		
		this.setModalityType(Dialog.ModalityType.DOCUMENT_MODAL);
		
		int gridy = 0;
		
		// title panel
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);
		JLabel titleLabel = new JLabel("Security configuration");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13.5f));
		titleLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
		DialogTextArea titleMessage = new DialogTextArea("Select a security profile for the service");
		titleMessage.setMargin(new Insets(5, 20, 10, 10));
		titleMessage.setFont(titleMessage.getFont().deriveFont(11f));
		titleMessage.setEditable(false);
		titleMessage.setFocusable(false);
		titlePanel.setBorder( new EmptyBorder(10, 10, 0, 10));
		titlePanel.add(titleLabel, BorderLayout.NORTH);
		titlePanel.add(titleMessage, BorderLayout.CENTER);
		addDivider(titlePanel, SwingConstants.BOTTOM, true);	
		
		// Main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			
	    //Create the radio buttons
	    noSecurityRadioButton = new JRadioButton("None");
	    noSecurityRadioButton.addItemListener(this);

	    wsSecurityAuthNRadioButton = new JRadioButton("WS-Security username and password authentication");
	    wsSecurityAuthNRadioButton.addItemListener(this);
	    
	    httpSecurityAuthNRadioButton = new JRadioButton("HTTP username and password authentication");
	    httpSecurityAuthNRadioButton.addItemListener(this);
	    
	    //Group the radio buttons
	    buttonGroup = new ButtonGroup();
	    buttonGroup.add(noSecurityRadioButton);
	    buttonGroup.add(wsSecurityAuthNRadioButton);
	    buttonGroup.add(httpSecurityAuthNRadioButton);
			
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);
		mainPanel.add(noSecurityRadioButton, gbc);
		
	    noSecurityLabel = new JLabel("Service requires no security");
	    noSecurityLabel.setFont(noSecurityLabel.getFont().deriveFont(11f));
//		addDivider(noSecurityLabel, SwingConstants.BOTTOM, false);
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 40, 10, 10);
		mainPanel.add(noSecurityLabel, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);
		mainPanel.add(httpSecurityAuthNRadioButton, gbc);
		
		ActionListener usernamePasswordListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				// Get Credential Manager UI to get the username and password for the service
				CredentialManagerUI credManagerUI = CredentialManagerUI.getInstance();
				if (credManagerUI != null)
					credManagerUI.newPasswordForService(oldBean.getWsdl());
			}
		};
		
	    httpSecurityAuthNLabel = new JLabel("Service requires HTTP username and password in order to authenticate the user");
	    httpSecurityAuthNLabel.setFont(httpSecurityAuthNLabel.getFont().deriveFont(11f));
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 40, 10, 10);
		mainPanel.add(httpSecurityAuthNLabel, gbc);
		
		// Set username and password button;
		setHttpUsernamePasswordButton = new JButton("Set username and password");
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 40, 10, 10);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0; // add any vertical space to this component
		mainPanel.add(setHttpUsernamePasswordButton, gbc);
		setHttpUsernamePasswordButton.addActionListener(usernamePasswordListener);
		
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);
		mainPanel.add(wsSecurityAuthNRadioButton, gbc);
		
	    wsSecurityAuthNLabel = new JLabel("Service requires WS-Security username and password in order to authenticate the user");
	    wsSecurityAuthNLabel.setFont(wsSecurityAuthNLabel.getFont().deriveFont(11f));
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 40, 0, 0);
		mainPanel.add(wsSecurityAuthNLabel, gbc);
		
		// Password type list
		passwordTypeComboBox = new JComboBox(passwordTypes);
		passwordTypeComboBox.setRenderer(new ComboBoxTooltipRenderer());
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(10, 40, 0, 0);
		mainPanel.add(passwordTypeComboBox, gbc);
		
		// 'Add timestamp' checkbox
		addTimestampCheckBox = new JCheckBox("Add a timestamp to SOAP message");
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 40, 10, 10);
		mainPanel.add(addTimestampCheckBox, gbc);

		// Set username and password button;
		setWsdlUsernamePasswordButton = new JButton("Set username and password");
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.insets = new Insets(0, 40, 10, 10);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0; // add any vertical space to this component
		mainPanel.add(setWsdlUsernamePasswordButton, gbc);
		setWsdlUsernamePasswordButton.addActionListener(usernamePasswordListener);
		
		addDivider(mainPanel, SwingConstants.BOTTOM, true);	
		
		// OK/Cancel button panel
		JPanel okCancelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				okPressed();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cancelPressed();
			}
		});
		okCancelPanel.add(cancelButton);
		okCancelPanel.add(okButton);
		
		// Enable/disable controls based on what is the current security profiles
	    String securityProfile = oldBean.getSecurityProfile();
		if (securityProfile == null){
	    	noSecurityRadioButton.setSelected(true);
	    }
	    else{
		    if (securityProfile.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_PLAINTEXTPASSWORD) ||
		    		securityProfile.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_DIGESTPASSWORD) ||
		    		securityProfile.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD) ||
		    		securityProfile.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD) ){
		    	wsSecurityAuthNRadioButton.setSelected(true);
		    }
		    if (securityProfile.equals(SecurityProfiles.HTTP_BASIC_AUTHN) ||
		    		securityProfile.equals(SecurityProfiles.HTTP_DIGEST_AUTHN)) {
		    	httpSecurityAuthNRadioButton.setSelected(true);
		    }
		    if (securityProfile.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_PLAINTEXTPASSWORD) ||
		    		securityProfile.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD)){
		    	passwordTypeComboBox.setSelectedItem(PLAINTEXT_PASSWORD);
		    }
		    else if (securityProfile.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_DIGESTPASSWORD) ||
		    		securityProfile.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD)){
		    	passwordTypeComboBox.setSelectedItem(DIGEST_PASSWORD);
		    }
		    if (securityProfile.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD) ||
		    		securityProfile.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD)){
		    	addTimestampCheckBox.setSelected(true);
		    }
		    else {
		    	addTimestampCheckBox.setSelected(false);
		    }
	    }
			    
		// Put everything together
	    JPanel layoutPanel = new JPanel(new BorderLayout());
	    layoutPanel.add(titlePanel, BorderLayout.NORTH);
	    layoutPanel.add(mainPanel, BorderLayout.CENTER);
	    layoutPanel.add(okCancelPanel, BorderLayout.SOUTH);
	    layoutPanel.setPreferredSize(new Dimension(550,400));
	    
	    this.getContentPane().add(layoutPanel);
	    this.pack();
	}

	private void okPressed() {

		if (noSecurityRadioButton.isSelected()){
			newBean.setSecurityProfile(null); // no security required
		}
		else if (httpSecurityAuthNRadioButton.isSelected()) {
				newBean.setSecurityProfile(SecurityProfiles.HTTP_BASIC_AUTHN);
		}
		else if (wsSecurityAuthNRadioButton.isSelected()){ // plaintext password
			if (passwordTypeComboBox.getSelectedItem().equals(PLAINTEXT_PASSWORD)){
				if (addTimestampCheckBox.isSelected()){
					newBean.setSecurityProfile(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD);
				}
				else{
					newBean.setSecurityProfile(SecurityProfiles.WSSECURITY_USERNAMETOKEN_PLAINTEXTPASSWORD);
				}
			}
			else { //digest password
				if (addTimestampCheckBox.isSelected()){
					newBean.setSecurityProfile(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD);
				}
				else{
					newBean.setSecurityProfile(SecurityProfiles.WSSECURITY_USERNAMETOKEN_DIGESTPASSWORD);
				}
			}
		}
		closeDialog();				
	}

	private void cancelPressed() {
		newBean = null; // to indicate that user has cancelled
		closeDialog();		
	}
	
    /**
     * Close the dialog.
     */
    private void closeDialog()
    {
        setVisible(false);
        dispose();
    }

    /**
     * Disable/enable items on the panel based on this radio button
     * has been selected.
     */
	public void itemStateChanged(ItemEvent e) {
	      
		Object source = e.getItemSelectable();
		if (source == noSecurityRadioButton) {
			httpSecurityAuthNLabel.setEnabled(false);
			wsSecurityAuthNLabel.setEnabled(false);
			passwordTypeComboBox.setEnabled(false);
			setHttpUsernamePasswordButton.setEnabled(false);
			setWsdlUsernamePasswordButton.setEnabled(false);
			addTimestampCheckBox.setEnabled(false);
			
			noSecurityLabel.setEnabled(true);
		}
		else if (source == httpSecurityAuthNRadioButton) {
			noSecurityLabel.setEnabled(false);
			httpSecurityAuthNLabel.setEnabled(true);
			wsSecurityAuthNLabel.setEnabled(false);
			passwordTypeComboBox.setEnabled(false);
			setHttpUsernamePasswordButton.setEnabled(true);
			setWsdlUsernamePasswordButton.setEnabled(false);
			addTimestampCheckBox.setEnabled(false);
		}
		else if (source == wsSecurityAuthNRadioButton) {
			noSecurityLabel.setEnabled(false);
			httpSecurityAuthNLabel.setEnabled(false);			
			wsSecurityAuthNLabel.setEnabled(true);
			passwordTypeComboBox.setEnabled(true);
			setHttpUsernamePasswordButton.setEnabled(false);
			setWsdlUsernamePasswordButton.setEnabled(true);
			addTimestampCheckBox.setEnabled(true);
		}
	}
	
	
/**
 * A renderer for JComboBox that will display a tooltip for 
 * the selected item.
 *
 */
	class ComboBoxTooltipRenderer extends BasicComboBoxRenderer {
	    public Component getListCellRendererComponent(JList list, Object value,
	        int index, boolean isSelected, boolean cellHasFocus) {
	      if (isSelected) {
	        setBackground(list.getSelectionBackground());
	        setForeground(list.getSelectionForeground());
	        if (-1 < index) {
	          list.setToolTipText(tooltips[index]);
	        }
	      } else {
	        setBackground(list.getBackground());
	        setForeground(list.getForeground());
	      }
	      setFont(list.getFont());
	      setText((value == null) ? "" : value.toString());
	      return this;
	    }
	 }
	
	/**
	 * Adds a light gray or etched border to the top or bottom of a JComponent.
	 * 
	 * @author David Withers
	 * @param component
	 */
	protected void addDivider(JComponent component, final int position, final boolean etched) {
		component.setBorder(new Border() {
			private final Color borderColor = new Color(.6f, .6f, .6f);
			
			public Insets getBorderInsets(Component c) {
				if (position == SwingConstants.TOP) {
					return new Insets(5, 0, 0, 0);
				} else {
					return new Insets(0, 0, 5, 0);
				}
			}

			public boolean isBorderOpaque() {
				return false;
			}

			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				if (position == SwingConstants.TOP) {
					if (etched) {
						g.setColor(borderColor);
						g.drawLine(x, y, x + width, y);
						g.setColor(Color.WHITE);
						g.drawLine(x, y + 1, x + width, y + 1);
					} else {
						g.setColor(Color.LIGHT_GRAY);
						g.drawLine(x, y, x + width, y);
					}
				} else {
					if (etched) {
						g.setColor(borderColor);
						g.drawLine(x, y + height - 2, x + width, y + height - 2);
						g.setColor(Color.WHITE);
						g.drawLine(x, y + height - 1, x + width, y + height - 1);
					} else {
						g.setColor(Color.LIGHT_GRAY);
						g.drawLine(x, y + height - 1, x + width, y + height - 1);
					}
				}
			}

		});
	}

	/**
	 * Gets the new bean after user has finished with configuring it.
	 */
	public WSDLActivityConfigurationBean getNewBean() {
		return newBean;
	}
}
