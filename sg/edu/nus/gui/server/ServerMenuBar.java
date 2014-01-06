package sg.edu.nus.gui.server;

import javax.swing.JMenuBar;
import javax.swing.JMenu;

import javax.swing.JOptionPane;
import sg.edu.nus.peer.LanguageLoader;

/**
 * 
 * Implement menu bar for server peer GUI
 *
 */
public class ServerMenuBar extends JMenuBar {

	JMenu fileMenu = null;
	JMenu otherMenu = null;
	String file = "menu.file";
	String other = "menu.other";
	
	public ServerMenuBar()
	{
		fileMenu = new JMenu(LanguageLoader.getProperty(file));
		otherMenu = new JMenu(LanguageLoader.getProperty(other));
		
		this.add(fileMenu);
		this.add(otherMenu);
	}
	
	public void SetComponentText()
	{
		if(fileMenu != null)
			fileMenu.setText(LanguageLoader.getProperty(file));
		
		if(otherMenu != null)
			otherMenu.setText(LanguageLoader.getProperty(other));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
