package sg.edu.nus.dbconnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Create connection and execute query over underlying DBMS.
 * 
 */

public class DBServer {
	
	private Connection conn;
	//private String server;
	private String driver;
	private String url;
	private String userName;
	private String password;
	private String port;
	private String dbName;

	/**
	 * Constructor without parameters
	 */
	public DBServer()
	{
		DBProperty property = new DBProperty();
		createConnection(property.bestpeerdb_driver, property.bestpeerdb_url, property.bestpeerdb_username, property.bestpeerdb_password, property.bestpeerdb_port, property.bestpeerdb_dbName);
	}
	
	public DBServer(String dbName)
	{
		DBProperty property = new DBProperty();
		this.dbName = dbName;
		//property.bestpeerdb_dbName = dbName;
		createConnection(property.bestpeerdb_driver, property.bestpeerdb_url, property.bestpeerdb_username, property.bestpeerdb_password, property.bestpeerdb_port, dbName);
	}
	
	/**
	 * Constructor with parameters
	 * @param server
	 * @param userName
	 * @param password
	 * @param port
	 * @param dbName
	 */
	public DBServer(String driver, String url, String userName, String password, String port, String dbName) 
	{
		createConnection(driver, url, userName, password, port, dbName);
	}
	
	/**
	 * Establish a connection
	 * @param server
	 * @param userName
	 * @param password
	 * @param port
	 * @param dbName
	 */
	public void createConnection(String driver, String url, String userName, String password, String port, String dbName)
	{
		try {
			this.driver = driver;
			this.url = url;
			this.userName = userName;
			this.password = password;
			this.port = port;
			this.dbName = dbName;
			Class.forName(driver);
			conn = DriverManager.getConnection(url+dbName, userName, password);
			
		}
		catch (Exception e) {
			System.out.println("Error while opening a connection to database server: " + e.getMessage());
		}
	}

	/**
	 * Get an additional connection for nested queries
	 * @return
	 */
	public DBServer getAdditionalConnection() 
	{
		return new DBServer(this.driver, this.url, this.userName, this.password, this.port, this.dbName);
	}
	
	/**
	 * Close connection to the database server	 
	 */
	public void close()
	{
		try 
		{
			conn.close();
		}
		catch (Exception e) 
		{
			System.out.println("Error while closing the connection to database server: " + e.getMessage());
		}
	}
	
	/**
	 * Execute a query, return a result set
	 * @param sqlstatement
	 * @return
	 */
	public ResultSet executeQuery(String sqlstatement) 
	{
		try
		{
			Statement st = conn.createStatement();
			st.setFetchSize(1000);
			return st.executeQuery (sqlstatement);
		}
		catch(Exception e) 
		{
			System.out.println("Error while executing a query: " + e.getMessage());			
			return null;
		}
	}
	
	/**
	 * Execute a SQL DDL statement
	 * @param DDL statement
	 * @return
	 * @author David Jiang
	 */
	final public void execute(String ddlStatement) throws SQLException{
		Statement stat = conn.createStatement();
		stat.execute(ddlStatement);
	}
	
