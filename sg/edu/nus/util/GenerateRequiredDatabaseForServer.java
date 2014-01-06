package sg.edu.nus.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import javax.swing.JOptionPane;

import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.peer.ServerPeer;

/**
 * This class is used to generate metadata database for server peer. 
 */

public class GenerateRequiredDatabaseForServer 
{
	private Properties prop = null;
	private String username = null;
	private String password = null;
	private Connection conn = null;
	private String URL = ServerPeer.SERVER_DB_URL;
	private String bestpeerDB = null;
	private String driver = null;
	private String url = null;
	private String port = null;
	private boolean isConnected = false;
	
	public GenerateRequiredDatabaseForServer()
	{
	}
	
	public void ConnectToDatabase()
	{
		int count = 0;
		while(count < 60)
		{
			try
			{
				DBProperty dbProperty = new DBProperty();
				driver = dbProperty.bestpeerdb_driver;
				url = dbProperty.bestpeerdb_url;
				username = dbProperty.bestpeerdb_username;
				port = dbProperty.bestpeerdb_port;
				password = dbProperty.bestpeerdb_password;
				bestpeerDB = dbProperty.bestpeerdb_dbName;
				
				Class.forName(driver);
				conn = DriverManager.getConnection(url+bestpeerDB, username, password);
				isConnected = true;
				return;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				count ++;
			}
			
			try{
				Thread.sleep(1000);
			}
			catch(Exception e)
			{
				
			}
		}
		
	}
	
	public boolean isConnected()
	{
		return this.isConnected;
	}
	
	public void ExecuteSQLCommand()
	{
		if(conn == null)
		{
			return;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("serversqlcommand.txt"));
			
			String line = null;
			
			
			while((line = reader.readLine())!= null)
			{
				if(line.indexOf("--") == -1 && !line.equals(""))
				{					
					System.out.println(line);
					
					try{
						Statement stmt = conn.createStatement();
						
						stmt.execute(line);	
					}
					catch(Exception e)
					{
						System.out.println("--------------ERROR-----------------");
						System.out.println("FAIL to process the sql statement:" + line);
						System.out.println("PLEASE check all Sql and run again");
						System.exit(1);
					}								
				}
			}
			
			System.out.println("Database and tables are successfully created");
			
			conn.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{   
		GenerateRequiredDatabaseForServer app = 
			new GenerateRequiredDatabaseForServer();
		
		app.ConnectToDatabase();
		
		if(app.isConnected)
			app.ExecuteSQLCommand();
		else
			JOptionPane.showMessageDialog(null, "Cannot connect to the database, please check your settings");
	}
}
