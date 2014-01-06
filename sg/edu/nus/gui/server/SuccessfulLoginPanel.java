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
 * Implement welcome panel after local admins login successfully
 *
 */
public class SuccessfulLoginPanel extends JPanel implements ActionListener
{
	private ImageIcon iiLogo = null;
	private JLabel jlLogo = null;
	private JLabel jlWelcomeMessage = null;
	private JButton btnLoginout = null;
	
	private OperatePanel parentComponent = null;
	
	public SuccessfulLoginPanel(OperatePanel parentComponent)
	{
		this.parentComponent = parentComponent;
		
		this.iiLogo = new ImageIcon("./sg/edu/nus/gui/res/newlogo.gif");
		this.jlLogo = new JLabel(this.iiLogo);

		this.jlWelcomeMessage = new JLabel();
		
		String welcomeMessage = "<html><font color=#0000ff size=10> Welcome to Corporate Database!</font></html>";
		this.jlWelcomeMessage.setText(welcomeMessage);
		
		this.btnLoginout = new JButton(LanguageLoader.getProperty("Login.btnLoginout"));
		
		this.btnLoginout.addActionListener(this);
		
		
		
		initialize();
	}
	
	public void initialize()
	{
		GridBagLayout layout = new GridBagLayout();
		
		this.setLayout(layout);
		
		GridBagConstraints jlLogo_constraints = new GridBagConstraints();
		jlLogo_constraints.gridx = 0;
		jlLogo_constraints.gridy = 0;
		jlLogo_constraints.gridwidth = 1;
		jlLogo_constraints.gridheight = 1;
		jlLogo_constraints.weightx = 100;
		jlLogo_constraints.weighty = 0;
		jlLogo_constraints.fill = GridBagConstraints.HORIZONTAL;
		jlLogo_constraints.insets.bottom = 20;

		GridBagConstraints jlWelcomeMessage_constraints = new GridBagConstraints();
		jlWelcomeMessage_constraints.gridx = 0;
		jlWelcomeMessage_constraints.gridy = 1;
		jlWelcomeMessage_constraints.gridwidth = 1;
		jlWelcomeMessage_constraints.gridheight = 1;
		jlWelcomeMessage_constraints.weightx = 50;
		jlWelcomeMessage_constraints.weighty = 50;
		jlWelcomeMessage_constraints.fill = GridBagConstraints.NONE;	
		jlWelcomeMessage_constraints.anchor = GridBagConstraints.CENTER;
		jlWelcomeMessage_constraints.insets.bottom = 20;
		
		GridBagConstraints btnLoginout_constraints = new GridBagConstraints();
		btnLoginout_constraints.gridx = 0;
		btnLoginout_constraints.gridy = 2;
		btnLoginout_constraints.gridwidth = 1;
		btnLoginout_constraints.gridheight = 1;
		btnLoginout_constraints.weightx = 50;
		btnLoginout_constraints.weighty = 0;
		btnLoginout_constraints.fill = GridBagConstraints.NONE;	
		btnLoginout_constraints.anchor = GridBagConstraints.EAST;
		
		JPanel panel = new JPanel();
		panel.setLayout(layout);
		
		
		
		panel.add(this.jlLogo, jlLogo_constraints);		
		panel.add(this.jlWelcomeMessage, jlWelcomeMessage_constraints);				
		panel.add(this.btnLoginout, btnLoginout_constraints);	
		
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
		/* perform login request here */
		ServerPeer serverpeer = this.parentComponent.getServergui().getServerpeer();
		serverpeer.performLogoutRequest();
		
		this.parentComponent.setComponentAt(OperatePanel.TAB_LOGINMANAGER_INDEX, 
				new LoginPanel(this.parentComponent));
		
		this.parentComponent.setEnabledAt(OperatePanel.TAB_DBMANAGER_INDEX, false);
		this.parentComponent.setEnabledAt(OperatePanel.TAB_QUERYMANAGER_INDEX, false);
		this.parentComponent.setEnabledAt(OperatePanel.TAB_ACCESSMANAGER_INDEX, false);
	}
}
