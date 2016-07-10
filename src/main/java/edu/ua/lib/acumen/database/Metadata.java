package edu.ua.lib.acumen.database;

import java.util.LinkedList;

public final class Metadata {

	private final long id;
	private final long parentId;
	private final int fileTypeId;
	
	private final String title;
	private final String fileName;
	private final String filePath;
	
	private final long fileSize;
	private final long fileLastModified;
	
	private final int statusTypeId;
	private final int found;
	
	private final LinkedList<Object> values;
	
	public Metadata(long id, long parentId, int fileTypeId, String title, String fileName, String filePath, long fileSize, long fileLastModified,
			int statusTypeId, int found) {
		LinkedList<Object> vals = new LinkedList<Object>();
		vals.add(id);
		this.id = id;
		vals.add(parentId);
		this.parentId = parentId;
		vals.add(fileTypeId);
		this.fileTypeId = fileTypeId;
		vals.add(title);
		this.title = title;
		vals.add(fileName);
		this.fileName = fileName;
		vals.add(filePath);
		this.filePath = filePath;
		vals.add(fileSize);
		this.fileSize = fileSize;
		vals.add(fileLastModified);
		this.fileLastModified = fileLastModified;
		vals.add(statusTypeId);
		this.statusTypeId = statusTypeId;
		vals.add(found);
		this.found = found;
		this.values = vals;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the parentId
	 */
	public long getParentId() {
		return parentId;
	}

	/**
	 * @return the fileTypeId
	 */
	public int getFileTypeId() {
		return fileTypeId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * @return the fileLastModified
	 */
	public long getFileLastModified() {
		return fileLastModified;
	}

	/**
	 * @return the statusTypeId
	 */
	public int getStatusTypeId() {
		return statusTypeId;
	}

	/**
	 * @return the found
	 */
	public int getFound() {
		return found;
	}

	public LinkedList<Object> listValues(){
		return values;
	}
	
}