package sg.edu.nus.gui.server;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Vector;
import java.sql.ResultSetMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javax.swing.table.AbstractTableModel;

import sg.edu.nus.peer.ServerPeer;

/**
 * This DatabaseQueryTableModel is a basic implementation of TableModel interface 
 * that fills out a Vector from a query's result set
 * 
 * @author Han Xixian
 * @version August-6-2008
 *
 */

public class DatabaseQueryTableModel extends AbstractTableModel {

	/**
	 * cache is used to store the tuple information obtained by specified query.
	 */
	Vector<String[]> cache = null;
	
	/**
	 * headers is used to specify the column information of table
	 */
	private String[] headers = null;
	
	/**
	 * These parameters are used for testing.
	 */
	private Connection conn = null;
	private String databaseName = "localdb";
	
	public DatabaseQueryTableModel()
	{
		cache = new Vector<String[]>();
		headers = new String[1];
		//cache = new Vector<Vector<String>>();	
		//headers = new Vector<String>();		
	}
	
	/**
	 * @return the databaseName
	 */
	public String getDatabaseName() {
		return databaseName;
	}

	/**
	 * @param databaseName the databaseName to set
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}


	/**
	 * Initialize Database to obtain the connection to specified database
	 */
	public void initDB()
	{
		if(this.databaseName == null)
			return;
		
		if(conn != null)
			return;
		
		try
		{
			Class.forName(ServerPeer.SERVER_DB_DRIVER);
			conn = DriverManager.getConnection(ServerPeer.SERVER_DB_URL + databaseName, 
					ServerPeer.SERVER_DB_USER, ServerPeer.SERVER_DB_PASS);
		}
		catch(Exception e)
		{
			System.out.println("Can't initialize Database");
			e.printStackTrace();
		}
	}
	
	/**
	 * close the connection to specified database
	 */
	public void closeDB()
	{
		try
		{
			if(conn != null)
				conn.close();
		}
		catch(Exception e)
		{
			System.out.println("Can't close Database");
			e.printStackTrace();
		}
	}
	
	/**
	 * setQuery is used to execute the specified query, and return the results
	 * @param sql
	 */
	public void setQuery(String sql)
	{
		if(this.databaseName == null)
			return;
		
		cache.clear();
		headers = null;
			
		
		try
		{
			Statement stmt = conn.createStatement();
			
			stmt.setFetchSize(1000);
			
			ResultSet rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			headers = new String[rsmd.getColumnCount()];
			
			for(int i = 1; i <= headers.length; i++)
				headers[i-1] = rsmd.getColumnName(i);
			
			//headers.add("DataSourceIP");
			int rowid = 0;
			while(rs.next())
			{
				//Vector<String> tuple = new Vector<String>();
				String[] tuple = new String[rsmd.getColumnCount()];
				cache.add(tuple);
				for(int i = 1; i <= rsmd.getColumnCount(); i++)
				{
					//tuple.add(rs.getString(i));
					this.setValueAt(rs.getString(i), rowid, i-1);
				}
				rowid++;
				//tuple.add("127.0.0.1");
				
				//cache.add(tuple);
			}
			
			//this.fireTableChanged(null);
			this.fireTableStructureChanged();
			this.fireTableDataChanged();
			
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
	
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return headers.length;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return cache.size();
	}

	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return cache.get(row)[col];
	}

	public String getColumnName(int i)
	{
		return headers[i];
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{	
			this.cache.get(rowIndex)[columnIndex] = (String)aValue;
	}
}
