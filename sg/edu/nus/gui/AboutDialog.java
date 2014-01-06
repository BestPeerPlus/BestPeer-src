/*
 * @(#) AboutDialog.java 1.0 2006-1-27
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.awt.*;
import javax.swing.*;

/**
 * Implement the about dialog.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-27
 */

public class AboutDialog extends AbstractDialog
{

	// private members

	private static final long serialVersionUID = -8086137545465429707L;

	/**
	 * Construct the about dialog.
	 * 
	 * @param gui the handler of the main frame
	 */
	public AboutDialog(Frame gui, String title, String image, String text)
	{
		super(gui, title, true, 480, 150);

		/* swing uses HTML statements */
		JLabel lbLogo = new JLabel(new ImageIcon(image));
		JTextArea txArea = new JTextArea();
		txArea.setEditable(false);
		txArea.setText(text);
		
		this.getContentPane().add(lbLogo, BorderLayout.WEST);
		this.getContentPane().add(txArea, BorderLayout.CENTER);
		
		/* show dialog */
		this.setVisible(true);
	}
	
	@Override
	protected void processWhenWindowClosing() 
	{
	}
	
}