/*
 * @(#) Pane.java 1.0 2005-12-30
 *
 * Copyright 2005, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui.bootstrap;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import sg.edu.nus.gui.*;
import sg.edu.nus.gui.table.*;
import sg.edu.nus.logging.LogEvent;
import sg.edu.nus.peer.*;
import sg.edu.nus.peer.management.*;
import sg.edu.nus.peer.info.*;

/**
 * Implement the content pane of the bootstrap server.
 * 
 * @author Xu Linhao
 * @version 1.0 2005-12-30
 * 
 * @author modified by Han Xixian
 * @version 2008-6-3
 */

public final class Pane extends JTabbedPane implements ActionListener
{

	// private members
	private static final long serialVersionUID = 6850720965924853476L;

	/* the handler of the main frame */
	private BootstrapGUI bootstrapGUI;
	
	private JButton endButton;
	private JButton clearButton;
	private JButton propButton;
	//private JButton distributeButton;
	//private JButton testNetworkButton;
	
	private SortedTable peerTable;
	private SortedTable eventTable;
	//private SortedTable globalSchemaTable;
	
	//VHTam
	JPanel schemaPanel =null;
	//VHtam
	
	private SortedTableModel peerTableModel;
	private SortedTableModel eventTableModel;
	//private SortedTableModel globalSchemaTableModel;
	
	/* TableSorter is a wrapper of TableModel. 
	 * When a table is in sorting status,
	 * only using the method getValueAt(row, col)
	 * will return the correct Objects. This is 
	 * because the TableSorter wraps the position
	 * of all rows and its getValueAt(row, col) 
	 * provides corresponding position transformation
	 * operation. */
	private TableSorter peerTableSorter;
	private TableSorter eventTableSorter;
	//private TableSorter globalSchemaTableSorter;
	
	private TableMouseListener tableMouseListener = new TableMouseListener();
	private TableListSelectionListener tableListSelectionListener = new TableListSelectionListener();
	
	private String[] btnName = {LanguageLoader.getProperty("button.kill"), LanguageLoader.getProperty("button.clear"), LanguageLoader.getProperty("button.property"), 
			LanguageLoader.getProperty("button.distributeGlobalSchema"), LanguageLoader.getProperty("button.TestNetworkStatus")};
	private String[] command = {"end", "clear", "show", "distribute", "testNetwork"};
	private String[] imgName = {"peer", "event","globalSchema"};
	
	boolean webStatus = false;
	boolean MySQLStatus = false;
	boolean superpeerStatus = false;
	
	/**
	 * Init the content pane.
	 * 
	 * @param bootstrap the handler of the main frame
	 */
	public Pane(BootstrapGUI bootstrap)
	{
		this.bootstrapGUI = bootstrap;
		
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		String imgLoc = AbstractMainFrame.SRC_PATH;
		
		JPanel panel = null;
		
		panel = this.makePeerPane();
		this.addTab(LanguageLoader.getProperty("tab.peer"), new ImageIcon(imgLoc + imgName[0] + ".png"), panel);
		
		panel = this.makeEventPane();
		this.addTab(LanguageLoader.getProperty("tab.event"), new ImageIcon(imgLoc + imgName[1] + ".png"), panel);

		panel = this.makeGlobalSchemaPane();
		this.addTab(LanguageLoader.getProperty("tab.globalSchema"), new ImageIcon(imgLoc + imgName[2] + ".png"), panel);

		// create pane for local admin managements
		panel = this.makePeerAccountPane();
		this.addTab("Peer Local Admins", new ImageIcon(imgLoc + "login.png"), panel);

		
		this.setSelectedIndex(0);
	}
	
	/**
	 * Make the peer panel.
	 * 
	 * @return the peer panel
	 */
	private JPanel makePeerPane()
	{
		JPanel peerPanel = new JPanel(new BorderLayout());
		
		/* make table */
		String[] header = { LanguageLoader.getProperty("table.id"),	LanguageLoader.getProperty("table.type"), 
				LanguageLoader.getProperty("table.ip"), LanguageLoader.getProperty("table.port") };
		int[] width = { 100, 100, 160, 80 };
		
		peerTableModel = new SortedTableModel(header);
		peerTableSorter = new TableSorter(peerTableModel);
		peerTable = this.makeTable(peerTableSorter, width);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(new JScrollPane(peerTable), BorderLayout.CENTER);
		peerPanel.add(panel, BorderLayout.CENTER);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		panel.add(endButton = this.makeButton(btnName[0], command[0]), BorderLayout.EAST);
		endButton.setEnabled(false);
		peerPanel.add(panel, BorderLayout.SOUTH);
		
		return peerPanel;
	}

