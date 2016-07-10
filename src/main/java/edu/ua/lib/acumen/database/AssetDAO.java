package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import edu.ua.lib.acumen.repo.Location;

public class AssetDAO extends MysqlDAO{
	
	public static final String ASSET_SELECT_BY_ID = "SELECT * FROM asset WHERE id = ?";
	public static final String ASSET_SELECT_BY_NAME = "SELECT * FROM asset WHERE name = '?'";
	public static final String ASSET_UPDATE = "UPDATE asset SET "+
												"asset_type_id = ?, name = '?', orig_path = '?', thumb_path = '?', "+
												"file_id = ?, file_size = ?, file_last_modified = ?, status_type_id = ?, found = ? "+
												"WHERE id = ?";
	
	public static final String ASSET_INSERT = "INSERT INTO asset "+
												"(asset_type_id, name, orig_path, thumb_path, file_id, "+
												"file_size, file_last_modified, status_type_id, found) "+
												"VALUES (?, '?', '?', '?', ?, ?, ?, ?, ?)";
	public static final String ASSET_GET_PARENT_ID = "SELECT id FROM file WHERE file_name LIKE '?.%' ORDER BY status_type_id LIMIT 1";
	public static final String ASSET_FIND_CLOSEST_PARENT_ID = "SELECT id FROM file WHERE file_name LIKE '?.%' AND status_type_id=1";
	
	public void update(Asset asset) throws SQLException{
		String query = buildUpdate(ASSET_UPDATE, asset.listValues());
		//System.out.println(" -- "+query);
		batchUpdate(query);
	}
	
	public void insert(Asset asset) throws SQLException{
		String query = buildInsert(ASSET_INSERT, asset.listValues());
		//System.out.println(" -- "+query);
		batchUpdate(query);
		//return -1;
	}
	
	public void found(long id) throws SQLException{
		String query = "UPDATE asset SET found=1 WHERE id="+id;
		batchUpdate(query);
		//System.out.println(" -- "+query);
	}
	
	public long exists(String repoLoc) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT id FROM asset WHERE name = '"+repoLoc+"'");
			if (rs.next()){
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return -1;
	}
	
	public long lastModified(long id) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT file_last_modified FROM asset WHERE id = "+id);
			if (rs.next()){
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return -1;
	}
	
	public List<Asset> selectList(String query) throws SQLException{
		List<Asset> metadata = new LinkedList<Asset>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				Asset meta = new Asset(
						rs.getLong("id"),
						rs.getInt("asset_type_id"),
						rs.getString("name"),
						rs.getString("orig_path"),
						rs.getString("thumb_path"),
						rs.getLong("file_id"),
						rs.getInt("file_size"),
						rs.getLong("file_last_modified"),
						rs.getInt("status_type_id"),
						rs.getByte("found"));
				metadata.add(meta);
			}
			return metadata;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return null;
	}
	
	public Asset get(long id) throws SQLException {
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(id);
		String query = buildQuery(ASSET_SELECT_BY_ID, vals);
		List<Asset> assetList = selectList(query);
		if (!assetList.isEmpty()){
			try {
				return assetList.get(0);
			} finally {
				assetList = null;
			}
		}
		return null;
	}
	
	public Asset get(String repoLoc) throws SQLException {
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(repoLoc);
		String query = buildQuery(ASSET_SELECT_BY_NAME, vals);
		List<Asset> assetList = selectList(query);
		if (!assetList.isEmpty()){
			try {
				return assetList.get(0);
			} finally {
				assetList = null;
			}
		}
		return null;
	}
	
	public long getParentID(String repoLoc) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		
		
		try {
			if (repoLoc != null) {
				LinkedList<Object> vals = new LinkedList<Object>();
				vals.add(repoLoc);
				String query = buildQuery(ASSET_FIND_CLOSEST_PARENT_ID, vals);
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getLong("id");
				}
				String parentLoc = Location.stepBackLoc(repoLoc);
				return getParentID(parentLoc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return -1;
	}
}