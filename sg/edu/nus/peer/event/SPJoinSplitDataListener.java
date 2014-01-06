/*
 * @(#) SPJoinSplitDataListener.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implement a listener for processing SP_JOIN_SPLIT_DATA message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPJoinSplitDataListener extends ActionAdapter
{

	public SPJoinSplitDataListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
    	Message result = null;
    	Head thead = new Head();
    	Body tbody = null;
    	
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			
			/* get the message body */
			SPJoinSplitDataBody body = (SPJoinSplitDataBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			PhysicalInfo physicalSender = body.getPhysicalSender();
			LogicalInfo logicalSender = body.getLogicalSender();
			
			AdjacentNodeInfo newNodeLeftAdjacent = 
				new AdjacentNodeInfo(serverpeer.getPhysicalInfo(), treeNode.getLogicalInfo());
			AdjacentNodeInfo newNodeRightAdjacent = 
				new AdjacentNodeInfo(physicalSender, logicalSender);
	    	RoutingItemInfo newNodeInfo;
	    	int i;

	    	//split its content
	    	ContentInfo content = SPGeneralAction.splitData(treeNode, true, serverpeer.getOrder());

	    	//notify the new node about its position
	    	tbody = new SPJoinAcceptBody(body.getPhysicalSender(), body.getLogicalSender(),
	    			body.getLogicalNewNode(), newNodeLeftAdjacent, 
	    			newNodeRightAdjacent, content, body.getNumberOfExpectedRTReply(),
	    			false, false);

			thead.setMsgType(MsgType.SP_JOIN_ACCEPT.getValue());
			result = new Message(thead, tbody);
	    	serverpeer.sendMessage(body.getPhysicalNewNode(), result);

	    	//update adjacent link
	    	treeNode.setRightAdjacentNode(new AdjacentNodeInfo(body.getPhysicalNewNode(), 
	    			body.getLogicalNewNode()));

	    	//notify its neighbor nodes
	    	SPGeneralAction.updateRangeValues(serverpeer, treeNode);

	    	//notify the new node's neighbor nodes
	    	RoutingItemInfo parentNodeInfo = body.getParentNodeInfo();
	    	newNodeInfo = new RoutingItemInfo(body.getPhysicalNewNode(), body.getLogicalNewNode(),
	    								null, null, content.getMinValue(), content.getMaxValue());

			RoutingTableInfo leftRT = body.getLeftRT();
	    	for (i = 0; i < leftRT.getTableSize(); i++) 
	    	{
	    		RoutingItemInfo neighborNode = leftRT.getRoutingTableNode(i);
	    		
	    		if (neighborNode != null)
	    		{
	    			tbody = new SPUpdateRoutingTableIndirectlyBody(neighborNode.getPhysicalInfo(),
	    					neighborNode.getLogicalInfo(),
	    					parentNodeInfo, newNodeInfo, i, true, true,
	    					neighborNode.getLogicalInfo());

	    			thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_INDIRECTLY.getValue());
	    			result = new Message(thead, tbody);    			
	    			serverpeer.sendMessage(neighborNode.getPhysicalInfo(), result);
	    		}
	    	}

			RoutingTableInfo rightRT = body.getRightRT();
	    	for (i = 0; i < rightRT.getTableSize(); i++) 
	    	{
	    		RoutingItemInfo neighborNode = rightRT.getRoutingTableNode(i);
	    		if (neighborNode != null)
	    		{
	    			tbody = new SPUpdateRoutingTableIndirectlyBody(neighborNode.getPhysicalInfo(),
	    					neighborNode.getLogicalInfo(),
	    					parentNodeInfo, newNodeInfo, i, false, true,
	    					neighborNode.getLogicalInfo());

	    			thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_INDIRECTLY.getValue());
	    			result = new Message(thead, tbody);
	    			serverpeer.sendMessage(neighborNode.getPhysicalInfo(), result);
	    		}
	    	}
	    	
	    	// also need to update right adjacent node if it exists
	    	ChildNodeInfo sibling = body.getRightChildInfo();
	    	if (sibling != null)
	    	{
	    		tbody = new SPUpdateRoutingTableDirectlyBody(serverpeer.getPhysicalInfo(),
	    				parentNodeInfo.getLogicalInfo(), newNodeInfo, 0, false, sibling.getLogicalInfo());
	    		
	    		thead.setMsgType(MsgType.SP_UPDATE_ROUTING_TABLE_DIRECTLY.getValue());
	    		result = new Message(thead, tbody);
	    		serverpeer.sendMessage(sibling.getPhysicalInfo(), result);
	    	}
	    	
	    	// update GUI
	    	((ServerGUI) gui).updatePane(treeNode);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_JOIN_SPLIT_DATA operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_JOIN_SPLIT_DATA.getValue())
			return true;
		return false;
	}

}