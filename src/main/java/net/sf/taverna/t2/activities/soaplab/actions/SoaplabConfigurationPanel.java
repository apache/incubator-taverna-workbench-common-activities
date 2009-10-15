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

import net.sf.taverna.t2.activities.soaplab.SoaplabActivity;
import net.sf.taverna.t2.activities.soaplab.SoaplabActivityConfigurationBean;
import net.sf.taverna.t2.workbench.ui.views.contextualviews.activity.ActivityConfigurationPanel;

public class SoaplabConfigurationPanel extends ActivityConfigurationPanel<SoaplabActivity, SoaplabActivityConfigurationBean> {
	
	private static final long serialVersionUID = 9133273338631693912L;
	
	SoaplabActivityConfigurationBean configuration;
	ActionListener closeClicked;
	ActionListener applyClicked;

	private JTextField intervalMaxField;
	private JTextField intervalField;
	private JTextField backoffField;
	private JCheckBox allowPolling;

	private SoaplabActivity activity;
	
	public SoaplabConfigurationPanel(SoaplabActivity activity) {
		this.activity = activity;
		initialise();
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
		this.configuration=activity.getConfiguration();
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
		
		intervalField = new JTextField(String.valueOf(configuration.getPollingInterval()));
		intervalMaxField = new JTextField(String.valueOf(configuration.getPollingIntervalMax()));
		backoffField = new JTextField(Double.toString(configuration.getPollingBackoff()));
		
		interval.add(intervalField,BorderLayout.CENTER);
		intervalMax.add(intervalMaxField);
		backoff.add(backoffField);
		
		allowPolling=new JCheckBox("Polling?",configuration.getPollingInterval()!=0);
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
		validate();
	}

	private void updateEnableForPollingFlag() {
		boolean enabled=allowPolling.isSelected();
		intervalField.setEnabled(enabled);
		intervalMaxField.setEnabled(enabled);
		backoffField.setEnabled(enabled);
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


	@Override
	public SoaplabActivityConfigurationBean getConfiguration() {
		return configuration;
	}


	@Override
	public boolean isConfigurationChanged() {
		boolean originalPolling = (configuration.getPollingInterval() != 0);
		return ((configuration.getPollingInterval() != getInterval()) ||
				(configuration.getPollingIntervalMax() != getIntervalMax()) ||
				(configuration.getPollingBackoff() != getBackoff()) ||
				((originalPolling != allowPolling.isSelected()) && (getInterval() != 0)));
	}


	@Override
	public void noteConfiguration() {
		SoaplabActivityConfigurationBean newConfiguration =
			(SoaplabActivityConfigurationBean) cloneBean (configuration);
		if (validateValues()) {
			int interval = 0;
			int intervalMax = 0;
			double backoff = 1.1;

			if (isAllowPolling()) {
				interval = getInterval();
				intervalMax = getIntervalMax();
				backoff = getBackoff();
			}

			newConfiguration.setPollingBackoff(backoff);
			newConfiguration.setPollingInterval(interval);
			newConfiguration.setPollingIntervalMax(intervalMax);
			configuration = newConfiguration;
		}
	}


	@Override
	public void refreshConfiguration() {
		removeAll();
		initialise();
	}


	@Override
	public boolean checkValues() {
		// TODO Not yet implemented
		return true;
	}
	
}
