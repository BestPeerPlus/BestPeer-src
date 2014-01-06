package sg.edu.nus.gui.bootstrap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import sg.edu.nus.gui.AbstractDialog;
import sg.edu.nus.peer.Bootstrap;
import sg.edu.nus.peer.LanguageLoader;

/**
 * Config service port of bootstrap peer and database connection for meta data of bootstrap
 * @author VHTam
 *
 */
public class DlgBootstrapConfig extends AbstractDialog implements ActionListener
{
	private static final String CONFIG_FILE = "./bootstrap.ini";
	
	// protected members
	/**
	 * Get the user input of the socket port used for monitoring network events.
	 */
	protected JTextField tfPort;
	/**
	 * Get the maximum number of threads simultanenously to listen network events.
	 */
	protected JTextField tfCapacity;
	/**
	 * The name of buttons.
	 */
	//protected final String[] btnName = {LanguageLoader.getProperty("button.ok"), LanguageLoader.getProperty("button.cancel")};
	protected final String[] btnName = {"Ok","Cancel", "Test Connection"};
	/**
	 * The command string used for buttons.
	 */
	protected final String[] command = {"ok", "cancel", "Test Connection"};
	
	JComboBox jcbDriver =null;
	JTextField jtfURL = null;
	JTextField jtfDbName = null;
	JTextField jtfUser = null;
	JPasswordField jpfPwd = null;
	
	JLabel jlblURL = null;
	JLabel jlblDriver = null;
	JLabel jlblDbName = null;
	JLabel jlblUser = null;
	JLabel jlblPwd = null;
	
	String strPort = null;
	String strCapacity = null;
	String strDriver = null;
	String strURL = null;
	String strDbName = null;
	String strUser = null;
	String strPwd = null;

	String[] drivers = new String[]{"com.mysql.jdbc.Driver",
	"oracle.jdbc.driver.OracleDriver",
	"org.postgresql.Driver",
	"com.microsoft.jdbc.sqlserver.SQLServerDriver"};
	
	String[] driverNames = new String[]{"MySQL","Oracle","PostgreSQL","SQLServer" };
	
	String title = null;
	
	/**
	 * 
	 * Construct the configuration dialog.
	 * 
	 * @param gui the handler of the main frame
	 * @param title the dialog title to be displayed
	 * @param model determine whether this is a model dialog
	 * @param height the height of this window
	 * @param width the width of this window
	 * @param port the port of the local server
	 * @param capacity the maximal number of threads used for handling network events
	 */
	public DlgBootstrapConfig(Frame gui, String title, int height, int width, String port, String capacity)
	{
		super(gui, title, true, height, width);
		this.setLayout(new BorderLayout());
		
		this.title = title;
		
		JPanel panel = null;

		/* make configure panel and add it to content pane */
		panel = makeContentPane(port, capacity);
		this.add(panel, BorderLayout.CENTER);

		/* make button panel and add it to content pane */
		panel = makeButtonPane();
		this.add(panel, BorderLayout.SOUTH);
	}

	public int getPort(){
		return Integer.parseInt(strPort);
	}
	
	public int getCapacity(){
		return Integer.parseInt(strCapacity);
	}
	
	public String getDbDriver(){
		return strDriver;
	}
	public String getDbURL(){
		return strURL;
	}
	public String getDbName(){
		return strDbName;
	}
	public String getUserName(){
		return strUser;
	}
	public String getPassword(){
		return strPwd;
	}

	public void setPort(int port){
		tfPort.setText(Integer.toString(port));
		
	}
	
	public void setCapacity(int capacity){
		tfCapacity.setText(Integer.toString(capacity));
	}
	
	public void setDbDriver(String driver){
		driver = driver.toLowerCase();
		if (driver.contains("mysql")){
			jcbDriver.setSelectedIndex(0);
		} else 
			if (driver.contains("oracle")){
				jcbDriver.setSelectedIndex(1);
			} else 
				if (driver.contains("postgresql")){
					jcbDriver.setSelectedIndex(2);
				} else 
					if (driver.contains("sqlserver")){
						jcbDriver.setSelectedIndex(3);
					}  
	}
	
	public void setDbURL(String url){
		jtfURL.setText(url);
	}
	
	public void setDbName(String dbname){
		jtfDbName.setText(dbname);
	}
	
