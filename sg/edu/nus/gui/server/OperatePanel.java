package sg.edu.nus.gui.server;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.UIManager;

//import sg.edu.nus.accesscontrol.gui.PanelAccessControlManagement;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.Vector;

/**
 * Class OperatePanel is a JTabbedPane used to organize the main tab for BestPeer
 * @author Han Xixian
 * @version Aug-2-2008
 * 
 */

public class OperatePanel extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5854564620617163788L;
	/**
	 * Static Integers are used to Facilitate to replace specified Panel of JTabbedPane 
	 */
	public static int TAB_LOGINMANAGER_INDEX = 0;
	public static int TAB_DBMANAGER_INDEX = 1;
	public static int TAB_QUERYMANAGER_INDEX = 2;
	public static int TAB_ACCESSMANAGER_INDEX = 3;
		
	private static int TAB_HEIGHT_OFFSET = 10;
	
	/**
	 * There are four tab in OperatePanel
	 * 1. Login Panel : user login
	 * 2. DBManagerPanel: set schema mapping and export data
	 * 3. QueryManagerPanel: perform query processing
	 * 4. PanelAccessControlManagement: user access control
	 */
	LoginPanel loginPanel = null;
	DBManagerPanel dbManagerPanel = null;
	QueryManagerPanel queryManagerPanel = null;
	//PanelAccessControlManagement panelAccessControl = null;
	JPanel panelAccessControl = null;
	
