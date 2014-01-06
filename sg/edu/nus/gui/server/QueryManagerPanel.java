package sg.edu.nus.gui.server;

import javax.swing.JPanel;
import javax.swing.Box;

import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JLabel;


import java.awt.Color;

import java.awt.BorderLayout;


import java.awt.event.*;


import sg.edu.nus.peer.LanguageLoader;

/**
 * QueryManagerPanel is used to display the choice of operation in Query Manager Tab.
 * @author Han Xixian
 * @version August 2 2008
 *
 */

public class QueryManagerPanel extends JPanel {
	
	private OperatePanel parentComponent;
	
	JLabel sqlQueryLabel = null;
	
	ImageIcon sqlQueryIcon_normal = null;
	ImageIcon sqlQueryIcon_selected = null;
	
	ImageIcon rightRowIcon = null;
	
	Color text_selected_color = new Color(0x7E, 0x31, 0x17);
	Color text_normal_color = Color.BLACK;
	
	SQLQueryBrowerPanel sqlQueryBrowerPanel = null;
	
	/**
	 * This is the default constructor
	 */
	public QueryManagerPanel(OperatePanel parentComponent) {
		super();	
		
		this.parentComponent = parentComponent;
		
		initialize();
	}

	public void setComponentText()
	{
		sqlQueryLabel.setText(LanguageLoader.getProperty("querymanager.sqlquery"));
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.sqlQueryIcon_normal = new ImageIcon("./sg/edu/nus/gui/res/sql_query_normal.png");
		this.sqlQueryIcon_selected = new ImageIcon("./sg/edu/nus/gui/res/sql_query_selected.png");
		
		this.sqlQueryLabel = new JLabel(this.sqlQueryIcon_normal);
		this.sqlQueryLabel.setText(LanguageLoader.getProperty("querymanager.sqlquery"));
		this.sqlQueryLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.sqlQueryLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		this.sqlQueryLabel.setBorder(null);
		
		this.sqlQueryLabel.addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent e)
			{
				QueryManagerPanel.this.sqlQueryLabel.setForeground(Color.BLUE);
				QueryManagerPanel.this.sqlQueryLabel.setIcon(QueryManagerPanel.this.sqlQueryIcon_selected);
				
			}
			
			public void mouseExited(MouseEvent e)
			{
				QueryManagerPanel.this.sqlQueryLabel.setForeground(Color.BLACK);
				QueryManagerPanel.this.sqlQueryLabel.setIcon(QueryManagerPanel.this.sqlQueryIcon_normal);
			}
			
			public void mouseClicked(MouseEvent e)
			{
				sqlQueryBrowerPanel = new SQLQueryBrowerPanel(QueryManagerPanel.this.parentComponent);
				
				QueryManagerPanel.this.parentComponent.setComponentAt(
						OperatePanel.TAB_QUERYMANAGER_INDEX, sqlQueryBrowerPanel);
			}
		});
		
		
		this.setLayout(new BorderLayout());
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(10));
		box.add(this.sqlQueryLabel);
		box.add(Box.createHorizontalGlue());
		
		this.add(box, BorderLayout.NORTH);
		
		this.setBackground(OperatePanel.panel_color);
		
		this.setSize(600, 600);
		
	}
	
	public SQLQueryBrowerPanel getSQLQueryBrowserPanel()
	{
		return this.sqlQueryBrowerPanel;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
