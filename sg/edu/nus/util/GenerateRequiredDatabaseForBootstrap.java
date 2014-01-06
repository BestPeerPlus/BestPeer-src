package sg.edu.nus.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JOptionPane;

import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.peer.ServerPeer;

/**
 * This class is used to generate metadata database for bootstrap peer. 
 */

public class GenerateRequiredDatabaseForBootstrap {
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
	
	public GenerateRequiredDatabaseForBootstrap()
	{
		
	}
	
	public void ConnectToDatabase()
	{
		int count = 0;
		while(count < 60)
		{
			try
			{
				Bootstrap.load();
				
				Class.forName(BootstrapGUI.DB_DRIVER);
				conn = DriverManager.getConnection(BootstrapGUI.DB_URL+BootstrapGUI.DB_NAME, 
						                           BootstrapGUI.DB_USERNAME, BootstrapGUI.DB_PASSWORD);
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
			BufferedReader reader = new BufferedReader(new FileReader("bootstrapsqlcommand.txt"));
			
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
		GenerateRequiredDatabaseForBootstrap app = 
			new GenerateRequiredDatabaseForBootstrap();
		
		app.ConnectToDatabase();
		
		if(app.isConnected)
			app.ExecuteSQLCommand();
		else
			JOptionPane.showMessageDialog(null, "Cannot connect to the database, please check your settings");
	}
}
