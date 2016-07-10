package edu.ua.lib.acumen.repo;

import java.io.File;
import java.io.FilenameFilter;

public class ThumbFilter implements FilenameFilter {
		private String type = null;

		public ThumbFilter(String type) {
			this.type = type;
		}

		public boolean accept(File dir, String name) {
			File f = new File(dir, name);
			if (type.equals("DIR")) {
				
				if (f.isDirectory() && !name.equals("Metadata") && !name.equals("Transcripts")) {
					System.out.println("filer: "+name);
					return true;
				}
			} else {
				if (name.endsWith(type)) {
					return true;
				}
			}
			return false;
		}
	}