package sg.edu.nus.db.synchronizer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import sg.edu.nus.gui.test.peer.ExportDBPanel;
import sg.edu.nus.peer.ServerPeer;

/**
 * given a set of tables, synchronize them with the existing ones.
 * 
 * @author Wu Sai
 * 
 */

public class Synchronizer {

	/**
	 * a connection to the database inside the ERP
	 */
	private Connection conn;

	/**
	 * a connection to the exported data
	 */
	private Connection conn2;

	public String dbName;

	public String exportDB;

	/**
	 * tables in the ERP to be synchronized
	 */
	private ArrayList<SynchronizedTable> synchronizedTable;

	/**
	 * information of the global schema
	 */
	private GlobalSchema gschema;

	/**
	 * record the progress of current processing
	 */
	public String progress;

	/**
	 * connect to the database in ERP system
	 * 
	 * @param driverString
	 * @param connectString
	 * @param user
	 * @param password
	 * @param dbName
	 */
	public Synchronizer(String dbName, String exportDB) {

		this.dbName = dbName;
		this.exportDB = exportDB;

		gschema = new GlobalSchema();

		synchronizedTable = new ArrayList<SynchronizedTable>();

		try {

			conn = ServerPeer.conn_localSchema;
			conn2 = ServerPeer.conn_exportDatabase;

		} catch (Exception e) {
			System.out.println("Fail to connect to the database");
			e.printStackTrace();
		}
	}

	/**
	 * add a new table for synchronization
	 * 
	 * @param table
	 *            table record for synchronization
	 * 
	 */
	public void insertTableForSynchronization(SynchronizedTable table) {
		synchronizedTable.add(table);
	}

	/**
	 * get the global schema information
	 * 
	 * @return
	 */
	public GlobalSchema getSchemaInfo() {
		return gschema;
	}

