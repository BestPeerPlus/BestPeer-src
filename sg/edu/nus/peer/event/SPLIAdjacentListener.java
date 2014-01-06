/*
 * @(#) SPLIAdjacentListener.java 1.0 2006-12-04
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
import sg.edu.nus.util.PeerMath;

/**
 * Implement a listener for processing SP_LI_ADJACENT message.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-12-04
 */

public class SPLIAdjacentListener extends ActionAdapter
{

	public SPLIAdjacentListener(AbstractMainFrame gui) 
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
			SPLIAdjacentBody body = (SPLIAdjacentBody) msg.getBody();			
			
			/* get the correspondent tree node*/
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			boolean direction = body.getDirection();
			int nodeLevel = treeNode.getLogicalInfo().getLevel();			
			int nodeNumber = treeNode.getLogicalInfo().getNumber();
			int parentLevel = nodeLevel - 1;
			int parentNumber = (nodeNumber + (nodeNumber % 2)) / 2;
			int failedNodeLevel = body.getLogicalFailedNode().getLevel();
			int failedNodeNumber = body.getLogicalFailedNode().getNumber();
			
			if (direction == SPLIAdjacentBody.FROM_LEFT_TO_RIGHT)
			{
				if ((treeNode.getLeftAdjacentNode() != null) && 
				    (treeNode.getLeftAdjacentNode().getLogicalInfo().equals(body.getLogicalFailedNode())))
				{
					AdjacentNodeInfo adjacentInfo =
						new AdjacentNodeInfo(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo());
					tbody = new SPLIAdjacentReplyBody(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo(), adjacentInfo, !direction,
								body.getLogicalRequester());
					thead.setMsgType(MsgType.SP_LI_ADJACENT_REPLY.getValue());
					tresult = new Message(thead, tbody);
					serverpeer.sendMessage(body.getPhysicalRequester(), tresult);
					
					//update its adjacent link pointing to the current holding peer
					treeNode.getLeftAdjacentNode().setPhysicalInfo(body.getPhysicalRequester());
				}
				else
				{
					if (PeerMath.compareNodePosition(failedNodeLevel, failedNodeNumber,
							parentLevel, parentNumber))
					{
						if (treeNode.getParentNode() != null)
						{
							body.setLogicalDestination(treeNode.getParentNode().getLogicalInfo());
							thead.setMsgType(MsgType.SP_LI_ADJACENT.getValue());
							tresult = new Message(thead, body);
							serverpeer.sendMessage(treeNode.getParentNode().getPhysicalInfo(), tresult);
						}
					}
					else
					{
						if (treeNode.getLeftAdjacentNode() != null)
						{
							body.setLogicalDestination(treeNode.getLeftAdjacentNode().getLogicalInfo());
							thead.setMsgType(MsgType.SP_LI_ADJACENT.getValue());
							tresult = new Message(thead, body);
							serverpeer.sendMessage(treeNode.getLeftAdjacentNode().getPhysicalInfo(), tresult);
						}
					}
				}
			}
			else
			{
				if ((treeNode.getRightAdjacentNode() != null) &&
				    (treeNode.getRightAdjacentNode().getLogicalInfo().equals(body.getLogicalFailedNode())))
				{
					AdjacentNodeInfo adjacentInfo =
						new AdjacentNodeInfo(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo());
					tbody = new SPLIAdjacentReplyBody(serverpeer.getPhysicalInfo(),
								treeNode.getLogicalInfo(), adjacentInfo, !direction,
								body.getLogicalRequester());
					thead.setMsgType(MsgType.SP_LI_ADJACENT_REPLY.getValue());
					tresult = new Message(thead, tbody);
					serverpeer.sendMessage(body.getPhysicalRequester(), tresult);
					
					//update its adjacent link pointing to the current holding peer
					treeNode.getRightAdjacentNode().setPhysicalInfo(body.getPhysicalRequester());
				}
				else
				{
					if (!PeerMath.compareNodePosition(failedNodeLevel, failedNodeNumber,
							parentLevel, parentNumber))
					{
						if (treeNode.getParentNode() != null)
						{
							body.setLogicalDestination(treeNode.getParentNode().getLogicalInfo());
							thead.setMsgType(MsgType.SP_LI_ADJACENT.getValue());
							tresult = new Message(thead, body);
							serverpeer.sendMessage(treeNode.getParentNode().getPhysicalInfo(), tresult);
						}
					}
					else
					{
						if (treeNode.getRightAdjacentNode() != null)
						{
							body.setLogicalDestination(treeNode.getRightAdjacentNode().getLogicalInfo());
							thead.setMsgType(MsgType.SP_LI_ADJACENT.getValue());
							tresult = new Message(thead, body);
							serverpeer.sendMessage(treeNode.getRightAdjacentNode().getPhysicalInfo(), tresult);
						}
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
		if (msg.getHead().getMsgType() == MsgType.SP_LI_ADJACENT.getValue())
			return true;
		return false;
	}

}