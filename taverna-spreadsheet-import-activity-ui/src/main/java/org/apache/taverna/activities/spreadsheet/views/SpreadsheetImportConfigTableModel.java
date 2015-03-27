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

package org.apache.taverna.activities.spreadsheet.views;

import java.util.HashMap;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import org.apache.taverna.activities.spreadsheet.SpreadsheetUtils;
import org.apache.taverna.activities.spreadsheet.il8n.SpreadsheetImportUIText;

/**
 * TableModel for mapping column labels to port names.
 * <p>
 * The default mapping is for the port name to be the same as the column label. The
 * columnToPortMapping only contains entries for port names that are not the same as the column
 * label.
 *
 * @author David Withers
 */
@SuppressWarnings("serial")
public class SpreadsheetImportConfigTableModel extends AbstractTableModel {

	private Map<String, String> columnToPortMapping;

	private int fromColumn, toColumn;

	/**
	 * Constructs a TableModel that maps column labels to port names for the specified range of
	 * columns.
	 *
	 * @param fromColumn
	 *            the start of the column range
	 * @param toColumn
	 *            the end of the column range
	 * @param columnToPortMapping
	 *            existing column to port mappings (can be null)
	 */
	public SpreadsheetImportConfigTableModel(String fromColumn, String toColumn,
			Map<String, String> columnToPortMapping) {
		if (columnToPortMapping == null) {
			this.columnToPortMapping = new HashMap<String, String>();
		} else {
			this.columnToPortMapping = new HashMap<String, String>(columnToPortMapping);
		}
		this.fromColumn = SpreadsheetUtils.getColumnIndex(fromColumn);
		this.toColumn = SpreadsheetUtils.getColumnIndex(toColumn);
	}

	/**
	 * Sets the start of the column range.
	 *
	 * @param fromColumn
	 *            the start of the column range
	 */
	public void setFromColumn(int fromColumn) {
		if (this.fromColumn != fromColumn) {
			this.fromColumn = fromColumn;
			fireTableStructureChanged();
		}
	}

	/**
	 * Sets the end of the column range.
	 *
	 * @param toColumn
	 *            the end of the column range
	 */
	public void setToColumn(int toColumn) {
		if (this.toColumn != toColumn) {
			this.toColumn = toColumn;
			fireTableStructureChanged();
		}
	}

	/**
	 * Returns the port name for the given column label.
	 * <p>
	 * If the columnLabel is the columnToPortMapping the value is returned; otherwise the
	 * columnLabel is returned.
	 *
	 * @param columnLabel
	 *            the column to find the port name for
	 * @return the port name for the given column label
	 */
	public String getPortName(String columnLabel) {
		String portName;
		if (columnToPortMapping.containsKey(columnLabel)) {
			portName = columnToPortMapping.get(columnLabel);
		} else {
			portName = columnLabel;
		}
		return portName;
	}

	/**
	 * Sets the port name for the column label.
	 * <p>
	 * If the port name is not the same as the column label the port name is added the
	 * columnToPortMapping.
	 *
	 * @param columnLabel
	 * @param portName
	 */
	public void setPortName(String columnLabel, String portName) {
		if (columnLabel.equals(portName)) {
			columnToPortMapping.remove(columnLabel);
		} else {
			columnToPortMapping.put(columnLabel, portName);
		}
	}

	/**
	 * Returns the map of column labels to port names.
	 * <p>
	 * The map only contains entries for port names that are not the same as their corresponding
	 * column label.
	 *
	 * @return the map of column labels to port names
	 */
	public Map<String, String> getColumnToPortMapping() {
		return columnToPortMapping;
	}

	// TableModel methods

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return SpreadsheetImportUIText.getString("SpreadsheetImportConfigTableModel.column");
		} else {
			return SpreadsheetImportUIText.getString("SpreadsheetImportConfigTableModel.portName");
		}
	}

	public int getRowCount() {
		return toColumn - fromColumn + 1;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		String columnLabel = SpreadsheetUtils.getColumnLabel(rowIndex + fromColumn);
		if (columnIndex == 0) {
			return columnLabel;
		} else {
			return getPortName(columnLabel);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			setPortName(SpreadsheetUtils.getColumnLabel(rowIndex + fromColumn), value.toString());
		}
	}

}
