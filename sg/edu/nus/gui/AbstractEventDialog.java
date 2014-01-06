package sg.edu.nus.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import sg.edu.nus.peer.LanguageLoader;

/**
 * An abstract dialog that displays network event received by a peer
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-26
 */

public abstract class AbstractEventDialog extends AbstractDialog implements ActionListener
{

	// private member
	private static final long serialVersionUID = 3524367182104191097L;
	
	/**
	 * The area used for displaying the description of the event. 
	 */
	protected JTextArea taDescription;
	/**
	 * The area used for displaying the date that event happens.
	 */
	protected JTextField tfDate;
	/**
	 * The area used for displaying the time that event happens.
	 */
	protected JTextField tfTime;
	/**
	 * The area used for displaying the event type.
	 */
	protected JTextField tfType;
	/**
	 * The area used for displaying the host name who sends requrest.
	 */
	protected JTextField tfHost;
	/**
	 * The area used for displaying the ip address who sends request.
	 */
	protected JTextField tfSource;
	/**
	 * The area used for displaying the current user of the peer.
	 */
	protected JTextField tfUser;
	
	protected String[] btnName = {"up", "down", LanguageLoader.getProperty("label.close")};
	protected String[] command = {"up", "down", "close"};
	
	/**
	 * Constructor.
	 * 
	 * @param gui the owner of the dialog
	 * @param model if <code>true</code>, using model style; otherwise, non-model style
	 */
	public AbstractEventDialog(Frame gui, String title, boolean model, int height, int width) 
	{
		super(gui, title, model, height, width);
		
		this.setLayout(new BorderLayout());
		
		/* make content pane */
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		tabbedPane.add(LanguageLoader.getProperty("label.eventname"), this.makeContentPane());
		this.add(tabbedPane, BorderLayout.CENTER);
		
		/* make button pane */
		this.add(this.makeBottomPane(), BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	/**
	 * Returns the <code>JPanel</code> with <code>JComponent</code>s to
	 * display the content of <code>LogEvent</code>.
	 * 
	 * @return the <code>JPanel</code>
	 */
	protected JPanel makeContentPane()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(Color.WHITE);
		panel.setOpaque(true);
		
		// begin of right button panel
		JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setOpaque(true);
		
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.PAGE_AXIS));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        btnPanel.add(Box.createHorizontalGlue());
		
        JButton button = this.makeButton(btnName[0], command[0], true);
        btnPanel.add(button);
        btnPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        button = this.makeButton(btnName[1], command[1], true);
        btnPanel.add(button);
        // end of right button panel

