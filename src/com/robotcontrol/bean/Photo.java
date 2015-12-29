package com.robotcontrol.bean;

import android.graphics.Bitmap;

public class Photo {

	private int id;
	private Bitmap img;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap img) {
		this.img = img;
	}
}
