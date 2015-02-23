package net.sf.taverna.t2.activities.rest.ui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
public class HTTPHeadersTableModel extends AbstractTableModel {

    protected String[] columnNames;
    protected ArrayList<ArrayList<String>> data;

    public HTTPHeadersTableModel() {
    	columnNames = new String[] {"HTTP Header Name", "HTTP Header Value"};
    	data = new ArrayList<ArrayList<String>>();
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public boolean isCellEditable(int row, int column) {
        return true;
    }

    public int getColumnCount()
    {
        return columnNames.length;
    }

    public int getRowCount()
    {
        return data.size();
    }

    public Object getValueAt(int row, int column)
    {
        return data.get(row).get(column);
    }

    public void setValueAt(Object value, int row, int column) {
        if ((row < 0) || (column < 0)) {
          throw new IllegalArgumentException("Invalid row/column setting");
        }
        data.get(row).set(column, (String)value);
      }
    
    /**
     * Get the class at of the cells at the given column position.
     */
    public Class<? extends Object> getColumnClass(int iCol)
    {
        return getValueAt(0, iCol).getClass();
    }

    
	public void addEmptyRow() {
        data.add(new ArrayList<String>(Arrays.asList("", ""))); // add empty row
        fireTableRowsInserted(
           data.size() - 1,
           data.size() - 1);
    }
	
	public void addRow(ArrayList<String> headerValuePair) {
        data.add(headerValuePair);
        fireTableRowsInserted(
           data.size() - 1,
           data.size() - 1);
    }
	
	public void removeRow(int row)
	{
		if (row >=0 && row < data.size()){
			data.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}

	public ArrayList<ArrayList<String>> getHTTPHeaderData(){
		// Return a deep copy of the 2-dimensional array
		return deepCopy(data);
	}
	
	public void setHTTPHeaderData(ArrayList<ArrayList<String>> data){
		this.data = deepCopy(data);
		fireTableChanged(null);
	}
	
	public ArrayList<String> getHTTPHeaderNames(){
		ArrayList<String> headerNames = new ArrayList<String>();
		for (ArrayList<String> headerNameValuePair : data){
			headerNames.add(headerNameValuePair.get(0));
		}
		return headerNames;
	}
	
	public ArrayList<String> getHTTPHeaderValues(){
		ArrayList<String> headerValues = new ArrayList<String>();
		for (ArrayList<String> headerNameValuePair : data){
			headerValues.add(headerNameValuePair.get(1));
		}
		return headerValues;
	}
	
	public static ArrayList<ArrayList<String>> deepCopy(ArrayList<ArrayList<String>> src)
	{
	    ArrayList<ArrayList<String>> dest = new ArrayList<ArrayList<String>>();

	    for (int i = 0; i< src.size(); i++){ 	
	    	dest.add(new ArrayList<String>(Arrays.asList(new String[src.get(i).size()])));  
	    	Collections.copy(dest.get(i), src.get(i));
	    }
	    return dest;
	}

}

