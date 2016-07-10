package edu.ua.lib.acumen.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class AuthorityDAO extends MysqlDAO {
	
	//Overwrite connection default inherited by MysqlDAO
	// Authorities are made with batch updates and only create a connection when
	// the batch limit is full and needs to be executed
	protected Connection con = null;

	/*private static final String AUTHORITY_JOIN_COLS = "a0.value AS 'value', a0.id AS 'id', a0.file_id AS 'file_id', "+
														"a0.asset_id, 'asset_id', a0.authority_type_id AS 'authority_type_id' ";*/
		
	//private static final String AUTHORITY_SELECT = "SELECT * FROM authority WHERE file_id = ?";
	private static final String AUTHORITY_UPDATE = "UPDATE authority SET file_id = ?, asset_id = ?, authority_type_id = ?, value = ? WHERE id = ?";
	private static final String AUTHORITY_INSERT = "INSERT INTO authority (file_id, asset_id, authority_type_id, value) VALUES (?, ?, ?, '?')";
	
	// Join statements
	/*private static final String AUTHORITY_SELECT_ALL = "SELECT "+AUTHORITY_JOIN_COLS+
												"a1.type AS 'type', "+
												"FROM authority "+
												"INNER JOIN authority_type AS a1 ON a0.authority_type_id = a1.id "+
												"WHERE a0.file_id = ? ORDER BY a0.value";
	private static final String AUTHORITY_SELECT_TYPE = "SELECT "+AUTHORITY_JOIN_COLS+
														"INNER JOIN authority_type AS a1 ON a0.authority_type_id = a1.id"+
														"WHERE a0.file_id = ? AND a1.type = ?";*/
	
	public void insert(Authority authority) throws SQLException{
		String insert = buildInsert(AUTHORITY_INSERT, authority.listValues());
		//System.out.println(" -- "+insert);
		batchUpdate(insert);
	}
	
	public void update(Authority authority) throws SQLException{
		String update = buildUpdate(AUTHORITY_UPDATE, authority.listValues());
		//System.out.println(" -- "+update);
		batchUpdate(update);
	}
	
	public void removeAll(long id) throws SQLException{
		Statement stmt = null;
		Connection rmCon = null;
		try {
			rmCon = Mysql.getConnection();
			stmt = rmCon.createStatement();
			//System.out.println(" -- DELETE FROM authority WHERE id = "+id);
			stmt.executeUpdate("DELETE FROM authority WHERE file_id = "+id+" AND authority_type_id != 22 AND authority_type_id != 23");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
			if (rmCon != null){ rmCon.close(); }
		}
	}
	
	public void removeAllByType(long id, int type_id) throws SQLException{
		Statement stmt = null;
		Connection rmCon = null;
		try {
			rmCon = Mysql.getConnection();
			stmt = rmCon.createStatement();
			stmt.executeUpdate("DELETE FROM authority WHERE file_id = " + id + " AND authority_type_id = " + type_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stmt != null) { stmt.close(); }
			if (rmCon != null){ rmCon.close(); }
		}
	}
	
	
	protected List<Authority> buildAuthorityList(ResultSet rs) throws SQLException {
		List<Authority> authorities = new LinkedList<Authority>();
		while (rs.next()){
			Authority a = new Authority(
								rs.getLong("id"),
								rs.getLong("file_id"),
								rs.getLong("asset_id"),
								rs.getInt("authority_type_id"),
								rs.getString("value"));
			authorities.add(a);
		}
		return authorities;
	}
}