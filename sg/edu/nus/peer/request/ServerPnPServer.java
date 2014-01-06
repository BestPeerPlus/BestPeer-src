/*
 * @(#) ServerPnPServer.java 1.0 2006-10-16
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.request;

import java.net.SocketException;
import java.util.*;

import sg.edu.nus.gui.AbstractMainFrame;

/**
 * The <code>ServerPnPServer</code> is used for sending and receiving UDP messages
 * between peers, which is composed of a <code>ServerPnPSender</code> and a 
 * <code>ServerPnPReceiver</code>. The <code>ServerPnPSender</code> is responsible for
 * sending <code>{@link sg.edu.nus.protocol.MsgType#PING}</code> messages 
 * to, bootstrapper, server peers in its routing table and its client peers, 
 * while the <code>ServerPnPReceiver</code> for receiving 
 * <code>{@link sg.edu.nus.protocol.MsgType#PONG}</code> messages.
 * 
 * <p>To start the <code>ServerPnPServer</code>, just implement the following codes
 * in the <code>{@link #run()}</code> method.
 * <pre>
 * public void run()
 * {
 * 	sender = new ServerPnPSender();
 * 	scheduler = new Timer("Ping Sender of Server Peer");
 * 	scheduler.schedule(sender, 10000); // 10 seconds
 * 
 * 	receiver = new ServerPnPReceiver(sender.getSocket(), 10);
 * 	Thread receiverThread = new Thread("Pong Receiver of Server Peer", receiver);
 * 	receiverThread.start();
 * }
 * </pre>
 * 
 * @author Xu Linhao
 * @version 1.0 2006-9-25
 * 
 * @see UDPServer
 * @see ServerPnPSender
 * @see ServerPnPReceiver
 */

public class ServerPnPServer extends UDPServer
{

	// private members
	private Thread receiverThread;	// thread for UDP receiver
	
	/**
	 * Construct the <code>ServerPnPServer</code> with specified paramemters.
	 * 
	 * @param gui the reference of <code>AbstractMainFrame</code>
	 * @param port the port to be used for starting the server
	 * @param maxConn the maximum number of handlers used for processing UDP messages
	 * @param period the sitting up time used for initiating next round of UDP packets dissemination
	 * @throws SocketException if open socket on the port fails
	 */
	public ServerPnPServer(AbstractMainFrame gui, int port, int maxConn, long period) throws SocketException 
	{
		super(gui, port, maxConn, period);
	}

	/**
	 * Start both sender and receiver for processing UDP messages.
	 */
	public void run() 
	{
		try 
		{
			// init sender
			sender = new ServerPnPSender(gui, port);
			scheduler = new Timer("Ping Sender of Server Peer");

			// init receiver
			receiver = new ServerPnPReceiver(gui, sender.getDatagramSocket(), maxConn);
			receiver.addUDPListener((ServerPnPSender) sender);
			receiverThread = new Thread(receiver, "Pong Receiver of Server Peer");

			// start both sender and receiver
			scheduler.scheduleAtFixedRate(sender, 100, period);
			receiverThread.start();
		} 
		catch (SocketException e) 
		{
			System.err.println("Cannot start UDP service");
			System.exit(1);
		}
	}
	
}