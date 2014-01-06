/*
 * @(#) EventDispatcherAdapter.java 1.0 2006-1-7
 * 
 * Copyright 2006, National University of Singapore.
 * All right reserved.
 */

package sg.edu.nus.peer.management;

import sg.edu.nus.util.*;

/**
 * An empty class is convenient for creating
 * a concrete event dispatcher.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-7
 */

public class EventDispatcherAdapter extends AbstractPooledSocketHandler implements EventDispatcher
{

	public void registerActionListeners() 
	{
	}

	public void handleConnection() 
	{
	}
	
}
