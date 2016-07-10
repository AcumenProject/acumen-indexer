package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MetadataTypeDAO extends MysqlDAO {

	//private static final String METADATA_TYPE_LIST = "SELECT * FROM file_type ORDER BY (CASE WHEN priority IS NULL then 1 ELSE 0 END), priority";
	private static final String METADATA_TYPE_EXTENSION_LIST = "SELECT extension FROM file_type ORDER BY (CASE WHEN priority IS NULL then 1 ELSE 0 END), priority";
	private static final String METADATA_PARENT_EXTENSION_LIST = "SELECT extension FROM file_type WHERE priority IS NOT NULL ORDER BY priority";
	//private static final String METADATA_TYPE_NAME_LIST = "SELECT type FROM file_type ORDER BY (CASE WHEN priority IS NULL then 1 ELSE 0 END), priority";
	private static final String METADATA_TYPE_MAP = "SELECT id, extension FROM file_type ORDER BY (CASE WHEN priority IS NULL then 1 ELSE 0 END), priority";

	public LinkedList<String> listExtensions() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		LinkedList<String> extensions = new LinkedList<String>();

		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(METADATA_TYPE_EXTENSION_LIST);
			while (rs.next()) {
				extensions.add(rs.getString("extension"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		
		return extensions;
	}
	
	public LinkedList<String> parentExtensions() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		LinkedList<String> extensions = new LinkedList<String>();
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(METADATA_PARENT_EXTENSION_LIST);
			while (rs.next()) {
				extensions.add(rs.getString("extension"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		
		return extensions;
	}

	public Map<Integer, String> typeIDMap() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		Map<Integer, String> types = new HashMap<Integer, String>();

		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(METADATA_TYPE_MAP);
			while (rs.next()) {
				types.put(rs.getInt("id"), rs.getString("extension"));
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