//	ImageIcon loginIcon = new ImageIcon("./sg/edu/nus/gui/res/login.png");
//	ImageIcon dbManagerIcon = new ImageIcon("./sg/edu/nus/gui/res/dbmanager.png");
//	ImageIcon queryManagerIcon = new ImageIcon("./sg/edu/nus/gui/res/querymanager.png");
//	ImageIcon accessManagerIcon = new ImageIcon("./sg/edu/nus/gui/res/accessmanager.gif");

	/**
	 * Icons are used to illustrate functionality of each tab
	 */
	ImageIcon loginIcon = new ImageIcon("./sg/edu/nus/gui/res/new/login.png");
	ImageIcon dbManagerIcon = new ImageIcon("./sg/edu/nus/gui/res/new/dbmanager.png");
	ImageIcon queryManagerIcon = new ImageIcon("./sg/edu/nus/gui/res/new/query.png");
	ImageIcon accessManagerIcon = new ImageIcon("./sg/edu/nus/gui/res/new/accesscontrol.png");

	
	/**
	 * Set different color for each tab when it is selected or not
	 * The color is RGB color
	 */	
	public static Color tab_selected_color = new Color(0x82, 0xCA, 0xFA);
	public static Color tab_normal_color = new Color(0xCF, 0xEC, 0xEC);
	public static Color panel_color = new Color(0xAF, 0xDC, 0xEC);
	
	/**
	 * ServerGUI is parent component of OperatePanel
	 */
	private ServerGUI servergui = null;
	
	/**
	 * There are two type of user
	 * normal user: only query database
	 * professional user: perform db management and access control	 * 
	 */
	private String userType = null;
	
	/**
	 * This is the default constructor
	 */
	public OperatePanel(ServerGUI servergui) {
		super();
		
		this.servergui = servergui;
		
		initialize();
	}

	/**
	 * setComponentText() is used to set tab text, but now it seems that it is useless.
	 */
	public void setComponentText()
	{
		this.setTitleAt(0, LanguageLoader.getProperty("tab.login"));
		this.setTitleAt(1, LanguageLoader.getProperty("tab.dbmanager"));
		this.setTitleAt(2, LanguageLoader.getProperty("tab.querymanager"));
		this.setTitleAt(3, LanguageLoader.getProperty("tab.accessmanager"));
		
		this.setToolTipTextAt(0, LanguageLoader.getProperty("tab.logintip"));		
		this.setToolTipTextAt(1, LanguageLoader.getProperty("tab.dbmanagertip"));
		this.setToolTipTextAt(2, LanguageLoader.getProperty("tab.querymanagertip"));
		this.setToolTipTextAt(3, LanguageLoader.getProperty("tab.usermanagertip"));

		this.dbManagerPanel.setComponentText();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		/**
		 * Construct four panels for each tab's component.
		 */
		dbManagerPanel = new DBManagerPanel(this);
		queryManagerPanel = new QueryManagerPanel(this);
		loginPanel = new LoginPanel(this);
		
		panelAccessControl = new JPanel();
		
		/**
		 * This part is used to left align icon.
		 * 1. obtain max length of tab title
		 * 2. Space is used to pad to make icon left aligned.
		 */
		//*****************************************************************
		//**Padding begins.
		String loginTitle = LanguageLoader.getProperty("tab.login");
		String dbmanagerTitle = LanguageLoader.getProperty("tab.dbmanager");
		String querymanagerTitle = LanguageLoader.getProperty("tab.querymanager");
		String accessmanagerTitle = LanguageLoader.getProperty("tab.accessmanager");
		
		Vector<String> lengthVec = new Vector<String>();
		lengthVec.add(loginTitle);
		lengthVec.add(dbmanagerTitle);
		lengthVec.add(querymanagerTitle);
		lengthVec.add(accessmanagerTitle);	
		
		int maxLength = -1;
		for(int i = 0; i < lengthVec.size(); i++)
		{
			int currentLength = lengthVec.get(i).length();
			if(currentLength > maxLength)
				maxLength = currentLength;
		}
		
		for(int i = 0; i < lengthVec.size(); i++)
		{
			String str = lengthVec.get(i);
			
			int diff = maxLength - str.length();
			for(int j = 0; j < diff; j++)
			{
				str = " " + str;
			}
			
			lengthVec.set(i, str);
		}				
		
		//Padding ends
		//**********************************************************
		
		/**
		 * Four tabs in OperatePanel. Each for one functionality
		 */
		this.addTab(lengthVec.get(0), this.loginIcon, loginPanel, LanguageLoader.getProperty("tab.logintip"));
		this.addTab(lengthVec.get(1), this.dbManagerIcon, dbManagerPanel, LanguageLoader.getProperty("tab.dbmanagertip"));
		this.addTab(lengthVec.get(2),this.queryManagerIcon, queryManagerPanel, LanguageLoader.getProperty("tab.querymanagertip"));
		this.addTab(lengthVec.get(3), this.accessManagerIcon, panelAccessControl, LanguageLoader.getProperty("tab.accessmanagertip"));
				
		/**
		 * This setSize is useless, because the size will be adjusted according to the whole layout.
		 * But you still need to set a value.
		 */
		this.setSize(700, 600);
		
		/**
		 * Make tab left placement. 
		 * : ),  this makes it more professional, at least it seems that.
		 */
		this.setTabLayoutPolicy(WRAP_TAB_LAYOUT);
		this.setTabPlacement(SwingConstants.LEFT);
		
		/**
		 * In UIManager, set tab different color for selected state
		 */
		UIManager.put("TabbedPane.selected", tab_selected_color);
		this.setBackgroundAt(0, tab_normal_color);
		this.setBackgroundAt(1, tab_normal_color);
		this.setBackgroundAt(2, tab_normal_color);
		this.setBackgroundAt(3, tab_normal_color);
		
		/**
		 * Before logining, three functionalities are not allowed to use.
		 */
		this.setEnabledAt(OperatePanel.TAB_DBMANAGER_INDEX, false);
		this.setEnabledAt(OperatePanel.TAB_QUERYMANAGER_INDEX, false);
		this.setEnabledAt(OperatePanel.TAB_ACCESSMANAGER_INDEX, false);
		
		/**
		 * Set Tab Height and Width to make it look more beautiful
		 */
		this.setUI(new BasicTabbedPaneUI() 
			{ 
				public int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight)     
				{ 
					return super.calculateTabHeight(tabPlacement, tabIndex, fontHeight) + TAB_HEIGHT_OFFSET; 
				} 
				public int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics)     
				{ 
					return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 200;
				} 
			}); 
		
		this.updateUI();
	}

	public void updateInterface(){
		//panelAccessControl.reloadSecurityTree();
	}
	
	/**
	 * @return the servergui
	 */
	public ServerGUI getServergui() {
		return servergui;
	}

	/**
	 * @param servergui the servergui to set
	 */
	public void setServergui(ServerGUI servergui) {
		this.servergui = servergui;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	public QueryManagerPanel getQueryPanel()
	{
		return this.queryManagerPanel;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
