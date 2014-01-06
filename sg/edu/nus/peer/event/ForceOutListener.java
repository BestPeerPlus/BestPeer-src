/*
 * @(#) ForceOutListener.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;

/**
 * Implement a listener for processing FORCE_OUT message.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class ForceOutListener extends ActionAdapter
{

	public ForceOutListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		try
		{
			gui.logout(false, true, true);
		}
		catch (Exception e)
		{
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.FORCE_OUT.getValue())
			return true;
		return false;
	}

}