package com.robotcontrol.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageGetUtil {

	
	public static Bitmap getbitmap(String base64str){
		byte[] bs= Base64.decode(base64str, Base64.DEFAULT);
		Bitmap img= BitmapFactory.decodeByteArray(bs, 0, bs.length);
		return img;
	}
}
