package edu.ua.lib.acumen.database;

import java.sql.Connection;
import java.sql.DriverManager;

import edu.ua.lib.acumen.Config;



public class Mysql {
	
	protected static String url = "jdbc:mysql://"+Config.DB_HOST+":3306/"+Config.CURRENT_DB+"?useUnicode=yes&characterEncoding=UTF-8";
	
	private Mysql(){
		// empty - prevent construction
	}
	
	public static Connection getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(url, Config.DB_USER, Config.DB_PASS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}