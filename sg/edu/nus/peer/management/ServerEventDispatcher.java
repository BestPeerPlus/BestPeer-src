/*
 * @(#) ServerEventDispatcher.java 1.0 2006-2-3
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.management;

//import sg.edu.nus.accesscontrol.normalpeer.CreateUserFromBoostrapListener;
import sg.edu.nus.gui.*;
import sg.edu.nus.peer.event.*;

/**
 * Implement a concrete event manager used for monitoring
 * all incoming socket connections related to the bootstrap server.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-3
 */

public class ServerEventDispatcher extends AbstractEventDispatcher
{

	public ServerEventDispatcher(AbstractMainFrame gui) 
	{
		super(gui);
	}

	@Override
	public void registerActionListeners() 
	{
		/* used for monitoring client peer's events */
		this.addActionListener(new AttachListener(gui));
		this.addActionListener(new LeaveListener(gui));
		this.addActionListener(new ForceOutListener(gui));
		//this.addActionListener(new SchemaUpdateListener(gui));
		
		/* used for monitoring events from knowledge bank */
		//this.addActionListener(new KBIndexPublishListener(gui));
		//this.addActionListener(new KBIndexSearchListener(gui));
		//this.addActionListener(new KBIndexQueryListener(gui));
		//this.addActionListener(new KBResultListener(gui));
		
		/* used for monitoring super peer's events */
		
		/* join and insert data objects */
		/* 
		 * SPInsertListner and SPInsertBundleListener are replaced 
		 * by SPIndexInsertListener
		 * $Id: ServerEventDispatcher.java,v 1.10 2008/10/09 09:55:18 wusai Exp $
		this.addActionListener(new SPInsertListener(gui));
		this.addActionListener(new SPInsertBundleListener(gui));
		 */
		//this.addActionListener(new SPIndexInsertBundleListener(gui));
		//this.addActionListener(new SPIndexInsertListener(gui));
		/* end of modification
		 * $Id: ServerEventDispatcher.java,v 1.10 2008/10/09 09:55:18 wusai Exp $
		 */
		
		this.addActionListener(new SPJoinAcceptListener(gui));
		this.addActionListener(new SPJoinListener(gui));
		this.addActionListener(new SPJoinForceListener(gui));
		this.addActionListener(new SPJoinForceForwardListener(gui));
		this.addActionListener(new SPJoinSplitDataListener(gui));
		
		/* load balance */
		this.addActionListener(new SPNotifyImbalanceListener(gui));		
		this.addActionListener(new SPLBFindLightlyNodeListener(gui));
		this.addActionListener(new SPLBGetLoadInfoListener(gui));
		this.addActionListener(new SPLBGetLoadInfoReplyListener(gui));
		this.addActionListener(new SPLBGetLoadInfoResendListener(gui));
		this.addActionListener(new SPLBNoRotationNodeListener(gui));
		this.addActionListener(new SPLBRotateUpdateAdjacentListener(gui));
		this.addActionListener(new SPLBRotateUpdateAdjacentReplyListener(gui));
		this.addActionListener(new SPLBRotateUpdateChildListener(gui));
		this.addActionListener(new SPLBRotateUpdateChildReplyListener(gui));
		this.addActionListener(new SPLBRotateUpdateParentListener(gui));
		this.addActionListener(new SPLBRotateUpdateParentReplyListener(gui));
		this.addActionListener(new SPLBRotateUpdateRTListener(gui));
		this.addActionListener(new SPLBRotateUpdateRTReplyListener(gui));
		this.addActionListener(new SPLBSplitDataListener(gui));
		this.addActionListener(new SPLBSplitDataResendListener(gui));
		this.addActionListener(new SPLBStablePositionListener(gui));
		this.addActionListener(new SPLBRotationPullListener(gui));
		
		/* leave the network */
		this.addActionListener(new SPLeaveListener(gui));
		this.addActionListener(new SPLeaveUrgentListener(gui));
		this.addActionListener(new SPLeaveNotifyListener(gui));
		this.addActionListener(new SPLeaveFindReplaceListener(gui));
		this.addActionListener(new SPLeaveFindReplaceReplyListener(gui));
		this.addActionListener(new SPLeaveReplacementListener(gui));
		this.addActionListener(new SPPassClientListener(gui));
		
		/* update links with other super peers */
		this.addActionListener(new SPUpdateAdjacentLinkListener(gui));
		this.addActionListener(new SPUpdateMinMaxValueListener(gui));
		this.addActionListener(new SPUpdateRouteTableListener(gui));
		this.addActionListener(new SPUpdateRouteTableDirectlyListener(gui));
		this.addActionListener(new SPUpdateRouteTableIndirectlyListener(gui));
		this.addActionListener(new SPUpdateRouteTableReplyListener(gui));
		
		/* delete and search data objects*/
		/*
		 * SPDeleteListener and SPDeleteBundleListener
		 * are replaced by SPIndexDeleteListener.
		 * $Id 2007-2-2 14:23 author: xulinhao$
		this.addActionListener(new SPDeleteListener(gui));
		this.addActionListener(new SPDeleteBundleListener(gui));
		 */
		//this.addActionListener(new SPIndexDeleteBundleListener(gui));
		//this.addActionListener(new SPIndexDeleteListener(gui));
		//this.addActionListener(new SPIndexUpdateBundleListener(gui));
		//this.addActionListener(new SPIndexUpdateListener(gui));
		/*
		 * end of modification
		 * $Id 2007-2-2 14:23 author: xulinhao$
		 */
		
		/*
		 * SPSearchExactListener, SPSearchExactBundleListener,
		 * SPSearchExactResultListener, SPSearchRangeListener,
		 * and SPSearchRangeResultListener are replaced by 
		 * SPIndexSearchListener.
		 * $Id 2007-2-1 11:10 author: xulinhao$
		this.addActionListener(new SPSearchExactListener(gui));
		this.addActionListener(new SPSearchExactBundleListener(gui));
		this.addActionListener(new SPSearchExactResultListener(gui));
		
		this.addActionListener(new SPSearchRangeListener(gui));
		this.addActionListener(new SPSearchRangeResultListener(gui));
		 */
		//this.addActionListener(new SPIndexSearchListener(gui));
		/* end of modification
		 * $Id 2007-2-1 11:10 author: xulinhao$
		 */
		
		/* failure recovery*/
		this.addActionListener(new SPLIAdjacentListener(gui));
		this.addActionListener(new SPLIAdjacentRootListener(gui));
		this.addActionListener(new SPLIAdjacentReplyListener(gui));
		this.addActionListener(new SPLIAdjacentRootReplyListener(gui));
		this.addActionListener(new SPLIRoutingTableListener(gui));
		this.addActionListener(new SPLIRoutingTableReplyListener(gui));
		this.addActionListener(new SPLIChildReplyListener(gui));
		this.addActionListener(new SPNotifyFailureListener(gui));
		this.addActionListener(new SPLIUpdateParentListener(gui));
		
		
	}	
}