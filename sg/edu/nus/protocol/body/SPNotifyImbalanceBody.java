/*
 * @(#) SPNotifyImbalanceBody.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;

import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for notifying the imbalance of
 * the stree structure, and hence it may be necessary to do network
 * restructuring later
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class SPNotifyImbalanceBody extends Body implements Serializable 
{

	// private members
	private static final long serialVersionUID = 77119351493357414L;

	private PhysicalInfo physicalSender;
	private LogicalInfo logicalSender;
	private LogicalInfo missingNode;
	private boolean direction;
	private LogicalInfo logicalDestination;

	/**
	 * Construct the message body with specified parameters.
	 * 
	 * @param physicalSender physical address of the sender
	 * @param logicalSender logical address of the sender
	 * @param missingNode missing position
	 * @param direction direction of sending request
	 * @param logicalDestination logical address of the receiver
	 */
	public SPNotifyImbalanceBody(PhysicalInfo physicalSender, LogicalInfo logicalSender,
								 LogicalInfo missingNode, boolean direction,
								 LogicalInfo logicalDestination) 
	{
		this.physicalSender = physicalSender;
		this.logicalSender = logicalSender;
		this.missingNode = missingNode;
		this.direction = direction;
		this.logicalDestination = logicalDestination;
	}

	/**
	 * Construct the message body with a string value.
	 * 
	 * @param serializeData the string value that contains 
	 * the serialized message body
	 */
	public SPNotifyImbalanceBody(String serializeData)
	{
		String[] arrData = serializeData.split(":");
		try
		{
			this.physicalSender = new PhysicalInfo(arrData[1]);
			this.logicalSender = new LogicalInfo(arrData[2]);
			this.missingNode = new LogicalInfo(arrData[3]);
			this.direction = Boolean.valueOf(arrData[4]).booleanValue();
			this.logicalDestination = new LogicalInfo(arrData[5]);
		}
		catch(Exception e)
		{
			System.out.println("Incorrect serialize data at NotifyImbalance:" + serializeData);
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
	 * Get missing node position
	 * 
	 * @return missing node position
	 */
	public LogicalInfo getMissingNode()
	{
		return this.missingNode;
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

		outMsg = "NOTIFYIMBALANCE";
		outMsg += "\n\t Physical Sender:" + physicalSender.toString();
		outMsg += "\n\t Logical Sender:" + logicalSender.toString();
		outMsg += "\n\t Missing Node:" + missingNode.toString();
		outMsg += "\n\t Direction:" + String.valueOf(direction);
		outMsg += "\n\t Logical Destination:" + logicalDestination.toString();

		return outMsg;
	}

	@Override
	public String toString()
	{
		String outMsg;

		outMsg = "NOTIFYIMBALANCE";
		outMsg += ":" + physicalSender.toString();
		outMsg += ":" + logicalSender.toString();
		outMsg += ":" + missingNode.toString();
		outMsg += ":" + String.valueOf(direction);
		outMsg += ":" + logicalDestination.toString();

		return outMsg;  
	}
	
}
