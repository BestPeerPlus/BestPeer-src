/*
 * @(#) SPLeaveFindReplaceReplyListener.java 1.0 2006-2-22
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
 * Implement a listener for processing SP_LEAVE_FIND_REPLACEMENT_NODE_REPLY message.
 * 
 * @author Vu Quang Hieu
 * @author (Modified by) Xu Linhao
 * @version 1.0 2006-2-22
 */

public class SPLeaveFindReplaceReplyListener extends ActionAdapter
{

	public SPLeaveFindReplaceReplyListener(AbstractMainFrame gui) 
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
			SPLeaveFindReplacementNodeReplyBody body = (SPLeaveFindReplacementNodeReplyBody) msg.getBody();
			
			TreeNode treeNode = serverpeer.getTreeNode(body.getLogicalDestination());
			if (treeNode == null)
			{
				System.out.println("Tree node is null, do not process the message");
				return;				
			}
			
			ActivateStablePosition position = serverpeer.getActivateStablePosition();
			if (position != null)
			{
				if (treeNode.getLogicalInfo().equals(position.getTreeNode().getLogicalInfo()))
				{
					serverpeer.stopActivateStablePosition();
				}
			}

			treeNode.setRole(0);
			tbody =	new SPLeaveReplacementBody(serverpeer.getPhysicalInfo(), treeNode.getLogicalInfo(), 
											   treeNode, treeNode.getContent());

			thead.setMsgType(MsgType.SP_LEAVE_REPLACEMENT.getValue());
			result = new Message(thead, tbody);
			serverpeer.sendMessage(body.getPhysicalSender(), result);
			
			treeNode.clearCoOwnerList();
			treeNode.addCoOwnerList(body.getPhysicalSender());
			treeNode.deleteAllWork();
			
			/* FIXME: NOW SIMPLY SET MENUBAR DISABLE */
			//((ServerGUI) gui).setMenuEnable(false);
			//((ServerGUI) gui).setPaneEnable(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("SP_LEAVE_FIND_REPLACEMENT_NODE_REPLY operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_LEAVE_FIND_REPLACEMENT_NODE_REPLY.getValue())
			return true;
		return false;
	}

}