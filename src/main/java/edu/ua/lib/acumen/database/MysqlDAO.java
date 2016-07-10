package edu.ua.lib.acumen.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ua.lib.acumen.repo.Location;
import edu.ua.lib.acumen.util.EscapeChars;


public class MysqlDAO {
	
	protected static Connection con = Mysql.getConnection();
	protected static ArrayList<String>batchQueries = new ArrayList<String>();
	protected static ArrayList<String>batchFound = new ArrayList<String>();
	//protected static Statement batchStmt = null;
	//protected static int batchSize = 0;
	//protected static Statement batchFoundStmt = null;
	//protected static int batchFoundSize = 0;
	private static Pattern stmtPattern = Pattern.compile("\\?");
	
	public void closeConnection(){
		if (con != null){
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected LinkedList<LinkedList<Object>> select(String select) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		LinkedList<LinkedList<Object>> rows = new LinkedList<LinkedList<Object>>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(select);
			ResultSetMetaData rsMeta = rs.getMetaData();
			while (rs.next()){
				LinkedList<Object> row = new LinkedList<Object>();
				for (int i=0; i<rsMeta.getColumnCount(); i++){
					row.add(rs.getObject(i));
				}
				rows.add(row);
			}
			
			return rows;
		} catch (SQLException e) {
			System.out.println("MysqlDAO.select:  "+select);
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null){ stmt.close(); }
		}
		return null;
	}
		
	public long update(String update) throws SQLException{
		Statement stmt = null;
		ResultSet genKeys = null;
		long insertedID = -1;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(update, Statement.RETURN_GENERATED_KEYS);
			genKeys = stmt.getGeneratedKeys();
			if (genKeys.next()){
				insertedID = genKeys.getLong(1);
			}
		} catch (SQLException e) {
			System.out.println("UPDATE FAIL: "+update);
			e.printStackTrace();
		} finally {
			if (genKeys != null){ genKeys.close(); }
			if (stmt != null){ stmt.close(); }
		}
		return insertedID;
	}
	
	public void batchUpdate(String update) throws SQLException{
		if (batchQueries.size() >= 500 ){
			batchExecute();
		}
		batchQueries.add(update);
	}
	
	/*public void batchFound(String found) throws SQLException{
		if (batchFound.size() >= 500){
			Connection batchCon = null;
			Statement stmt = null;
			try{
				batchCon = Mysql.getConnection();
				stmt = batchCon.createStatement();
				for (String query : batchFound){
					stmt.addBatch(query);
				}
				stmt.executeBatch();
				
			} catch (SQLException e){
				System.out.println("MysqlDAO.batchFound:" + found);
				e.printStackTrace();
			} finally {
				if (stmt != null) { stmt.close(); }
				if (batchCon != null) { batchCon.close(); }
				batchFound.clear();
			}
		}
		batchFound.add(found);
	}*/
	
	public static void batchExecute() throws SQLException{
		Connection batchCon = null;
		Statement stmt = null;
		String tempQuery = null;
		try{
			batchCon = Mysql.getConnection();
			stmt = batchCon.createStatement();
			for (String query : batchQueries){
				tempQuery = query;
				stmt.addBatch(query);
			}
			stmt.executeBatch();
			
		} catch (SQLException e){
			System.out.println("MysqlDAO.batchExecute:");
			System.out.println(tempQuery);
			System.out.println("-----------------------------------");
			e.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
			if (batchCon != null) { batchCon.close(); }
			batchQueries.clear();
		}
		/*if (batchQueries.size() > 0){
			batchStmt.executeBatch();
			batchStmt.clearBatch();
			batchSize = 0;
		}*/
	}
	
	public static void delete(String query) throws SQLException{
		Connection delCon = null;
		Statement stmt = null;
		
		try {
			delCon = Mysql.getConnection();
			stmt = delCon.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MysqlDAO.delete:  "+query);
			e.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
			if (delCon != null) { delCon.close(); }
			query = null;
		}
	}
	
	public long getParentID(String queryTemplate, String fileName) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null; 
		
		String parentLoc = Location.ofParent(fileName);
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(parentLoc);
		
		String query = buildQuery(queryTemplate, vals);
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()){
				return rs.getLong(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MysqlDAO.getParentID:  "+queryTemplate+", "+fileName);
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null){ stmt.close(); }
		}
		return 0;
		
	}
	
	public long findParentID(String file_name) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		
		String query = "SELECT id FROM file WHERE file_name LIKE '"+file_name+".%' AND status_type_id=1";
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()){
				return rs.getLong("id");
			}
			String parent = file_name.substring(0, file_name.lastIndexOf('_'));
			return findParentID(parent);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("MysqlDAO.findParentID:  "+file_name);
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null){ stmt.close(); }
		}
		return 0;
		
	}
	
	protected String buildInsert(String st, LinkedList<Object> values){
		values.pop();
		return buildQuery(st, values);
	}
	
	protected String buildUpdate(String st, LinkedList<Object> values){
		Object id = values.pop();
		values.add(id);
		return buildQuery(st, values);
	}
			
	public String buildQuery(String st, LinkedList<Object> values){
		Matcher m = stmtPattern.matcher(st);
		StringBuffer sb = new StringBuffer();

		Iterator<Object> it = values.iterator();
		while (m.find()){
			String val =String.valueOf(it.next());
			String cleanVal = EscapeChars.forSQL(val);
			m.appendReplacement(sb, cleanVal);
		}
		m.appendTail(sb);
		return sb.toString();
	}
}