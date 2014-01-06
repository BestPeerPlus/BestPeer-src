package sg.edu.nus.gui.server;

import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.Spring;

import java.awt.Color;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import java.awt.event.*;

import sg.edu.nus.gui.dbview.*;
import sg.edu.nus.peer.LanguageLoader;

/**
 * DBManagerPanel is interface for db configure and db explorer
 * 
 * @author Han Xixian
 *
 */

public class DBManagerPanel extends JPanel {
	
	private OperatePanel parentComponent;
	
	JLabel dbConfigLabel = null;
	JLabel dbExplorerLabel = null;
	
	ImageIcon dbConfigIcon_normal = null;
	ImageIcon dbConfigIcon_selected = null;
	
	ImageIcon dbExplorerIcon_normal = null;
	ImageIcon dbExplorerIcon_selected = null;
	
	ImageIcon rightRowIcon = null;
	
	Color text_selected_color = new Color(0x7E, 0x31, 0x17);
	Color text_normal_color = Color.BLACK;
	
	/**
	 * This is the default constructor
	 */
	public DBManagerPanel(OperatePanel parentComponent) {
		super();	
		
		this.parentComponent = parentComponent;
		
		initialize();
	}

	public void setComponentText()
	{
		dbConfigLabel.setText(LanguageLoader.getProperty("dbmanager.dbconfig"));
		dbExplorerLabel.setText(LanguageLoader.getProperty("dbmanager.dbexplorer"));
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.dbConfigIcon_normal = new ImageIcon("./sg/edu/nus/gui/res/dbconfigure_normal.gif");
		this.dbConfigIcon_selected = new ImageIcon("./sg/edu/nus/gui/res/dbconfigure_selected.gif");
		
		this.dbConfigLabel = new JLabel(this.dbConfigIcon_normal);
		this.dbConfigLabel.setText(LanguageLoader.getProperty("dbmanager.dbconfig"));
		this.dbConfigLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.dbConfigLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		this.dbConfigLabel.setBorder(null);
		
		/**
		 * Mouse Event Listener for entering, leave, click
		 */
		this.dbConfigLabel.addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent e)
			{
				DBManagerPanel.this.dbConfigLabel.setForeground(Color.BLUE);
				DBManagerPanel.this.dbConfigLabel.setIcon(DBManagerPanel.this.dbConfigIcon_selected);
				
			}
			
			public void mouseExited(MouseEvent e)
			{
				DBManagerPanel.this.dbConfigLabel.setForeground(Color.BLACK);
				DBManagerPanel.this.dbConfigLabel.setIcon(DBManagerPanel.this.dbConfigIcon_normal);
			}
			
			public void mouseClicked(MouseEvent e)
			{
				
				DBConfigureView dbConfigView = new DBConfigureView(DBManagerPanel.this.parentComponent);
				
				DBManagerPanel.this.parentComponent.setComponentAt(
						OperatePanel.TAB_DBMANAGER_INDEX, dbConfigView);
			}
		});
		
		
		this.dbExplorerIcon_normal = new ImageIcon("./sg/edu/nus/gui/res/dbexplorer_normal.png");
		this.dbExplorerIcon_selected = new ImageIcon("./sg/edu/nus/gui/res/dbexplorer_selected.png");
		
		this.dbExplorerLabel = new JLabel(this.dbExplorerIcon_normal);
		this.dbExplorerLabel.setText(LanguageLoader.getProperty("dbmanager.dbexplorer"));
		this.dbExplorerLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
		this.dbExplorerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		this.dbExplorerLabel.setBorder(null);
		
		/**
		 * Mouse Event Listener for entering, leave, click
		 */
		this.dbExplorerLabel.addMouseListener(new MouseAdapter()
				{
					public void mouseEntered(MouseEvent e)
					{
						DBManagerPanel.this.dbExplorerLabel.setForeground(Color.BLUE);	
						DBManagerPanel.this.dbExplorerLabel.setIcon(DBManagerPanel.this.dbExplorerIcon_selected);
						
					}
					
					public void mouseExited(MouseEvent e)
					{
						DBManagerPanel.this.dbExplorerLabel.setForeground(Color.BLACK);
						DBManagerPanel.this.dbExplorerLabel.setIcon(DBManagerPanel.this.dbExplorerIcon_normal);

					}
					
					public void mouseClicked(MouseEvent e)
					{
						JPanel dbxPanel = new JPanel();
						
						DBManagerPanel.this.parentComponent.setComponentAt(
								OperatePanel.TAB_DBMANAGER_INDEX, dbxPanel);
					}
				});
		
		this.setLayout(new BorderLayout());
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(10));
		box.add(this.dbConfigLabel);
		box.add(Box.createHorizontalStrut(10));
		box.add(this.dbExplorerLabel);
		box.add(Box.createHorizontalGlue());
		
		this.add(box, BorderLayout.NORTH);
		
		this.setBackground(OperatePanel.panel_color);
		
		this.setSize(600, 600);		
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
