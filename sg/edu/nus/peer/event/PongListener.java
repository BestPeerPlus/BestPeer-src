/*
 * @(#) PongListener.java 1.0 2006-2-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.util.*;

/**
 * Implement a listener when processing a PONG message.
 *  
 * @author Xu Linhao
 * @version 1.0 2006-2-4
 */

public class PongListener extends ActionAdapter
{

	public PongListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
    	Head thead = null;
    	Body tbody = null;
    	
		/* register MySQL driver */
		DBConnector.registerDriver();
		
		try
		{

			/* construct the reply message */
	    	new Message(thead, tbody);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Pong operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.PONG.getValue())
			return true;
		return false;
	}
	
}