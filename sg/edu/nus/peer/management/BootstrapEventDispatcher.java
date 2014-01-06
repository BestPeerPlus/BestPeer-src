/*
 * @(#) BootstrapEventDispatcher.java 1.0 2006-2-3
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.peer.management;

//import sg.edu.nus.accesscontrol.bootstrap.AccessControlRoleUpdateListener;
//import sg.edu.nus.accesscontrol.bootstrap.CreateUserFromServerPeerListener;
import sg.edu.nus.gui.*;
import sg.edu.nus.peer.event.*;

/**
 * Implement a concrete event manager used for monitoring
 * all incoming socket connections related to the bootstrap server.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-3
 */

public class BootstrapEventDispatcher extends AbstractEventDispatcher
{

	public BootstrapEventDispatcher(AbstractMainFrame gui) 
	{
		super(gui);
	}

	@Override
	public void registerActionListeners() 
	{
		this.addActionListener(new RegisterListener(gui));
		this.addActionListener(new LoginListener(gui));
		this.addActionListener(new JoinListener(gui));
		this.addActionListener(new PongListener(gui));
		this.addActionListener(new LeaveListener(gui));
		
		//VHTam: add following code to the next //end VHTam
		//add access control listener
		//this.addActionListener(new AccessControlRoleUpdateListener(gui));
		//this.addActionListener(new CreateUserFromServerPeerListener(gui));
		//end VHTam

	}
	
}