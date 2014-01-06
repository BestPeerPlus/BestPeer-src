/*
 * @(#) BootstrapRequestManager.java 1.0 2006-9-18
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.request;

import java.net.*;

import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.util.*;

/**
 * Implements all request operations of bootstrap server.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-9-18
 */

public class BootstrapRequestManager
{
	
	// private members
	private PeerMaintainer maintainer = PeerMaintainer.getInstance();
	private DatagramSocket socket;
	
	/**
	 * When the bootstrap server exits the system for any reason,
	 * it will broadcast a TROUBLESHOOT message to all online 
	 * super peers for this event.
	 */
	public void troubleshoot()
	{
		try	// init datagram socket 
		{
			socket = new DatagramSocket();
		} 
		catch (SocketException e) 
		{
			return;
		}
		
		// create packet
		int len = 4;
		byte[] buf = new byte[len];
		int val = MsgType.TROUBLESHOOT.getValue();
		Tools.intToByteArray(val, buf, 0);
		if (maintainer.hasServer())
		{
			PeerInfo[] list = maintainer.getServers();
			PeerInfo info;
			int size = list.length;
			for (int i = 0; i < size; i++)
			{
				try 
				{
					info = list[i];
					socket.send(new DatagramPacket(buf, 0, len, InetAddress.getByName(info.getInetAddress()), info.getPort()));
				} 
				catch (Exception e) 
				{ /* ignore */ }
			}
		}
	}
	
}