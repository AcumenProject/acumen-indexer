package edu.ua.lib.acumen.repo;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MetadataComparator implements Comparator<String> {
	
	private static LinkedList<String> extensionOrder;
			
	private Map<String, String> f1 = new HashMap<String, String>();
	private Map<String, String> f2 = new HashMap<String, String>();
	
	public MetadataComparator(){
		if (extensionOrder == null){
			extensionOrder = buildExtOrderKeys();
		}
	}
	
	public int compare(String file1, String file2){

		f1 = Regex.splitAtExtension(file1);
		f2 = Regex.splitAtExtension(file2);
		
		int nameComp = f1.get("name").compareTo(f2.get("name"));
		if (nameComp == 0){
			int ext1 = extensionOrder.indexOf(f1.get("ext"));
			int ext2 = extensionOrder.indexOf(f2.get("ext"));
			
			if (ext1 == -1){
				ext1 = sink(ext1);
			}
			if (ext2 == -1){
				ext2 = sink(ext2);
			}
			
			if (ext1 < ext2){
				return -1;
			}
			else if (ext1 == ext2){
				return 0;
			}
			else{
				return 1;
			}
		}
		return nameComp;
	}
	
	private LinkedList<String> buildExtOrderKeys(){
		try {
			LinkedList<String> exts = new LinkedList<String>();
			exts.addAll(Database.metaExtensions());
			return exts;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private int sink(int i){
		return i*(-extensionOrder.size());
	}
	
}