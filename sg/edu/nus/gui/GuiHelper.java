package sg.edu.nus.gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.MediaTracker;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


/**
 * Help create custom component with property in GuiLoader class
 * @author VHTam
 * 
 */
public class GuiHelper {
	
	public static JLabel createLnFLabel(String caption){
		JLabel label = new JLabel(caption);
		
		label.setBackground(GuiLoader.contentBkColor);
		
		label.setForeground(GuiLoader.contentTextColor);
		
		return label;
	}
	
	public static JPanel createLnFPanel(){
		JPanel component = new JPanel();
		component.setBackground(GuiLoader.contentBkColor);
		
		//component.setForeground(AccCtrlGuiLoader.contentTextColor);

		return component;
	}
	
	public static Border createLnFBorder(){
		Border component = BorderFactory.createLineBorder(GuiLoader.componentBorderColor);
		return component;
	}
	
	public static JTextField createLnFTextField(){
		JTextField component = new JTextField();
		component.setBorder(createLnFBorder());
		component.setBackground(GuiLoader.themeBkColor);
		
		component.setForeground(GuiLoader.contentTextColor);
		
		return component;
	}

	public static JComboBox createLnFComboBox(){
		JComboBox component = new JComboBox();
		component.setBorder(createLnFBorder());
		component.setBackground(GuiLoader.themeBkColor);
		
		component.setForeground(GuiLoader.contentTextColor);

		return component;
	}

	public static JPasswordField createLnFPasswordField(){
		JPasswordField component = new JPasswordField();
		component.setBorder(createLnFBorder());
		component.setBackground(GuiLoader.themeBkColor);
		
		component.setForeground(GuiLoader.contentTextColor);
		
		return component;
	}
	
	public static JScrollPane createLnFScrollPane(JComponent comp){
		JScrollPane component = new JScrollPane(comp);
		component.setBorder(createLnFBorder());
		return component;
	}
	
	public static JTextArea createLnFTextArea(){
		JTextArea component = new JTextArea();
		component.setBackground(GuiLoader.themeBkColor);
		//component.setBorder(createLnFBorder()); // do not need this if use with scroll pane
		
		component.setForeground(GuiLoader.contentTextColor);
		
		return component;
	}

	public static JList createLnFList(){
		JList component = new JList();
		component.setBackground(GuiLoader.themeBkColor);
		//component.setBorder(createLnFBorder()); // do not need this if use with scroll pane
		
		component.setForeground(GuiLoader.contentTextColor);
		component.setSelectionBackground(GuiLoader.selectionBkColor);
		component.setSelectionForeground(GuiLoader.contentTextColor);
		
		return component;
	}
	
	public static JRadioButton createLnFRadioButton(String caption){
		JRadioButton component = new JRadioButton(caption);
		
		component.setBackground(GuiLoader.contentBkColor);
		
		component.setForeground(GuiLoader.contentTextColor);
		
		return component;
	}

	public static JButton createLnFButton(String caption){
		JButton component = new JButton(caption);
		
		component.setBackground(GuiLoader.contentBkColor);
		
		component.setForeground(GuiLoader.contentTextColor);
				
		return component;
	}
	
//	public static GradientLabel createGradientLabel(String text){
//		GradientLabel component = new GradientLabel(text);
//		// setting some thing
//		component.setForeground(GuiLoader.contentTextColor);
//		//component.setForeground(new Color(232,242,254).darker().darker());
//		
//		
//		return component;
//	}
	
	////////////////
	public static ImageIcon getIcon(String iconConfigId){
		String path = GuiLoader.getProperty("image_path")+
			GuiLoader.getProperty(iconConfigId);
		
		ImageIcon icon = new ImageIcon(path);
		if (icon.getImageLoadStatus()==MediaTracker.ERRORED){
			System.err.println("Image path ERROR: " + path);
		}
		
		return new ImageIcon(path);
	}

	////////////////
	
    public static JPanel createBlankThemePanel(int inset){
    	JPanel pane = new JPanel();
    	pane.setBorder(new EmptyBorder(new Insets(inset, inset, inset, inset)));
    	
    	pane.setBackground(GuiLoader.themeBkColor);
    	
    	return pane;
    }

    public static JPanel createBlankColorPanel(int inset, Color bkColor){
    	JPanel pane = new JPanel();
    	pane.setBorder(new EmptyBorder(new Insets(inset, inset, inset, inset)));
    	
    	pane.setBackground(bkColor);
    	
    	return pane;
    }

    public static JPanel createBlankColorPanel(int height, int width, Color bkColor){
    	JPanel pane = new JPanel();
    	pane.setBorder(new EmptyBorder(new Insets(height, width, height, width)));
    	
    	pane.setBackground(bkColor);
    	
    	return pane;
    }
    
    public static JPanel createThemePanel(){
    	JPanel pane = new JPanel();
    	
    	pane.setBackground(GuiLoader.themeBkColor);
    	
    	return pane;
    }

    public static JPanel createBlankThemePanel(int top, int left, int bottom, int right){
    	JPanel pane = new JPanel();
    	pane.setBorder(new EmptyBorder(new Insets(top, left, bottom, right)));
    	
    	pane.setBackground(GuiLoader.themeBkColor);
    	
    	return pane;
    }
    
}
