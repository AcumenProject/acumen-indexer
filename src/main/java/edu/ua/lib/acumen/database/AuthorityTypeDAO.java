package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class AuthorityTypeDAO extends MysqlDAO {

	private static final String AUTHORITY_TYPE_LIST = "SELECT * FROM authority_type";

	public Map<String, Integer> typeIDMap() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, Integer> types = new HashMap<String, Integer>();
		
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(AUTHORITY_TYPE_LIST);
			while (rs.next()) {
				types.put(rs.getString("type"), rs.getInt("id"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		
		return types;
	}
	
	public int getTypeId(String type) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		int id = -1;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT id FROM authority_type WHERE type='" + type +"'");
			while (rs.next()){
				id = rs.getInt("id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		} 
		
		return id;
	}

}