package edu.ua.lib.acumen.database;

public final class MetadataType {
	
	private final long id;
	private final int priority;
	private final String type;
	private final String extension;
	private final String summary_xsl;
	
	public MetadataType(long id, int priority, String type, String extension, String summary_xsl) {
		this.id = id;
		this.priority = priority;
		this.type = type;
		this.extension = extension;
		this.summary_xsl = summary_xsl;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return the summary_xsl
	 */
	public String getSummary_xsl() {
		return summary_xsl;
	}
	
}