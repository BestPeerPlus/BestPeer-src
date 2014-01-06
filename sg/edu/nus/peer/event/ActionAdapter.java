/*
 * @(#) ActionAdapter.java 1.0 2006-1-25
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.peer.management.*;

/**
 * An abstract adapter class for receiving network events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-25
 */

public class ActionAdapter implements ActionListener
{
	
	// protected member
	protected static final boolean debug = true;
	protected AbstractMainFrame gui;
	
	public ActionAdapter(AbstractMainFrame gui)
	{
		this.gui = gui;
	}
	
	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException
	{
		// do nothing now
		//System.out.println("IN" + msg);
	}

	public boolean isConsumed(Message msg) throws EventHandleException
	{
		return false;
	}

}