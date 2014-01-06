/*
 * @(#) SPLBRotateUpdateAdjacentReplyListener.java 1.0 2006-2-22
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
 * Implement a listener for processing SP_LB_ROTATE_UPDATE_ADJACENT_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLBRotateUpdateAdjacentReplyListener extends ActionAdapter
{

	public SPLBRotateUpdateAdjacentReplyListener(AbstractMainFrame gui) 
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
			SPLBRotateUpdateAdjacentReplyBody body = (SPLBRotateUpdateAdjacentReplyBody) msg.getBody();
			
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
			else{
				if (treeNode.getRightAdjacentNode().getLogicalInfo().equals(newAdjacent.getLogicalInfo()))
					treeNode.setRightAdjacentNode(newAdjacent);
			}

			synchronized (treeNode)
			{
				int numOfExpectedRTReply;
				numOfExpectedRTReply = treeNode.getNumOfExpectedRTReply();
				if (numOfExpectedRTReply > 0)
				{
					numOfExpectedRTReply --;
					treeNode.setNumOfExpectedRTReply(numOfExpectedRTReply);
					if (numOfExpectedRTReply == 0)
					{
						serverpeer.stopActivateStablePosition();
						tbody = new SPLBStablePositionBody(serverpeer.getPhysicalInfo(), 
								treeNode.getLogicalInfo(), treeNode.getLogicalInfo());
		
						thead.setMsgType(MsgType.SP_LB_STABLE_POSITION.getValue());
						Message message = new Message(thead, tbody);
						for (int i = 0; i < treeNode.getCoOwnerSize(); i++)
						{
							serverpeer.sendMessage(treeNode.getCoOwnerList(i), message);
						}
						treeNode.clearCoOwnerList();			
					}
				}
			}
			//update GUI
			((ServerGUI) gui).updatePane(treeNode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_LB_ROTATE_UPDATE_ADJACENT_REPLY operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LB_ROTATE_UPDATE_ADJACENT_REPLY.getValue())
			return true;
		return false;
	}

}
