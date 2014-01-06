/*
 * @(#) TableNode.java 1.0 2006-12-26
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A customized node in the DBTree to represent the <code>table</code> in the database
 * 
 * @author Huang Yukai
 * @version 1.0 2006-12-26
 * @author modified by Han Xixian 2008-6-3 
 * @see javax.swing.tree.DefaultMutableTreeNode
 * @see ColumnNode
 */
public class TableNode extends DefaultMutableTreeNode
{
	//private members
	private static final long serialVersionUID = -7505215041496882063L;

	// the name of the table
	private String tableName;
	
	// the symbol of share of the table
	private boolean shared;

	private boolean temShare;
	private boolean temUnshare;
	
	private String nodeType;
	
	/**
	 * Constructor
	 * 
	 * @param tableName -  the name of the table
	 */
	public TableNode(String tableName)
	{
		super(tableName);
		this.tableName = tableName;
		shared = false;
		temShare = false;
		temUnshare = false;
		
		nodeType = "TABLE";
	}
	
	/**
	 * Return the name of the table
	 * 
	 * @return name of table
	 */
	public String getName() 
	{
		return tableName;
	}

	/**
	 * Set the string value as the name of table
	 * 
	 * @param tableName - the name of table
	 */
	public void setName(String tableName) 
	{
		this.tableName = tableName;
	}
	
	/**
	 * Return the share symbol of the table
	 * 
	 * @return share symbol
	 */
	public boolean getShared() {
		return shared;
	}

	/**
	 * Set the boolean value as the share symbol of table
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
