/*******************************************************************************
 * Copyright (C) 2007 The University of Manchester   
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
package net.sf.taverna.t2.wadl.ui.serviceprovider;

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

import net.sf.taverna.t2.activities.rest.ui.servicedescription.RESTActivityIcon;
import net.sf.taverna.t2.workbench.MainWindow;
import net.sf.taverna.t2.workbench.helper.HelpEnabledDialog;

import org.apache.log4j.Logger;

/**
 * Dialog that lets user specify a URL of a WADL service they want 
 * to add to the Service Panel. In the case the WADL URL is behind
 * HTTPS or service's endpoints require HTTPS it will ask user to confirm
 * if they want to trust it. 
 * 
 * @author Alex Nenadic
 *
 */
@SuppressWarnings("serial")
public abstract class AddWadlServiceDialog extends HelpEnabledDialog {

	private JTextField wadlLocationField;
	private Logger logger = Logger.getLogger(AddWadlServiceDialog.class);

	public AddWadlServiceDialog()  {
		super(MainWindow.getMainWindow(), "Add WADL service", true, null); // create a non-modal dialog
		initComponents();
		setLocationRelativeTo(getParent());
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel(new GridBagLayout());
		mainPanel.setBorder(new EmptyBorder(10,10,10,10));
		
		JLabel wadlLocatitionLabel = new JLabel("WADL location",RESTActivityIcon.getRESTActivityIcon(), JLabel.LEFT);		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weighty = 0.0;
		
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 0);
		mainPanel.add(wadlLocatitionLabel, gbc);
        
		wadlLocationField = new JTextField("http://somehost/service?wadl");
		gbc.weightx = 1.0;
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 10, 0, 5);		
		mainPanel.add(wadlLocationField, gbc);
		
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
		final String wadlURLString = wadlLocationField.getText().trim();
		new Thread("Adding WADL " + wadlURLString) {
			public void run() {
				// Only add the service provider for this service if service URL
				// starts with 'http'
				// or if it starts with 'https' and user explicitly said they
				// wanted to trust this service.
				/*
				 * if (shouldTrust(wadlURLString)){ addRegistry(wadlURLString);
				 * }
				 */
				try {
					URL url = new URL(wadlURLString);
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
						// wadl
						// service provider for this service to
						// the registry
					} finally {
						try {
							connection.getInputStream().close();
						} catch (IOException ex) {
						}
					}
					addRegistry(wadlURLString);
				} catch (Exception ex) { // anything failed
					JOptionPane.showMessageDialog(null,
							"Could not read the wadl definition from "
									+ wadlURLString + ":\n" + ex,
							"Could not add wadl service",
							JOptionPane.ERROR_MESSAGE);

					logger.error(
							"Failed to add wadl service provider for service: "
									+ wadlURLString, ex);

				}
			};
		}.start();
		closeDialog();
    }

    /**
     * If wadl service's URL starts with 'https' - asks user
     * whether to trust it or not. If it starts with 'http' - 
     * does not ask anything as the service is implicitly trusted (weird but true).
     */
	protected abstract void addRegistry(String wadl);
	
	/**
	 * Closes the dialog.
	 */
	private void closeDialog() {
		setVisible(false);
		dispose();
	}
}