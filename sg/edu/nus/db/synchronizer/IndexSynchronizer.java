package sg.edu.nus.db.synchronizer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import sg.edu.nus.peer.ServerPeer;

/**
 * synchronize the index of the local peer and the remote ones in the P2P
 * networks
 * 
 * @author Wu Sai
 * 
 */

public class IndexSynchronizer {

	/**
	 * record the namespace of the columns that have been built index
	 */
	// private ArrayList<String> indexedColumn;
	/**
	 * existing table
	 */
	// private String existingTable;
	/**
	 * new exported table
	 */
	// private String newTable;
	/**
	 * namespace of the table that stores the index vectors
	 */
	// private String vectorTable;
	/**
	 * name of the table that stores the vectors for updating, the table is
	 * stored in the same database as vectorTable
	 */
	// private String updateTable;
	/**
	 * for database connection
	 */
	// private String driver;
	//
	// private String connectString;
	//
	// private String user;
	//
	// private String password;
	/**
	 * temp table for storing the new vector table
	 */
	// private String tmp_Table;
	/**
	 * parameter for batch processing, after more than "updateThreshold" sql
	 * queries are generated, they will be executed together
	 */
	public static int updateThreshold = 1000;

	/**
	 * constructor
	 * 
	 * @param existingTable
	 *            the old exported table in the database
	 * @param newTable
	 *            the new exported one
	 * @param vectorTable
	 *            the table holds the vector that represents the keyword index
	 * @param updateTable
	 *            the table holds the vectors that should be sent for updating
	 * @param index
	 *            the array that stores the names of columns being indexed
	 */
	public IndexSynchronizer(
	// String driver, String connectString, String user,
	// String password
	) {
		// String existingTable, String newTable,
		// String vectorTable){
		// String updateTable,
		// ArrayList<String> index){
		// this.driver = driver;
		// this.connectString = connectString;
		// this.user = user;
		// this.password = password;
		// this.existingTable = existingTable;
		// this.newTable = newTable;
		// this.vectorTable = vectorTable;
		// this.tmp_Table = vectorTable + "_tmp";
		// this.updateTable = updateTable;
		// this.indexedColumn = index;
	}

