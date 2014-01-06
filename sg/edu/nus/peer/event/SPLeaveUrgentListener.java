/*
 * @(#) SPLeaveUrgentListener.java 1.0 2006-3-3
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implement a listener for processing SP_LEAVE_URGENT message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLeaveUrgentListener extends ActionAdapter
{

	public SPLeaveUrgentListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			PhysicalInfo physicalInfo = serverpeer.getPhysicalInfo();
			
			/* get the message body */
			SPLeaveUrgentBody body = (SPLeaveUrgentBody) msg.getBody();
			
			TreeNode treeNode = body.getTreeNode();
			serverpeer.addListItem(treeNode);
			
			if (SPGeneralAction.checkRotationPull(treeNode))
    		{
    			SPGeneralAction.doLeave(serverpeer, physicalInfo, treeNode);
    		}
    		else{
    			SPGeneralAction.doFindReplacement(serverpeer, physicalInfo, treeNode);
    		}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_LEAVE_URGENT operation failure", e);
		}
	}	
	
	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LEAVE_URGENT.getValue())
			return true;
		return false;
	}

}