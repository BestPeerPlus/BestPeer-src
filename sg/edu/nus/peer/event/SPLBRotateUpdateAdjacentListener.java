/*
 * @(#) SPLBRotateUpdateAdjacentListener.java 1.0 2006-2-22
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
 * Implement a listener for processing SP_LB_ROTATE_UPDATE_ADJACENT message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLBRotateUpdateAdjacentListener extends ActionAdapter
{

	public SPLBRotateUpdateAdjacentListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		super.actionPerformed(oos, msg);
		
    	Head thead = new Head();
    	Body tbody = null;
    	
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			
			/* get the message body */
			SPLBRotateUpdateAdjacentBody body = (SPLBRotateUpdateAdjacentBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			AdjacentNodeInfo newAdjacent = new AdjacentNodeInfo(body.getPhysicalSender(), body.getLogicalSender());

			boolean direction = body.getDirection();
			if (!direction)
			{
				if (treeNode.getLeftAdjacentNode().getLogicalInfo().equals(newAdjacent.getLogicalInfo()))
					treeNode.setLeftAdjacentNode(newAdjacent);
			}
			else
			{
				if (treeNode.getRightAdjacentNode().getLogicalInfo().equals(newAdjacent.getLogicalInfo()))
					treeNode.setRightAdjacentNode(newAdjacent);
			}

			tbody = new SPLBRotateUpdateAdjacentReplyBody(serverpeer.getPhysicalInfo(), treeNode.getLogicalInfo(),
														!direction, body.getLogicalSender());

			thead.setMsgType(MsgType.SP_LB_ROTATE_UPDATE_ADJACENT_REPLY.getValue());
			Message message = new Message(thead, tbody);
			serverpeer.sendMessage(body.getPhysicalSender(), message);
			
			//update GUI
			((ServerGUI) gui).updatePane(treeNode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_LB_ROTATE_UPDATE_ADJACENT operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LB_ROTATE_UPDATE_ADJACENT.getValue())
			return true;
		return false;
	}

}
