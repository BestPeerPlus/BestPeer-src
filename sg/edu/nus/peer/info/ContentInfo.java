/*
 * @(#) ContentInfo.java 1.0 2006-2-7
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.info;

import java.io.*;
import java.util.*;

/**
 * Implement the index key set of each node in the BATON tree.
 * 
 * @author Quang Hieu Vu
 * @author (Modified by) Xu Linhao
 * @author (Modified by) Quang Hieu Vu
 * @author (Modified by) David Jiang
 * @version 1.0 2006-2-7
 */

public class ContentInfo implements Serializable 
{
	
	// public members
	/**
	 * If the index key to be inserted lies in the range of 
	 * the min and max index key.
	 */
	public final static int INSERT_NORMALLY   = 0;
	
	/**
	 * If the index key to be inserted is smaller than 
	 * the old min index key.
	 */
	public final static int INSERT_AS_MIN_KEY = 1;
	
	/**
	 * If the index key to be inserted is bigger than
	 * the old max index key.
	 */
	public final static int INSERT_AS_MAX_KEY = 2;
	
	// private members
	private static final long serialVersionUID = 3676792785906596644L;

	private BoundaryValue minValue;
	private BoundaryValue maxValue;
	
	private int order;
	private Vector<IndexValue> data;

	/**
	 * Contruct the set of the index keys at each super node.
	 * 
	 * @param minValue the minimum index value
	 * @param maxValue the maximum index value
	 * @param order the load balance ratio
	 * @param data the set of the index keys
	 */
	public ContentInfo(BoundaryValue minValue, BoundaryValue maxValue, int order, Vector<IndexValue> data) 
	{
	    this.minValue = minValue;
	    this.maxValue = maxValue;
	    this.order = order;
	    this.data  = new Vector<IndexValue>();
	    //Collections.copy(this.data, data);
	    this.data = data;
	}
	
	/**
	 * Construct the set of the index keys with
	 * a serialized string value.
	 * 
	 * @param serializeData the serialized string value
	 */
	public ContentInfo(String serializeData)
	{
	    String[] arrData = serializeData.split("_");
	    try
	    {
	    	this.minValue = new BoundaryValue(arrData[0]);
	    	this.maxValue = new BoundaryValue(arrData[1]);
	    	this.order = Integer.parseInt(arrData[2]);
	    	this.data  = new Vector<IndexValue>();
	    	if (!arrData[3].equals("null"))
	    	{
	    		String[] arrStoredData = arrData[3].split("%");
	    		for (int i = 0; i < arrStoredData.length; i++)
	    		{
	    			this.data.add(new IndexValue(arrStoredData[i]));
	    		}
	    	}
	    }
	    catch (Exception e)
	    {
	    	e.printStackTrace();
	    	System.out.println("Incorrect serialize data at ContentInfo:" + serializeData);
	    }
	}
	
	/**
	 * Set the minimum index value.
	 * 
	 * @param minValue the minimum index value
	 */
	public void setMinValue(BoundaryValue minValue)
	{
	    this.minValue = minValue;
	}

	/**
	 * Get the minimum index value.
	 * 
	 * @return the minimum index value
	 */
	public BoundaryValue getMinValue()
	{
	    return this.minValue;
	}

	/**
	 * Set the maximum index value.
	 * 
	 * @param maxValue the maximum index value
	 */
	public void setMaxValue(BoundaryValue maxValue)
	{
	    this.maxValue = maxValue;
	}

	/**
	 * Get the maximum index value.
	 * 
	 * @return the maximum index value
	 */
	public BoundaryValue getMaxValue()
	{
	    return this.maxValue;
	}

	/**
	 * Set the ratio of the load balance.
	 * 
	 * @param order the ratio of the load balance
	 */
	public void setOrder(int order)
	{
	    this.order = order;
	}

	/**
	 * Get the ratio of the load balance.
	 * 
	 * @return the ratio of the load balance
	 */
	public int getOrder()
	{
	    return this.order;
	}

	/**
	 * Get the set of the index value.
	 * 
	 * @return the set of the index key
	 */
	public Vector<IndexValue> getData()
	{
	    return this.data;
	}

	public void setData(Vector<IndexValue> data)
	{
		this.data = data;
	}
	
	/**
	 * Remove all index keys.
	 */
	public void deleteAll()
	{
	    this.data.removeAllElements();
	}

