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
package net.sf.taverna.t2.activities.beanshell.views;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.sf.taverna.t2.activities.beanshell.BeanshellActivity;
import net.sf.taverna.t2.workflowmodel.processor.activity.config.ActivityInputPortDefinitionBean;

/**
 * A view representing {@link ActivityInputPortDefinitionBean}s of a
 * {@link BeanshellActivity} and the various parts which can be edited,
 * primarily the name and the depth.
 * 
 * @author Ian Dunlop
 * 
 */
@SuppressWarnings("serial")
public class BeanshellInputViewer extends JPanel {

	private ActivityInputPortDefinitionBean bean;

	private JTextField nameField;

	private JSpinner depthSpinner;

	private JTextArea refSchemeText;

	private JTextArea mimeTypeText;

	private JLabel translatedType;

	private JComboBox literalSelector;

	private boolean editable;

	/**
	 * Calls {@link #initView()} to set the look and feel and sets the
	 * components to be editable or not
	 * 
	 * @param bean
	 *            the {@link ActivityInputPortDefinitionBean} which represents
	 *            the view
	 * @param editable
	 *            whether the components should be enable for editing or not
	 */
	public BeanshellInputViewer(ActivityInputPortDefinitionBean bean,
			boolean editable) {
		this.bean = bean;
		this.editable = editable;
		setBorder(javax.swing.BorderFactory.createEtchedBorder());
		initView();
		setEditMode();
	}

	/**
	 * Uses {@link GridBagLayout} to layout the overall component. Adds the
	 * individual editable elements to the view to allow parts of the
	 * {@link ActivityInputPortDefinitionBean} to be changeD
	 */
	private void initView() {
		setLayout(new GridBagLayout());
		GridBagConstraints outerConstraint = new GridBagConstraints();
		outerConstraint.anchor = GridBagConstraints.FIRST_LINE_START;
		outerConstraint.gridx = 0;
		outerConstraint.gridy = 0;
		outerConstraint.weighty = 0;
		outerConstraint.weightx = 0.1;
		outerConstraint.fill = GridBagConstraints.BOTH;

		nameField = new JTextField(bean.getName());
		add(nameField, outerConstraint);

		Vector<Boolean> literalSelectorList = new Vector<Boolean>();
		literalSelectorList.add(true);
		literalSelectorList.add(false);
		literalSelector = new JComboBox(literalSelectorList);
		if (!bean.getAllowsLiteralValues()) {
			literalSelector.setSelectedIndex(1);
		}
		outerConstraint.gridx = 1;
		add(literalSelector, outerConstraint);

		outerConstraint.gridx = 2;
		SpinnerNumberModel model = new SpinnerNumberModel(new Integer(bean
				.getDepth()), new Integer(0), new Integer(100), new Integer(1));
		depthSpinner = new JSpinner(model);
		depthSpinner.setEnabled(false);
		depthSpinner.setToolTipText("A depth of 0 means a simple value, like a string. Depth 1 is a list of simple values, while depth 2 is a list of a list of simple values");
		// depthSpinner.setValue(bean.getDepth());

		add(depthSpinner, outerConstraint);

		outerConstraint.gridx = 3;
		refSchemeText = new JTextArea();
		String refs = "";
		for (Object refScheme : bean.getHandledReferenceSchemes()) {
			refs = refs + refScheme.getClass().getSimpleName() + "\n";
		}
		refSchemeText.setText(refs);
		refSchemeText.setEditable(false);
		refSchemeText.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		// add(refSchemeText, outerConstraint);

		outerConstraint.gridx = 4;
		// mimeTypeText = new JTextArea();
		// String mimes = "";
		// for (String mimeType : bean.getMimeTypes()) {
		// mimes = mimes + mimeType + "\n";
		// }
		// mimeTypeText.setText(mimes);
		// mimeTypeText.setEditable(false);
		// mimeTypeText.setBorder(javax.swing.BorderFactory.createEtchedBorder());
		// add(mimeTypeText, outerConstraint);

		outerConstraint.gridx = 5;
		translatedType = new JLabel(bean.getTranslatedElementType()
				.getSimpleName());
		// add(translatedType, outerConstraint);
	}

	/**
	 * Get the component which allows the
	 * {@link ActivityInputPortDefinitionBean} name to be edited
	 * 
	 * @return
	 */
	public JTextField getNameField() {
		return nameField;
	}

	/**
	 * Get the component which allows {@link ReferenceScheme}s to be added to a
	 * {@link ActivityInputPortDefinitionBean}
	 * 
	 * @return
	 */
	public JTextArea getRefSchemeText() {
		return refSchemeText;
	}

	/**
	 * Get the component which allows Mime Types to be added to a
	 * {@link ActivityInputPortDefinitionBean}
	 * 
	 * @return
	 */
	public JTextArea getMimeTypeText() {
		return mimeTypeText;
	}

	/**
	 * Get the component which allows Translated Types to be added to a
	 * {@link ActivityInputPortDefinitionBean}
	 * 
	 * @return
	 */
	public JLabel getTranslatedType() {
		return translatedType;
	}

	/**
	 * Set the
	 * {@link ActivityInputPortDefinitionBean#setAllowsLiteralValues(boolean)}
	 * to be true or false using this component
	 * 
	 * @return
	 */
	public JComboBox getLiteralSelector() {
		return literalSelector;
	}

	/**
	 * Change the depth of the {@link ActivityInputPortDefinitionBean}
	 * 
	 * @return
	 */
	public JSpinner getDepthSpinner() {
		return depthSpinner;
	}

	/**
	 * Get the actual {@link ActivityInputPortDefinitionBean} which is
	 * represented by this view
	 * 
	 * @return
	 */
	public ActivityInputPortDefinitionBean getBean() {
		return bean;
	}

	/**
	 * Can the components on this view be edited?
	 * 
	 * @return
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Set all the components to be editable or not
	 * 
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		setEditMode();
	}

	/**
	 * Sets the {@link #nameField}, {@link #literalSelector} and
	 * {@link #depthSpinner} to allow editing
	 */
	public void setEditMode() {
		this.nameField.setEditable(editable);
		this.literalSelector.setEnabled(editable);
		this.depthSpinner.setEnabled(editable);

	}

}
