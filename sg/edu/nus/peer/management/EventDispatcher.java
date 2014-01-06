/*
 * @(#) EventDispatcher.java 1.0 2006-1-7
 * 
 * Copyright 2006, National University of Singapore.
 * All right reserved.
 */

package sg.edu.nus.peer.management;

/**
 * Interfaces used for managing event listeners.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-7
 */

public interface EventDispatcher 
{

	/**
	 * Register a set of <code>ActionListener</code>s to
	 * the <code>PooledSocketHandler</code> for the purpose
	 * of processing any incoming network events.
	 */
	void registerActionListeners(); 
	
}
