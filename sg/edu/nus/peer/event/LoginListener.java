/*
 * @(#) LoginListener.java 1.0 2006-2-1
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;
import java.sql.*;

import javax.swing.JOptionPane;

//import sg.edu.nus.accesscontrol.RoleManagement;
import sg.edu.nus.gui.*;
import sg.edu.nus.gui.bootstrap.*;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Implement a listener for processing LOGIN message.
 * <p>
 * <code>DBConnector</code> is used to initiate the
 * connection with the back-end database.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-1
 */

public class LoginListener extends ActionAdapter
{

	public LoginListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		//System.out.println("Bootstrap process login...");
		
		// store result if login success or fails
		boolean loginSuccess = true;
		BootstrapGUI bootstrapGui = (BootstrapGUI) gui;
						
    	Message result = null;
    	Head thead = null;
    	
    	UserBody ub = (UserBody) msg.getBody();
    	String user = ub.getUserID();
    	String pwd  = ub.getPassword();
    	String ip	= ub.getIP();
    	int port	= ub.getPort();
    	String type = ub.getPeerType();
    	
    	// the flag read from config file require to check login from database
		if (bootstrapGui.DB_USER_CHECK){
			// we check local_admin user type only
			// normal user should connect to its server
			loginSuccess = MetaDataAccess.metaCheckLoginLocalAdmin(bootstrapGui.conn, user, pwd);
		}
		
		//
		if (!loginSuccess) 
		{
			thead = new Head();
			thead.setMsgType(MsgType.USER_NOT_EXIST.getValue());
			Body tbody = new ConfirmBody();
			Message message = new Message(thead, tbody);
			message.serialize(oos);
			return;
		}
		
		// in case login success
		
    	PeerMaintainer maintainer = PeerMaintainer.getInstance();
    	PeerInfo peerInfo = new PeerInfo(user, ip, port, type);

    	/* if have online super peers */
    	if (maintainer.hasServer()) 
    	{
    		thead = new Head();
    		thead.setMsgType(MsgType.LACK.getValue());
    		FeedbackBody tbody = new FeedbackBody();
    		tbody.setOnlineSuperPeers(maintainer.getServers());
    		
    		result = new Message(thead, tbody);
    	}
				
    	/*
    	 * if the current peer is a super peer and
    	 * there does not exist any other online super peers,
    	 * then the current super peer directly joins the network
    	 */
    	else if (type.equals(PeerType.SUPERPEER.getValue()))	
    	{
    		thead = new Head();
    		thead.setMsgType(MsgType.UNIQUE_SUPER_PEER.getValue());
    		Body tbody = new ConfirmBody();

    		result = new Message(thead, tbody);
    		
    		/* register this peer */
    		maintainer.put(peerInfo);
    		
    		/* update table component */
    		Pane pane = ((BootstrapGUI) gui).getPane();
    		pane.firePeerTableRowInserted(pane.getPeerTableRowCount(), peerInfo.toObjectArray());
    	}
    	
