/*
 * @(#) AbstractRegisterDialog.java 1.0 2006-1-5
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

/**
 * An abstract class used for allowing a user to register himself to the 
 * super peer network by specifying his identifier and password.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-5
 */

public abstract class AbstractRegisterDialog extends AbstractDialog implements ActionListener
{
	
	// private members
	private static final long serialVersionUID = -8430923395387086485L;

	// protected members
	/**
	 * Get the user selection of a peer who will acts as a server to the user's request.   
	 */
	protected JComboBox  cbServer;
	/**
	 * Get the user input of the socket port used for monitoring network events.
	 */
	protected JTextField tfPort;
	/**
	 * Get the user identifier to be registered into the system.
	 */
	protected JTextField tfUserID;
	/**
	 * Get the user's email address.
	 */
	protected JTextField tfEmail;
	/**
	 * Get the user's password.
	 */
	protected JPasswordField tfPassword;
	/**
	 * Get the user's password.
	 */
	protected JPasswordField tfPwdAgain;
	
	/**
	 * The name of buttons.
	 */
	protected final String[] btnName = {"Ok", "Cancel"};
	/**
	 * The command string used for buttons.
	 */
	protected final String[] command = {"ok", "cancel"};
	
	/**
	 * Construct the configuration dialog.
	 * 
	 * @param gui the handler of the main frame
	 * @param title the dialog title to be displayed
	 * @param model determine whether this is a model dialog
	 * @param height the height of this window
	 * @param width the width of this window
	 * @param ip an array of the IP addresses
	 * @param port the server port of the remote server
	 */
	public AbstractRegisterDialog(Frame gui, String title, boolean model, int height, int width, String[] ip, String port)
	{
		super(gui, title, model, height, width);
		this.setLayout(new BorderLayout());
		
		JPanel panel = null;

		/* make configure panel and add it to content pane */
		panel = makeContentPane(ip, port);
		this.add(panel, BorderLayout.CENTER);

		/* make button panel and add it to content pane */
		panel = makeButtonPane();
		this.add(panel, BorderLayout.SOUTH);
	}
	
	/**
	 * Construct the panel used for input register information.
	 * 
	 * @param ip an array of the IP addresses
	 * @param port the server port of the remote server
	 * @return the instance of <code>JPanel</code> with labels and text fields
	 */
	protected JPanel makeContentPane(String[] ip, String port)
	{
		JPanel panel = new JPanel(new BorderLayout());
		JPanel textPane = new JPanel(new GridLayout(6, 2, 5, 5));
		
		JLabel label = null;
		label = new JLabel("Bootstrap Sever: ");
		textPane.add(label);
		
		cbServer  = new JComboBox(ip);
		cbServer.setEditable(true);
		cbServer.setSelectedIndex(0);
		textPane.add(cbServer);

		label = new JLabel("Server Port: ");
		textPane.add(label);
		tfPort 	   = new JTextField(port, 5);
		textPane.add(tfPort);

		label = new JLabel("User ID: "); 
		textPane.add(label);
		tfUserID   = new JTextField();
		textPane.add(tfUserID);
		
		label = new JLabel("Enter Password: "); 
		textPane.add(label);
		tfPassword = new JPasswordField();
		textPane.add(tfPassword);
		
		label = new JLabel("Re-enter Password: "); 
		textPane.add(label);
		tfPwdAgain = new JPasswordField();
		textPane.add(tfPwdAgain);
		
		label = new JLabel("Email: "); 
		textPane.add(label);
		tfEmail	   = new JTextField();
		textPane.add(tfEmail);
		
		textPane.setBorder(BorderFactory.createTitledBorder(null, "Register User Account", 
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
	
	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	protected boolean checkValue()
	{
		String str = (String)cbServer.getSelectedItem();
		if ((str == null) || (str.equals("")))
		{
			JOptionPane.showMessageDialog(gui, "Please select DSN");
			cbServer.grabFocus();
			return false;
		}
		
		str = tfPort.getText().trim();
		if (str == "")
		{
			JOptionPane.showMessageDialog(gui, "Please input port number!");
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
					JOptionPane.showMessageDialog(gui, "Input format error!");
					tfPort.grabFocus();
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(gui, "Input format error!");
				tfPort.grabFocus();
				return false;
			}
		}

		str = tfUserID.getText().trim();
		if ((str == null) || (str.equals("")))
		{
			JOptionPane.showMessageDialog(gui, "Please input user name");
			tfUserID.grabFocus();
			return false;
		}
		
		str = new String(tfPassword.getPassword());
		if ((str == null) || (str.equals("")))
		{
			JOptionPane.showMessageDialog(gui, "Please enter password");
			tfPassword.grabFocus();
			return false;
		}
		
		String str2 = new String(tfPwdAgain.getPassword());
		if ((str2 == null) || (str2.equals("")))
		{
			JOptionPane.showMessageDialog(gui, "Please re-enter password");
			tfPwdAgain.grabFocus();
			return false;
		}
		
		if (!str.equals(str2))
		{
			JOptionPane.showMessageDialog(gui, "Password mismatch, please re-enter");
			tfPassword.setText("");
			tfPwdAgain.setText("");
			tfPassword.grabFocus();
			return false;
		}
		
		return true;
	}
	
}