/*
 * @(#) AbstractEntrancePointDialog.java 1.0 2006-1-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved. 
 */

package sg.edu.nus.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import sg.edu.nus.gui.table.*;
import sg.edu.nus.peer.info.*;

/**
 * An abstract dialog used for getting the bootstrap server returned
 * online super peers and allows the common user to select
 * an arbitrary one to attach.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-10
 */

public abstract class AbstractEntrancePointDialog extends AbstractDialog implements ActionListener, ListSelectionListener
{
	
	// private members
	private static final long serialVersionUID = -1924839997613927760L;
	/**
	 * A <code>SortedTable</code> used for displaying online super peer's information.
	 */
	protected SortedTable entranceTable;
	/**
	 * A <code>TableModel</code> used for storing online super peer's information.
	 */
	protected SortedTableModel entranceTableModel;
	/**
	 * A <code>TableSorter</code> used for sorting table elements.
	 */
	protected TableSorter entranceTableSorter;
	/**
	 * Get the action from the user to join the network.
	 */
	protected JButton btnJoin; // have to get the reference of the button
	/**
	 * The name of buttons.
	 */
	protected final String[] btnName = {"Join", "Cancel"};
	/**
	 * The command string used for buttons.
	 */
	protected final String[] command = {"join", "cancel"};
	
	/**
	 * Construct the entrance point dialog.
	 *
	 * @param gui the handler of the main frame
	 * @param title the dialog title to be displayed
	 * @param model determine whether this is a model dialog
	 * @param height the height of this window
	 * @param width the width of this window
	 * @param data an array of online super peers 
	 */
	public AbstractEntrancePointDialog(Frame gui, String title, boolean model, int height, int width, PeerInfo[] data)
	{
		super(gui, title, model, height, width);

		this.setLayout(new BorderLayout());
		
		JPanel panel = null;

		/* make the content pane */
		panel = makeContentPane(data);
		this.add(panel, BorderLayout.CENTER);

		/* make button panel and add it to content pane */
		panel = makeButtonPane();
		this.add(panel, BorderLayout.SOUTH);

		/* insert data into the table and update UI */
		int rowIdx = 0;
		for (int i = 0; i < data.length; i++)
		{
			rowIdx = entranceTableModel.getRowCount();
			entranceTableModel.insertRow(rowIdx, data[i].toObjectArrayWithoutType());
		}
	}

	/**
	 * Make the content panel.
	 * 
	 * @param data a set of <code>PeerInfo</code>
	 * @return the instance of <code>JPanel</code> a <code>SortedTable</code>
	 */
	protected JPanel makeContentPane(PeerInfo[] data) 
	{
		JPanel panel = new JPanel(new BorderLayout());
		String[] columns = { "Peer ID", "IP Address", "Port" };	
		int[] width = { 100, 100, 80 };
		
		entranceTableModel  = new SortedTableModel(columns);
		entranceTableSorter = new TableSorter(entranceTableModel);
		entranceTable = new SortedTable(entranceTableSorter, width);
		entranceTableSorter.setTableHeader(entranceTable.getTableHeader());
		
		entranceTable.setGridColor(Color.LIGHT_GRAY);
		entranceTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		entranceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionModel rowSM = entranceTable.getSelectionModel();
        rowSM.addListSelectionListener(this);
        
		JScrollPane scrollPane = new JScrollPane(entranceTable);
		panel.add(scrollPane, BorderLayout.CENTER);

		panel.setBorder(BorderFactory.createTitledBorder(null, 
				"Select An Entrance Point", 
				TitledBorder.DEFAULT_JUSTIFICATION,	TitledBorder.DEFAULT_POSITION, 
				null, null));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		return panel;
	}

	/**
	 * Make the button panel.
	 * 
	 * @return the instance of <code>JPanel</code> with buttons
	 */
	protected JPanel makeButtonPane()
	{
		JButton button = null;
		JPanel panel = new JPanel();

		/* set panel layout */
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(Box.createHorizontalGlue());
		
        /* add buttons to panel */
		btnJoin = makeButton(btnName[0], command[0]);
		btnJoin.setEnabled(false);
        panel.add(btnJoin);

        panel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        button = makeButton(btnName[1], command[1]);
        panel.add(button);
        
        return panel;
	}
	
	/**
	 * Make individual button.
	 * 
	 * @param name the button name
	 * @param cmd the command string
	 * @return the instance of <code>JButton</code>
	 */
	protected JButton makeButton(String name, String cmd)
	{ 
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
        button.addActionListener(this);
        return button;
	}
	
	public void valueChanged(ListSelectionEvent event) 
	{
		ListSelectionModel lsm = (ListSelectionModel)event.getSource();
		if (lsm.isSelectionEmpty()) 
		{
			btnJoin.setEnabled(false);
			return;
		} 
		btnJoin.setEnabled(true);
	}
	
}