package edu.ua.lib.acumen.database;

public final class AuthorityType {
	
	private final long id;
	private final String type;

	/**
	 * @param id
	 * @param type
	 */
	public AuthorityType(long id, String type) {
		this.id = id;
		this.type = type;
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
	
}