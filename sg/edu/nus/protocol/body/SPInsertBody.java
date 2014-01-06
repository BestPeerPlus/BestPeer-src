/*
 * @(#) SPInsertBody.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;

import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for inserting a new 
 * data item into the super peer network.
 *  
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class SPInsertBody extends Body implements Serializable
{

	// private members
	private static final long serialVersionUID = 8564152697562874898L;
	
	private PhysicalInfo physicalSender;
	private LogicalInfo  logicalSender;
	private IndexValue	 data;
	private LogicalInfo  logicalDestination;

	/**
	 * Construct the message body with specified parameters.
	 * 
	 * @param physicalSender physical address of the sender 
	 * @param logicalSender logical address of the sender 
	 * @param data data item wanted to insert
	 * @param logicalDestination logical address of the receiver
	 */
	public SPInsertBody(PhysicalInfo physicalSender, LogicalInfo logicalSender,
			IndexValue data, LogicalInfo logicalDestination)
	{
		this.physicalSender = physicalSender;
		this.logicalSender = logicalSender;
		this.data = data;
		this.logicalDestination = logicalDestination;
	}

	/**
	 * Construct the message body from a string value.
	 * 
	 * @param serializeData the string value that contains 
	 * the serialized message body
	 */
	public SPInsertBody(String serializeData)
	{
		String[] arrData = serializeData.split(":");
		try
		{
			this.physicalSender = new PhysicalInfo(arrData[1]);
			if (arrData[2].equals("null"))
			{
				this.logicalSender = null;
			}
			else
			{
				this.logicalSender = new LogicalInfo(arrData[2]);
			}
			
			this.data = new IndexValue(arrData[3]);
			if (arrData[4].equals("null"))
			{
				this.logicalDestination = null;
			}
			else
			{
				this.logicalDestination = new LogicalInfo(arrData[4]);
			}
		}
		catch(Exception e)
		{
			System.out.println("Incorrect serialize data at Insert:" + serializeData);
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
	 * Update logical address of the sender
	 * 
	 * @param logicalSender logical address of the sender
	 */
	public void setLogicalSender(LogicalInfo logicalSender)
	{
		this.logicalSender = logicalSender;	
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
	 * Get data item wanted to insert
	 * 
	 * @return data item wanted to insert
	 */
	public IndexValue getData()
	{
		return this.data;
	}

	/**
	 * Update logical address of the receiver
	 * 
	 * @param logicalDestination logical address of the receiver
	 */
	public void setLogicalDestination(LogicalInfo logicalDestination)
	{
		this.logicalDestination = logicalDestination;	
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

		outMsg = "INSERT";
		outMsg += "\n\t Physical Sender:" + physicalSender.toString();
		if (logicalSender == null)
		{
			outMsg += "\n\t Logical Sender:null";
		}
		else
		{
			outMsg += "\n\t Logical Sender:" + logicalSender.toString();
		}
		
		outMsg += "\n\t Data:" + data.toString();
		if (logicalDestination == null)
		{
			outMsg += "\n\t Logical Destination:null";
		}
		else
		{
			outMsg += "\n\t Logical Destination:" + logicalDestination.toString();
		}

		return outMsg;
	}

	@Override
	public String toString()
	{
		String outMsg;

		outMsg = "INSERT";
		outMsg += ":" + physicalSender.toString();
		if (logicalSender == null)
		{
			outMsg += ":null";
		}
		else
		{
			outMsg += ":" + logicalSender.toString();
		}
		
		outMsg += ":" + data.toString();
		if (logicalDestination == null)
		{
			outMsg += ":null";
		}
		else
		{
			outMsg += ":" + logicalDestination.toString();
		}

		return outMsg;
	}
	
}
