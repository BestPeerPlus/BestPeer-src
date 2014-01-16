package sg.edu.nus.db.synchronizer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

public class SynchronizerGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2704812754697059744L;

	/******************************************/
	/**         for database connection    ***/
	/*****************************************/
	public String[] databaseType = { "ODBC", "SQLSever", "Oracle", "DB2",
			"MySQL" };

	public String driverString1;

	public String connString1;

	public String user1;

	public String user2;

	public String pass1;

	public String pass2;

	public String db1;

	public String db2;

	/************** end ********************/

	public JPanel mainPane;

	public JPanel leftPane;

	public JPanel midPane;

	public JPanel rightPane;

	public JList leftList;

	public JList rightList;

	public JButton exportButton;

	public JLabel driverLabel1;

	public JLabel dbName1;

	public JLabel userName1;

	public JLabel passName1;

	public JLabel driverLabel2;

	public JLabel dbName2;

	public JLabel userName2;

	public JLabel passName2;

	public JComboBox driverDropDown1;

	public JTextArea dbText1;

	public JTextArea userText1;

	public JPasswordField passText1;

	public JComboBox driverDropDown2;

	public JTextArea dbText2;

	public JTextArea userText2;

	public JPasswordField passText2;

	public JButton connect1;

	public JButton connect2;

	public SynchronizerGUI() {

		init();
	}

	private void init() {
		mainPane = new JPanel(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();

		c1.gridx = 0;
		c1.gridy = 0;
		c1.gridwidth = 3;
		c1.ipadx = 300;
		// c1.weighty = 1;
		c1.fill = GridBagConstraints.BOTH;

		leftPane = new JPanel(new GridBagLayout());

		mainPane.add(leftPane, c1);

		GridBagConstraints c2 = new GridBagConstraints();

		c2.gridx = 3;
		c2.gridy = 0;
		c2.gridwidth = 1;

		// c2.weighty = 1;
		c2.fill = GridBagConstraints.BOTH;

		midPane = new JPanel(new GridBagLayout());

		mainPane.add(midPane, c2);

		GridBagConstraints c3 = new GridBagConstraints();

		c3.gridx = 4;
		c3.gridy = 0;
		c3.gridwidth = 3;
		c3.ipadx = 300;
		// c3.weighty = 1;
		c3.fill = GridBagConstraints.BOTH;

		rightPane = new JPanel(new GridBagLayout());

		mainPane.add(rightPane, c3);

		String[] test1 = { "table1", "table2" };

		String[] test2 = { "table3", "table4" };

		leftList = new JList(test1);

		rightList = new JList(test2);

		exportButton = new JButton("->>");

		// leftPane.add(leftList);

		// rightPane.add(rightList);

		midPane.add(exportButton);

		driverLabel1 = new JLabel("Database Type:");

		driverLabel2 = new JLabel("Database Type:");

		dbName1 = new JLabel("Database Name:");

		dbName2 = new JLabel("Database Name:");

		userName1 = new JLabel("User:");

		userName2 = new JLabel("User:");

		passName1 = new JLabel("Password:");

		passName2 = new JLabel("Password:");

		driverDropDown1 = new JComboBox(databaseType);

		String[] mysql = { "MySQL" };
		driverDropDown2 = new JComboBox(mysql);

		dbText1 = new JTextArea();
		dbText1.setTabSize(20);

		dbText2 = new JTextArea();
		dbText2.setTabSize(20);

		userText1 = new JTextArea();
		userText1.setTabSize(20);

		userText2 = new JTextArea();
		userText2.setTabSize(20);

		passText1 = new JPasswordField();

		passText2 = new JPasswordField();

		connect1 = new JButton("Connect");

		connect2 = new JButton("Connect");

		GridBagConstraints c4 = new GridBagConstraints();

		c4.fill = GridBagConstraints.HORIZONTAL;
		c4.gridx = 0;
		c4.gridy = 0;
		c4.gridwidth = 1;
		c4.anchor = GridBagConstraints.PAGE_START;
		leftPane.add(driverLabel1, c4);

		c4.gridx = 1;
		c4.gridy = 0;
		c4.gridwidth = 2;
		leftPane.add(driverDropDown1, c4);

		c4.gridx = 3;
		c4.gridy = 0;
		c4.gridwidth = 1;
		leftPane.add(dbName1, c4);

		c4.gridx = 4;
		c4.gridy = 0;
		c4.gridwidth = 2;
		leftPane.add(dbText1, c4);

		c4.gridx = 0;
		c4.gridy = 1;
		c4.gridwidth = 1;
		leftPane.add(userName1, c4);

		c4.gridx = 1;
		c4.gridy = 1;
		c4.gridwidth = 2;
		leftPane.add(userText1, c4);

		c4.gridx = 3;
		c4.gridy = 1;
		c4.gridwidth = 1;
		leftPane.add(passName1, c4);

		c4.gridx = 4;
		c4.gridy = 1;
		c4.gridwidth = 2;
		leftPane.add(passText1, c4);

		this.add(mainPane);

		this.setSize(800, 600);

		this.setVisible(true);

	}

	public static void main(String[] args) {
		new SynchronizerGUI();
	}
}
