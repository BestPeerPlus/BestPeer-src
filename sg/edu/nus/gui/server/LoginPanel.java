package sg.edu.nus.gui.server;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import sg.edu.nus.peer.LanguageLoader;
import sg.edu.nus.peer.ServerPeer;

/**
 * 
 * Implement panel for users to login
 *
 */

public class LoginPanel extends JPanel implements ActionListener
{
	private ImageIcon iiLogo = null;
	private JLabel jlLogo = null;
	private JLabel jlDescription1 = null;
	private JLabel jlDescription2 = null;
	
	private JLabel jlBootstrapServer = null;
	private JComboBox jcbBootstrapServer = null;
	private JLabel jlPort = null;
	private JTextField jtfPort = null;
	
	private JLabel jlUser = null;
	private JTextField jtfUser = null;
	private JLabel jlPassword = null;
	private JPasswordField jtfPassword = null;
	private JLabel jlUserType = null;
	private JComboBox jcbUserType = null;
	private JCheckBox jchbSaveInfo = null;
	private String strSaveInfo = LanguageLoader.getProperty("Login.saveInfo");
	
	private JButton btnLogin = null;
	String strCmdLogin = "login";
	String strCmdUserType = "usertype";
	
	
	private OperatePanel parentComponent = null;
	
	String[] userTypes = new String[]{LanguageLoader.getProperty("UserType.professional"), 
			LanguageLoader.getProperty("UserType.normal")};

	String[] serverTypes = new String[]{LanguageLoader.getProperty("Login.bootstrapserver"), 
			LanguageLoader.getProperty("Login.serverpeer")};

	public LoginPanel(OperatePanel parentComponent)
	{
		this.parentComponent = parentComponent;
		
		this.iiLogo = new ImageIcon("./sg/edu/nus/gui/res/newlogo.gif");
		this.jlLogo = new JLabel(this.iiLogo);
		this.jlDescription1 = new JLabel(LanguageLoader.getProperty("Login.firstdescription"));
		this.jlDescription2 = new JLabel(LanguageLoader.getProperty("Login.seconddescription"));
		
		this.jlBootstrapServer = new JLabel(serverTypes[0]);
		this.jcbBootstrapServer = new JComboBox(ServerPeer.BOOTSTRAP_SERVER_LIST.split(":"));
		
		this.jcbBootstrapServer.setEditable(true);
		this.jcbBootstrapServer.addActionListener(this);
		
		this.jlPort = new JLabel(LanguageLoader.getProperty("Login.port"));
		this.jtfPort = new JTextField("30000");
				
		this.jlUser = new JLabel(LanguageLoader.getProperty("Login.user"));
		this.jtfUser = new JTextField();
		this.jlPassword = new JLabel(LanguageLoader.getProperty("Login.password"));
		this.jtfPassword = new JPasswordField();
		this.jlUserType = new JLabel(LanguageLoader.getProperty("Login.usertype"));
		this.jcbUserType = new JComboBox();
		this.jcbUserType.addItem(userTypes[0]);
		this.jcbUserType.addItem(userTypes[1]);

		this.jcbUserType.addActionListener(this);
		this.jcbUserType.setActionCommand(strCmdUserType);

		
		this.jchbSaveInfo = new JCheckBox(strSaveInfo);
		this.btnLogin = new JButton(LanguageLoader.getProperty("Login.btnLogin"));
		
		this.btnLogin.addActionListener(this);
		this.btnLogin.setActionCommand(strCmdLogin);
		
		this.jtfPort.setColumns(10);
		this.jtfUser.setColumns(18);
		this.jtfPassword.setColumns(18);
		this.jchbSaveInfo.setBackground(OperatePanel.panel_color);
		
		initialize();
	}
	
