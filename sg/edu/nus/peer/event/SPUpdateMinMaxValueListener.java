/*
 * @(#) SPUpdateMinMaxValueListener.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.server.*;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implement a listener for processing SP_UPDATE_MAX_MIN_VALUE message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPUpdateMinMaxValueListener extends ActionAdapter
{

	public SPUpdateMinMaxValueListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		super.actionPerformed(oos, msg);
		
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			
			/* get the message body */
			SPUpdateMaxMinValueBody body = (SPUpdateMaxMinValueBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			if (!body.getDirection())
			{
				treeNode.getLeftRoutingTable().setRoutingTableNode(body.getNodeInfo(), body.getIndex());
			}
			else
			{
				treeNode.getRightRoutingTable().setRoutingTableNode(body.getNodeInfo(), body.getIndex());
			}
			
			/* update GUI */
			((ServerGUI) gui).updatePane(treeNode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Super peer updates index range failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_UPDATE_MAX_MIN_VALUE.getValue())
			return true;
		return false;
	}

}