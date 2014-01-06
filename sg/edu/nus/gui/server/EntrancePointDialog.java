/*
 * @(#) EntrancePointDialog.java 1.0 2006-1-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved. 
 */

package sg.edu.nus.gui.server;

import java.awt.event.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.info.*;

/**
 * This class is used to get the bootstrap server returned
 * online super peers and allows the super user to select
 * an arbitrary one to join the super peer network.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-10
 */
public final class EntrancePointDialog extends AbstractEntrancePointDialog
{
	
	// private members
	private static final long serialVersionUID = -1924839997613927760L;
	private ServerPeer serverpeer;
	
	/**
	 * Construct the entrance point dialog.
	 *
	 * @param gui the handler of the main frame
	 * @param data an array of online super peers 
	 */
	public EntrancePointDialog(ServerGUI gui, PeerInfo[] data)
	{
		super(gui, "Online Super Peers", true, 360, 280, data);
		this.serverpeer = gui.peer();

		/* show dialog */
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event) 
	{
	    String cmd = event.getActionCommand();
	    
	    /* if join network */
	    if (cmd.equals(command[0]))
	    {
	    	int rowIdx = entranceTable.getSelectedRow();
			String ip  = "" + entranceTableSorter.getValueAt(rowIdx, 1);
			int port   = Integer.parseInt("" + entranceTableSorter.getValueAt(rowIdx, 2));

			if (serverpeer.performJoinRequest(ip, port))
			{
				((ServerGUI) gui).startService();
		    	dispose();
			}
	    }
	    /* if cancel join */
	    else if (cmd.equals(command[1]))
	    {
			serverpeer.performCancelJoinRequest();
			serverpeer.clearSession();
			dispose();
	    }
	}

	@Override
	protected void processWhenWindowClosing() 
	{
		serverpeer.performCancelJoinRequest();
		serverpeer.clearSession();
	}
	
}