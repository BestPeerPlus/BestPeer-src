package sg.edu.nus.db.synchronizer;

import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import sg.edu.nus.dbconnection.DB;
import sg.edu.nus.dbconnection.DBProperty;

/**
 * Given two sorted tables and the inverted index table
 * this class generate two tables, one for new inserted
 * keywords and the other one for the deletion 
 * @author Wu Sai
 *
 */

public class TableComparator {

	public String originalTable;

	public String newExportedTable;

	public String insertedIndex;

	public String deletedIndex;

	private String indexTable;

	private int updateThreshold = 100;

	/**
	 * storing the update operations in a hashtable
	 * to speed up the processing
	 */
	private Hashtable<String, ArrayList<UpdateRecord>> updateHistory;

	// an inner class for storing
	// the update operations
	class UpdateRecord {
		public String attributeName;
		public String keyword;
		public boolean isNew;
		public int count;
	}

	public TableComparator(String oldTable, String newTable) {
		originalTable = oldTable;
		newExportedTable = newTable;

		// automatically generate the inserted and deleted tables' names
		insertedIndex = oldTable + "_inserted";
		deletedIndex = oldTable + "_deleted";

		indexTable = oldTable + "_idx";

		updateHistory = new Hashtable<String, ArrayList<UpdateRecord>>();
	}