	private JPanel makePeerAccountPane()
	{
		JPanel peerAccountPanel = new JPanel(new BorderLayout());
		
		//LocalAdminTreePanel LocalAdminPanel = new LocalAdminTreePanel(bootstrapGUI);
		//peerAccountPanel.add(LocalAdminPanel, BorderLayout.CENTER);
		
		return peerAccountPanel;
	}
	
	private JPanel makeGlobalSchemaPane()
	{
		JPanel globalSchemaPanel = new JPanel(new BorderLayout());
		
//		String[] header = { LanguageLoader.getProperty("table.task"),	LanguageLoader.getProperty("table.status"),
//				 	LanguageLoader.getProperty("table.none")};
//		int[] width = { 200, 100, 300 };
//		
//		globalSchemaTableModel = new SortedTableModel(header);
//		globalSchemaTableSorter = new TableSorter(globalSchemaTableModel);
//		globalSchemaTable = this.makeTable(globalSchemaTableSorter, width);
//		
//		JPanel panel = new JPanel(new BorderLayout());
//		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//		panel.add(new JScrollPane(globalSchemaTable), BorderLayout.CENTER);
//		globalSchemaPanel.add(panel, BorderLayout.CENTER);
//		
//		panel = new JPanel(new BorderLayout());
//		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
//		panel.add(distributeButton = this.makeButton(btnName[3], command[3]), BorderLayout.EAST);
//		distributeButton.setEnabled(false);
//		
//		panel.add(testNetworkButton = this.makeButton(btnName[4], command[4]), BorderLayout.WEST);
//		testNetworkButton.setEnabled(false);	
//
//		globalSchemaPanel.add(panel, BorderLayout.SOUTH);
		
		schemaPanel = new JPanel();
		
		globalSchemaPanel.add(schemaPanel, BorderLayout.CENTER);
		
		return globalSchemaPanel;
	}
	
	/**
	 * Make the event panel.
	 * 
	 * @return the event panel
	 */
	private JPanel makeEventPane()
	{
		JPanel eventPanel = new JPanel(new BorderLayout());

		/* make table */
		String[] header = {
				LanguageLoader.getProperty("table.type"), LanguageLoader.getProperty("table.date"), 
				LanguageLoader.getProperty("table.time"), LanguageLoader.getProperty("table.desp"), 
				LanguageLoader.getProperty("table.host"), LanguageLoader.getProperty("table.ip"), 
				LanguageLoader.getProperty("table.user"),
		};

		eventTableModel = new SortedTableModel(header);
		eventTableSorter = new TableSorter(eventTableModel);
		eventTable = this.makeTable(eventTableSorter, AbstractMainFrame.EVENT_TABLE_WIDTH);
		
		/*
		 * I AM NOT SURE WHY THE FOLLOWING CODE CANNOT WORK!!! 
		 * -----------------
		 * logerTable.setDefaultRenderer(JLabel.class, new LogerTableCellRenderer());
		 * -----------------
		 * 
		 * SO, I USE A VERY STUPID METHOD TO ACHIEVE MY PURPOSE.
		 * NOTE THAT: THIS METHOD MAY CAUSE UNEXPECTED ERROR!
		 */
		TableColumn column = null;
		for (int i = 0; i < eventTable.getColumnCount(); i++)
		{
			column = eventTable.getColumnModel().getColumn(i);
			column.setCellRenderer(new EventTableCellRenderer());
		}

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(new JScrollPane(eventTable), BorderLayout.CENTER);
		eventPanel.add(panel, BorderLayout.CENTER);
		
        /* lay out the buttons from left to right */
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(Box.createHorizontalGlue());

		clearButton = this.makeButton(btnName[1], command[1]);
        panel.add(clearButton);

        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        propButton  = this.makeButton(btnName[2], command[2]);
        propButton.setEnabled(false);
        panel.add(propButton);

        eventPanel.add(panel, BorderLayout.SOUTH);
 		return eventPanel;
	}
	
	/**
	 * Make the individual table with specified table model.
	 * 
	 * @param sorter the tabel sorter used for constructing the table
	 * @param width the width of each column
	 * @return the table
	 */
	private SortedTable makeTable(TableSorter sorter, int[] width)
	{
		SortedTable table = new SortedTable(sorter, width);
		
		sorter.setTableHeader(table.getTableHeader());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// add event listener
		table.addMouseListener(tableMouseListener);
		table.getTableHeader().addMouseListener(tableMouseListener);
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(tableListSelectionListener);

        return table;
	}
	
