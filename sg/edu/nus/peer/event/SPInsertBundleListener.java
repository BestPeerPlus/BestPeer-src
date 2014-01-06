/*
 * @(#) SPInsertBundleListener.java 1.0 2006-3-6
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.event;

import java.io.*;
import java.util.Vector;

import sg.edu.nus.gui.*;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.protocol.*;
import sg.edu.nus.protocol.body.*;

/**
 * Implement a listener for processing SP_INSERT_BUNDLE message.
 * 
 * @author Vu Quang Hieu 
 * @version 1.0 2006-3-6
 */

public class SPInsertBundleListener extends ActionAdapter
{

	public SPInsertBundleListener(AbstractMainFrame gui) 
	{
		super(gui);
	}

	public void actionPerformed(ObjectOutputStream oos, Message msg) throws EventHandleException 
	{
		super.actionPerformed(oos, msg);
		
    	Message result = null;
    	Head head = new Head();
    	Body insertBody;
    	
		try
		{
			/* get the handler of the ServerPeer */
			ServerPeer serverpeer = (ServerPeer) gui.peer();
			PhysicalInfo physicalInfo = serverpeer.getPhysicalInfo();			
			
			/* get the message body */
			SPInsertBundleBody body = (SPInsertBundleBody) msg.getBody();			
			Vector<IndexPair> insertedData = body.getData();
			PhysicalInfo peerID = body.getPhysicalSender();
			String docID = body.getDocID();			
			
			head.setMsgType(MsgType.SP_INSERT.getValue());
			for (int i = 0; i < insertedData.size(); i++)
			{
				IndexPair indexPair = (IndexPair) insertedData.get(i);				
				IndexInfo indexInfo = new IndexInfo(docID);
				IndexValue indexValue = new IndexValue(IndexValue.STRING_TYPE, indexPair.getKeyword(), indexInfo);
				insertBody = new SPInsertBody(physicalInfo, null, indexValue, null);
				result = new Message(head, insertBody);
				serverpeer.sendMessage(physicalInfo, result);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new EventHandleException("Super peer bundle insertion failure", e);
		}
	}

	public boolean isConsumed(Message msg) throws EventHandleException 
	{
		if (msg.getHead().getMsgType() == MsgType.SP_INSERT_BUNDLE.getValue())
			return true;
		return false;
	}

}