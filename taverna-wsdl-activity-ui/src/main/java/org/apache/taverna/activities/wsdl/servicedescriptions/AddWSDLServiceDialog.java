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

package org.apache.taverna.activities.wsdl.servicedescriptions;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.taverna.workbench.MainWindow;
import org.apache.taverna.workbench.helper.HelpEnabledDialog;

import org.apache.log4j.Logger;

/**
 * Dialog that lets user specify a URL of a WSDL service they want
 * to add to the Service Panel. In the case the WSDL URL is behind
 * HTTPS or service's endpoints require HTTPS it will ask user to confirm
 * if they want to trust it.
 *
 * @author Alex Nenadic
 *
 */
@SuppressWarnings("serial")
public abstract class AddWSDLServiceDialog extends HelpEnabledDialog {

	private JTextField wsdlLocationField;
	private Logger logger = Logger.getLogger(AddWSDLServiceDialog.class);

	public AddWSDLServiceDialog()  {
		super(MainWindow.getMainWindow(), "Add WSDL service", true, null); // create a non-modal dialog
		initComponents();
		setLocationRelativeTo(getParent());
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10,10,10,10));

		JLabel wsdlLocatitionLabel = new JLabel("WSDL location",WSDLActivityIcon.getWSDLIcon(), JLabel.LEFT);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 0.0;

		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);
		mainPanel.add(wsdlLocatitionLabel, gbc);

		wsdlLocationField = new JTextField("http://somehost/service?wsdl");
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 5);
		mainPanel.add(wsdlLocationField, gbc);

	    final JButton addServiceButton = new JButton("Add");
	    addServiceButton.addActionListener(new ActionListener()
	        {
	            public void actionPerformed(ActionEvent evt)
	            {
	                addPressed();
	            }
	        });

	    // When user presses "Return" key fire the action on the "Add" button
	    addServiceButton.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					addPressed();
				}
			}
		});
		getRootPane().setDefaultButton(addServiceButton);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonsPanel.add(addServiceButton);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		setSize(getPreferredSize());
        pack();
	}

    /**
     * 'Add service' button pressed or otherwise activated.
     */
    private void addPressed()
    {
		final String wsdlURLString = wsdlLocationField.getText().trim();
		new Thread("Adding WSDL " + wsdlURLString) {
			public void run() {
				// Only add the service provider for this service if service URL
				// starts with 'http'
				// or if it starts with 'https' and user explicitly said they
				// wanted to trust this service.
				/*
				 * if (shouldTrust(wsdlURLString)){ addRegistry(wsdlURLString);
				 * }
				 */
				try {
					URL url = new URL(wsdlURLString);
					URLConnection connection = url.openConnection();
					try {
						// If the url starts with 'https' - security hook for
						// https connection's trust manager
						// will be engaged and user will be asked automatically
						// if they want
						// to trust the connection (if it is not already
						// trusted). If the urls starts with 'http' -
						// this will not have any effect apart from checking if
						// we can open a connection.
						connection.connect(); // if this does not fail - add the
						// WSDL
						// service provider for this service to
						// the registry
					} finally {
						try {
							connection.getInputStream().close();
						} catch (IOException ex) {
						}
					}
					addRegistry(wsdlURLString);
				} catch (Exception ex) { // anything failed
					JOptionPane.showMessageDialog(null,
							"Could not read the WSDL definition from "
									+ wsdlURLString + ":\n" + ex,
							"Could not add WSDL service",
							JOptionPane.ERROR_MESSAGE);

					logger.error(
							"Failed to add WSDL service provider for service: "
									+ wsdlURLString, ex);

				}
			};
		}.start();
		closeDialog();
    }

    /**
     * If WSDL service's URL starts with 'https' - asks user
     * whether to trust it or not. If it starts with 'http' -
     * does not ask anything as the service is implicitly trusted (weird but true).
     */
	protected abstract void addRegistry(String wsdl);

	/**
	 * Checks if a service is trusted and if not - asks user if they want to trust it.
	 */