	/**
	 * Make individual button.
	 * 
	 * @param name the button name
	 * @param cmd the command string
	 * @return the instance of <code>JButton</code>
	 */
	private JButton makeButton(String name, String cmd)
	{ 
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
        button.addActionListener(this);
        return button;
	}
	
	/**
	 * Get Schema Tree Panel.
	 * 
	 * @return the instance of the Schema Tree Panel
	 */
	public JPanel getSchemaTreePane()
	{
		return schemaPanel;
	}
	
	/**
	 * Get the row count of the table. 
	 * 
	 * @return the number of the table
	 */
	public synchronized int getPeerTableRowCount()
	{
		return peerTableModel.getRowCount();
	}
	
	/**
	 * Insert a row into the table.
	 * 
	 * @param row the row that the data to be inserted
	 * @param data the data to be inserted
	 */
	public synchronized void firePeerTableRowInserted(int row, Object[] data)
	{
		peerTableModel.insertRow(row, data);
	}
	
	/**
	 * Remove a row from the table.
	 * 
	 * @param row the row to be removed
	 */
	public synchronized void firePeerTableRowRemoved(int row)
	{
		peerTableModel.removeRow(row);
	}
	
	/**
	 * Remove a row from the table with specified data value.
	 * 
	 * @param info the information of the peer
	 */
	public synchronized void firePeerTableRowRemoved(PeerInfo info)
	{
		Object obj;
		Object[] target = info.toObjectArray();
		boolean found = false;
		
		int row = peerTableModel.getRowCount();
		int col = peerTableModel.getColumnCount();
		int i;
		for (i = row - 1; i >= 0; i--)
		{
			found = true;
			for (int j = 0; j < col; j++)
			{
				obj = peerTableModel.getValueAt(i, j);
				if (!obj.equals(target[j]))
				{
					found = false;
					break;
				}
			}
			if (found) break;
		}
		if (found)
			peerTableModel.removeRow(i);
	}

	/**
	 * Test Network Status to distribute global schema to SuperPeers. 
	 * 
	 * @return the result of Test (true or false)
	 */
	public boolean testNetworkStatus()
	{
		return webStatus && MySQLStatus && superpeerStatus;
	}	
	
	/**
	 * Set Network Status. 
	 * 
	 * @param webStatus the status of Web
	 * @param MySQLStatus the status of MySQL
	 * @param superpeerStatus the status of superpeerStatus
	 */
	public void setNetworkStatus(boolean webStatus, boolean MySQLStatus, boolean superpeerStatus)
	{
		this.webStatus = webStatus;
		this.MySQLStatus = MySQLStatus;
		this.superpeerStatus = superpeerStatus;
	}	
	
	/**
	 * Enables or disables testNetworkStatus button, depending on the value of the parameter b
	 * 
	 * @param b If true, this component is enabled; otherwise this component is disabled
	 */
	public void setEnableTestNetworkStatus(boolean b)
	{
		//this.testNetworkButton.setEnabled(b);
	}
	
	/**
	 * Enables or disables distributeGlobalSchema button, depending on the value of the parameter b
	 * 
	 * @param b If true, this component is enabled; otherwise this component is disabled
	 */
	public void setEnableDistributeGlobalSchema(boolean b)
	{
		//this.distributeButton.setEnabled(b);
	}

	/**
	 * Clear all content in globalSchema Table
	 * 
	 */
	public void clearGlobalSchemaTable()
	{
//		while(globalSchemaTableModel.getRowCount()!=0)
//			globalSchemaTableModel.removeRow(0);	
	}
	
	public void actionPerformed(ActionEvent event) 
	{
		String cmd = event.getActionCommand();
		
		/* if end peers */
		if (cmd.equals(command[0]))
		{
			int rowIndex = peerTable.getSelectedRow();
			if (rowIndex != -1)
			{
				String ipaddr = (String) peerTableModel.getValueAt(rowIndex, 2);
				Integer port  = (Integer) peerTableModel.getValueAt(rowIndex, 3);
				((Bootstrap) bootstrapGUI.peer()).forceOut(ipaddr.trim(), port.intValue());
				this.removeOnlinePeer(rowIndex);
			}
		}
		/* if clear event */
		else if (cmd.equals(command[1]))
		{
			for (int i = eventTableModel.getRowCount() - 1; i >= 0; i--)
				eventTableModel.removeRow(i);
			
			if (eventDialog != null)
			{
				eventDialog.dispose();
				eventDialog = null;
			}
		}
		/* if show property */
		else if (cmd.equals(command[2]))
		{
			if (eventTable.getSelectedRow() != -1)
			{
				if (eventDialog == null)
				{
					eventDialog = new EventDialog(bootstrapGUI, false);
					eventDialog.setLogerEvent(getLogEvent());
				}
				else
				{
					eventDialog.setLogerEvent(getLogEvent());
					eventDialog.setVisible(true);
				}
			}
		}
	}
	
