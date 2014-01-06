/*
 * @(#) AbstractConfigDialog.java 1.0 2006-1-2
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import sg.edu.nus.peer.LanguageLoader;

/**
 * An abstract class used for configuring the port and capability of 
 * <code>EventManager</code> that is responsible for acceping 
 * incoming requests at the same time. 
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-2
 */

public abstract class AbstractConfigDialog extends AbstractDialog implements ActionListener
{
	
	// private members
	private static final long serialVersionUID = 3836055560723321234L;

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
	protected final String[] btnName = {LanguageLoader.getProperty("button.ok"), LanguageLoader.getProperty("button.cancel")};
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
	 * @param port the port of the local server
	 * @param capacity the maximal number of threads used for handling network events
	 */
	public AbstractConfigDialog(Frame gui, String title, boolean model, int height, int width, String port, String capacity)
	{
		super(gui, title, model, height, width);
		this.setLayout(new BorderLayout());
		
		JPanel panel = null;

		/* make configure panel and add it to content pane */
		panel = makeContentPane(port, capacity);
		this.add(panel, BorderLayout.CENTER);

		/* make button panel and add it to content pane */
		panel = makeButtonPane();
		this.add(panel, BorderLayout.SOUTH);
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
		JPanel textPane = new JPanel(new GridLayout(3, 2, 10, 10));
		
		JLabel label = null;
		label = new JLabel(LanguageLoader.getProperty("label.serverport"));
		textPane.add(label);
		tfPort 	   = new JTextField(port, 5);
		textPane.add(tfPort);

		label = new JLabel(LanguageLoader.getProperty("label.connection")); 
		textPane.add(label);
		tfCapacity = new JTextField(capacity, 4);
		textPane.add(tfCapacity);
		
		textPane.setBorder(BorderFactory.createTitledBorder(null, LanguageLoader.getProperty("label.conf"), 
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
	
}