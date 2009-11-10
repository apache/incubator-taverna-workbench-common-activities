/*******************************************************************************
 * Copyright (C) 2009 The University of Manchester   
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
package net.sf.taverna.t2.activities.spreadsheet.views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import javax.help.CSH;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;

import net.sf.taverna.t2.activities.spreadsheet.Range;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetEmptyCellPolicy;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportConfiguration;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetOutputFormat;
import net.sf.taverna.t2.activities.spreadsheet.SpreadsheetUtils;
import net.sf.taverna.t2.activities.spreadsheet.il8n.SpreadsheetImportUIText;
import net.sf.taverna.t2.lang.ui.DialogTextArea;
import net.sf.taverna.t2.lang.ui.icons.Icons;
import net.sf.taverna.t2.workflowmodel.processor.activity.ActivityConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * Configuration panel for the spreadsheet import activity.
 * 
 * @author David Withers
 */
@SuppressWarnings("serial")
public class SpreadsheetImportConfigView extends JPanel {

	private static final String INCONSISTENT_ROW_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.INCONSISTENT_ROW_MESSAGE");

	private static final String INCONSISTENT_COLUMN_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.INCONSISTENT_COLUMN_MESSAGE");

	private static final String FROM_COLUMN_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.FROM_COLUMN_ERROR_MESSAGE");

	private static final String TO_COLUMN_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.TO_COLUMN_ERROR_MESSAGE");

	private static final String FROM_ROW_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.FROM_ROW_ERROR_MESSAGE");

	private static final String TO_ROW_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.TO_ROW_ERROR_MESSAGE");

	private static final String DEFAULT_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.DEFAULT_MESSAGE");

	private static final String EMPTY_FROM_ROW_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.EMPTY_FROM_ROW_ERROR_MESSAGE");

	private static final String EMPTY_FROM_COLUMN_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.EMPTY_FROM_COLUMN_ERROR_MESSAGE");

	private static final String EMPTY_TO_COLUMN_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.EMPTY_TO_COLUMN_ERROR_MESSAGE");

	private static final String DUPLICATE_PORT_NAME_ERROR_MESSAGE = SpreadsheetImportUIText
			.getString("SpreadsheetImportConfigView.DUPLICATE_PORT_NAME_ERROR_MESSAGE");

	private static Logger logger = Logger.getLogger(SpreadsheetImportConfigView.class);

	private SpreadsheetImportConfiguration oldConfiguration;

	private SpreadsheetImportConfiguration newConfiguration;

	private JPanel titlePanel, contentPanel, buttonPanel, page1, page2;

	private JLabel titleLabel, titleIcon, rowLabel, columnLabel;

	private JLabel emptyCellLabel, outputFormatLabel, outputFormatDelimiterLabel, columnMappingLabel;

	private DialogTextArea titleMessage;

	private JTextField columnFromValue, columnToValue, rowFromValue, rowToValue;

	private JTextField emptyCellUserDefinedValue, outputFormatDelimiter;

	private JCheckBox rowSelectAllOption, rowExcludeFirstOption, rowIgnoreBlankRows;

	private ButtonGroup emptyCellButtonGroup, outputFormatButtonGroup;

	private JRadioButton emptyCellEmptyStringOption, emptyCellUserDefinedOption,
	emptyCellErrorValueOption;

	private JRadioButton outputFormatMultiplePort, outputFormatSinglePort;

	private JTable columnMappingTable;

	private SpreadsheetImportConfigTableModel columnMappingTableModel;

	private JButton actionOkButton, actionCancelButton, nextButton, backButton;

	private CardLayout cardLayout = new CardLayout();

	private Stack<String> warningMessages = new Stack<String>();

	private Stack<String> errorMessages = new Stack<String>();

