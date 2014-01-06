/*
 * @(#) SPLBSplitDataListener.java 1.0 2006-2-22
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
 * Implement a listener for processing SP_LB_SPLIT_DATA message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLBSplitDataListener extends ActionAdapter
{

	public SPLBSplitDataListener(AbstractMainFrame gui) 
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
			SPLBSplitDataBody body = (SPLBSplitDataBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			//1. insert data
			for (int i = 0; i < body.getData().size(); i++)
			{
				treeNode.getContent().insertData((IndexValue)body.getData().get(i), 0);
			}

			//2. update range of values
			if (treeNode.getContent().getMaxValue().compareTo(body.getMinValue())==0)
			{
				treeNode.getContent().setMaxValue(body.getMaxValue());
			}
			else if (treeNode.getContent().getMinValue().compareTo(body.getMaxValue())==0)
			{
				treeNode.getContent().setMinValue(body.getMinValue());
			}
			else
			{
				if (body.getMinValue().compareTo(treeNode.getContent().getMinValue()) < 0)
				{
					treeNode.getContent().setMinValue(body.getMinValue());
				}
				if (body.getMaxValue().compareTo(treeNode.getContent().getMaxValue()) > 0)
				{
					treeNode.getContent().setMaxValue(body.getMaxValue());
				}
			}

			//3. update routing table
			SPGeneralAction.updateRangeValues(serverpeer, treeNode);

			//4. check load balancing
			if ((treeNode.getContent().isOverloaded()) && (!treeNode.isProcessLoadBalance()))
			{
				if (!body.getDirection())
				{
					SPGeneralAction.doLoadBalance(serverpeer, treeNode, false, true);
				}
				else
				{
					SPGeneralAction.doLoadBalance(serverpeer, treeNode, true, false);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_LB_SPLIT_DATA operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LB_SPLIT_DATA.getValue())
			return true;
		return false;
	}

}