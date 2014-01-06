/*
 * @(#) MenuBar.java 1.0 2005-12-30
 *
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.awt.event.*;
import java.sql.SQLException;

import javax.swing.*;

//import sg.edu.nus.accesscontrol.UtilAccessControl;
import sg.edu.nus.gui.*;
import sg.edu.nus.peer.*;

/**
 * Implement the menu bar of the bootstrap server.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 */

public final class MenuBar extends JMenuBar implements ActionListener
{
	
	// private members
	private static final long serialVersionUID = 2797854999218800176L;

	/* the handler of the main frame */
	private BootstrapGUI bootstrapGUI;
	
	/* common munu item */
	private JMenuItem statMenuItem;
	private JMenuItem stopMenuItem;
	private JMenuItem confMenuItem;

	/* define menu items */
	private final String[] menuName = {
			LanguageLoader.getProperty("gui.file"),
			LanguageLoader.getProperty("gui.option"),
			LanguageLoader.getProperty("gui.action"),
			LanguageLoader.getProperty("gui.view"),
			LanguageLoader.getProperty("gui.help")};
	
	private final String[][] itemName = 
	{
			{LanguageLoader.getProperty("gui.start"), LanguageLoader.getProperty("gui.stop"), LanguageLoader.getProperty("gui.conf"), LanguageLoader.getProperty("gui.exit")},
			{LanguageLoader.getProperty("menu.top")},
			{LanguageLoader.getProperty("menu.createRole"),LanguageLoader.getProperty("menu.roleConfig"),LanguageLoader.getProperty("menu.createUser"),LanguageLoader.getProperty("menu.createPeer"),LanguageLoader.getProperty("menu.openlog"), LanguageLoader.getProperty("menu.savelog"), LanguageLoader.getProperty("menu.clear")},
			{LanguageLoader.getProperty("menu.refresh"), LanguageLoader.getProperty("menu.speed"), LanguageLoader.getProperty("menu.high"), LanguageLoader.getProperty("menu.normal"), LanguageLoader.getProperty("menu.low")},
			{LanguageLoader.getProperty("menu.feedback"), LanguageLoader.getProperty("menu.help"), LanguageLoader.getProperty("menu.about")}
	};
	
