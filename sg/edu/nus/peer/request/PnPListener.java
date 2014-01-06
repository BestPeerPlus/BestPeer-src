/*
 * @(#) PnPListener.java 1.0 2006-9-26
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.request;

import sg.edu.nus.peer.info.*;

/**
 * The <code>PnPListener</code> defines a callback interface used for 
 * processing the behavior of node failure according to the PONG message.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-9-26
 * 
 * @see BootstrapPnPServer
 * @see ClientPnPServer
 * @see ServerPnPServer
 */

public interface PnPListener extends UDPListener
{
	
	/**
	 * This method will be invoked if a node is still alive.
	 * 
	 * @param info the <code>PeerInfo</code> instance
	 */
	public void nodeAlive(PeerInfo info);
	
	/**
	 * This method will be invoked when a node is detected as failure.
	 * 
	 * @param info the <code>PeerInfo</code> instance
	 */
	public void nodeFailure(PeerInfo info);
	
}