	/**
	 * compare the two table and produce the results
	 *
	 */
	public void compare() {
		try {
			DBProperty prop = new DBProperty();

			// test whether the table already exists
			DB dbConnection = prop.getBestpeerDB();
			DatabaseMetaData dbmd = dbConnection.getMetadata();

			String[] type = new String[1];
			type[0] = "TABLE";
			ResultSet table = dbmd.getTables(null, null, null, type);
			int count = 0;
			boolean f1 = false;
			boolean f2 = false;
			String sql = "";
			while (table.next()) {
				String tname = table.getString("TABLE_NAME");
				if (tname.equals(originalTable)
						|| tname.equals(newExportedTable)
						|| tname.equals(originalTable + "_idx")) {
					count++;
				} else if (tname.equals(insertedIndex)) {
					sql = "delete from " + insertedIndex;
					dbConnection.processUpdate(sql);
					f1 = true;
				} else if (tname.equals(deletedIndex)) {
					sql = "delete from " + deletedIndex;
					dbConnection.processUpdate(sql);
					f2 = true;
				}
			}
			table.close();

			if (count != 3) {
				System.err
						.println("Cannot compare the tables, as table names are incorrect!");
				return;
			}

			if (!f1) {
				sql = "create table " + insertedIndex
						+ "(keyword VARCHAR(256), attribute VARCHAR(256))";
				dbConnection.processUpdate(sql);
			}

			if (f2) {
				sql = "drop table " + deletedIndex;
				dbConnection.processUpdate(sql);
			}

			// get the metadata of the table
			ResultSet columns = dbmd
					.getColumns(null, null, originalTable, null);

			ArrayList<String> commonCol = new ArrayList<String>();
			ArrayList<String> col1 = new ArrayList<String>();
			while (columns.next()) {
				String colName = columns.getString("COLUMN_NAME");
				col1.add(colName);
			}
			columns.close();

			columns = dbmd.getColumns(null, null, newExportedTable, null);
			ArrayList<String> col2 = new ArrayList<String>();
			while (columns.next()) {
				String colName = columns.getString("COLUMN_NAME");
				col2.add(colName);
			}
			columns.close();

			// find the common columns
			commonCol.addAll(col1);
			for (int i = 0; i < commonCol.size(); i++) {
				String name = commonCol.get(i);
				if (!col2.contains(name)) {
					commonCol.remove(i);
					i--;
				}
			}

			// find the indexed columns
			sql = "select distinct attribute from " + indexTable;
			ResultSet indexed = dbConnection.processQuery(sql);
			ArrayList<String> indexedColumn = new ArrayList<String>();
			while (indexed.next()) {
				String cname = indexed.getString("attribute");
				indexedColumn.add(cname);
			}
			indexed.close();

			// start the comparison
			Statement selstmt = dbConnection.getStatement();
			Statement updatestmt = dbConnection.getStatement();
			sql = "select * from " + originalTable;

			ResultSet originalData = dbConnection.processQuery(sql);

			sql = "select * from " + newExportedTable;
			ResultSet newData = selstmt.executeQuery(sql);

			boolean outer = true;
			boolean inner = true;
			boolean endOfOuter = false;
			boolean endOfInner = false;

			while (true) {
				if (outer) {
					if (!originalData.next()) {
						endOfOuter = true;
						if (inner) {
							if (!newData.next()) {
								endOfInner = true;
							}
						}
						break;
					}
				}

				if (inner) {
					if (!newData.next()) {
						endOfInner = true;
						if (outer) {
							if (!originalData.next()) {
								endOfOuter = true;
							}
						}
						break;
					}
				}

				int decision = compare(originalData, newData, commonCol);
				if (decision < 0) {
					// the tuple has been deleted
					for (int i = 0; i < col1.size(); i++) {
						String name = col1.get(i);
						if (indexedColumn.contains(name)) {
							// decrease the count by 1
							Object value = originalData.getObject(name);
							ArrayList<String> words = toString(value);
							for (int j = 0; j < words.size(); j++) {
								String keyword = words.get(j);

								// check the existing history
								if (updateHistory.containsKey(keyword)) {
									ArrayList<UpdateRecord> recordList = updateHistory
											.get(keyword);
									boolean hit = false;
									for (int x = 0; x < recordList.size(); x++) {
										UpdateRecord record = recordList.get(x);
										if (record.attributeName.equals("name")) {
											record.count--;
											hit = true;
										}
									}
									if (!hit) {
										UpdateRecord newrecord = new UpdateRecord();
										newrecord.keyword = keyword;
										newrecord.attributeName = name;
										newrecord.count = -1;
										newrecord.isNew = false;
										recordList.add(newrecord);
									}
								} else {
									ArrayList<UpdateRecord> recordList = new ArrayList<UpdateRecord>();
									UpdateRecord newrecord = new UpdateRecord();
									newrecord.keyword = keyword;
									newrecord.attributeName = name;
									newrecord.count = -1;
									newrecord.isNew = false;
									recordList.add(newrecord);
									updateHistory.put(keyword, recordList);
								}

								/*
								sql = "update " + indexTable + " set count=count-1 where " +
								      "keyword='" + keyword + "' and attribute='" + name + "'";
								updatestmt.executeUpdate(sql);
								*/
							}
						}
					}
					outer = true;
					inner = false;
				} else if (decision > 0) {
					// new tuple has been inserted
					for (int i = 0; i < col2.size(); i++) {
						String name = col2.get(i);
						if (indexedColumn.contains(name)) {
							// insert the new index
							Object value = newData.getObject(name);
							ArrayList<String> words = toString(value);
							for (int j = 0; j < words.size(); j++) {
								// check whether the keyword has been indexed
								String keyword = words.get(j);

								boolean hit = false;
								if (updateHistory.containsKey(keyword)) {
									ArrayList<UpdateRecord> recordList = updateHistory
											.get(keyword);
									for (int x = 0; x < recordList.size(); x++) {
										UpdateRecord record = recordList.get(x);
										if (record.attributeName.equals(name)) {
											hit = true;
											// only need to modify the record
											record.count++;
											break;
										}
									}
								}

								if (!hit) {
									sql = "select count(*) from " + indexTable
											+ " where " + "attribute='" + name
											+ "' and keyword='" + keyword + "'";
									ResultSet counter = updatestmt
											.executeQuery(sql);
									int number = 0;
									if (counter.next()) {
										number = counter.getInt(1);
									}
									counter.close();

									if (number == 0) {
										// new entry
										UpdateRecord record = new UpdateRecord();
										record.keyword = keyword;
										record.attributeName = name;
										record.count = 1;
										record.isNew = true;
										if (updateHistory.containsKey(keyword)) {
											ArrayList<UpdateRecord> recordList = updateHistory
													.get(keyword);
											recordList.add(record);
										} else {
											ArrayList<UpdateRecord> recordList = new ArrayList<UpdateRecord>();
											recordList.add(record);
											updateHistory.put(keyword,
													recordList);
										}
										/*
										sql = "insert into " + insertedIndex +  " values ('" + keyword +
										  "','" + name + "')";
										updatestmt.executeUpdate(sql);
										
										sql = "insert into " + indexTable + " values('" + keyword +
										  "','" + name + "', 1)";
										updatestmt.executeUpdate(sql);
										 */
									} else {
										// update the old one
										UpdateRecord record = new UpdateRecord();
										record.keyword = keyword;
										record.attributeName = name;
										record.count = 1;
										record.isNew = false;
										if (updateHistory.containsKey(keyword)) {
											ArrayList<UpdateRecord> recordList = updateHistory
													.get(keyword);
											recordList.add(record);
										} else {
											ArrayList<UpdateRecord> recordList = new ArrayList<UpdateRecord>();
											recordList.add(record);
											updateHistory.put(keyword,
													recordList);
										}
										/*
										sql = "update " + indexTable + " set count=count+1 where " +
										  "attribute='" + name + "' and keyword='" + keyword + "'";
										updatestmt.executeUpdate(sql);
										*/
									}
								}
							}
						}
					}
					outer = false;
					inner = true;
				} else {
					// match
					outer = true;
					inner = true;
				}

				if (updateHistory.size() > updateThreshold) {
					// execute the update in batch
					Enumeration<ArrayList<UpdateRecord>> enu = updateHistory
							.elements();
					while (enu.hasMoreElements()) {
						ArrayList<UpdateRecord> recordList = enu.nextElement();
						for (int i = 0; i < recordList.size(); i++) {
							UpdateRecord record = recordList.get(i);
							if (record.isNew) {
								sql = "insert into " + insertedIndex
										+ " values ('" + record.keyword + "','"
										+ record.attributeName + "')";
								updatestmt.addBatch(sql);

								sql = "insert into " + indexTable + " values('"
										+ record.keyword + "','"
										+ record.attributeName + "', "
										+ record.count + ")";
								updatestmt.addBatch(sql);
							} else {
								sql = "update " + indexTable
										+ " set count=count+" + record.count
										+ " where " + "attribute='"
										+ record.attributeName
										+ "' and keyword='" + record.keyword
										+ "'";
								updatestmt.addBatch(sql);
							}
						}
					}
					updatestmt.executeBatch();
					updateHistory.clear();
				}
			}

			// handle the rest of tuples
			if (endOfOuter && !endOfInner) {
				do {
					for (int i = 0; i < col2.size(); i++) {
						String name = col2.get(i);
						if (indexedColumn.contains(name)) {
							// insert the new index
							Object value = newData.getObject(name);
							ArrayList<String> words = toString(value);
							for (int j = 0; j < words.size(); j++) {
								// check whether the keyword has been indexed
								String keyword = words.get(j);

								boolean hit = false;
								if (updateHistory.containsKey(keyword)) {
									ArrayList<UpdateRecord> recordList = updateHistory
											.get(keyword);
									for (int x = 0; x < recordList.size(); x++) {
										UpdateRecord record = recordList.get(x);
										if (record.attributeName.equals(name)) {
											hit = true;
											// only need to modify the record
											record.count++;
											break;
										}
									}
								}

								if (!hit) {
									sql = "select count(*) from " + indexTable
											+ " where " + "attribute='" + name
											+ "' and keyword='" + keyword + "'";
									ResultSet counter = updatestmt
											.executeQuery(sql);
									int number = 0;
									if (counter.next()) {
										number = counter.getInt(1);
									}
									counter.close();

									if (number == 0) {
										// new entry
										UpdateRecord record = new UpdateRecord();
										record.keyword = keyword;
										record.attributeName = name;
										record.count = 1;
										record.isNew = true;
										if (updateHistory.containsKey(keyword)) {
											ArrayList<UpdateRecord> recordList = updateHistory
													.get(keyword);
											recordList.add(record);
										} else {
											ArrayList<UpdateRecord> recordList = new ArrayList<UpdateRecord>();
											recordList.add(record);
											updateHistory.put(keyword,
													recordList);
										}
									} else {
										// update the old one
										UpdateRecord record = new UpdateRecord();
										record.keyword = keyword;
										record.attributeName = name;
										record.count = 1;
										record.isNew = false;
										if (updateHistory.containsKey(keyword)) {
											ArrayList<UpdateRecord> recordList = updateHistory
													.get(keyword);
											recordList.add(record);
										} else {
											ArrayList<UpdateRecord> recordList = new ArrayList<UpdateRecord>();
											recordList.add(record);
											updateHistory.put(keyword,
													recordList);
										}
									}

									/*
									if(number == 0){
										//new entry
										sql = "insert into " + insertedIndex +  " values ('" + keyword +
										"','" + name + "')";
										updatestmt.executeUpdate(sql);
									
										sql = "insert into " + indexTable + " values('" + keyword +
									      "','" + name + "', 1)";
										updatestmt.executeUpdate(sql);
									}
									else{
										//update the old one
										sql = "update " + indexTable + " set count=count+1 where " +
										"attribute='" + name + "' and keyword='" + keyword + "'";
										updatestmt.executeUpdate(sql);
									}*/
								}
							}
						}
					}

					// periodically process the update
					if (updateHistory.size() > updateThreshold) {
						// execute the update in batch
						Enumeration<ArrayList<UpdateRecord>> enu = updateHistory
								.elements();
						while (enu.hasMoreElements()) {
							ArrayList<UpdateRecord> recordList = enu
									.nextElement();
							for (int i = 0; i < recordList.size(); i++) {
								UpdateRecord record = recordList.get(i);
								if (record.isNew) {
									sql = "insert into " + insertedIndex
											+ " values ('" + record.keyword
											+ "','" + record.attributeName
											+ "')";
									updatestmt.addBatch(sql);

									sql = "insert into " + indexTable
											+ " values('" + record.keyword
											+ "','" + record.attributeName
											+ "', " + record.count + ")";
									updatestmt.addBatch(sql);
								} else {
									sql = "update " + indexTable
											+ " set count=count+"
											+ record.count + " where "
											+ "attribute='"
											+ record.attributeName
											+ "' and keyword='"
											+ record.keyword + "'";
									updatestmt.addBatch(sql);
								}
							}
						}
						updatestmt.executeBatch();
						updateHistory.clear();
					}

				} while (newData.next());
			} else if (!endOfOuter && endOfInner) {
				do {
					for (int i = 0; i < col1.size(); i++) {
						String name = col1.get(i);
						if (indexedColumn.contains(name)) {
							// decrease the count by 1
							Object value = originalData.getObject(name);
							ArrayList<String> words = toString(value);
							for (int j = 0; j < words.size(); j++) {
								String keyword = words.get(j);
								sql = "update " + indexTable
										+ " set count=count-1 where "
										+ "keyword='" + keyword
										+ "' and attribute='" + name + "'";
								updatestmt.addBatch(sql);
								// updatestmt.executeUpdate(sql);
							}
						}
						if (i % updateThreshold == 0)
							updatestmt.executeBatch();
					}
					updatestmt.executeBatch();
				} while (originalData.next());
			}

			originalData.close();
			newData.close();

			// handle the rest updates
			if (updateHistory.size() > 0) {
				Enumeration<ArrayList<UpdateRecord>> enu = updateHistory
						.elements();
				while (enu.hasMoreElements()) {
					ArrayList<UpdateRecord> recordList = enu.nextElement();
					for (int i = 0; i < recordList.size(); i++) {
						UpdateRecord record = recordList.get(i);
						if (record.isNew) {
							sql = "insert into " + insertedIndex + " values ('"
									+ record.keyword + "','"
									+ record.attributeName + "')";
							updatestmt.addBatch(sql);

							sql = "insert into " + indexTable + " values('"
									+ record.keyword + "','"
									+ record.attributeName + "', "
									+ record.count + ")";
							updatestmt.addBatch(sql);
						} else {
							if (record.count > 0)
								sql = "update " + indexTable
										+ " set count=count+" + record.count
										+ " where " + "attribute='"
										+ record.attributeName
										+ "' and keyword='" + record.keyword
										+ "'";
							else
								sql = "update " + indexTable
										+ " set count=count" + record.count
										+ " where " + "attribute='"
										+ record.attributeName
										+ "' and keyword='" + record.keyword
										+ "'";
							updatestmt.addBatch(sql);
						}
					}
				}
				updatestmt.executeBatch();
				updateHistory.clear();
			}

			// getting the index that should be deleted
			sql = "create table " + deletedIndex
					+ " (select keyword, attribute from " + indexTable
					+ " where count<=0)";
			updatestmt.executeUpdate(sql);
			sql = "delete from " + indexTable + " where count<=0";
			updatestmt.executeUpdate(sql);

			// close the statement
			selstmt.close();
			updatestmt.close();
		} catch (Exception e) {
			System.err.println("fail to compare the tables");
			e.printStackTrace();
		}
	}