    	/*
    	 * if the current peer is a client peer and 
    	 * there does not exist any other online super peers,
    	 * then the current peer cannot join the system
    	 */
    	else
    	{
    		thead = new Head();
    		thead.setMsgType(MsgType.NO_ONLINE_SUPER_PEER.getValue());
    		Body tbody = new ConfirmBody();
    		
    		result = new Message(thead, tbody);
    	}
		
//    	/* register MySQL driver */
//    	DBConnector.registerDriver();
//    	
//		try
//		{
//			UserBody ub = (UserBody) msg.getBody();
//			String user = ub.getUserID();
//			String pwd  = ub.getPassword();
//			String ip	= ub.getIP();
//			int port	= ub.getPort();
//			String type = ub.getPeerType();
//			
//			/* insert a user and his password into the user account table */
//			String sql = "SELECT * FROM USER_ACCOUNT WHERE user_id='" + user 
//					+ "' AND type='" + type + "'";
//
//			Connection conn = DBConnector.getConnection();
//			Statement stmt  = conn.createStatement();
//    		ResultSet rs	= stmt.executeQuery(sql); 
//    		
//    		/* 
//    		 * if does not exists such a user,
//    		 * return a USER_NOT_EXIST message 
//    		 */
//    		if (!rs.next()) 
//    		{
//        		stmt.close();
//        		conn.close();
//    			/* otherwise,  */
//    			thead = new Head();
//    			thead.setMsgType(MsgType.USER_NOT_EXIST.getValue());
//    			Body tbody = new ConfirmBody();
//    			Message message = new Message(thead, tbody);
//    			message.serialize(oos);
//    			return;
//    		}
//
//    		/* judge whether this user is valid */
//    		sql = "SELECT * FROM USER_ACCOUNT WHERE user_id='" + user 
//    			+ "' AND password='" + pwd + "' AND type='" + type + "'";
//
//    		conn = DBConnector.getConnection();
//    		stmt = conn.createStatement();
//    		rs	 = stmt.executeQuery(sql); 
//    		
//    		/* if exists such a user */
//    		if (rs.next()) 
//    		{
//				/* judge whether exist online super peers */
//    	    	PeerMaintainer maintainer = PeerMaintainer.getInstance();
//    	    	PeerInfo peerInfo = new PeerInfo(user, ip, port, type);
//
//				/* if have online super peers */
//				if (maintainer.hasServer()) 
//				{
//					thead = new Head();
//					thead.setMsgType(MsgType.LACK.getValue());
//					FeedbackBody tbody = new FeedbackBody();
//					tbody.setOnlineSuperPeers(maintainer.getServers());
//					
//					result = new Message(thead, tbody);
//				}
//				
//				/*
//				 * if the current peer is a super peer and
//				 * there does not exist any other online super peers,
//				 * then the current super peer directly joins the network
//				 */
//				else if (type.equals(PeerType.SUPERPEER.getValue()))	
//				{
//					thead = new Head();
//					thead.setMsgType(MsgType.UNIQUE_SUPER_PEER.getValue());
//					Body tbody = new ConfirmBody();
//
//					result = new Message(thead, tbody);
//
//					/* register this peer */
//					maintainer.put(peerInfo);
//
//					/* update table component */
//					Pane pane = ((BootstrapGUI) gui).getPane();
//					pane.firePeerTableRowInserted(pane.getPeerTableRowCount(), peerInfo.toObjectArray());
//				}
//				/*
//				 * if the current peer is a client peer and 
//				 * there does not exist any other online super peers,
//				 * then the current peer cannot join the system
//				 */
//				else
//				{
//					thead = new Head();
//					thead.setMsgType(MsgType.NO_ONLINE_SUPER_PEER.getValue());
//					Body tbody = new ConfirmBody();
//
//					result = new Message(thead, tbody);
//				}
//    		}
//    		/* if does not exists such a user */
//    		else 
//    		{
//    			thead = new Head();
//    			thead.setMsgType(MsgType.LNCK.getValue());
//    			Body tbody = new ConfirmBody();
//
//    			result = new Message(thead, tbody);
//    		}
//    		
//    		/* close connection and statement */
//    		stmt.close();
//    		conn.close();
//		}
//		catch (Exception e)
//    	{
//    		throw new EventHandleException("user login failure", e);
//    	}
    	
		/* write result to the request peer */
		result.serialize(oos);
		
		//VHTam
		//send global schema to peers
		//and send role setting to peers
		if (loginSuccess){
			try {
				
//				Pane pane = ((BootstrapGUI) gui).getPane();
//				SchemaTreePanel schemaPanel = pane.getSchemaTreePane();
//				String schema = schemaPanel.getSchemaString();
//				
//				oos.writeObject(schema);
				
				// send role setting
				//RoleManagement roleManagement = new RoleManagement();
				//roleManagement.readFromDB(((BootstrapGUI) gui).conn);
				
				//oos.writeObject(roleManagement);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//end VHTam
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.LOGIN.getValue())
			return true;
		return false;
	}

}