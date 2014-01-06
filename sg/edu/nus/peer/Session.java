/*
 * @(#) Session.java 1.0 2005-10-25
 * 
 * Copyrights 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer;

/**
 * Implement a session during a user login the system. For each user, there exists
 * an unique session. After logout the system, the session will be deleted.
 * <p>
 * This class is a singleton class. That is, for a user we do not allow 
 * the user to switch to another role during his own session.
 * <p>
 * NOTE: In the future, the password should be encrypted.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-10-25
 */

public class Session
{
	
	/* user information during his session */
	private String userID;
	private String password;
	
	/* define the handler of the session */
	private static Session instance;
	
	/**
	 * Contruct a session with the user identifier and his password.
	 * 
	 * @param userID the identifier of the user
	 * @param pwd the password of the user
	 */
	private Session(String userID, String pwd)
	{
		this.userID    = userID;
		this.password  = pwd;
	}
	
	/**
	 * Get the instance of the session.
	 * 
	 * @param userID the identifier of the user
	 * @param pwd the password of the user
	 */
	public synchronized static Session getInstance(String userID, String pwd)
	{
		/* create a new instance */
		instance = new Session(userID, pwd);
		return instance;
	}
	
	/**
	 * Get the instance of the session.
	 * 
	 * @return the instance of the session
	 * @throws RuntimeException if instance cannot be found
	 */
	public synchronized static Session getInstance() throws RuntimeException
	{
		if (instance == null)
			throw new RuntimeException("Instance not found!");
		return instance;
	}
	
	/**
	 * Determine if the session has already been created.
	 * 
	 * @return if the session is null, return <code>false</code>;
	 * 			otherwise, return <code>true</code>
	 */
	public synchronized static boolean hasInstance()
	{
		return (instance != null);
	}
	
	/**
	 * Clear the current session.
	 */
	public synchronized static void clear()
	{
		instance = null;
	}
	
	/**
	 * Get the identifier of the user.
	 * 
	 * @return the identifier of the user
	 */
	public String getUserID()
	{
		return userID;
	}
	
	/**
	 * Get the password of the user.
	 * 
	 * @return the password of the user
	 */
	public String getPassword()
	{
		return password;
	}
	
}