	public void setUserName(String user){
		jtfUser.setText(user);
	}
	
	public void setPassword(String pwd){
		jpfPwd.setText(pwd);
	}
	/**
	 * Construct the panel used for input configuration information.
	 * 
	 * @param port the port of the local server
	 * @param capacity the maximal number of threads used for handling network events
	 * @return the instance of <code>JPanel</code> with labels and text fields
	 */
	protected JPanel makeContentPane(String port, String capacity)
	{
		JPanel panel = new JPanel(new BorderLayout());
		JPanel textPane = new JPanel(new GridLayout(8, 2, 5, 15));
		
		JLabel label = null;
		//label = new JLabel(LanguageLoader.getProperty("label.serverport"));
		label = new JLabel("Server port:");
		textPane.add(label);
		tfPort 	   = new JTextField(port, 5);
		textPane.add(tfPort);

		//label = new JLabel(LanguageLoader.getProperty("label.connection")); 
		label = new JLabel("Connection capacity:");
		textPane.add(label);
		tfCapacity = new JTextField(capacity, 4);
		textPane.add(tfCapacity);
		
		//
		jlblDriver = new JLabel("Database driver:");
		jlblURL = new JLabel("Database URL:");
		jlblDbName = new JLabel("Database name:");
		jlblUser = new JLabel("User name:");
		jlblPwd = new JLabel("Password:");
		
		jtfURL = new JTextField("jdbc:mysql://localhost:3306/");
		jtfDbName = new JTextField("mysql");
		jtfUser = new JTextField("root");
		jpfPwd = new JPasswordField();
		jcbDriver = createDriverCombobox();
		
		textPane.add(jlblDriver);
		textPane.add(jcbDriver);
		
		textPane.add(jlblURL);
		textPane.add(jtfURL);
		
		textPane.add(jlblDbName);
		textPane.add(jtfDbName);
		
		textPane.add(jlblUser);
		textPane.add(jtfUser);
		
		textPane.add(jlblPwd);
		textPane.add(jpfPwd);
		
		//
		textPane.setBorder(BorderFactory.createTitledBorder(null, title, 
				TitledBorder.DEFAULT_JUSTIFICATION,	TitledBorder.DEFAULT_POSITION, 
				null, null));
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(textPane, BorderLayout.CENTER);
		
		return panel;
	}
	
	/**
	 * Make the button panel.
	 * 
	 * @return the instance of <code>JPanel</code> with buttons
	 */
	protected JPanel makeButtonPane()
	{
		JButton button = null;
		JPanel panel = new JPanel();

		/* set panel layout */
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(Box.createHorizontalGlue());
		
        /* add buttons to panel */
		button = makeButton(btnName[0], command[0]);
        panel.add(button);

        panel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        button = makeButton(btnName[1], command[1]);
        panel.add(button);
        
        button = makeButton(btnName[2], command[2]);
        panel.add(button);
       // button.addActionListener(this);

        return panel;
	}
	
	/**
	 * Make individual button.
	 * 
	 * @param name the button name
	 * @param cmd the command string
	 * @return the instance of <code>JButton</code>
	 */
	protected JButton makeButton(String name, String cmd)
	{ 
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
        button.addActionListener(this);
        return button;
	}
	
