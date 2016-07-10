package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.ua.lib.acumen.repo.Location;
import edu.ua.lib.acumen.repo.Database;
import edu.ua.lib.acumen.repo.Regex;

public class MetadataDAO extends MysqlDAO {

	public static final String METADATA_SELECT_BY_ID =  "SELECT * FROM file WHERE id = ?";
	public static final String METADATA_SELECT_LAST_MODIFIED_BY_ID =  "SELECT file_last_modified FROM file WHERE id = ?";
	public static final String METADATA_SELECT_BY_NAME =  "SELECT * FROM file WHERE file_name = '?'";
	public static final String METADATA_SELECT_AT_REPO_LOC = "SELECT id FROM file WHERE file_name LIKE '?.%' AND status_type_id=1";
	public static final String METADATA_INSERT =  "INSERT INTO file "+
													"(parent_id, file_type_id, title, file_name, " +
													"file_path, file_size, file_last_modified, status_type_id, found) "+
													"VALUES (?, ?, '?', '?', '?', ?, ?, ?, ?)";
	public static final String METADATA_UPDATE =  "UPDATE file SET "+
													"parent_id = ?, file_type_id = ?, title = '?', file_name = '?', "+
													"file_path = '?', file_size = ?, file_last_modified = ?, status_type_id = ?, found = ? "+
													"WHERE id = ?";
	public static final String METADATA_GET_PARENT_ID = "SELECT id FROM file WHERE file_name LIKE '?.%' AND status_type_id=1";
	public static final String METADATA_GET_PARENT_TITLE = "SELECT title FROM file WHERE file_name LIKE '?.%' ORDER BY status_type_id LIMIT 1";
	public static final String METADATA_GET_PARENT_INFO = "SELECT id, title FROM file WHERE file_name LIKE '?.%' AND status_type_id=1";

	public void update(Metadata meta) throws SQLException{
		String query = buildUpdate(METADATA_UPDATE, meta.listValues());
		//System.out.println(" -- "+query);
		super.update(query);
	}
	
	public long insert(Metadata meta) throws SQLException{
		String query = buildInsert(METADATA_INSERT, meta.listValues());
		//System.out.println(" -- "+query);
		return super.update(query);
		//return -1;
	}
	
	public void found(long id) throws SQLException{
		String query = "UPDATE file SET found=1 WHERE id="+id;
		batchUpdate(query);
		//System.out.println(" -- "+query);
	}
	
