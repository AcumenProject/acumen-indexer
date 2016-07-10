package edu.ua.lib.acumen.repo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.ua.lib.acumen.database.AssetTypeDAO;
import edu.ua.lib.acumen.database.AuthorityTypeDAO;
import edu.ua.lib.acumen.database.MetadataTypeDAO;
import edu.ua.lib.acumen.database.Mysql;
import edu.ua.lib.acumen.database.MysqlDAO;
import edu.ua.lib.acumen.database.StatusTypeDAO;

public final class Database {
	
	public static final String METADATA_ORPHAN = "file_orphan";
	public static final String ASSET_ORPHAN = "file_orphan";
	
	private static LinkedList<String> METADATA_EXTENSIONS = null;
	private static LinkedList<String> METADATA_PARENT_EXTENSIONS = null;
	private static Map<Integer, String> METADATA_TYPE_ID_MAP = null;
	
	private static Map<Integer, List<String>> ASSET_TYPE_ID_MAP = null;
	private static List<String> ASSET_EXTENSIONS = null;
	
	private static Map<String, Integer> AUTHORITY_TYPE_ID_MAP = null;
	
	private static Map<String, Integer> STATUS_TYPE_ID_MAP = null;
	
	public static Map<String, Integer> AUTHORITY_TYPES;
	
	private Database() {
		
	}
	
