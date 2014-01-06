/*
 * @(#) SPLBGetLoadInfoBody.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;

import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for getting the status of the workload
 * of adjacent super peers when a node is overloaded.
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class SPLBGetLoadInfoBody extends Body implements Serializable
{

	// private members
	private static final long serialVersionUID = -7715987761065409053L;
	
	private PhysicalInfo physicalSender;
	private LogicalInfo logicalSender;
	private boolean direction;
	private LogicalInfo logicalDestination;

	/**
	 * Construct the message body with speicified parameters.
	 * 
	 * @param physicalSender physical address of the sender
	 * @param logicalSender logical address of the sender
	 * @param direction direction of sending request
	 * @param logicalDestination logical address of the receiver
	 */
	public SPLBGetLoadInfoBody(PhysicalInfo physicalSender, LogicalInfo logicalSender,
							   boolean direction, LogicalInfo logicalDestination) 
	{
		this.physicalSender = physicalSender;
		this.logicalSender = logicalSender;
		this.direction = direction;
		this.logicalDestination = logicalDestination;
	}

	/**
	 * Construct the message body with a string value.
	 * 
	 * @param serializeData the string value that contains 
	 * the serialized message body
	 */
	public SPLBGetLoadInfoBody(String serializeData)
	{
		String[] arrData = serializeData.split(":");
		try
		{
			this.physicalSender = new PhysicalInfo(arrData[1]);
			this.logicalSender = new LogicalInfo(arrData[2]);
			this.direction = Boolean.valueOf(arrData[3]).booleanValue();
			this.logicalDestination = new LogicalInfo(arrData[4]);
		}
		catch(Exception e){
			System.out.println("Incorrect serialize data at LBGetLoadInfo:" + serializeData);
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
	 * Get direction of sending request
	 * 
	 * @return direction of sending request
	 */
	public boolean getDirection()
	{
		return this.direction;
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

		outMsg = "LBGETLOADINFO";
		outMsg += "\n\t Physical Sender:" + physicalSender.toString();
		outMsg += "\n\t Logical Sender:" + logicalSender.toString();
		outMsg += "\n\t Direction:" + direction;
		outMsg += "\n\t Logical Destination:" + logicalDestination.toString();

		return outMsg;
	}

	@Override
	public String toString()
	{
		String outMsg;

		outMsg = "LBGETLOADINFO";
		outMsg += ":" + physicalSender.toString();
		outMsg += ":" + logicalSender.toString();
		outMsg += ":" + direction;
		outMsg += ":" + logicalDestination.toString();

		return outMsg;
	}
	
}