	/**
	 * export data out from local database in local schema to the exported
	 * database in global schema
	 */
	@SuppressWarnings("unchecked")
	public void exportData() {
		try {
			// first test if the export database exists, if not creates
			progress = "start to load the mapping relation";

			//ExportDBPanel.exportdb_statusBar.setText(progress);
			//ExportDBPanel.exportdb_statusBar.repaint();

			Statement exstmt = conn2.createStatement();
			String sql = "create database if not exists " + exportDB;

			exstmt.executeUpdate(sql);

			// get the metadata for verifying the correctness of the schema
			DatabaseMetaData dbmd = conn.getMetaData();
			String[] types = new String[1];
			types[0] = "TABLE";
			ResultSet tnames = dbmd.getTables(null, null, null, types);
			ArrayList<String> existingTables = new ArrayList<String>();
			while (tnames.next()) {
				String name = tnames.getString("TABLE_NAME");
				existingTables.add(name);
			}
			tnames.close();

			// remove the incorrect relations
			for (int i = 0; i < synchronizedTable.size(); i++) {
				SynchronizedTable next = synchronizedTable.get(i);
				if (!existingTables.contains(next.getTable())) {
					synchronizedTable.remove(i);
					i--;
				}
			}
			existingTables.clear();

			// map the local tables to the tables with global schema
			Hashtable<String, SynchronizedTable> destinatedTable = new Hashtable<String, SynchronizedTable>();
			for (int i = 0; i < synchronizedTable.size(); i++) {
				SynchronizedTable next = synchronizedTable.get(i);
				ArrayList<String> columns = next.getColumns();

				for (int j = 0; j < columns.size(); j++) {
					String cname = columns.get(j);
					String[] namespace = gschema.getMappingRelation(dbName,
							next.getTable(), cname);
					if (destinatedTable.containsKey(namespace[1])) {
						SynchronizedTable exist = destinatedTable
								.get(namespace[1]);
						exist.insert(namespace[2]);
					} else {
						SynchronizedTable exist = new SynchronizedTable(
								namespace[1]);
						exist.insert(namespace[2]);
						destinatedTable.put(namespace[1], exist);
					}
				}
			}

			// record the sql query to generate the required tables
			Hashtable<String, String> SQLStatement = new Hashtable<String, String>();

			Enumeration enu = destinatedTable.keys();

			while (enu.hasMoreElements()) {
				String key = (String) enu.nextElement();
				SynchronizedTable targetTable = destinatedTable.get(key);
				String tableSQL = "drop table if exists " + key + "_tmp";
				exstmt.executeUpdate(tableSQL);
				tableSQL = "create table " + key + "_tmp(";
				String selectSQL = "select ";
				ArrayList<String> allTables = new ArrayList<String>();

				// get all join relations
				ArrayList<String> columns = targetTable.getColumns();

				// sort it to get the unique order
				java.util.Collections.sort(columns);

				for (int i = 0; i < columns.size(); i++) {
					String cname = columns.get(i);
					String[] namespace = gschema.getReverseMappingRelation(
							exportDB, key, cname);
					selectSQL = selectSQL + namespace[1] + "." + namespace[2]
							+ " as " + cname + ",";
					String type = gschema.getType(namespace[0], namespace[1],
							namespace[2]);
					if (type == null)
						type = "text";
					tableSQL = tableSQL + cname + " " + type + ",";
					// add a new table
					if (!allTables.contains(namespace[1])) {
						allTables.add(namespace[1]);
					}
				}
				// remove the last period
				selectSQL = selectSQL.substring(0, selectSQL.length() - 1);
				tableSQL = tableSQL.substring(0, tableSQL.length() - 1);
				tableSQL = tableSQL + ")";

				// fill in the "from" clause
				selectSQL = selectSQL + " from ";
				for (int i = 0; i < allTables.size(); i++) {
					String tableName = allTables.get(i);
					selectSQL = selectSQL + tableName + ",";
				}
				selectSQL = selectSQL.substring(0, selectSQL.length() - 1);

				if (allTables.size() == 1) {
					SQLStatement.put(key, selectSQL);
					exstmt.executeUpdate(tableSQL);

					// log this sql for later use in periodical export
					logExportSQL(selectSQL, tableSQL);

					continue;
				}

				selectSQL = selectSQL + " where ";

				// fill in the "where" clause
				for (int i = 0; i < allTables.size() - 1; i++) {
					String tableName = allTables.get(i);
					for (int j = i + 1; j < allTables.size(); j++) {
						String joinableTable = allTables.get(j);
						String[] rel = gschema.getJoinableRelation(dbName,
								tableName, joinableTable);
						if (rel != null) {
							String[] ns1 = rel[0].split("\\.");
							String[] ns2 = rel[1].split("\\.");
							selectSQL = selectSQL + ns1[1] + "." + ns1[2] + "="
									+ ns2[1] + "." + ns2[2] + " and ";
						}
					}
				}
				// remove the last "and"
				selectSQL = selectSQL.substring(0, selectSQL.length() - 5);

				SQLStatement.put(key, selectSQL);

				progress = "create temporary table for table:" + key;
				//ExportDBPanel.exportdb_statusBar.setText(progress);
				//ExportDBPanel.exportdb_statusBar.repaint();

				exstmt.executeUpdate(tableSQL);

				// log this sql for later use in periodical export
				logExportSQL(selectSQL, tableSQL);

			}

			// start to build up the table
			progress = "map local data to the global schema and sort tables";
			//ExportDBPanel.exportdb_statusBar.setText(progress);
			//ExportDBPanel.exportdb_statusBar.repaint();
			enu = SQLStatement.keys();
			int updateRate = 100;
			int count = 0;
			Statement stmt = conn.createStatement();
			while (enu.hasMoreElements()) {

				String key = (String) enu.nextElement();
				SynchronizedTable targetTable = destinatedTable.get(key);
				progress = "sort the table:" + key;
				//ExportDBPanel.exportdb_statusBar.setText(progress);
				//ExportDBPanel.exportdb_statusBar.repaint();
				String nextSQL = SQLStatement.get(key);

				ResultSet rs = stmt.executeQuery(nextSQL);

				ArrayList<String> columns = targetTable.getColumns();
				java.util.Collections.sort(columns);

				while (rs.next()) {
					// insert the data into the temp table
					sql = "insert into " + key + "_tmp values(";
					for (int i = 0; i < columns.size(); i++) {
						String cname = columns.get(i);
						Object value = rs.getObject(cname);
						if (value instanceof String || value instanceof Date
								|| value instanceof Time) {
							String svalue = value.toString();
							// remove the quotation
							svalue = svalue.replace("'", "\\'");
							svalue = svalue.replace("\"", "\\\"");
							sql = sql + "'" + svalue + "'";
						} else
							sql = sql + value;

						if (i != columns.size() - 1)
							sql = sql + ",";
					}
					sql = sql + ")";

					// System.out.println(sql);

					exstmt.addBatch(sql);

					count++;

					// execute the insertion in batch mode
					if (count == updateRate) {
						exstmt.executeBatch();
						count = 0;
					}
				}
				rs.close();
			}
			exstmt.executeBatch();

			// get the metadata for verifying the correctness of the schema
			dbmd = conn2.getMetaData();
			types = new String[1];
			types[0] = "TABLE";
			tnames = dbmd.getTables(null, null, null, types);
			while (tnames.next()) {
				String name = tnames.getString("TABLE_NAME");
				existingTables.add(name);
			}
			tnames.close();

			enu = destinatedTable.keys();

			while (enu.hasMoreElements()) {
				String key = (String) enu.nextElement();
				if (!existingTables.contains(key)) {
					sql = "rename table " + key + "_tmp to " + key;
					exstmt.executeUpdate(sql);
				} else {
					// replace the old table with the new one
					sql = "drop table " + key;
					exstmt.executeUpdate(sql);

					sql = "rename table " + key + "_tmp to " + key;
					exstmt.executeUpdate(sql);
				}
			}

			sql = "commit";
			exstmt.executeUpdate(sql);
			exstmt.close();

			stmt.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		//ExportDBPanel.exportdb_statusBar.setText("");
		//ExportDBPanel.exportdb_statusBar.repaint();
	}

	private void logExportSQL(String selectSQL, String tableSQL) {
		System.out.println("DEBUG: export: selectSQL: " + selectSQL);
		System.out.println("DEBUG: export: tableSQL: " + tableSQL);
	}
}
