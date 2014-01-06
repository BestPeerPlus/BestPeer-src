/*
 * @(#) Bootstrap.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.gui.bootstrap.BootstrapGUI;
import sg.edu.nus.logging.LogEventType;
import sg.edu.nus.logging.LogManager;
import sg.edu.nus.peer.info.PeerInfo;
import sg.edu.nus.peer.management.BootstrapEventManager;
import sg.edu.nus.peer.management.PeerMaintainer;
import sg.edu.nus.peer.request.BootstrapPnPServer;
import sg.edu.nus.protocol.Head;
import sg.edu.nus.protocol.Message;
import sg.edu.nus.protocol.MsgType;
import sg.edu.nus.protocol.body.ForceOutBody;
import sg.edu.nus.util.Inet;
import sg.edu.nus.util.Tools;

/**
 * Implement a bootstrap.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class Bootstrap extends AbstractPeer
{

	// private member
	private static final String CONFIG_FILE = "./bootstrap.ini";
	
	/**
	 * Load system-defined value.
	 */
	public static void load()
	{
    	try
    	{
    		FileInputStream in = new FileInputStream(CONFIG_FILE);
    		Properties keys = null;
    		if (in != null)
    		{
    			keys = new Properties();
    			keys.load(in);
    			
    			String value = keys.getProperty("LOCALSERVERPORT");
       			if (value != null && !value.equals(""))
       				LOCAL_SERVER_PORT = Integer.parseInt(value);
        		
       			value = keys.getProperty("CAPACITY");
       			if (value != null && !value.equals(""))
       				CAPACITY = Integer.parseInt(value);

       			value = keys.getProperty("DB_DRIVER");
       			if (value != null && !value.equals(""))
       				BootstrapGUI.DB_DRIVER = value;
       			
       			value = keys.getProperty("DB_URL");
       			if (value != null && !value.equals(""))      			
       				BootstrapGUI.DB_URL = value;
       			
       			value = keys.getProperty("DB_NAME");
       			if (value != null && !value.equals("")){      			
       				BootstrapGUI.DB_NAME = value;
       				
       				//fixme: just use the default name at this time
       				BootstrapGUI.DB_NAME = "BootstrapMetaBestPeer";
       				////////
       			}
       			
       			value = keys.getProperty("DB_USERNAME");
       			if (value != null && !value.equals(""))      			
       				BootstrapGUI.DB_USERNAME = value;
       			
       			value = keys.getProperty("DB_PASSWORD");
       			if (value != null && !value.equals(""))      			
       				BootstrapGUI.DB_PASSWORD = value;
       			
       			value = keys.getProperty("DB_USER_CHECK");
       			if (value != null && !value.equals(""))      			
       				BootstrapGUI.DB_USER_CHECK = new Boolean(value).booleanValue();
       			      			
       			in.close();
    		}
    	}
    	catch (IOException e)
    	{
    		/*
    		 * if exception occurs, just use default value
    		 */
    		LOCAL_SERVER_PORT = 30000;
    		CAPACITY = 10;
    		
    		return;
    	}
	}
	
	/**
	 * Write user-defined values to file. Notice that this function must be
	 * called after user applies the change.
	 */
	public static void write()
	{
    	try
    	{
    		FileOutputStream out = new FileOutputStream(CONFIG_FILE);
        	Properties keys = new Properties();
        	
        	keys.put("LOCALSERVERPORT", Integer.toString(LOCAL_SERVER_PORT));
        	keys.put("CAPACITY", Integer.toString(CAPACITY));
        	
        	keys.put("DB_DRIVER", BootstrapGUI.DB_DRIVER);
        	keys.put("DB_URL", BootstrapGUI.DB_URL);
        	keys.put("DB_NAME", BootstrapGUI.DB_NAME);
        	keys.put("DB_USERNAME", BootstrapGUI.DB_USERNAME);
        	keys.put("DB_PASSWORD", BootstrapGUI.DB_PASSWORD);
        	keys.put("DB_USER_CHECK", Boolean.toString(BootstrapGUI.DB_USER_CHECK));
        	        	
        	keys.store(out, "Bootstrap Server Configuration");
        	
        	out.close();
    	}
    	catch (IOException e)
    	{
    		e.printStackTrace();
    	}
	}

	public Bootstrap(AbstractMainFrame gui, String peerType) 
	{
		super(gui, peerType);
		this.peerMaintainer = PeerMaintainer.getInstance();	/* init online peer manager now */
	}

	// ----------- for tcp service -----------------
	
	@Override
	public boolean startEventManager(int port, int capacity) 
	{
		if (!isEventManagerAlive())
		{
			try
			{
				eventManager = new BootstrapEventManager(gui, port, capacity);
				new Thread(eventManager, "TCP Server").start();
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	// ------------ for udp service -------------
	
	@Override
	public boolean startUDPServer(int port, int capacity, long period) 
	{
		if (!this.isUDPServerAlive())
		{
			try
			{
				udpServer = new BootstrapPnPServer(gui, port, capacity, period);
				new Thread(udpServer, "UDP Server").start();
				return true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	public void troubleshoot(boolean toBoot, boolean toServer, boolean toClient)
	{
		if (toServer && this.peerMaintainer.hasServer())
		{
			PeerInfo info = null;
			PeerInfo[] peerList = null;

			// init datagram packets
			PeerType type = PeerType.BOOTSTRAP;
			String pid = type.getValue();
			DatagramPacket trouble = this.troubleshoot(type, pid);
			DatagramSocket socket = this.udpServer.getDatagramSocket();
			try 
			{
				peerList = this.peerMaintainer.getServers();
				int size = peerList.length;
				for (int i = 0; i < size; i++)
				{
					info = peerList[i];
					trouble.setAddress(InetAddress.getByName(info.getInetAddress()));
					trouble.setPort(info.getPort());
					for (int j = 0; j < TRY_TIME; j++)
					{
						socket.send(trouble);
						if (debug)
							System.out.println("case 7: send troubleshoot out to " + trouble.getAddress().getHostAddress() + " : " + trouble.getPort());
					}
				} 
			}
			catch (UnknownHostException e) 
			{ /* ignore it */
			}
			catch (SocketException e) 
			{ /* ignore it */
			}
			catch (IOException e)
			{ /* ignore it */
			}
		}
	}
	
	@Override
	public void forceOut(String ip, int port) 
	{
		try
		{
			/* init socket connection with super peer */
			Socket socket = new Socket(ip, port);

			Head head = new Head();
			head.setMsgType(MsgType.FORCE_OUT.getValue());
			Message message = new Message(head, new ForceOutBody());

			/* send force out message to super peer */
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(message);
			
			/* add log event */
			((BootstrapGUI) gui).log(LogEventType.INFORMATION.getValue(), MsgType.description(head.getMsgType()) + " request is sent out...",	socket.getLocalAddress().getHostName(),	socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort(), System.getProperty("user.name"));
			LogManager.getLogger(this).info(MsgType.description(head.getMsgType()) + " request is sent out from " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
			
			/* close stream */
			oos.close();
			socket.close();
		}
		catch (Exception e)
		{
			// record exception event
			((BootstrapGUI) gui).log(LogEventType.WARNING.getValue(), "Fail to open connection with super peer because:\r\n" + Tools.getException(e), Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name")); 
			LogManager.getLogger(this).error("Fail to open connection to super peer", e);
		}
	}

	// ------------ for system log --------------
	
	@Override
	public String getLogStore() 
	{
		return logpath + peerType + "/";
	}
	

//	public int sendSchema(){
//		PeerMaintainer maintainer = PeerMaintainer.getInstance();
//
//		PeerInfo[] servers = maintainer.getServers();
//		int result =0;
//		for (int i = 0 ; i < servers.length;i++){
//			try{
//				//send a message to a peer
//			Socket socket = new Socket(servers[i].getInetAddress(),servers[i].getPort());
//			Head head = new Head();
//			head.setMsgType(MsgType.SCHEMA_UPDATE.getValue());
//			SchemaDumper sd = new SchemaDumper(null,null,null,null);//use default values;
//			Message message = new Message(head, new SchemaUpdateBody(sd.dumpDB()));
//
//			/* send force out message to super peer */
//			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//			oos.writeObject(message);
//			
//			/* add log event */
//			((BootstrapGUI) gui).log(LogEventType.INFORMATION.getValue(), MsgType.description(head.getMsgType()) + " request is sent out...",	socket.getLocalAddress().getHostName(),	socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort(), System.getProperty("user.name"));
//			LogManager.getLogger(this).info(MsgType.description(head.getMsgType()) + " request is sent out from " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
//			
//			/* close stream */
//			oos.close();
//			socket.close();
//			}catch(Exception e){
//				((BootstrapGUI) gui).log(LogEventType.WARNING.getValue(), "Fail to open connection with super peer because:\r\n" + Tools.getException(e), Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name")); 
//				LogManager.getLogger(this).error("Fail to open connection to super peer", e);
//				result++;
//			}
//			
//		}
//		
//		return result;
//	}

	/**
	 * The method reads the schema from the 'BestPeer' database and sends it to all the super-peers that the
	 * bootstrap node knows of.
	 * 
	 * @return the number of servers that failed to receive the schema (0 means everything is ok)
	 */
	//VHTam
	public int sendSchema(String schema){
		
		PeerMaintainer maintainer = PeerMaintainer.getInstance();

		PeerInfo[] servers = maintainer.getServers();
		int result =0;
//		for (int i = 0 ; i < servers.length;i++){
//			try{
//				//send a message to a peer
//			Socket socket = new Socket(servers[i].getInetAddress(),servers[i].getPort());
//			Head head = new Head();
//			head.setMsgType(MsgType.SCHEMA_UPDATE.getValue());
//			
//			Message message = new Message(head, new SchemaUpdateBody(schema));
//
//			/* send force out message to super peer */
//			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//			oos.writeObject(message);
//			
//			/* add log event */
//			((BootstrapGUI) gui).log(LogEventType.INFORMATION.getValue(), MsgType.description(head.getMsgType()) + " request is sent out...",	socket.getLocalAddress().getHostName(),	socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort(), System.getProperty("user.name"));
//			LogManager.getLogger(this).info(MsgType.description(head.getMsgType()) + " request is sent out from " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
//			
//			/* close stream */
//			oos.close();
//			socket.close();
//			}catch(Exception e){
//				((BootstrapGUI) gui).log(LogEventType.WARNING.getValue(), "Fail to open connection with super peer because:\r\n" + Tools.getException(e), Inet.getInetAddress2().getHostName(), Inet.getInetAddress(), System.getProperty("user.name")); 
//				LogManager.getLogger(this).error("Fail to open connection to super peer", e);
//				result++;
//			}
//			
//		}
		
		return result;
	
	}
}