	public void initialize()
	{
		GridBagLayout layout = new GridBagLayout();
		
		this.setLayout(layout);
		
		GridBagConstraints jlLogo_constraints = new GridBagConstraints();
		jlLogo_constraints.gridx = 0;
		jlLogo_constraints.gridy = 0;
		jlLogo_constraints.gridwidth = 2;
		jlLogo_constraints.gridheight = 1;
		jlLogo_constraints.weightx = 100;
		jlLogo_constraints.weighty = 0;
		jlLogo_constraints.fill = GridBagConstraints.HORIZONTAL;
		jlLogo_constraints.insets.bottom = 10;
		
		GridBagConstraints jlDescription1_constraints = new GridBagConstraints();
		jlDescription1_constraints.gridx = 0;
		jlDescription1_constraints.gridy = 1;
		jlDescription1_constraints.gridwidth = 2;
		jlDescription1_constraints.gridheight = 1;
		jlDescription1_constraints.weightx = 0;
		jlDescription1_constraints.weighty = 0;
		jlDescription1_constraints.fill = GridBagConstraints.NONE;
		jlDescription1_constraints.insets.bottom = 2;
		
		GridBagConstraints jlDescription2_constraints = new GridBagConstraints();
		jlDescription2_constraints.gridx = 0;
		jlDescription2_constraints.gridy = 2;
		jlDescription2_constraints.gridwidth = 2;
		jlDescription2_constraints.gridheight = 1;
		jlDescription2_constraints.weightx = 0;
		jlDescription2_constraints.weighty = 0;
		jlDescription2_constraints.fill = GridBagConstraints.NONE;
		jlDescription2_constraints.insets.bottom = 5;
		
		GridBagConstraints jlBootstrapServer_constraints = new GridBagConstraints();
		jlBootstrapServer_constraints.gridx = 0;
		jlBootstrapServer_constraints.gridy = 3;
		jlBootstrapServer_constraints.gridwidth = 1;
		jlBootstrapServer_constraints.gridheight = 1;
		jlBootstrapServer_constraints.weightx = 50;
		jlBootstrapServer_constraints.weighty = 0;
		jlBootstrapServer_constraints.fill = GridBagConstraints.NONE;
		jlBootstrapServer_constraints.anchor = GridBagConstraints.EAST;
		jlBootstrapServer_constraints.insets.bottom = 5;
		
		GridBagConstraints jcbBootstrapServer_constraints = new GridBagConstraints();
		jcbBootstrapServer_constraints.gridx = 1;
		jcbBootstrapServer_constraints.gridy = 3;
		jcbBootstrapServer_constraints.gridwidth = 1;
		jcbBootstrapServer_constraints.gridheight = 1;
		jcbBootstrapServer_constraints.weightx = 50;
		jcbBootstrapServer_constraints.weighty = 0;
		jcbBootstrapServer_constraints.fill = GridBagConstraints.NONE;
		jcbBootstrapServer_constraints.anchor = GridBagConstraints.WEST;
		jcbBootstrapServer_constraints.insets.bottom = 5;		

		GridBagConstraints jlPort_constraints = new GridBagConstraints();
		jlPort_constraints.gridx = 0;
		jlPort_constraints.gridy = 4;
		jlPort_constraints.gridwidth = 1;
		jlPort_constraints.gridheight = 1;
		jlPort_constraints.weightx = 50;
		jlPort_constraints.weighty = 0;
		jlPort_constraints.fill = GridBagConstraints.NONE;
		jlPort_constraints.anchor = GridBagConstraints.EAST;
		jlPort_constraints.insets.bottom = 5;
		
		GridBagConstraints jtfPort_constraints = new GridBagConstraints();
		jtfPort_constraints.gridx = 1;
		jtfPort_constraints.gridy = 4;
		jtfPort_constraints.gridwidth = 1;
		jtfPort_constraints.gridheight = 1;
		jtfPort_constraints.weightx = 50;
		jtfPort_constraints.weighty = 0;
		jtfPort_constraints.fill = GridBagConstraints.NONE;
		jtfPort_constraints.anchor = GridBagConstraints.WEST;
		jtfPort_constraints.insets.bottom = 5;
						
		GridBagConstraints jlUser_constraints = new GridBagConstraints();
		jlUser_constraints.gridx = 0;
		jlUser_constraints.gridy = 5;
		jlUser_constraints.gridwidth = 1;
		jlUser_constraints.gridheight = 1;
		jlUser_constraints.weightx = 50;
		jlUser_constraints.weighty = 0;
		jlUser_constraints.fill = GridBagConstraints.NONE;
		jlUser_constraints.anchor = GridBagConstraints.EAST;
		jlUser_constraints.insets.bottom = 5;
		
		GridBagConstraints jtfUser_constraints = new GridBagConstraints();
		jtfUser_constraints.gridx = 1;
		jtfUser_constraints.gridy = 5;
		jtfUser_constraints.gridwidth = 1;
		jtfUser_constraints.gridheight = 1;
		jtfUser_constraints.weightx = 50;
		jtfUser_constraints.weighty = 0;
		jtfUser_constraints.fill = GridBagConstraints.NONE;
		jtfUser_constraints.anchor = GridBagConstraints.WEST;
		jtfUser_constraints.insets.bottom = 5;
		
		GridBagConstraints jlPassword_constraints = new GridBagConstraints();
		jlPassword_constraints.gridx = 0;
		jlPassword_constraints.gridy = 6;
		jlPassword_constraints.gridwidth = 1;
		jlPassword_constraints.gridheight = 1;
		jlPassword_constraints.weightx = 50;
		jlPassword_constraints.weighty = 0;
		jlPassword_constraints.fill = GridBagConstraints.NONE;		
		jlPassword_constraints.anchor = GridBagConstraints.EAST;
		jlPassword_constraints.insets.bottom = 5;
		
		GridBagConstraints jtfPassword_constraints = new GridBagConstraints();
		jtfPassword_constraints.gridx = 1;
		jtfPassword_constraints.gridy = 6;
		jtfPassword_constraints.gridwidth = 1;
		jtfPassword_constraints.gridheight = 1;
		jtfPassword_constraints.weightx = 50;
		jtfPassword_constraints.weighty = 0;
		jtfPassword_constraints.fill = GridBagConstraints.NONE;		
		jtfPassword_constraints.anchor = GridBagConstraints.WEST;
		jtfPassword_constraints.insets.bottom = 5;
		
		GridBagConstraints jlUserType_constraints = new GridBagConstraints();
		jlUserType_constraints.gridx = 0;
		jlUserType_constraints.gridy = 7;
		jlUserType_constraints.gridwidth = 1;
		jlUserType_constraints.gridheight = 1;
		jlUserType_constraints.weightx = 50;
		jlUserType_constraints.weighty = 0;
		jlUserType_constraints.fill = GridBagConstraints.NONE;
		jlUserType_constraints.anchor = GridBagConstraints.EAST;
		jlUserType_constraints.insets.bottom = 5;
		
		GridBagConstraints jcbUserType_constraints = new GridBagConstraints();
		jcbUserType_constraints.gridx = 1;
		jcbUserType_constraints.gridy = 7;
		jcbUserType_constraints.gridwidth = 1;
		jcbUserType_constraints.gridheight = 1;
		jcbUserType_constraints.weightx = 50;
		jcbUserType_constraints.weighty = 0;
		jcbUserType_constraints.fill = GridBagConstraints.NONE;	
		jcbUserType_constraints.anchor = GridBagConstraints.WEST;
		jcbUserType_constraints.insets.bottom = 5;
		
		GridBagConstraints jchbSaveInfo_constraints = new GridBagConstraints();
		jchbSaveInfo_constraints.gridx = 1;
		jchbSaveInfo_constraints.gridy = 8;
		jchbSaveInfo_constraints.gridwidth = 1;
		jchbSaveInfo_constraints.gridheight = 1;
		jchbSaveInfo_constraints.weightx = 50;
		jchbSaveInfo_constraints.weighty = 0;
		jchbSaveInfo_constraints.fill = GridBagConstraints.NONE;	
		jchbSaveInfo_constraints.anchor = GridBagConstraints.WEST;
		jchbSaveInfo_constraints.insets.bottom = 5;

		GridBagConstraints btnLogin_constraints = new GridBagConstraints();
		btnLogin_constraints.gridx = 1;
		btnLogin_constraints.gridy = 9;
		btnLogin_constraints.gridwidth = 1;
		btnLogin_constraints.gridheight = 1;
		btnLogin_constraints.weightx = 50;
		btnLogin_constraints.weighty = 0;
		btnLogin_constraints.fill = GridBagConstraints.NONE;	
		btnLogin_constraints.anchor = GridBagConstraints.WEST;
		
		JPanel panel = new JPanel();
		panel.setLayout(layout);
		
		panel.add(this.jlLogo, jlLogo_constraints);		
		panel.add(this.jlDescription1, jlDescription1_constraints);
		panel.add(this.jlDescription2, jlDescription2_constraints);
		
		panel.add(this.jlBootstrapServer, jlBootstrapServer_constraints);
		panel.add(this.jcbBootstrapServer, jcbBootstrapServer_constraints);	
		
		panel.add(this.jlPort, jlPort_constraints);
		panel.add(this.jtfPort, jtfPort_constraints);	
		
		panel.add(this.jlUser, jlUser_constraints);
		panel.add(this.jtfUser, jtfUser_constraints);		
		panel.add(this.jlPassword, jlPassword_constraints);
		panel.add(this.jtfPassword, jtfPassword_constraints);		
		panel.add(this.jlUserType, jlUserType_constraints);
		panel.add(this.jcbUserType, jcbUserType_constraints);
		panel.add(this.jchbSaveInfo, jchbSaveInfo_constraints);
		panel.add(this.btnLogin, btnLogin_constraints);	
		
//		panel.setBorder(BorderFactory.createTitledBorder(
//				BorderFactory.createBevelBorder(4), 
//				LanguageLoader.getProperty("PanelTitle.login")));	
		panel.setBackground(OperatePanel.panel_color);
		
		GridBagConstraints panel_constraints = new GridBagConstraints();
		panel_constraints.gridx = 0;
		panel_constraints.gridy = 0;
		panel_constraints.gridwidth = 1;
		panel_constraints.gridheight = 1;
		panel_constraints.weightx = 0;
		panel_constraints.weighty = 0;
		panel_constraints.fill = GridBagConstraints.NONE;
		panel_constraints.anchor = GridBagConstraints.CENTER;		
			
		this.add(panel, panel_constraints);
		
		this.setBackground(OperatePanel.panel_color);
	}
	
