package com.robotcontrol.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

	
	//主线程toast方法
	public static void showtomain(Context context,String content){
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
}
