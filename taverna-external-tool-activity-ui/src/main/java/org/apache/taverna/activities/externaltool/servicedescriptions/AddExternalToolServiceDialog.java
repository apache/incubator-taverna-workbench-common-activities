package org.apache.taverna.activities.externaltool.servicedescriptions;
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.taverna.workbench.MainWindow;
import org.apache.taverna.workbench.helper.HelpEnabledDialog;

import org.apache.log4j.Logger;

/**
 * Dialog that lets user specify a URL of a Tool service they want 
 * to add to the Service Panel. In the case the Tool URL is behind
 * HTTPS or service's endpoints require HTTPS it will ask user to confirm
 * if they want to trust it. 
 * 
 * @author Alex Nenadic
 *
 */
@SuppressWarnings("serial")
public abstract class AddExternalToolServiceDialog extends HelpEnabledDialog {

	private JTextField toolLocationField;
	private Logger logger = Logger.getLogger(AddExternalToolServiceDialog.class);

	public AddExternalToolServiceDialog()  {
		super(MainWindow.getMainWindow(), "Add tool service", true, null); // create a non-modal dialog
		initComponents();
		setLocationRelativeTo(getParent());
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel toolLocatitionLabel = new JLabel("Tool registry location",ExternalToolActivityIcon.getExternalToolIcon(), JLabel.LEFT);		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 0.0;
		
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);
		mainPanel.add(toolLocatitionLabel, gbc);
        
		toolLocationField = new JTextField("http://taverna.nordugrid.org/sharedRepository/xml.php");
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 5);		
		mainPanel.add(toolLocationField, gbc);
		
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
		final String toolURLString = toolLocationField.getText().trim();
		new Thread("Adding tool " + toolURLString) {
			public void run() {
				// Only add the service provider for this service if service URL
				// starts with 'http'
				// or if it starts with 'https' and user explicitly said they
				// wanted to trust this service.
				/*
				 * if (shouldTrust(toolURLString)){ addRegistry(toolURLString);
				 * }
				 */
				try {
					URL url = new URL(toolURLString);
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
						// tool
						// service provider for this service to
						// the registry
					} finally {
						try {
							connection.getInputStream().close();
						} catch (IOException ex) {
						}
					}
					addRegistry(toolURLString);
				} catch (Exception ex) { // anything failed
					JOptionPane.showMessageDialog(null,
							"Could not read the tool descriptions from "
									+ toolURLString + ":\n" + ex,
							"Could not add tool service",
							JOptionPane.ERROR_MESSAGE);

					logger.error(
							"Failed to add tool description provider for service: "
									+ toolURLString, ex);

				}
			};
		}.start();
		closeDialog();
    }

	protected abstract void addRegistry(String tool);	
	
	/**
	 * Closes the dialog.
	 */
	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}
