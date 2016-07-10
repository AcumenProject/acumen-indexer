package edu.ua.lib.acumen;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;


public final class Config {	
	
	public static XMLConfiguration config;	
	
//	public static final long MIN_FILE_SIZE = 400;
	
	public static String BASE_DIR;
	public static String REPO_DIR = "/content";
	public static String REPO_DIR_FULL;

	public static String ACUMEN_DIR = "/acumen";
//	public static final String ACUMEN_DIR_DEV = "/acumen/dev";
	public static String XSL_DIR = "/assets/xsl";
	
//	public static final String SOLR_DIR = "/home/acumen/indexer/content/";
	
	public static String METADATA_DIR = "Metadata";
	public static String TRANSCRIPTS_DIR = "Transcripts";
	
	public static String BASE_URL;
	
	public static String PARENT_DELIMITER = "_";
//	public static final String SOLR_EXT = ".solr.xml";	
	
//	public static final String DB_NAME = "acumen";
//	public static final String DB_NAME_STAGING = "acumen_staging";
	
	public static String DB_USER;
	public static String DB_PASS;

	public static String DB_HOST;
	public static String CURRENT_DB;
	public static Map<String, String> INSTALL_PATH_MAP = null;
	public static String INSTALL_PATH;
	
	public static void set(String configFile){
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<XMLConfiguration> builder =
			    new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
			    .configure(params.xml()
			        .setFileName(configFile));
		try
		{
		    XMLConfiguration config = builder.getConfiguration();
		    BASE_DIR = config.getString("basePath");
		    REPO_DIR = config.getString("acumen.global.contentDir");
		    REPO_DIR_FULL = BASE_DIR+REPO_DIR;
		    
		    BASE_URL = config.getString("acumen.global.url");
		    
		    ACUMEN_DIR = config.getString("acumen.live.dir");
		    XSL_DIR = config.getString("acumen.global.xslDir");
		    METADATA_DIR = config.getString("acumen.global.metadataDirName");
		    TRANSCRIPTS_DIR = config.getString("acumen.global.transcriptsDirName");
		    PARENT_DELIMITER = config.getString("acumen.global.fileNameParentDelim");
		    
		    CURRENT_DB = config.getString("acumen.dev.database.dbname");
		    DB_USER = config.getString("acumen.dev.database.user");
		    DB_PASS = config.getString("acumen.dev.database.pass");
		    DB_HOST = config.getString("acumen.dev.database.host");
		    
		}catch(ConfigurationException cex)
		{
			cex.printStackTrace();
		}
	}
	
	public static void INSTALL_PATH_MAP(){
		INSTALL_PATH_MAP = new HashMap<String, String>();
		INSTALL_PATH_MAP.put("acumen", "/");
		INSTALL_PATH_MAP.put("acumen_staging", "/dev");
		INSTALL_PATH_MAP.put("acumen_dev", "/deversoon");
	}
	public static String getINSTALL_PATH_FROM_MAP(String context){
		if (INSTALL_PATH_MAP == null){
			INSTALL_PATH_MAP();
		}
		return INSTALL_PATH_MAP.get(context);
	}
	public static String INSTALL_PATH(){
		return INSTALL_PATH;		
	}
	public static void setINSTALL_PATH(String context){
		INSTALL_PATH = getINSTALL_PATH_FROM_MAP(context);
	}
	
}