	/**
	 * generate the index vector
	 * 
	 * @param db
	 *            database name
	 * @param table
	 *            table name
	 * @param column
	 *            column name
	 * @param indexTable
	 *            table name that stores the index
	 */
	public void buildIndex(String db, String table, ArrayList<String> column,
			String indexTable) {

		/** scan the table for creating the index **/

		try {
			Connection conn1 = ServerPeer.conn_exportDatabase;
			// create the table if not exist
			String sql = "";
			Statement vectorStmt = conn1.createStatement();
			Statement updateStmt = conn1.createStatement();
			// vectorStmt.executeUpdate(sql);
			sql = "create table if not exists " + indexTable
					+ " ( keyword varchar(250), vector varchar("
					+ GlobalSchema.VECTOR_SIZE + "), updateFlag boolean)";
			vectorStmt.executeUpdate(sql);

			// reset the update flag
			// sql = "update table " + indexTable + " set updateFlag=false";
			// vectorStmt.executeUpdate(sql);

			// retrieving the new keyword
			Statement stmt = conn1.createStatement();
			sql = "select ";
			for (int i = 0; i < column.size(); i++) {
				String cname = column.get(i);
				sql = sql + cname + ",";
			}
			sql = sql.substring(0, sql.length() - 1);
			sql = sql + " from " + table;

			ResultSet rs = stmt.executeQuery(sql);

			Hashtable<String, ArrayList<Integer>> cache = new Hashtable<String, ArrayList<Integer>>();

			// compute the position of the columns
			Hashtable<String, Integer> positions = new Hashtable<String, Integer>();
			for (int i = 0; i < column.size(); i++) {
				String cname = column.get(i);
				int idx = GlobalSchema.getVectorPosition(db, table, cname);
				positions.put(cname, idx);
			}

			while (rs.next()) {

				for (int i = 0; i < column.size(); i++) {
					String cname = column.get(i);
					Object value = rs.getObject(cname);
					ArrayList<String> keywords = toString(value);
					for (int j = 0; j < keywords.size(); j++) {
						String key = keywords.get(j);
						Integer idx = positions.get(cname);
						if (cache.containsKey(key)) {
							ArrayList<Integer> list = cache.get(key);

							if (!list.contains(idx)) {
								list.add(idx);
							}
						} else {
							ArrayList<Integer> list = new ArrayList<Integer>();
							list.add(idx);
							cache.put(key, list);
						}
					}
				}

				// start to update the database

				if (cache.size() > updateThreshold) {
					Enumeration<String> enu = cache.keys();
					// Hashtable<String, String> newValues = new
					// Hashtable<String, String>();
					while (enu.hasMoreElements()) {
						String key = enu.nextElement();
						ArrayList<Integer> bits = cache.get(key);
						sql = "select vector from " + indexTable
								+ " where keyword='" + key + "'";
						ResultSet vrs = vectorStmt.executeQuery(sql);

						boolean flag = false;
						if (vrs.next()) {
							// flag = true;
							String V = vrs.getString("vector");
							String Vnew = "";
							// update the vector if necessary
							for (int i = 0; i < bits.size(); i++) {
								Integer I = bits.get(i);
								if (Vnew.length() > 0) {
									String[] rev = updateVector(Vnew, I
											.intValue());
									if (!flag && rev[1].equals("1")) {
										flag = true;
									}
									Vnew = rev[0];
								} else {
									String[] rev = updateVector(V, I.intValue());
									if (!flag && rev[1].equals("1")) {
										flag = true;
									}
									Vnew = rev[0];
								}
							}

							// update the database
							if (flag) {
								sql = "update " + indexTable + " set vector='"
										+ Vnew + "' and updateFlag=true where "
										+ "keyword='" + key + "'";
								updateStmt.addBatch(sql);
							}
						} else {
							// this is a new keyword set its vector
							char[] data = new char[GlobalSchema.VECTOR_SIZE];
							java.util.Arrays.fill(data, '0');
							String vector = new String(data);
							// set the specific bit to 1
							for (int i = 0; i < bits.size(); i++) {
								Integer I = bits.get(i);
								String[] newvector = updateVector(vector, I
										.intValue());
								vector = newvector[0];
							}
							sql = "insert into "
									+ indexTable
									+ " (keyword, vector, updateFlag) values ('"
									+ key + "','" + vector + "', true)";
							updateStmt.addBatch(sql);
						}
					}

					// process the updates in batch
					updateStmt.executeBatch();
					cache.clear();
				}
			}

			// send the rest update to the database
			Enumeration<String> enu = cache.keys();
			// Hashtable<String, String> newValues = new Hashtable<String,
			// String>();
			while (enu.hasMoreElements()) {
				String key = enu.nextElement();
				ArrayList<Integer> bits = cache.get(key);
				sql = "select vector from " + indexTable + " where keyword='"
						+ key + "'";
				ResultSet vrs = vectorStmt.executeQuery(sql);

				boolean flag = false;
				if (vrs.next()) {
					// flag = true;
					String V = vrs.getString("vector");
					String Vnew = "";
					// update the vector if necessary
					for (int i = 0; i < bits.size(); i++) {
						Integer I = bits.get(i);
						if (Vnew.length() > 0) {
							String[] rev = updateVector(Vnew, I.intValue());
							if (!flag && rev[1].equals("1")) {
								flag = true;
							}
							Vnew = rev[0];
						} else {
							String[] rev = updateVector(V, I.intValue());
							if (!flag && rev[1].equals("1")) {
								flag = true;
							}
							Vnew = rev[0];
						}
					}

					// update the database
					if (flag) {
						sql = "update " + indexTable + " set vector='" + Vnew
								+ "' and updateFlag=true where " + "keyword='"
								+ key + "'";
						updateStmt.addBatch(sql);
					}
				} else {
					// this is a new keyword set its vector
					char[] data = new char[GlobalSchema.VECTOR_SIZE];
					java.util.Arrays.fill(data, '0');
					String vector = new String(data);

					// set the specific bit to 1
					for (int i = 0; i < bits.size(); i++) {
						Integer I = bits.get(i);
						String[] newvector = updateVector(vector, I.intValue());
						vector = newvector[0];
					}

					sql = "insert into " + indexTable
							+ " (keyword, vector, updateFlag) values ('" + key
							+ "','" + vector + "', true)";
					updateStmt.addBatch(sql);
				}
			}
			updateStmt.executeBatch();
			rs.close();

			// close the connection
			updateStmt.close();
			vectorStmt.close();
			stmt.close();
			// conn1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * compare the new index with the old one and update the index in P2P
	 * network
	 * 
	 * @param dbName
	 *            database name
	 * @param existingIndex
	 *            the table name of existing vector index
	 * @param newIndex
	 *            the table name of new vector index
	 * @param updateIndex
	 *            the table that holds the updatable index
	 */
	public void synchronize(String dbName, String existingIndex,
			String newIndex, String updateIndex) {
		try {
			Connection conn = ServerPeer.conn_exportDatabase;
			Statement existingStmt = conn.createStatement();
			Statement newStmt = conn.createStatement();

			// create the update table if not exist
			String sql = "create table if not exists "
					+ GlobalSchema.VECTOR_UPDATE_TABLE
					+ "(keyword varchar(250) COMMENT 'keyword',"
					+ "vector varchar("
					+ GlobalSchema.VECTOR_SIZE
					+ ") COMMENT 'index vector',"
					+ "operation varchar(10) COMMENT 'type of update: insert, delete or update')";
			existingStmt.executeUpdate(sql);

			sql = "create table if not exists vector(keyword varchar(250), vector varchar("
					+ GlobalSchema.VECTOR_SIZE + "))";
			existingStmt.executeUpdate(sql);

			// get the modified one
			sql = "select " + existingIndex + ".vector as old, " + newIndex
					+ ".vector as new, " + existingIndex + ".keyword from "
					+ existingIndex + "," + newIndex + " where "
					+ existingIndex + ".keyword=" + newIndex + ".keyword";
			ResultSet joinRS = existingStmt.executeQuery(sql);
			int count = 0;
			while (joinRS.next()) {
				String oldV = joinRS.getString("old");
				String newV = joinRS.getString("new");
				String key = joinRS.getString("keyword");
				if (oldV.compareTo(newV) == 0) {
					// it has not been modified, do nothing

				} else {
					// it is different, should be updated
					sql = "insert into " + GlobalSchema.VECTOR_UPDATE_TABLE
							+ " values ('" + key + "', '" + newV
							+ "', 'update')";
					newStmt.addBatch(sql);
					count++;

					if (count > 100) {
						newStmt.executeBatch();
						count = 0;
					}
				}
			}
			joinRS.close();

			// get the newly inserted ones
			sql = "select vector, keyword from " + newIndex
					+ " where keyword not in (select keyword from "
					+ existingIndex + ")";

			ResultSet insertRS = existingStmt.executeQuery(sql);
			count = 0;
			while (insertRS.next()) {
				String keyword = insertRS.getString("keyword");
				String vector = insertRS.getString("vector");

				sql = "insert into " + GlobalSchema.VECTOR_UPDATE_TABLE
						+ " values ('" + keyword + "','" + vector
						+ "','insert')";
				newStmt.addBatch(sql);
				count++;
				if (count > 100) {
					newStmt.executeBatch();
					count = 0;
				}
			}
			newStmt.executeBatch();
			insertRS.close();

			// get the deleted ones
			sql = "select vector, keyword from " + existingIndex
					+ " where keyword not in (select keyword from " + newIndex
					+ ")";

			ResultSet deleteRS = existingStmt.executeQuery(sql);
			count = 0;
			while (deleteRS.next()) {
				String keyword = deleteRS.getString("keyword");
				String vector = deleteRS.getString("vector");

				sql = "insert into " + GlobalSchema.VECTOR_UPDATE_TABLE
						+ " values ('" + keyword + "','" + vector
						+ "','delete')";
				newStmt.addBatch(sql);
				count++;
				if (count > 100) {
					newStmt.executeBatch();
					count = 0;
				}
			}
			newStmt.executeBatch();
			deleteRS.close();

			// done, close the connection
			existingStmt.close();
			newStmt.close();
			// conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * update the value of a specific position from "0" to "1"
	 * 
	 * @param param
	 * @param idx
	 * @return a string array, the first element is the modified string, the
	 *         second element represents the status if the value has been
	 *         updated return "1" value, else return "0"
	 */
	private String[] updateVector(String param, int idx) {

		String[] rev = new String[2];

		if (param.length() <= idx)
			return null;

		char c = param.charAt(idx);
		if (c == '0') {
			rev[0] = param.substring(0, idx) + "1"
					+ param.substring(idx + 1, param.length());
			rev[1] = "1";
			return rev;
		} else {
			rev[0] = param;
			rev[1] = "0";
			return rev;
		}
	}

	/**
	 * given an object that represents an attribute value, return the
	 * correponding keywords
	 * 
	 * @param value
	 * @return
	 */
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
