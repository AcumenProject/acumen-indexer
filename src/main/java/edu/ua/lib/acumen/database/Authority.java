package edu.ua.lib.acumen.database;

import java.util.LinkedList;


public final class Authority {
	
	private final long id;
	private final long fileID;
	private final long assetID;
	private final int authorityTypeID;
	
	private final String value;
	
	private final LinkedList<Object> values = new LinkedList<Object>();

	/**
	 * @param id
	 * @param fileID
	 * @param assetID
	 * @param authorityTypeID
	 * @param value
	 * @param type
	 */
	public Authority(long id, long fileID, long assetID, int authorityTypeID, String value) {
		values.add(id);
		this.id = id;
		values.add(fileID);
		this.fileID = fileID;
		values.add(assetID);
		this.assetID = assetID;
		values.add(authorityTypeID);
		this.authorityTypeID = authorityTypeID;
		values.add(value);
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the fileID
	 */
	public long getFileID() {
		return fileID;
	}

	/**
	 * @return the assetID
	 */
	public long getAssetID() {
		return assetID;
	}

	/**
	 * @return the authorityTypeID
	 */
	public int getAuthorityTypeID() {
		return authorityTypeID;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	public LinkedList<Object> listValues(){
		return values;
	}
}