/*
 * @(#) FeedbackDialog.java 1.0 2006-2-9
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */

package sg.edu.nus.gui;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import sg.edu.nus.gui.table.*;
import sg.edu.nus.peer.LanguageLoader;

/**
 * Implement a dialog used for send bug or other helpful
 * information to the developers.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-2-9
 */

public class FeedbackDialog extends AbstractDialog implements ActionListener
{

	// private members
	private static final long serialVersionUID = 6626363788184394909L;
	
	private static final int FIELD_NUMBER = 5;
	private static final String historyFile = "./history.dat";
	
	private JTextField tfName;
	private JTextField tfEmail;
	private JComboBox  cbSubject;
	private JTextArea  taMessage;
	
	/**
	 * The name of buttons.
	 */
	protected final String[] btnName = {LanguageLoader.getProperty("button.send"), LanguageLoader.getProperty("button.cancel")};
	/**
	 * The command string used for buttons.
	 */
	protected final String[] command = {"send", "cancel"};
	
	public FeedbackDialog(Frame gui, String title, boolean model, int height, int width) 
	{
		super(gui, title, model, height, width);
		this.setLayout(new BorderLayout());
		
		/* make configure panel and add it to content pane */
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		JPanel panel = null;
		panel = this.makeFeedbackPane();
		tabbedPane.add(LanguageLoader.getProperty("tab.feedback"), panel);

		panel = this.makeHistoryPane();
		tabbedPane.add(LanguageLoader.getProperty("tab.error"), panel);
		tabbedPane.setSelectedIndex(0);
		
		this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	/**
	 * Make the panel used for replying feedback information.
	 * 
	 * @return the instance of <code>JPanel</code>
	 */
	protected JPanel makeFeedbackPane()
	{
		JPanel panel = new JPanel(new BorderLayout());
		
		JLabel label = null;
		JPanel textPane = new JPanel(new GridLayout(6, 1, 0, 0));
		
		label = new JLabel(LanguageLoader.getProperty("label.name"));
		textPane.add(label);
		tfName = new JTextField();
		textPane.add(tfName);

		label = new JLabel(LanguageLoader.getProperty("label.email"));
		textPane.add(label);
		tfEmail = new JTextField();
		textPane.add(tfEmail);

		label = new JLabel(LanguageLoader.getProperty("label.subject")); 
		textPane.add(label);
		
		String[] subject = {LanguageLoader.getProperty("label.bug"), LanguageLoader.getProperty("label.feature"), LanguageLoader.getProperty("label.technical"), LanguageLoader.getProperty("label.suggest")};
		cbSubject = new JComboBox(subject);
		cbSubject.setEditable(false);
		cbSubject.setSelectedIndex(0);
		textPane.add(cbSubject);

		JPanel infoPane = new JPanel(new BorderLayout());
		
		label = new JLabel(LanguageLoader.getProperty("label.info"));
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		infoPane.add(label, BorderLayout.NORTH);
		taMessage = new JTextArea();
		taMessage.setLineWrap(true);
		taMessage.setWrapStyleWord(true);
		infoPane.add(new JScrollPane(taMessage), BorderLayout.CENTER);
		infoPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		
		JPanel decoratePane = new JPanel(new BorderLayout());
		
		decoratePane.setBorder(BorderFactory.createTitledBorder(null, "", 
				TitledBorder.DEFAULT_JUSTIFICATION,	TitledBorder.DEFAULT_POSITION, 
				null, null));
		decoratePane.add(textPane, BorderLayout.NORTH);
		decoratePane.add(infoPane, BorderLayout.CENTER);
		
		JPanel buttonPane = this.makeButtonPane();
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.add(decoratePane, BorderLayout.CENTER);
		panel.add(buttonPane, BorderLayout.SOUTH);
		
		return panel;
	}
	
	/**
	 * Make the panel used for displaying history information.
	 * 
	 * @return the instance of <code>JPanel</code>
	 */
	protected JPanel makeHistoryPane()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		/* setup a sorted table */
		String[] header = {LanguageLoader.getProperty("table.reportdate"), LanguageLoader.getProperty("table.feedtype"), LanguageLoader.getProperty("table.reportname"), LanguageLoader.getProperty("table.desp"), LanguageLoader.getProperty("table.fix")};
		int[] width = {100, 100, 150, 200, 100};
		
		SortedTableModel model = new SortedTableModel(header);
		TableSorter sorter = new TableSorter(model);
		SortedTable table = new SortedTable(sorter, width);
		sorter.setTableHeader(table.getTableHeader());
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setGridColor(Color.LIGHT_GRAY);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		try
		{
			File file = new File(historyFile);
			if (file.exists())
			{
				BufferedReader in = new BufferedReader(new FileReader(file));
				StringTokenizer token;
				
				String line = new String();
				while ((line = in.readLine()) != null)
				{
					/*
					 * History File Format:
					 * date # type # reporter # description # fix date
					 */
					token = new StringTokenizer(line, "#");
					
					/* if record format is incorrect */
					if (token.countTokens() != FIELD_NUMBER)
						continue;
	
					String[] item = new String[FIELD_NUMBER];
					for (int i = 0; i < FIELD_NUMBER; i++)
						item[i] = token.nextToken();
					
					model.insertRow(model.getRowCount(), item);
				}
				
				in.close();
			}
			else
			{
				file.createNewFile();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		panel.add(new JScrollPane(table), BorderLayout.CENTER);
		
		return panel;
	}

	/**
	 * Make the button panel.
	 * 
	 * @return the instance of <code>JPanel</code> with buttons
	 */
	protected JPanel makeButtonPane()
	{
		JButton button = null;
		JPanel panel = new JPanel();

		/* set panel layout */
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.add(Box.createHorizontalGlue());
		
        /* add buttons to panel */
		button = makeButton(btnName[0], command[0]);
        panel.add(button);

        panel.add(Box.createRigidArea(new Dimension(10, 0)));
		
        button = makeButton(btnName[1], command[1]);
        panel.add(button);
        
        return panel;
	}
	
	/**
	 * Make individual button.
	 * 
	 * @param name the button name
	 * @param cmd the command string
	 * @return the instance of <code>JButton</code>
	 */
	protected JButton makeButton(String name, String cmd)
	{ 
		JButton button = new JButton(name);
		button.setActionCommand(cmd);
        button.addActionListener(this);
        return button;
	}
	
	/**
	 * Check whether user input is valid.
	 * 
	 * @return if all user input are valid, return <code>true</code>;
	 * 			otherwise, return <code>false</code>.
	 */
	protected boolean checkValue()
	{
		String str = tfEmail.getText().trim();
		if (str.equals("") || str.startsWith("@") || (str.indexOf("@") == -1))
		{
			JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg5"));
			tfEmail.grabFocus();
			return false;
		}
		
		str = taMessage.getText().trim();
		if (str.equals(""))
		{
			JOptionPane.showMessageDialog(gui, LanguageLoader.getProperty("message.msg6"));
			taMessage.grabFocus();
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void processWhenWindowClosing() 
	{
	}

	public void actionPerformed(ActionEvent event) 
	{
		String cmd = event.getActionCommand();
		
		/* if press "send" button */
		if (cmd.equals(command[0]))
		{
			if (this.checkValue())
			{
				
			}
		}
		/* if press "cancel" button */
		else if (cmd.equals(command[1]))
		{
			dispose();
		}
	}
	
}