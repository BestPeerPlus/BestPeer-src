package sg.edu.nus.gui.server;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import sg.edu.nus.gui.dbview.GlobalQueryDBTree;
import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

/**
 * SQLQueryBrowerPanel is interface for query.
 * 
 * @author Han Xixia
 * 
 */

public class SQLQueryBrowerPanel extends JPanel 
{
	private OperatePanel operatePanel = null;
	
	/**
	 * sqlInputField is used for input SQL commands to BESTPEER
	 */
	private JTextArea sqlInputArea = null;
	
	/**
	 * btnExecution is used to execute the specified command
	 * btnCancelExecution is used to cancel the execution of current SQL command
	 */
	private JButton btnExecution = null;
	private JLabel jlExecution = null;
	private ImageIcon iconExecution = null;
	private ImageIcon iconExecution_selected = null;
	
	private JButton btnCancelExecution = null;
	
	private GlobalQueryDBTree globalDBTree = null;
	
	private JTable resultTable = null;
	private DatabaseQueryTableModel qtm = null;
	
	/**
	 * This is the default constructor
	 */
	public SQLQueryBrowerPanel(OperatePanel operatePanel) {
		super();		
		this.operatePanel = operatePanel;		
		
		qtm = new DatabaseQueryTableModel();
		
		initialize();
	}
	
	/**
	 * Initialize the component of this panel
	 */
	public void initialize()
	{
		/**
		 * Set layout manager
		 */
		GridBagLayout layout = new GridBagLayout();
		this.setLayout(layout);
		
		/**
		 * Initialize input sql Area
		 *		 
		 */
		this.sqlInputArea = new JTextArea();
		this.sqlInputArea.setRows(6);
		this.sqlInputArea.setColumns(60);
		this.sqlInputArea.setFont(new Font("Times New Roman", Font.BOLD, 15));

		JScrollPane scrollInputArea = new JScrollPane(this.sqlInputArea);
		scrollInputArea.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(4), 
				LanguageLoader.getProperty("PanelTitle.sqlinputarea") + "/ Connection:" +
						ServerPeer.SERVER_DB_USER + "" + ServerPeer.SERVER_DB_PORT + "/" + 
						ServerPeer.SERVER_DB_NAME));

		
		/**
		 * Initialize the execution button
		 */
//		this.iconExecution = new ImageIcon("./sg/edu/nus/gui/res/executequery.gif");
		this.iconExecution = new ImageIcon("./sg/edu/nus/gui/res/new/execute2.png");

		this.iconExecution_selected = new ImageIcon("./sg/edu/nus/gui/res/search_selected.png");
		
//		this.jlExecution = new JLabel(iconExecution);
//		this.jlExecution.setPreferredSize(new Dimension(35, 36));

