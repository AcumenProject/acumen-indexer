package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AssetTypeDAO extends MysqlDAO {

	//private static final String ASSET_TYPE_LIST = "SELECT * FROM asset_type";
	private static final String ASSET_TYPE_EXTENSION_LIST = "SELECT asset_tails FROM asset_type";
	// private static final String ASSET_TYPE_NAME_LIST =
	// "SELECT type FROM asset_type";
	private static final String ASSET_TYPE_MAP = "SELECT id, asset_tails FROM asset_type";
	//private static final String ASSET_TYPE_LABEL_MAP = "SELECT id, asset_tails FROM asset_type";

	public List<String> listExtensions() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		List<String> extensions = new LinkedList<String>();
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(ASSET_TYPE_EXTENSION_LIST);
			while (rs.next()) {
				String[] tails = rs.getString("asset_tails").split(",");
				for (String tail : tails) {
					extensions.add(tail);

				}
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
		
	public Map<Integer, List<String>> typeMap() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		Map<Integer, List<String>> types = new HashMap<Integer, List<String>>();

		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(ASSET_TYPE_MAP);
			while (rs.next()) {
				String[] tails = rs.getString("asset_tails").split(",");
				List<String> extensions = new ArrayList<String>();
				for (String tail : tails) {
					extensions.add(tail);
					// System.out.println(tail);
				}
				types.put(rs.getInt(1), extensions);
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