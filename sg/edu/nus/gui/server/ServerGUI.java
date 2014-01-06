package sg.edu.nus.gui.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.sql.DriverManager;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sg.edu.nus.dbconnection.DBProperty;
import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.util.PeerLog;

/**
 * Main function of server peer GUI
 * 
 * @author Han Xixian
 * 
 * @version 2008-8-1
 *
 */

public class ServerGUI extends AbstractMainFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8997906615159674963L;


	// public members
	/**
	 * Define an obscure port to test whether an instance has existed.
	 */
	public static final int RUN_PORT = 60020; 
	
	
	// protected members
	/** 
	 * Define a super peer manager, who is responsible for providing 
	 * all operations related to the super peer. Through this class, 
	 * the minimal cohensions are expected between GUI and non-GUI services. 
	 */
	protected ServerPeer serverpeer;
	
	private JPanel jContentPane = null;
	private ServerMenuBar menuBar = null;
	private OperatePanel operatePanel = null;

	private Logo_Toolbar_Panel logo_toolbar = null;

	
	public static PeerLog inputLog = null;
	public static PeerLog outputLog = null;
	
	//private Pane pane;
	

	
	/*
	 * Init all static methods or variables for the class object
	 */
	static 
	{
		ServerPeer.load();

	}
	
	/**
	 * Use an obscure port to limit the bootstrap server to one instance.
	 * 
	 * @return if the bootstrap server does not exist, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	private static boolean isSingleton()
	{
		if (!ServerPeer.DEBUG)
		{
			try
			{
				SERVER_SOCKET = new ServerSocket(RUN_PORT);
			}
			catch (BindException e)
			{
		    	JOptionPane.showMessageDialog(null, LanguageLoader.getProperty("system.msg6"));
				return false;
			}
			catch (IOException e)
			{
		    	JOptionPane.showMessageDialog(null, LanguageLoader.getProperty("system.msg7"));
				return false;
			}
		}
		return true;
	}
	


	/**
	 * This is the default constructor
	 */
	public ServerGUI() {
		super(LanguageLoader.getProperty("system.super"), AbstractMainFrame.SRC_PATH + "icon.JPG", 700, 640);
		
		serverpeer = new ServerPeer(this, PeerType.SUPERPEER.getValue());
		
		if(LanguageLoader.locale == -1)
			LanguageLoader.newLanguageLoader(LanguageLoader.english);		
		
		initialize();
	}

	/**
	 * Returns the handler of the <code>ServerPeer</code>.
	 * 
	 * @return the handler of the <code>ServerPeer</code>
	 */
	public ServerPeer peer()
	{
		return this.serverpeer;
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.menuBar = new ServerMenuBar();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();   
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(this.getGraphicsConfiguration());   
		Rectangle desktopBounds = new Rectangle(   
		                  screenInsets.left, screenInsets.top,     
		                  screenSize.width - screenInsets.left - screenInsets.right,   
		                  screenSize.height - screenInsets.top - screenInsets.bottom); 
		
		this.setSize(desktopBounds.width, desktopBounds.height);
		
		this.setContentPane(getJContentPane());
		this.setTitle(LanguageLoader.getProperty("servergui.title"));
		
		this.setJMenuBar(menuBar);
				
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Toolkit.getDefaultToolkit().setDynamicLayout(true);
		
		this.addWindowListener(new WindowListener() {   
            public void windowOpened(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
  
            public void windowClosing(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
  
            public void windowClosed(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
  
            public void windowIconified(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
  
            public void windowDeiconified(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
  
            public void windowActivated(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
  
            public void windowDeactivated(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
        });   
        this.addWindowFocusListener(new WindowFocusListener() {   
            public void windowGainedFocus(WindowEvent e) {   
            	ServerGUI.this.repaint();  
            }   
  
            public void windowLostFocus(WindowEvent e) {   
            	ServerGUI.this.repaint();  
            }   
        });   
        this.addWindowStateListener(new WindowStateListener() {   
            public void windowStateChanged(WindowEvent e) {   
            	ServerGUI.this.repaint();
            }   
        });   

	}
	
	/**
	 * call operatePanel update interface due to new data received in database
	 *
	 */
	public void updateInterface(){
		operatePanel.updateInterface();
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getOperatePanel(), BorderLayout.CENTER);
			jContentPane.add(getLogo_Toolbar(), BorderLayout.NORTH);
		}
		return jContentPane;
	}
	
	public OperatePanel getOperatePanel()
	{
		if(operatePanel == null)
			operatePanel = new OperatePanel(this);
		
		return operatePanel;
	}
	
	public Logo_Toolbar_Panel getLogo_Toolbar()
	{
		if(logo_toolbar == null)
			logo_toolbar = new Logo_Toolbar_Panel(this);
		
		return logo_toolbar;
	}
	
	public void restart()
	{
		this.dispose();
		
		new ServerGUI().setVisible(true);
	}
	
	public void setComponentText()
	{
		this.setTitle(LanguageLoader.getProperty("servergui.title"));
		menuBar.SetComponentText();
		operatePanel.setComponentText();
		logo_toolbar.setComponentText();
	}
	
	/**
	 * When be successful to login, modify the interface of Loing manager
	 * 
	 */
	
	public void showSuccessfulLoginUI()
	{
		operatePanel.setComponentAt(OperatePanel.TAB_LOGINMANAGER_INDEX, 
				new SuccessfulLoginPanel(operatePanel));
		
		String userType = operatePanel.getUserType();
		
		if(userType.equals(LanguageLoader.getProperty("UserType.normal")))
		{
			operatePanel.setEnabledAt(OperatePanel.TAB_QUERYMANAGER_INDEX, true);
		}
		else
		{
			operatePanel.setEnabledAt(OperatePanel.TAB_DBMANAGER_INDEX, true);
			operatePanel.setEnabledAt(OperatePanel.TAB_QUERYMANAGER_INDEX, true);
			operatePanel.setEnabledAt(OperatePanel.TAB_ACCESSMANAGER_INDEX, true);
		}
	}
	
	/**
	 * Try to start the socket server for accepting incoming connections.
	 * 
	 * @return if the socket server starts, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public static boolean tryStartService()
	{
		try
		{
			ServerSocket socket = new ServerSocket(ServerPeer.LOCAL_SERVER_PORT, ServerPeer.CAPACITY);
			socket.close();
			return true;
		}
		catch (BindException e)
		{
			return false;
		}
		catch (IOException e)
		{
			return false;
		}
	}
	
	/**
	 * Start the socket server for accepting incoming connections.
	 * 
	 * @return if success, return <code>true</code>; 
	 * 			otherwise, return <code>false</code>
	 */
	public boolean startService()
	{
		if (serverpeer.startEventManager(ServerPeer.LOCAL_SERVER_PORT, ServerPeer.CAPACITY) 
				&& serverpeer.startUDPServer(ServerPeer.LOCAL_SERVER_PORT, ServerPeer.CAPACITY, ServerGUI.NORM_FREQ)
				)
		{		
			this.showSuccessfulLoginUI();
			
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
	public boolean stopService()
	{
		if (serverpeer.stopEventManager() && serverpeer.stopUDPServer())
		{			
			return true;
		}
		return false;
	}
	
	public void scheduleUDPSender(long period)
	{
		serverpeer.scheduleUDPSender(period);
	}
	
	public void logout(boolean toBoot, boolean toServer, boolean toClient)
	{
		this.serverpeer.troubleshoot(toBoot, toServer, toClient);
		this.serverpeer.clearSession();
		this.stopService();
	}
	
	public void log(String type, String desp, String host, String src, String dest)
	{
		
	}
	
	@Override
	protected void processWhenWindowClosing() 
	{
		// we use udp to replace tcp here
		//serverpeer.performLogoutRequest();
		this.logout(true, true, true);
	}
//	
//	/**
//	 * Get the instance of the tabbed pane.
//	 * 
//	 * @return the instance of the tabbed pane
//	 */
//	public Pane getPane()
//	{
//		return pane;
//	}
	
	/**
	 * Update the UI of the content pane.
	 * 
	 * @param treeNode the instance of <code>TreeNode</code>
	 */
	public synchronized void updatePane(TreeNode treeNode)
	{
//		pane.updateNodeInfo(treeNode);
	}
	
	/**
	 * 
	 * Reset the UI of the content pane.
	 */
	public synchronized void updatePane()
	{
//		pane.updateNodeInfo();
	}
	
	/**
	 * Set the menus in the menu bar enable or disable.
	 * 
	 * @param flag the signal to determine if enable or disable
	 */
	public void setMenuEnable(boolean flag)
	{
//		menuBar.setEnable(flag);
	}

	public synchronized void updateSchema(String newSchema){
		this.serverpeer.performSchemaUpdate(newSchema);
	}
	
//	/**
//	 * adds an entry into the "Matches" table 
//	 * 
//	 * @param sourceColumn the source column, given as dbName.tableName.columnName
//	 * @param targetColumn the target column, given as dbName.tableName.columnName
//	 * @return a positive integer representing the number of columns in the global db that are still not matched, 
//	 * or a negative number if there were exceptions or other problems
//	 */
//	public synchronized int matchColumns(String sourceColumn, String targetColumn){
//		return this.serverpeer.performColumnMatch(sourceColumn, targetColumn);
//	}
	
//	/**
//	 * Removes an entry from the "Matches" table.
//	 * 
//	 * If one of the parameters is the empty string or null, then it removes all matches pertaining to the other parameter. <br>
//	 * If both parameters are empty, it clears the table<br>
//	 * 
//	 * @param sourceColumn the source column, given as dbName.tableName.columnName
//	 * @param targetColumn the target column, given as dbName.tableName.columnName
//	 * @return a positive integer representing the number of columns in the global db that are still not matched, 
//	 * or a negative number if there were exceptions or other problems
//	 */
//	public synchronized int unmatchColumns(String sourceColumn, String targetColumn){
//		return this.serverpeer.performColumnUnmatch(sourceColumn, targetColumn);
//	}

	/**
	 * @return the serverpeer
	 */
	public ServerPeer getServerpeer() {
		return serverpeer;
	}

	/**
	 * @param serverpeer the serverpeer to set
	 */
	public void setServerpeer(ServerPeer serverpeer) {
		this.serverpeer = serverpeer;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DBProperty dbProperty = new DBProperty();
		
		ServerPeer.ERP_DB_DRIVER = dbProperty.localdb_driver;
		ServerPeer.ERP_DB_URL = dbProperty.localdb_url;
		ServerPeer.ERP_DB_USER = dbProperty.localdb_username;
		ServerPeer.ERP_DB_PASS = dbProperty.localdb_password;
		//ServerPeer.ERP_DB_NAME = dbProperty.localdb_dbName;
		ServerPeer.ERP_DB_NAME = "localdb";
		
		ServerPeer.SERVER_DB_DRIVER = dbProperty.bestpeerdb_driver;
		ServerPeer.SERVER_DB_URL = dbProperty.bestpeerdb_url;
		System.out.println("server url: " + dbProperty.bestpeerdb_url);
		ServerPeer.SERVER_DB_USER = dbProperty.bestpeerdb_username;
		ServerPeer.SERVER_DB_PORT = dbProperty.bestpeerdb_port;
		ServerPeer.SERVER_DB_PASS = dbProperty.bestpeerdb_password;
		
		
		//UtilAccessControl.setMetaDbUser(dbProperty.bestpeerdb_username);
		//UtilAccessControl.setMetaDbPwd(dbProperty.bestpeerdb_password);		
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader("config.ini"));
			
			String line = null;
			
			while((line = reader.readLine())!= null)
			{
				String[] arrData = line.split("=");
				if(arrData[0].equals("Language"))
				{
					int language = Integer.parseInt(arrData[1].trim());
					
					if(language == 0)
						LanguageLoader.newLanguageLoader(LanguageLoader.english);
					else if(language == 1)
						LanguageLoader.newLanguageLoader(LanguageLoader.chinese);
					else if(language == 2)
						LanguageLoader.newLanguageLoader(LanguageLoader.locale);										
				}
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
		
		String URL = ServerPeer.SERVER_DB_URL;
		String password = ServerPeer.SERVER_DB_PASS;
		String user = ServerPeer.SERVER_DB_USER;
		
		try
		{
			Class.forName(ServerPeer.SERVER_DB_DRIVER);
			
			ServerPeer.COMMON_VALUE = new String(URL);
			
			//ServerPeer.conn_localSchema = DriverManager.getConnection(ServerPeer.ERP_DB_URL + ServerPeer.SERVER_DB_NAME, user, password);
			ServerPeer.conn_globalSchema = DriverManager.getConnection(URL + ServerPeer.GLOBAL_DB, user, password);
			ServerPeer.conn_schemaMapping = DriverManager.getConnection(URL + ServerPeer.MATCHES_DB, user, password);
			ServerPeer.conn_exportDatabase = DriverManager.getConnection(URL + ServerPeer.EXPORTED_DB, user, password);
			ServerPeer.conn_bestpeerindexdb = DriverManager.getConnection(URL + ServerPeer.BESTPEERINDEXDATABASE, user, password);
			ServerPeer.conn_metabestpeerdb = DriverManager.getConnection(URL + ServerPeer.METABESTPEERDB, user, password);
 
			if(!ServerPeer.ERP_DB_DRIVER.equals(ServerPeer.SERVER_DB_DRIVER))
				Class.forName(ServerPeer.ERP_DB_DRIVER);
			ServerPeer.conn_localSchema = DriverManager.getConnection(ServerPeer.ERP_DB_URL + ServerPeer.ERP_DB_NAME, 
					                                                  ServerPeer.ERP_DB_USER, ServerPeer.ERP_DB_PASS);
			//ServerPeer.conn_bestpeerdb = DriverManager.getConnection(URL + ServerPeer.BESTPEERDB, user, password);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		/* limit our app to one instance */
		if (!isSingleton())
		{
			System.exit(1);
		}
		
		ServerGUI serverGUI = new ServerGUI();
		serverGUI.setVisible(true); 
	}
	
	
//	private class UniqueColumnKey
//	{
//		public String tablename;
//		public String columnname;
//		public int uniqueKey;
//		
//		public UniqueColumnKey(String tablename, String columnname,
//				int uniqueKey) {
//			super();
//			this.tablename = tablename;
//			this.columnname = columnname;
//			this.uniqueKey = uniqueKey;
//		}
//		/**
//		 * @return the uniqueKey
//		 */
//		public int getUniqueKey() {
//			return uniqueKey;
//		}
//		/**
//		 * @param uniqueKey the uniqueKey to set
//		 */
//		public void setUniqueKey(int uniqueKey) {
//			this.uniqueKey = uniqueKey;
//		}
//		public UniqueColumnKey(String tablename, String columnname) {
//			super();
//			this.tablename = tablename;
//			this.columnname = columnname;
//		}
//		/**
//		 * @return the tablename
//		 */
//		public String getTablename() {
//			return tablename;
//		}
//		public UniqueColumnKey() {
//			super();
//		}
//		/**
//		 * @param tablename the tablename to set
//		 */
//		public void setTablename(String tablename) {
//			this.tablename = tablename;
//		}
//		/**
//		 * @return the columnname
//		 */
//		public String getColumnname() {
//			return columnname;
//		}
//		/**
//		 * @param columnname the columnname to set
//		 */
//		public void setColumnname(String columnname) {
//			this.columnname = columnname;
//		}
//	}
}
