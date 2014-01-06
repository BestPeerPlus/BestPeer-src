package sg.edu.nus.gui.server;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;


/**
 * Implement toobar and Logo of BestPeer in server peer GUI
 * 
 * @author Han Xixian
 * 
 */

public class Logo_Toolbar_Panel extends JPanel {

	private ServerGUI servergui = null;
	
	private JLabel logo = null;
	private ImageIcon bestLogo = null;

	private ServerToolbar serverToolbar = null;
	/**
	 * This is the default constructor
	 */
	public Logo_Toolbar_Panel(ServerGUI servergui) {
		super();
		
		this.servergui = servergui;
		
		bestLogo = new ImageIcon("./sg/edu/nus/gui/res/logo.gif");
		logo = new JLabel(bestLogo);
		
		serverToolbar = new ServerToolbar(servergui);

		this.setLayout(new BorderLayout());		
		
		initialize();
	}

	public void setComponentText()
	{
		serverToolbar.setComponentText();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		
		this.add(logo, BorderLayout.EAST);	
		this.add(serverToolbar, BorderLayout.WEST);
		this.setSize(600, 400);
	}
}
