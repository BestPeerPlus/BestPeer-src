package sg.edu.nus.db.synchronizer;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;

/**
 * given a table, create a temp table
 * by sorting its tuples
 * @author Wu Sai
 *
 */
public class TableSorter {
	public String tablename;

	public String tmpname;

	public TableSorter(String tname) {
		tablename = tname;

		// if not specified, we will generate the
		// temp table's name automatically
		tmpname += tablename + "_tmp";

	}

	public void sort() {
		try {
			DBProperty prop = new DBProperty();

			// test whether the table already exists
			DB dbConnection = prop.getBestpeerDB();
			DatabaseMetaData dbmd = dbConnection.getMetadata();

			String[] type = new String[1];
			type[0] = "TABLE";
			ResultSet table = dbmd.getTables(null, null, null, type);
			while (table.next()) {
				String tname = table.getString("TABLE_NAME");
				if (tname.equals(tmpname))
					return;
			}
			table.close();

			// sort the table by values of all attributes and store
			// it in a new tmp table
			// DatabaseMetaData dbmd = dbConnection.getMetadata();
			ResultSet columns = dbmd.getColumns(null, null, tablename, null);
			String sortSQL = "create table " + tmpname + "( select * from "
					+ tablename + " order by ";
			while (columns.next()) {
				String colName = columns.getString("COLUMN_NAME");
				sortSQL += colName + ",";
			}
			sortSQL = sortSQL.substring(0, sortSQL.length() - 1) + ")";
			columns.close();
			dbConnection.processUpdate(sortSQL);
		} catch (Exception e) {
			System.err.println("Fail to sort the table.");
			e.printStackTrace();
		}
	}
}
