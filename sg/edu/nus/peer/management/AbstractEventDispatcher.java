/*
 * @(#) AbstractEventDispatcher.java 1.0 2005-12-30
 * 
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.management;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.logging.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.peer.PeerType;
import sg.edu.nus.peer.event.*;
import sg.edu.nus.util.*;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.gui.server.ServerGUI;

/**
 * This class extends <code>AbstractPooledSocketHandler</code> and 
 * is responsible for handling the incoming socket connection. 
 * <p>
 * The incoming network event will be dispatched to the corresponding event
 * processor and the response will be returned by the event processor.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 */

public abstract class AbstractEventDispatcher extends EventDispatcherAdapter
{

	// protected members
	protected AbstractMainFrame gui;
	
	/**
	 * A container for keeping all registered network event listeners.
	 * Each network event listener is responsible for processing a type
	 * of networking message.
	 */
	protected List<ActionListener> listeners;
	
	// private members
	private ObjectInputStream  ois;
	private ObjectOutputStream oos;
	
	/**
	 * A mutex lock used for making sure all events be processed
	 * in a concurrent manner.
	 */
	private Lock mutex;
	
	/**
	 * Construct an <code>AbstractEventDispatcher</code>.
	 * 
	 * @param gui the main frame that contains the event manager 
	 */
	public AbstractEventDispatcher(AbstractMainFrame gui)
	{
		this.gui = gui;
		mutex = new ReentrantLock();
		listeners = Collections.checkedList(new Vector<ActionListener>(), ActionListener.class);
	}
	
	/**
	 * Process the incoming socket connection.
	 */
	public void handleConnection() 
	{
		try
		{
			/* read stream and process request */
			ois  = new ObjectInputStream(connection.getInputStream());
			Message message = Message.deserialize(ois);
			
			/* determine whether the message type is valid first */
			if (MsgType.checkValue(message.getHead().getMsgType()))
			{		
				processEvent(message);
				
				String peerType = this.gui.peer().getPeerType();
				if (peerType.equals(PeerType.BOOTSTRAP.getValue()))
				{
					if (BootstrapGUI.inputLog != null)
					{
						BootstrapGUI.inputLog.WriteLog(message.toString());
					}
				}
				else if (peerType.equals(PeerType.SUPERPEER.getValue()))
				{
					if (ServerGUI.inputLog != null)
					{
						ServerGUI.inputLog.WriteLog(message.toString());
					}
				}
			}
			
			/* never close reader before writer */
			oos.close();
			ois.close();
		}
		catch (Exception e)
		{
			gui.log(LogEventType.ERROR.getValue(), "Exception occurs because:\r\n" + Tools.getException(e), connection.getInetAddress().getHostName(), connection.getInetAddress().getHostAddress() + ":" + connection.getPort(), System.getProperty("user.name"));
			LogManager.getLogger(gui.peer()).error("Exception ocurrs in handleConnection of AbstractEventDispatcher", e);
		}
	}
	
	/**
	 * Pass the network event to each registered listener,
	 * which will process the obtained event and then
	 * return the confirm message, if necessary.
	 * <p>
	 * The response message should be processed in the listener's
	 * <code>actionPerformed</code> method.
	 * <p>
	 * Note: the current design is not good enough, but I have no
	 * any good idea.
	 * 
	 * @param msg the network event
	 */
	protected void processEvent(Message msg)
	{
		try
		{
			/* get the reference of the output stream */
			oos = new ObjectOutputStream(connection.getOutputStream());
			
			/* 
			 * this clone operation here is VERY IMPORTANT,
			 * after clone, the current listeners will be
			 * not affected by the new joined ones
			 */
			Object[] list;
			synchronized (this)
			{
				list = listeners.toArray().clone();
			}

			/* make sure concurrent processing */
			mutex.lock();
			
			/* process network events */
			ActionListener listener;
			for (int i = 0; i < list.length; i++)
			{
				listener = (ActionListener) list[i];				
				
				if (listener.isConsumed(msg))
				{
					// add system log
					LogManager.getLogger(gui.peer()).info(MsgType.description(msg.getHead().getMsgType()) + " event from " + connection.getInetAddress().getHostAddress() + ":" + connection.getPort() + " will be operated...");
					
					// handle the specific case of file downloading
//					String Name = "sg.edu.nus.peer.event.DownloadRequestListener";
//					if(listener.getClass().getName().equals(Name)){
//						((DownloadRequestListener)listener).actionPerformed(this.connection, msg);						
//					}			
//					else
						// perform action here
						listener.actionPerformed(oos, (Message) msg);
					
					// add log event
					gui.log(LogEventType.INFORMATION.getValue(), MsgType.description(msg.getHead().getMsgType()) + " event performs successfully", connection.getInetAddress().getHostName(), connection.getInetAddress().getHostAddress() + ":" + connection.getPort(), System.getProperty("user.name"));
					LogManager.getLogger(gui.peer()).info(MsgType.description(msg.getHead().getMsgType()) + " event from " + connection.getInetAddress().getHostAddress() + ":" + connection.getPort() + " performs successfully...");
				}
			}
		}	// release lock in finally clause
		catch (IOException e)
		{
			mutex.unlock();
			gui.log(LogEventType.ERROR.getValue(), "I/O exception occurs because:\r\n" + Tools.getException(e), connection.getInetAddress().getHostName(), connection.getInetAddress().getHostAddress() + ":" + connection.getPort(), System.getProperty("user.name"));
			LogManager.getLogger(gui.peer()).error("IOException ocurrs in processEvent of AbstractEventDispatcher", e);
		}
		catch (EventHandleException e)
		{
			mutex.unlock();
			gui.log(LogEventType.ERROR.getValue(), MsgType.description(msg.getHead().getMsgType()) + " event performs failure because:\r\n" + Tools.getException(e), connection.getInetAddress().getHostName(), connection.getInetAddress().getHostAddress() + ":" + connection.getPort(), System.getProperty("user.name"));
			LogManager.getLogger(gui.peer()).error("EventHandleException ocurrs in processEvent of AbstractEventDispatcher", e);
		}
		finally
		{
			/* unlock all listeners */
			mutex.unlock();
		}
	}
	
	/**
	 * Register a set of <code>ActionListener</code>s to
	 * the <code>PooledSocketHandler</code> for the purpose
	 * of processing any incoming network events.
	 * 
	 * 
	 */
	public abstract void registerActionListeners(); 
	
	/**
	 * Add an instance of the <code>ActionListener</code> 
	 * into the <code>EventDispatcher</code>.
	 * 
	 * @param l an instance of the <code>ActionListener</code>
	 */
	public synchronized void addActionListener(ActionListener l)
	{
		if (!listeners.contains(l))
			listeners.add(l);
	}
	
	/**
	 * Remove an instance of the <code>ActionListener</code>
	 * from the <code>EventDispatcher</code>.
	 * 
	 * @param l an instance of the <code>ActionListener</code>
	 */
	public synchronized void removeActionListener(ActionListener l)
	{
		if (!listeners.isEmpty())
			listeners.remove(l);
	}
	
	/**
	 * Remove all <code>ActionListener</code>.
	 */
	public synchronized void removeAll()
	{
		listeners.clear();
	}

}