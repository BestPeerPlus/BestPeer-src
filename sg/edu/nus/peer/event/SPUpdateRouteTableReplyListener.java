/*
 * @(#) SPUpdateRouteTableReplyListener.java 1.0 2006-2-22
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
 * Implement a listener for processing SP_UPDATE_ROUTING_TABLE_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPUpdateRouteTableReplyListener extends ActionAdapter
{

	public SPUpdateRouteTableReplyListener(AbstractMainFrame gui) 
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
			SPUpdateRoutingTableReplyBody body = (SPUpdateRoutingTableReplyBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());			
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			int index = body.getIndex();
			boolean direction = body.getDirection();
			RoutingItemInfo newNodeInfo = body.getInfoRequester();

			if (!direction)
			{
				treeNode.getLeftRoutingTable().setRoutingTableNode(newNodeInfo, index);
			}
			else
			{
				treeNode.getRightRoutingTable().setRoutingTableNode(newNodeInfo, index);
			}

			int numOfExpectedRTReply = treeNode.getNumOfExpectedRTReply();
			if (numOfExpectedRTReply > 0)
			{
				numOfExpectedRTReply --;
				treeNode.setNumOfExpectedRTReply(numOfExpectedRTReply);
				if (numOfExpectedRTReply == 0)
				{
					treeNode.setStatus(TreeNode.ACTIVE);
				}
			}
			
			//update GUI
			((ServerGUI) gui).updatePane(treeNode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_UPDATE_ROUTING_TABLE_REPLY operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_UPDATE_ROUTING_TABLE_REPLY.getValue())
			return true;
		return false;
	}

}