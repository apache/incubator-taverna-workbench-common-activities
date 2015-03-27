/*
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements. See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership. The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.apache.taverna.activities.wsdl.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URI;

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

import org.apache.taverna.activities.wsdl.security.SecurityProfiles;
import org.apache.taverna.lang.ui.DialogTextArea;
import org.apache.taverna.security.credentialmanager.CredentialManager;
import org.apache.taverna.workbench.ui.credentialmanager.CredentialManagerUI;
import org.apache.taverna.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;
import org.apache.taverna.scufl2.api.activity.Activity;

/**
 * Configuration dialog for WSDL activity.
 *
 * @author Alex Nenadic
 */
@SuppressWarnings("serial")
public class WSDLActivityConfigurationView extends ActivityConfigurationPanel implements ItemListener {

	private CredentialManager credentialManager;
	private CredentialManagerUI credManagerUI;

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
	private String[] passwordTypes = new String[] { PLAINTEXT_PASSWORD, DIGEST_PASSWORD };
	private String[] tooltips = new String[] {
			"Password will be sent in plaintext (which is OK if service is using HTTPS)",
			"Password will be digested (cryptographically hashed) before sending" };
	private JComboBox<String> passwordTypeComboBox;
	private JCheckBox addTimestampCheckBox;
	private JButton setHttpUsernamePasswordButton;
	private JButton setWsdlUsernamePasswordButton;

	// private Logger logger = Logger.getLogger(WSDLActivityConfigurationView.class);

	public WSDLActivityConfigurationView(Activity activity, CredentialManager credentialManager) {
		super(activity);
		this.credentialManager = credentialManager;
		initialise();
	}

