package sg.edu.nus.dbconnection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

/**
 * Load and store database connection congiguration.
 * 
 */
public class DBProperty 
{
	private Properties prop = null;
	private FileInputStream infile = null;
	
	//1.Server information
	public String server = "localhost";
		
	//2.User and password information
	
	public String localdb_driver = null;
	public String localdb_url = null;
	public String localdb_port = null;
	public String localdb_username = null;
	public String localdb_password = null;
	public String localdb_dbName = null;
	
	public String bestpeerdb_driver = null;
	public String bestpeerdb_url = null;
	public String bestpeerdb_port = null;
	public String bestpeerdb_username = null;
	public String bestpeerdb_password = null;
	public String bestpeerdb_dbName = null;
	public String FileName = "connection.ini";
	
	public String mysqlDriver = null;
	public String oracleDriver = null;
	public String sqlserverDriver = null;
	public String postgresqlDriver = null;
	
	/**
	 * Constructor: initialize, read property file name
	 * @param FileName Property file name
	 */
	public DBProperty() {


		try{
			infile = new FileInputStream(FileName);
			prop = new Properties();
			
			prop.load(infile);
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found:" + FileName);
		}
		catch (IOException e) {
			System.out.println("Cannot load the file " + FileName);
		}
		
		//read property values into variables
		this.localdb_driver = prop.getProperty("localdb_driver");
		this.localdb_url = prop.getProperty("localdb_url");
		this.localdb_port = prop.getProperty("localdb_port");
		this.localdb_username = prop.getProperty("localdb_username");
		this.localdb_password = prop.getProperty("localdb_password");
		this.localdb_dbName = prop.getProperty("localdb_dbname");
		
		this.bestpeerdb_driver = prop.getProperty("bestpeerdb_driver");
		this.bestpeerdb_url = prop.getProperty("bestpeerdb_url");
		this.bestpeerdb_port = prop.getProperty("bestpeerdb_port");
		this.bestpeerdb_username = prop.getProperty("bestpeerdb_username");
		this.bestpeerdb_password = prop.getProperty("bestpeerdb_password");
		this.bestpeerdb_dbName = prop.getProperty("bestpeerdb_dbname");
		
		this.mysqlDriver = prop.getProperty("mysql_driver");
		this.oracleDriver = prop.getProperty("oracle_driver");
		this.sqlserverDriver = prop.getProperty("sqlserver_driver");
		this.postgresqlDriver = prop.getProperty("postgresql_driver");		
	}
	
		
	
	public void put_localdb_configure(Vector<String> vec)
	{
		prop.put("localdb_driver", vec.get(0));
		prop.put("localdb_url", vec.get(1));
		prop.put("localdb_port", vec.get(2));
		prop.put("localdb_username", vec.get(3));
		prop.put("localdb_password", vec.get(4));
		prop.put("localdb_dbname", vec.get(5));
		
        try
        {
        	FileOutputStream oFile = new FileOutputStream(FileName,false);
        	prop.store(oFile,"test");   
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        } 
	}
	
	public void put_bestpeer_cinfigure(Vector<String> vec)
	{
		prop.put("bestpeerdb_driver", vec.get(0));
		prop.put("bestpeerdb_url", vec.get(1));
		prop.put("bestpeerdb_port", vec.get(2));
		prop.put("bestpeerdb_username", vec.get(3));
		prop.put("bestpeerdb_password", vec.get(4));
		prop.put("bestpeerdb_dbname", vec.get(5));
		
        try
        {
        	FileOutputStream oFile = new FileOutputStream(FileName,false);
        	prop.store(oFile,"test");   
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        } 
	}
}