	private KeyListener enterKeyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (actionOkButton.isSelected() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				actionOkButton.doClick();
			}
		}
	};

	/**
	 * Constructs a configuration view for an SpreadsheetImport Activity.
	 * 
	 * @param activity
	 */
	public SpreadsheetImportConfigView(SpreadsheetImportActivity activity) {
		oldConfiguration = activity.getConfiguration();
		newConfiguration = new SpreadsheetImportConfiguration(oldConfiguration);
		initialise();
		layoutPanel();
	}

	public SpreadsheetImportConfiguration getConfiguration() {
		return newConfiguration;
	}

	public boolean isConfigurationChanged() {
		return !oldConfiguration.equals(newConfiguration);
	}

	/**
	 * Initialises the panel components.
	 */
	private void initialise() {
		CSH.setHelpIDString(this, this.getClass().getCanonicalName());
		
		// title
		titlePanel = new JPanel(new BorderLayout());
		titlePanel.setBackground(Color.WHITE);
		addDivider(titlePanel, SwingConstants.BOTTOM, true);	
		
		titleLabel = new JLabel(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.panelTitle"));
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 13.5f));
		titleIcon = new JLabel("");
		titleMessage = new DialogTextArea(DEFAULT_MESSAGE);
		titleMessage.setMargin(new Insets(5, 10, 10, 10));
		// titleMessage.setMinimumSize(new Dimension(0, 30));
		titleMessage.setFont(titleMessage.getFont().deriveFont(11f));
		titleMessage.setEditable(false);
		titleMessage.setFocusable(false);
		// titleMessage.setFont(titleLabel.getFont().deriveFont(Font.PLAIN,
		// 12f));

		// column range
		columnLabel = new JLabel(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.columnSectionLabel"));

		Range columnRange = newConfiguration.getColumnRange();
		columnFromValue = new JTextField(new UpperCaseDocument(), SpreadsheetUtils.getColumnLabel(columnRange.getStart()), 4);
		columnFromValue.addKeyListener(enterKeyListener);
		columnFromValue.setMinimumSize(columnFromValue.getPreferredSize());
		columnToValue = new JTextField(new UpperCaseDocument(), SpreadsheetUtils.getColumnLabel(columnRange.getEnd()), 4);
		columnToValue.addKeyListener(enterKeyListener);
		columnToValue.setMinimumSize(columnToValue.getPreferredSize());

		columnFromValue.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				checkValue(columnFromValue.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				checkValue(columnFromValue.getText());
			}

			private void checkValue(String text) {
				if (text.trim().equals("")) {
					addErrorMessage(EMPTY_FROM_COLUMN_ERROR_MESSAGE);
				} else if (text.trim().matches("[A-Za-z]+")) {
					String fromColumn = columnFromValue.getText().toUpperCase();
					String toColumn = columnToValue.getText().toUpperCase();
					int fromColumnIndex = SpreadsheetUtils.getColumnIndex(fromColumn);
					int toColumnIndex = SpreadsheetUtils.getColumnIndex(toColumn);
					if (checkColumnRange(fromColumnIndex, toColumnIndex)) {
						columnMappingTableModel.setFromColumn(fromColumnIndex);
						columnMappingTableModel.setToColumn(toColumnIndex);
						newConfiguration.setColumnRange(new Range(fromColumnIndex, toColumnIndex));
						validatePortNames();
					}
					removeErrorMessage(FROM_COLUMN_ERROR_MESSAGE);
					removeErrorMessage(EMPTY_FROM_COLUMN_ERROR_MESSAGE);
				} else {
					addErrorMessage(FROM_COLUMN_ERROR_MESSAGE);
					removeErrorMessage(EMPTY_FROM_COLUMN_ERROR_MESSAGE);
				}
			}

		});

		columnToValue.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				checkValue(columnToValue.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				checkValue(columnToValue.getText());
			}

			private void checkValue(String text) {
				if (text.trim().equals("")) {
					addErrorMessage(EMPTY_TO_COLUMN_ERROR_MESSAGE);
				} else if (text.trim().matches("[A-Za-z]+")) {
					String fromColumn = columnFromValue.getText().toUpperCase();
					String toColumn = columnToValue.getText().toUpperCase();
					int fromColumnIndex = SpreadsheetUtils.getColumnIndex(fromColumn);
					int toColumnIndex = SpreadsheetUtils.getColumnIndex(toColumn);
					if (checkColumnRange(fromColumnIndex, toColumnIndex)) {
						columnMappingTableModel.setFromColumn(fromColumnIndex);
						columnMappingTableModel.setToColumn(toColumnIndex);
						newConfiguration.setColumnRange(new Range(fromColumnIndex, toColumnIndex));
						validatePortNames();
					}
					removeErrorMessage(TO_COLUMN_ERROR_MESSAGE);
					removeErrorMessage(EMPTY_TO_COLUMN_ERROR_MESSAGE);

				} else {
					addErrorMessage(TO_COLUMN_ERROR_MESSAGE);
					removeErrorMessage(EMPTY_TO_COLUMN_ERROR_MESSAGE);
				}
			}
		});

		// row range
		rowLabel = new JLabel(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.rowSectionLabel"));
		addDivider(rowLabel, SwingConstants.TOP, false);

		rowSelectAllOption = new JCheckBox(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.selectAllRowsOption"));
		rowExcludeFirstOption = new JCheckBox(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.excludeHeaderRowOption"));
		rowIgnoreBlankRows = new JCheckBox(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.ignoreBlankRowsOption"));
		rowSelectAllOption.setFocusable(false);
		rowExcludeFirstOption.setFocusable(false);
		
		Range rowRange = newConfiguration.getRowRange();
		rowFromValue = new JTextField(new NumericDocument(), String.valueOf(rowRange.getStart() + 1), 4);
		if (rowRange.getEnd() == -1) {
			rowToValue = new JTextField(new NumericDocument(), "", 4);
		} else {
			rowToValue = new JTextField(new NumericDocument(), String.valueOf(rowRange.getEnd() + 1), 4);
		}
		rowFromValue.addKeyListener(enterKeyListener);
		rowFromValue.setMinimumSize(rowFromValue.getPreferredSize());
		rowToValue.addKeyListener(enterKeyListener);
		rowToValue.setMinimumSize(rowToValue.getPreferredSize());

		if (newConfiguration.isAllRows()) {
			rowSelectAllOption.setSelected(true);
			rowFromValue.setEditable(false);
			rowFromValue.setEnabled(false);
			rowToValue.setEditable(false);
			rowToValue.setEnabled(false);
		} else {
			rowExcludeFirstOption.setEnabled(false);
		}
		rowExcludeFirstOption.setSelected(newConfiguration.isExcludeFirstRow());
		rowIgnoreBlankRows.setSelected(newConfiguration.isIgnoreBlankRows());
		
		rowFromValue.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				checkValue(rowFromValue.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				checkValue(rowFromValue.getText());
			}

			private void checkValue(String text) {
				if (text.trim().equals("")) {
					addErrorMessage(EMPTY_FROM_ROW_ERROR_MESSAGE);
				} else if (text.trim().matches("[1-9][0-9]*")) {
					checkRowRange(rowFromValue.getText(), rowToValue.getText());
					int fromRow = Integer.parseInt(rowFromValue.getText());
					newConfiguration.getRowRange().setStart(fromRow - 1);
					removeErrorMessage(FROM_ROW_ERROR_MESSAGE);
					removeErrorMessage(EMPTY_FROM_ROW_ERROR_MESSAGE);
				} else {
					addErrorMessage(FROM_ROW_ERROR_MESSAGE);
					removeErrorMessage(EMPTY_FROM_ROW_ERROR_MESSAGE);
				}
			}
		});

		rowToValue.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
			}

			public void insertUpdate(DocumentEvent e) {
				checkValue(rowToValue.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				checkValue(rowToValue.getText());
			}

			private void checkValue(String text) {
				if (text.trim().equals("")) {
					newConfiguration.getRowRange().setEnd(-1);
					removeErrorMessage(TO_ROW_ERROR_MESSAGE);
					removeErrorMessage(INCONSISTENT_ROW_MESSAGE);
				} else if (text.trim().matches("[0-9]+")) {
					checkRowRange(rowFromValue.getText(), rowToValue.getText());
					int toRow = Integer.parseInt(rowToValue.getText());
					newConfiguration.getRowRange().setEnd(toRow - 1);
					removeErrorMessage(TO_ROW_ERROR_MESSAGE);
				} else {
					addErrorMessage(TO_ROW_ERROR_MESSAGE);
				}
			}
		});

		rowSelectAllOption.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					newConfiguration.setAllRows(true);
					rowExcludeFirstOption.setEnabled(true);
					if (rowExcludeFirstOption.isSelected()) {
						rowFromValue.setText("2");
					} else {
						rowFromValue.setText("1");
					}
					rowToValue.setText("");
					rowFromValue.setEditable(false);
					rowFromValue.setEnabled(false);
					rowToValue.setEditable(false);
					rowToValue.setEnabled(false);
				} else {
					newConfiguration.setAllRows(false);
					rowExcludeFirstOption.setEnabled(false);
					rowFromValue.setEditable(true);
					rowFromValue.setEnabled(true);
					rowToValue.setEditable(true);
					rowToValue.setEnabled(true);
				}
			}
		});

		rowExcludeFirstOption.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					newConfiguration.setExcludeFirstRow(true);
					rowFromValue.setText("2");
					newConfiguration.getRowRange().setStart(1);
				} else {
					newConfiguration.setExcludeFirstRow(false);
					rowFromValue.setText("1");
					newConfiguration.getRowRange().setStart(0);
				}
			}
		});

		rowIgnoreBlankRows.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				newConfiguration.setIgnoreBlankRows(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		// empty cells
		emptyCellLabel = new JLabel(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.emptyCellSectionLabel"));
		addDivider(emptyCellLabel, SwingConstants.TOP, false);

		emptyCellButtonGroup = new ButtonGroup();
		emptyCellEmptyStringOption = new JRadioButton(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.emptyStringOption"));
		emptyCellUserDefinedOption = new JRadioButton(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.userDefinedOption"));
		emptyCellErrorValueOption = new JRadioButton(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.generateErrorOption"));
		emptyCellEmptyStringOption.setFocusable(false);
		emptyCellUserDefinedOption.setFocusable(false);
		emptyCellErrorValueOption.setFocusable(false);

		emptyCellUserDefinedValue = new JTextField(newConfiguration.getEmptyCellValue());
		emptyCellUserDefinedValue.addKeyListener(enterKeyListener);

		emptyCellButtonGroup.add(emptyCellEmptyStringOption);
		emptyCellButtonGroup.add(emptyCellUserDefinedOption);
		emptyCellButtonGroup.add(emptyCellErrorValueOption);

		if (newConfiguration.getEmptyCellPolicy().equals(SpreadsheetEmptyCellPolicy.GENERATE_ERROR)) {
			emptyCellErrorValueOption.setSelected(true);
			emptyCellUserDefinedValue.setEnabled(false);
			emptyCellUserDefinedValue.setEditable(false);
		} else if (newConfiguration.getEmptyCellPolicy().equals(
				SpreadsheetEmptyCellPolicy.EMPTY_STRING)) {
			emptyCellEmptyStringOption.setSelected(true);
			emptyCellUserDefinedValue.setEnabled(false);
			emptyCellUserDefinedValue.setEditable(false);
		} else {
			emptyCellUserDefinedOption.setSelected(true);
			emptyCellUserDefinedValue.setText(newConfiguration.getEmptyCellValue());
			emptyCellUserDefinedValue.setEnabled(true);
			emptyCellUserDefinedValue.setEditable(true);
		}

		emptyCellEmptyStringOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newConfiguration.setEmptyCellPolicy(SpreadsheetEmptyCellPolicy.EMPTY_STRING);
				emptyCellUserDefinedValue.setEnabled(false);
				emptyCellUserDefinedValue.setEditable(false);
			}
		});
		emptyCellUserDefinedOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newConfiguration.setEmptyCellPolicy(SpreadsheetEmptyCellPolicy.USER_DEFINED);
				emptyCellUserDefinedValue.setEnabled(true);
				emptyCellUserDefinedValue.setEditable(true);
				emptyCellUserDefinedValue.requestFocusInWindow();
			}
		});
		emptyCellErrorValueOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newConfiguration.setEmptyCellPolicy(SpreadsheetEmptyCellPolicy.GENERATE_ERROR);
				emptyCellUserDefinedValue.setEnabled(false);
				emptyCellUserDefinedValue.setEditable(false);
			}
		});

		emptyCellUserDefinedValue.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newConfiguration.setEmptyCellValue(emptyCellUserDefinedValue.getText());
			}

			public void insertUpdate(DocumentEvent e) {
				newConfiguration.setEmptyCellValue(emptyCellUserDefinedValue.getText());
			}

			public void removeUpdate(DocumentEvent e) {
				newConfiguration.setEmptyCellValue(emptyCellUserDefinedValue.getText());
			}
		});

		// column mappings
		columnMappingLabel = new JLabel(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.columnMappingSectionLabel"));
		addDivider(columnMappingLabel, SwingConstants.TOP, false);

		columnMappingTableModel = new SpreadsheetImportConfigTableModel(columnFromValue.getText(),
				columnToValue.getText(), oldConfiguration.getColumnNames());

		columnMappingTable = new JTable();
		columnMappingTable.setRowSelectionAllowed(false);
		columnMappingTable.getTableHeader().setReorderingAllowed(false);
		columnMappingTable.setGridColor(Color.LIGHT_GRAY);
		// columnMappingTable.setFocusable(false);

		columnMappingTable.setColumnModel(new DefaultTableColumnModel() {
			public TableColumn getColumn(int columnIndex) {
				TableColumn column = super.getColumn(columnIndex);
				if (columnIndex == 0) {
					column.setMaxWidth(100);
				}
				return column;
			}
		});

		TableCellEditor defaultEditor = columnMappingTable.getDefaultEditor(String.class);
		if (defaultEditor instanceof DefaultCellEditor) {
			DefaultCellEditor defaultCellEditor = (DefaultCellEditor) defaultEditor;
			defaultCellEditor.setClickCountToStart(1);
			Component editorComponent = defaultCellEditor.getComponent();
			if (editorComponent instanceof JTextComponent) {
				final JTextComponent textField = (JTextComponent) editorComponent;
				textField.getDocument().addDocumentListener(new DocumentListener() {
					public void changedUpdate(DocumentEvent e) {
						updateModel(textField.getText());
					}

					public void insertUpdate(DocumentEvent e) {
						updateModel(textField.getText());
					}

					public void removeUpdate(DocumentEvent e) {
						updateModel(textField.getText());
					}

					private void updateModel(String text) {
						int row = columnMappingTable.getEditingRow();
						int column = columnMappingTable.getEditingColumn();
						columnMappingTableModel.setValueAt(text, row, column);
						newConfiguration.setColumnNames(columnMappingTableModel
								.getColumnToPortMapping());
						validatePortNames();
					}

				});
			}
		}

		columnMappingTable.setModel(columnMappingTableModel);

		// output format
		outputFormatLabel = new JLabel(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.outputFormatSectionLabel"));
		
		outputFormatMultiplePort = new JRadioButton(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.multiplePortOption"));
		outputFormatSinglePort = new JRadioButton(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.singlePortOption"));
		outputFormatMultiplePort.setFocusable(false);
		outputFormatSinglePort.setFocusable(false);

		outputFormatDelimiterLabel = new JLabel(SpreadsheetImportUIText
				.getString("SpreadsheetImportConfigView.userDefinedCsvDelimiter"));
		outputFormatDelimiter = new JTextField(newConfiguration.getCsvDelimiter(), 5);
		
		outputFormatButtonGroup = new ButtonGroup();
		outputFormatButtonGroup.add(outputFormatMultiplePort);
		outputFormatButtonGroup.add(outputFormatSinglePort);
		
		if (newConfiguration.getOutputFormat().equals(SpreadsheetOutputFormat.PORT_PER_COLUMN)) {
			outputFormatMultiplePort.setSelected(true);
			outputFormatDelimiterLabel.setEnabled(false);
			outputFormatDelimiter.setEnabled(false);
		} else {
			outputFormatSinglePort.setSelected(true);
			columnMappingLabel.setEnabled(false);
			enableTable(columnMappingTable, false);
		}

		outputFormatMultiplePort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputFormatDelimiterLabel.setEnabled(false);
				outputFormatDelimiter.setEnabled(false);
				columnMappingLabel.setEnabled(true);
				enableTable(columnMappingTable, true);
				newConfiguration.setOutputFormat(SpreadsheetOutputFormat.PORT_PER_COLUMN);
			}
		});
		outputFormatSinglePort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputFormatDelimiterLabel.setEnabled(true);
				outputFormatDelimiter.setEnabled(true);
				columnMappingLabel.setEnabled(false);
				enableTable(columnMappingTable, false);
				newConfiguration.setOutputFormat(SpreadsheetOutputFormat.SINGLE_PORT);
			}

		});
		outputFormatDelimiter.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				handleUpdate();
			}

			public void insertUpdate(DocumentEvent e) {
				handleUpdate();
			}

			public void removeUpdate(DocumentEvent e) {
				handleUpdate();
			}
			
			private void handleUpdate() {
				String text = null;
				try {
					text = StringEscapeUtils.unescapeJava(outputFormatDelimiter.getText());
				} catch (RuntimeException re) {}
				if (text == null || text.length() == 0) {
					newConfiguration.setCsvDelimiter(",");
				} else {
					newConfiguration.setCsvDelimiter(text.substring(0, 1));
				}
			}

		});
		
		// buttons
		actionOkButton = new JButton();
		actionOkButton.setFocusable(false);
		actionOkButton.setSelected(true);

		actionCancelButton = new JButton();
		actionCancelButton.setFocusable(false);
		
		nextButton = new JButton(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.nextButton"));
		nextButton.setFocusable(false);
		nextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backButton.setVisible(true);
				nextButton.setVisible(false);
				cardLayout.last(contentPanel);
			}
		});

		backButton = new JButton(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.backButton"));
		backButton.setFocusable(false);
		backButton.setVisible(false);
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextButton.setVisible(true);
				backButton.setVisible(false);
				cardLayout.first(contentPanel);
			}
		});
		
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		addDivider(buttonPanel, SwingConstants.TOP, true);	
	}

	private void layoutPanel() {
		setPreferredSize(new Dimension(450, 400));
		setLayout(new BorderLayout());
		
		page1 = new JPanel(new GridBagLayout());
		page2 = new JPanel(new GridBagLayout());

		contentPanel = new JPanel(cardLayout);
		contentPanel.add(page1, "page1");
		contentPanel.add(page2, "page2");
		add(contentPanel, BorderLayout.CENTER);
		
		// title
		titlePanel.setBorder(new CompoundBorder(titlePanel.getBorder(), new EmptyBorder(10, 10, 0, 10)));
		add(titlePanel, BorderLayout.NORTH);
		titlePanel.add(titleLabel, BorderLayout.NORTH);
		titlePanel.add(titleIcon, BorderLayout.WEST);
		titlePanel.add(titleMessage, BorderLayout.CENTER);

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		// column range
		c.insets = new Insets(10, 10, 0, 10);
		page1.add(columnLabel, c);

		c.insets = new Insets(10, 25, 0, 0);
		c.gridwidth = 1;
		c.weightx = 0;
		page1.add(new JLabel(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.from")), c);
		c.insets = new Insets(10, 0, 0, 0);
		c.gridx = 1;
		page1.add(columnFromValue, c);
		c.gridx = 2;
		page1.add(new JLabel(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.to")), c);
		c.gridx = 3;
		page1.add(columnToValue, c);

		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(10, 10, 0, 10);
		c.gridwidth = GridBagConstraints.REMAINDER;

		// row range
		page1.add(rowLabel, c);

		c.insets = new Insets(10, 25, 0, 0);
		c.gridwidth = 1;
		c.gridx = 0;
		c.weightx = 0;
		page1.add(new JLabel(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.from")), c);
		c.insets = new Insets(10, 0, 0, 0);
		c.gridx = 1;
		page1.add(rowFromValue, c);
		c.gridx = 2;
		page1.add(new JLabel(SpreadsheetImportUIText.getString("SpreadsheetImportConfigView.to")), c);
		c.gridx = 3;
		page1.add(rowToValue, c);
		c.gridx = 4;
		page1.add(rowSelectAllOption, c);
		c.gridx = 5;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 0, 10);
		page1.add(rowExcludeFirstOption, c);
		c.insets = new Insets(10, 25, 0, 0);
		c.gridx = 0;
		page1.add(rowIgnoreBlankRows, c);

		c.gridx = 0;
		
		// empty cells
		c.insets = new Insets(10, 10, 10, 10);
		page1.add(emptyCellLabel, c);

		c.insets = new Insets(0, 25, 0, 10);
		page1.add(emptyCellEmptyStringOption, c);
		JPanel userDefinedPanel = new JPanel(new BorderLayout());
		userDefinedPanel.add(emptyCellUserDefinedOption, BorderLayout.WEST);
		userDefinedPanel.add(emptyCellUserDefinedValue, BorderLayout.CENTER);
		page1.add(userDefinedPanel, c);
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		page1.add(emptyCellErrorValueOption, c);

		// output format
		c.insets = new Insets(10, 10, 10, 10);
		c.weighty = 0;
		c.weightx = 1;
		page2.add(outputFormatLabel, c);		

		c.insets = new Insets(0, 25, 0, 10);
		page2.add(outputFormatMultiplePort, c);
		page2.add(outputFormatSinglePort, c);

		c.insets = new Insets(0, 50, 0, 10);
		JPanel outputFormatDelimiterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		outputFormatDelimiterPanel.add(outputFormatDelimiterLabel);
		outputFormatDelimiterPanel.add(outputFormatDelimiter);
		page2.add(outputFormatDelimiterPanel, c);
		
		// column mapping
		c.insets = new Insets(10, 10, 0, 10);
		page2.add(columnMappingLabel, c);

		c.insets = new Insets(10, 10, 10, 10);
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		page2.add(new JScrollPane(columnMappingTable), c);

		buttonPanel.add(actionCancelButton);
		buttonPanel.add(backButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(actionOkButton);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	/**
	 * Displays the message with no icon.
	 * 
	 * @param message
	 *            the message to display
	 */
	public void setMessage(String message) {
		titleIcon.setIcon(null);
		titleMessage.setText(message);
		actionOkButton.setEnabled(true);
	}

	/**
	 * Adds the message to the top of the warning message stack. If the message is already in the
	 * stack it is moved to the top. If there are no error messages the message is displayed.
	 * 
	 * @param message
	 *            the warning message to add
	 */
	public void addWarningMessage(String message) {
		if (warningMessages.contains(message)) {
			warningMessages.remove(message);
		}
		warningMessages.push(message);
		if (errorMessages.isEmpty()) {
			setWarningMessage(message);
		}
	}

	/**
	 * Removes the message from the warning message stack. If there are no error messages the next
	 * warning message is displayed. If there are no warning messages the default message is
	 * displayed.
	 * 
	 * @param message
	 *            the warning message to remove
	 */
	public void removeWarningMessage(String message) {
		warningMessages.remove(message);
		if (errorMessages.isEmpty()) {
			if (warningMessages.isEmpty()) {
				setMessage(DEFAULT_MESSAGE);
			} else {
				setWarningMessage(warningMessages.peek());
			}
		}
	}

	/**
	 * Displays the message and a warning icon.
	 * 
	 * @param message
	 *            the warning message to display
	 */
	public void setWarningMessage(String message) {
		titleIcon.setIcon(Icons.warningIcon);
		titleMessage.setText(message);
		actionOkButton.setEnabled(true);
	}

	/**
	 * Adds the message to the top of the error message stack. If the message is already in the
	 * stack it is moved to the top. The message is then displayed.
	 * 
	 * @param message
	 *            the error message to add
	 */
	public void addErrorMessage(String message) {
		if (errorMessages.contains(message)) {
			errorMessages.remove(message);
		}
		errorMessages.push(message);
		setErrorMessage(message);
	}

	/**
	 * Removes the message from the error message stack and displays the next error message. If
	 * there are no error messages the next warning message is displayed. If there are no warning
	 * messages the default message is displayed.
	 * 
	 * @param message
	 *            the error message to remove
	 */
	public void removeErrorMessage(String message) {
		errorMessages.remove(message);
		if (errorMessages.isEmpty()) {
			if (warningMessages.isEmpty()) {
				setMessage(DEFAULT_MESSAGE);
			} else {
				setWarningMessage(warningMessages.peek());
			}
		} else {
			setErrorMessage(errorMessages.peek());
		}
	}

	/**
	 * Displays the message and an error icon.
	 * 
	 * @param message
	 *            the error message to display
	 */
	public void setErrorMessage(String message) {
		titleIcon.setIcon(Icons.severeIcon);
		titleMessage.setText(message);
		actionOkButton.setEnabled(false);
	}

	protected boolean validatePortNames() {
		boolean isValid = true;
		Range columnRange = newConfiguration.getColumnRange();
		Map<String, String> mapping = newConfiguration.getColumnNames();
		Set<String> usedNames = new HashSet<String>();
		for (Entry<String, String> entry : mapping.entrySet()) {
			if (columnRange.contains(SpreadsheetUtils.getColumnIndex(entry.getKey()))) {
				String portName = entry.getValue();
				if (!usedNames.add(portName)) {
					isValid = false;
					break;
				}
				if (portName.matches("[A-Z]+")) {
					if (!mapping.containsKey(portName)) {
						int columnIndex = SpreadsheetUtils.getColumnIndex(portName);
						if (columnRange.contains(columnIndex)) {
							isValid = false;
							break;
						}
					}
				}
			}
		}
		if (isValid) {
			removeErrorMessage(DUPLICATE_PORT_NAME_ERROR_MESSAGE);
		} else {
			addErrorMessage(DUPLICATE_PORT_NAME_ERROR_MESSAGE);
		}
		return isValid;
	}

	protected boolean checkRowRange(String from, String to) {
		boolean result = false;
		try {
			int fromRow = Integer.parseInt(from);
			int toRow = Integer.parseInt(to);
			if (toRow < fromRow) {
				addErrorMessage(INCONSISTENT_ROW_MESSAGE);
			} else {
				removeErrorMessage(INCONSISTENT_ROW_MESSAGE);
				result = true;
			}
		} catch (NumberFormatException e) {
			logger.warn("Problem checking row range", e);
		}
		return result;
	}

	protected boolean checkColumnRange(int fromColumn, int toColumn) {
		boolean result = false;
		if (toColumn < fromColumn) {
			addErrorMessage(INCONSISTENT_COLUMN_MESSAGE);
		} else {
			removeErrorMessage(INCONSISTENT_COLUMN_MESSAGE);
			result = true;
		}
		return result;
	}

	/**
	 * Adds a light gray or etched border to the top or bottom of a JComponent.
	 * 
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

	public void setOkAction(Action okAction) {
		actionOkButton.setAction(okAction);
	}

	public void setCancelAction(Action cancelAction) {
		actionCancelButton.setAction(cancelAction);
	}

	private void enableTable(JTable table, boolean enabled) {
		table.setEnabled(enabled);
		Component editor = table.getEditorComponent();
		if (editor != null) {
			editor.setEnabled(enabled);
		}
		if (enabled) {
			table.setForeground(Color.BLACK);
			table.getTableHeader().setForeground(Color.BLACK);		
		} else {
			table.setForeground(Color.LIGHT_GRAY);
			table.getTableHeader().setForeground(Color.LIGHT_GRAY);		
		}
	}
	 
	static class UpperCaseDocument extends PlainDocument {
        @Override
        public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        	if (text.matches("[A-Za-z]+")) {
        		text = text.toUpperCase();
        		super.replace(offset, length, text, attrs);
        	}
        }
     }

	static class NumericDocument extends PlainDocument {
        @Override
        public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        	if (text.length() == 0 || text.matches("[0-9]+")) {
        		text = text.toUpperCase();
        		super.replace(offset, length, text, attrs);
        	}
        }
     }
	 
	/**
	 * Main method for testing the panel.
	 * 
	 * @param args
	 * @throws ActivityConfigurationException
	 */
	public static void main(String[] args) throws ActivityConfigurationException {
		final JFrame frame = new JFrame();
		SpreadsheetImportActivity activity = new SpreadsheetImportActivity();
		activity.configure(new SpreadsheetImportConfiguration());
		final SpreadsheetImportConfigView config = new SpreadsheetImportConfigView(activity);
		config.setOkAction(new AbstractAction("Finish") {
			public void actionPerformed(ActionEvent arg0) {
				Range columnRange = config.getConfiguration().getColumnRange();
				String fromColumn = SpreadsheetUtils.getColumnLabel(columnRange.getStart());
				String toColumn = SpreadsheetUtils.getColumnLabel(columnRange.getEnd());
				System.out.printf("%s (%s) - %s (%s)", fromColumn, columnRange.getStart(),
						toColumn, columnRange.getEnd());
				frame.setVisible(false);
				frame.dispose();
			}
		});
		config.setCancelAction(new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent arg0) {
				frame.setVisible(false);
				frame.dispose();
			}
		});
		frame.add(config);
		frame.pack();
		frame.setVisible(true);
	}

}
