package sg.edu.nus.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import sg.edu.nus.dbconnection.DBProperty;

class DBConfigureView extends JPanel {
	public static Color panel_color = new Color(0xAF, 0xDC, 0xEC);
	
	private JLabel localdb_driverLabel = null;
	private JComboBox localdb_driverComboBox = null;
	private JLabel localdb_urlLabel = null;
	private JTextField localdb_urlTextField = null;
	private JLabel localdb_portLabel = null;
	private JTextField localdb_portTextField = null;
	private JLabel localdb_dbNameLabel = null;
	private JLabel localdb_usernameLabel = null;
	private JTextField localdb_dbNameTextField = null;
	private JTextField localdb_usernameTextField = null;
	private JLabel localdb_pwLabel = null;
	private JPasswordField localdb_pwField = null;
	private JButton localdb_btnSave = null;
	private JButton localdb_btnCheckConn = null;
	
	private JLabel bestpeerdb_driverLabel = null;
	private JComboBox bestpeerdb_driverComboBox = null;
	private JLabel bestpeerdb_urlLabel = null;
	private JTextField bestpeerdb_urlTextField = null;
	private JLabel bestpeerdb_portLabel = null;
	private JTextField bestpeerdb_portTextField = null;
	private JLabel bestpeerdb_dbNameLabel = null;
	private JLabel bestpeerdb_usernameLabel = null;
	private JTextField bestpeerdb_dbNameTextField = null;
	private JTextField bestpeerdb_usernameTextField = null;
	private JLabel bestpeerdb_pwLabel = null;
	private JPasswordField bestpeerdb_pwField = null;
	private JButton bestpeerdb_btnSave = null;
	private JButton bestpeerdb_btnCheckConn = null;
	
	private JPanel localdb_Panel = null;
	private JPanel bestpeerdb_Panel = null;
	
	private JButton btnContinue = null;
	
	private JFrame parent = null;
	/**
	 * This is the default constructor
	 */
	public DBConfigureView(JFrame frame) {
		super();
		
		parent = frame;
		
		this.getLocaldbConfigure();
		this.getbestpeerdbConfigure();
		
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.setLayout(new GridBagLayout());
		this.setBackground(panel_color);
		
		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 0;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.NONE;
		constraints1.insets.right = 50;
		constraints1.insets.bottom = 20;
		
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 0;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.insets.bottom = 20;
		
		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 1;
		constraints3.gridy = 1;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 0;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.NONE;
		constraints3.anchor = GridBagConstraints.EAST;
		
		this.add(localdb_Panel, constraints1);
		this.add(bestpeerdb_Panel, constraints2);
		this.add(getBtnContinue(), constraints3);
	}

