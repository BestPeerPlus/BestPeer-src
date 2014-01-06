/*
 * @(#) Body.java 1.0 2006-1-4
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.protocol.body;

import java.io.*;

/**
 * Define the abstract body of the message.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-4
 */

public abstract class Body implements Cloneable, Serializable
{

	/**
	 * Clone self.
	 * 
	 * @return The instance of <code>Body</code>.
	 */
	public Object clone()
	{
		Body instance = null;
		try
		{
			instance = (Body)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return instance;
	}
	
}