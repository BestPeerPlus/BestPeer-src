package sg.edu.nus.gui;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Load gui configuration from ini file.
 * @author VHTam
 */

public abstract class GuiLoader {

	// property for content panel
	public static Color contentBkColor = new Color(240, 240, 240);

	public static Color contentLineColor = new Color(128, 128, 128);

	//public static Color contentTextColor = Color.GRAY;
	public static Color contentTextColor = new Color(168, 167, 236);

	public static Color topStartColor = new Color(238, 238, 238);

	public static Color topEndColor = new Color(255, 255, 255);

	public static Color bottomStartColor = new Color(220, 220, 220);

	public static Color bottomEndColor = new Color(236, 236, 236);

	public static int contentInset = 30; // distance from content panel border to the first top component

	// TO DO load below and input property in config file

	public static int contentGridBagInset = 7;

	// propery fo button Apply/Revert
	public static int buttonInset = 25;

	public static int buttonDistance = 45;

	public static Color buttonStartColor = new Color(255, 255, 255);

	//public static Color buttonEndColor = new Color(82, 82, 82); //gray
	public static Color buttonEndColor = new Color(72, 154, 255); //blue

	public static Color buttonRollOverColor = new Color(255, 91, 13);

	public static Color buttonPressedColor = new Color(206, 67, 0);

	public static Color buttonTextColor = new Color(255, 255, 255);

	// property for simple gradient panel
	//public static Color panelStartColor = new Color(255,255,204);
	public static Color panelStartColor = new Color(255, 255, 180);

	public static Color panelEndColor = new Color(255, 255, 235);

	//	 property for simple gradient label
	//public static Color labelStartColor = new Color(210, 210, 210);
	public static Color labelStartColor = new Color(255, 255, 195);

	public static Color labelEndColor = new Color(255, 255, 255);

	// property for component border
	//public static Color componentBorderColor = new Color(128, 128, 128);
	public static Color componentBorderColor = contentLineColor.brighter();

	// property for theme
	public static Color themeBkColor = new Color(255, 255, 235);

	public static Color selectionBkColor = new Color(232, 242, 254);

	public static Color titleColor = new Color(70, 124, 235);

	public static int widthColumn = 200;

	// end to do

	private static final String guiConfigFile = "./sg/edu/nus/accesscontrol/gui/access_control_gui_config.ini";

	private static Properties keys = null;

	public static void load() {
		if (keys != null) {
			return; // only actually load once
		}

		try {
			/* load gui config */
			FileInputStream fin = null;
			fin = new FileInputStream(guiConfigFile);

			/* load items into memory */
			if (fin != null) {
				keys = new Properties();
				keys.load(fin);

				/// re-config color
				contentBkColor = getColor(getProperty("content_bk_color"));
				contentLineColor = getColor(getProperty("content_line_color"));
				contentTextColor = getColor(getProperty("content_text_color"));
				topStartColor = getColor(getProperty("top_start_color"));
				bottomStartColor = getColor(getProperty("bottom_start_color"));
				topEndColor = getColor(getProperty("top_end_color"));
				bottomEndColor = getColor(getProperty("bottom_end_color"));

				contentInset = Integer.parseInt(getProperty("content_inset"));
				widthColumn = Integer.parseInt(getProperty("width_column"));

				//
				fin.close();
			}
		} catch (IOException e) {
			throw new RuntimeException("Cannot find gui file", e);
		}

	}

	private static Color getColor(String RGB) {
		String[] arrStr = RGB.split(",");
		return new Color(Integer.parseInt(arrStr[0]), Integer
				.parseInt(arrStr[1]), Integer.parseInt(arrStr[2]));
	}

	public static String getProperty(String key) {
		if (keys == null)
			throw new RuntimeException(
					"Access control Gui loader is not initiated");

		String tmp = keys.getProperty(key);

		if (tmp == null) {
			tmp = "No property for this";
		}

		try {
			return new String(tmp.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			return tmp;
		}
	}

	public static Properties getProperties() {
		if (keys == null)
			throw new RuntimeException(
					"Access control Gui loader is not initiated");

		return keys;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Access control Gui config loader!");

		GuiLoader.load();

		System.out.println("Loaded Access control Gui config!");
		String key = "image_table_node";
		String result = getProperty(key);
		System.out.println(key + ": " + result);

		//Properties prop = AccCtrlGuiLoader.getProperties();
		//prop.list(System.out);

		System.out.println("End access control gui config loader!");
	}
}
