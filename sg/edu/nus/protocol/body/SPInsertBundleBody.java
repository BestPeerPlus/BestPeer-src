/*
 * @(#) SPInsertBundleBody.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;
import java.util.Vector;

import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for inserting a bundle of 
 * data items into the super peer network.
 *  
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class SPInsertBundleBody extends Body implements Serializable
{

	// private members
	private static final long serialVersionUID = -7136118098211874191L;

	private PhysicalInfo physicalSender;
	private String docID;
	private Vector<IndexPair> data;	

	/**
	 * Construct the message body with specified parameters
	 * 
	 * @param physicalSender
	 * @param docID
	 * @param data
	 */	
	public SPInsertBundleBody(PhysicalInfo physicalSender, 
			String docID, Vector<IndexPair> data)
	{
		this.physicalSender = physicalSender;
		this.docID = docID;
		this.data = data;		
	}

	/**
	 * Construct the message body from a string value.
	 * 
	 * @param serializeData the string value that contains 
	 * the serialized message body
	 */
	public SPInsertBundleBody(String serializeData)
	{
		String[] arrData = serializeData.split(":");
		try
		{
			this.physicalSender = new PhysicalInfo(arrData[1]);
			this.docID = arrData[2];
			this.data = new Vector<IndexPair>();
			String[] arrTransferedData = arrData[3].split("%");
			for (int i = 0; i < arrTransferedData.length; i++)
			{
				this.data.add(new IndexPair(arrTransferedData[i]));
			}
		}
		catch(Exception e)
		{
			System.out.println("Incorrect serialize data at InsertBundle:" + serializeData);
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Update physical address of the sender
	 * 
	 * @param physicalSender physical address of the sender
	 */
	public void setPhysicalSender(PhysicalInfo physicalSender)
	{
		this.physicalSender = physicalSender;
	}	

	/**
	 * Get physical address of the sender
	 * 
	 * @return physical address of the sender
	 */
	public PhysicalInfo getPhysicalSender()
	{
		return this.physicalSender;
	}
	
	/**
	 * Get data items wanted to insert
	 * 
	 * @return data items wanted to insert
	 */
	public Vector<IndexPair> getData()
	{
		return this.data;
	}

	/**
	 * Get document id
	 * 
	 * @return docID
	 */
	public String getDocID()
	{
		return this.docID;
	}

	/**
	 * Return a readable string for testing or writing in the log file 
	 * 
	 * @return a readable string
	 */
	public String getString()
	{
		String outMsg;

		outMsg = "INSERTBUNDLE";
		outMsg += "\n\t Physical Sender:" + physicalSender.toString();
		outMsg += "\n\t Document ID:" + docID;
		outMsg += "\n\t Data:";
		for (int i = 0; i < data.size() - 1; i++)
		{
			outMsg += data.get(i) + " ";
		}
		outMsg += data.get(data.size() - 1);		
		
		return outMsg;
	}

	@Override
	public String toString()
	{
		String outMsg;

		outMsg = "INSERTBUNDLE";
		outMsg += ":" + physicalSender.toString();
		outMsg += ":" + docID;
		outMsg += ":";
		for (int i = 0; i < data.size() - 1; i++)
		{
			outMsg += data.get(i) + "%";
		}
		outMsg += data.get(data.size() - 1);
		
		return outMsg;
	}	
}