	private void getLocaldbConfigure()
	{
		localdb_driverLabel = new JLabel();
		localdb_driverLabel.setText("Driver:");
		
		localdb_urlLabel = new JLabel();
		localdb_urlLabel.setText("URL:");
		
		localdb_portLabel = new JLabel();
		localdb_portLabel.setText("Port:");
		
		localdb_dbNameLabel = new JLabel();
		localdb_dbNameLabel.setText("DBName:");
		
		localdb_usernameLabel = new JLabel();
		localdb_usernameLabel.setText("UserName:");
		
		localdb_pwLabel = new JLabel();
		localdb_pwLabel.setText("Password:");
				
		
		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 50;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.NONE;
		constraints1.anchor = GridBagConstraints.EAST;
		constraints1.insets.bottom = 10;
		constraints1.insets.right = 10;
		
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 50;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.insets.bottom = 10;		
		
		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 0;
		constraints3.gridy = 1;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 50;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.NONE;
		constraints3.anchor = GridBagConstraints.EAST;
		constraints3.insets.bottom = 10;
		constraints3.insets.right = 10;
		
		GridBagConstraints constraints4 = new GridBagConstraints();
		constraints4.gridx = 1;
		constraints4.gridy = 1;
		constraints4.gridwidth = 1;
		constraints4.gridheight = 1;
		constraints4.weightx = 50;
		constraints4.weighty = 0;
		constraints4.fill = GridBagConstraints.NONE;
		constraints4.anchor = GridBagConstraints.WEST;
		constraints4.insets.bottom = 10;			
		

		GridBagConstraints constraints5 = new GridBagConstraints();
		constraints5.gridx = 0;
		constraints5.gridy = 2;
		constraints5.gridwidth = 1;
		constraints5.gridheight = 1;
		constraints5.weightx = 50;
		constraints5.weighty = 0;
		constraints5.fill = GridBagConstraints.NONE;
		constraints5.anchor = GridBagConstraints.EAST;
		constraints5.insets.bottom = 10;
		constraints5.insets.right = 10;
		
		GridBagConstraints constraints6 = new GridBagConstraints();
		constraints6.gridx = 1;
		constraints6.gridy = 2;
		constraints6.gridwidth = 1;
		constraints6.gridheight = 1;
		constraints6.weightx = 50;
		constraints6.weighty = 0;
		constraints6.fill = GridBagConstraints.NONE;
		constraints6.anchor = GridBagConstraints.WEST;
		constraints6.insets.bottom = 10;	
		
		
		GridBagConstraints constraints7 = new GridBagConstraints();
		constraints7.gridx = 0;
		constraints7.gridy = 3;
		constraints7.gridwidth = 1;
		constraints7.gridheight = 1;
		constraints7.weightx = 50;
		constraints7.weighty = 0;
		constraints7.fill = GridBagConstraints.NONE;
		constraints7.anchor = GridBagConstraints.EAST;
		constraints7.insets.bottom = 10;
		constraints7.insets.right = 10;
		
		GridBagConstraints constraints8 = new GridBagConstraints();
		constraints8.gridx = 1;
		constraints8.gridy = 3;
		constraints8.gridwidth = 1;
		constraints8.gridheight = 1;
		constraints8.weightx = 50;
		constraints8.weighty = 0;
		constraints8.fill = GridBagConstraints.NONE;
		constraints8.anchor = GridBagConstraints.WEST;
		constraints8.insets.bottom = 10;	
		
		
		GridBagConstraints constraints9 = new GridBagConstraints();
		constraints9.gridx = 0;
		constraints9.gridy = 4;
		constraints9.gridwidth = 1;
		constraints9.gridheight = 1;
		constraints9.weightx = 50;
		constraints9.weighty = 0;
		constraints9.fill = GridBagConstraints.NONE;
		constraints9.anchor = GridBagConstraints.EAST;
		constraints9.insets.bottom = 10;
		constraints9.insets.right = 10;
		
		GridBagConstraints constraints10 = new GridBagConstraints();
		constraints10.gridx = 1;
		constraints10.gridy = 4;
		constraints10.gridwidth = 1;
		constraints10.gridheight = 1;
		constraints10.weightx = 50;
		constraints10.weighty = 0;
		constraints10.fill = GridBagConstraints.NONE;
		constraints10.anchor = GridBagConstraints.WEST;
		constraints10.insets.bottom = 10;	
		
		GridBagConstraints constraints11 = new GridBagConstraints();
		constraints11.gridx = 0;
		constraints11.gridy = 5;
		constraints11.gridwidth = 1;
		constraints11.gridheight = 1;
		constraints11.weightx = 50;
		constraints11.weighty = 0;
		constraints11.fill = GridBagConstraints.NONE;
		constraints11.anchor = GridBagConstraints.EAST;
		constraints11.insets.bottom = 10;
		constraints11.insets.right = 10;
		
		GridBagConstraints constraints12 = new GridBagConstraints();
		constraints12.gridx = 1;
		constraints12.gridy = 5;
		constraints12.gridwidth = 1;
		constraints12.gridheight = 1;
		constraints12.weightx = 50;
		constraints12.weighty = 0;
		constraints12.fill = GridBagConstraints.NONE;
		constraints12.anchor = GridBagConstraints.WEST;
		constraints12.insets.bottom = 10;	
		
		GridBagConstraints constraints13 = new GridBagConstraints();
		constraints13.gridx = 1;
		constraints13.gridy = 6;
		constraints13.gridwidth = 2;
		constraints13.gridheight = 1;
		constraints13.weightx = 50;
		constraints13.weighty = 0;
		constraints13.fill = GridBagConstraints.NONE;
		constraints13.anchor = GridBagConstraints.CENTER;
		constraints13.insets.bottom = 10;	
		
		localdb_Panel = new JPanel();
		localdb_Panel.setLayout(new GridBagLayout());
		
		localdb_Panel.setBackground(panel_color);
		localdb_Panel.add(localdb_driverLabel, constraints1);
		localdb_Panel.add(getlocaldb_driverComboBox(), constraints2);
		localdb_Panel.add(localdb_urlLabel, constraints3);
		localdb_Panel.add(getlocaldb_urlTextField(), constraints4);
		localdb_Panel.add(localdb_portLabel, constraints5);
		localdb_Panel.add(getlocaldb_portTextField(), constraints6);
		localdb_Panel.add(localdb_dbNameLabel, constraints7);
		localdb_Panel.add(getlocaldb_dbNameTextField(), constraints8);
		localdb_Panel.add(localdb_usernameLabel, constraints9);
		localdb_Panel.add(getLocaldb_userField(), constraints10);
		localdb_Panel.add(localdb_pwLabel, constraints11);
		localdb_Panel.add(getlocaldb_pwField(), constraints12);
		
		JPanel panel = new JPanel();
		panel.add(getlocaldb_btnCheckConn());
		panel.add(getlocaldb_btnSave());
		panel.setBackground(panel_color);
		
		localdb_Panel.add(panel, constraints13);
		
		TitledBorder titledBorder = new TitledBorder(
		BorderFactory.createLineBorder(Color.BLUE));
		Font font = new Font("titledBorderFont", Font.BOLD, 12);

		titledBorder.setTitleColor(Color.BLUE);
		titledBorder.setTitleFont(font);
		titledBorder.setTitle("Local DB Configure (exporting data for sharing)");	
		
		localdb_Panel.setBorder(titledBorder);
	}
	
