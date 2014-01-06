
/*
 * @(#) BootstrapGUI.java 1.0 2005-12-29
 * 
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.awt.*;
import javax.swing.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.logging.LogEvent;
import sg.edu.nus.peer.*;
import sg.edu.nus.util.PeerLog;

import sg.edu.nus.gui.customcomponent.JStatusBar;

/**
 * Implement a bootstrap server that provides the entrance point of 
 * register, sign in and depart from the PeerDB system. 
 * In other words, only through the bootstrap server,
 * each user or peer can know where is the system and 
 * how to join the network.
 * <p>
 * The basic functionality of the bootstrap server is to monitor 
 * all online peers.
 * modified by Han Xixian 2008-6-3
 *   
 * @author Xu Linhao
 * @version 1.0 2005-12-29
 */

public class BootstrapGUI extends AbstractMainFrame
{
	
	// private members
	
	private static final long serialVersionUID = -3865284058256280506L;
	
	/*
	 * The idea is to have an instance of ServerSocket listening over some port 
	 * for as long as the first running instance of our app runs. 
	 * Consequent launches of the same application will try to set up 
	 * their own ServerSocket over the same port, and will get a java.net.BindException
	 * (because we cannot start two versions of a server on the same port), 
	 * and we'll know that another instance of the app is already running. 
	 * 
	 * Now, if, for any unintentional reason, our app dies, the resources used by
	 * the app die away with it, and another instance can start and set the ServerSocket
	 * to prevent more instances from running.
	 * 
	 * We use an obscure port to test whether an instance has existed.
	 */
	/**
	 * Define an obscure port to test whether an instance has existed.
	 */
	public static final int RUN_PORT = 60000; 
	
	/* define components */
	private MenuBar menuBar;
	private ToolBar toolBar;
	private Pane pane;
	private JStatusBar statusBar;
	
	/* define boostrap peer */
	private Bootstrap bootstrap;
	
	// for logging
	public static PeerLog inputLog = null;
	public static PeerLog outputLog = null;
	
	/* for database connection */
	public Connection conn = null;
	//these are initial value only, will be loaded also in config file
	public static boolean DB_USER_CHECK = true;
	public static String DB_URL = "jdbc:mysql://localhost:3306/";
	public static String DB_DRIVER="com.mysql.jdbc.Driver";
	public static String DB_NAME="BootstrapMetaBestPeer";
	public static String DB_USERNAME="root";
	public static String DB_PASSWORD="s3";
	
	
	/*
	 * Init all static methods or variables for the class object
	 */
	static 
	{
		Bootstrap.load();
	}
	
	/**
	 * Use an obscure port to limit the bootstrap server to one instance.
	 * 
	 * @return if the bootstrap server does not exist, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	private static boolean isSingleton()
	{
		try
		{
			SERVER_SOCKET = new ServerSocket(RUN_PORT);
		}
		catch (BindException e)
		{
	    	JOptionPane.showMessageDialog(null, LanguageLoader.getProperty("system.msg1"));
			return false;
		}
		catch (IOException e)
		{
	    	JOptionPane.showMessageDialog(null, LanguageLoader.getProperty("system.msg2"));
			return false;
		}
		return true;
	}
	
	/**
	 * Construct a bootstrap server.
	 */
	public BootstrapGUI()
	{
		super(LanguageLoader.getProperty("system.boot"), AbstractMainFrame.SRC_PATH + "icon.JPG", 700, 500);
		
		// create database connection
		// for global schema, access control
		createDbConnection();
		
		this.centerOnScreen();
		
		bootstrap = new Bootstrap(this, PeerType.BOOTSTRAP.getValue());

		/* set layout */ 
		this.setLayout(new BorderLayout());
		
		/* make menu bar */
		menuBar = new MenuBar(this);
		this.setJMenuBar(menuBar);
		menuBar.setEnable(false);
		
		/* init tool bar */
		toolBar = new ToolBar(this);
		this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
		toolBar.setEnable(false);
		
		/* init content pane with tabbed panel */
		pane = new Pane(this);
		this.getContentPane().add(pane, BorderLayout.CENTER);
		
		/* init status bar */
		statusBar = new JStatusBar();
		statusBar.setText(LanguageLoader.getProperty("system.msg3"));
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		
		/* show frame now */
		this.setVisible(true);
	}
	
