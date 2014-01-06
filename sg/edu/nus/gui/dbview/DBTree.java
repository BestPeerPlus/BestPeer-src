/*
 * @(#) DBTree.java 1.0 2006-12-26
 * 
 * Copyright 2006, Natioal University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

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

/**
 * This class wraps the <code>JTree</code> to enable the users to explore 
 * the connected database, the root node of the DBTree is the database, children 
 * of the root node are all the tables (TableNode instances), and grandchildren 
 * of the root node are all the schema information of corresponding tables.
 * <p>
 * Also, some customized popup menus of the right click mouse are defined in this class.
 * 
 * @author Huang Yukai
 * @version 1.0 2006-12-26 
 * @see javax.swing.JTree
 */

public class DBTree extends JTree
{
	//private members
	private static final long serialVersionUID = 1458145543006995833L;
	
	private DBTreeMouseListener mouseListener;

	// the parent container which holds this tree
	private Component parentComponent;
	
	private Vector<String> temporalShareInfo;
	
	private String currentDatabaseName = null;
	
	/**
	 * Construct an empty tree on the parent component
	 * 
	 * @param parent -  the parent container which holds this tree
	 */
	public DBTree(Component parent)
	{
		this.parentComponent = parent;
		
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		// add customized TreeCellRenderer
		this.setCellRenderer(new DBTreeCellRenderer());
		
		// add new mouse listener
		mouseListener = new DBTreeMouseListener(this);
		this.addMouseListener(mouseListener);
		
		temporalShareInfo = new Vector<String>();
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
		this.currentDatabaseName = databaseName;
		
		DBTreeNode root = new DBTreeNode(databaseName);
		root.setNodeType("DATABASE");
		
		File sharedInfoFile = new File("./" + databaseName + ".dat");
		
		String shareInfo = new String();
		shareInfo += "DATABASE" + "|" + (String)root.getUserObject() + "\r\n";
		shareInfo += "END" + "\r\n";
		
		ResultSet tables, columns;
		String[] tableTypes = {"TABLE"};
		
		tables = metadata.getTables(null, null, null, tableTypes);
			
		String tableName;
		DBTreeNode tableNode;
		DBTreeNode columnNode;
		// retrieval all the table information from the resultset
		while(tables.next())
		{
			if(sharedInfoFile.exists())
			{
				BufferedReader reader;
				try
				{
					reader = new BufferedReader(new FileReader(sharedInfoFile));
					tableName = tables.getString("TABLE_NAME");
					tableNode = new DBTreeNode(tableName);
					tableNode.setNodeType("TABLE");
					int tableStatus = checkTableStatus(reader, tableName);
					reader.close();
					if(tableStatus == 1)
						tableNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
					root.add(tableNode);
					columns = metadata.getColumns(null, null, tableName, null);
					
					while(columns.next())
					{
						columnNode = new DBTreeNode(columns.getString("COLUMN_NAME"));
						columnNode.setNodeType("COLUMN");
						
						reader = new BufferedReader(new FileReader(sharedInfoFile));	
						
						int columnStatus = checkColumnStatus(reader, tableName, columns.getString("COLUMN_NAME"));
						
						reader.close();
						
						if(columnStatus == 1)
							columnNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
						
						tableNode.add(columnNode);
					}
					
					
					columns.close();				
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				tableName = tables.getString("TABLE_NAME");
				tableNode = new DBTreeNode(tableName);
				tableNode.setNodeType("TABLE");
				root.add(tableNode);
				columns = metadata.getColumns(null, null, tableName, null);
				
				shareInfo += "TABLE" + "|" + tableName + "|" + 0 + "\r\n";
				
				while(columns.next())
				{
					columnNode = new DBTreeNode(columns.getString("COLUMN_NAME"));
					columnNode.setNodeType("COLUMN");
					tableNode.add(columnNode);
					
					shareInfo += "COLUMN" + "|" + tableName + "|" + columns.getString("COLUMN_NAME")
								+ "|" + 0 + "\r\n";					
				}
				
				shareInfo += "END" + "\r\n";
				
				columns.close();				
			}
//			tableName = tables.getString("TABLE_NAME");
//			tableNode = new TableNode(tableName);
//			root.add(tableNode);
//			columns = metadata.getColumns(null, null, tableName, null);
//			
//			shareInfo += "TABLE" + "|" + tableName + "|" + 0 + "\r\n";
//			while(columns.next())
//			{
//				columnNode = new ColumnNode(columns.getString("COLUMN_NAME"));
//				tableNode.add(columnNode);
//				
//				shareInfo += "COLUMN" + "|" + tableName + "|" + columns.getString("COLUMN_NAME")
//							+ "|" + 0 + "\r\n";
//				
//			}
//			columns.close();
		}
		tables.close();
		
		// use the DefaultTreeModel as the model
		DefaultTreeModel model = new DefaultTreeModel(root);
		this.setModel(model);
	
		//Generate sharedInformation
		BufferedWriter writer = null;
		
		if(!sharedInfoFile.exists())
		{
			try
			{
				writer = new BufferedWriter(new FileWriter(sharedInfoFile));
				writer.write(shareInfo);
				writer.flush();
				writer.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}		
	}
	
	private int checkTableStatus(BufferedReader reader, String tableName)
	{
		int shared = -1;
		
		try
		{	
			String line;
		
			while((line = reader.readLine()) != null)
			{
				String[] str = line.split("\\|");
				if((str[0].equals("TABLE")) &&(str[1].equals(tableName)))
				{
					shared = Integer.parseInt(str[2]);
					break;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return shared;
	}
	
	private int checkColumnStatus(BufferedReader reader, String tableName, String columnName)
	{
		int shared = -1;
		
		try
		{	
			String line;
		
			while((line = reader.readLine()) != null)
			{
				String[] str = line.split("\\|");
				if((str[0].equals("COLUMN")) &&(str[1].equals(tableName)) && (str[2].equals(columnName)))
				{
					shared = Integer.parseInt(str[3]);
					break;
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return shared;
	}
	
	public void ModifyShareTableInfo(String opType, String tableName)
	{
		try
		{
			String filename = this.currentDatabaseName;
			
			BufferedReader reader = new BufferedReader(new FileReader("./" + filename + ".dat"));
			BufferedWriter writer = new BufferedWriter(new FileWriter("./temporal.dat"));
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] arrData = line.split("\\|");
				if(arrData[0].equals("TABLE") && arrData[1].equals(tableName))
				{
					if(opType.equals("SHARE"))
						writer.write(arrData[0] + "|" + arrData[1]+ "|" + 1 + "\r\n");
					else
						writer.write(arrData[0] + "|" + arrData[1]+ "|" + 0 + "\r\n");					
				}
				else
					writer.write(line + "\r\n");
			}
			
			reader.close();
			writer.close();
			
			File file = new File("./" + filename + ".dat");
			file.delete();
			File file2 = new File("./temporal.dat");
			file2.renameTo(new File("./" + filename + ".dat"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void ModifyShareColumnInfo(String opType, String str)
	{
		String[] arr = str.split("\\|");
		String tableName = arr[0];
		String columnName = arr[1];
		
		try
		{
			String filename = this.currentDatabaseName;
			
			BufferedReader reader = new BufferedReader(new FileReader("./" + filename + ".dat"));
			BufferedWriter writer = new BufferedWriter(new FileWriter("./temporal.dat"));
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] arrData = line.split("\\|");
				if(arrData[0].equals("COLUMN") && arrData[1].equals(tableName) 
						&& (arrData[2].equals(columnName)))
				{
					if(opType.equals("SHARE"))
						writer.write(arrData[0] + "|" + arrData[1]+ "|" + arrData[2] + "|" + 1 + "\r\n");
					else
						writer.write(arrData[0] + "|" + arrData[1]+ "|" + arrData[2] + "|" + 0 + "\r\n");				
				}
				else
					writer.write(line + "\r\n");
			}
			
			reader.close();
			writer.close();
			
			File file = new File("./" + filename + ".dat");
			file.delete();
			File file2 = new File("./temporal.dat");
			file2.renameTo(new File("./" + filename + ".dat"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	public DBTreeNode FindTableNode(DBTreeNode root, String tableName)
	{
		DBTreeNode tNode = null;
		
		int tableCount = root.getChildCount();
		for(int j = 0; j < tableCount; j++)
		{
			DBTreeNode currentNode = (DBTreeNode)root.getChildAt(j);
			
			if(currentNode.getSourceSchemaName().equals(tableName))
			{
				tNode = currentNode;
				break;
			}
		}
		
		return tNode;
	}
	
	public DBTreeNode FindColumnNode(DBTreeNode tNode, String columnName)
	{
		DBTreeNode cNode = null;
		
		int ColumnCount = tNode.getChildCount();
		for(int z = 0; z < ColumnCount; z++)
		{
			DBTreeNode currentNode = (DBTreeNode)tNode.getChildAt(z);
			
			if(currentNode.getSourceSchemaName().equals(columnName))
			{
				cNode = currentNode;
				break;
			}
		}
		
		return cNode;
	}	
	
	public void saveModification()
	{
		for(int i = 0; i < temporalShareInfo.size(); i++)
		{						
			String info = temporalShareInfo.get(i);
			
			String[] arrData = info.split("\\|");
			
			DBTreeNode root = (DBTreeNode)this.getModel().getRoot();			
			
			if(arrData[0].equals("SHARE"))
			{
				if(arrData[1].equals("TABLE"))
				{	
					DBTreeNode tNode = FindTableNode(root, arrData[2]);
					
					if(tNode == null)
						return;
					
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
					
					ModifyShareTableInfo(arrData[0], arrData[2]);
				}
				else
				{
					DBTreeNode tNode = FindTableNode(root, arrData[2]);
					
					if(tNode == null)
						return;
					
					DBTreeNode cNode = FindColumnNode(tNode, arrData[3]);
					
					if(cNode == null)
						return;
					
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
					
					ModifyShareColumnInfo(arrData[0],arrData[2] + "|" + arrData[3]);
				}
			}
			else
			{
				if(arrData[1].equals("TABLE"))
				{
					DBTreeNode tNode = FindTableNode(root, arrData[2]);
					
					if(tNode == null)
						return;
					
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);				
					
					ModifyShareTableInfo(arrData[0], arrData[2]);
				}
				else
				{
					DBTreeNode tNode = FindTableNode(root, arrData[2]);
					
					if(tNode == null)
						return;
					
					DBTreeNode cNode = FindColumnNode(tNode, arrData[3]);
					
					if(cNode == null)
						return;
					
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);
					
					ModifyShareColumnInfo(arrData[0], arrData[2] + "|" + arrData[3]);				
				}
			}
		}
		
		this.temporalShareInfo.clear();
		
		this.parentComponent.repaint();
	}
	
	public void cancelModification()
	{
		DBTreeNode root = (DBTreeNode)this.getModel().getRoot();
		
		for(int i = temporalShareInfo.size() - 1; i >=0 ; i--)
		{
			String info = temporalShareInfo.get(i);
			
			String[] arrData = info.split("\\|");
			
			String nodeType = arrData[1];
			String opType = arrData[0];
			String tableName = arrData[2];
			
			DBTreeNode tNode = null;
			
			int tableCount = root.getChildCount();
			for(int j = 0; j < tableCount; j++)
			{
				DBTreeNode currentNode = (DBTreeNode)root.getChildAt(j);
				
				if(currentNode.getSourceSchemaName().equals(tableName))
				{
					tNode = currentNode;
					break;
				}
			}
			
			if(tNode == null)
				break;
			
			if(nodeType.equals("TABLE"))
			{
				if(opType.equals("SHARE"))
				{
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);
				}
				else 
				{
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
				}
				
			}
			else
			{
				String columnName = arrData[3];
				
				DBTreeNode cNode = null;
				
				int ColumnCount = tNode.getChildCount();
				for(int z = 0; z < ColumnCount; z++)
				{
					DBTreeNode currentNode = (DBTreeNode)tNode.getChildAt(z);
					
					if(currentNode.getSourceSchemaName().equals(columnName))
					{
						cNode = currentNode;
						break;
					}
				}
				
				if(cNode == null)
					break;				
				
				if(opType.equals("SHARE"))
				{
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_UNSHARE);
				}
				else 
				{
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
				}				
			}
		}
		
		this.temporalShareInfo.clear();
		
		this.parentComponent.repaint();
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
			
		new DBTreePopupMenu().show(c, x, y);
	}

	/**
	 * Create a <code>MouseListener</code> for <code>DBTree</code>.
	 * 
	 * @author Huang Yukai
	 * @version 1.0 2006-12-26
	 */
	
	class ModifiedShareInfo
	{
		private String nodeType;
		private String shareInfo;
		private TreePath treePath;
		
		public ModifiedShareInfo()
		{
			
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

		/**
		 * @return the shareInfo
		 */
		public String getShareInfo() {
			return shareInfo;
		}

		/**
		 * @param shareInfo the shareInfo to set
		 */
		public void setShareInfo(String shareInfo) {
			this.shareInfo = shareInfo;
		}

		/**
		 * @return the treePath
		 */
		public TreePath getTreePath() {
			return treePath;
		}

		/**
		 * @param treePath the treePath to set
		 */
		public void setTreePath(TreePath treePath) {
			this.treePath = treePath;
		}
		
		
	}
	
	final class DBTreeMouseListener implements MouseListener
	{
		//private members
		private DBTree tree;
		
		/**
		 * Constructor.
		 * 
		 * @param tree the handler of <code>DBTree</code>
		 */
		public DBTreeMouseListener(DBTree tree)
		{
			this.tree = tree;
		}
		
	    /**
	     * Invoked when the mouse has been clicked on a component.
	     */
	    public void mouseClicked(MouseEvent e) 
	    {
	   		this.showPopupMenu(e);
	    }
	
	    /**
	     * Invoked when a mouse button has been pressed on a component.
	     */
	    public void mousePressed(MouseEvent e) 
	    {
	    	this.showPopupMenu(e);
	    }
	
	    /**
	     * Invoked when a mouse button has been released on a component.
	     */
	    public void mouseReleased(MouseEvent e) 
	    {
	    	this.showPopupMenu(e);
	    }
	
	    /**
	     * Invoked when the mouse enters a component.
	     */
	    public void mouseEntered(MouseEvent e) {}
	
	    /**
	     * Invoked when the mouse exits a component.
	     */
	    public void mouseExited(MouseEvent e) {}
	    
	    // Show the popup menu.
	    // @see DBTreePopupMenu
	    private void showPopupMenu(MouseEvent e)
	    {
	    	if(e.isPopupTrigger())
	    	{
	    		tree.showPopupMenu(e.getComponent(), e.getX(), e.getY());
	    	}
	    	
	    }
	}
	
	// ----------- used for popup menu --------------
	
	/**
	 * Construct a popup menu for <code>DBTree</code>.
	 * 
	 * @author Huang Yukai
	 * @version 1.0 2006-12-26
	 * @see javax.swing.JPopupMenu
	 */
	
	final class DBTreePopupMenu extends JPopupMenu implements ActionListener
	{
		// private members
		private static final long serialVersionUID = 6764829618615422807L;

		// menu items
		private JMenuItem openMenuItem;
		private JMenuItem schemaMenuItem;
		private JMenuItem searchMenuItem;
		private JMenuItem shareMenuItem;
		private JMenuItem unshareMenuItem;
		private JMenuItem reshareMenuItem;
		private JMenuItem refreshMenuItem;	
		private JMenuItem schemaMappingMenuItem;
		
		private DefaultMutableTreeNode node;
	
		private String[] itemName = 
		{
				"Open Table", "View Schema", "Search...", "Share", "Unshare", "Reshare", "Refresh", "SchemaMapping"
		};
		
		private String[] commands = 
		{
				"open", "schema", "search", "share", "unshare", "reshare", "refresh", "SchemaMapping"
		};
		
		private String[] images =
		{
				"openTable", "schema", "empty", "share", "unshare", "reshare", "refresh", "SchemaMapping"
		};
	
		/**
		 * Constructor
		 *
		 */
		public DBTreePopupMenu()
		{
			this.makeMenu();
		}
	
		/*
		 * Construct the menu under different situations.
		 */
		private void makeMenu()
		{
			
			node = (DefaultMutableTreeNode)DBTree.this.getSelectionPath().getLastPathComponent();
			
//			if(!node.toString().equals("TableNode")){
//				return;
//			}
			
			int offset = -14;
			
			openMenuItem = this.makeMenuItem(itemName[0], commands[0], images[0]);
			openMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
			
			schemaMenuItem = this.makeMenuItem(itemName[1], commands[1], images[1]);
			schemaMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
	
			searchMenuItem = this.makeMenuItem(itemName[2], commands[2], images[2]);
			searchMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
	
			shareMenuItem = this.makeMenuItem(itemName[3], commands[3], images[3]);
			shareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
	
			unshareMenuItem = this.makeMenuItem(itemName[4], commands[4], images[4]);
			unshareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
	
			reshareMenuItem = this.makeMenuItem(itemName[5], commands[5], images[5]);
			reshareMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
	
			refreshMenuItem = this.makeMenuItem(itemName[6], commands[6], images[6]);
			refreshMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
	
			schemaMappingMenuItem = this.makeMenuItem(itemName[7], commands[7], images[7]);
			schemaMappingMenuItem.setBorder(BorderFactory.createEmptyBorder(2, offset, 2, 0));
			
//			this.add(openMenuItem);
//			this.add(schemaMenuItem);
			// Only click on the table node, then show "open table" and "view schema" functions

//			if(!node.toString().equals("TableNode"))
//			{
//				openMenuItem.setEnabled(false);
//				schemaMenuItem.setEnabled(false);				
//			}

//			this.addSeparator();
//			this.add(searchMenuItem);
//			this.addSeparator();
			this.add(shareMenuItem);
			shareMenuItem.setEnabled(true);
			this.add(unshareMenuItem);
			unshareMenuItem.setEnabled(true);
//			this.add(reshareMenuItem);
//			this.addSeparator();
//			this.add(schemaMappingMenuItem);
						
			// Only after connecting to the PeerDB system, then show "Share/Unshare/Reshare" functions
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
			this.addSeparator();
			this.add(refreshMenuItem);
			
		}
		
		/**
		 * Make individual menu item.
		 * 
		 * @param name - the menu item name
		 * @param cmd - the command string
		 * @param img - the image name
		 * 
		 * @return the instance of <code>JMenuItem</code>
		 */
		private JMenuItem makeMenuItem(String name, String cmd, String img)
		{ 
			/* look for the image */
	        String imageLoc = AbstractMainFrame.SRC_PATH + img + ".png";
	
	        JMenuItem menuItem = new JMenuItem(name);
			menuItem.setActionCommand(cmd);
	        menuItem.addActionListener(this);
	
	        try 
	        {	//image found
	            menuItem.setIcon(new ImageIcon(imageLoc, name));
	        } 
	        catch (Exception e) 
	        {   //no image found
	        	System.err.println("Miss such image: " + imageLoc);
	        }
	
	        return menuItem;
		}
		
		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent event)
		{
			String cmd = event.getActionCommand();
			
			// if view the data in the selected table
			if(cmd == commands[0]) 
			{
//				String tableName = ((TableNode)node).getName();
//				((DBTreeView)DBTree.this.parentComponent).showTable(tableName);
			}
			// if view the schema of selected table
			else if(cmd.equals(commands[1]))
			{
//				String tableName = ((TableNode)node).getName();
//				((DBTreeView)DBTree.this.parentComponent).showSchema(tableName);
			}
			// if search local resource and other peers
			else if (cmd.equals(commands[2]))
			{
				//new SearchDialog((ClientGUI)((DatabaseExplorer)((DBTreeView)DBTree.this.parentComponent).getParentComponent()).getParentComponent());
			}
			// if share a file or directory
			/* If shared a table or column, then change the color of shared table and column */
			//Added by Han Xixian, 2008-6-3
			else if (cmd.equals(commands[3]))
			{
				Vector<String> vec = DBTree.this.getTemporalShareInfo();
				
				DBTreeNode dbNode = (DBTreeNode)node;
				
				if(dbNode.getNodeType().equals("TABLE"))
				{	
					DBTreeNode tNode = dbNode;
					
					if(tNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
						return;
						
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);
						
					vec.add("SHARE|TABLE|" + tNode.getSourceSchemaName());						
						
					int childCount = tNode.getChildCount();
					for(int i = 0; i < childCount; i++)
					{
						DBTreeNode cNode = (DBTreeNode)tNode.getChildAt(i);
						cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);
						
						vec.add("SHARE|COLUMN|" + tNode.getSourceSchemaName() + "|" 
								+ cNode.getSourceSchemaName());							
					}						
				}
				else
				{						
					DBTreeNode cNode = dbNode;
					
					if(cNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE)
						return;
						
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPSHARE);				
					DBTreeNode tNode = (DBTreeNode)(cNode.getParent());
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_SHARE);
						
					vec.add("SHARE|TABLE|" + tNode.getSourceSchemaName());
					vec.add("SHARE|COLUMN|" + tNode.getSourceSchemaName() + "|" 
							+ cNode.getSourceSchemaName());	
				}				
					
				parentComponent.repaint();					
			}
			
			// if unshare a file or directory
			/* If unshared a table or column, then change the color of unshared table and column */
			//Added by Han Xixian, 2008-6-3
			else if (cmd.equals(commands[4]))
			{
				//if a shared table is changed to be unshared, then all shared column should be unshared also.
				//Added by Han Xixian
				//Date: 2008-6-3
				Vector<String> vec = DBTree.this.getTemporalShareInfo();
				
				DBTreeNode dbNode = (DBTreeNode)node;
				
				if(dbNode.getNodeType().equals("TABLE"))
				{						
					DBTreeNode tNode = dbNode;
					
					if(tNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE)
						return;
					
					tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);
					
					vec.add("UNSHARE|TABLE|" + tNode.getSourceSchemaName());	
						
					int childCount = tNode.getChildCount();
					for(int i = 0; i < childCount; i++)
					{			
						DBTreeNode cNode = (DBTreeNode)tNode.getChildAt(i);
						
//						if(cNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPUNSHARE)
//							break;
						
						if(cNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE)
							continue;
						
						cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);
						
						vec.add("UNSHARE|COLUMN|" + tNode.getSourceSchemaName() + "|" 
								+ cNode.getSourceSchemaName());							
					}						
				}
				else
				{
					DBTreeNode cNode = dbNode;	
					
					if(cNode.getShareStatus() == DBTreeNode.SHARESTATUS_UNSHARE)
						return;
					
					cNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);
					
					DBTreeNode tNode = (DBTreeNode)(cNode.getParent());
					
					vec.add("UNSHARE|COLUMN|"+ tNode.getSourceSchemaName() + "|" + cNode.getSourceSchemaName());
					
					int childCount = tNode.getChildCount();
					
					boolean shareTableNode = false;
					
					for(int i = 0; i < childCount; i++)
					{			
						DBTreeNode ctNode = (DBTreeNode)tNode.getChildAt(i);
						
						if((ctNode.getShareStatus() == DBTreeNode.SHARESTATUS_SHARE) 
								|| (ctNode.getShareStatus() == DBTreeNode.SHARESTATUS_TEMPSHARE))
						{
							shareTableNode = true;
							break;
						}						
					}						
					
					if(shareTableNode == false)
					{
						tNode.setShareStatus(DBTreeNode.SHARESTATUS_TEMPUNSHARE);
						vec.add("UNSHARE|TABLE|" + tNode.getSourceSchemaName());
					}
				}
			
				parentComponent.repaint();						
					
			}
			// if update index
			else if (cmd.equals(commands[5]))
			{

			}
			// if refresh currrent directory
			else if (cmd.equals(commands[6]))
			{

			}
			else if (cmd.equals(commands[7]))
			{

			}
				
		}
	}

	/**
	 * @return the temporalShareInfo
	 */
	public Vector<String> getTemporalShareInfo() {
		return temporalShareInfo;
	}

	/**
	 * @param temporalShareInfo the temporalShareInfo to set
	 */
	public void setTemporalShareInfo(Vector<String> temporalShareInfo) {
		this.temporalShareInfo = temporalShareInfo;
	}

	/**
	 * @return the currentDatabaseName
	 */
	public String getCurrentDatabaseName() {
		return currentDatabaseName;
	}

	/**
	 * @param currentDatabaseName the currentDatabaseName to set
	 */
	public void setCurrentDatabaseName(String currentDatabaseName) {
		this.currentDatabaseName = currentDatabaseName;
	}
}
