/*
 * @(#) SortedTable.java 1.0 2006-2-10
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.table;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Wrap the <code>JTable</code> that can be sorted at any column.
 * <p>
 * Example:
 * <pre>
 * String[] column = {"name", "sex", "age"};
 * SortedTableModel model = new SortedTableModel(column);
 * TableSorter sorter = new TableSorter(model);
 * SortedTable table = new SortedTable(sorter);
 * </pre>
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-10
 */

public class SortedTable extends JTable
{

	// private members
	private static final long serialVersionUID = -9117513805688200663L;
	
	/**
	 * Construct a table with a sorted table model.
	 * 
	 * @param sorter the sorted table model
	 */
	public SortedTable(TableSorter sorter)
	{
		super(sorter);
	}

	/**
	 * Construct a table with a sorted table model and the width of each column.
	 * 
	 * @param sorter the sorted table model
	 * @param width the width of each column
	 */
	public SortedTable(TableSorter sorter, int[] width)
	{
		this(sorter);
		
		/* set width of each column */
		if (width.length != this.getColumnCount())
			throw new IllegalArgumentException("Parameter erorr!");
		
		TableColumn column = null;
		for (int i = 0; i < this.getColumnCount(); i++) 
		{
		    column = this.getColumnModel().getColumn(i);
	        column.setPreferredWidth(width[i]);
		}
	}
	
}