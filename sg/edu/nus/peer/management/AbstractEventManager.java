/*
 * @(#) AbstractEventManager.java 1.0 2006-1-24
 * 
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.management;

import java.io.*;
import java.net.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.util.*;

/**
 * An abstract event manager is responsbile for 
 * accepting the incoming socket connections.
 * <p>
 * The methods in this class are empty. This class exists as
 * convenience for creating concrete event managers.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 */

public abstract class AbstractEventManager extends AbstractPooledSocketServer
{
	
	// protected members
	protected AbstractMainFrame gui;

	/**
	 * A set of <code>AbstractEventDispatcher</code> that will be
	 * responsible for processing the incoming socket requests.
	 */
	protected AbstractEventDispatcher[] eventHandlers;
	
	/**
	 * Construct a pooled socket server at specified port 
	 * and with maximum number of incoming connections
	 *  
	 * @param gui the main frame that contains the event manager 
	 * @param port the port used for server socket
	 * @param maxConn the maximum connections that can be handled simultaneously
	 */
	public AbstractEventManager(AbstractMainFrame gui, int port, int maxConn) 
	{
		super(port, maxConn);
		this.gui = gui;
	}

	public void stop()
	{
		try
		{
			stop = true;
			
			/* stop socket handlers first */
			for (int i = 0; i < maxConn; i++)
				eventHandlers[i].stop();
			
			AbstractPooledSocketHandler.stopAllHandlers();

			/* then stop server socket */
			serverSocket.close();
			serverSocket = null;
		}
        catch (SocketException e)
        {
        	return;
        }
        catch (IOException e)
        {   
	        return;
        }
	}

	@Override
	protected void showConnections(Socket incomingConnection) 
	{
		// do nothing now
	}

}