/*
 * @(#) AbstractMainFrame.java 1.0 2006-1-26
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.io.File;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sg.edu.nus.logging.*;
import sg.edu.nus.peer.AbstractPeer;

/**
 * An abstract frame that simply wrapps <code>JFrame</code>
 * for the convenience of constructing a main GUI.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 */

public abstract class AbstractMainFrame extends JFrame implements ComponentListener, LogEventWatcher
{

	// public members
	/**
	 * The path of all system resource files.
	 */
	public static final String SRC_PATH = "./sg/edu/nus/res/";
	
	public static final int[] EVENT_TABLE_WIDTH = {90, 70, 75, 140, 120, 120, 50};

	/** define the high frequency of sending UDP messages to remote peers */
	public final static long HIGH_FREQ = 60000;		// 60 seconds
	
	/** define the normal frequency of sending UDP messages to remote peers */
	public final static long NORM_FREQ = 90000;		// 90 seconds

	/** define the low frequency of sending UDP messages to remote peers */
	public final static long LOW_FREQ  = 120000;	// 120 seconds
	
	// protecte members
	/**
	 * The idea is to have an instance of ServerSocket listening over some port 
	 * for as long as the first running instance of our app runs. 
	 * Consequent launches of the same application will try to set up 
	 * their own ServerSocket over the same port, and will get a java.net.BindException
	 * (because we cannot start two versions of a server on the same port), 
	 * and we'll know that another instance of the app is already running. 
	 * <p>
	 * Now, if, for any unintentional reason, our app dies, the resources used by
	 * the app die away with it, and another instance can start and set the ServerSocket
	 * to prevent more instances from running
	 */
	protected static ServerSocket SERVER_SOCKET;
	
	/**
	 * The minimal height of the window
	 */
	protected int MIN_HEIGHT;
	
	/**
	 * The minimal width of the window
	 */
	protected int MIN_WIDTH;
	
	// private members
	private static final long serialVersionUID = 1946143456523911618L;
	
	public AbstractMainFrame(String title, String image)
	{
		super(title);
		
		/* add listener for window events */ 
		this.addComponentListener(this);
		
		/* set frame icon image */
        try 
        {	//image found
    		this.setIconImage(new ImageIcon(image).getImage());
        } 
        catch (Exception e) 
        {   //no image found
            System.err.println("Resource not found: " + image);
        }
        
        /* initialize a log directory for debug purpose */
        if (true)
        {
        	try
        	{
        		File file = new File("./log");
        		if (!file.exists())
        			file.mkdirs();
        	}
        	catch (Exception e)
        	{
        		throw new RuntimeException("Cannot create log directory for debug", e);
        	}
        }
        
        this.setAlwaysOnTop(false);
	}
	
	/**
	 * Construct a <code>JFrame</code> with specified parameters.
	 * 
	 * @param title the frame title to be displayed
	 * @param image the path of the icon image to be displayed 
	 * @param height the height of the window
	 * @param width the width of the window
	 */
	public AbstractMainFrame(String title, String image, int height, int width)
	{
		this(title, image);
        
        /* set display position */
        this.MIN_HEIGHT = height;
        this.MIN_WIDTH  = width;
		this.setSize(MIN_WIDTH, MIN_HEIGHT);
//		this.centerOnScreen();

        /* set other settings */
		this.setAlwaysOnTop(false);
	}
	
	/**
	 * Returns the handler of <code>AbstractPeer</code> 
	 * correponding to the sub-class of <code>AbstractMainFrame</code>.
	 * 
	 * @return the handler of <code>AbstractPeer</code>
	 */
	public abstract AbstractPeer peer();
	
	/**
	 * Place the window at the center of the screen.
	 */
    protected void centerOnScreen() 
    {
    	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    	this.setLocation((screen.width  - this.MIN_WIDTH ) / 2, 0);
    }  
    
    // ------------ for login and logout ---------------
    
    /**
	 * Re-schedule the UDP Sender with a new time interval.
	 * 
	 * @param period the new period for disseminating UDP packets
     */
	public abstract void scheduleUDPSender(long period);

    /**
     * Execute logout operation and broadcast 
     * <code>{@link sg.edu.nus.protocol.MsgType#TROUBLESHOOT}</code> 
     * message to all relevant peers. 
     * 
	 * @param toBoot if <code>true</code>, send messages to bootstrapper
	 * @param toServer if <code>true</code>, send messages to server peers
	 * @param toClient if <code>true</code>, send messages to client peers
     */
    public abstract void logout(boolean toBoot, boolean toServer, boolean toClient);
    
    // ------------ for customize window shape ------------

	public void componentResized(ComponentEvent arg0) 
	{
		int width  = getWidth();
        int height = getHeight();

        /*
         * check if either the width or the height 
         * is smaller than the minimum value
         */
        boolean resize = false;

        if (width < MIN_WIDTH) 
        {
        	resize = true;
        	width  = MIN_WIDTH;
        }
        if (height < MIN_HEIGHT) 
        {
        	resize = true;
        	height = MIN_HEIGHT;
        }
        
        if (resize) 
        {
        	this.setSize(MIN_WIDTH, MIN_HEIGHT);
        	centerOnScreen();
        }
	}

	public void componentMoved(ComponentEvent arg0) 
	{
	}

	public void componentShown(ComponentEvent arg0) 
	{
	}

	public void componentHidden(ComponentEvent arg0) 
	{
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
			System.exit(0);
		}
	}
	
	/**
	 * Perform some necessary jobs when the window
	 * is closing.
	 */
	protected abstract void processWhenWindowClosing();
	
}