	public void createDbConnection(){
			//create connection
			//please ensure the database is created first
			try {
				//System.out.println(BootstrapGUI.DB_DRIVER);
				
				Class.forName(BootstrapGUI.DB_DRIVER);
				
				String dns = BootstrapGUI.DB_URL + BootstrapGUI.DB_NAME;
				
				//System.out.println(dns);
				
				String user = BootstrapGUI.DB_USERNAME;
				String passwd = BootstrapGUI.DB_PASSWORD;
				
				conn = DriverManager.getConnection(dns,user,passwd);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, "Fail to connect to metadata db on Bootstrap Server! Please RUN GENERATE DB...");
				e.printStackTrace();
				
			}		
	}
	
	/**
	 * Get the instance of the tabbed pane.
	 * 
	 * @return the instance of the tabbed pane
	 */
	public Pane getPane()
	{
		return pane;
	}

	/**
	 * Returns the handler of <code>Bootstrap</code>.
	 * 
	 * @return the handler of <code>Bootstrap</code>
	 */
	public AbstractPeer peer() 
	{
		return bootstrap;
	}

	/**
	 * Start the bootstrap service.
	 * 
	 * @return if success, return <code>true</code>; 
	 * 		   otherwise, return <code>false</code>
	 */
	public boolean startServer()
	{
		if (bootstrap.startEventManager(Bootstrap.LOCAL_SERVER_PORT, Bootstrap.CAPACITY) 
				&& bootstrap.startUDPServer(Bootstrap.LOCAL_SERVER_PORT, Bootstrap.CAPACITY, BootstrapGUI.NORM_FREQ))
		{
			statusBar.setText(LanguageLoader.getProperty("system.msg4") + Bootstrap.LOCAL_SERVER_PORT);
			menuBar.setEnable(true);
			toolBar.setEnable(true);
			bootstrap.createLogStore();	// init log directory
			
			//Added by Han Xixian, used for test network status to distribute global schema
			//Modified in 2008-6-3
			
			pane.setEnableTestNetworkStatus(true);
		
			//This is just s demo which is used to show the functionality.
			//The code here should be deleted for formal edition.
			boolean webStatus = true;
			boolean MySQLStatus = true;
			boolean superpeerStatus = true;
			pane.setNetworkStatus(webStatus, MySQLStatus, superpeerStatus);
			
			
			if (inputLog == null)
				inputLog = new PeerLog("./log/bootstrap.in");
			if (outputLog == null)
				outputLog = new PeerLog("./log/bootstrap.out");
			
			return true;
		}
		return false;
	}
	
	/**
	 * Stop the bootstrap server.
	 * 
	 * @return if success, return <code>true</code>; 
	 * 			otherwise, return <code>false</code>
	 */
	public boolean stopServer()
	{
		if (bootstrap.stopEventManager() && bootstrap.stopUDPServer())
		{
			statusBar.setText(LanguageLoader.getProperty("system.msg5"));
			menuBar.setEnable(false);
			toolBar.setEnable(false);
			pane.removeOnlinePeers();
			pane.removeLogEvents();

			pane.clearGlobalSchemaTable();
			pane.setEnableDistributeGlobalSchema(false);
			pane.setEnableTestNetworkStatus(false);
			
			
			if (inputLog != null)
			{
				inputLog.Exit();
				inputLog = null;
			}
			if (outputLog != null)
			{
				outputLog.Exit();
				outputLog = null;
			}
			
			return true;
		}
		return false;
	}
	
	public void scheduleUDPSender(long period)
	{
		bootstrap.scheduleUDPSender(period);
	}
	
	/**
	 * Add the message to the status bar.
	 * 
	 * @param str the message
	 */
	public void addMsg2StatusBar(String str)
	{
		statusBar.setText(str);
	}
	
	public void log(String type, String desp, String host, String src, String dest)
	{
		pane.log(new LogEvent(type, desp, host, src, dest));
	}
	
	@Override
	protected void processWhenWindowClosing() 
	{
		/* do clear job here */
		this.logout(false, true, false);
	}
	
	public void logout(boolean toBoot, boolean toServer, boolean toClient)
	{
		this.bootstrap.troubleshoot(toBoot, toServer, toClient);
		this.stopServer();
	}
	
	// VHTam
	public void sendSchema(String schema){
		this.bootstrap.sendSchema(schema);
	}

	/**
	 * Run the bootstrap server.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		/* limit our app to one instance */
		if (!isSingleton())
		{
			System.exit(1);
		}
		
		/* init bootstrap server now */
		try // set look and feel 
		{ 
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		LanguageLoader.newLanguageLoader(LanguageLoader.english);
		new BootstrapGUI();
	}

}