//	public boolean shouldTrust(String wsdlURLString){
//		try {
//			URI wsdlURI = new URI(wsdlURLString);
//			URL wsdlURL = wsdlURI.toURL();
//			String protocol = wsdlURL.getProtocol();
//			if (protocol.toLowerCase().startsWith("https")){
//				logger.info("Checking if service " + wsdlURLString + " is already trusted.");
//				// Check if opening an HTTPS connection will cause a SSLHandshakeException.
//				// This is most probably due to the fact that we do not have this service's
//				// certificate in Credential Manager's truststore
//				try {
//					HttpsURLConnection httpsConnection;
//					httpsConnection = (HttpsURLConnection) wsdlURL.openConnection();
//					httpsConnection.connect();
//					logger.info("HTTPS works out of the box for service " + wsdlURLString);
//					return true; // Opening HTTPS connection worked - so we trust this service already
//				}
//				catch (SSLException sslex) { // most probably due to the fact that service is not trusted, i.e. its certificate is not in Credential Manager's Truststore
//					logger.info("Service " + wsdlURLString + " is not trusted out of the box. Trying to fetch its certificate.");
//					logger.info("The SSLException was caused by: " + sslex.getCause());
//						// Handshake most probably failed as we do not already trust this service -
//						// fetch its certificate and ask user if they want to add this service as trusted
//					try {
//
//						// This controls SSL socket creation for HTTPS connections
//						// per thread so the damage of switching off certificates
//						// verification is limited
//						ThreadLocalSSLSocketFactory.install();
//						// switch certificate checking off for a moment so we can fetch
//						// service's certificate
//						ThreadLocalSSLSocketFactory.startTrustingEverything();
//
//						HttpsURLConnection httpsConnection;
//						httpsConnection = (HttpsURLConnection) wsdlURL
//								.openConnection();
//						httpsConnection.connect();
//						// Stop being overly trusting
//						ThreadLocalSSLSocketFactory.stopTrustingEverything();
//						Certificate[] certificates = httpsConnection
//								.getServerCertificates();
//						logger.info("Need to ask user if they want to trust service " + wsdlURLString);
//						// Ask user if they want to trust this service
//						ConfirmTrustedCertificateDialog confirmCertTrustDialog = new ConfirmTrustedCertificateDialog(
//								this, "Untrusted HTTPS connection", true,
//								(X509Certificate) certificates[0]);
//						confirmCertTrustDialog.setLocationRelativeTo(null);
//						confirmCertTrustDialog.setVisible(true);
//						boolean shouldTrust = confirmCertTrustDialog
//								.shouldTrust();
//						if (shouldTrust) {
//							try {
//								CredentialManager credManager = CredentialManager
//										.getInstance();
//								credManager
//										.saveTrustedCertificate((X509Certificate) certificates[0]);
//								return true;
//							} catch (CMException cme) {
//								logger
//										.error(
//												"Failed to add WSDL service provider for service: "
//														+ wsdlURLString
//														+ " . Credential Manager failed to "
//														+ "save trusted certificate.",
//												cme);
//								return false;
//							}
//						} else {
//							// Do not even add a WSDL service provider for this
//							// service and tell user the service will not be
//							// added to Service Panel
//							JOptionPane
//									.showMessageDialog(
//											this,
//											"As you refused to trust it, the service will not be added to Service Panel.",
//											"Add WSDL service",
//											JOptionPane.INFORMATION_MESSAGE);
//							return false;
//						}
//					} catch (Exception e1) {
//						logger
//								.error(
//										"Failed to add WSDL service provider for service: "
//												+ wsdlURLString
//												+ ". 'Trust everyone' HTTPS connection failed.",
//										e1);
//						return false;
//					} finally {// switch it off here as well if some unexpected exception occurred
//						ThreadLocalSSLSocketFactory.stopTrustingEverything();
//					}
//
//				} catch (Exception e2) {
//					logger.error("Failed to add WSDL service provider for service: "+ wsdlURLString+". Connecting to service failed.", e2);
//					return false;
//				}
//			}
//			else{ // protocol starts with 'http'
//				return true;
//			}
//		} catch (MalformedURLException e3) {
//			logger.error("Failed to add WSDL service provider: URL "+ wsdlURLString+" was malformed.", e3);
//			return false;
//		} catch (URISyntaxException e4) {
//			logger.error("Failed to add WSDL service provider: URI "+ wsdlURLString+" could not be parsed.", e4);
//			return false;
//		}
//	}

	/**
	 * Closes the dialog.
	 */
	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}
