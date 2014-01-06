/*
 * @(#) SPLIAdjacentRootListener.java 1.0 2006-12-14
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
 * Implement a listener for processing SP_LI_ADJACENT_ROOT message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-12-14
 */

public class SPLIAdjacentRootListener extends ActionAdapter
{

	public SPLIAdjacentRootListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		super.actionPerformed(oos, msg);
		Body tbody = null;
		Head thead = new Head();
		Message tresult = null;
		
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			
			/* get the message body */
			SPLIAdjacentRootBody body = (SPLIAdjacentRootBody) msg.getBody();			
			
			/* get the correspondent tree node*/
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			boolean direction = body.getDirection();
						
			if (direction == SPLIAdjacentBody.FROM_LEFT_TO_RIGHT)
			{
				if (treeNode.getLeftChild() == null)
				{
					AdjacentNodeInfo adjacentInfo =
						new AdjacentNodeInfo(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo());
					tbody = new SPLIAdjacentRootReplyBody(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo(), adjacentInfo, 
								treeNode.getContent().getMinValue(), 
								!direction, body.getLogicalRequester());
					thead.setMsgType(MsgType.SP_LI_ADJACENT_ROOT_REPLY.getValue());
					tresult = new Message(thead, tbody);
					serverpeer.sendMessage(body.getPhysicalRequester(), tresult);
					
					//update its adjacent link pointing to the current holding peer
					treeNode.getLeftAdjacentNode().setPhysicalInfo(body.getPhysicalRequester());
				}
				else
				{
					body.setLogicalDestination(treeNode.getLeftChild().getLogicalInfo());
					thead.setMsgType(MsgType.SP_LI_ADJACENT_ROOT.getValue());
					tresult = new Message(thead, body);
					serverpeer.sendMessage(treeNode.getLeftChild().getPhysicalInfo(), tresult);
				}				
			}
			else
			{
				if (treeNode.getRightChild() == null)
				{
					AdjacentNodeInfo adjacentInfo =
						new AdjacentNodeInfo(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo());
					tbody = new SPLIAdjacentRootReplyBody(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo(), adjacentInfo, 
								treeNode.getContent().getMaxValue(),
								!direction,	body.getLogicalRequester());
					thead.setMsgType(MsgType.SP_LI_ADJACENT_REPLY.getValue());
					tresult = new Message(thead, tbody);
					serverpeer.sendMessage(body.getPhysicalRequester(), tresult);
				}
				else
				{
					body.setLogicalDestination(treeNode.getRightChild().getLogicalInfo());
					thead.setMsgType(MsgType.SP_LI_ADJACENT_ROOT.getValue());
					tresult = new Message(thead, body);
					serverpeer.sendMessage(treeNode.getRightChild().getPhysicalInfo(), tresult);
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
		if (msg.getHead().getMsgType() == MsgType.SP_LI_ADJACENT_ROOT.getValue())
			return true;
		return false;
	}

}