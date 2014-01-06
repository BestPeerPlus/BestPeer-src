/*
 * @(#) AttachListener.java 1.0 2006-2-1
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
 * Implement a listener for processing ATTACH_REQUEST message.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class AttachListener extends ActionAdapter
{

	public AttachListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
    	Message result = null;
    	Head thead = null;
    	
    	PeerInfo peerInfo = null;
    	
    	boolean checkpoint1 = false;
    	boolean checkpoint2 = false;
    	
    	PeerMaintainer maintainer = PeerMaintainer.getInstance();
		try
		{
			UserBody ub = (UserBody) msg.getBody();
			String user = ub.getUserID();
			String ip   = ub.getIP();
			int port	= ub.getPort();
			String type = ub.getPeerType();

			peerInfo = new PeerInfo(user, ip, port, type);
			maintainer.put(peerInfo);
			
			checkpoint1 = true;
			
			/* update table component */
			//Pane pane = ((ServerGUI) gui).getPane();
			//pane.firePeerTableRowInserted(pane.getPeerTableRowCount(), peerInfo.toObjectArray());
			
			checkpoint2 = true;
			
			/* response message */
			thead = new Head();
			thead.setMsgType(MsgType.ATTACH_SUCCESS.getValue());
			
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			Session session   = Session.getInstance();
			PhysicalInfo info = serverpeer.getPhysicalInfo();
			Body tbody = new UserBody(session.getUserID(), "", "", info.getIP(),
									  info.getPort(), serverpeer.getPeerType());
			result = new Message(thead, tbody);
		}
		catch (Exception e)
		{
			if (checkpoint1)
			{
				/* remove the peer from online peer manager */
				maintainer.remove(peerInfo);
				if (checkpoint2)
				{
					//Pane pane = ((ServerGUI) gui).getPane();
					//pane.firePeerTableRowRemoved(peerInfo);
				}
			}

			/* create reply message */
			thead = new Head();
			thead.setMsgType(MsgType.ATTACH_FAILURE.getValue());
			Body tbody = new ConfirmBody();
			result = new Message(thead, tbody);
		}
		
		/* write result to the request peer */
		result.serialize(oos);
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.ATTACH_REQUEST.getValue())
			return true;
		return false;
	}

}