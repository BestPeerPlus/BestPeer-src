/*
 * @(#) SPLIAdjacentReplyListener.java 1.0 2006-12-04
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
 * Implement a listener for processing SP_LI_ADJACENT_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-12-04
 */

public class SPLIAdjacentReplyListener extends ActionAdapter
{

	public SPLIAdjacentReplyListener(AbstractMainFrame gui) 
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
			SPLIAdjacentReplyBody body = (SPLIAdjacentReplyBody) msg.getBody();			
			
			/* get the correspondent tree node*/
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			if (body.getDirection() == SPLIAdjacentReplyBody.FROM_LEFT_TO_RIGHT)
			{
				treeNode.setLeftAdjacentNode(body.getNewAdjacent());
			}
			else
			{
				treeNode.setRightAdjacentNode(body.getNewAdjacent());
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
		if (msg.getHead().getMsgType() == MsgType.SP_LI_ADJACENT_REPLY.getValue())
			return true;
		return false;
	}

}