package com.robotcontrol.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.robotcontrol.activity.R;
import com.robotcontrol.bean.Result;
import com.robotcontrol.broadcastReceiver.NetStateBroadcastReceiver;
import com.robotcontrol.broadcastReceiver.SocketErrorReceiver;
import com.robotcontrol.net.helper.Decoder;
import com.robotcontrol.net.helper.SocketConnect;
import com.robotcontrol.net.helper.SocketConnect.SocketListener;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.FileUtil;
import com.robotcontrol.utils.NetUtil;
import com.robotcontrol.utils.ThreadPool;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;

public class SocketService extends Service {

	Timer time = new Timer();

	private SocketListener listener;
	boolean the = true;
	private ChannelHandlerContext ctx;
	private MessageEvent e;
	private NetStateBroadcastReceiver netstate = new NetStateBroadcastReceiver();

	@Override
	public void onCreate() {

		BroadcastReceiverRegister.reg(getApplicationContext(),
				new String[] { Constants.Move_aciton }, move);
		BroadcastReceiverRegister.reg(getApplicationContext(),
				new String[] { Constants.Speech_action }, speak);
		BroadcastReceiverRegister.reg(getApplicationContext(), new String[] {
				Constants.Task_Remove, Constants.Task_Add,
				Constants.Task_Query, Constants.Task_Updata }, task);
		BroadcastReceiverRegister.reg(getApplicationContext(),
				new String[] { Constants.Stop }, stop);
		BroadcastReceiverRegister.reg(getApplicationContext(), new String[] {
				Constants.Photo_Delete, Constants.Photo_Query,
				Constants.Photo_Query_Name }, photo);
		BroadcastReceiverRegister.reg(this,
				new String[] { ConnectivityManager.CONNECTIVITY_ACTION },
				netstate);
		BroadcastReceiverRegister.reg(getApplicationContext(),
				new String[] { Constants.Socket_Error },
				new SocketErrorReceiver());
		BroadcastReceiverRegister.reg(getApplicationContext(),
				new String[] { Constants.Robot_Info_Update }, flush);
	}

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	BroadcastReceiver speak = speak = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {

			if (intent.getAction().equals(Constants.Speech_action)) {
				JSONObject params = new JSONObject();
				NetUtil.Scoket(params, 2, e, ctx);
			}
		}
	};
	BroadcastReceiver move = move = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Move_aciton)) {
				JSONObject params = new JSONObject();
				if (e != null) {
					NetUtil.Scoket(params, 1, e, ctx);
				}

			}
		}

	};;;
	BroadcastReceiver task = task = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			NetUtil.socket(e, ctx, intent);
		}
	};
	BroadcastReceiver stop = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals("stop")) {
				if (time != null && ctx != null && e != null) {

					time.cancel();
					Channels.close(ctx, e.getFuture());
					flag = 0;
					the = true;
				}
			}
		}
	};
	BroadcastReceiver photo = photo = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			NetUtil.photo_socket(e, ctx, intent);
		}
	};;

	BroadcastReceiver flush = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			NetUtil.robotinfoupdate(e, ctx, intent);
		}
	};

	private void exe(final ChannelHandlerContext ctx, final MessageEvent e) {

		if (flag == 0) {
			this.ctx = ctx;
			this.e = e;
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("cmd", "/robot/controll");
				jsonObject.put(
						"id",
						getSharedPreferences("userinfo", MODE_PRIVATE).getInt(
								"id", 0));
				Log.i("id", getSharedPreferences("userinfo", MODE_PRIVATE)
						.getInt("id", 0) + "");
				jsonObject.put("session",
						getSharedPreferences("userinfo", MODE_PRIVATE)
								.getString("session", null));
				jsonObject.put("rid",
						getSharedPreferences("Receipt", MODE_PRIVATE)
								.getString("robotid", null));
			} catch (JSONException ee) {
				ee.printStackTrace();
			}
			NetUtil.Scoket(jsonObject, 0, e, ctx);
		}

	}

	static int flag = 0;
	String name = null;

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		listener = new SocketListener() {

			@Override
			public void writeData(final ChannelHandlerContext ctx,
					final MessageEvent e) {
				Log.i("write", "WriteData");
				exe(ctx, e);
			}

			@Override
			public void receiveSuccess(final ChannelHandlerContext ctx,
					final MessageEvent e) {

				time.cancel();
				int ret = 0;
				Object o = e.getMessage();
				String callback = "";
				JSONObject Result = null;
				Intent in = new Intent("online");
				if (o instanceof JSONObject) {
					Log.i("Message", e.getMessage().toString());
					try {
						Result = (JSONObject) o;
						callback = Result.getString("cmd");
						if (callback.equals("/robot/callback")) {
							JSONObject queryresult = new JSONObject(
									Result.getString("command"));
							if (queryresult.getString("cmd").equals(
									"photo_names")) {
								ArrayList<String> names = new ArrayList<String>();
								String querynames = queryresult
										.getString("names");
								JSONArray array = null;
								if (querynames.equals("null")) {
									return;
								} else {
									array = new JSONArray(querynames);
								}
								for (int i = 0; i < array.length(); i++) {
									names.add(array.getJSONObject(i).getString(
											"name"));
								}
								sendBroadcast(new Intent(
										Constants.Photo_Reply_Names).putExtra(
										"result", names));
							} else if (queryresult.getString("cmd").equals("")) {
								sendBroadcast(new Intent(Constants.Photo_Query)
										.putExtra("result", ""));
							} else {
								sendBroadcast(new Intent("result")
										.putExtra("result",
												queryresult.getString("data")));
							}
							return;
						} else if (callback.equals("/robot/uncontroll")) {
							sendBroadcast(new Intent(Constants.Socket_Error));
							return;
						} else if (callback.equals("/robot/flush")) {
							sendBroadcast(new Intent("flush").putExtra(
									"result", "success"));
							return;
						}
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					try {
						ret = Result.getInt("ret");
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					switch (ret) {
					case 0:
						flag = 1;
						exe(ctx, e);
						time = new Timer();
						time.schedule(new TimerTask() {
							@Override
							public void run() {
								NetUtil.Scoket(new JSONObject(), 3, e, ctx);
							}
						}, new Date(), 9000);
						in.putExtra("ret", 0);
						the = false;
						break;
					case -1:
						in.putExtra("ret", -1);
						the = true;
						Channels.close(ctx, e.getFuture());
						break;
					case 1:
						in.putExtra("ret", 1);
						the = true;
						Channels.close(ctx, e.getFuture());
						break;
					case 2:
						in.putExtra("ret", 2);
						Channels.close(ctx, e.getFuture());
						the = true;
						break;
					case 3:
						in.putExtra("ret", 3);
						Channels.close(ctx, e.getFuture());
						the = true;
						break;
					case 4:
						in.putExtra("ret", 4);
						Channels.close(ctx, e.getFuture());
						the = true;
						break;
					case 5:
						in.putExtra("ret", 5);
						Channels.close(ctx, e.getFuture());
						the = true;
						break;
					case 6:
						in.putExtra("ret", 6);
						Channels.close(ctx, e.getFuture());
						the = true;
						break;
					case 7:
						in.putExtra("ret", 6);
						Channels.close(ctx, e.getFuture());
						the = true;
						break;
					default:
						the = true;
						Channels.close(ctx, e.getFuture());
						break;

					}
					sendBroadcast(in);
				} else if (o instanceof Decoder.Result2) {
					Log.i("Success", "收到图片");
					final Decoder.Result2 res = (Decoder.Result2) o;
					Result result = new Result();
					result.setDw(BitmapFactory.decodeByteArray(res.datas, 0,
							res.datas.length));
					try {
						name = new JSONObject(res.json.getString("command"))
								.getString("name");

						result.setName(name);
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
					ThreadPool.execute(new Runnable() {

						@Override
						public void run() {
							FileUtil.writefile(getApplication()
									.getExternalFilesDir(null)
									.getAbsolutePath(), res.datas, name);
						}
					});

					Intent intent = new Intent();
					intent.putExtra("result", result);
					intent.setAction(Constants.Photo_Reply);
					sendBroadcast(intent);
				}

			}

			@Override
			public void connectSuccess(final ChannelHandlerContext ctx,
					final ChannelStateEvent e) {
				NetUtil.Scoket(new JSONObject(), ctx);
			}

			@Override
			public void connectFail() {
				Log.i("Connect", "connectFail");
				connectsocket();
			}

			@Override
			public void connectClose(ChannelHandlerContext ctx,
					ChannelStateEvent e) {

			}
		};
		if (ctx != null && e != null) {
			Channels.close(ctx, e.getFuture());
		}
		connectsocket();
		return super.onStartCommand(intent, flags, startId);
	}

	private void connectsocket() {
		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SocketConnect.InitSocket(listener,
						getResources().getString(R.string.ip), Integer
								.parseInt(getResources().getString(
										R.string.port)));
			}
		});
	}

	@Override
	public void onDestroy() {
		if (netstate != null) {
			unregisterReceiver(netstate);
		}

		super.onDestroy();
	}
}
