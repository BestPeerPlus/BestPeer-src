/*
 * @(#) ToolBar.java 1.0 2006-1-2
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

//import sg.edu.nus.accesscontrol.bootstrap.DlgRoleManagement;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.LanguageLoader;

/**
 * Implement the tool bar of the bootstrap's UI.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-2
 */

public final class ToolBar extends JToolBar implements ActionListener
{
	
	// private member
	private static final long serialVersionUID = 9216101996763678645L;
	private BootstrapGUI bootstrapGUI;
	
	/* define buttons */
	private JButton btnStart;
	private JButton btnStop;
	private JButton btnConfig;
	private JButton btnRoleManage;
	
	/* define command names */
	private final String[] commands  = {
			LanguageLoader.getProperty("gui.start"), 
			LanguageLoader.getProperty("gui.stop"), 
			LanguageLoader.getProperty("gui.conf"), 
			LanguageLoader.getProperty("gui.exit"),
			"Role Management"};
	
	/**
	 * Construct the tool bar.
	 * 
	 * @param gui the reference of the <code>BootstrapGUI</code>
	 */
	public ToolBar(BootstrapGUI gui)
	{
		this.bootstrapGUI = gui;
		this.addButtons();
		this.setFloatable(false);
		this.setRollover(true);
	}
	
	/**
	 * Init buttons of the tool bar.
	 */
	private void addButtons()
	{
		/* add buttons bellow */
		btnStart = makeButton(commands[0], commands[0], "Start Service", commands[0]);
		this.add(btnStart);
		
		btnStop = makeButton(commands[1], commands[1], "Stop Service", commands[1]);
		this.add(btnStop);

		this.addSeparator();
		
		btnConfig = makeButton(commands[2], commands[2], "Configure Bootstrap Server", commands[2]);
		this.add(btnConfig);
		
		//VHTam
		btnRoleManage = makeButton("role",commands[4], "Configure Role for Access control", commands[4]);
		this.add(btnRoleManage);
		//end VHTam

		this.addSeparator();
		
		JButton button = makeButton(commands[3], commands[3], "Exit", commands[3]);
		this.add(button);
	}
	
	/**
	 * Create a button with system specified parameters.
	 * 
	 * @param image the URL of the icon image
	 * @param cmd the command name of the button
	 * @param tip the tool tip text
	 * @param name the name to be displayed
	 * 
	 * @return the instance of a tool bar button
	 */
	private JButton makeButton(String image, String cmd, String tip, String name)
	{
		/* look for the image */
        String imageLoc = AbstractMainFrame.SRC_PATH + image + ".png";

        /* create and initialize the button */
        JButton button = new JButton(name);
        button.setActionCommand(cmd);
        button.setToolTipText(tip);
        button.addActionListener(this);

        try 
        {	//image found
            button.setIcon(new ImageIcon(imageLoc, name));
        } 
        catch (Exception e) 
        {   //no image found
        }

        return button;
	}
	