	@Override
	protected void initialise() {
		super.initialise();

		int gridy = 0;

		// title panel
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);
		JLabel titleLabel = new JLabel("Security configuration");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13.5f));
		titleLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
		DialogTextArea titleMessage = new DialogTextArea(
				"Select a security profile for the service");
		titleMessage.setMargin(new Insets(5, 20, 10, 10));
		titleMessage.setFont(titleMessage.getFont().deriveFont(11f));
		titleMessage.setEditable(false);
		titleMessage.setFocusable(false);
		titlePanel.setBorder(new EmptyBorder(10, 10, 0, 10));
		titlePanel.add(titleLabel, BorderLayout.NORTH);
		titlePanel.add(titleMessage, BorderLayout.CENTER);
		addDivider(titlePanel, SwingConstants.BOTTOM, true);

		// Main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		// Create the radio buttons
		noSecurityRadioButton = new JRadioButton("None");
		noSecurityRadioButton.addItemListener(this);

		wsSecurityAuthNRadioButton = new JRadioButton(
				"WS-Security username and password authentication");
		wsSecurityAuthNRadioButton.addItemListener(this);

		httpSecurityAuthNRadioButton = new JRadioButton("HTTP username and password authentication");
		httpSecurityAuthNRadioButton.addItemListener(this);

		// Group the radio buttons
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
		// addDivider(noSecurityLabel, SwingConstants.BOTTOM, false);
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
				if (credManagerUI == null) {
					credManagerUI = new CredentialManagerUI(credentialManager);
				}
				credManagerUI.newPasswordForService(URI.create(getJson().get("operation")
						.get("wsdl").textValue()));
			}
		};

		httpSecurityAuthNLabel = new JLabel(
				"Service requires HTTP username and password in order to authenticate the user");
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

		wsSecurityAuthNLabel = new JLabel(
				"Service requires WS-Security username and password in order to authenticate the user");
		wsSecurityAuthNLabel.setFont(wsSecurityAuthNLabel.getFont().deriveFont(11f));
		gbc.gridx = 0;
		gbc.gridy = gridy++;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(0, 40, 0, 0);
		mainPanel.add(wsSecurityAuthNLabel, gbc);

		// Password type list
		passwordTypeComboBox = new JComboBox<>(passwordTypes);
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

		// Enable/disable controls based on what is the current security profiles
		if (!getJson().has("securityProfile")) {
			noSecurityRadioButton.setSelected(true);
		} else {
			URI securityProfile = URI.create(getJson().get("securityProfile").textValue());
			if (securityProfile.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_PLAINTEXTPASSWORD)
					|| securityProfile
							.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_DIGESTPASSWORD)
					|| securityProfile
							.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD)
					|| securityProfile
							.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD)) {
				wsSecurityAuthNRadioButton.setSelected(true);
			}
			if (securityProfile.equals(SecurityProfiles.HTTP_BASIC_AUTHN)
					|| securityProfile.equals(SecurityProfiles.HTTP_DIGEST_AUTHN)) {
				httpSecurityAuthNRadioButton.setSelected(true);
			}
			if (securityProfile.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_PLAINTEXTPASSWORD)
					|| securityProfile
							.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD)) {
				passwordTypeComboBox.setSelectedItem(PLAINTEXT_PASSWORD);
			} else if (securityProfile
					.equals(SecurityProfiles.WSSECURITY_USERNAMETOKEN_DIGESTPASSWORD)
					|| securityProfile
							.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD)) {
				passwordTypeComboBox.setSelectedItem(DIGEST_PASSWORD);
			}
			if (securityProfile
					.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD)
					|| securityProfile
							.equals(SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD)) {
				addTimestampCheckBox.setSelected(true);
			} else {
				addTimestampCheckBox.setSelected(false);
			}
		}

		// Put everything together
		JPanel layoutPanel = new JPanel(new BorderLayout());
		layoutPanel.add(titlePanel, BorderLayout.NORTH);
		layoutPanel.add(mainPanel, BorderLayout.CENTER);
		layoutPanel.setPreferredSize(new Dimension(550, 400));

		add(layoutPanel);
	}

	@Override
	public boolean checkValues() {
		return true;
	}

	@Override
	public void noteConfiguration() {

		if (noSecurityRadioButton.isSelected()) {
			getJson().remove("securityProfile"); // no security required
		} else if (httpSecurityAuthNRadioButton.isSelected()) {
			getJson().put("securityProfile", SecurityProfiles.HTTP_BASIC_AUTHN.toString());
		} else if (wsSecurityAuthNRadioButton.isSelected()) { // plaintext password
			if (passwordTypeComboBox.getSelectedItem().equals(PLAINTEXT_PASSWORD)) {
				if (addTimestampCheckBox.isSelected()) {
					getJson().put(
							"securityProfile",
							SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_PLAINTEXTPASSWORD
									.toString());
				} else {
					getJson().put("securityProfile",
							SecurityProfiles.WSSECURITY_USERNAMETOKEN_PLAINTEXTPASSWORD.toString());
				}
			} else { // digest password
				if (addTimestampCheckBox.isSelected()) {
					getJson().put(
							"securityProfile",
							SecurityProfiles.WSSECURITY_TIMESTAMP_USERNAMETOKEN_DIGESTPASSWORD
									.toString());
				} else {
					getJson().put("securityProfile",
							SecurityProfiles.WSSECURITY_USERNAMETOKEN_DIGESTPASSWORD.toString());
				}
			}
		}
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
		} else if (source == httpSecurityAuthNRadioButton) {
			noSecurityLabel.setEnabled(false);
			httpSecurityAuthNLabel.setEnabled(true);
			wsSecurityAuthNLabel.setEnabled(false);
			passwordTypeComboBox.setEnabled(false);
			setHttpUsernamePasswordButton.setEnabled(true);
			setWsdlUsernamePasswordButton.setEnabled(false);
			addTimestampCheckBox.setEnabled(false);
		} else if (source == wsSecurityAuthNRadioButton) {
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
	 */
	class ComboBoxTooltipRenderer extends BasicComboBoxRenderer {
		public Component getListCellRendererComponent(JList list, Object value, int index,
				boolean isSelected, boolean cellHasFocus) {
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

}
