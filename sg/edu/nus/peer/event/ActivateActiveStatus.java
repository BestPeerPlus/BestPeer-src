/*
 * @(#) ActivateActiveStatus.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.util.*;

import sg.edu.nus.peer.info.*;

/**
 * Used for activating the status of a super peer after a period of time. 
 * Usually, a peer is activated after receiving enough routing table replies.
 * However, in some cases, it may not received enough replies. As a result,
 * it cannot be activated. 
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class ActivateActiveStatus 
{

	protected TreeNode treeNode;
	protected Timer timer;

	public ActivateActiveStatus(TreeNode treeNode, int seconds) 
	{
		this.treeNode = treeNode;
		this.timer = new Timer();
		this.timer.schedule(new ReminderActiveStatus(), seconds * 1000);
	}

	class ReminderActiveStatus extends TimerTask
	{
		public void run()
		{
			//System.out.println("Activate status of node " + treeNode.getLogicalInfo().toString());
			treeNode.setNumOfExpectedRTReply(0);
			treeNode.setStatus(TreeNode.ACTIVE);
			timer.cancel();
		}
	}
}
