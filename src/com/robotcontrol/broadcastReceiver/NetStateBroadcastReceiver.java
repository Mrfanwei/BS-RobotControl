package com.robotcontrol.broadcastReceiver;

import com.robotcontrol.activity.ConnectActivity;
import com.robotcontrol.huanxin.DemoHXSDKHelper;
import com.robotcontrol.service.SocketService;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.ToastUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2015/10/12 0012.
 */
public class NetStateBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);

		NetworkInfo mobile= connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi=connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (!mobile.isConnected()&&!wifi.isConnected()) {
			back(context);
			ToastUtil.showtomain(context, "无网络连接");
		} else if (!mobile.isAvailable()&&!wifi.isAvailable()) {
			back(context);
			ToastUtil.showtomain(context, "网络连接不可用");
		}

	}

	public void back(Context context) {
		if (DemoHXSDKHelper.getInstance().isLogined()) {
			context.sendBroadcast(new Intent(Constants.Stop));
			context.stopService(new Intent(context,SocketService.class));
			context.startActivity(new Intent(context, ConnectActivity.class)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(
							Intent.FLAG_ACTIVITY_CLEAR_TASK));
		}

	}
}
