/*
 * @(#) ServerPeer.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer;

import java.io.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.sql.*;
import sg.edu.nus.gui.*;
import sg.edu.nus.gui.server.LoginPanel;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.logging.LogEventType;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.event.*;
import sg.edu.nus.peer.request.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ForceOutBody;
import sg.edu.nus.util.Inet;
import sg.edu.nus.util.Tools;

/**
 * Implement a super peer.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class ServerPeer extends AbstractPeer 
{

	// public members
	/**
	 * The list of the bootstraps.
	 */
	public static String BOOTSTRAP_SERVER_LIST;
	/**
	 * The IP address of the bootstrap server.
	 */
	public static String BOOTSTRAP_SERVER;
	/**
	 * The port of the bootstrap server used for monitoring network events. 
	 */
	public static int BOOTSTRAP_SERVER_PORT = 30010;
	/**
	 * The signal used for determine whether allows to print debug information. 
	 */
	public static boolean DEBUG = true;
	// for indexing and searching
	/**
	 * The path used for storing Lucene index of the client peer.
	 */
	private final static String INDEX_PATH = "./peerdb-test/spindex/";
	// for baton stabilization operation
	/**
	 * Time for a node to become stable once it joins/rejoins the system 
	 */
	public static int TIME_TO_STABLE_STATE = 100;
	/**
	 * Time for a node to become stable once it changes the posision
	 */
	public static int TIME_TO_STABLE_POSITION = 200;
	/**
	 * Time for a failed node to be recovered to a stable position
	 */
	public static int TIME_TO_RECOVER_FAILED_NODE = 200;
	/**
	 * Interval time of checking imbalance at a node
	 */
	public static int TIME_TO_CHECK_IMBALANCE = 300;
	
	/**
	 * The driver to access the erp database on this superpeer
	 */
	public static String ERP_DB_DRIVER = "com.mysql.jdbc.Driver";
	
	/**
	 * The driver to access the local database on this superpeer
	 */
	public static String SERVER_DB_DRIVER = "com.mysql.jdbc.Driver";
	
	public static String ERP_DB_NAME = "localdb";
	
	/**
	 * The location of the erp database on this superpeer
	 */
	public static String ERP_DB_URL = "jdbc:mysql://localhost:3306/";
	
	/**
	 * The location of the database on this superpeer
	 */
	public static String SERVER_DB_URL = "jdbc:mysql://localhost:3306/";
	
	/**
	 * The username to access the erp database on this superpeer
	 */
	public static String ERP_DB_USER ="root";
	
	/**
	 * The username to access the database on this superpeer
	 */
	public static String SERVER_DB_USER ="root";
	
	/**
	 * The password to access the erp database on this superpeer
	 */
	public static String ERP_DB_PASS= "112358";
	
	/**
	 * The password to access the database on this superpeer
	 */
	public static String SERVER_DB_PASS= "112358";
	
	//Added by Han Xixian July 24 2008
	public static String SERVER_DB_PORT = "3306";

	/**
	 * The name of the database holding the schema matches between the local and the global schemas
	 * The name of our view of the global schema (i.e. what has been sent from bootstrap)
	 */
	public static String SERVER_DB_NAME = "mysql";
	public static String MATCHES_DB="BestPeerSchemaMapping";
	public static String GLOBAL_DB="BestPeerServerGS";	
	public static String EXPORTED_DB="exported_db";
	public static String BESTPEERDB = "bestpeerdb";
	public static String BESTPEERINDEXDATABASE = "bestpeerindexdb";
	public static String METABESTPEERDB = "MetaBestPeer";
	
	public static Connection conn_localSchema = null;
	public static Connection conn_globalSchema = null;
	public static Connection conn_schemaMapping = null;
	public static Connection conn_exportDatabase = null;
	public static Connection conn_bestpeerdb = null;
	public static Connection conn_bestpeerindexdb = null;
	public static Connection conn_metabestpeerdb = null;

	public static String COMMON_VALUE = "initial string";
	/**
	 * Database Type added by Han Xixian
	 */
	
	public static String dbtype_mysql = "MySQL";
	public static String dbtype_oracle = "Oracle";
	public static String dbtype_sqlserver = "SQL Server";
	public static String dbtype_db2 = "DB2";
	public static String dbtype_postgreSQL = "PostgreSQL";
	
	// protected members
	/** 
	 * Define the requester manager who is responsible for sending 
	 * all possible requests, e.g., login, logout, and stabilize,
	 * to other super peers or client peers. All request operations
	 * simply invoke the implementation of the <code>ServerRequestManager</code>. 
	 */
	protected ServerRequestManager requestManager;	

	//VHTam: add following code to the next //end VHTam
	//requestManager for access control
	
	//protected AccessControlRequestManager accessControlRequestManager;
	String userName;
	String pwd;
	
	//end VHTam
	
	/**
	 * An indexer that is responsible for indexing all received lucene documents.
	 */
	//protected ServerIndexManager indexManager;
	
	// private members
	private static final String CONFIG_FILE = "./super.ini";
	
	/* variables used in BATON */
	private int order;
	private LinkedList<TreeNode> nodeList = null; 
	private ActivateStablePosition activateStablePosition = null;
	private ActivateActiveStatus activateActiveStatus = null;
	
	/**
	 * Load system-defined value.
	 */
	public static void load()
	{
		try
    	{
    		FileInputStream in = new FileInputStream(CONFIG_FILE);
    		Properties keys = null;
    		if (in != null)
    		{
    			keys = new Properties();
    			keys.load(in);
    			
    			String value = keys.getProperty("BOOTSTRAP");
       			if (value != null && !value.equals(""))
       			{
       				if (!checkInet(value))
       				{
           				BOOTSTRAP_SERVER_LIST = Inet.getInetAddress() + ":" + value;
       				}
       				else
       				{
       					BOOTSTRAP_SERVER_LIST = value;
       				}
       			}
        		
       			value = keys.getProperty("BOOTSTARPSERVERPORT");
       			if (value != null && !value.equals(""))
       				BOOTSTRAP_SERVER_PORT = Integer.parseInt(value);

       			value = keys.getProperty("LOCALSERVERPORT");
       			if (value != null && !value.equals(""))
       				LOCAL_SERVER_PORT = Integer.parseInt(value);

       			value = keys.getProperty("CAPACITY");
       			if (value != null && !value.equals(""))
       				CAPACITY = Integer.parseInt(value);

       			value = keys.getProperty("DEBUG");
       			if (value != null && !value.equals(""))
       				DEBUG = (value.equalsIgnoreCase("true"));
       			
       			value = keys.getProperty("TIMETOSTABLESTATE");
       			if (value != null && !value.equals(""))
       				TIME_TO_STABLE_STATE = Integer.parseInt(value);
       			
       			value = keys.getProperty("TIMETOSTABLEPOSITION");
       			if (value != null && !value.equals(""))
       				TIME_TO_STABLE_POSITION = Integer.parseInt(value);
       			
       			value = keys.getProperty("TIMETORECOVERFAILEDNODE");
       			if (value != null && !value.equals(""))
       				TIME_TO_RECOVER_FAILED_NODE = Integer.parseInt(value);
       			
       			value = keys.getProperty("TIMETOCHECKIMBALANCE");
       			if (value != null && !value.equals(""))
       				TIME_TO_CHECK_IMBALANCE = Integer.parseInt(value);

       			value = keys.getProperty("SERVERDBDRIVER");
       			if (value != null && !value.equals(""))
       				SERVER_DB_DRIVER = value;

       			value = keys.getProperty("SERVERDBURL");
       			if (value != null && !value.equals(""))
       				SERVER_DB_URL = value;
       			in.close();

       			value = keys.getProperty("SERVERDBUSER");
       			if (value != null && !value.equals(""))
       				SERVER_DB_USER = value;

       			value = keys.getProperty("SERVERDBDPASS");
       			if (value != null && !value.equals(""))
       				SERVER_DB_PASS = value;

       			value = keys.getProperty("MATCHESDB");
       			if (value != null && !value.equals(""))
       				MATCHES_DB = value;

       			value = keys.getProperty("GLOBALDB");
       			if (value != null && !value.equals(""))
       				GLOBAL_DB = value;
       			
    		}
    	}
    	catch (IOException e)
    	{
    		/*
    		 * if exception occurs, just use default value
    		 */
    		BOOTSTRAP_SERVER_LIST = Inet.getInetAddress() + ":spade.ddns.comp.nus.edu.sg";
    		BOOTSTRAP_SERVER_PORT = 30010;
    		LOCAL_SERVER_PORT = 40000;
    		CAPACITY = 10;
    		DEBUG = true;
    		TIME_TO_STABLE_STATE = 100;
    		TIME_TO_STABLE_POSITION = 200;
    		TIME_TO_RECOVER_FAILED_NODE = 200;
    		TIME_TO_CHECK_IMBALANCE = 300;
    		SERVER_DB_DRIVER="com.mysql.jdbc.Driver";
    		SERVER_DB_URL="jdbc:mysql://localhost:3306/";
    		SERVER_DB_USER="root";
    		SERVER_DB_PASS="toor";
    		MATCHES_DB="BestPeerSchemaMapping";
    		GLOBAL_DB="BestPeerServerGS";
    		return;
    	}
	}
	
	/**
	 * Write user-defined values to file. Notice that this function must be
	 * called after user applies the change.
	 */
	public static void write()
	{
    	try
    	{
    		FileOutputStream out = new FileOutputStream(CONFIG_FILE);
        	Properties keys = new Properties();

        	keys.put("BOOTSTRAP", BOOTSTRAP_SERVER_LIST);
        	keys.put("BOOTSTARPSERVERPORT", Integer.toString(BOOTSTRAP_SERVER_PORT));
        	keys.put("LOCALSERVERPORT", Integer.toString(LOCAL_SERVER_PORT));
        	keys.put("CAPACITY", Integer.toString(CAPACITY));
        	keys.put("DEBUG", Boolean.toString(DEBUG));
        	keys.put("TIMETOSTABLESTATE", Integer.toString(TIME_TO_STABLE_STATE));
        	keys.put("TIMETOSTABLEPOSITION", Integer.toString(TIME_TO_STABLE_POSITION));
        	keys.put("TIMETORECOVERFAILEDNODE", Integer.toString(TIME_TO_RECOVER_FAILED_NODE));
        	keys.put("TIMETOCHECKIMBALANCE", Integer.toString(TIME_TO_CHECK_IMBALANCE));
        	keys.put("SERVERDBDRIVER",SERVER_DB_DRIVER);
        	keys.put("SERVERDBURL",SERVER_DB_URL);
        	keys.put("SERVERDBUSER",SERVER_DB_USER);
        	keys.put("SERVERDBPASS",SERVER_DB_PASS);
        	keys.put("MATCHESDB",MATCHES_DB);
        	keys.put("GLOBALDB",GLOBAL_DB);

        	keys.store(out, "Super Peer Configuration");
        	out.close();
    	}
    	catch (IOException e)
    	{
    		throw new RuntimeException("Cannot write configuration file to disk: " + CONFIG_FILE, e);
    	}
	}

	public ServerPeer(AbstractMainFrame gui, String peerType) 
	{
		super(gui, peerType);		
		this.order = 10;
		/* init online peer manager now */
		this.peerMaintainer = PeerMaintainer.getInstance();		
		/* init request manager */
		this.requestManager = new ServerRequestManager(this);
		
		//VHTam: add following code to the next //end VHTam
		//initialize access control request manager
		
		//this.accessControlRequestManager = new AccessControlRequestManager(this);
		
		//end VHTam
		
	}
	
