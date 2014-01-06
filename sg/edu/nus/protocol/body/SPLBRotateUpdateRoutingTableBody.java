/*
 * @(#) SPLBRotateUpdateRoutingTableBody.java 1.0 2006-2-22
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.Serializable;

import sg.edu.nus.peer.info.*;

/**
 * Implement the message body used for updating routing table 
 * of a super peer when it changes its logical position due to 
 * network restructuring 
 * 
 * @author Vu Quang Hieu
 * @version 1.0 2006-2-22
 */

public class SPLBRotateUpdateRoutingTableBody extends Body implements Serializable
{

	// private members
  	private static final long serialVersionUID = 2861941729659632997L;
  	
  	private PhysicalInfo physicalSender;
  	private LogicalInfo logicalSender;
  	private RoutingItemInfo infoRequester;
  	private int index;
  	private boolean direction;
  	private LogicalInfo logicalDestination;

  	/**
	 * Construct the message body with speicified parameters.
  	 * 
  	 * @param physicalSender physical address of the sender
  	 * @param logicalSender logical address of the sender
  	 * @param infoRequester information of the changed node 
  	 * @param index position of the changed node in its 
  	 * neighbor node's routing table
  	 * @param direction direction of sending request
  	 * @param logicalDestination logical address of the receiver
  	 */
  	public SPLBRotateUpdateRoutingTableBody(PhysicalInfo physicalSender, 
  			LogicalInfo logicalSender, RoutingItemInfo infoRequester, 
  			int index, boolean direction, LogicalInfo logicalDestination)
  	{
  		this.physicalSender = physicalSender;	
  		this.logicalSender = logicalSender;
  		this.infoRequester = infoRequester;
  		this.index = index;
  		this.direction = direction;
  		this.logicalDestination = logicalDestination;
  	}

	/**
	 * Construct the message body with a string value.
	 * 
	 * @param serializeData the string value that contains 
	 * the serialized message body
	 */
  	public SPLBRotateUpdateRoutingTableBody(String serializeData)
  	{
  		String[] arrData = serializeData.split(":");	
  		try
  		{
  			this.physicalSender = new PhysicalInfo(arrData[1]);
  			this.logicalSender = new LogicalInfo(arrData[2]);
  			this.infoRequester = new RoutingItemInfo(arrData[3]);
  			this.index = Integer.parseInt(arrData[4]);
  			this.direction = Boolean.valueOf(arrData[5]).booleanValue();
  			this.logicalDestination = new LogicalInfo(arrData[6]);
  		}
  		catch(Exception e)
  		{
  			System.out.println("Incorrect serialize data at LBRotateUpdateRoutingTable:" + serializeData);
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
  	 * Get information of the changed node
  	 * 
  	 * @return information of the changed node
  	 */
  	public RoutingItemInfo getInfoRequester()
  	{
  		return this.infoRequester;
  	}

  	/**
  	 * Get index position of the changed node
  	 * 
  	 * @return index position of the changed node in its neighbor's routing table
  	 */
  	public int getIndex()
  	{
  		return this.index;
  	}

  	/**
  	 * Get direction 
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

  		outMsg = "LBROTATEUPDATEROUTINGTABLE";
  		outMsg += "\n\t Physical Sender:" + physicalSender.toString();
  		outMsg += "\n\t Logical Sender:" + logicalSender.toString();
  		outMsg += "\n\t Info Requester:" + infoRequester.toString();
  		outMsg += "\n\t Index:" + index;
  		outMsg += "\n\t Direction:" + String.valueOf(direction);
  		outMsg += "\n\t Logical Destination:" + logicalDestination.toString();

  		return outMsg;
  	}

  	@Override
  	public String toString()
  	{
  		String outMsg;

  		outMsg = "LBROTATEUPDATEROUTINGTABLE";
  		outMsg += ":" + physicalSender.toString();
  		outMsg += ":" + logicalSender.toString();
  		outMsg += ":" + infoRequester.toString();
  		outMsg += ":" + index;
  		outMsg += ":" + String.valueOf(direction);
  		outMsg += ":" + logicalDestination.toString();

  		return outMsg;
  	}
  	
}
