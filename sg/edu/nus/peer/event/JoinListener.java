/*
 * @(#) JoinListener.java 1.0 2006-2-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.gui.bootstrap.Pane;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implements a listener for processing JOIN_SUCCESS or JOIN_FAILURE messages.
 * Both events occurs when a peer selects a super peer to join either the
 * super network or a super peer.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-4
 */

public class JoinListener extends ActionAdapter
{

	public JoinListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		PeerMaintainer maintainer = PeerMaintainer.getInstance();
		if (msg.getHead().getMsgType() == MsgType.JOIN_SUCCESS.getValue())
		{
			try
			{
				UserBody ub = (UserBody) msg.getBody();
				String user = ub.getUserID();
				String ip   = ub.getIP();
				int port	= ub.getPort();
				String type = ub.getPeerType();

				PeerInfo peerInfo = new PeerInfo(user, ip, port, type);
				maintainer.put(peerInfo);
				
				/* update table component */
				Pane pane = ((BootstrapGUI) gui).getPane();
				pane.firePeerTableRowInserted(pane.getPeerTableRowCount(), peerInfo.toObjectArray());
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new EventHandleException("Peer join operation failure", e);
			}
		}
		else if (msg.getHead().getMsgType() == MsgType.JOIN_FAILURE.getValue())
		{
			try
			{
				UserBody ub = (UserBody) msg.getBody();
				String user = ub.getUserID();
				String ip   = ub.getIP();
				int port	= ub.getPort();
				String type = ub.getPeerType();
				
				PeerInfo peerInfo = new PeerInfo(user, ip, port, type);
				/* if have online super peers */
				if (maintainer.hasServer()) 
					maintainer.remove(peerInfo);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new EventHandleException("Peer join operation failure", e);
			}
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.JOIN_SUCCESS.getValue())
		{
			return true;
		}
		else if (msg.getHead().getMsgType() == MsgType.JOIN_FAILURE.getValue())
		{
			return true;
		}
		return false;
	}
	
}