package sg.edu.nus.db.synchronizer;

import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JOptionPane;

import sg.edu.nus.bestpeer.indexdata.RangeIndex;
import sg.edu.nus.dbconnection.DBIndex;
import sg.edu.nus.peer.ServerPeer;
import sg.edu.nus.peer.info.TreeNode;
import sg.edu.nus.util.DB_TYPE;
import sg.edu.nus.util.MetaDataAccess;

/**
 * Base functions for exporting data and indexing exported data
 * 
 * @author dcsvht
 * 
 */
public class DataExport {

	public static void exportData(Vector<String> tables,
			Hashtable<String, Vector<String>> columnsOfTable) {
		try {
			Synchronizer synchronizer = new Synchronizer(ServerPeer.erp_db
					.getDbName(), ServerPeer.EXPORTED_DB);

			GlobalSchema gschema = synchronizer.getSchemaInfo();

			if (gschema == null) {
				JOptionPane.showMessageDialog(null, "GSchema is null");
				return;
			}

			Connection conn = ServerPeer.conn_localSchema;

			int tableCount = tables.size();

			DatabaseMetaData dbmd = conn.getMetaData();

			for (int i = 0; i < tableCount; i++) {
				String tableName = tables.get(i);
				SynchronizedTable synchronizedTable = new SynchronizedTable(
						tableName);

				Hashtable<String, String> columnTypeInfo = new Hashtable<String, String>();

				ResultSet columns = dbmd
						.getColumns(null, null, tableName, null);
				while (columns.next()) {
					String colName = columns.getString("COLUMN_NAME");
					int typeName = columns.getInt("DATA_TYPE");
					String typeString = null;
					if (typeName == java.sql.Types.BIGINT)
						typeString = "int";
					else if (typeName == java.sql.Types.BINARY)
						typeString = "binary";
					else if (typeName == java.sql.Types.BIT)
						typeString = "bit";
					else if (typeName == java.sql.Types.BOOLEAN)
						typeString = "boolean";
					else if (typeName == java.sql.Types.CHAR)
						typeString = "char";
					//
					else if (typeName == java.sql.Types.DATE) {
						typeString = "datetime";// for mysql
					} else if (typeName == java.sql.Types.TIME) {
						typeString = "time";
					} else if (typeName == java.sql.Types.DECIMAL)
						typeString = "decimal";
					else if (typeName == java.sql.Types.DOUBLE) {
						if (ServerPeer.bestpeer_db.getDbType().equals(
								DB_TYPE.SQL_SERVER))
							typeString = "real";
						else
							typeString = "double";
					} else if (typeName == java.sql.Types.FLOAT) {
						typeString = "float";
					} else if (typeName == java.sql.Types.INTEGER) {
						typeString = "int";
					} else if (typeName == java.sql.Types.NUMERIC) {
						typeString = "int";
					} else if (typeName == java.sql.Types.REAL) {
						if (ServerPeer.bestpeer_db.getDbType().equals(
								DB_TYPE.SQL_SERVER))
							typeString = "real";
						else
							typeString = "double";
					} else if (typeName == java.sql.Types.SMALLINT) {
						typeString = "int";
					} else
						typeString = "text";
					columnTypeInfo.put(colName, typeString);

				}
				columns.close();

				Vector<String> tableColumns = columnsOfTable.get(tableName);
				int columnCount = tableColumns.size();

				for (int j = 0; j < columnCount; j++) {
					String colName = tableColumns.get(j);
					synchronizedTable.insert(colName);
					String typeInfo = columnTypeInfo.get(colName);
					String namespace = ServerPeer.erp_db.getDbName() + "."
							+ tableName + "." + colName;
					gschema.insertTypeInfo(namespace, typeInfo);
				}

				synchronizer.insertTableForSynchronization(synchronizedTable);

			}

			// export data...
			synchronizer.exportData();

			// generate histogram for the exported tables
			// GenerateHistogram genHist = new GenerateHistogram();
			// genHist.generateHistogram();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void rePublishIndex(ServerPeer serverpeer) {

		Vector<String> listOfTables = new Vector<String>();
		Hashtable<String, Vector<RangeIndex>> rangeIndexInTables = new Hashtable<String, Vector<RangeIndex>>();

		// get the indexed column
		try {
			String sql = "select * from local_index";
			Connection conn3 = ServerPeer.conn_bestpeerindexdb;
			Statement idxstmt;

			idxstmt = conn3.createStatement();
			ResultSet rs = idxstmt.executeQuery(sql);

			Hashtable<String, String> tables = new Hashtable<String, String>();

			Vector<RangeIndex> rangeIndexOfTable = new Vector<RangeIndex>();

			while (rs.next()) {
				String column = rs.getString("ind");
				String table = rs.getString("val");

				// add table
				tables.put(table, table);

				// add range index of column
				Connection conn = ServerPeer.conn_exportDatabase;
				Statement stmt = conn.createStatement();
				sql = "select max(" + column + ") from " + table;
				ResultSet rset = stmt.executeQuery(sql);
				rset.next();
				String maxVal = rset.getString(1);
				rset.close();
				sql = "select min(" + column + ") from " + table;
				rset = stmt.executeQuery(sql);
				rset.next();
				String minVal = rset.getString(1);
				rset.close();

				String columnType = MetaDataAccess.metaGetColumnType(
						ServerPeer.conn_metabestpeerdb, table, column);
				int type = columnType.contains("varchar") ? RangeIndex.STRING_TYPE
						: RangeIndex.NUMERIC_TYPE;
				RangeIndex rangeIndex = new RangeIndex(table, column, type,
						minVal, maxVal, serverpeer.getPhysicalInfo());

				rangeIndexOfTable.add(rangeIndex);

				rangeIndexInTables.put(table, rangeIndexOfTable);
			}

			rs.close();
			idxstmt.close();

			Enumeration<String> enumTables = tables.elements();
			while (enumTables.hasMoreElements()) {
				listOfTables.add(enumTables.nextElement());
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// update index
		TreeNode[] treeNode = serverpeer.getTreeNodes();
		if (treeNode != null) {

			DBIndex dbIndex;

			try {
				dbIndex = new DBIndex(serverpeer, serverpeer.getPhysicalInfo(),
						treeNode[0].getLogicalInfo(), listOfTables,
						rangeIndexInTables, null);
				dbIndex.indexDatabase();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}

		}
	}

	ServerPeer serverpeer = null;

	Timer timer = null;

	public DataExport(ServerPeer serverpeer) {
		this.serverpeer = serverpeer;
	}

	public void setSchedule(int hour, int minute, int second) {

		if (timer != null) {
			timer.cancel();
		}

		timer = new Timer();
		TimerTask task = new ExportTask(serverpeer, hour, minute, second);
		timer.scheduleAtFixedRate(task, 0, 1000);
	}

	class ExportTask extends TimerTask {

		int scheduledHour = 23;
		int scheduleMinute = 0;
		int scheduleSecond = 0;
		ServerPeer serverpeer = null;

		public ExportTask(ServerPeer serverpeer, int hour, int minute,
				int second) {
			this.serverpeer = serverpeer;
			scheduledHour = hour;
			scheduleMinute = minute;
			scheduleSecond = second;
		}

		public void run() {
			Calendar t = Calendar.getInstance();
			int hour = t.get(Calendar.HOUR_OF_DAY);
			int minute = t.get(Calendar.MINUTE);
			int second = t.get(Calendar.SECOND);

			// System.out.println("Checking scheduled export...");

			if (hour == scheduledHour && minute == scheduleMinute
					&& second == scheduleSecond) {

				System.out.println("PERIODICALLY EXPORTING DATA...");

				if (serverpeer != null) {

					Vector<String> tables = MetaDataAccess
							.metaGetLocalTableExported(ServerPeer.conn_metabestpeerdb);
					Hashtable<String, Vector<String>> columnsOfTable = MetaDataAccess
							.metaGetLocalColumnsExported(ServerPeer.conn_metabestpeerdb);

					exportData(tables, columnsOfTable);

					rePublishIndex(serverpeer);
					// System.out.println("exporting...");
				}

				System.out.println("FINISHING EXPORTING DATA...");
			}

			// usage of calendar
			// t.get(Calendar.YEAR) int value of the year
			// t.get(Calendar.MONTH) int value of the month (0-11)
			// t.get(Calendar.DAY_OF_MONTH) int value of the day of the month
			// (1-31)
			// t.get(Calendar.DAY_OF_WEEK) int value of the day of the week
			// (0-6)
			// t.get(Calendar.HOUR) int value of the hour in 12 hour notation
			// (0-12)
			// t.get(Calendar.AM_PM) returns either Calendar.AM or Calendar.PM
			// t.get(Calendar.HOUR_OF_DAY) int value of the hour of the day in
			// 24-hour notation (0-24)
			// t.get(Calendar.MINUTE) int value of the minute in the hour (0-59)
			// t.get(Calendar.SECOND) int value of the second within the minute
			// (0-59).
			// t.get(Calendar.MILLISECOND) int value of the milliseconds within
			// a second (0-999).

		}

	}

	public static void main(String[] argv) throws Exception {
		DataExport dataExport = new DataExport(null);
		dataExport.setSchedule(10, 57, 0);
	}
}
