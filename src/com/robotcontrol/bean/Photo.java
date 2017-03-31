package com.robotcontrol.bean;

import android.graphics.Bitmap;

public class Photo {

	private String name;
	private Bitmap img;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap img) {
		this.img = img;
	}
}
