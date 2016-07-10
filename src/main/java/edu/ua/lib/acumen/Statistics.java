package edu.ua.lib.acumen;

import java.util.ArrayList;

public class Statistics {
	
	private static int assetTotal = 0;
	private static int metaTotal = 0;
	private static int assetsAdded = 0;
	private static int metaAdded = 0;
	private static ArrayList<String> failedAssets = new ArrayList<String>();
	private static ArrayList<String> failedMetadata = new ArrayList<String>();
	
	public static void tallyAsset(){
		assetTotal++;
	}
	
	public static void tallyMetadata(){
		metaTotal++;
	}
	
	public static void addedAsset(){
		assetsAdded++;
	}
	
	public static void addedMetadata(){
		metaAdded++;
	}
	
	public static void assetFailed(String asset){
		failedAssets.add(asset);
	}
	
	public static void metaFailed(String metadata){
		failedMetadata.add(metadata);
	}
	
	public static void printTotals(){
		System.out.print("\n");
		System.out.println("----------------------------------------------");
		System.out.println("|          Indexer Statistics                 |");
		System.out.println("----------------------------------------------");
		System.out.println("Metadata Total:         "+metaTotal);
		System.out.println("Asset Total:            "+assetTotal);
		System.out.println(" ");
		System.out.println("Metadata Added:         "+metaAdded);
		System.out.println("Assets Added:           "+assetsAdded);
		System.out.print("\n");
		if (failedMetadata.size() > 0){
			System.out.println("Failed Metadata:");
			for (String metadata : failedMetadata){
				System.out.println(" --- "+metadata);
			}
		}
		if (failedAssets.size() > 0){
			System.out.println("Failed Assets:");
			for (String asset : failedAssets){
				System.out.println(" --- "+asset);
			}
		}
		System.out.println("----------------------------------------------");
		System.out.print("\n");
	}
}