	public static void prepTablesForIndexing(){
		Connection con = null;
		Statement stmt = null;
		String[] prepFoundTables = {"file", "asset"};
		//String[] truncateTables = {"authority", "file", "asset"};
		try {
			con = Mysql.getConnection();
			stmt = con.createStatement();
			// All items must be declaired lost before found
			for (String table:prepFoundTables){
				stmt.addBatch("UPDATE "+table+" SET found=0");
			}
			
			/*for (String table:truncateTables){
				stmt.addBatch("TRUNCATE TABLE "+table);
			}*/
			stmt.executeBatch();
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) { stmt.close(); }
				if (con != null) {con.close(); }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void cleanUp() throws SQLException{
		Connection con = null;
		Statement stmt = null;
		Statement batch = null;
		
		ResultSet rs = null;

		String authQuery = null;
		String fixAuth = "UPDATE authority as au LEFT JOIN file as f ON au.file_id = f.id SET au.file_id = f.parent_id WHERE f.found = 0";
		String fixParents = "UPDATE file as f0 LEFT JOIN file as f1 ON f0.parent_id = f1.id SET f0.parent_id = f1.parent_id WHERE f1.found = 0";
		//String mQuery = "DELETE FROM file WHERE found=0";
		//String aQuery = "DELETE FROM asset WHERE found=0";
		
		int batch_count = 0;
		
		try {
			//Execute any queries left in the main indexer batch
			MysqlDAO.batchExecute();
			
			con = Mysql.getConnection();
			stmt = con.createStatement();
			batch = con.createStatement();
			rs = stmt.executeQuery("SELECT id, file_type_id FROM file WHERE found = 0");
			while (rs.next()){
				if (rs.getInt("file_type_id") == 5 || rs.getInt("file_type_id") == 7){
					authQuery = "DELETE FROM authority WHERE asset_id = "+rs.getLong("id");
				}
				else{
					authQuery = "DELETE FROM authority WHERE file_id = "+rs.getLong("id")+" AND asset_id < 0";
				}
				batch.addBatch(authQuery);
				batch_count++;
				if (batch_count >= 100){
					batch.executeBatch();
					batch_count = 0;
				}
			}
			if (batch_count > 0){
				batch.executeBatch();
			}
			
			MysqlDAO.delete(fixAuth);
			MysqlDAO.delete(fixParents);
			//Delete removed Metadata and Assets		
			//MysqlDAO.delete(mQuery);
			//MysqlDAO.delete(aQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { rs.close(); }
			if (stmt != null) { stmt.close(); }
			if (batch != null) { batch.close(); }
			if (con != null) { con.close(); }
		}
	}
	
	/*public static void cleanUp(){
		// Execute any queries left in the batch
		try {
			//Execute any queries left in the batch
			MysqlDAO.batchExecute();
			
			//Delete removed Metadata and Assets
			String mQuery = "DELETE FROM file WHERE found=0";
			String aQuery = "DELETE FROM asset WHERE found=0";
			
			MysqlDAO.delete(mQuery);
			MysqlDAO.delete(aQuery);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*String[] cleanLost = {"file", "asset"};
		MysqlDAO sqlClean = new MysqlDAO();
		for (String table:cleanLost){
			sqlClean.update("DELETE FROM "+table+" WHERE found=0");
		}
		sqlClean.closeConnection();*
	}*/
	
	
	public static LinkedList<String> metaExtensions() throws SQLException{
		if (METADATA_PARENT_EXTENSIONS == null){
			MetadataTypeDAO mTypes = new MetadataTypeDAO();
			METADATA_PARENT_EXTENSIONS = mTypes.listExtensions();
			mTypes = null;
		}
		return METADATA_PARENT_EXTENSIONS;
	}
	
	public static LinkedList<String> metaParentExtensions() throws SQLException{
		if (METADATA_EXTENSIONS == null){
			MetadataTypeDAO mTypes = new MetadataTypeDAO();
			METADATA_EXTENSIONS = mTypes.parentExtensions();
			mTypes = null;
		}
		return METADATA_EXTENSIONS;
	}
	
	public static List<String> assetExtensions() throws SQLException{
		if (ASSET_EXTENSIONS == null){
			AssetTypeDAO aTypes = new AssetTypeDAO();
			ASSET_EXTENSIONS = aTypes.listExtensions();
			aTypes = null;
		}
		return ASSET_EXTENSIONS;
	}
	
	public static Map<Integer, String> metaTypeIDMap() throws SQLException{
		if (METADATA_TYPE_ID_MAP == null){
			MetadataTypeDAO mTypes = new MetadataTypeDAO();
			METADATA_TYPE_ID_MAP = mTypes.typeIDMap();
			mTypes = null;
		}
		return METADATA_TYPE_ID_MAP;
	}
	
	public static Map<Integer, List<String>> assetTypeIDMap() throws SQLException{
		if (ASSET_TYPE_ID_MAP == null){
			AssetTypeDAO aTypes = new AssetTypeDAO();
			ASSET_TYPE_ID_MAP = aTypes.typeMap();
			aTypes = null;
		}
		return ASSET_TYPE_ID_MAP;
	}
		
	public static Map<String, Integer> statusTypeIDMap() throws SQLException {
		if (STATUS_TYPE_ID_MAP == null){
			StatusTypeDAO sTypes = new StatusTypeDAO();
			STATUS_TYPE_ID_MAP = sTypes.typeMap();
			sTypes = null;
		}
		return STATUS_TYPE_ID_MAP;
	}
	
	public static Map<String, Integer> authorityTypeIDMap() throws SQLException {
		if (AUTHORITY_TYPE_ID_MAP == null){
			AuthorityTypeDAO authTypes = new AuthorityTypeDAO();
			AUTHORITY_TYPE_ID_MAP = authTypes.typeIDMap();
			authTypes = null;
		}
		return AUTHORITY_TYPE_ID_MAP;
	}
	
	public static Map<String, Integer> getAuthorityTypes() throws SQLException{
		if (AUTHORITY_TYPES == null){
			AuthorityTypeDAO authTypes = new AuthorityTypeDAO();
			AUTHORITY_TYPES = authTypes.typeIDMap();
			authTypes = null;
		}
		return AUTHORITY_TYPES;
	}
	
	public static boolean isAuthority(String field) throws SQLException {
		if (AUTHORITY_TYPES == null){
			getAuthorityTypes();
		}
		return AUTHORITY_TYPES.containsKey(field);
	}
	
	public static int getAuthorityTypeID(String field) throws SQLException {
		if (AUTHORITY_TYPES == null){
			getAuthorityTypes();
		}
		return AUTHORITY_TYPES.get(field);
	}
}