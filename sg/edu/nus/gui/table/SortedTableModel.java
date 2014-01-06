/*
 * @(#) SortedTableModel.java 1.0 2006-2-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.table;

import javax.swing.table.*;;

/**
 * This class simply wraps the <code>DefaultTableModel</code> for
 * dynamically insert, remove and update table cell data.
 * <p>
 * If necessary, some derived funtions of the <code>DefaultTableModel</code>
 * should be customized for our own purpose.
 * 
 * @see javax.swing.table.DefaultTableModel
 */

public class SortedTableModel extends DefaultTableModel
{

	// private members
	private static final long serialVersionUID = 7336655840072918485L;
	
	/**
	 * Construct an empty table model with column names.
	 * 
	 * @param columns the column names
	 */
	public SortedTableModel(String[] columns)
	{
		super();
		super.setColumnIdentifiers(columns);
	}

	@Override
	public boolean isCellEditable(int row, int col)
	{
		return false;
	}

}