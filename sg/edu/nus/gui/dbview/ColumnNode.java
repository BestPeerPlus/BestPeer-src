/*
 * @(#) ColumnNode.java 1.0 2006-12-26
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import javax.swing.tree.DefaultMutableTreeNode;
/**
 * A customized node in the DBTree to represent the <code>column</code> of tables in database
 *   
 * @author Huang Yukai
 * @version 1.0 2006-12-26
 * @author modified by Han Xixian 2008-6-3
 * @see javax.swing.tree.DefaultMutableTreeNode
 * @see TableNode
 */

public class ColumnNode extends DefaultMutableTreeNode
{
	//private members
	private static final long serialVersionUID = 5884128319679687283L;

	// the name of the column
	private String columnName;
	private boolean shared;
	
	private boolean temShare;
	private boolean temUnshare;
	
	private String nodeType;
	
	/**
	 * Constructor
	 * 
	 * @param columnName -  the name of the column
	 */
	public ColumnNode(String columnName)
	{
		super(columnName);
		this.columnName = columnName;
		shared = false;
		temShare = false;
		temUnshare = false;
		
		nodeType = "COLUMN";
	}

	/**
	 * Return the name of the column
	 * 
	 * @return name of column
	 */
	public String getName() 
	{
		return columnName;
	}

	/**
	 * Set the string value as the name of column
	 * 
	 * @param columnName - the name of column
	 */
	public void setName(String columnName) 
	{
		this.columnName = columnName;
	}
	
	/**
	 * Return the share symbol of the column
	 * 
	 * @return share symbol
	 */
	public boolean getShared() {
		return shared;
	}

	/**
	 * Set the boolean value as the share symbol of column
	 * 
	 * @param shared - the share symbol of column
	 */
	public void setShared(boolean shared) {
		this.shared = shared;
	}

	/**
	 * @return the temShare
	 */
	public boolean isTemShare() {
		return temShare;
	}

	/**
	 * @param temShare the temShare to set
	 */
	public void setTemShare(boolean temShare) {
		this.temShare = temShare;
	}

	/**
	 * @return the temUnshare
	 */
	public boolean isTemUnshare() {
		return temUnshare;
	}

	/**
	 * @param temUnshare the temUnshare to set
	 */
	public void setTemUnshare(boolean temUnshare) {
		this.temUnshare = temUnshare;
	}

	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
}
