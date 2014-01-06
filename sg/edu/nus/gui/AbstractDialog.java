/*
 * @(#) AbstractDialog.java 1.0 2006-1-26
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * An abstract dialog that simply wrapps the <code>JDialog</code>
 * for the convenience of constructing dialog component.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 */

public abstract class AbstractDialog extends JDialog
{

	// protected members
	/**
	 * The handler of the main frame that invokes this window.
	 */
	protected Frame gui;
	
	/**
	 * Construct a <code>JDialog</code> with specified parameters.
	 * 
	 * @param gui the owner of this component
	 * @param title the dialog title to be displayed
	 * @param model determine whether this is a model dialog
	 * @param height the height of this window
	 * @param width the width of this window
	 */
	public AbstractDialog(Frame gui, String title, boolean model, int height, int width)
	{
		super(gui, title, model);
		
		/* get the handler of the component */
		this.gui = gui;
		
		/* set display configuration */
		this.setSize(height, width);

		if (gui == null)
		{
	        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (screenSize.width  - width ) / 2;
			int y = (screenSize.height - height) / 2;
			this.setLocation(x, y);
		}
		else
		{
			Point pos = gui.getLocation();
			Dimension guiSize = gui.getSize();
			int xoffset = (guiSize.width  - width ) / 3;
			int yoffset = (guiSize.height - height) / 3;
			this.setLocation(pos.x + xoffset, pos.y + yoffset);
		}
		
		this.setResizable(false);
	}
	
	/**
	 * When a <code>WindowEvent</code> happens.
	 * 
	 * @param e a <code>WindowEvent</code>
	 */
	public void processWindowEvent(WindowEvent e)
	{
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
			this.processWhenWindowClosing();
			dispose();
		}
	}
	
	/**
	 * Perform some necessary jobs when the window
	 * is closing.
	 */
	protected abstract void processWhenWindowClosing();
	
}