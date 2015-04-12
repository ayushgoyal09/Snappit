package com.ayushgoyal.snappit.beans;

import android.graphics.Bitmap;

public class AlbumBean {
	private String id;
	private String name;
	private Bitmap albumCover;
	
	/**
	 * @param name
	 */
	public AlbumBean(String name) {
		super();
		this.name = name;
	}

	/**
	 * @param id
	 * @param name
	 * @param albumCover
	 */
	public AlbumBean(String id, String name, Bitmap albumCover) {
		super();
		this.id = id;
		this.name = name;
		this.albumCover = albumCover;
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

	public Bitmap getAlbumCover() {
		return albumCover;
	}

	public void setAlbumCover(Bitmap albumCover) {
		this.albumCover = albumCover;
	}
	
	
}