	protected boolean checkValue()
	{
		return true;
	}
	
	public void actionPerformed(ActionEvent event) 
	{
//		// action due to update combobox bootstrap address
//		if (event.getSource().getClass().equals(JComboBox.class)){
//			JComboBox combo = (JComboBox)event.getSource();
//			System.out.println(combo.getSelectedItem());
//			//JOptionPane.showMessageDialog(null,combo.getSelectedItem());
//			//strColumnType = (String)combo.getSelectedItem();
//		}		

	    String cmd = event.getActionCommand();
	    if (cmd.equals(strCmdUserType)) {
	    	//JOptionPane.showMessageDialog(this, "change user type");
	    	jlBootstrapServer.setText(serverTypes[jcbUserType.getSelectedIndex()]);
	    }
		/* if login */
		if (cmd.equals(strCmdLogin)) {

			if (checkValue()) {
				try {
					/* step 1: update configuration */
					ServerPeer.BOOTSTRAP_SERVER = (String) jcbBootstrapServer
							.getSelectedItem();
					ServerPeer.BOOTSTRAP_SERVER_PORT = Integer.parseInt(jtfPort
							.getText().trim());
					ServerPeer.write();

					/* step 2: perform login request */
					if (ServerGUI.tryStartService()) {
						/* create message to be sent out */
						String user = jtfUser.getText().trim();
						String pwd = new String(jtfPassword.getPassword());

						String userType = (String) this.jcbUserType
								.getSelectedItem();
						parentComponent.setUserType(userType);

						/* perform login request here */
						ServerPeer serverpeer = this.parentComponent
								.getServergui().getServerpeer();
						serverpeer.performLoginRequest(this, user, pwd);

					} else {
						JOptionPane.showMessageDialog(this,
								"A program is running on the port "
										+ ServerPeer.LOCAL_SERVER_PORT
										+ "\r\n Please config your local "
										+ "server port");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
