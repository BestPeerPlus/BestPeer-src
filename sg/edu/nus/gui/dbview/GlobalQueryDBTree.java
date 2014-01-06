/*
 * @(#) DBTree.java 1.0 2006-12-26
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.ComponentOrientation;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


import sg.edu.nus.gui.AbstractMainFrame;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.Session;

/**
 * GlobalSchemaDBTree is used for schema mapping, and display the 
 * table and column of global schema.
 * <p>
 * 
 * @author Han Xixian
 * @version 1.0 2008-08-16 
 * @see javax.swing.JTree
 */

public class GlobalQueryDBTree extends JTree
{
	//private members
	private static final long serialVersionUID = 1458145543006995860L;
	
	//private GlobalSchemaDBTreeMouseListener mouseListener;

	// the parent container which holds this tree
	private Component parentComponent;
	
	public static int ORIENTATION_RIGHT_TO_LEFT = 0;
	public static int ORIENTATION_LEFT_TO_RIGHT = 1;	
	
	/**
	 * Construct an empty tree on the parent component
	 * 
	 * @param parent -  the parent container which holds this tree
	 */
	public GlobalQueryDBTree(Component parent)
	{
		this.parentComponent = parent;
		
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(null);
		this.setModel(model);
		
		// add customized TreeCellRenderer
		this.setCellRenderer(new GlobalSchemaTreeCellRenderer());

		Connection conn_global = ServerPeer.conn_globalSchema;
		
		try
		{
			DatabaseMetaData dbmd = conn_global.getMetaData();
			
			String databaseName = "globaldb";
			
			this.setupTree(dbmd, databaseName);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(4), 
				LanguageLoader.getProperty("PanelTitle.globalschema")));
	}
	
	/**
	 * Set up a tree with the data extracted from the metadata and use the input root node.
	 * 
	 * Modified by Han Xixian 2008-6-6
	 * When setting up this tree, table or column sharing information is also checked.
	 * Note: Sharing information is stored in file sharedInfo.dat	 * 
	 * 
	 * @param metadata - metadata to hold all the table information of the connected database 
	 * @param root - the root node
	 * 
	 * @throws SQLException - if a database access error occurs
	 */
	public void setupTree(DatabaseMetaData metadata, String databaseName) throws SQLException
	{
		Connection conn = ServerPeer.conn_schemaMapping;
		
		DBTreeNode root = new DBTreeNode(databaseName);
		root.setNodeType("DATABASE");
		ResultSet tables, columns;
		String[] tableTypes = {"TABLE"};
		
		tables = metadata.getTables(null, null, null, tableTypes);
			
		String tableName;
		DBTreeNode tableNode;
		DBTreeNode columnNode;
		
		// retrieval all the table information from the resultset
		while(tables.next())
		{
			tableName = tables.getString("TABLE_NAME");
			tableNode = new DBTreeNode(tableName);

			tableNode.setNodeType("TABLE");
			
			Statement stmt = conn.createStatement();
			String query = "select count(*) count from matches where " +
					"sourceTable = '" + tableName + " '";
			
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int count = rs.getInt("count");
			if(count > 0)
			{
				tableNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
				tableNode.setSelected(true);
			}
			
			root.add(tableNode);
			columns = metadata.getColumns(null, null, tableName, null);
			rs.close();
			stmt.close();
				
			while(columns.next())
			{
				String columnName = columns.getString("COLUMN_NAME");
				
				stmt = conn.createStatement();
				query = "select * from matches where " +
				"targetTable = '" + tableName + " ' and targetColumn = '" +
				columnName + "'";
				
				rs = stmt.executeQuery(query);
				
				
				columnNode = new DBTreeNode(columnName);
				columnNode.setNodeType("COLUMN");
				
				while(rs.next())
				{
					String targetDB = rs.getString("sourceDB");
					String targetTable = rs.getString("sourceTable");
					String targetColumn = rs.getString("sourceColumn");
					
					String targetSchemaName = targetDB + "." + targetTable + "."
						+ targetColumn;
					
//					columnNode = new DBTreeNode(databaseName + "." + 
//							tableName + "." + columnName, true);
										
					columnNode.getTargetSchemaName().add(targetSchemaName);
					
					columnNode.setNodeType("COLUMN");
//					columnNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);					
				}

				
				tableNode.add(columnNode);			

				
				rs.close();
				stmt.close();
			}
			
			columns.close();				
		}
				
		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);
	}
	
	public void setTreeOrientation(int oriententation)
	{
	}
	

	/**
	 * Show <code>JPopupMenu</code>.
	 * 
	 * @param x - the x coordinate of the mouse event
	 * @param y - the y coordinate of the mouse event
	 */
	public void showPopupMenu(Component c, int x, int y)
	{
		TreePath path = getClosestPathForLocation(x, y);
		this.setSelectionPath(path);
			
		//new DBTreePopupMenu().show(c, x, y);
	}