	/**
	 * Execute an action.
	 * 
	 * @param event the action event
	 */
	public void actionPerformed(ActionEvent event) 
	{
	    String cmd = event.getActionCommand();

        /* if start server */
        if (cmd.equals(commands[0])) 
        { 	
            bootstrapGUI.startServer();
        } 
        /* if stop server */
        else if (cmd.equals(commands[1])) 
        {
            bootstrapGUI.logout(false, true, false);
        } 
        /* if configure */
        else if (cmd.equals(commands[2])) 
        {
        	//new ConfigDialog(bootstrapGUI);
        	
        	String title = "Configure Bootstrap Server";
    		DlgBootstrapConfig dlg = new DlgBootstrapConfig(bootstrapGUI, title, 350, 400, "" + Bootstrap.LOCAL_SERVER_PORT,
    				"" + Bootstrap.CAPACITY);
    		
    		dlg.setDbDriver(BootstrapGUI.DB_DRIVER);
    		dlg.setDbURL(BootstrapGUI.DB_URL);
    		dlg.setDbName(BootstrapGUI.DB_NAME);
    		dlg.setUserName(BootstrapGUI.DB_USERNAME);
    		dlg.setPassword(BootstrapGUI.DB_PASSWORD);
    		
    		dlg.setVisible(true);
    		
    		if (dlg.isOkPressed()){
    			Bootstrap.LOCAL_SERVER_PORT = dlg.getPort();
    			Bootstrap.CAPACITY = dlg.getCapacity();
    			
    			BootstrapGUI.DB_DRIVER = dlg.getDbDriver();
    			BootstrapGUI.DB_URL = dlg.getDbURL();
    			BootstrapGUI.DB_NAME = dlg.getDbName();
    			BootstrapGUI.DB_USERNAME = dlg.getUserName();
    			BootstrapGUI.DB_PASSWORD = dlg.getPassword();
    			
    			//close old conn
    			if (bootstrapGUI.conn!=null){
    				try {
						bootstrapGUI.conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    			
    			bootstrapGUI.createDbConnection();
    		}
    		
        }
        /* if exit */
        else if (cmd.equals(commands[3]))
        {
       		bootstrapGUI.processWhenWindowClosing();
        	System.exit(0);
        }
        
        /* if role management */
        // VHTam
        else if (cmd.equals(commands[4]))
        {
    		//DlgRoleManagement dlg = new DlgRoleManagement(null,"Role setting for Access control", bootstrapGUI.conn);
    		
        	//dlg.setVisible(true);


        }
        
	}
	
	private int checkWebServer(){
	       String computer = "localhost";
	        int httpPort = 80; //each computer uses this port for http
	        BufferedReader in = null;
	        BufferedWriter out = null;
	        Socket socketComputer = null;
	 
	        try{
	            System.out.println(computer + ":" + httpPort);
	            socketComputer = new Socket(computer,httpPort);
	            socketComputer.setSoTimeout(1000);
	            out = new BufferedWriter(new OutputStreamWriter(socketComputer.getOutputStream()));
	            out.write("a\n");//send something just to see...
	            out.flush();
	            in = new BufferedReader( new InputStreamReader( socketComputer.getInputStream() ) );
	            String timeStamp = in.readLine();
	            System.out.println( computer + " is alive at " + timeStamp );
	        }catch (UnknownHostException e) {
	            return 1;
	        }catch (IOException e) {
	            return 2;
	        }
		 return 0;
	}
	
	private int checkMySqlServer(){
	       String computer = "localhost";
	        int mysqlPort = 3306; //each computer uses this port for mysql
	        BufferedReader in = null;
	        Socket socketComputer = null;
	 
	        try{
	            System.out.println(computer + ":" + mysqlPort);
	            socketComputer = new Socket(computer,mysqlPort);
	            socketComputer.setSoTimeout(1000);
	            in = new BufferedReader( new InputStreamReader( socketComputer.getInputStream() ) );
	            String timeStamp = in.readLine();
	            System.out.println( computer + " is alive at " + timeStamp );
	        }catch (UnknownHostException e) {
	            return 1;
	        }catch (IOException e) {
	            return 2;
	        }
		return 0;
	}
	
	private int checkBestPeerDatabase(){
		String driver = "MySQL";
		String url = "localhost";
		String port = "3306";
		String dbName = "";
		String username = "root";
		String password = "toor";
		String driverFormat="com.mysql.jdbc.Driver";
		String urlFormat = "jdbc:mysql://" + url + "/" + dbName;

		try
		{
			Class.forName(driverFormat);
			Connection con = DriverManager.getConnection(urlFormat, username, password);
			Statement s = con.createStatement();
			s.execute("CREATE DATABASE IF NOT EXISTS BestPeer");
		}
		catch(SQLException sqle)
		{
			sqle.printStackTrace();
			return 1;
		}
		catch(ClassNotFoundException cnfe)
		{
			System.err.println("Not support such driver: " + driver);
			return 2;
		}
		return 0;
	}
	
	private int checkGlobalSchemaPrerequisites(){		
		if (checkWebServer()>0){
			System.out.println("Web Server not found.");
			return 1;
		}
		if (checkMySqlServer()>0){
			System.out.println("MySQL server not found");
			return 2;
		}
		if (checkBestPeerDatabase()>0){
			System.out.println("BestPeer database could not be found nor created");
			return 3;
		}
		return 0;
	}
	

	
	/**
	 * Set tool bar items enable or disable.
	 * 
	 * @param flag the signal to determine if enable tool bar items
	 */
	public void setEnable(boolean flag)
	{
		btnStart.setEnabled(!flag);
		btnConfig.setEnabled(!flag);
		btnStop.setEnabled(flag);
	}
	
}