	/**
	 * Insert an index key into the local key set.
	 * 
	 * @param dataValue the index key to be inserted
	 * @param mode  if INSERT_NORMALLY, then directly insert this key;
	 * 				if INSERT_AS_MIN_KEY, then insert this key as the new min key;
	 * 				if INSERT_AS_MAX_KEY, then insert this key as the new max key
	 */
	public void insertData(IndexValue dataValue, int mode)
	{
    	IndexValue temptValue, sentinelValue;	    	

    	switch (mode)
	    {
	    case INSERT_NORMALLY:
	    	/* FALL THROUGH */
	    	
	    case INSERT_AS_MIN_KEY:
	    	sentinelValue = dataValue;
	    	if (this.data.size() != 0) 
	    	{
	    		int i = 0;
	    		while (i < this.data.size()) 
	    		{
	    			if (((IndexValue) this.data.get(i)).compareTo(sentinelValue) > 0) 
	    			{
	    				temptValue = (IndexValue)this.data.get(i);
	    				this.data.setElementAt(sentinelValue, i);
	    				sentinelValue = temptValue;
	    			}
	    			i++;
	    		}
	    	}
	    	this.data.add(sentinelValue);
	    	
	    	/* change min key value */
	    	if (dataValue.getType() == IndexValue.NUMERIC_TYPE)
	    		this.minValue.setLongValue(Long.parseLong(dataValue.getKey()));
	    	else if (dataValue.getType() == IndexValue.STRING_TYPE)
	    		this.minValue.setStringValue(dataValue.getKey());	    
	    	
	    	break;
	    	
	    case INSERT_AS_MAX_KEY:
	    	this.data.add(dataValue);
	    	
	    	/* change max key value */
	    	if (dataValue.getType() == IndexValue.NUMERIC_TYPE)
	    		this.maxValue.setLongValue(Long.parseLong(dataValue.getKey()));
	    	else if (dataValue.getType() == IndexValue.STRING_TYPE)
	    		this.maxValue.setStringValue(dataValue.getKey());
	    	
	    	break;
	    }
	}

	/**
	 * Delete an index key from the key set.
	 * 
	 * @param dataValue the index key to be removed
	 */
	public void deleteData(IndexValue dataValue)
	{
		for (int i = 0; i < this.data.size(); i++)
		{
			if (dataValue.compareTo((IndexValue)this.data.get(i)) == 0)
			{
				this.data.remove(i);
				return;
			}
	    }
	}

	/**
	 * Check if the value is in the range
	 */
	public boolean isInRange(String value){
		if (value.compareTo(minValue.getStringValue()) >= 0 &&
				value.compareTo(maxValue.getStringValue()) < 0)
			return true;
		return false;
	}
	
	/**
	 * Check if the value is less than the range
	 * A value is less than the range if it is less than the lower
	 * bound of that range
	 */
	public boolean isLessThanRange(String value){
		if(value.compareTo(minValue.getStringValue()) < 0)
			return true;
		return false;
	}
	
	/**
	 * Check if the value is greater than the range
	 */
	public boolean isGreaterThanRange(String value){
		if(value.compareTo(maxValue.getStringValue()) >= 0)
			return true;
		return false;
	}
	
	/**
	 * Compare the value with the range
	 * >0 for greater
	 * =0 for equal
	 * <0 for less than
	 */
	public int compareTo(String value){
		if(value.compareTo(minValue.getStringValue()) < 0)
			return -1;
		if(value.compareTo(maxValue.getStringValue()) < 0)
			return 0;
		return 1;
	}
	/**
	 * FIXME: Determine if the node is overload.
	 * 
	 * @return if overload, return <code>true</code>;
	 * 			otherwise, return <code>false</code>
	 */
	public boolean isOverloaded()
	{
	    /*
	     * personal comments:
	     * I suggest that this method should be
	     * changed to the class ServerPeer. On
	     * the other side, isUnderloaded() method
	     * should be provided.
	     */
		return this.data.size() >= 2 * this.order;
	}
	
	@Override
	public String toString()
	{
	    String outMsg;
	    outMsg = this.minValue + "_" + this.maxValue + "_" + this.order;
	    if (this.data.size() == 0)
	    	outMsg += "_null";
	    else
	    {
	    	outMsg += "_";
	    	for (int i = 0; i < this.data.size() - 1; i++)
	    	{
	    		outMsg += ((IndexValue)this.data.get(i)).toString() + "%";
	    	}
	    	outMsg += ((IndexValue)this.data.get(this.data.size()-1)).toString();
	    }
	    return outMsg;
	}

	/**
	 * Get the serialized representation of the <code>ContentInfo</code>.
	 * 
	 * @return the serialized representation of the <code>ContentInfo</code>
	 */
	public String serialize()
	{
	    String outMsg;
	    outMsg = "MinValue=" + this.minValue + ",MaxValue=" + this.maxValue + ",Order=" + this.order;
	    if (this.data.size() == 0)
	    	outMsg += ",Data=null";
	    else
	    {
	    	outMsg += ",Data=";
	    	for (int i = 0; i < this.data.size() - 1; i++)
	    	{
	    		outMsg += ((IndexValue)this.data.get(i)).toString() + ":";
	    	}
	    	outMsg += ((IndexValue)this.data.get(this.data.size()-1)).toString();
	    }
	    return outMsg;
	}
	
}