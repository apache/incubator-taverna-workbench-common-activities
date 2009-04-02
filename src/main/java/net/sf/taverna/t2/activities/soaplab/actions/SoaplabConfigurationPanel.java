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
package net.sf.taverna.t2.activities.soaplab.actions;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;

public class SoaplabConfigurationPanel extends JPanel {
	
	private static final long serialVersionUID = 9133273338631693912L;
	
	SoaplabActivityConfigurationBean bean;
	ActionListener cancelClicked;
	ActionListener okClicked;

	private JTextField intervalMaxField;
	private JTextField intervalField;
	private JTextField backoffField;
	private JCheckBox allowPolling;
	
	public SoaplabConfigurationPanel(SoaplabActivityConfigurationBean bean) {
		this.bean=bean;
		initialise();
	}
	
	public void setCancelClickedListener(ActionListener listener) {
		cancelClicked=listener;
	}
	
	public void setOKClickedListener(ActionListener listener) {
		okClicked=listener;
	}
	
	public boolean isAllowPolling() {
		return allowPolling.isSelected();
	}
	
	public int getInterval() {
		return Integer.parseInt(intervalField.getText());
	}
	
	public int getIntervalMax() {
		return Integer.parseInt(intervalMaxField.getText());
	}
	
	public double getBackoff() {
		return Double.parseDouble(backoffField.getText());
	}
	
	private void initialise() {
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		JPanel interval = new JPanel();
		interval.setLayout(new BorderLayout());
		interval.setBorder(new TitledBorder("Interval"));
		
		JPanel intervalMax = new JPanel();
		intervalMax.setLayout(new BorderLayout());
		intervalMax.setBorder(new TitledBorder("Max interval"));
		
		JPanel backoff = new JPanel();
		backoff.setLayout(new BorderLayout());
		backoff.setBorder(new TitledBorder("Backoff"));
		
		intervalField = new JTextField(String.valueOf(bean.getPollingInterval()));
		intervalMaxField = new JTextField(String.valueOf(bean.getPollingIntervalMax()));
		backoffField = new JTextField(Double.toString(bean.getPollingBackoff()));
		
		interval.add(intervalField,BorderLayout.CENTER);
		intervalMax.add(intervalMaxField);
		backoff.add(backoffField);
		
		allowPolling=new JCheckBox("Polling?",bean.getPollingInterval()!=0);
		allowPolling.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateEnableForPollingFlag();
			}
		});
		
		updateEnableForPollingFlag();
		JPanel allowPollingPanel = new JPanel();
		allowPollingPanel.setLayout(new BorderLayout());
		allowPollingPanel.add(allowPolling,BorderLayout.WEST);
		add(allowPollingPanel);
		add(interval);
		add(intervalMax);
		add(backoff);
		add(Box.createGlue());
		add(buttonPanel());

	}

	private void updateEnableForPollingFlag() {
		boolean enabled=allowPolling.isSelected();
		intervalField.setEnabled(enabled);
		intervalMaxField.setEnabled(enabled);
		backoffField.setEnabled(enabled);
	}
	
	@SuppressWarnings("serial")
	private JPanel buttonPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton cancelButton = new JButton("Cancel");
		
		cancelButton.setAction(new AbstractAction() {

			public void actionPerformed(ActionEvent event) {
				cancelClicked.actionPerformed(event);
			}
			
		});
		
		JButton okButton = new JButton("OK");
		okButton.setAction(new AbstractAction() {

			public void actionPerformed(ActionEvent e) {
				okClicked.actionPerformed(e);	
			}
			
		});
		
		cancelButton.setText("Cancel");
		okButton.setText("OK");
		panel.add(cancelButton);
		panel.add(okButton);
		
		return panel;
	}

	public boolean validateValues() {
		if (allowPolling.isSelected()) {
			try {
				new Integer(intervalField.getText());	
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"The interval field must be a valid integer","Invalid value",
						JOptionPane.ERROR_MESSAGE);
				return false;
				
			}
			
			try {
				new Integer(intervalMaxField.getText());	
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"The maximum interval field must be a valid integer","Invalid value",
						JOptionPane.ERROR_MESSAGE);
				return false;
				
			}
			
			try {
				new Double(backoffField.getText());	
			}
			catch (Exception e) {
				JOptionPane.showMessageDialog(null,
						"The backoff field must be a valid float","Invalid value",
						JOptionPane.ERROR_MESSAGE);
				return false;
				
			}
		}
		
		return true;
	}
	
}