	/**
	 * Create an SQL statement for executing later
	 * @return
	 */
	public Statement createStatement() 
	{
		Statement stmt = null;
		try 
		{
			stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MIN_VALUE);
		} 
		catch (Exception e) 
		{
			System.out.println("Error while creating a statement: " + e.getMessage());
		}
		return stmt;
	}
	
	/**
	 * Create an SQL statement for executing later
	 * @param rsType
	 * @param rsConcurrency
	 * @return
	 */
	public Statement createStatement(int rsType, int rsConcurrency) 
	{
		Statement stmt = null;
		try 
		{
			stmt = conn.createStatement(rsType, rsConcurrency);
			stmt.setFetchSize(Integer.MIN_VALUE);
		} 
		catch (Exception e) 
		{
			System.out.println("Error while creating a statement: " + e.getMessage());
		}
		return stmt;    	
	}

	/**
	 * Create an SQL statement with parameters, which are updated later
	 * @param sql
	 * @return
	 */
	public PreparedStatement createPreparedStatement(String sql) 
	{
		try 
		{
			return conn.prepareStatement(sql);
		}
		catch (Exception e) 
		{
			System.out.println("Error while creating a prepare statement: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Create an SQL statement with parameters, which are updated later
	 * @param sql
	 * @param rsType
	 * @param rsConcurrency
	 * @return
	 */
	public PreparedStatement createPreparedStatement(String sql, int rsType, int rsConcurrency) 
	{
		try 
		{
			return conn.prepareStatement(sql, rsType, rsConcurrency);
		}
		catch (Exception e) 
		{
			System.out.println("Error while creating a prepare statement: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Get all tables in the database
	 * @return
	 */
	public Vector<String> getTables()
	{
		try 
		{
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getTables(null, null, null, null);
			Vector<String> tables = new Vector<String>();
			while (rSet.next()) 
			{
				String tableName = rSet.getString(3);
				tables.addElement(tableName);
			}
			rSet.close();
			return tables;
		}
		catch(Exception e)
		{
			System.out.println("Error while retrieving names of tables in the database: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Check if a table exists in the database
	 * @param tableName
	 * @return
	 */
	public boolean hasTable(String tableName)
	{
		Vector<String> tables = getTables();
    	for (int i = 0; i < tables.size(); i++)
    	{
    		if(((String)tables.get(i)).compareToIgnoreCase(tableName)==0)
    			return true;    		
    	}
    	return false;
	}
	
	/**
	 * Check if a column exists in a table
	 * @param columnName
	 * @return
	 */
	public boolean hasColumn(String tableName, String columnName)
	{		
		Vector<String> cols = this.getTableDescription(tableName);
    	for(int j = 0; j < cols.size(); j += 2)
    	{
    		String colName = (String)cols.get(j);
			if (colName.compareToIgnoreCase(columnName) == 0)
				return true;
    	}
		return false;
	}
	
	/**
	 * Retrieve table description
	 * @author David Jiang
	 * @param table_name 
	 * @throws SQLException 
	 */
	public ResultSetMetaData getTableSchema(String table_name) throws SQLException{
		ResultSetMetaData desc = null;
		ResultSet rs = executeQuery("select * from " + table_name);
		desc = rs.getMetaData();
		return desc;
	}
	
	/**
	 * Retrieve table description
	 * @deprecated
	 * @param tableName
	 * @return
	 */
	public Vector<String> getTableDescription(String tableName) 
	{
		try
		{
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getColumns(null, null, tableName, null);
			Vector<String> columns = new Vector<String>();
			while(rSet.next())
			{
				String colName = rSet.getString(4);
				String colType = rSet.getString(6);
				columns.addElement(colName);
				columns.addElement(colType);
			}
			rSet.close();
			return columns;
		}
		catch(Exception ex)
		{
			System.out.println("Exception in DatabaseAccess.describeTable()");
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get primary key of a table
	 * @param tableName
	 * @return
	 */
	public Vector<String> getPrimaryKey(String tableName) 
	{
		try 
		{
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getPrimaryKeys(null, null, tableName);
			Vector<String> prmKey = new Vector<String>();
			while(rSet.next())
			{
				String colName = rSet.getString(4);
				prmKey.addElement(colName);
			}
			rSet.close();
			return prmKey;
		}
		catch (Exception e) 
		{
			System.out.println("Error while getting primary key of a table: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Get exported keys
	 * @param primaryTable
	 * @return
	 */
	public Vector<String> getExportedKeys(String primaryTable)
	{
		try
		{
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getExportedKeys("", null, primaryTable);
			Vector<String> primaryKeys = new Vector<String>();
			while(rSet.next())
			{
				String primaryTableName = rSet.getString(3);
				String primaryColumn = rSet.getString(4);
				String foreignTableName = rSet.getString(7);
				String foreignColumn = rSet.getString(8);
				primaryKeys.addElement(primaryTableName);
				primaryKeys.addElement(primaryColumn);
				primaryKeys.addElement(foreignTableName);
				primaryKeys.addElement(foreignColumn);
			}
			rSet.close();
			return primaryKeys;
		}
		catch(Exception ex)
		{
			System.out.println("Exception in DatabaseAccess.getExportedKeys()");
			System.out.println(ex.getCause() + " " + ex.getMessage());
			return null;
		}
	}
	
	/**
	 * Retrieve file path for creating tempt files
	 * @param absPath
	 * @return
	 */
	public String getMySQLFilepath(String absPath)
	{		
		StringTokenizer fNameTokens = new StringTokenizer(absPath, File.separator);
    	String mysqlPath;

    	if(absPath.indexOf(":") > 0)
    		mysqlPath = "";    	
    	else
    		mysqlPath = "/";
    	
		while(fNameTokens.hasMoreTokens())
			mysqlPath += fNameTokens.nextToken() + "/";		
		mysqlPath = mysqlPath.substring(0, mysqlPath.length()-1);
		
		return mysqlPath;
	}

	/**
	 * Get indexes of a table
	 * @param tableName
	 * @return
	 */
	public Vector<String> getIndexes(String tableName)
	{
		try 
		{
			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet rSet = metaData.getIndexInfo(null, null, tableName, false, true);
			Vector<String> indexes = new Vector<String>();
			while(rSet.next())
			{
				String type = rSet.getString(6);
				String colName = rSet.getString(9);
				indexes.addElement(type);
				indexes.addElement(colName);
			}
			rSet.close();
			return indexes;
		}
		catch(Exception ex)
		{
			System.out.println("Exception in DatabaseAccess.getIndexes()");
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get connection
	 * @return
	 */
	public Connection getConnection()
	{
		return this.conn;
	}	

}