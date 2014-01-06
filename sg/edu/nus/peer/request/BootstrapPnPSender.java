/*
 * @(#) BootstrapPnPSender.java 1.0 2006-10-9
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.request;

import java.io.IOException;
import java.net.*;
import java.util.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.bootstrap.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;

/**
 * The <code>BootstrapPnPSender</code> is responsible for sending UDP packets with the type of 
 * <code>{@link sg.edu.nus.protocol.MsgType#PING}</code> out to remote nodes.
 * 
 * <p>The method <code>{@link #sendPacket()}</code> must be implemented to send UDP
 * packages to remote peers. To obtain the IP address list of the remote peers to be
 * contacted, just invoke the interface of <code>{@link sg.edu.nus.peer.management.PeerMaintainer#getServers()}</code>.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-10-9
 * 
 * @see BootstrapPnPServer
 * @see UDPSender
 */

public class BootstrapPnPSender extends UDPSender implements PnPListener
{

	// private member
	private BootstrapGUI bootstrapGUI;
	private PeerMaintainer maintainer = PeerMaintainer.getInstance();
	private List<PeerInfo> peerList;
	
	/**
	 * Construct the UDP sender with specified parameters.
	 * 
	 * @param gui the reference of the <code>AbstractMainFrame</code>
	 * @param port the port to be used for initializing <code>DatagramSocket</code>
	 * @throws SocketException if cannot initialize <code>DatagramSocket</code> on the specified port
	 */
	public BootstrapPnPSender(AbstractMainFrame gui, int port) throws SocketException 
	{
		super(gui, port);
		bootstrapGUI = (BootstrapGUI) gui;
		peerList = Collections.synchronizedList(new ArrayList<PeerInfo>());
	}
	
	/**
	 * Send UDP packets to all online server peers.
	 * 
	 * <p>Within system-defined timeout, if bootstrapper does not 
	 * receive any reply from a server peer, then bootstrapper regards 
	 * the server peer as failure and removes its information
	 * from the PeerMaintainer and update GUI components.
	 */
	protected void sendPacket() 
	{
		if (debug)
			System.out.println("######################### new round of heartbeat ########################");
		this.removeFailureNodes();
		this.broadcast();
	}
	
	/**
	 * Remove all nodes who are regarded as failure since
	 * they do not respond within system-defined timeout.
	 */
	private void removeFailureNodes()
	{
		synchronized (peerList)
		{
			if (peerList == null || peerList.isEmpty())
				return;
			
			PeerInfo info = null;
			int size = peerList.size();
			for (int i = 0; i < size; i++)
			{
				info = peerList.get(i);
				if (debug)
					System.out.println("case 1: " + info.toString() + " is failure because no pong is received within timeout");
				this.maintainer.remove(info);
				this.bootstrapGUI.getPane().firePeerTableRowRemoved(info);
			}
			peerList.clear();
		}
	}
	
	/**
	 * Broadcast UDP packets to all server peers 
	 * that are assumed online currently.
	 */
	private void broadcast()
	{
		synchronized (peerList)
		{
			if (maintainer.hasServer())
			{
				PeerInfo info = null;
				DatagramPacket ping = this.ping();	// init ping packet
				try
				{
					PeerInfo[] list = maintainer.getServers();
					int size = list.length;
					for (int i = 0; i < size; i++)
					{
						info = list[i];
						peerList.add(info);
						ping.setAddress(InetAddress.getByName(info.getInetAddress()));
						ping.setPort(info.getPort());
						for (int j = 0; j < TRY_TIME; j++)
						{
							this.datagramSocket.send(ping);
							if (debug)
								System.out.println("case 2: send ping out to " + ping.getAddress().getHostAddress() + " : " + ping.getPort());
						}
					}
				}
				catch (UnknownHostException e) 
				{	/* if error happens, ignore it */
				}
				catch (IOException e)
				{	/* if error happens, ignore it */
				}
			}
		}
	}
	
	public void nodeAlive(final PeerInfo info) 
	{
		if (debug)
			System.out.println("case 3: receive pong from " + info.toString());
		this.updatePeerList(info);
	}

	public void nodeFailure(final PeerInfo info) 
	{
		if (debug)
			System.out.println("case 4: receive troubleshoot from " + info.toString());

		PeerInfo cmp = maintainer.getServer(info.getKey());
		if ((cmp != null) && (cmp.equalsIgnoreType(info)))
		{
			this.maintainer.remove(cmp);
			this.updatePeerList(cmp);
			this.bootstrapGUI.getPane().firePeerTableRowRemoved(cmp);
		}
	}
	
	private void updatePeerList(final PeerInfo info)
	{
		synchronized (peerList)
		{
			PeerInfo cmp = null;
			int size = peerList.size();
			for (int i = (size - 1); i >=0; i--)
			{
				cmp = peerList.get(i);
				if (cmp.equalsIgnoreType(info))
					peerList.remove(i);
			}
		}
	}
	
}