        // begin of info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setOpaque(true);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // begin of left panel
        JPanel leftPanel = new JPanel(new GridLayout(0, 1, 8, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setOpaque(true);

        JPanel tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBackground(Color.WHITE);
        tmpPanel.setOpaque(true);
        JLabel label  = new JLabel(LanguageLoader.getProperty("label.date"));
        tfDate = this.makeTextField(false, Color.WHITE, JTextField.LEFT);
        tmpPanel.add(label, BorderLayout.WEST);
        tmpPanel.add(tfDate, BorderLayout.CENTER);
        
        leftPanel.add(tmpPanel);

        tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBackground(Color.WHITE);
        tmpPanel.setOpaque(true);
        label  = new JLabel(LanguageLoader.getProperty("label.time"));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        tfTime = this.makeTextField(false, Color.WHITE, JTextField.LEFT);
        tmpPanel.add(label, BorderLayout.WEST);
        tmpPanel.add(tfTime, BorderLayout.CENTER);
        
        leftPanel.add(tmpPanel);

        tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBackground(Color.WHITE);
        tmpPanel.setOpaque(true);
        label  = new JLabel(LanguageLoader.getProperty("label.type"));
        tfType = this.makeTextField(false, Color.WHITE, JTextField.LEFT);
        tmpPanel.add(label, BorderLayout.WEST);
        tmpPanel.add(tfType, BorderLayout.CENTER);
        
        leftPanel.add(tmpPanel);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
        // end of left panel

        // begin of right panel
        JPanel rightPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setOpaque(true);

        tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBackground(Color.WHITE);
        tmpPanel.setOpaque(true);
        label  = new JLabel(LanguageLoader.getProperty("label.host"));
        tfHost = this.makeTextField(false, Color.WHITE, JTextField.LEFT);
        tmpPanel.add(label, BorderLayout.WEST);
        tmpPanel.add(tfHost, BorderLayout.CENTER);
        
        rightPanel.add(tmpPanel);

        tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBackground(Color.WHITE);
        tmpPanel.setOpaque(true);
        label  = new JLabel(LanguageLoader.getProperty("label.ip"));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
        tfSource = this.makeTextField(false, Color.WHITE, JTextField.LEFT);
        tmpPanel.add(label, BorderLayout.WEST);
        tmpPanel.add(tfSource, BorderLayout.CENTER);
        
        rightPanel.add(tmpPanel);

        tmpPanel = new JPanel(new BorderLayout());
        tmpPanel.setBackground(Color.WHITE);
        tmpPanel.setOpaque(true);
        label  = new JLabel(LanguageLoader.getProperty("label.user"));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
        tfUser = this.makeTextField(false, Color.WHITE, JTextField.LEFT);
        tmpPanel.add(label, BorderLayout.WEST);
        tmpPanel.add(tfUser, BorderLayout.CENTER);
        
        rightPanel.add(tmpPanel);
        // end of right panel
        
        infoPanel.add(leftPanel, BorderLayout.WEST);
        infoPanel.add(rightPanel, BorderLayout.CENTER);
        // end of info panel
        
        // begin of center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setOpaque(true);
        
        centerPanel.add(infoPanel, BorderLayout.CENTER);
        centerPanel.add(btnPanel, BorderLayout.EAST);
        // end of center panel
        
        // begin of description panel
        JPanel descrpPanel = new JPanel(new BorderLayout());
        Color foreColor = descrpPanel.getBackground();
        descrpPanel.setBackground(Color.WHITE);
        descrpPanel.setOpaque(true);
        
        label = new JLabel(LanguageLoader.getProperty("label.desp"));
        descrpPanel.add(label, BorderLayout.NORTH);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        
        // set font of description panel
        Font font = tfDate.getFont();
        
        taDescription = new JTextArea();
        taDescription.setFont(font);
        taDescription.setBackground(foreColor);
        taDescription.setEditable(false);
        taDescription.setLineWrap(false);
		taDescription.setBorder(BorderFactory.createLoweredBevelBorder());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setOpaque(true);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		scrollPane.setViewportView(taDescription);
        
		descrpPanel.add(scrollPane, BorderLayout.CENTER);
        // end of description panel
        
        panel.add(centerPanel, BorderLayout.NORTH);
        panel.add(descrpPanel, BorderLayout.CENTER);
		return panel;
	}
	
	/**
	 * Returns the <code>JPanel</code> with close button.
	 * 
	 * @return the <code>JPanel</code>
	 */
	protected JPanel makeBottomPane()
	{
		JButton button = null;
		JPanel panel = new JPanel();

		/* set panel layout */
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(Box.createHorizontalGlue());
		
        /* add buttons to panel */
		button = this.makeButton(btnName[2], command[2], false);

		panel.add(button);
        
        return panel;
	}
	
	/**
	 * Returns the instance of <code>JButton</code> with specified parameters.
	 * 
	 * @param name the display name of <code>JButton</code>
	 * @param cmd the command of <code>JButton</code>
	 * @param onlyIcon if <code>true</code>, then the <code>JButton</code> 
	 * 					with <code>Icon</code> only; otherwise, only with name
	 * @return the instance of <code>JButton</code>
	 */
	protected JButton makeButton(String name, String cmd, boolean onlyIcon)
	{ 
        JButton button = new JButton();
        if (onlyIcon) 
        {
	        String imageLoc = AbstractMainFrame.SRC_PATH + name + ".png";
            button.setIcon(new ImageIcon(imageLoc, name));
        } 
        else 
        {
            button.setText(name);
        }
        button.setActionCommand(cmd);
        button.addActionListener(this);

        return button;
	}

	/**
	 * Returns the instance of <code>JTextField</code> with specified parameters.
	 * 
	 * @param editable if <code>true</code>, then <code>JTextField</code> can be edited;
	 * 					otherwise, <code>JTextField</code> cannot be edited
	 * @param color the background color of <code>JTextField</code>
	 * @param align the alignment style of the content of <code>JTextField</code>
	 * @return the instance of <code>JTextField</code>
	 */
	protected JTextField makeTextField(boolean editable, Color color, int align)
	{
		JTextField textField = new JTextField();
		textField.setEditable(editable);
		textField.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		textField.setBackground(color);
		textField.setHorizontalAlignment(align);
		return textField;
	}
	
	/**
	 * Update the content of the <code>JComponent</code> in the panel.
	 * 
	 * @param event an <code>String</code> array to represent <code>LogEvent</code>
	 */
	public void setLogerEvent(String[] event)
	{
		tfType.setText(event[0]);
		tfDate.setText(event[1]);
		tfTime.setText(event[2]);
		// clear text first and then put new one
		taDescription.setText("");
		taDescription.setText(event[3]);
		tfHost.setText(event[4]);
		tfSource.setText(event[5]);
		tfUser.setText(event[6]);
	}

}
