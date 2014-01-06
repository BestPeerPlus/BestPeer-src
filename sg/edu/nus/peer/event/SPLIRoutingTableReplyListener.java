/*
 * @(#) SPLIRoutingTableReplyListener.java 1.0 2006-12-04
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
 * Implement a listener for processing SP_LI_ROUTING_TABLE_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-12-04
 */

public class SPLIRoutingTableReplyListener extends ActionAdapter
{

	public SPLIRoutingTableReplyListener(AbstractMainFrame gui) 
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
			SPLIRoutingTableReplyBody body = (SPLIRoutingTableReplyBody) msg.getBody();			
			
			/* get the correspondent tree node*/
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			int index = body.getIndex();
			boolean direction = body.getDirection();
			RoutingItemInfo newNodeInfo = body.getInfoRequester();

			treeNode.getContent().setMinValue(body.getOldMin());
			treeNode.getContent().setMaxValue(body.getOldMax());
			
			if (direction == SPLIRoutingTableReplyBody.FROM_LEFT_TO_RIGHT)
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
					if (SPGeneralAction.checkRotationPull(treeNode))
		    		{
		    			SPGeneralAction.doLeave(serverpeer, serverpeer.getPhysicalInfo(), treeNode);
		    		}
		    		else{
		    			SPGeneralAction.doFindReplacement(serverpeer, serverpeer.getPhysicalInfo(), treeNode);
		    		}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Message processing fails", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LI_ROUTING_TABLE_REPLY.getValue())
			return true;
		return false;
	}

}