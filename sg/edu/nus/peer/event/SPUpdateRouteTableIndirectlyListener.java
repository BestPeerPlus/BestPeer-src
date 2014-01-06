/*
 * @(#) SPUpdateRouteTableIndirectlyListener.java 1.0 2006-2-22
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
 * Implement a listener for processing SP_UPDATE_ROUTING_TABLE_INDIRECTLY message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPUpdateRouteTableIndirectlyListener extends ActionAdapter
{

	public SPUpdateRouteTableIndirectlyListener(AbstractMainFrame gui) 
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
			SPUpdateRoutingTableIndirectlyBody body = (SPUpdateRoutingTableIndirectlyBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			int index = body.getIndex();
			
			boolean direction = body.getDirection();
			boolean special   = body.getSpecial();
			
			RoutingItemInfo senderNodeInfo = body.getInfoSender();
			RoutingItemInfo newNodeInfo    = body.getInfoChild();
			
			if (!direction)
			{
				if (!special)
				{
					treeNode.getLeftRoutingTable().setRoutingTableNode(senderNodeInfo, index);
				}
				else
				{
					ChildNodeInfo newLeftChild = body.getInfoSender().getLeftChild();
					
					RoutingItemInfo updateNodeInfo = (RoutingItemInfo)
					treeNode.getLeftRoutingTable().getRoutingTableNode(index);
					
					updateNodeInfo.setLeftChild(newLeftChild);
					treeNode.getLeftRoutingTable().setRoutingTableNode(updateNodeInfo, index);
				}
			}
			else
			{
				if (!special)
				{
					treeNode.getRightRoutingTable().setRoutingTableNode(senderNodeInfo, index);
				}
				else
				{
					ChildNodeInfo newLeftChild = body.getInfoSender().getLeftChild();
					
					RoutingItemInfo updateNodeInfo = (RoutingItemInfo)
					treeNode.getRightRoutingTable().getRoutingTableNode(index);
					
					updateNodeInfo.setLeftChild(newLeftChild);
					treeNode.getRightRoutingTable().setRoutingTableNode(updateNodeInfo, index);
				}
			}

			//forward to its children
			if (((newNodeInfo.getLogicalInfo().getNumber() % 2) == 0) && (treeNode.getRightChild() != null))
			{
				tbody = new SPUpdateRoutingTableDirectlyBody(serverpeer.getPhysicalInfo(),
															treeNode.getLogicalInfo(),
															newNodeInfo, index + 1, direction,
															treeNode.getRightChild().getLogicalInfo());

				thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_DIRECTLY.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(treeNode.getRightChild().getPhysicalInfo(), result);
			}
			
			if (((newNodeInfo.getLogicalInfo().getNumber() % 2) == 1) && (treeNode.getLeftChild() != null))
			{
				tbody = new SPUpdateRoutingTableDirectlyBody(serverpeer.getPhysicalInfo(),
															treeNode.getLogicalInfo(),
															newNodeInfo, index + 1, direction,
															treeNode.getLeftChild().getLogicalInfo());

				thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_DIRECTLY.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(treeNode.getLeftChild().getPhysicalInfo(), result);
			}
			
			//special cases for two adjacent nodes
			if ((!direction) && (index == 0) && ((newNodeInfo.getLogicalInfo().getNumber() % 2) == 0) 
					&& (treeNode.getLeftChild() != null))
			{
				tbody = new SPUpdateRoutingTableDirectlyBody(serverpeer.getPhysicalInfo(),
															treeNode.getLogicalInfo(),
															newNodeInfo, 0, direction,
															treeNode.getLeftChild().getLogicalInfo());

				thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_DIRECTLY.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(treeNode.getLeftChild().getPhysicalInfo(), result);
			}
			
			if ((direction) && (index == 0) && ((newNodeInfo.getLogicalInfo().getNumber() % 2) == 1) 
					&& (treeNode.getRightChild() != null))
			{
				tbody = new SPUpdateRoutingTableDirectlyBody(serverpeer.getPhysicalInfo(),
															treeNode.getLogicalInfo(),
															newNodeInfo, 0, direction,
															treeNode.getRightChild().getLogicalInfo());

				thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_DIRECTLY.getValue());
				result = new Message(thead, tbody);
				serverpeer.sendMessage(treeNode.getRightChild().getPhysicalInfo(), result);
			}
			
			//update GUI
			((ServerGUI) gui).updatePane(treeNode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Super peer indirectly updates routing table failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_UPDATE_ROUTING_TABLE_INDIRECTLY.getValue())
			return true;
		return false;
	}

}