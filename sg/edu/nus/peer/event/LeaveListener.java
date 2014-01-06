/*
 * @(#) LeaveListener.java 1.0 2006-2-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.gui.server.ServerGUI;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implement a listener when a peer leaves the network 
 * or detaches from a super peer.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-4
 */

public class LeaveListener extends ActionAdapter
{

	public LeaveListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		try
		{
			UserBody ub = (UserBody) msg.getBody();
			String user = ub.getUserID();
			String ip   = ub.getIP();
			int port	= ub.getPort();
			String type = ub.getPeerType();
			
			PeerInfo peerInfo = new PeerInfo(user, ip, port, type);

			/* judge whether exist online super peers */
			PeerMaintainer maintainer = PeerMaintainer.getInstance();
			
			/*
			 * if have online super peers
			 */
			if (maintainer.hasServer()) 
			{
				maintainer.remove(peerInfo);
				sg.edu.nus.gui.bootstrap.Pane pane = ((BootstrapGUI) gui).getPane();
				pane.firePeerTableRowRemoved(peerInfo);
			}
			else if (maintainer.hasClient())
			{
				maintainer.remove(peerInfo);
				//sg.edu.nus.gui.server.Pane pane = ((ServerGUI) gui).getPane();
				//pane.firePeerTableRowRemoved(peerInfo);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Peer departure operation failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.I_WILL_LEAVE.getValue())
			return true;
		return false;
	}
	
}