//		this.jlExecution.addMouseListener(new MouseAdapter()
//		{
//			public void mouseEntered(MouseEvent e)
//			{
////				SQLQueryBrowerPanel.this.jlExecution.setIcon(SQLQueryBrowerPanel.this.iconExecution_selected);
//			}
//			
//			public void mouseExited(MouseEvent e)
//			{
////				SQLQueryBrowerPanel.this.jlExecution.setIcon(SQLQueryBrowerPanel.this.iconExecution);			
//			}
//			
//			public void mouseClicked(MouseEvent e)
//			{
//				try
//				{
//				    int pos = SQLQueryBrowerPanel.this.sqlInputArea.getCaretPosition();
//				    int row = SQLQueryBrowerPanel.this.sqlInputArea.getLineOfOffset(pos); 
//				    
//				    int lineStartOffset = SQLQueryBrowerPanel.this.sqlInputArea.getLineStartOffset(row);
//				    
//			        String sqlCommand = SQLQueryBrowerPanel.this.sqlInputArea.getText();
//			        
//			        int posBegin = -1;
//			        int posEnd = -1;
//			        
//			        for(int i = lineStartOffset; i >=0; i--)
//			        {
//			        	char ch = sqlCommand.charAt(i);
//			        	
//			        	if(ch == ';')
//			        	{
//			        		posBegin = i + 1;
//			        		break;
//			        	}
//			        }
//			        
//			        if(posBegin == -1)
//			        	posBegin = 0;
//			        
//		        	while(sqlCommand.charAt(posBegin) == '\n')
//		        		posBegin = posBegin + 1;
//			        
//			        for(int i = lineStartOffset; i < sqlCommand.length(); i++)
//			        {
//			        	char ch = sqlCommand.charAt(i);
//			        	
//			        	if(ch == ';')
//			        	{
//			        		posEnd = i;
//			        		break;
//			        	}
//			        }				        						        
//					
//					qtm.initDB();
//					qtm.setQuery(sqlCommand.substring(posBegin, posEnd));
//					qtm.closeDB();
//				}
//				catch(Exception event)
//				{
//					event.printStackTrace();
//				}
//			}
//		});
		
		btnExecution = new JButton(iconExecution);
		this.btnExecution.setPreferredSize(new Dimension(35, 48));
		
		this.btnExecution.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							try
							{
								/**
								 * Determine current sql command in input field
								 */
								
							    int pos = SQLQueryBrowerPanel.this.sqlInputArea.getCaretPosition();
							    int row = SQLQueryBrowerPanel.this.sqlInputArea.getLineOfOffset(pos); 
							    
							    int lineStartOffset = SQLQueryBrowerPanel.this.sqlInputArea.getLineStartOffset(row);
							    
						        String sqlCommand = SQLQueryBrowerPanel.this.sqlInputArea.getText();

						        /*
						        int posBegin = -1;
						        int posEnd = -1;
						        
						        if(sqlCommand.length() == 0)
						        	return;
						        
						        for(int i = lineStartOffset; i >=0; i--)
						        {
						        	char ch = sqlCommand.charAt(i);
						        	
						        	if(ch == ';')
						        	{
						        		posBegin = i + 1;
						        		break;
						        	}
						        }
						        
						        if(posBegin == -1)
						        	posBegin = 0;
						        
					        	while(sqlCommand.charAt(posBegin) == '\n')
					        		posBegin = posBegin + 1;
						        
						        for(int i = lineStartOffset; i < sqlCommand.length(); i++)
						        {
						        	char ch = sqlCommand.charAt(i);
						        	
						        	if(ch == ';')
						        	{
						        		posEnd = i;
						        		break;
						        	}
						        }				        						        
								
								qtm.initDB();
								qtm.setQuery(sqlCommand.substring(posBegin, posEnd));
								qtm.closeDB();*/
							}
							catch(Exception event)
							{
								event.printStackTrace();
							}
						}
					}
				);


		/*
		 * Set result to output table.
		 */
		this.resultTable = new JTable(qtm);
		JScrollPane scrollTable = new JScrollPane(this.resultTable);	
		scrollTable.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createBevelBorder(4), 
				LanguageLoader.getProperty("PanelTitle.sqlresultoutput")));
		
		globalDBTree = new GlobalQueryDBTree(this);
		globalDBTree.setPreferredSize(new Dimension(150, 400));
		JScrollPane scrollTree = new JScrollPane(this.globalDBTree);
		
		/**
		 * Set GridBagConstraint of component
		 */
		GridBagConstraints constraint1 = new GridBagConstraints();
		constraint1.gridx = 0;
		constraint1.gridy = 0;
		constraint1.gridwidth = 1;
		constraint1.gridheight = 1;
		constraint1.weightx = 100;
		constraint1.weighty = 0;
		constraint1.fill = GridBagConstraints.HORIZONTAL;
		
		GridBagConstraints constraint2 = new GridBagConstraints();
		constraint2.gridx = 1;
		constraint2.gridy = 0;
		constraint2.gridwidth = 1;
		constraint2.gridheight = 1;
		constraint2.weightx = 0;
		constraint2.weighty = 0;
		constraint2.fill = GridBagConstraints.NONE;

		GridBagConstraints constraint3 = new GridBagConstraints();
		constraint3.gridx = 0;
		constraint3.gridy = 1;
		constraint3.gridwidth = 1;
		constraint3.gridheight = 1;
		constraint3.weightx = 100;
		constraint3.weighty = 100;
		constraint3.fill = GridBagConstraints.BOTH;		
		
		GridBagConstraints constraint4 = new GridBagConstraints();
		constraint4.gridx = 1;
		constraint4.gridy = 1;
		constraint4.gridwidth = 1;
		constraint4.gridheight = 1;
		constraint4.weightx = 0;
		constraint4.weighty = 100;
		constraint4.fill = GridBagConstraints.VERTICAL;		
		
		JPanel upperlevelPanel = new JPanel();
		upperlevelPanel.setLayout(layout);
		
		upperlevelPanel.add(scrollInputArea, constraint1);
		upperlevelPanel.add(this.btnExecution, constraint2);
		
		JPanel lowerlevelPanel = new JPanel();
		lowerlevelPanel.setLayout(layout);
		lowerlevelPanel.add(scrollTable, constraint3);
		lowerlevelPanel.add(scrollTree, constraint4);
		
		GridBagConstraints constraint5 = new GridBagConstraints();
		constraint5.gridx = 0;
		constraint5.gridy = 0;
		constraint5.gridwidth = 1;
		constraint5.gridheight = 1;
		constraint5.weightx = 100;
		constraint5.weighty = 0;
		constraint5.fill = GridBagConstraints.HORIZONTAL;		
		
		GridBagConstraints constraint6 = new GridBagConstraints();
		constraint6.gridx = 0;
		constraint6.gridy = 1;
		constraint6.gridwidth = 1;
		constraint6.gridheight = 1;
		constraint6.weightx = 100;
		constraint6.weighty = 100;
		constraint6.fill = GridBagConstraints.BOTH;	
		
		this.add(upperlevelPanel, constraint5);
		this.add(lowerlevelPanel, constraint6);
	}	
	
	public DatabaseQueryTableModel getTableModel()
	{
		return this.qtm;
	}
}
