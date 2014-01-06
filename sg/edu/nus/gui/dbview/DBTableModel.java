/*
 * @(#) DBTableModel.java 1.0 2006-12-26
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;

/**
 * This class simply wraps the <code>AbstractTableModel</code> 
 * for the purpose of show data in table or schema info of table.
 * <p>
 * If necessary, some derived funtions of the <code>AbstractTableModel</code>
 * should be customized for our own purpose.
 * 
 * @author Huang Yukai
 * @version 1.0 2006-12-26 
 * @see javax.swing.table.AbstractTableModel
 */

public class DBTableModel extends AbstractTableModel
{
	//private members
	private static final long serialVersionUID = -1488927675177390046L;
	
	// true  - show table;  false - show schema
	private boolean isShowTable = true;	
	// All the data from the result set will be cached in the arraylist first.
	private ArrayList cache;
	private ResultSetMetaData rsmd;
	
	//Only show the column name and data type in the schema of table currently
	private String[] schemaColumns = {"Field Name", "Data Type"};
	
	//private SharedDataIndexer sharedDataIdx;
	private String tname;    //table name
	private String colname;  //first column name
	public Boolean []shared; //boolean value indicates whethe the data are shared
	private JDialog clientgui;
	/**
	 * Constructor
	 * 
	 * @param rs - the result set as the input of table data/schema 
	 * @param isShowTable - <code>true</code> if show data
	 * 						<code>false</code> if show shema
	 * @throws SQLException - if a database access error occurs
	 */
	DBTableModel(JDialog gui, ResultSet rs, boolean isShowTable ) throws SQLException
	{
		this.clientgui = gui;
		this.isShowTable = isShowTable;
		this.rsmd = rs.getMetaData();
		tname = this.rsmd.getTableName(1);
		colname = this.rsmd.getColumnName(1);
		//this.sharedDataIdx = indexer;
		cache = new ArrayList();
		int cols = getColumnCount();
		
		// If show the data in table, retreival datas from resultset into cache.
		if(isShowTable)
		{
			while(rs.next())
			{
				Object[] row = new Object[cols-1];
				for(int j=0; j<row.length;j++)
				{
					row[j] = rs.getObject(j+1);
				}
				cache.add(row);
			}
			shared = new Boolean[cache.size()];
			for(int i=0; i<cache.size(); i++){
				Object[] row = (Object[])cache.get(i);
			    String keyvalue = String.valueOf(row[0]);
				String name = tname + "@" + keyvalue;
//				if(this.sharedDataIdx.isShared(name))
//					this.shared[i] = Boolean.TRUE;
//				else this.shared[i] = Boolean.FALSE;
				shared[i] = Boolean.FALSE;
			}
		}
		// If show the schema of table, retreival the column information from the metadata of result set.
		else
		{
			int rows = rsmd.getColumnCount();
			for(int i=0; i<rows; i++)
			{
				Object[] row = new Object[schemaColumns.length];
				
				row[0] = rsmd.getColumnName(i+1);
				row[1] = rsmd.getColumnTypeName(i+1);
				
				cache.add(row);
			}
		}
	}
	
	/**
	 * return the number of rows in the model. The DBTree uses this method to
	 * determine how many rows it should display.
	 * 
	 * @return the number of rows in the model
	 */
	public int getRowCount()
	{
		return cache.size();
	}
	
	/**
	 * Return the number of columns in the model. The DBTree uses this method to 
	 * determine how many columns it should create and display.
	 * 
	 * @return the number of columns in the model 
	 */
	public int getColumnCount()
	{
		try
		{
			// If show the data in the table, return the number of columns in the result set
			if(isShowTable)
			{
				return rsmd.getColumnCount()+1;
			}
			// If show the schema of the table, return the number of information user 
			// care about in the metadata of the result set.
			else
			{
				return schemaColumns.length;
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return 0;
		}
		
	}
	
	/**
	 * Return the name of column at columnIndex. This is used to initialize
	 * the table's column header name.
	 * 
	 * @param columnIndex - the index of the column
	 * @return the name of column
	 */
	public String getColumnName(int columnIndex)
	{
		try
		{
			// If show the data in the table, return the columnName in the result set.
			if(isShowTable)
			{
				if(columnIndex == 0)
					return "shared?";
				else{
					return this.rsmd.getColumnName(columnIndex);
				}
			}
			// If show the schema of the table, return the columnName of the schema, like: "Field Name", "Data Type"
			else
			{
				return this.schemaColumns[columnIndex];
			}
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			return "#error#";
		}
	}
	
	/**
	 * Return the value for the cell at columnIndex and rowIndex
	 * 
	 * @param rowIndex - the row whose value is to be queried.
	 * @param columnIndex - the column whose value is to be queried.
	 * 
	 * @return the value Object at the specified cell
	 */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex < cache.size())
		{
			if(columnIndex==0)
			{
				/*
				Object[] row = (Object[])cache.get(rowIndex);
			    String value = String.valueOf(row[0]);
			    String name = this.tname + ":" + value + this.colname;
			    if(sharedDataIdx.isShared(name))
			    	return Boolean.TRUE;
			    else return Boolean.FALSE;
			    */
				return shared[rowIndex];
			}
			
			return ((Object[])cache.get(rowIndex))[columnIndex-1];
		}
		else 
			return null;
	}
	
	/**
	 * only the first column can be modified
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex == 0)
			return true;
		else return false;
    }
	
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if(columnIndex != 0)
			return;		

		Boolean TRUE = new Boolean(true);
		if(TRUE.equals(value)){
			Object[] row = (Object[])cache.get(rowIndex);
		    String keyvalue = String.valueOf(row[0]);
		    //System.out.println(rowIndex+" "+columnIndex+" "+keyvalue+" "+this.tname+" "+this.colname);
		    //this.sharedDataIdx.insertSharedData(this.tname+"@"+keyvalue, this.colname);
		    shared[rowIndex] = Boolean.TRUE;
		    
		    
		    /* index the tuple in the network   */
		    
		    //String serializedData = this.sharedDataIdx.getPrefix()+"@"+this.tname+"@"+keyvalue+" "+this.colname;
		    //Tuple tuple = new Tuple(this.rsmd);
		    //tuple.parseSerializedData(serializedData);
		    // tuple.setFields(row);
		    //IndexBody body = new DBIndexInsertBody(tuple, !ClientPeer.indexExists());
		    //IndexBody body = new DBIndexInsertBody(tuple, false);
			//IndexEvent indexEvent = new IndexEvent(IndexEventType.DBINSERT, body);
//			this.clientgui.peer().performIndexOperation(indexEvent);
			
		}
		else{
			Object[] row = (Object[])cache.get(rowIndex);
		    String keyvalue = String.valueOf(row[0]);
		    //this.sharedDataIdx.deleteSharedData(this.tname+"@"+keyvalue);
		    shared[rowIndex] = Boolean.FALSE;
		    
		    /* delete the index of corresponding tuple */
		    
		    //String serializedData = this.sharedDataIdx.getPrefix()+"@"+this.tname+"@"+keyvalue+" "+this.colname;
		    //Tuple tuple = new Tuple(serializedData);
		    //IndexBody body = new DBIndexDeleteBody(tuple);
			//IndexEvent indexEvent = new IndexEvent(IndexEventType.DBDELETE, body);
//			this.clientgui.peer().performIndexOperation(indexEvent);
		}
		super.fireTableCellUpdated(rowIndex, columnIndex);
	}
	
	 public Class getColumnClass(int columnIndex) {
		 if(columnIndex == 0)
			 return Boolean.class;
		 else return Object.class;
     }

}

