package sg.edu.nus.db.synchronizer;

import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Hashtable;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;

/**
 * create the inverted index for a
 * specific table, the inverted index
 * is stored in a table as well
 * @author wusai
 *
 */
@SuppressWarnings("unchecked")
public class InvertedIndexTable {

	public String tablename;

	public String idxname;

	private Hashtable map;

	public InvertedIndexTable(String tname) {
		tablename = tname;

		// if not specified, we will generate the
		// idx table's name automatically
		idxname = tname + "_idx";

		//
		map = new Hashtable(10000);
	}

	/**
	 * Build inverted index for the table
	 * @param column : the columns of being indexed, if set to null, all attributes are indexed
	 * @param incremental : if true, the old inverted index table is kept and new data are inserted,
	 *                      otherwise, the old table is dropped and new one is created 
	 */
	public void createIndexTable(ArrayList<String> column, boolean incremental) {
		try {
			DBProperty prop = new DBProperty();

			// test whether the table already exists
			DB dbConnection = prop.getBestpeerDB();
			DatabaseMetaData dbmd = dbConnection.getMetadata();
			String[] type = new String[1];
			type[0] = "TABLE";
			ResultSet table = dbmd.getTables(null, null, null, type);
			boolean flag = false;
			while (table.next()) {
				String tname = table.getString("TABLE_NAME");
				if (tname.equals(idxname)) {
					flag = true;
					break;
				}
			}
			table.close();

			String sql = "";
			if (!flag) {
				// create the idx table
				sql = "create table "
						+ idxname
						+ " (keyword VARCHAR(256), attribute VARCHAR(256), count INT)";
				dbConnection.processUpdate(sql);
			} else if (flag && !incremental) {
				sql = "delete from " + idxname;
				dbConnection.processUpdate(sql);
			}

			// get all the column name
			ArrayList<String> cnames = new ArrayList<String>();
			if (column != null) {
				cnames.addAll(column);
			} else {
				ResultSet columns = dbmd
						.getColumns(null, null, tablename, null);
				while (columns.next()) {
					String colName = columns.getString("COLUMN_NAME");
					cnames.add(colName);
				}
				columns.close();
			}

			// insert data
			if (!flag || (flag && !incremental)) {
				sql = "select * from " + tablename;
				ResultSet result = dbConnection.processQuery(sql);
				Statement updatestmt = dbConnection.getStatement();
				while (result.next()) {
					for (int i = 0; i < cnames.size(); i++) {
						String name = cnames.get(i);
						Object value = result.getObject(name);
						ArrayList<String> words = toString(value);

						// insert into these words
						for (int j = 0; j < words.size(); j++) {
							String keyword = words.get(j);
							// test whether the word has been inserted
							if (map.containsKey(keyword)) {
								sql = "update " + idxname
										+ " set count=count+1 where keyword='"
										+ keyword + "' and attribute='" + name
										+ "'";
								updatestmt.executeUpdate(sql);
							} else {
								map.put(keyword, 1);
								sql = "insert into " + idxname + " values('"
										+ keyword + "','" + name + "', 1)";
								updatestmt.executeUpdate(sql);
							}
						}
					}
				}
				result.close();
			}
		} catch (Exception e) {
			System.out.println("Fail to create the inverted index table!");
			e.printStackTrace();
		}
	}

	private ArrayList<String> toString(Object value) {

		ArrayList<String> result = new ArrayList<String>();
		if (value instanceof Integer) {
			String text = ((Integer) value).toString();
			result.add(text);
		} else if (value instanceof String) {
			String[] split = ((String) value).split(" ");
			for (int i = 0; i < split.length; i++)
				result.add(split[i]);
		} else if (value instanceof Boolean) {
			String text = ((Boolean) value).toString();
			result.add(text);
		} else if (value instanceof Byte) {
			String text = ((Byte) value).toString();
			result.add(text);
		} else if (value instanceof Date) {
			String text = ((Date) value).toString();
			result.add(text);
		} else if (value instanceof Double) {
			String text = ((Double) value).toString();
			result.add(text);
		} else if (value instanceof Float) {
			String text = ((Float) value).toString();
			result.add(text);
		} else if (value instanceof Long) {
			String text = ((Long) value).toString();
			result.add(text);
		} else if (value instanceof Short) {
			String text = ((Short) value).toString();
			result.add(text);
		} else if (value instanceof Time) {
			String text = ((Time) value).toString();
			result.add(text);
		}

		return result;
	}
}
