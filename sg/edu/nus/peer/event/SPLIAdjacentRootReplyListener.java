/*
 * @(#) SPLIAdjacentRootReplyListener.java 1.0 2006-12-!4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implement a listener for processing SP_LI_ADJACENT_ROOT_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-12-14
 */

public class SPLIAdjacentRootReplyListener extends ActionAdapter
{

	public SPLIAdjacentRootReplyListener(AbstractMainFrame gui) 
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
			SPLIAdjacentRootReplyBody body = (SPLIAdjacentRootReplyBody) msg.getBody();			
			
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
				treeNode.getContent().setMinValue(body.getBorderValue());
			}
			else
			{
				treeNode.setRightAdjacentNode(body.getNewAdjacent());
				treeNode.getContent().setMaxValue(body.getBorderValue());
			}
			
			if ((treeNode.getLeftAdjacentNode() != null)
				&& (treeNode.getRightAdjacentNode() != null))
			{
				//this node must hold both the root and its left child
				TreeNode transferNode = serverpeer.getTreeNode(new LogicalInfo(1, 1));				
				if (SPGeneralAction.checkRotationPull(transferNode))
				{
					SPGeneralAction.doLeave(serverpeer, serverpeer.getPhysicalInfo(), transferNode);
		    	}
				else{
					SPGeneralAction.doFindReplacement(serverpeer, serverpeer.getPhysicalInfo(), transferNode);
		    	}
				
				//update GUI
				((ServerGUI) gui).updatePane(treeNode);
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
		if (msg.getHead().getMsgType() == MsgType.SP_LI_ADJACENT_ROOT_REPLY.getValue())
			return true;
		return false;
	}

}