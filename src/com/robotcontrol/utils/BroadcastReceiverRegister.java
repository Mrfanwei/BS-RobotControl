package com.robotcontrol.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class BroadcastReceiverRegister {

	public static void reg(Context context, String[] actions,
			BroadcastReceiver boradcast) {
		
		IntentFilter intentFilter = new IntentFilter();
		for (int i = 0; i < actions.length; i++) {
			intentFilter.addAction(actions[i]);
		}
		intentFilter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(boradcast, intentFilter);
	}
}
