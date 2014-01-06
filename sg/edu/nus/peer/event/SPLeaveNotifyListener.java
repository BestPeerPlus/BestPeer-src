/*
 * @(#) SPLeaveNotifyListener.java 1.0 2006-3-4
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
 * Implement a listener for processing SP_LEAVE_NOTIFY message.
 * 
 * @author Vu Quang Hieu 
 * @version 1.0 2006-3-4
 */

public class SPLeaveNotifyListener extends ActionAdapter
{

	public SPLeaveNotifyListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		try
	 	{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
						
			/* get the message body */
			SPLeaveNotifyBody body = (SPLeaveNotifyBody) msg.getBody();
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			PhysicalInfo physicalReplacer = body.getPhysicalReplacer();
			RoutingItemInfo nodeInfo;
			int index = body.getIndex();
			
			switch (body.getPosition())
			{
				case 0: //departure is the parent node
					treeNode.getParentNode().setPhysicalInfo(physicalReplacer);
					break;
				case 1: //departure is the left adjacent node
					if (treeNode.getLeftAdjacentNode() != null)
						treeNode.getLeftAdjacentNode().setPhysicalInfo(physicalReplacer);
					break;
				case 2: //departure is the right adjacent node
					if (treeNode.getRightAdjacentNode() != null)
						treeNode.getRightAdjacentNode().setPhysicalInfo(physicalReplacer);
					break;
				case 3:	//departure is the left neighbor node			
					nodeInfo = treeNode.getLeftRoutingTable().getRoutingTableNode(index);
				    if (nodeInfo != null)
				    	nodeInfo.setPhysicalInfo(physicalReplacer);
					break;
				case 4: //departure is the right neighbor node
					nodeInfo = treeNode.getRightRoutingTable().getRoutingTableNode(index);
					if (nodeInfo != null)
						nodeInfo.setPhysicalInfo(physicalReplacer);
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Super peer notifies its neighbors failure when it leaves network", e);
		}
	}	
	
	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LEAVE_NOTIFY.getValue())
			return true;
		return false;
	}

}