//	/**
//	 * Create a <code>MouseListener</code> for <code>DBTree</code>.
//	 * 
//	 * @author Huang Yukai
//	 * @version 1.0 2006-12-26
//	 */
//	
//	final class GlobalSchemaDBTreeMouseListener implements MouseListener
//	{
//		//private members
//		private GlobalSchemaDBTree tree;
//		
//		/**
//		 * Constructor.
//		 * 
//		 * @param tree the handler of <code>DBTree</code>
//		 */
//		public GlobalSchemaDBTreeMouseListener(GlobalSchemaDBTree tree)
//		{
//			this.tree = tree;
//		}
//		
//	    /**
//	     * Invoked when the mouse has been clicked on a component.
//	     */
//	    public void mouseClicked(MouseEvent e) 
//	    {
//	   		this.showPopupMenu(e);
//	    }
//	
//	    /**
//	     * Invoked when a mouse button has been pressed on a component.
//	     */
//	    public void mousePressed(MouseEvent e) 
//	    {
//	    	this.showPopupMenu(e);
//	    }
//	
//	    /**
//	     * Invoked when a mouse button has been released on a component.
//	     */
//	    public void mouseReleased(MouseEvent e) 
//	    {
//	    	this.showPopupMenu(e);
//	    }
//	
//	    /**
//	     * Invoked when the mouse enters a component.
//	     */
//	    public void mouseEntered(MouseEvent e) {}
//	
//	    /**
//	     * Invoked when the mouse exits a component.
//	     */
//	    public void mouseExited(MouseEvent e) {}
//	    
//	    // Show the popup menu.
//	    // @see DBTreePopupMenu
//	    private void showPopupMenu(MouseEvent e)
//	    {
//	    	if(e.isPopupTrigger())
//	    	{
//	    		tree.showPopupMenu(e.getComponent(), e.getX(), e.getY());
//	    	}
//	    	
//	    }
//	}
//	
//	// ----------- used for popup menu --------------
//	
//	/**
//	 * Construct a popup menu for <code>DBTree</code>.
//	 * 
//	 * @author Huang Yukai
//	 * @version 1.0 2006-12-26
//	 * @see javax.swing.JPopupMenu
//	 */
//	
//	final class DBTreePopupMenu extends JPopupMenu implements ActionListener
//	{
//		// private members
//		private static final long serialVersionUID = 6764829618615422807L;
//
//		// menu items
//		private JMenuItem openMenuItem;
//		private JMenuItem schemaMenuItem;
//		private JMenuItem searchMenuItem;
//		private JMenuItem shareMenuItem;
//		private JMenuItem unshareMenuItem;
//		private JMenuItem reshareMenuItem;
//		private JMenuItem refreshMenuItem;	
//		
//		private DefaultMutableTreeNode node;
//	
//		private String[] itemName = 
//		{
//				"Open Table", "View Schema", "Search...", "Share", "Unshare", "Reshare", "Refresh"
//		};
//		
//		private String[] commands = 
//		{
//				"open", "schema", "search", "share", "unshare", "reshare", "refresh"
//		};
//		
//		private String[] images =
//		{
//				"openTable", "schema", "empty", "share", "unshare", "reshare", "refresh"
//		};
//	
//		/**
//		 * Constructor
//		 *
//		 */
//		public DBTreePopupMenu()
//		{
//			this.makeMenu();
//		}
//	
//		/*
//		 * Construct the menu under different situations.
//		 */
//		private void makeMenu()
//		{
//			
//			node = (DefaultMutableTreeNode)GlobalQueryDBTree.this.getSelectionPath().getLastPathComponent();
//			
////			if(!node.toString().equals("TableNode")){
////				return;
////			}
//			
//			int offset = -14;
//			
//			openMenuItem = this.makeMenuItem(itemName[0], commands[0], images[0]);
//			openMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//			
//			schemaMenuItem = this.makeMenuItem(itemName[1], commands[1], images[1]);
//			schemaMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//	
//			searchMenuItem = this.makeMenuItem(itemName[2], commands[2], images[2]);
//			searchMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//	
//			shareMenuItem = this.makeMenuItem(itemName[3], commands[3], images[3]);
//			shareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//	
//			unshareMenuItem = this.makeMenuItem(itemName[4], commands[4], images[4]);
//			unshareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//	
//			reshareMenuItem = this.makeMenuItem(itemName[5], commands[5], images[5]);
//			reshareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//	
//			refreshMenuItem = this.makeMenuItem(itemName[6], commands[6], images[6]);
//			refreshMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
//	
//			this.add(openMenuItem);
//			this.add(schemaMenuItem);
//			// Only click on the table node, then show "open table" and "view schema" functions
//
//			if(!node.toString().equals("TableNode"))
//			{
//				openMenuItem.setEnabled(false);
//				schemaMenuItem.setEnabled(false);				
//			}
//
//			this.addSeparator();
//			this.add(searchMenuItem);
//			this.addSeparator();
//			this.add(shareMenuItem);
//			this.add(unshareMenuItem);
//			this.add(reshareMenuItem);
//						
//			// Only after connecting to the PeerDB system, then show "Share/Unshare/Reshare" functions
//			try
//			{
//				Session.getInstance();
//			}
//			catch(RuntimeException e)
//			{
//				/*Not connect to the peerDB*/
//				shareMenuItem.setEnabled(false);
//				unshareMenuItem.setEnabled(false);
//				reshareMenuItem.setEnabled(false);
//			}
//			this.addSeparator();
//			this.add(refreshMenuItem);
//			
//		}
//		
//		/**
//		 * Make individual menu item.
//		 * 
//		 * @param name - the menu item name
//		 * @param cmd - the command string
//		 * @param img - the image name
//		 * 
//		 * @return the instance of <code>JMenuItem</code>
//		 */
//		private JMenuItem makeMenuItem(String name, String cmd, String img)
//		{ 
//			/* look for the image */
//	        String imageLoc = AbstractMainFrame.SRC_PATH + img + ".png";
//	
//	        JMenuItem menuItem = new JMenuItem(name);
//			menuItem.setActionCommand(cmd);
//	        menuItem.addActionListener(this);
//	
//	        try 
//	        {	//image found
//	            menuItem.setIcon(new ImageIcon(imageLoc, name));
//	        } 
//	        catch (Exception e) 
//	        {   //no image found
//	        	System.err.println("Miss such image: " + imageLoc);
//	        }
//	
//	        return menuItem;
//		}
//		
//		/**
//		 * Invoked when an action occurs.
//		 */
//		public void actionPerformed(ActionEvent event)
//		{
//			String cmd = event.getActionCommand();
//			
//			// if view the data in the selected table
//			if(cmd == commands[0]) 
//			{
//			}
//			// if view the schema of selected table
//			else if(cmd.equals(commands[1]))
//			{
//			}
//			// if search local resource and other peers
//			else if (cmd.equals(commands[2]))
//			{
//				//new SearchDialog((ClientGUI)((DatabaseExplorer)((DBTreeView)DBTree.this.parentComponent).getParentComponent()).getParentComponent());
//			}
//			// if share a file or directory
//			/* If shared a table or column, then change the color of shared table and column */
//			//Added by Han Xixian, 2008-6-3
//			else if (cmd.equals(commands[3]))
//			{
//				
//			}
//			// if unshare a file or directory
//			/* If unshared a table or column, then change the color of unshared table and column */
//			//Added by Han Xixian, 2008-6-3
//			else if (cmd.equals(commands[4]))
//			{
//				
//			}
//			// if update index
//			else if (cmd.equals(commands[5]))
//			{
//
//			}
//			// if refresh currrent directory
//			else if (cmd.equals(commands[6]))
//			{
//
//			}
//		}
//	}
}