//	public boolean startIndexManager()
//	{
//		try
//		{
//			this.indexManager = new ServerIndexManager(this, new StandardAnalyzer());
//			new Thread(indexManager, "Index Service Manager").start();
//			return true;
//		}
//		catch (Exception e)
//		{
//			return false;
//		}
//	}
	
//	public boolean stopIndexManager()
//	{
//		try
//		{
//			if (this.indexManager != null)
//			{
//				this.indexManager.stop();
//			}
//			return true;
//		}
//		catch (Exception e)
//		{
//			return false;
//		}
//	}
	
//	/**
//	 * Returns the index manager of this peer.
//	 * 
//	 * @return returns the index manager of this peer
//	 */
//	public ServerIndexManager getIndexManager()
//	{
//		return this.indexManager;
//	}

	// ------------ for tcp service ------------------
	
	@Override
	public boolean startEventManager(int port, int capacity) 
	{
		if (!isEventManagerAlive())
		{
			try
			{
				eventManager = new ServerEventManager(gui, port, capacity);
				new Thread(eventManager).start();
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	// -------------- for udp service --------------------
	
	@Override
	public boolean startUDPServer(int port, int capacity, long period) 
	{
		if (!this.isUDPServerAlive())
		{
			try
			{
				this.udpServer = new ServerPnPServer(gui, port, capacity, period);
				new Thread(udpServer, "UDP Server").start();
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	// -------------- for stabilization -----------------
	
	@Override
	public void troubleshoot(boolean toBoot, boolean toServer, boolean toClient) 
	{
		// if session has already been cleared
		if (!Session.hasInstance()) return;
		
		//otherwise, send troubleshoot message out
		PeerType type = PeerType.SUPERPEER;
		String pid = Session.getInstance().getUserID();
		DatagramPacket trouble = this.troubleshoot(type, pid);
		DatagramSocket socket = this.udpServer.getDatagramSocket();
		
		try 
		{
			// send trouble shoot to bootstrapper
			if (toBoot)
			{
	            // Added by Quang Hieu, send meaningful messages when possible
				// Comment these below lines if wanna test failure
				if (this.performLogoutRequest())
				{				
					//return;
				}
				
				trouble.setAddress(InetAddress.getByName(BOOTSTRAP_SERVER));
				trouble.setPort(BOOTSTRAP_SERVER_PORT);
				for (int i = 0; i < TRY_TIME; i++)
				{
					socket.send(trouble);
					if (debug)
						System.out.println("case 7: send troubleshoot out to " + trouble.getAddress().getHostAddress() + " : " + trouble.getPort());
				}
			}
			// send trouble shoot to server peers
			if (toServer)
			{
				TreeNode node = null;
				TreeNode[] nodes = getTreeNodes();
				if (nodes != null)
				{
					NodeInfo nodeInfo = null;
					int size = nodes.length;
					for (int i = 0; i < size; i++)
					{
						node = nodes[i];
						if (node.getRole() == 1)			// if tree node is self
						{
							// ping parent node
							nodeInfo = node.getParentNode();
							if (nodeInfo != null) { notify(nodeInfo.getPhysicalInfo(), type, pid); }
							// ping left child node
							nodeInfo = node.getLeftChild();
							if (nodeInfo != null) { notify(nodeInfo.getPhysicalInfo(), type, pid); }
							// ping right child node
							nodeInfo = node.getRightChild();
							if (nodeInfo != null) {	notify(nodeInfo.getPhysicalInfo(), type, pid); }
							// ping left adjacent node
							nodeInfo = node.getLeftAdjacentNode();
							if (nodeInfo != null) {	notify(nodeInfo.getPhysicalInfo(), type, pid); }
							// ping right adjacent node
							nodeInfo = node.getRightAdjacentNode();
							if (nodeInfo != null) {	notify(nodeInfo.getPhysicalInfo(), type, pid); }
							// ping left routing table
							RoutingItemInfo rtItem = null;
							RoutingTableInfo table = node.getLeftRoutingTable();
							int tsize = 0;
							if (table != null)
							{
								tsize = table.getTableSize();
								for (int j = 0; j < tsize; j++)
								{
									rtItem = table.getRoutingTableNode(j);
									if (rtItem != null)
										this.notify(rtItem.getPhysicalInfo(), type, pid);
								}
							}
							// ping right routing table
							table = node.getRightRoutingTable();
							if (table != null)
							{
								tsize = table.getTableSize();
								for (int j = 0; j < tsize; j++)
								{
									rtItem = table.getRoutingTableNode(j);
									if (rtItem != null)
										this.notify(rtItem.getPhysicalInfo(), type, pid);
								}
							}
							// exit loop
							break;
						}
					}
				}
			}
			// send trouble shoot to client peers
			if (toClient)
			{
				PeerInfo info = null;
				int size = 0;
				if (this.peerMaintainer.hasClient())
				{
					PeerInfo[] clientList = this.peerMaintainer.getClients();
					size = clientList.length;
					for (int i = 0; i < size; i++)
					{
						info = clientList[i];
						trouble.setAddress(InetAddress.getByName(info.getInetAddress()));
						trouble.setPort(info.getPort());
						for (int j = 0; j < TRY_TIME; j++)
						{
							socket.send(trouble);
							if (debug)
								System.out.println("case 7: send troubleshoot out to " + trouble.getAddress().getHostAddress() + " : " + trouble.getPort());
						}
					}
				}
			}
		} 
		catch (UnknownHostException e) 
		{ /* ignore it */
		} 
		catch (SocketException e) 
		{ /* ignore it */
		} 
		catch (IOException e) 
		{ /* ignore it */
		}
	}
	
	@Override
	public void forceOut(String ip, int port) 
	{
		try
		{
			/* init socket connection with super peer */
			Socket socket = new Socket(ip, port);

			Head head = new Head();
			head.setMsgType(MsgType.FORCE_OUT.getValue());
			Message message = new Message(head, new ForceOutBody());

			/* send force out message to super peer */
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(message);
			
			/* add log event */
			((ServerGUI) gui).log(LogEventType.INFORMATION.getValue(), MsgType.description(head.getMsgType()) + " request is sent out...",	socket.getLocalAddress().getHostName(),	socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort(), System.getProperty("user.name"));
			LogManager.getLogger(this).info(MsgType.description(head.getMsgType()) + " request is sent out from " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
			
			/* close stream */
			oos.close();
			socket.close();
		}
		catch (Exception e)
		{
			// record exception event
			((ServerGUI) gui).log(LogEventType.WARNING.getValue(), "Fail to open connection with client peer because:\r\n" + Tools.getException(e), Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name")); 
			LogManager.getLogger(this).error("Fail to open connection to client peer", e);
		}
	}


	private void notify(PhysicalInfo physical, PeerType type, String pid)
	{
		try
		{
			DatagramSocket socket  = this.udpServer.getDatagramSocket();
			DatagramPacket trouble = this.troubleshoot(type, pid);
			trouble.setAddress(InetAddress.getByName(physical.getIP()));
			trouble.setPort(physical.getPort());
			for (int j = 0; j <TRY_TIME; j++)
			{
				socket.send(trouble);
				if (debug)
					System.out.println("case 7: send troubleshoot out to " + trouble.getAddress().getHostAddress() + " : " + trouble.getPort());
			}
		}
		catch (UnknownHostException e) 
		{	/* if error happens, ignore it */
		}
		catch (IOException e)
		{	/* if error happens, ignore it */
		}
	}
	
// ------------ for lucene index --------------
	
	/**
	 * Returns the string that represents the Lucene directory.
	 * 
	 * @return the string represents the Lucene directory
	 */
	public static String getIndexDir()
	{
		return INDEX_PATH + Session.getInstance().getUserID() + "/";
	}

//	/**
//	 * Returns if the Lucene index exists in the system specified directory.
//	 * 
//	 * @return if exists, return <code>true</code>;
//	 * 			otherwise, return <code>false</code>
//	 */
//	public static boolean indexExists()
//	{
//		return IndexReader.indexExists(getIndexDir());
//	}

//	/**
//	 * Create the file directory for storing the Lucene index.
//	 * 
//	 * @throws IOException
//	 */
//	public synchronized static void createIndexDir() throws IOException
//	{
//		if (!indexExists())
//		{
//			IndexWriter writer = null;
//			try 
//			{
//				writer = new IndexWriter(getIndexDir(), new StandardAnalyzer(), true);
//				writer.setUseCompoundFile(true);
//				writer.close();
//			} 
//			catch (IOException e) 
//			{
//				File file = new File(getIndexDir());
//				throw new RuntimeException("Cannot create directory: " + file.getAbsolutePath());
//				//System.exit(1);
//			}
//			finally
//			{
//				if (writer != null)
//					writer.close();
//			}
//		}
//	}

	/**
	 * Get the order that is used for determining if perform load balance operation.
	 * 
	 * @return the order
	 */
	public int getOrder()
	{
		return this.order;
	}
	
	/**
	 * Get the size of the container of the tree nodes.
	 * 
	 * @return the size of the container of the tree nodes
	 */
	public synchronized int getListSize()
	{
		return this.nodeList.size();
	}
	
	/**
	 * Returns all tree nodes maintained by the server peer.
	 * 
	 * @return all tree nodes maintained by the server peer
	 */
	public synchronized TreeNode[] getTreeNodes()
	{
		if (nodeList != null)
		{
			TreeNode[] result = new TreeNode[nodeList.size()];
			return (TreeNode[]) nodeList.toArray(result);
		}
		return null;
	}
	
	/**
	 * Add a <code>TreeNode</code> into the nodeList for retrieving.
	 * 
	 * @param treeNode the <code>TreeNode</code>
	 */
	//public synchronized void addListItem(TreeNode treeNode)
	public void addListItem(TreeNode treeNode)
	{
		if (this.nodeList == null) 
			this.nodeList = new LinkedList<TreeNode>();
		this.nodeList.add(treeNode);
		
		this.activateActiveStatus = new ActivateActiveStatus(treeNode, TIME_TO_STABLE_STATE);
	}
	
	/**
	 * Get an instance of <code>TreeNode</code> from the container of the tree nodes.
	 * 
	 * @return the instance of <code>TreeNode</code>
	 */
	public synchronized TreeNode getListItem(int idx)
	{
		if ((idx < 0) || (idx >= nodeList.size()))
			throw new IllegalArgumentException("Out of bound index");
		
		return this.nodeList.get(idx);
	}
	
	/**
	 * Remove an instance of <code>TreeNode from the container of the tree node.
	 * 
	 * @param idx the position of the item to be removed
	 * @return the instance of <code>TreeNode</code> to be removed
	 */
	public synchronized TreeNode removeListItem(int idx)
	{
		if ((idx < 0) || (idx >= nodeList.size()))
			throw new IllegalArgumentException("Out of bound index");
		
		return this.nodeList.remove(idx);
	}
	
	/**
	 * Get the tree node whose logical information equals to
	 * the specified logical information.
	 * 
	 * @param dest the logical information
	 * @return if find a tree node whose logical information equals to
	 * 			the specified logical information, return the <code>TreeNode</code>;
	 * 			otherwise, return <code>null</code>
	 */
	public synchronized TreeNode getTreeNode(LogicalInfo dest)
	{
		/* loop */
		while (nodeList == null)
		{
			//waiting for a new tree node is created			
		}
		while (nodeList.size() == 0)
		{
			//double check
		}
		
		if (dest == null)
		{
			return nodeList.get(0);
		}
		else
		{
			for (int i = 0; i < nodeList.size(); i++)
			{
				TreeNode treeNode = nodeList.get(i);
				if (dest.equals(treeNode.getLogicalInfo()))              
				{
					return treeNode;            
				}
			}
			return null;
		}
	}
	
	/**
	 * Get the instance of <code>ActivateStablePosition</code>.
	 * 
	 * @return the instance of <code>ActivateStablePosition</code>
	 */
	public ActivateStablePosition getActivateStablePosition()
	{
		return this.activateStablePosition;
	}
	
	/**
	 * Set the instance of <code>ActivateStablePosition</code>.
	 * 
	 * @param activateStablePosition
	 */
	public void setActivateStablePosition(ActivateStablePosition activateStablePosition)
	{
		this.activateStablePosition = activateStablePosition;
	}
	
	/**
	 * Stop stabilizing.
	 */
	public void stopActivateStablePosition()
	{
		if (activateStablePosition != null)
		{
			activateStablePosition.stop();
			activateStablePosition = null;
	    }
	}
	
	// ------------ for system log --------------
	
	@Override
	public String getLogStore() 
	{
		return logpath + peerType + "/";
	}

	/**
	 * Perform the LOGIN request to the bootstrap server. The method simply 
	 * invokes the same function of the <code>ServerRequestManager</code>.
	 * 
	 * @param window the handler of the login window
	 * @param user the user to be sign in the system
	 * @param pwd the password
	 */
	public void performLoginRequest(LoginPanel window, String user, String pwd)
	{

		requestManager.performLoginRequest(window, user, pwd, BOOTSTRAP_SERVER, BOOTSTRAP_SERVER_PORT);
		
		//VHTam: add following code to the next //end VHTam
		//store user name and pwd for later use also
		//in the case of contacting bootstrap again for role synchronize
		
		this.userName = user;
		this.pwd = pwd;
		
		//end VHTam
	}
	
	public String getServerPeerAdminName(){
		return userName;
	}
	
	//VHTam: add following code to the next //end VHTam
	/**
	 * Perform the ROLE SYNCHRONIZE request to the bootstrap server 
	 *  
	 */
	public void performRoleSynchronize(){
		//accessControlRequestManager.performAccessControlRequest(userName, pwd, BOOTSTRAP_SERVER, BOOTSTRAP_SERVER_PORT);
	}
	//end VHTam
	
	/**
	 * Perform the REGISTER request to the bootstrap server. The method simply 
	 * invokes the same function of the <code>ServerRequestManager</code>.
	 * 
	 * @param window the handler of the register window
	 * @param user the user to be sign in the system
	 * @param pwd the password
	 * @param email the email address of this user
	 */
	public void performRegisterRequest(Window window, String user, String pwd, String email) 
	{
		requestManager.performRegisterRequest(window, user, pwd, BOOTSTRAP_SERVER, BOOTSTRAP_SERVER_PORT, email);
	}

	/**
	 * Perform the I_WILL_LEAVE request to other peers. All online
	 * peers who receive such message will update the online peers
	 * in the <code>PeerMaintainer</code>. The method simply 
	 * invokes the same function of the <code>ServerRequestManager</code>.
	 * 
	 * @return if logout successfully, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public boolean performLogoutRequest() 
	{
		return requestManager.performLogoutRequest(BOOTSTRAP_SERVER, BOOTSTRAP_SERVER_PORT);
	}
	
	/**
	 * Perform a SP_JOIN request to the bootstrap server. The method simply 
	 * invokes the same function of the <code>ServerRequestManager</code>.
	 * 
	 * @param ip the IP address of an online super peer
	 * @param port the port of the online super peer
	 * @return if success, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public boolean performJoinRequest(String ip, int port)
	{
		return requestManager.performJoinRequest(ip, port);
	}
	
	/**
	 * Perform a JOIN_SUCCESS request to the bootstrap server. The method simply 
	 * invokes the same function of the <code>ServerRequestManager</code>.
	 * 
	 * @return if join operation is success, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public boolean performSuccessJoinRequest()
	{
		return requestManager.performSuccessJoinRequest(BOOTSTRAP_SERVER, BOOTSTRAP_SERVER_PORT);
	}
	
	/**
	 * Perform a JOIN_FAILURE request to the bootstrap server. The method simply 
	 * invokes the same function of the <code>ServerRequestManager</code>.
	 * 
	 * @return if join operation is canceled, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public boolean performCancelJoinRequest()
	{
		return requestManager.performCancelJoinRequest(BOOTSTRAP_SERVER, BOOTSTRAP_SERVER_PORT);
	}
	
	/**
	 * Perform the SP_STABILIZE request to for all other super peers,
	 * who are in its routing table, to perform stabilization operation.
	 * 
	 */
	public void performStabilizeRequest() 
	{
		// TODO: do a loop here for each super peer
		//requestManager.performStabilizeRequest();
	}

	/**
	 * Perform the REFRESH request to update the status of all
	 * super peers who are in its routing table. If any inconsistency
	 * happens, the super peer will perform statilization operation
	 * to maintain the correctness of the super peer network. 
	 * 
	 */
	public void performRefreshRequest() 
	{
		// TODO: do a loop here for each super peer
		//requestManager.performRefreshRequest();
	}
	
	public ServerRequestManager getServerRequestManager()
	{
		return this.requestManager;
	}
	
	/**
	 * added by mihai, june 2nd
	 */
	public void performSchemaUpdate(String newSchema){
		requestManager.performSchemaUpdate(newSchema);
	}
	
//	/**
//	 * added my mihai, june 27th
//	 * @param sourceColumn
//	 * @param targetColumn
//	 * @return
//	 * @see ServerGUI.matchColumns
//	 */
//	public int performColumnMatch(String sourceColumn, String targetColumn){
//		return requestManager.performColumnMatch(sourceColumn, targetColumn);
//	}
//	
//	/**
//	 * added my mihai, june 27th
//	 * @param sourceColumn
//	 * @param targetColumn
//	 * @return
//	 * @see ServerGUI.unmatchColumns
//	 */
//	public int performColumnUnmatch(String sourceColumn, String targetColumn){
//		return requestManager.performColumnUnmatch(sourceColumn, targetColumn);
//	}

	/**
	 * @return the requestManager
	 */
	public ServerRequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * @param requestManager the requestManager to set
	 */
	public void setRequestManager(ServerRequestManager requestManager) {
		this.requestManager = requestManager;
	}
}