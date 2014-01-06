/*
 * @(#) ConfigDialog.java 1.0 2006-1-2
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.awt.event.*;

import javax.swing.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.peer.*;

/**
 * Configure the port and capability of <code>EventManager</code> that
 * is responsible for acceping incoming requests at the same time. 
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-2
 */

public class ConfigDialog extends AbstractConfigDialog
{
	
	// private members
	
	private static final long serialVersionUID = 3836055560723321234L;

	/**
	 * Construct the configuration dialog.
	 * 
	 * @param gui the handler of the main frame
	 */
	public ConfigDialog(BootstrapGUI gui)
	{
		super(gui, LanguageLoader.getProperty("label.conf"), true, 300, 200, "" + Bootstrap.LOCAL_SERVER_PORT,
				"" + Bootstrap.CAPACITY);
		
		/* show dialog */
		this.setVisible(true);
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
		
		/* remember to call super class's method */
		return super.checkValue();
	}
	
	public void actionPerformed(ActionEvent event) 
	{
	    String cmd = event.getActionCommand();
	    
	    /* if change configuration */
	    if (cmd.equals(command[0]))
	    {
			if (checkValue())
			{
				/* flush change to file */
				Bootstrap.LOCAL_SERVER_PORT = Integer.parseInt(tfPort.getText().trim());
				Bootstrap.CAPACITY = Integer.parseInt(tfCapacity.getText().trim());
				Bootstrap.write();
				
				dispose();
			}	    	
	    }
	    /* if do not change configuration */
	    else if (cmd.equals(command[1]))
	    {
			dispose();
	    }
	}

	@Override
	protected void processWhenWindowClosing() 
	{
	}

}