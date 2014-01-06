/*
 * @(#) PingListener.java 1.0 2006-2-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.util.*;

/**
 * Implement a listener when processing a PING message.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-2
 */

public class PingListener implements ActionListener
{

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
			throw new EventHandleException("Ping operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.PING.getValue())
			return true;
		return false;
	}
	
}