	public Map<String, Long> exists(String fileName) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, Long> exists = new HashMap<String, Long>();
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT id, file_last_modified FROM file WHERE file_name='"+fileName+"'");
			while (rs.next()){
				exists.put("id", rs.getLong("id"));
				exists.put("lastModified", rs.getLong("file_last_modified"));
			}
			/*if (!exists.isEmpty()){
				return exists;
			}
			return exists;*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return exists;
	}
	
	public Object selectField(String query) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()){
				return rs.getObject(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return null;
	}
		
	public List<Metadata> selectList(String query) throws SQLException{
		List<Metadata> metadata = new LinkedList<Metadata>();
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while(rs.next()){
				Metadata meta = new Metadata(
									rs.getLong("id"),
									rs.getLong("parent_id"),
									rs.getInt("file_type_id"),
									rs.getString("title"),
									rs.getString("file_name"),
									rs.getString("file_path"),
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
	
	public long lastModified(long id) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT file_last_modified FROM file WHERE id = "+id);
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
	
	public Metadata get(long id) throws SQLException {
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(id);
		String query = buildQuery(METADATA_SELECT_BY_ID, vals);
		List<Metadata> metaList = selectList(query);
		if (!metaList.isEmpty()){
			try {
				return metaList.get(0);
			} finally {
				metaList = null;
			}
		}
		return null;
	}
	
	public Metadata get(String fileName) throws SQLException {
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(fileName);
		String query = buildQuery(METADATA_SELECT_BY_NAME, vals);
		List<Metadata> metaList = selectList(query);
		if (!metaList.isEmpty()){
			try {
				return metaList.get(0);
			} finally {
				metaList = null;
			}
		}
		return null;
	}
	
	public long getParentID(String repoLoc) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			if (repoLoc != null) {
				String parentLoc = Location.stepBackLoc(repoLoc);
				LinkedList<Object> vals = new LinkedList<Object>();
				vals.add(parentLoc);
				String query = buildQuery(METADATA_GET_PARENT_ID, vals);
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
				if (rs.next()) {
					return rs.getLong("id");
				}
				
				return getParentID(parentLoc);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return 0;
	} 
	
	public Map<String, String> getParentInfo(String repoLoc) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		
		if (repoLoc != null) {
			LinkedList<Object> vals = new LinkedList<Object>();
			vals.add(repoLoc);
			String query = buildQuery(METADATA_GET_PARENT_INFO, vals);
			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(query);
				Map<String, String> info = new HashMap<String, String>();
				if (rs.next()) {
					info.put("id", rs.getString("id"));
					info.put("title", rs.getString("title"));
				}
				
				if (!info.isEmpty()) {
					return info;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (rs != null) { rs.close(); }
				if (stmt != null) { stmt.close(); }
			}
			String parentLoc = Location.stepBackLoc(repoLoc);
			return getParentInfo(parentLoc);
		}
		return null;
	}
	
	public String getParentTitle(String repoLoc) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(repoLoc);
		
		String query = buildQuery(METADATA_GET_PARENT_TITLE, vals);
		try {
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs != null) {
				if (rs.next()){
					return rs.getString(1);
				}
			} 
			String parentLoc = Location.stepBackLoc(repoLoc);
			if (parentLoc != null){
				return getParentTitle(parentLoc);
			}
			return null;
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
	}
	
	public int inferStatusTypeId(String repoLoc, String ext) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		LinkedList<Object> vals = new LinkedList<Object>();			
		
		if ((".txt").equals(ext) || (".tags.xml").equals(ext)){
			return 5;
		}
		
		vals.add(repoLoc);
		String query = buildQuery("SELECT id, file_name FROM file WHERE file_name LIKE '?.%' AND status_type_id=1", vals);
		
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			if (rs.next()){					
				String siblingExt = Regex.getExtension(rs.getString("file_name"));
				
				if (Database.metaExtensions().indexOf(ext) < Database.metaExtensions().indexOf(siblingExt)){	
					int siblingId = rs.getInt("id");
					batchUpdate("UPDATE file SET status_type_id = 3 WHERE id = "+siblingId);
					return 1;
				}
				else {
					return 3;
				}
			}
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		
		return 1;			
	}
	
	/*public int inferStatusTypeId(String repoLoc, String ext) throws SQLException{
		Statement stmt = null;
		ResultSet rs = null;
		LinkedList<Object> vals = new LinkedList<Object>();
		
		if ((".txt").equals(ext) || (".tags.xml").equals(ext)){
			return 5;
		}
		
		vals.add(repoLoc);
		String query = buildQuery(METADATA_SELECT_AT_REPO_LOC, vals);
		try {
			
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (rs.next()) {
				return 3;
			}
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
		}
		return 1;
	}*/
	
	public void verifyChildren(long id, long parent_id, String repoLoc) throws SQLException{
		Statement stmt = null;
		Statement s = null;
		ResultSet rsf = null;
		ResultSet rsa = null;
		
		try{
			stmt = con.createStatement();
			s = con.createStatement();
			rsf = stmt.executeQuery("SELECT id, file_type_id FROM file WHERE file_name LIKE '"+repoLoc+"\\_%' AND parent_id="+parent_id);
			while(rsf.next()){
				long childID = rsf.getLong("id");
				int fileTypeID = rsf.getInt("file_type_id");
				
				s.executeUpdate("UPDATE file SET parent_id = "+id+" WHERE id = "+childID);
				//System.out.println("SELECT id, file_type_id FROM file WHERE file_name LIKE '"+repoLoc+"\\_%' AND parent_id="+parent_id);
				//System.out.println("UPDATE file SET parent_id = "+id+" WHERE id = "+childID);
				if (fileTypeID == 5 || fileTypeID  == 7){
					s.executeUpdate("UPDATE authority SET file_id = "+id+" WHERE asset_id = "+childID);
					//System.out.println("UPDATE authority SET file_id = "+id+" WHERE asset_id = "+childID);
				}
			}
			
			rsa = stmt.executeQuery("SELECT id FROM asset WHERE name LIKE '"+repoLoc+"%' AND file_id="+parent_id);
			while(rsa.next()){
				s.executeUpdate("UPDATE asset SET file_id = "+id+" WHERE id = "+rsa.getLong("id"));
				//System.out.println("SELECT id FROM asset WHERE name LIKE '"+repoLoc+"%' AND file_id="+parent_id);
				//System.out.println("UPDATE asset SET file_id = "+id+" WHERE id = "+rsa.getLong("id"));
			}
		} finally {
			if (rsf != null) { rsf.close(); }
			if (rsa != null) { rsa.close(); }
			if (stmt != null) { stmt.close(); }
			if (s != null) { s.close(); }
		}
	}
}