package com.robotcontrol.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class HandlerUtil {

	public static void sendmsg(Handler handler,String result,int what){
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("result", result);
		msg.what=what;
		msg.setData(params);
		handler.sendMessage(msg);
	}
}
