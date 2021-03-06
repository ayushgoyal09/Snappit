package com.ayushgoyal.snappit.album;

import android.graphics.Bitmap;

public class ImageItem implements Comparable<ImageItem>{
	private Bitmap image;
	private String title;

	public ImageItem(Bitmap image, String title) {
		super();
		this.image = image;
		this.title = title;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int compareTo(ImageItem another) {
		return this.title.compareTo(another.title);
	}
	
	
}
