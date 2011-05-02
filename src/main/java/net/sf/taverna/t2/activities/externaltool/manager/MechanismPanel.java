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
package net.sf.taverna.t2.activities.externaltool.manager;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import net.sf.taverna.t2.spi.SPIRegistry;

/**
 * UI for creating/editing dataflow input ports.
 * 
 * @author David Withers
 */
public class MechanismPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JTextField mechanismNameField;
	
	private JComboBox mechanismTypeSelector;
	
	private static SPIRegistry<InvocationMechanismEditor> invocationMechanismEditorRegistry = new SPIRegistry(InvocationMechanismEditor.class);

	public MechanismPanel() {
		super(new GridBagLayout());

		mechanismNameField = new JTextField();


		setBorder(new EmptyBorder(10, 10, 10, 10));
		
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.anchor = GridBagConstraints.WEST;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.ipadx = 10;
		add(new JLabel("Name:"), constraints);

		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.ipadx = 0;
		constraints.weightx = 1d;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		add(mechanismNameField, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.weightx = 0d;
		constraints.fill = GridBagConstraints.NONE;
		constraints.ipadx = 10;
		constraints.insets = new Insets(10, 0, 0, 0);
		add(new JLabel("Type:"), constraints);

		mechanismTypeSelector = new JComboBox();
		for (InvocationMechanismEditor ime : invocationMechanismEditorRegistry.getInstances()) {
			if (!ime.isSingleton()) {
				mechanismTypeSelector.addItem(ime.getName());
			}
		}	
		constraints.gridx = 1;
		constraints.gridwidth = 2;
		constraints.ipadx = 0;
		add(mechanismTypeSelector, constraints);


	}

	/**
	 * Returns the portNameField.
	 *
	 * @return the portNameField
	 */
	public JTextField getMechanismNameField() {
		return mechanismNameField;
	}

	/**
	 * Returns the port name.
	 *
	 * @return the port name
	 */
	public String getMechanismName() {
		return mechanismNameField.getText();
	}

	public String getMechanismTypeName() {
		return (String) mechanismTypeSelector.getSelectedItem();
	}

	public Component getMechanismTypeSelector() {
		return mechanismTypeSelector;
	}

	
}
