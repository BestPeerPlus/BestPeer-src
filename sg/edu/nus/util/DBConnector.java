/*
 * @(#) DBConnector.java 1.0 2006-1-27
 * 
 * Copyright 2006, National University of Singapore.
 * All rights reserved.
 */
package sg.edu.nus.util;

import java.sql.*;
import java.util.Vector;
import java.sql.ResultSetMetaData;

/**
 * This class is used for providing the default information in order to connect 
 * to the back-end database.
 * 
 * @author Xu Linhao
 * @version 1.0 2006-1-27
 */

public abstract class DBConnector
{

	/* 
	 * FIXME: currently, we hardcode the user name, password and IP address
	 * of the back-end database. 
	 */
	private final static String DSN  = "jdbc:mysql://spade.ddns.comp.nus.edu.sg:3306/PEERDB"; 
	private final static String USER = "peerdb";
	private final static String PWD  = "123456";
	
	/**
     * Initiate the MySQL driver to be prepared for building
     * connection with the database.
     */
    public static void registerDriver() 
    {
		try 
		{
			Class.forName("com.mysql.jdbc.Driver");
		} 
		catch(java.lang.ClassNotFoundException e) 
		{
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
    }
    
    /**
     * Build connection with the back-end database with the default information.
     * 
     * @return the <code>Connection</code> with a specified database
     */
    public static Connection getConnection()
    {
    	try
    	{
    		return DriverManager.getConnection(DSN, USER, PWD);
    	}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
    }
    
     //VHTam: add following code to the next //end VHTam
     //new function to create db connection from input parameters
         
    public static Connection getConnection(String DSN, String USER, String PWD)
    {
    	
    	try
    	{
    		return DriverManager.getConnection(DSN, USER, PWD);
    	}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Cannot create connection!!!!");
		}
		return null;
    }
    
    public static ResultSet executeQuery(Connection conn, String selectSQL){
    	try {
    		
			Statement stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery(selectSQL);
			
			return rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }
    
    public static Vector<Vector<String>> getDataFromResultSet(ResultSet rs){
    	try {
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int nCol = rsmd.getColumnCount();
			
			Vector<Vector<String>> rows = new Vector<Vector<String>>();
			
			while (rs.next()) {
				Vector<String> row = new Vector<String>();
				for (int j = 0; j < nCol; j++) {
					row.add(rs.getString(j + 1));
				}
				rows.add(row);
			}
			
			return rows;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }

    public static Vector<String> getColumnNames(ResultSet rs){
    	try {
    		Vector<String> columnIds = new Vector<String>();
    				
			ResultSetMetaData rsmd = rs.getMetaData();
			int nCol = rsmd.getColumnCount();
			
			for (int i = 0; i < nCol; i++) {
				columnIds.add(rsmd.getColumnName(i + 1));
			}
			
			return columnIds;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

	public static void printRS(ResultSet rs) {
		if (rs==null){
			System.out.println("NULL result set");
		}
		try {
			ResultSetMetaData md = rs.getMetaData();
			int count = md.getColumnCount();
			for (int i = 1; i <= count; i++) {
				System.out.print(md.getColumnLabel(i));
				System.out.print('\t');
			}
			System.out.println();
			while (rs.next()) {
				for (int i = 1; i <= count; i++) {
					System.out.print(rs.getObject(i));
					System.out.print('\t');
				}
				System.out.println();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
     * Just for testing above code
     */	
    public static void main(String[] args){
    	System.out.println("Test db");
    	String DSN  = "jdbc:mysql://localhost:3306/s3"; 
    	String USER = "s3";
    	String PWD  = "s3";
    	
    	DBConnector.registerDriver();
    	Connection conn = DBConnector.getConnection(DSN, USER, PWD);
    	try {
    		
			String sql = "SELECT * FROM global_schemas";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			
			DBConnector.printRS(rs);
			
			conn.close();
			
    	} catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    	System.out.println("End test db");
    }
    //end VHTam
	
}