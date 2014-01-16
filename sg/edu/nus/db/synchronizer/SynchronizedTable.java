package sg.edu.nus.db.synchronizer;

import java.util.ArrayList;

/**
 * the class represents a table that
 * needs to be synchronized
 * @author Wu Sai
 *
 */
public class SynchronizedTable {

	/**
	 * table name
	 */
	private String tableName;

	/**
	 * attribute that needs to be exported 
	 */
	private ArrayList<String> attribute;

	/**
	 * constructor
	 * @param tname table name
	 */
	public SynchronizedTable(String tname) {
		tableName = tname;
		attribute = new ArrayList<String>();
	}

	/**
	 * 
	 * @return the table name
	 */
	public String getTable() {
		return tableName;
	}

	/**
	 * 
	 * @return the columns for exporting
	 */
	public ArrayList<String> getColumns() {
		return attribute;
	}

	/**
	 * insert new column for exporting
	 * @param columnName column name
	 */
	public void insert(String columnName) {
		this.attribute.add(columnName);
	}
}
