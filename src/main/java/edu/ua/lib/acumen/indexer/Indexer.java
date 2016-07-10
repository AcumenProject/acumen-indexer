package edu.ua.lib.acumen.indexer;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ua.lib.acumen.Config;
import edu.ua.lib.acumen.Statistics;
import edu.ua.lib.acumen.repo.Database;
import edu.ua.lib.acumen.repo.MetadataComparator;
import edu.ua.lib.acumen.repo.Regex;
import edu.ua.lib.acumen.util.Time;

public class Indexer {
	
	public Indexer(){
//		Config.CURRENT_DB = database;
		
		System.out.println("/////////////////////////");
		System.out.println("Indexing Starting: " + Time.start());
		System.out.println("/////////////////////////\n");
		// Prepare specific tables for Indexing
		//--> File and Asset tables should declare all items as lost (i.e., found=0)
		//------ This provides easier cleanup for items that have been removed from the repository
		//--> Authorities table is currently truncated upon indexing
		//------ The problem lies in that there is no direct link in the database between which authority
		//------ belongs to XML tag in the DOM tree. So every authority type would
		//------ have to be scanned twice the number of authorities of that type.
		//------ While proper testing is being done to find an efficient solution, a lazy truncation is done here.
		Database.prepTablesForIndexing();
		
		crawlRepository();
		
		try {
			Database.cleanUp();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("/////////////////////////");
		System.out.println("Indexing Completed: " + Time.stop());
		System.out.println("Time Elapsed: " + Time.elapsedInHours());
		System.out.println("/////////////////////////\n");
	}
	
	public static void crawlRepository(){
		TreeMap<String, Map<String, String>> files = new TreeMap<String, Map<String, String>>(new MetadataComparator());
		File repoDir = new File(Config.REPO_DIR_FULL);
		
		findIndexableFiles(repoDir, files);
		
		for (Map.Entry<String, Map<String, String>> indexFile : files.entrySet()){
			String type = indexFile.getValue().get("index_type");
			String repoPath = indexFile.getValue().get("repo_path");
			File file = new File(repoPath+File.separator+indexFile.getKey());
			
			if (("asset").equals(type)){
				AssetIndexer asset = null;
				try {
					Statistics.tallyAsset();
					asset = new AssetIndexer(file, indexFile.getValue());
					asset.index();
				} catch (Exception e) {
					e.printStackTrace();
					Statistics.assetFailed(file.getName());
				} finally {
					asset = null;
				}
			}
			else{
				MetadataIndexer metadata;
				try {
					metadata = new MetadataIndexer(file, indexFile.getValue());
					metadata.index();
					Statistics.tallyMetadata();
				} catch (Exception e) {
					Statistics.metaFailed(file.getName());
					e.printStackTrace();
				} finally{
					metadata = null;
				}
			}
		}
		Statistics.printTotals();
	}
	
	public static void findIndexableFiles(File dir, final TreeMap<String, Map<String, String>> indexTree){
		dir.list(new FilenameFilter(){
			public boolean accept(File dir, String name){
				File file = new File(dir,name);
				if (file.isDirectory()){
					findIndexableFiles(file, indexTree);
				}
				else{
					if (name.lastIndexOf(".") > 0){
						try {
							Map<String, String> info = Regex.splitAtExtension(file.getName());
							for (Map.Entry<Integer, List<String>> aType : Database.assetTypeIDMap().entrySet()){
								for (String assetExt : aType.getValue()){
									if (file.getName().endsWith(assetExt)){
										String repoLoc = name.replace(assetExt, "");
										Map<String, String> aInfo = new HashMap<String, String>();
										aInfo.put("type_id", String.valueOf(aType.getKey()));
										aInfo.put("index_type", "asset");
										aInfo.put("ext", info.get("ext"));
										aInfo.put("repo_loc", repoLoc);
										aInfo.put("repo_path", file.getParent());
										indexTree.put(file.getName(), aInfo);
										return true;
									}
								}
							}
							for (Map.Entry<Integer, String> mType : Database.metaTypeIDMap().entrySet()){
								if (mType.getValue().equals(info.get("ext"))){
									Map<String, String> mInfo = new HashMap<String, String>();
									mInfo.put("type_id", String.valueOf(mType.getKey()));
									mInfo.put("index_type", "metadata");
									mInfo.put("ext", info.get("ext"));
									mInfo.put("repo_loc", info.get("name"));
									mInfo.put("repo_path", file.getParent());
									indexTree.put(file.getName(), mInfo);
									return true;
								}
							}
							//System.out.println(file.getName()+" -- SKIPPED -- Not a file type registered in the database");
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else {
						System.out.println(file.getName()+" -- SKIPPED -- File has no extension");
					}
				}
				
				return false;
			}
		});
	}
}