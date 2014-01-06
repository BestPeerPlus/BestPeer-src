/*
 * @(#) DBTreeView.java 1.0 2006-12-23
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.dbview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import sg.edu.nus.util.Inet;
/**
 * A tree view enables user to explore the data in the database in a <code>DBTree</code>.
 *   
 * @author Huang Yukai
 * @version 1.0 2006-12-23
 */

public class DBTreeView extends JPanel implements
ListSelectionListener 
{
	//private members
	private static final long serialVersionUID = 3609575253483334882L;

	private Component parentComponent;
	
	// the customized tree to show the tables in the connected database.
	private DBTree dbTree;
	private JTable dbTable;
	
	private JPanel statusPane;
	private JPanel contentPane;
	
	private JPanel treePane;
	private JPanel tablePane;
	private JPanel tableTopPane;
	private JPanel tableDownPane;
	
	private JLabel tableNameLabel;
	
	private DBTableModel tableModel;
	//private SharedDataIndexer sharedDataIdx;
	private String sharedidx = "./sharedidx.idx";
	
	private JDialog clientgui;
	
	private JButton saveButton;
	private JButton cancelButton;
	
	/**
	 * Constructor
	 * 
	 * @param parent - the parent container that hold this panel.
	 *                 the parent container must be <code>DatabaseExplorer</code> instance.
	 */
	public DBTreeView(JDialog gui, Component/*DatabaseExplorer*/ parent)
	{
		this.clientgui = gui;
		this.parentComponent = parent;
		this.initPane();
		
		saveButton = new JButton("Save");
		cancelButton = new JButton("Cancel");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(saveButton, BorderLayout.WEST);
		buttonPanel.add(cancelButton, BorderLayout.EAST);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		saveButton.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					dbTree.saveModification();
				}
			});
		
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dbTree.cancelModification();
			}
		});
	}
	
	/* Initialize all the components in the interface.
	 * 
	 * this(JPanel, BorderLayout)
	 *   - contentPane(JPanel, BorderLayout)
	 *   	- treePane(JPanel, BorderLayout)
	 *   		- dbTree(DBTree)
	 *   	- tablePane(JPanel, BorderLayout)
	 *   		- tableTopPane(JPanel, BorderLayout)
	 *   			- closelabel(JLabel, "-Close-")
	 *   			- tabelNameLabel(JLabel, "Tabel:xxxx")
	 *   		- tableDownPane(JPanel, BorderLayout)
	 *   			- dbTabel(DBTabel)
	 *   - statusPane(JPanel, Flowlayout)
	 * 			- connectButton(JButton, "disConnect")
	 */
	private void initPane()
	{
		this.setLayout(new BorderLayout());
			
		treePane = new JPanel(new BorderLayout());	
		
		tablePane = new JPanel(new BorderLayout());	
		
		tableTopPane = new JPanel(new BorderLayout());
		
		JLabel closeLabel = new JLabel("-Close-  ");
		// button event: hide the table panel
		closeLabel.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				DBTreeView.this.hideTable();
			}
			public void mouseEntered(MouseEvent e)
			{
				e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		});
		tableTopPane.add(closeLabel, BorderLayout.EAST);
		tableNameLabel = new JLabel("  Table: ");
		tableTopPane.add(tableNameLabel, BorderLayout.WEST);
		tablePane.add(tableTopPane, BorderLayout.NORTH);
		tableDownPane = new JPanel(new BorderLayout());
		JScrollPane jsp = new JScrollPane(tableDownPane);
		
		tablePane.add(jsp, BorderLayout.CENTER);
		
		Dimension d = jsp.getPreferredSize();
		d.setSize(d.getWidth(), 200);
		jsp.setPreferredSize(d);
		
		
		tablePane.setVisible(false);

		contentPane = new JPanel(new BorderLayout());
		contentPane.add(treePane, BorderLayout.CENTER);
		contentPane.add(tablePane, BorderLayout.SOUTH);
		this.add(contentPane, BorderLayout.CENTER);
		