	private JComboBox createDriverCombobox(){
		
		JComboBox combo = new JComboBox();
		
		combo.addItem(driverNames[0]);
		combo.addItem("Oracle");
		combo.addItem("PostgreSQL");
		combo.addItem("SQLServer");					

		combo.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox cb = (JComboBox)e.getSource();
		        String dbType = (String)cb.getSelectedItem();
		        if(dbType.equals("MySQL"))
		        {
		        	jtfURL.setText("jdbc:mysql://localhost:3306/");
		        }
		        else if(dbType.equals("Oracle"))
		        {
		        	jtfURL.setText("jdbc:oracle:thin:@localhost:1521:");
		        }
		        else if(dbType.equals("PostgreSQL"))
		        {
		        	jtfURL.setText("jdbc:postgresql://localhost:5432/");
		        }
		        else if(dbType.equals("SQLServer"))
		        {
		        	jtfURL.setText("jdbc:sqlserver://testserver:1433/");
		        }		        
		        
			}
			
		});
		
		return combo;
	}
	
	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	private boolean checkPortAndCapacity()
	{
		/* check port text field */
		String str = tfPort.getText().trim();
		
		if (str == "")
		{
			JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg1"));
			tfPort.grabFocus();
			return false;
		}
		else
		{
			try
			{
				int port = Integer.parseInt(str);
				if ((port <= 0) || (port > 65535))
				{
					JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg2"));
					tfPort.grabFocus();
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg2"));
				tfPort.grabFocus();
				return false;
			}
		}
		
		/* check capacity text field */
		str = tfCapacity.getText().trim();
		if (str == "")
		{
			JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg3"));
			tfCapacity.grabFocus();
			return false;
		}
		else
		{
			try
			{
				int capacity = Integer.parseInt(str);
				if (capacity <= 0)
				{
					JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg2"));
					tfCapacity.grabFocus();
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg2"));
				tfCapacity.grabFocus();
				return false;
			}
		}
		return true;
	}

	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	protected boolean checkValue()
	{
		String str = tfPort.getText().trim();
		try
		{
			int port = Integer.parseInt(str);
			if (port == BootstrapGUI.RUN_PORT)
			{
				JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg4"));
				return false;
			}
		}
		catch (NumberFormatException e)
		{
			JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg2"));
			return false;
		}
		
		return checkPortAndCapacity();
	}
	
	boolean ok = false;
	public boolean isOkPressed(){
		return ok;
	}
	
	public void actionPerformed(ActionEvent event) 
	{
	    String cmd = event.getActionCommand();
	    
	    /* if change configuration */
	    if (cmd.equals(command[0]))
	    {
			if (checkValue())
			{
				ok = true;
				
				/* flush change to file */
				strPort = tfPort.getText().trim();
				strCapacity = tfCapacity.getText().trim();
				
				strDriver = drivers[jcbDriver.getSelectedIndex()];
				strURL = jtfURL.getText().trim();
				strDbName = jtfDbName.getText().trim();
				strUser = jtfUser.getText().trim();
				strPwd = new String(jpfPwd.getPassword());
				
				
				//write to file
				writeBootstrapConfig();
				
				dispose();
			}	    	
	    }
	    /* if do not change configuration */
	    else if (cmd.equals(command[1]))
	    {
	    	ok = false;
	    	
			dispose();
			
	    }
	    else if (cmd.equals(command[2]))
	    {
	    	try
			{
	    		strPort = tfPort.getText().trim();
				strCapacity = tfCapacity.getText().trim();
				
				strDriver = drivers[jcbDriver.getSelectedIndex()];
				strURL = jtfURL.getText().trim();
				strDbName = jtfDbName.getText().trim();
				strUser = jtfUser.getText().trim();
				strPwd = new String(jpfPwd.getPassword());
	    		
				Class.forName(this.strDriver);
				Connection conn = DriverManager.getConnection(this.strURL+strDbName, 
                        strUser, strPwd);
				
				JOptionPane.showMessageDialog(null, "Bootstrap DB Setting is successful!");
				conn.close();
				
			}
			catch(Exception exception)
			{
				exception.printStackTrace();
				JOptionPane.showMessageDialog(null, "Bootstrap DB Setting is wrong!");
			}			
	    }
	}
	
	private void writeBootstrapConfig(){
    	try
    	{
    		FileOutputStream out = new FileOutputStream(CONFIG_FILE);
        	Properties keys = new Properties();
        	
        	keys.put("LOCALSERVERPORT", strPort);
        	keys.put("CAPACITY", strCapacity);
        	
        	keys.put("DB_DRIVER", strDriver);
        	keys.put("DB_URL", strURL);
        	keys.put("DB_NAME", strDbName);
        	keys.put("DB_USERNAME", strUser);
        	keys.put("DB_PASSWORD", strPwd);
        	keys.put("DB_USER_CHECK", Boolean.toString(true));
        	        	
        	keys.store(out, "Bootstrap Server Configuration");
        	
        	out.close();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}		
	}
	
	@Override
	protected void processWhenWindowClosing() 
	{
		
	}
	
	/*
	 * Run dialog as a standalone config dlg for bootstrap server
	 */
	public static void main (String[] args){
		
		DlgBootstrapConfig dlg = new DlgBootstrapConfig(null, "Configure Bootstrap Server", 350, 400, "30000","10");
		
		//show dlg
		dlg.setVisible(true);
		
		System.exit(0);
	}
}