	private int compare(ResultSet left, ResultSet right, ArrayList<String> cols) {
		try {
			for (int i = 0; i < cols.size(); i++) {
				String name = cols.get(i);

				Object value1 = left.getObject(name);
				Object value2 = right.getObject(name);

				if (value1 instanceof Integer && value2 instanceof Integer) {
					int v1 = ((Integer) value1).intValue();
					int v2 = ((Integer) value2).intValue();
					if (v1 != v2)
						return (v1 - v2);
				} else if (value1 instanceof String && value2 instanceof String) {
					String v1 = (String) value1;
					String v2 = (String) value2;
					if (!v1.equals(v2))
						return v1.compareTo(v2);
				} else if (value1 instanceof Boolean
						&& value2 instanceof Boolean) {
					boolean v1 = ((Boolean) value1).booleanValue();
					boolean v2 = ((Boolean) value2).booleanValue();
					if (v1 != v2) {
						if (v1 == true)
							return 1;
						else
							return -1;
					}
				} else if (value1 instanceof Byte && value2 instanceof Byte) {
					byte v1 = ((Byte) value1).byteValue();
					byte v2 = ((Byte) value2).byteValue();
					if (v1 != v2)
						return v1 - v2;
				} else if (value1 instanceof Date && value2 instanceof Date) {
					Date v1 = (Date) value1;
					Date v2 = (Date) value2;
					if (!v1.equals(v2))
						return v1.compareTo(v2);
				} else if (value1 instanceof Double && value2 instanceof Double) {
					double v1 = ((Double) value1).doubleValue();
					double v2 = ((Double) value2).doubleValue();
					if (v1 != v2)
						if (v1 < v2)
							return -1;
						else
							return 1;
				} else if (value1 instanceof Float && value2 instanceof Float) {
					float v1 = ((Float) value1).floatValue();
					float v2 = ((Float) value2).floatValue();
					if (v1 != v2)
						if (v1 < v2)
							return -1;
						else
							return 1;
				} else if (value1 instanceof Long && value2 instanceof Long) {
					long v1 = ((Long) value1).longValue();
					long v2 = ((Long) value2).longValue();
					if (v1 != v2)
						if (v1 < v2)
							return -1;
						else
							return 1;
				} else if (value1 instanceof Short && value2 instanceof Short) {
					short v1 = ((Short) value1).shortValue();
					short v2 = ((Short) value2).shortValue();
					if (v1 != v2)
						if (v1 < v2)
							return -1;
						else
							return 1;
				} else if (value1 instanceof Time && value2 instanceof Time) {
					Time v1 = (Time) value1;
					Time v2 = (Time) value2;
					if (!v1.equals(v2)) {
						return v1.compareTo(v2);
					}
				}
			}
			// System.err.println("Fail to compare the values!");
			return 0;
		} catch (Exception e) {
			System.err.println("Exceptions in value comparison!");
			e.printStackTrace();
			return -1;
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
