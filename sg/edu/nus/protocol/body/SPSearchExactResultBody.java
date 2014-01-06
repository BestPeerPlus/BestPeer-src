/*
 * @(#) SPSearchExactResultBody.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;

import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for returning the result of the exact search.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class SPSearchExactResultBody extends Body implements Serializable 
{

	// private members
	private static final long serialVersionUID = 2155555075757450123L;
	
	private PhysicalInfo physicalSender;
	private LogicalInfo logicalSender;
	private boolean result;
	private IndexValue returnedInfo;
	private LogicalInfo logicalDestination;

	/**
	 * Construct the message body withe specified parameters.
	 * 
	 * @param physicalSender physical address of the sender
	 * @param logicalSender logical address of the sender
	 * @param result <code>true</code> found at least one data item
	 * <code>false</code> no data item is found
	 * @param logicalDestination logical address of the receiver
	 */
	public SPSearchExactResultBody(PhysicalInfo physicalSender, LogicalInfo logicalSender,
									boolean result, IndexValue returnedInfo, 
									LogicalInfo logicalDestination) 
	{
		this.physicalSender = physicalSender;
		this.logicalSender = logicalSender;
		this.result = result;
		this.returnedInfo = returnedInfo;
		this.logicalDestination = logicalDestination;
	}

	/**
	 * Construct the message body with a string value.
	 * 
	 * @param serializeData the string value that contains 
	 * the serialized message body
	 */
	public SPSearchExactResultBody(String serializeData)
	{
		String[] arrData = serializeData.split(":");
		try
		{
			this.physicalSender = new PhysicalInfo(arrData[1]);
			this.logicalSender = new LogicalInfo(arrData[2]);
			this.result = Boolean.valueOf(arrData[3]).booleanValue();
			if (arrData[4].equals("null"))
				this.returnedInfo = null;
			else
				this.returnedInfo = new IndexValue(arrData[4]);
			this.logicalDestination = new LogicalInfo(arrData[5]);
		}
		catch(Exception e)
		{
			System.out.println("Incorrect serialize data at SearchExactResult:" + serializeData);
			System.out.println(e.getMessage());
		}
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
	 * Get logical address of the sender
	 * 
	 * @return logical address of the sender
	 */
	public LogicalInfo getLogicalSender()
	{
		return this.logicalSender;
	}

	/**
	 * Get result of search
	 * 
	 * @return <code>true</code> found at least one data item
	 * <code>false</code> no data item is found
	 */
	public boolean getResult()
	{
		return this.result;
	}

	/**
	 * Get returned results.
	 * 
	 * @return returned results
	 */
	public IndexValue getReturnedInfo()
	{
		return this.returnedInfo;
	}
	
	/**
	 * Get logical address of the receiver
	 * 
	 * @return logical address of the receiver
	 */
	public LogicalInfo getLogicalDestination()
	{
		return this.logicalDestination;
	}

	/**
	 * Return a readable string for testing or writing in the log file 
	 * 
	 * @return a readable string
	 */
	public String getString()
	{
		String outMsg;

		outMsg = "SEARCHEXACTRESULT";
		outMsg += "\n\t Physical Sender:" + physicalSender.toString();
		outMsg += "\n\t Logical Sender:" + logicalSender.toString();
		outMsg += "\n\t Result:" + String.valueOf(result);
		if (returnedInfo == null)
			outMsg += "\n\t Returned Information: null";
		else
			outMsg += "\n\t Returned Information:" + returnedInfo.toString();
		outMsg += "\n\t Logical Destination:" + logicalDestination.toString();

		return outMsg;
	}

	@Override
	public String toString()
	{
		String outMsg;

		outMsg = "SEARCHEXACTRESULT";
		outMsg += ":" + physicalSender.toString();
		outMsg += ":" + logicalSender.toString();
		outMsg += ":" + String.valueOf(result);
		if (returnedInfo == null)
			outMsg += ":null";
		else
			outMsg += ":" + returnedInfo.toString();
		outMsg += ":" + logicalDestination.toString();

		return outMsg;
	}
	
}