	/**
	 * Remove all online peers.
	 */
	public synchronized void removeOnlinePeers()
	{
		int size = peerTableModel.getRowCount();
		for (int i = size - 1; i >= 0; i--)
			peerTableModel.removeRow(i);
		PeerMaintainer.getInstance().removeAll();
	}
	
	/**
	 * Remove a row from the table that stores all online peers.
	 * 
	 * @param row the row to be removed
	 */
	public synchronized void removeOnlinePeer(int row)
	{
		peerTableModel.removeRow(row);
	}
	
	// ------------ for event table bellow -----------------
	
	private void showEventPopupMenu(Component c, int x, int y)
	{
		new EventPopupMenu().show(c, x, y);
	}
	
	/**
	 * Add <code>LogEvent</code> to the event panel.
	 * 
	 * @param event the <code>LogEvent</code> to be added to the event panel
	 */
	public synchronized void log(LogEvent event)
	{
		eventTableModel.insertRow(eventTableModel.getRowCount(), event.toArray());
		eventTableModel.fireTableDataChanged();
	}
	
	/**
	 * Returns the String representation of <code>LogEvent</code>.
	 * 
	 * @return the String representation of <code>LogEvent</code>
	 */
	public synchronized String[] getLogEvent() throws IndexOutOfBoundsException
	{
		if (LogEvent.EVENT_ITEMS != eventTable.getColumnCount())
		{
			throw new IndexOutOfBoundsException("The number of table column does not match LogEvent");
		}
		
		String[] result = new String[LogEvent.EVENT_ITEMS];
		
		int rowIndex = eventTable.getSelectedRow();
		if (rowIndex != -1)
		{
			Object value = null;
			int columnCount = eventTable.getColumnCount();
			for (int i = 0; i < columnCount; i++)
			{
				value = eventTableSorter.getValueAt(rowIndex, i);
				if (value instanceof JLabel)
				{
					result[i] = ((JLabel) value).getText();
				}
				else
				{
					result[i] = (String) value;
				}
			}
			propButton.setEnabled(true);
		}
		
		return result;
	}
	
	/**
	 * Remove all log events.
	 */
	public synchronized void removeLogEvents()
	{
		int size = eventTableModel.getRowCount();
		for (int i = size -1; i >= 0; i--)
			eventTableModel.removeRow(i);
	}
	
	// ------------ list handler for table --------------
	
	// used by TableListSelectionListener, TableMouseListener and LogerPopupMenu
	private EventDialog eventDialog;	
	
	final class TableListSelectionListener implements ListSelectionListener
	{

		public void valueChanged(ListSelectionEvent event) 
		{
			ListSelectionModel lsm = (ListSelectionModel)event.getSource();
			if (!lsm.isSelectionEmpty())
			{
				if (eventDialog != null)
				{
					eventDialog.setLogerEvent(getLogEvent());
				}
			}
		}
		
	}
	
	// ------------ mouse handler for queue and loger ------------------
	
	final class TableMouseListener extends MouseAdapter
	{
		
		public void mouseClicked(MouseEvent event) 
		{
			Object src = event.getSource();
			if (src == eventTable)
			{
				if ((event.getButton() == MouseEvent.BUTTON1) && (event.getClickCount() == 2))
				{
					if (eventDialog == null)
					{
						eventDialog = new EventDialog(bootstrapGUI, false);
						eventDialog.setLogerEvent(getLogEvent());
					}
					else
					{
						eventDialog.setLogerEvent(getLogEvent());
						eventDialog.setVisible(true);
						eventDialog.repaint();
					}
				}
			}
			else if (src == eventTable.getTableHeader())
			{
				propButton.setEnabled(false);
			}
			else if (src == peerTable)
			{
				int rowIndex = 0;
				int rowCount = peerTable.getRowCount();
				if (rowCount > 0)
				{
					rowIndex = peerTable.rowAtPoint(new Point(event.getX(), event.getY()));
					peerTable.setRowSelectionInterval(rowIndex, rowIndex);
					endButton.setEnabled(true);
				}
			}
			else if (src == peerTable.getTableHeader())
			{
				endButton.setEnabled(false);
			}
		}
		