	private KeyStroke[][] keyStroke =
	{
			// for file menu items
			{
				KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK),
				KeyStroke.getKeyStroke(KeyEvent.VK_F6, ActionEvent.CTRL_MASK),
				null,
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK)
			},
			// for option menu items
			{
				null
			},
			// for action menu items
			{
				null, null, null, null,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),
				KeyStroke.getKeyStroke(KeyEvent.VK_C, (ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)),
			},
			// for view menu items
			{
				null, null, null, null, null
			},
			// for help menu items
			{
				KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.CTRL_MASK),
				null,
				KeyStroke.getKeyStroke(KeyEvent.VK_F2, ActionEvent.CTRL_MASK),
			}
	};
	
	private final String[][] commands = 
	{
			{"start", "stop", "configure", "exit"},
			{"top"},
			{"createRole","roleConfig","createUser","createPeer","open", "save", "clear"},
			{"refresh", "update", "high", "normal", "low"},
			{"feedback", "help", "about"}
	};
	
	/**
	 * Init the menu bar and menu items.
	 * 
	 * @param bootstrap the handler of the main frame
	 */
	public MenuBar(BootstrapGUI bootstrap)
	{
		this.bootstrapGUI = bootstrap;
		this.addMenus();
	}
	
	/**
	 * Make menus.
	 */
	private void addMenus()
	{
		JMenu menu = null;
		JMenu subMenu = null;
		JMenuItem menuItem = null;
		
		// add 'File' menu
		menu = this.makeMenu(menuName[0], 'F');
		
		statMenuItem = this.makeMenuItem(itemName[0][0], commands[0][0], keyStroke[0][0]);
		menu.add(statMenuItem);
		
		stopMenuItem = this.makeMenuItem(itemName[0][1], commands[0][1], keyStroke[0][1]);
		menu.add(stopMenuItem);
		menu.addSeparator();
		
		confMenuItem = this.makeMenuItem(itemName[0][2], commands[0][2], keyStroke[0][2]);
		menu.add(confMenuItem);
		menu.addSeparator();
		
		menuItem 	 = this.makeMenuItem(itemName[0][3], commands[0][3], keyStroke[0][3]);
		menu.add(menuItem);
		
		this.add(menu);
		
		// add 'Option' menu
		menu = this.makeMenu(menuName[1], 'O');

		menuItem = this.makeCheckBoxMenuItem(itemName[1][0], commands[1][0], false);
		menu.add(menuItem);
		
		this.add(menu);
		
		// add 'Action' menu
		menu = this.makeMenu(menuName[2], 'A');

		menuItem = this.makeMenuItem(itemName[2][0], commands[2][0], keyStroke[2][0]);
		menu.add(menuItem);
		
		menuItem = this.makeMenuItem(itemName[2][1], commands[2][1], keyStroke[2][1]);
		menu.add(menuItem);
		
		menuItem = this.makeMenuItem(itemName[2][2], commands[2][2], keyStroke[2][2]);
		menu.add(menuItem);

		menuItem = this.makeMenuItem(itemName[2][3], commands[2][3], keyStroke[2][3]);
		menu.add(menuItem);
		
		menu.addSeparator();

		menuItem = this.makeMenuItem(itemName[2][4], commands[2][4], keyStroke[2][4]);
		menu.add(menuItem);
		
		menuItem = this.makeMenuItem(itemName[2][5], commands[2][5], keyStroke[2][5]);
		menu.add(menuItem);
		
		menu.addSeparator();
		
		menuItem = this.makeMenuItem(itemName[2][6], commands[2][6], keyStroke[2][6]);
		menu.add(menuItem);

		this.add(menu);
		
		// add 'Log' menu
		menu = this.makeMenu(menuName[3], 'V');

		menuItem = this.makeMenuItem(itemName[3][0], commands[3][0], keyStroke[3][0]);
		menu.add(menuItem);
		menu.addSeparator();
		
		subMenu = this.makeMenu(itemName[3][1], 'S');

		ButtonGroup bg = new ButtonGroup();
		menuItem = this.makeRadioButtonMenuItem(itemName[3][2], commands[3][2], false);
		subMenu.add(menuItem);
		bg.add(menuItem);
		
		menuItem = this.makeRadioButtonMenuItem(itemName[3][3], commands[3][3], true);
		subMenu.add(menuItem);
		bg.add(menuItem);

		menuItem = this.makeRadioButtonMenuItem(itemName[3][4], commands[3][4], false);
		subMenu.add(menuItem);
		bg.add(menuItem);
		
		menu.add(subMenu);
		
		this.add(menu);
		
		// add 'Help' menu
		menu = this.makeMenu(menuName[4], 'H');

		menuItem = this.makeMenuItem(itemName[4][0], commands[4][0], keyStroke[4][0]);
		menu.add(menuItem);

		menuItem = this.makeMenuItem(itemName[4][1], commands[4][1], keyStroke[4][1]);
		menu.add(menuItem);
		menu.addSeparator();
		
		menuItem = this.makeMenuItem(itemName[4][2], commands[4][2], keyStroke[4][2]);
		menu.add(menuItem);
		
		this.add(menu);
	}
	
	/**
	 * Make individual menu.
	 * 
	 * @param name the menu name
	 * @param mnemonic the shortcut key
	 * @return the instance of <code>JMenu</code>
	 */
	private JMenu makeMenu(String name, char mnemonic)
	{
		JMenu menu = new JMenu(name);
		menu.setMnemonic(mnemonic);
		return menu;
	}
	
	/**
	 * Make individual menu item.
	 * 
	 * @param name the menu item name
	 * @param cmd the command string
	 * @param keyStroke the accelerator key
	 * @return the instance of <code>MenuItem</code>
	 */
	private JMenuItem makeMenuItem(String name, String cmd, KeyStroke keyStroke)
	{ 
		JMenuItem menuItem = new JMenuItem(name);
		menuItem.setActionCommand(cmd);
		menuItem.setAccelerator(keyStroke);
        menuItem.addActionListener(this);
        return menuItem;
	}
	
	/**
	 * Make individual menu item.
	 * 
	 * @param name the menu item name
	 * @param cmd the command string
	 * @param sel the selected state
	 * @return the instance of <code>MenuItem</code>
	 */
	private JCheckBoxMenuItem makeCheckBoxMenuItem(String name, String cmd, boolean sel)
	{
		JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(name, sel);
		menuItem.setActionCommand(cmd);
		menuItem.addActionListener(this);
		return menuItem;
	}
	
	/**
	 * Make individual menu item.
	 * 
	 * @param name the menu item name
	 * @param cmd the command string
	 * @param sel the selected state
	 * @return the instance of <code>MenuItem</code>
	 */
	private JRadioButtonMenuItem makeRadioButtonMenuItem(String name, String cmd, boolean sel)
	{
		JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name, sel);
		menuItem.setActionCommand(cmd);
		menuItem.addActionListener(this);
		return menuItem;
	}
	
	public void actionPerformed(ActionEvent event) 
	{
	    String cmd = event.getActionCommand();
	    
	    /* if start server */
	    if (cmd.equals(commands[0][0]))
	    {
	    	bootstrapGUI.startServer();
	    }
	    /* if stop server */
	    else if (cmd.equals(commands[0][1]))
	    {
	    	bootstrapGUI.logout(false, true, false);	    	
	    }
	    /* if config server */
	    else if (cmd.equals(commands[0][2]))
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
	    /* if exit system */
	    else if (cmd.equals(commands[0][3]))
	    {
       		bootstrapGUI.processWhenWindowClosing();
	    	System.exit(0);
	    }
	    /* if set alway on top */
	    else if (cmd.equals(commands[1][0]))
	    {
	    	if (bootstrapGUI.isAlwaysOnTop())
	    	{
	    		bootstrapGUI.setAlwaysOnTop(false);
	    		return;
	    	}
	    	bootstrapGUI.setAlwaysOnTop(true);
	    }
	    
	    /* if createRole */
	    else if (cmd.equals(commands[2][0]))
	    {
	    	//JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][0]);

	    }
	    /* if roleConfig */
	    else if (cmd.equals(commands[2][1]))
	    {
	    	JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][1]);	    	
	    }
	    /* if createUser */
	    else if (cmd.equals(commands[2][2]))
	    {
	    	//JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][2]);
	    }
	    /* if createPeer */
	    else if (cmd.equals(commands[2][3]))
	    {
	    	JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][3]);
	    }
	    /* if Open log file */
	    else if (cmd.equals(commands[2][4]))
	    {
	    	JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][4]);
	    }
	    /* if save log file */
	    else if (cmd.equals(commands[2][5]))
	    {
	    	JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][5]);
	    }
	    /* if clear log events */
	    else if (cmd.equals(commands[2][6]))
	    {
	    	JOptionPane.showMessageDialog(bootstrapGUI, "Menu item: "+commands[2][6]);
	    }

	    /* if refresh online peers now */
	    else if (cmd.equals(commands[3][0]))
	    {
	    	bootstrapGUI.scheduleUDPSender(AbstractMainFrame.NORM_FREQ);
	    }
	    /* if refresh in high speed */
	    else if (cmd.equals(commands[3][2]))
	    {
	    	bootstrapGUI.scheduleUDPSender(AbstractMainFrame.HIGH_FREQ);
	    }
	    /* if refresh in normal speed */
	    else if (cmd.equals(commands[3][3]))
	    {
	    	bootstrapGUI.scheduleUDPSender(AbstractMainFrame.NORM_FREQ);
	    }
	    /* if refresh in low speed */
	    else if (cmd.equals(commands[3][4]))
	    {
	    	bootstrapGUI.scheduleUDPSender(AbstractMainFrame.LOW_FREQ);
	    }
	    /* if send feedback */
	    else if (cmd.equals(commands[4][0]))
	    {
	    	new FeedbackDialog(bootstrapGUI, "Feedback & Error History", true, 500, 400);
	    }
	    /* if open help topic */
	    else if (cmd.equals(commands[4][1]))
	    {
	    	
	    }
	    /* if open about dialog */
	    else if (cmd.equals(commands[4][2]))
	    {
	    	new AboutDialog(bootstrapGUI, "About Bootstrap Server",
	    					AbstractMainFrame.SRC_PATH + "logo.jpg", 
	    					"Bootstrap Server\r\n\r\nVersion 1.0\r\n" +
							"Copyright National University of Singapore.\r\n" + 
							"All rights reserved.\r\nhttp://www.nus.edu.sg");
	    }
	}
	
	/**
	 * Set menu items enable or disable.
	 * 
	 * @param flag the signal to determine if enable menu items
	 */
	public void setEnable(boolean flag)
	{
		statMenuItem.setEnabled(!flag);
		confMenuItem.setEnabled(!flag);
		stopMenuItem.setEnabled(flag);
	}
	
}