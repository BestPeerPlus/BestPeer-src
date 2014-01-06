/*
 * @(#) CheckImbalance.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.request;

import java.util.*;

import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.logging.LogEventType;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;
import sg.edu.nus.util.*;

/**
 * Used for checking the status of the workload at peers.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class CheckImbalance 
{

	// private members
	private ServerGUI servergui;
	private ServerPeer serverpeer;
	private Timer timer;
		
	public CheckImbalance(ServerPeer serverpeer, int seconds) 
	{
		this.serverpeer = serverpeer;
		this.servergui  = (ServerGUI) serverpeer.getMainFrame();
		this.timer = new Timer();
		this.timer.schedule(new ReminderCheckImbalance(), 0, seconds * 1000);
	}

	class ReminderCheckImbalance extends TimerTask
	{
		public void run()
		{
			Head head = new Head();
			Body body = null;
			try
			{
				//System.out.println("Checking imbalance ... ");
				if (serverpeer.getListSize() == 1)
				{
					TreeNode treeNode = serverpeer.getListItem(0);
					if (((treeNode.getLeftChild() != null) || (treeNode.getRightChild() != null))
						&& (!treeNode.isNotifyImbalance()))
					{
						LogicalInfo missingNode;
						int missNodeLevel = treeNode.getLogicalInfo().getLevel();
						int missNodeNumber;
						RoutingTableInfo leftRoutingTable = treeNode.getLeftRoutingTable();
						for (int i = 0; i < leftRoutingTable.getTableSize(); i++)
						{
							if (leftRoutingTable.getRoutingTableNode(i) == null)
							{
								missNodeNumber = treeNode.getLogicalInfo().getNumber() - PeerMath.pow(2, i);
								missingNode = new LogicalInfo(missNodeLevel, missNodeNumber);
								treeNode.notifyImbalance(true);
								treeNode.setMissingNode(missingNode);
								body = new SPNotifyImbalanceBody(serverpeer.getPhysicalInfo(),
										treeNode.getLogicalInfo(), missingNode, true,
										treeNode.getParentNode().getLogicalInfo());
	
								head.setMsgType(MsgType.SP_NOTIFY_IMBALANCE.getValue());
								Message message = new Message(head, body);							
								serverpeer.sendMessage(treeNode.getParentNode().getPhysicalInfo(), message);

								// add log event
								servergui.log(LogEventType.INFORMATION.getValue(), MsgType.description(head.getMsgType()) + " request is sent out...",	Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name"));
								LogManager.getLogger(serverpeer).info(MsgType.description(head.getMsgType()) + " request is sent out...");
								return;
							}
						}
						
						RoutingTableInfo rightRoutingTable = treeNode.getRightRoutingTable();
						for (int i = 0; i < rightRoutingTable.getTableSize(); i++)
						{
							if (rightRoutingTable.getRoutingTableNode(i) == null)
							{
								missNodeNumber = treeNode.getLogicalInfo().getNumber() + PeerMath.pow(2, i);
								missingNode = new LogicalInfo(missNodeLevel, missNodeNumber);
								treeNode.notifyImbalance(true);
								treeNode.setMissingNode(missingNode);
								
								body = new SPNotifyImbalanceBody(serverpeer.getPhysicalInfo(), 
										treeNode.getLogicalInfo(), missingNode, false,
										treeNode.getParentNode().getLogicalInfo());
	
								head.setMsgType(MsgType.SP_NOTIFY_IMBALANCE.getValue());
								Message message = new Message(head, body);
								serverpeer.sendMessage(treeNode.getParentNode().getPhysicalInfo(), message);

								// add log event
								servergui.log(LogEventType.INFORMATION.getValue(), MsgType.description(head.getMsgType()) + " request is sent out...",	Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name"));
								LogManager.getLogger(serverpeer).info(MsgType.description(head.getMsgType()) + " request is sent out...");
								return;
							}
						}	
					}
				}
			}
			catch (Exception e)
			{
				servergui.log(LogEventType.ERROR.getValue(), "Exception happens because:\r\n" + Tools.getException(e),	Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name"));
				LogManager.getLogger(serverpeer).error("Exception happens in ReminderCheckImbalance", e);
			}
		}
	}
}