		public void mousePressed(MouseEvent event)
		{
			Object src = event.getSource();
			int rowIndex = 0;
			int rowCount = 0;
			if (src == eventTable)
			{
				rowCount = eventTable.getRowCount();
				if (rowCount > 0)	// if contain data
				{
					rowIndex = eventTable.rowAtPoint(new Point(event.getX(), event.getY()));
					eventTable.setRowSelectionInterval(rowIndex, rowIndex);
					propButton.setEnabled(true);
				}
			}
			else if (src == eventTable.getTableHeader())
			{
				propButton.setEnabled(false);
			}
			else if (src == peerTable)
			{
				rowCount = peerTable.getRowCount();
				if (rowCount > 0)
				{
					rowIndex = peerTable.rowAtPoint(new Point(event.getX(), event.getY()));
					peerTable.setRowSelectionInterval(rowIndex, rowIndex);
					endButton.setEnabled(true);
				}
			}
			else if (src == peerTable.getTableHeader())
			{
				endButton.setEnabled(false);
			}
			this.showPopupMenu(event);
		}
		
		public void mouseReleased(MouseEvent event)
		{
			this.showPopupMenu(event);
		}
		
		private void showPopupMenu(MouseEvent event)
		{
			Object src = event.getSource();
			if (event.isPopupTrigger() && (src == eventTable))
			{
				showEventPopupMenu(event.getComponent(), event.getX(), event.getY());
			}
		}

	}
	
	// ----------- popup menu for loger --------------
	
	final class EventPopupMenu extends JPopupMenu implements ActionListener
	{

		// private member
		private static final long serialVersionUID = -5630677845941564070L;
		private JMenuItem propMenuItem;
		private String command = "property";
		
		public EventPopupMenu()
		{
			this.addMenu();
		}
		
		private void addMenu()
		{
			propMenuItem = this.makeMenuItem(LanguageLoader.getProperty("menu.property"), command);
			this.add(propMenuItem);
		}
		
		private JMenuItem makeMenuItem(String name, String cmd)
		{ 
	        JMenuItem menuItem = new JMenuItem(name);
			menuItem.setActionCommand(cmd);
	        menuItem.addActionListener(this);

	        return menuItem;
		}
		
		public void actionPerformed(ActionEvent event) 
		{
			String cmd = event.getActionCommand();
			if (cmd.equals(command))
			{
				if (eventDialog == null)
				{
					eventDialog = new EventDialog(bootstrapGUI, false);
					eventDialog.setLogerEvent(getLogEvent());
				}
				else
				{
					eventDialog.setLogerEvent(getLogEvent());
					eventDialog.setVisible(true);
					eventDialog.repaint();
				}
			}
		}
		
	}
	
	// ----------- table cell render for loger -------------

	final class EventTableCellRenderer extends DefaultTableCellRenderer 
    {
	
		// private members
		private static final long serialVersionUID = -1895272885977337809L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
		{
		    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		public void setValue(Object value) 
		{ 
		    setIcon(null);
		    if (value instanceof JLabel) 
		    {
		    	JLabel label = (JLabel) value;
		    	setText(label.getText());
		    	setIcon(label.getIcon());
		    } 
		    else 
		    {
		    	super.setValue(value);
		    }
		}

    }
	
	// ----------- display event recorded by loger ----------------
	
	final class EventDialog extends AbstractEventDialog
	{

		// private member
		private static final long serialVersionUID = 3524367182104191097L;
		
		public EventDialog(Frame gui, boolean model) 
		{
			super(gui, LanguageLoader.getProperty("label.event"), model, 360, 400);
			this.setVisible(true);
		}

		public void actionPerformed(ActionEvent event) 
		{
			String cmd = event.getActionCommand();
			
			int rowIndex = eventTable.getSelectedRow();
			int rowCount = eventTable.getRowCount();

			// if up 
			if (cmd.equals(command[0]))
			{
				if (rowCount > 0)
				{
					if (rowIndex > 0)
					{
						eventTable.setRowSelectionInterval(rowIndex - 1, rowIndex - 1);
						this.setLogerEvent(getLogEvent());
					}
				}
			}
			// if down
			else if (cmd.equals(command[1]))
			{
				if (rowCount > 0)
				{
					if (rowIndex < (rowCount - 1))
					{
						eventTable.setRowSelectionInterval(rowIndex + 1, rowIndex + 1);
						this.setLogerEvent(getLogEvent());
					}
				}
			}
			// if close dialog
			else if (cmd.equals(command[2]))
			{
				this.setVisible(false);
			}
		}
		
		@Override
		protected void processWhenWindowClosing() 
		{
		}

	}

}