package edu.ua.lib.acumen;

import java.io.File;

import org.apache.commons.cli.*;
import org.apache.commons.configuration2.ex.ConfigurationException;

import edu.ua.lib.acumen.Config;
import edu.ua.lib.acumen.indexer.Indexer;

public class Acumen {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException {
		
		CommandLineParser parser = new DefaultParser();
		
		Option db = Option.builder("db")
				.hasArg()
				.argName("database")
				.longOpt("database")
				.desc("Name of database for indexer to use")
				.required(false)
				.build();
		
		Option config = Option.builder("c")
				.hasArg()
				.argName("file")
				.longOpt("config")
				.desc("Configuration file")
				.required(false)
				.build();
		
		Option dir = Option.builder("d")
				.hasArg()
				.argName("directory")
				.longOpt("dir")
				.desc("Directory to index")
				.required(false)
				.build();
		
		Option rec = Option.builder("R")
				.longOpt("recursive")
				.desc("Index directory recursively")
				.required(false)
				.build();
		
		Options options = new Options();
		options.addOption(db);
		options.addOption(config);
		options.addOption(dir);
		options.addOption(rec);
		
		try {
			CommandLine line = parser.parse( options, args );
			String[] ar = line.getArgs();			
			
			if (ar.length > 0){
				if (ar[0].equals("setup")){
					setup(ar, line);
				}
			}
			else{
				index(ar, line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void index(String[] args, CommandLine line) throws ConfigurationException{
		String configFile = "config.xml";
		File f;
		
		if (line.hasOption("config")){
			configFile = line.getOptionValue("config");
		}
		
		f = new File(configFile);
		
		if (f.exists() && !f.isDirectory()){
			Config.set(configFile);
			System.out.println("repo dir from config: "+Config.REPO_DIR_FULL);
		}
		else{
			System.out.println(f.getName()+"  is not a file!");
		}		
		
		
		if (line.hasOption("db")){
			Config.CURRENT_DB = line.getOptionValue("db");
			System.out.println("database manually set to " + line.getOptionValue("db"));
		}
		if (line.hasOption("d")){
			Config.REPO_DIR_FULL = Config.BASE_DIR + line.getOptionValue("d");
			System.out.println("dir manually set to " + line.getOptionValue("dir"));
		}
		if (line.hasOption("R")){
			System.out.println("set to recursive");
		}
		
		new Indexer();
		
	}
	
	public static void setup(String[] args, CommandLine line){
		if (line.hasOption("config")){
			File f = new File(line.getOptionValue("config"));
			if (f.exists() && !f.isDirectory()){
				System.out.println(f.getName()+"  exists.");
			}
			else{
				System.out.println(f.getName()+"  is not a file!");
			}
		}
	}
	
}
