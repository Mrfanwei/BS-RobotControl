package com.robotcontrol.broadcastReceiver;

import com.easemob.chat.EMChatManager;

import com.robotcontrol.activity.ConnectActivity;
import com.robotcontrol.service.SocketService;
import com.robotcontrol.utils.ToastUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SocketErrorReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent Intent) {
		if (Intent.getAction().equals("socket_error")) {
			context.sendBroadcast(new Intent("stop"));
			EMChatManager.getInstance().endCall();
			ToastUtil.showtomain(context, "机器人离线");
			context.stopService(new Intent(context,SocketService.class));
			context.startActivity(new Intent(context, ConnectActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TASK));
		}

	}

}
