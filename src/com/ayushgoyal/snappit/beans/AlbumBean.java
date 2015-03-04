package com.ayushgoyal.snappit.beans;

public class AlbumBean {
	private String id;
	private String name;
	
	/**
	 * @param id
	 * @param name
	 */
	public AlbumBean(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
