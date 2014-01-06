/*
 * @(#) SPJoinForceListener.java 1.0 2006-2-22
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
import sg.edu.nus.util.*;

/**
 * Implement a listener for processing SP_JOIN_FORCED message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPJoinForceListener extends ActionAdapter
{

	public SPJoinForceListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		super.actionPerformed(oos, msg);
		
    	Message result = null;
    	Head thead = new Head();
    	Body tbody = null;
    	
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			
			/* get the message body */
			SPJoinForcedBody body = (SPJoinForcedBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			boolean direction = body.getDirection();
			treeNode.processLoadBalance(false);

			if ((treeNode.getLeftChild() == null) || (treeNode.getRightChild() == null))
			{
				ContentInfo content;
				ChildNodeInfo childNode;
				int childLevel;
				int childNumber;
				int numOfExpectedRTReply;
				if (treeNode.getLeftChild() == null)
				{
					childLevel = treeNode.getLogicalInfo().getLevel() + 1;
					childNumber = treeNode.getLogicalInfo().getNumber() * 2 - 1;
					childNode = new ChildNodeInfo(body.getPhysicalSender(), new LogicalInfo(childLevel, childNumber));
					treeNode.setLeftChild(childNode);
					numOfExpectedRTReply = PeerMath.getNumberOfExpectedRTReply(true, 
																treeNode.getRightChild() != null,
																treeNode.getLeftRoutingTable(),
																treeNode.getLeftRoutingTable());
					
					content = SPGeneralAction.splitData(treeNode, false, serverpeer.getOrder());
				}
				else
				{
					childLevel = treeNode.getLogicalInfo().getLevel() + 1;
					childNumber = treeNode.getLogicalInfo().getNumber() * 2;
					childNode = new ChildNodeInfo(body.getPhysicalSender(), new LogicalInfo(childLevel, childNumber));
					treeNode.setRightChild(childNode);
					numOfExpectedRTReply = PeerMath.getNumberOfExpectedRTReply(false, false,
																treeNode.getLeftRoutingTable(),
																treeNode.getLeftRoutingTable());
					
					content = SPGeneralAction.splitData(treeNode, true, serverpeer.getOrder());
				}
				RoutingItemInfo nodeInfo = SPGeneralAction.doAccept(serverpeer, treeNode,
																		body.getPhysicalSender(),
																		new LogicalInfo(childLevel, childNumber),
																		content, numOfExpectedRTReply, false, false);			
				
				SPGeneralAction.updateRoutingTable(serverpeer, treeNode, nodeInfo);
			}
			else
			{
				ContentInfo content;
				if (!direction)
				{
					content = SPGeneralAction.splitData(treeNode, false, serverpeer.getOrder());
					tbody = new SPJoinForcedForwardBody(serverpeer.getPhysicalInfo(),
														treeNode.getLogicalInfo(),
														body.getPhysicalSender(),
														content, direction,
														treeNode.getLeftAdjacentNode().getLogicalInfo());

					thead.setMsgType(MsgType.SP_JOIN_FORCED_FORWARD.getValue());
					result = new Message(thead, tbody);
					serverpeer.sendMessage(treeNode.getLeftAdjacentNode().getPhysicalInfo(), result);
				}
				else
				{
					content = SPGeneralAction.splitData(treeNode, true, serverpeer.getOrder());
					tbody = new SPJoinForcedForwardBody(serverpeer.getPhysicalInfo(),
														treeNode.getLogicalInfo(),
														body.getPhysicalSender(),
														content, direction,
														treeNode.getRightAdjacentNode().getLogicalInfo());

					thead.setMsgType(MsgType.SP_JOIN_FORCED_FORWARD.getValue());
					result = new Message(thead, tbody);
					serverpeer.sendMessage(treeNode.getRightAdjacentNode().getPhysicalInfo(), result);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_JOIN_FORCED operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_JOIN_FORCED.getValue())
			return true;
		return false;
	}

}