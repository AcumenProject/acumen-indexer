package edu.ua.lib.acumen.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.ua.lib.acumen.repo.Regex;



public class Cleanup extends MysqlDAO {
		
	public void fixSocialAuthorities(){
		Statement stmt = null;
		Statement batchStmt = null;
		ResultSet rs = null;
		int batchSize = 0;
		try {
			stmt = con.createStatement();
			batchStmt = con.createStatement();
			rs = stmt.executeQuery("SELECT f0.file_name, a0.id FROM file AS f0 INNER JOIN authority AS a0 ON f0.id=a0.asset_id");
			while (rs.next()){
				String name = Regex.getFileName(rs.getString(0));
				long authID = rs.getLong(1);
				if (batchSize % 200 == 0){
					batchStmt.executeBatch();
					batchStmt.clearBatch();
					batchSize = 0;
				}
				batchStmt.addBatch("UPDATE authority SET asset_id = (SELECT id FROM asset WHERE name='"+name+"') WHERE id="+authID);
				batchSize++;
			}
			rs.close();
			batchStmt.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}