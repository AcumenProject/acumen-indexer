package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class StatusTypeDAO extends MysqlDAO{
	
	private static final String STATUS_TYPE_SELECT = "SELECT * FROM status_type";
	
	public Map<String, Integer> typeMap() throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, Integer> types = new HashMap<String, Integer>();
		

		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(STATUS_TYPE_SELECT);
			while (rs.next()){
				types.put(rs.getString("type"), rs.getInt("id"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}

		return types;
	}
	
}
