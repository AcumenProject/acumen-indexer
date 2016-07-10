package edu.ua.lib.acumen.repo;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ua.lib.acumen.Config;

public final class Location {

	private static Map<String, String> METADATA_XSL_MAP = null;

	public static String ofParent(String currentLoc) throws SQLException {
		File dir = new File(repoFullPath(currentLoc)+Config.METADATA_DIR);
		for (String file:dir.list()){
			for (String ext:Database.metaParentExtensions()){
				if (file.endsWith(ext)){
					return currentLoc;
				}
			}
		}
		String oneLocBack = stepBackLoc(currentLoc);
		if (oneLocBack != null){
			return ofParent(oneLocBack);
		}
		return null;
	}
	
	public static boolean isParent(File file) throws SQLException{
		String ext = Regex.getExtension(file.getName());
		if (ext != null){
			return Database.metaParentExtensions().contains(ext);
		}
		return false;
	}
	
	public static String fullURL(String fileAbsolutePath) {
		return Config.BASE_URL + webSafePath(fileAbsolutePath);
	}

	public static String webSafePath(String path) {
		return path.replace(Config.BASE_DIR, "");
	}

	public static String stepBackLoc(String currentLoc) {
		Pattern parentLocPattern = Pattern.compile("[^.]*(?=_[a-zA-Z0-9]+)");
		Matcher m = parentLocPattern.matcher(currentLoc);

		if (m.find()) {
			return m.group();
		} else {
			return null;
		}
	}
		
	public static String repoFullPath(String repoLoc) {
		//System.out.println("repoFullPath: " + repoLoc);
		return Config.REPO_DIR_FULL + repoPath(repoLoc);
	}

	public static String repoPath(String repoLoc) {
		//System.out.println("repoPath: " + repoLoc.replace(Config.PARENT_DELIMITER, File.separator));
		return File.separator + repoLoc.replace(Config.PARENT_DELIMITER, File.separator);
	}
	
	public static File getFileAtLoc(String repoLoc) {
		return new File(repoFullPath(repoLoc));
	}

	public static String ofSolrXSL(String ext) {
		if (METADATA_XSL_MAP == null) {
			Map<String, String> xsl = new HashMap<String, String>();
			xsl.put(".mods.xml", "mods_to_solr.xsl");
			xsl.put(".mets.xml", "mets_to_solr.xsl");
			xsl.put(".ead.xml", "ead_to_solr.xsl");
			xsl.put(".xml", "collection_to_solr.xsl");
			xsl.put(".tags.xml", "tags_to_solr.xsl");
			METADATA_XSL_MAP = xsl;
		}
		String xslFile = METADATA_XSL_MAP.get(ext);
		if (xslFile != null){
			return Config.BASE_DIR + Config.ACUMEN_DIR + Config.XSL_DIR + File.separator + xslFile;
		}
		return null;
	}

	public static String ofAssetIcon(String ext) {
		if ((".mp3").equals(ext)) {
			return "/mv/images/audio_icon_128.png";
		}
		if ((".pdf").equals(ext)) {
			return "/dev/mv/images/pdf_icon_128.png";
		}
		return null;
	}
}