//		JButton connectButton = new JButton("DisConnect");
//		// button event: disconnect to the database
//		connectButton.addActionListener(new ActionListener()
//				{
//					public void actionPerformed(ActionEvent event)
//					{
//						DBTreeView.this.destroyTree();
//						((DBExplorerPanel)(DBTreeView.this.parentComponent)).isConnected(false);
//					}
//				});
//		statusPane = new JPanel();
//		statusPane.setLayout(new FlowLayout(FlowLayout.TRAILING));
//		statusPane.add(connectButton, BorderLayout.EAST);
//		this.add(statusPane, BorderLayout.SOUTH);
	}
	
	/**
	 * Construct a <code>DBTree</code> with the information: dbmetadata and databaseName
	 * 
	 * @param dbmetadata -  the metadata for the connected database
	 * @param databaseName -  the name of the connected database
	 * @throws SQLException - if a database access error occurs
	 */
	public void showTree(DatabaseMetaData dbmetadata, String databaseName) throws SQLException
	{
//		DefaultMutableTreeNode root = new DefaultMutableTreeNode(databaseName);
		
		dbTree = new DBTree(this);
		dbTree.setupTree(dbmetadata, databaseName);
		
		String namespace = Inet.getInetAddress()+"@"+databaseName; 
		//sharedDataIdx = new SharedDataIndexer(sharedidx, namespace);
		
		JScrollPane scrollPane = new JScrollPane(dbTree);
		treePane.add(scrollPane);
	}
	
	/**
	 * Remove the DBTree from the panel before disconnect to the database
	 *
	 */
	public void destroyTree()
	{
		this.hideTable();
		dbTree.setModel(null);		
		this.treePane.removeAll();
		//this.sharedDataIdx.close();
	}
	
	/**
	 * Show the relational data in the corresponding table
	 * 
	 * @param tableName -  the name of the corresponding table
	 */
	public void showTable(String tableName)
	{
		this.showTableInfo(tableName, true);
	}
	
	/**
	 * Show data or schema of table in the database
	 * 
	 * @param tableName - the name of the table which is to be shown
	 * @param isShowTable - <code>true</code> if show data in table
	 * 					    <code>false</code> if show schema of table
	 */
	public void showTableInfo(String tableName, boolean isShowTable)
	{
		if(dbTable != null) this.hideTable();
		
		String sqlQuery = "select * from " + tableName + ";";
		
//		Connection con = ((DBExplorerPanel)this.getParentComponent()).getConnection();
//		try 
//		{
//			Statement statement = con.createStatement();
//			ResultSet rs = statement.executeQuery(sqlQuery);
//			//tableModel = new DBTableModel(clientgui, rs, isShowTable, sharedDataIdx);
//			tableModel = new DBTableModel(clientgui, rs, isShowTable);
//			dbTable = new JTable(tableModel);
//			dbTable.setBackground(contentPane.getBackground());
//			
//			dbTable.setAutoscrolls(true);
//			dbTable.setAutoCreateColumnsFromModel(true);
//			dbTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//			dbTable.getSelectionModel().addListSelectionListener (this);
//		
//			rs.close();
//			statement.close();
//		} 
//		catch (SQLException e) 
//		{
//			e.printStackTrace();
//		}
		this.tableNameLabel.setText("  Table: " + tableName);
		this.tableDownPane.add(dbTable.getTableHeader(), BorderLayout.NORTH);
		this.tableDownPane.add(dbTable, BorderLayout.CENTER);		
		this.tablePane.setVisible(true);
	}
	
	/**
	 * Hide the table
	 */
	public void hideTable()
	{
		this.tablePane.setVisible(false);
		if(dbTable != null)	this.tableDownPane.removeAll();
	}

	/**
	 * Show the schema of the corresponding table
	 * 
	 * @param tableName - the name of the corresponding table
	 */
	public void showSchema(String tableName)
	{
		this.showTableInfo(tableName, false);
	}
	
	/**
	 * Return the parent container which holds this panel
	 * 
	 * @return the parent container(<code>DatabaseExplorer</code>)
	 */
	public Component getParentComponent()
	{
		return parentComponent;
	}
	
	/**
	 * Return a string: "DBTreeView"
	 * 
	 * @return "DBTreeView"
	 */
	public String toString()
	{
		return "DBTreeView";
	}

	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) {
			int index = e.getFirstIndex();
			//tableModel.setValueAt(!tableModel.shared[index], index, 0);
		}
	}
}