	private void getbestpeerdbConfigure()
	{
		bestpeerdb_driverLabel = new JLabel();
		bestpeerdb_driverLabel.setText("Driver:");
		
		bestpeerdb_urlLabel = new JLabel();
		bestpeerdb_urlLabel.setText("URL:");
		
		bestpeerdb_portLabel = new JLabel();
		bestpeerdb_portLabel.setText("Port:");
		
		bestpeerdb_dbNameLabel = new JLabel();
		bestpeerdb_dbNameLabel.setText("DBName");
		
		bestpeerdb_usernameLabel = new JLabel();
		bestpeerdb_usernameLabel.setText("UserName:");
		
		bestpeerdb_pwLabel = new JLabel();
		bestpeerdb_pwLabel.setText("Password");
				
		
		GridBagConstraints constraints1 = new GridBagConstraints();
		constraints1.gridx = 0;
		constraints1.gridy = 0;
		constraints1.gridwidth = 1;
		constraints1.gridheight = 1;
		constraints1.weightx = 50;
		constraints1.weighty = 0;
		constraints1.fill = GridBagConstraints.NONE;
		constraints1.anchor = GridBagConstraints.EAST;
		constraints1.insets.bottom = 10;
		constraints1.insets.right = 10;
		
		GridBagConstraints constraints2 = new GridBagConstraints();
		constraints2.gridx = 1;
		constraints2.gridy = 0;
		constraints2.gridwidth = 1;
		constraints2.gridheight = 1;
		constraints2.weightx = 50;
		constraints2.weighty = 0;
		constraints2.fill = GridBagConstraints.NONE;
		constraints2.anchor = GridBagConstraints.WEST;
		constraints2.insets.bottom = 10;		
		
		GridBagConstraints constraints3 = new GridBagConstraints();
		constraints3.gridx = 0;
		constraints3.gridy = 1;
		constraints3.gridwidth = 1;
		constraints3.gridheight = 1;
		constraints3.weightx = 50;
		constraints3.weighty = 0;
		constraints3.fill = GridBagConstraints.NONE;
		constraints3.anchor = GridBagConstraints.EAST;
		constraints3.insets.bottom = 10;
		constraints3.insets.right = 10;
		
		GridBagConstraints constraints4 = new GridBagConstraints();
		constraints4.gridx = 1;
		constraints4.gridy = 1;
		constraints4.gridwidth = 1;
		constraints4.gridheight = 1;
		constraints4.weightx = 50;
		constraints4.weighty = 0;
		constraints4.fill = GridBagConstraints.NONE;
		constraints4.anchor = GridBagConstraints.WEST;
		constraints4.insets.bottom = 10;			
		

		GridBagConstraints constraints5 = new GridBagConstraints();
		constraints5.gridx = 0;
		constraints5.gridy = 2;
		constraints5.gridwidth = 1;
		constraints5.gridheight = 1;
		constraints5.weightx = 50;
		constraints5.weighty = 0;
		constraints5.fill = GridBagConstraints.NONE;
		constraints5.anchor = GridBagConstraints.EAST;
		constraints5.insets.bottom = 10;
		constraints5.insets.right = 10;
		
		GridBagConstraints constraints6 = new GridBagConstraints();
		constraints6.gridx = 1;
		constraints6.gridy = 2;
		constraints6.gridwidth = 1;
		constraints6.gridheight = 1;
		constraints6.weightx = 50;
		constraints6.weighty = 0;
		constraints6.fill = GridBagConstraints.NONE;
		constraints6.anchor = GridBagConstraints.WEST;
		constraints6.insets.bottom = 10;	
		
		
		GridBagConstraints constraints7 = new GridBagConstraints();
		constraints7.gridx = 0;
		constraints7.gridy = 3;
		constraints7.gridwidth = 1;
		constraints7.gridheight = 1;
		constraints7.weightx = 50;
		constraints7.weighty = 0;
		constraints7.fill = GridBagConstraints.NONE;
		constraints7.anchor = GridBagConstraints.EAST;
		constraints7.insets.bottom = 10;
		constraints7.insets.right = 10;
		
		GridBagConstraints constraints8 = new GridBagConstraints();
		constraints8.gridx = 1;
		constraints8.gridy = 3;
		constraints8.gridwidth = 1;
		constraints8.gridheight = 1;
		constraints8.weightx = 50;
		constraints8.weighty = 0;
		constraints8.fill = GridBagConstraints.NONE;
		constraints8.anchor = GridBagConstraints.WEST;
		constraints8.insets.bottom = 10;	
		
		
		GridBagConstraints constraints9 = new GridBagConstraints();
		constraints9.gridx = 0;
		constraints9.gridy = 4;
		constraints9.gridwidth = 1;
		constraints9.gridheight = 1;
		constraints9.weightx = 50;
		constraints9.weighty = 0;
		constraints9.fill = GridBagConstraints.NONE;
		constraints9.anchor = GridBagConstraints.EAST;
		constraints9.insets.bottom = 10;
		constraints9.insets.right = 10;
		
		GridBagConstraints constraints10 = new GridBagConstraints();
		constraints10.gridx = 1;
		constraints10.gridy = 4;
		constraints10.gridwidth = 1;
		constraints10.gridheight = 1;
		constraints10.weightx = 50;
		constraints10.weighty = 0;
		constraints10.fill = GridBagConstraints.NONE;
		constraints10.anchor = GridBagConstraints.WEST;
		constraints10.insets.bottom = 10;	
		
		GridBagConstraints constraints11 = new GridBagConstraints();
		constraints11.gridx = 0;
		constraints11.gridy = 5;
		constraints11.gridwidth = 1;
		constraints11.gridheight = 1;
		constraints11.weightx = 50;
		constraints11.weighty = 0;
		constraints11.fill = GridBagConstraints.NONE;
		constraints11.anchor = GridBagConstraints.EAST;
		constraints11.insets.bottom = 10;
		constraints11.insets.right = 10;
		
		GridBagConstraints constraints12 = new GridBagConstraints();
		constraints12.gridx = 1;
		constraints12.gridy = 5;
		constraints12.gridwidth = 1;
		constraints12.gridheight = 1;
		constraints12.weightx = 50;
		constraints12.weighty = 0;
		constraints12.fill = GridBagConstraints.NONE;
		constraints12.anchor = GridBagConstraints.WEST;
		constraints12.insets.bottom = 10;	
		
		GridBagConstraints constraints13 = new GridBagConstraints();
		constraints13.gridx = 1;
		constraints13.gridy = 6;
		constraints13.gridwidth = 1;
		constraints13.gridheight = 1;
		constraints13.weightx = 50;
		constraints13.weighty = 0;
		constraints13.fill = GridBagConstraints.NONE;
		constraints13.anchor = GridBagConstraints.EAST;
		constraints13.insets.bottom = 10;	
		
		bestpeerdb_Panel = new JPanel();
		bestpeerdb_Panel.setLayout(new GridBagLayout());
		
		bestpeerdb_Panel.setBackground(panel_color);
		bestpeerdb_Panel.add(bestpeerdb_driverLabel, constraints1);
		bestpeerdb_Panel.add(getbestpeerdb_driverComboBox(), constraints2);
		bestpeerdb_Panel.add(bestpeerdb_urlLabel, constraints3);
		bestpeerdb_Panel.add(getbestpeerdb_urlTextField(), constraints4);
		bestpeerdb_Panel.add(bestpeerdb_portLabel, constraints5);
		bestpeerdb_Panel.add(getbestpeerdb_portTextField(), constraints6);
		bestpeerdb_Panel.add(bestpeerdb_dbNameLabel, constraints7);
		bestpeerdb_Panel.add(getbestpeerdb_dbNameTextField(), constraints8);
		bestpeerdb_Panel.add(bestpeerdb_usernameLabel, constraints9);
		bestpeerdb_Panel.add(getBestPeer_userField(), constraints10);
		bestpeerdb_Panel.add(bestpeerdb_pwLabel, constraints11);
		bestpeerdb_Panel.add(getbestpeerdb_pwField(), constraints12);

		JPanel panel = new JPanel();
		panel.add(getbestpeerdb_btnCheckConn());
		panel.add(getbestpeerdb_btnSave());
		panel.setBackground(panel_color);
		
		bestpeerdb_Panel.add(panel, constraints13);
		
		TitledBorder titledBorder = new TitledBorder(
		BorderFactory.createLineBorder(Color.BLUE));
		Font font = new Font("titledBorderFont", Font.BOLD, 12);

		titledBorder.setTitleColor(Color.BLUE);
		titledBorder.setTitleFont(font);
		titledBorder.setTitle("BestPeer DB Configure (storing the exported data)");	
		
		bestpeerdb_Panel.setBorder(titledBorder);
	}
	
	
	/**
	 * This method initializes localdb_driverComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getlocaldb_driverComboBox() {
		if (localdb_driverComboBox == null) {
			localdb_driverComboBox = new JComboBox();
			
			localdb_driverComboBox.addItem("MySQL");
			localdb_driverComboBox.addItem("Oracle");
			localdb_driverComboBox.addItem("PostgreSQL");
			localdb_driverComboBox.addItem("SQLServer");			
		}
		
		
		
		localdb_driverComboBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox cb = (JComboBox)e.getSource();
		        String dbType = (String)cb.getSelectedItem();
		        if(dbType.equals("MySQL"))
		        {
		        	localdb_urlTextField.setText("jdbc:mysql://localhost:3306/");
		        	localdb_dbNameTextField.setText("mysql");
		        	localdb_portTextField.setText("3306");
		        }
		        else if(dbType.equals("Oracle"))
		        {
		        	localdb_urlTextField.setText("jdbc:oracle:thin:@localhost:1521:");
		        	localdb_dbNameTextField.setText("oracle");
		        	localdb_portTextField.setText("1521");
		        }
		        else if(dbType.equals("PostgreSQL"))
		        {
		        	localdb_urlTextField.setText("jdbc:postgresql://localhost:5432/");
		        	localdb_dbNameTextField.setText("postgres");
		        	localdb_portTextField.setText("5432");
		        }
		        else if(dbType.equals("SQLServer"))
		        {
		        	localdb_urlTextField.setText("jdbc:sqlserver://testserver:1433/");
		        	localdb_dbNameTextField.setText("sqlserver");
		        	localdb_portTextField.setText("1433");
		        }		        
		        
			}
			
		});
		
		return localdb_driverComboBox;
	}

	/**
	 * This method initializes localdb_urlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getlocaldb_urlTextField() {
		if (localdb_urlTextField == null) {
			localdb_urlTextField = new JTextField();
		}
		
		localdb_urlTextField.setText("jdbc:mysql://localhost:3306/");
		
		localdb_urlTextField.setColumns(25);
		
		return localdb_urlTextField;
	}

	/**
	 * This method initializes localdb_portTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getlocaldb_portTextField() {
		if (localdb_portTextField == null) {
			localdb_portTextField = new JTextField();
		}
		
		localdb_portTextField.setText("3306");
		localdb_portTextField.setColumns(25);
		
		return localdb_portTextField;
	}

	/**
	 * This method initializes localdb_dbNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getlocaldb_dbNameTextField() {
		if (localdb_dbNameTextField == null) {
			localdb_dbNameTextField = new JTextField();
		}
		
		localdb_dbNameTextField.setColumns(25);
		localdb_dbNameTextField.setText("mysql");
		
		return localdb_dbNameTextField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getLocaldb_userField() {
		if (localdb_usernameTextField == null) {
			localdb_usernameTextField = new JTextField();
		}
				
		localdb_usernameTextField.setColumns(25);
		
		localdb_usernameTextField.setText("");
		
		return localdb_usernameTextField;
	}

	/**
	 * This method initializes localdb_pwField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JPasswordField getlocaldb_pwField() {
		if (localdb_pwField == null) {
			localdb_pwField = new JPasswordField();
		}
		
		localdb_pwField.setColumns(25);
		
		return localdb_pwField;
	}

	/**
	 * This method initializes localdb_btnSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getlocaldb_btnSave() {
		if (localdb_btnSave == null) {
			localdb_btnSave = new JButton();
			localdb_btnSave.setText("Save");
			localdb_btnSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
			localdb_btnSave.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							DBProperty dbProperty = new DBProperty();
							String dbtype = (String)DBConfigureView.this.localdb_driverComboBox.getSelectedItem();
							String driver = null;
							
							if(dbtype.equals("MySQL"))
							{
								driver = dbProperty.mysqlDriver;
							}
							else if(dbtype.equals("Oracle"))
							{
								driver = dbProperty.oracleDriver;
							}
							else if(dbtype.equals("SQLServer"))
							{
								driver = dbProperty.sqlserverDriver;
							}
							else if(dbtype.equals("PostgreSQL"))
							{
								driver = dbProperty.postgresqlDriver;
							}
							
							String url = DBConfigureView.this.localdb_urlTextField.getText();
							String port = DBConfigureView.this.localdb_portTextField.getText();
							String username = DBConfigureView.this.localdb_usernameTextField.getText();
							String password = DBConfigureView.this.localdb_pwField.getText();
							String dbname = DBConfigureView.this.localdb_dbNameTextField.getText();
							
							Vector<String> vec = new Vector<String>();
							vec.add(driver);
							vec.add(url);
							vec.add(port);
							vec.add(username);
							vec.add(password);
							vec.add(dbname);
							

							dbProperty.put_localdb_configure(vec);
							
							JOptionPane.showMessageDialog(null, "Successfully Save!");
						}
					});
		}
		return localdb_btnSave;
	}

	private JButton getlocaldb_btnCheckConn() {
		if (localdb_btnCheckConn == null) {
			localdb_btnCheckConn = new JButton();
			localdb_btnCheckConn.setText("Check Connection");
			localdb_btnCheckConn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
			localdb_btnCheckConn.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							DBProperty dbProperty = new DBProperty();
							String dbtype = (String)DBConfigureView.this.localdb_driverComboBox.getSelectedItem();
							String driver = null;
							
							if(dbtype.equals("MySQL"))
							{
								driver = dbProperty.mysqlDriver;
							}
							else if(dbtype.equals("Oracle"))
							{
								driver = dbProperty.oracleDriver;
							}
							else if(dbtype.equals("SQLServer"))
							{
								driver = dbProperty.sqlserverDriver;
							}
							else if(dbtype.equals("PostgreSQL"))
							{
								driver = dbProperty.postgresqlDriver;
							}
							
							String url = DBConfigureView.this.localdb_urlTextField.getText();
							String port = DBConfigureView.this.localdb_portTextField.getText();
							String username = DBConfigureView.this.localdb_usernameTextField.getText();
							String password = DBConfigureView.this.localdb_pwField.getText();
							String dbname = DBConfigureView.this.localdb_dbNameTextField.getText();
							
							try
							{
								Class.forName(driver);
								Connection conn = DriverManager.getConnection(url + dbname, username, password);
								
								JOptionPane.showMessageDialog(null, "ERP DB Setting is successful!");
								
								conn.close();
								
							}
							catch(Exception exception)
							{
								JOptionPane.showMessageDialog(null, "ERP DB Setting is wrong!");
							}
						}
					});
		}
		return localdb_btnCheckConn;
	}
	
	/**
	 * This method initializes bestpeerdb_driverComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getbestpeerdb_driverComboBox() {
		if (bestpeerdb_driverComboBox == null) {
			bestpeerdb_driverComboBox = new JComboBox();
			
			bestpeerdb_driverComboBox.addItem("MySQL");
			bestpeerdb_driverComboBox.addItem("Oracle");
			bestpeerdb_driverComboBox.addItem("PostgreSQL");
			bestpeerdb_driverComboBox.addItem("SQLServer");			
		}
		
		
		bestpeerdb_driverComboBox.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JComboBox cb = (JComboBox)e.getSource();
		        String dbType = (String)cb.getSelectedItem();
		        if(dbType.equals("MySQL"))
		        {
		        	bestpeerdb_urlTextField.setText("jdbc:mysql://localhost:3306/");
		        	bestpeerdb_portTextField.setText("3306");
		        	bestpeerdb_dbNameTextField.setText("mysql");
		        }
		        else if(dbType.equals("Oracle"))
		        {
		        	bestpeerdb_urlTextField.setText("jdbc:oracle:thin:@localhost:1521:");
		        	bestpeerdb_portTextField.setText("1521");
		        	bestpeerdb_dbNameTextField.setText("oracle");
		        }
		        else if(dbType.equals("PostgreSQL"))
		        {
		        	bestpeerdb_urlTextField.setText("jdbc:postgresql://localhost:5432/");
		        	bestpeerdb_portTextField.setText("5432");
		        	bestpeerdb_dbNameTextField.setText("postgres");
		        }
		        else if(dbType.equals("SQLServer"))
		        {
		        	bestpeerdb_urlTextField.setText("jdbc:sqlserver://testserver:1433/");
		        	bestpeerdb_portTextField.setText("1433");
		        	bestpeerdb_dbNameTextField.setText("sqlserver");
		        }
			}
			
		});
		
		return bestpeerdb_driverComboBox;
	}

	/**
	 * This method initializes bestpeerdb_urlTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getbestpeerdb_urlTextField() {
		if (bestpeerdb_urlTextField == null) {
			bestpeerdb_urlTextField = new JTextField();
		}
		
		bestpeerdb_urlTextField.setText("jdbc:mysql://localhost:3306/");
		
		bestpeerdb_urlTextField.setColumns(25);
		
		return bestpeerdb_urlTextField;
	}

	/**
	 * This method initializes bestpeerdb_portTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getbestpeerdb_portTextField() {
		if (bestpeerdb_portTextField == null) {
			bestpeerdb_portTextField = new JTextField();
		}
		
		bestpeerdb_portTextField.setText("3306");
		bestpeerdb_portTextField.setColumns(25);
		
		return bestpeerdb_portTextField;
	}

	/**
	 * This method initializes bestpeerdb_dbNameTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getbestpeerdb_dbNameTextField() {
		if (bestpeerdb_dbNameTextField == null) {
			bestpeerdb_dbNameTextField = new JTextField();
		}
		
		bestpeerdb_dbNameTextField.setColumns(25);
		bestpeerdb_dbNameTextField.setText("mysql");
		
		return bestpeerdb_dbNameTextField;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getBestPeer_userField() {
		if (bestpeerdb_usernameTextField == null) {
			bestpeerdb_usernameTextField = new JTextField();
		}
				
		bestpeerdb_usernameTextField.setColumns(25);
		
		bestpeerdb_usernameTextField.setText("");
		
		return bestpeerdb_usernameTextField;
	}

	/**
	 * This method initializes bestpeerdb_pwField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JPasswordField getbestpeerdb_pwField() {
		if (bestpeerdb_pwField == null) {
			bestpeerdb_pwField = new JPasswordField();
		}
		
		bestpeerdb_pwField.setColumns(25);
		
		return bestpeerdb_pwField;
	}

	/**
	 * This method initializes bestpeerdb_btnSave	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getbestpeerdb_btnSave() {
		if (bestpeerdb_btnSave == null) {
			bestpeerdb_btnSave = new JButton();
			bestpeerdb_btnSave.setText("Save");
			bestpeerdb_btnSave.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
			bestpeerdb_btnSave.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							DBProperty dbProperty = new DBProperty();
							String dbtype = (String)DBConfigureView.this.bestpeerdb_driverComboBox.getSelectedItem();
							String driver = null;
							
							if(dbtype.equals("MySQL"))
							{
								driver = dbProperty.mysqlDriver;
							}
							else if(dbtype.equals("Oracle"))
							{
								driver = dbProperty.oracleDriver;
							}
							else if(dbtype.equals("SQLServer"))
							{
								driver = dbProperty.sqlserverDriver;
							}
							else if(dbtype.equals("PostgreSQL"))
							{
								driver = dbProperty.postgresqlDriver;
							}
							
							String url = DBConfigureView.this.bestpeerdb_urlTextField.getText();
							String port = DBConfigureView.this.bestpeerdb_portTextField.getText();
							String username = DBConfigureView.this.bestpeerdb_usernameTextField.getText();
							String password = DBConfigureView.this.bestpeerdb_pwField.getText();
							String dbname = DBConfigureView.this.bestpeerdb_dbNameTextField.getText();
							
							Vector<String> vec = new Vector<String>();
							vec.add(driver);
							vec.add(url);
							vec.add(port);
							vec.add(username);
							vec.add(password);
							vec.add(dbname);
							

							dbProperty.put_bestpeer_cinfigure(vec);
							
							JOptionPane.showMessageDialog(null, "Successfully Save!");						
						}
					});
		}
		return bestpeerdb_btnSave;
	}

	private JButton getbestpeerdb_btnCheckConn() {
		if (bestpeerdb_btnCheckConn == null) {
			bestpeerdb_btnCheckConn = new JButton();
			bestpeerdb_btnCheckConn.setText("Check Connection");
			bestpeerdb_btnCheckConn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
			bestpeerdb_btnCheckConn.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							DBProperty dbProperty = new DBProperty();
							String dbtype = (String)DBConfigureView.this.localdb_driverComboBox.getSelectedItem();
							String driver = null;
							
							if(dbtype.equals("MySQL"))
							{
								driver = dbProperty.mysqlDriver;
							}
							else if(dbtype.equals("Oracle"))
							{
								driver = dbProperty.oracleDriver;
							}
							else if(dbtype.equals("SQLServer"))
							{
								driver = dbProperty.sqlserverDriver;
							}
							else if(dbtype.equals("PostgreSQL"))
							{
								driver = dbProperty.postgresqlDriver;
							}
							
							String url = DBConfigureView.this.bestpeerdb_urlTextField.getText();
							String port = DBConfigureView.this.bestpeerdb_portTextField.getText();
							String username = DBConfigureView.this.bestpeerdb_usernameTextField.getText();
							String password = DBConfigureView.this.bestpeerdb_pwField.getText();
							String dbname = DBConfigureView.this.bestpeerdb_dbNameTextField.getText();
							
							try
							{
								Class.forName(driver);
								Connection conn = DriverManager.getConnection(url + dbname, username, password);
								
								JOptionPane.showMessageDialog(null, "BESTPEER DB Setting is successful!");
								conn.close();
								
							}
							catch(Exception exception)
							{
								exception.printStackTrace();
								JOptionPane.showMessageDialog(null, "BESTPEER DB Setting is wrong!");
							}					
						}
					});
		}
		return bestpeerdb_btnCheckConn;
	}
	
	private JButton getBtnContinue() {
		if (btnContinue == null) {
			btnContinue = new JButton();
			btnContinue.setText("Save and Exit");
			btnContinue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
			btnContinue.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							DBProperty dbProperty = new DBProperty();
							String dbtype = (String)DBConfigureView.this.localdb_driverComboBox.getSelectedItem();
							String driver = null;
							
							if(dbtype.equals("MySQL"))
							{
								driver = dbProperty.mysqlDriver;
							}
							else if(dbtype.equals("Oracle"))
							{
								driver = dbProperty.oracleDriver;
							}
							else if(dbtype.equals("SQLServer"))
							{
								driver = dbProperty.sqlserverDriver;
							}
							else if(dbtype.equals("PostgreSQL"))
							{
								driver = dbProperty.postgresqlDriver;
							}
							
							String local_url = DBConfigureView.this.localdb_urlTextField.getText();
							String local_port = DBConfigureView.this.localdb_portTextField.getText();
							String local_username = DBConfigureView.this.localdb_usernameTextField.getText();
							String local_password = DBConfigureView.this.localdb_pwField.getText();
							String local_dbname = DBConfigureView.this.localdb_dbNameTextField.getText();
							
							Vector<String> vec = new Vector<String>();
							vec.add(driver);
							vec.add(local_url);
							vec.add(local_port);
							vec.add(local_username);
							vec.add(local_password);
							vec.add(local_dbname);
							

							dbProperty.put_localdb_configure(vec);
							
							dbtype = (String)DBConfigureView.this.bestpeerdb_driverComboBox.getSelectedItem();
										
							if(dbtype.equals("MySQL"))
							{
								driver = dbProperty.mysqlDriver;
							}
							else if(dbtype.equals("Oracle"))
							{
								driver = dbProperty.oracleDriver;
							}
							else if(dbtype.equals("SQLServer"))
							{
								driver = dbProperty.sqlserverDriver;
							}
							else if(dbtype.equals("PostgreSQL"))
							{
								driver = dbProperty.postgresqlDriver;
							}
							
							String bestpeer_url = DBConfigureView.this.bestpeerdb_urlTextField.getText();
							String bestpeer_port = DBConfigureView.this.bestpeerdb_portTextField.getText();
							String bestpeer_username = DBConfigureView.this.bestpeerdb_usernameTextField.getText();
							String bestpeer_password = DBConfigureView.this.bestpeerdb_pwField.getText();
							String bestpeer_dbname = DBConfigureView.this.bestpeerdb_dbNameTextField.getText();
							
							Vector<String> vec2 = new Vector<String>();
							vec2.add(driver);
							vec2.add(bestpeer_url);
							vec2.add(bestpeer_port);
							vec2.add(bestpeer_username);
							vec2.add(bestpeer_password);
							vec2.add(bestpeer_dbname);
							

							dbProperty.put_bestpeer_cinfigure(vec2);
							
							
							if(DBConfigureView.this.parent != null)
								DBConfigureView.this.parent.dispose();
						}
					});
		}
		return btnContinue;
	}
	
	
}  


/**
 * Dialog for congifuring the connection to the ERP database and BestPeer database 
 *
 */

public class DBConfigureFrame extends JFrame{	
	
	public DBConfigureFrame()
	{
		super("Database Configure");
		
		DBConfigureView configureView = new DBConfigureView(this);
		
		this.setLayout(new BorderLayout());
		this.add(configureView, BorderLayout.CENTER);
		
		this.setSize(new Dimension(800, 400));
		
		int X = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() - this.getSize().width) / 2;

		int Y = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() - this.getSize().height) / 3;

		this.setLocation(X, Y);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setVisible(true);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DBConfigureFrame app = new DBConfigureFrame();
		
	}

}
