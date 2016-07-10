package edu.ua.lib.acumen.database;

public final class AssetType {
	
	private final long id;
	private final String type;
	private final String assetTails;
	private final String thumbTails;
	private final String parentFolder;
	
	public AssetType(long id, String type, String assetTails, String thumbTails, String parentFolder) {
		this.id = id;
		this.type = type;
		this.assetTails = assetTails;
		this.thumbTails = thumbTails;
		this.parentFolder = parentFolder;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the assetTails
	 */
	public String getAssetTails() {
		return assetTails;
	}

	/**
	 * @return the thumbTails
	 */
	public String getThumbTails() {
		return thumbTails;
	}

	/**
	 * @return the parentFolder
	 */
	public String getParentFolder() {
		return parentFolder;
	}
	
	
}