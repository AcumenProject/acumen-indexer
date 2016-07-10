package edu.ua.lib.acumen.database;

import java.util.LinkedList;

public final class Asset {
	private final long id;
	private final int assetTypeId;
	
	private final String name;
	private final String origPath;
	private final String thumbPath;
	
	private final long fileId;
	private final long fileSize;
	private final long fileLastModified;
	private final int statusTypeId;
	
	private final int found;
	
	private final LinkedList<Object> values = new LinkedList<Object>();

	public Asset(long id, int asset_type_id, String name, String orig_path, String thumb_path, long file_id, long file_size, long file_last_modified, int status_type_id, int found) {
		values.add(id);
		this.id = id;
		values.add(asset_type_id);
		this.assetTypeId = asset_type_id;
		values.add(name);
		this.name = name;
		values.add(orig_path);
		this.origPath = orig_path;
		values.add(thumb_path);
		this.thumbPath = thumb_path;
		values.add(file_id);
		this.fileId = file_id;
		values.add(file_size);
		this.fileSize = file_size;
		values.add(file_last_modified);
		this.fileLastModified = file_last_modified;
		values.add(status_type_id);
		this.statusTypeId = status_type_id;
		values.add(found);
		this.found = found;
	}
	

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the assetTypeId
	 */
	public int getAssetTypeId() {
		return assetTypeId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the origPath
	 */
	public String getOrigPath() {
		return origPath;
	}

	/**
	 * @return the thumbPath
	 */
	public String getThumbPath() {
		return thumbPath;
	}

	/**
	 * @return the fileId
	 */
	public long getFileId() {
		return fileId;
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