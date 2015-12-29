package com.robotcontrol.activity;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;

import android.util.Log;
import android.util.TypedValue;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import android.widget.AdapterView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;

import com.robotcontrol.adapter.RobotAdapter;
import com.robotcontrol.bean.Robot;
import com.robotcontrol.service.SocketService;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.HandlerUtil;
import com.robotcontrol.utils.NetUtil;
import com.robotcontrol.utils.NetUtil.callback;
import com.robotcontrol.utils.StartUtil;
import com.robotcontrol.utils.ThreadPool;
import com.robotcontrol.utils.ToastUtil;
import com.robotcontrol.widget.RobotDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class ConnectActivity extends Activity implements View.OnClickListener {

	private SwipeMenuListView robots_bind;
	private TextView findrobot;
	private SharedPreferences sharedPreferences;
	private List<Robot> list_robots;
	private SwipeRefreshLayout refreshableView;
	private TextView setting;
	private long time;
	private int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_connect);
		robots_bind = (SwipeMenuListView) findViewById(R.id.robotlist_bind);
		refreshableView = (SwipeRefreshLayout) findViewById(R.id.refresh_bind);
		robots_bind.setOnItemClickListener(itemClickListener);
		findrobot = (TextView) findViewById(R.id.findrobot);
		findrobot.setOnClickListener(this);
		setting = (TextView) findViewById(R.id.setting_into);
		setting.setOnClickListener(this);
		sharedPreferences = getSharedPreferences("userinfo", 0);
		list_robots = new ArrayList<Robot>();
		refreshableView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				if (System.currentTimeMillis() - time < 1000) {
					ToastUtil.showtomain(ConnectActivity.this,
							"不要刷新得这么频繁嘛！(ˉ▽ˉ；)...");
					refreshableView.setRefreshing(false);
				} else {
					getrobotinfo();
				}
			}
		});
		getrobotinfo();
		flag = 0;
	}

	// private void SendMSearchMessage() {
	// SSDPSearchMsg searchProduct = new SSDPSearchMsg(SSDPConstants.Root);
	// SSDPSocket sock = null;
	// try {
	// sock = new SSDPSocket();
	// for (int i = 0; i < 2; i++) {
	// sock.send(searchProduct.toString());
	// Log.i("-------------", "要发送的消息为：" + searchProduct.toString());
	// }
	// while (true) {
	// DatagramPacket dp = sock.receive(); // Here, I only receive the
	// // same packets I initially
	// // sent above
	// String c = new String(dp.getData()).trim();
	// String ip = new String(dp.getAddress().toString()).trim();
	// Log.i("------------", "接收到的消息为：" + c + "来源IP地址：" + ip);
	// }
	// } catch (IOException e) {
	// Log.e("M-SEARCH", e.getMessage());
	// }
	// }

	@Override
	protected void onResume() {
		if (flag == 0) {
			flag = 1;
		} else {
			getrobotinfo();
		}
		super.onResume();
	}

	public void getrobotinfo() {

		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", sharedPreferences.getInt("id", 0) + "");
				params.put("session",
						sharedPreferences.getString("session", null));
				list_robots.clear();
				if (list_robots.size() == 0) {
					try {
						NetUtil.getinstance().http(
								getString(R.string.url) + "/robot/info",
								params, new callback() {

									@Override
									public void success(JSONObject json) {
										try {
											String jsonstr = json
													.getString("Robots");
											JSONArray jsonarray = new JSONArray(
													jsonstr);
											for (int i = 0; i < jsonarray
													.length(); i++) {
												Robot robot = new Robot();
												JSONObject jsonobject = jsonarray
														.getJSONObject(i);
												robot.setId(jsonobject
														.getString("id"));
												robot.setAddress(jsonobject
														.getString("addr"));
												robot.setRid(jsonobject
														.getInt("rid"));
												robot.setRname(jsonobject
														.getString("rname"));
												robot.setOnline(jsonobject
														.getBoolean("online"));
												robot.setController(jsonobject
														.getInt("controller"));
												robot.setRobot_serial(jsonobject
														.getString("serial"));
												robot.setAir(Robot.air.bind);
												list_robots.add(robot);
											}
											time = System.currentTimeMillis();
											handler.sendEmptyMessage(2);
										} catch (JSONException e1) {

											e1.printStackTrace();
										}
									}

									@Override
									public void error(String errorresult) {
										HandlerUtil.sendmsg(handler,
												errorresult, 5);
									}
								}, ConnectActivity.this);
					} catch (SocketTimeoutException e) {
						if (refreshableView.isRefreshing()) {
							refreshableView.setRefreshing(false);
						}
						HandlerUtil.sendmsg(handler, "请求超时", 5);
						e.printStackTrace();
					}

				}

			}
		});
	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				ToastUtil.showtomain(ConnectActivity.this, "机器人不存在");
				break;
			case 2:
				refreshableView.setRefreshing(false);
				setadapter();
				break;
			case 3:
				robot.notifyDataSetChanged();
				refreshableView.setRefreshing(false);
				break;
			case 4:
				ToastUtil.showtomain(ConnectActivity.this, "连接失败");
				break;
			case 5:
				if (refreshableView.isRefreshing()) {
					refreshableView.setRefreshing(false);
				}
				if (unbind != null) {
					list_robots.add(unbind);
					robot.notifyDataSetChanged();
				}
				ToastUtil.showtomain(ConnectActivity.this, msg.getData()
						.getString("result"));
				break;
			case 6:
				ToastUtil.showtomain(ConnectActivity.this, "该机器人不在线！");
				break;
			case 7:
				backlogin("用户登录过期，请重新登录！");
				break;
			case 8:
				backlogin("用户登录错误，请重新登录！");
				break;
			case 9:
				ToastUtil.showtomain(ConnectActivity.this, "机器人已被控制");
				break;
			case 10:
				ToastUtil.showtomain(ConnectActivity.this, "机器人已被绑定！");
				break;
			case 11:
				ToastUtil.showtomain(ConnectActivity.this, "已超过绑定数量！");
				break;
			case 12:
				ToastUtil.showtomain(ConnectActivity.this, "绑定参数不正确！");
				break;
			default:
				break;
			}

		};
	};

	private void backlogin(String content) {
		ToastUtil.showtomain(ConnectActivity.this, content);
		getSharedPreferences("userinfo", MODE_PRIVATE).edit().clear().commit();
		StartUtil.startintent(ConnectActivity.this, LoginActivity.class,
				"finish");
	}

	RobotDialog alert = null;

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.findrobot:
			StartUtil.startintentforresult(this, BindRobotActivity.class,
					Constants.bindrobot_RequestCode);
			break;
		case R.id.setting_into:
			Bundle params = new Bundle();
			params.putString("flag", "connect");
			StartUtil.startintent(this, SettingActivity.class, "no", params);
			break;
		default:
			break;
		}

	}

	ProgressDialog pro = null;
	Timer timer = null;
	private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, final View view,
				int position, long id) {
			if (!list_robots.get(position).isOnline()) {
				ToastUtil.showtomain(ConnectActivity.this, "机器人处于离线状态");
				return;
			} else if (list_robots.get(position).getController() != 0) {
				ToastUtil.showtomain(ConnectActivity.this, "机器人已被控制");
				return;
			}
			String username = list_robots.get(position).getId();
			Intent intent1 = new Intent(ConnectActivity.this,
					SocketService.class);
			getSharedPreferences("Receipt", MODE_PRIVATE)
					.edit()
					.putString("username", username)
					.putString("robotid",
							list_robots.get(position).getRid() + "").commit();
			getSharedPreferences("robotname", MODE_PRIVATE).edit()
					.putString("name", list_robots.get(position).getRname())
					.commit();
			startService(intent1);
			BroadcastReceiverRegister.reg(ConnectActivity.this,
					new String[] { "online" }, bro);
			pro = new ProgressDialog(ConnectActivity.this);
			pro.setMessage("正在连接机器人......");
			pro.show();
		}
	};
	// 接收soket返回状态
	BroadcastReceiver bro = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals("online")) {
				pro.dismiss();
				switch (intent.getIntExtra("ret", 0)) {
				case 0:
					StartUtil.startintent(ConnectActivity.this,
							PowerListActivity.class, "no");
					unregisterReceiver(bro);
					break;
				case -1:
					handler.sendEmptyMessage(4);
					break;
				case -5:
					break;
				case 1:
					handler.sendEmptyMessage(7);
					break;
				case 2:
					handler.sendEmptyMessage(8);
					break;
				case 3:
					handler.sendEmptyMessage(8);
					break;
				case 4:
					break;
				case 5:
					handler.sendEmptyMessage(6);
					break;
				case 6:
					handler.sendEmptyMessage(9);
					break;

				default:
					break;
				}

			}
		}
	};
	private RobotAdapter robot = null;

	public List<Robot> sort(List<Robot> list) {
		List<Robot> rs = new ArrayList<Robot>();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getAir().equals(Robot.air.nearby)) {
				rs.add(list.get(i));
			}

		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getAir().equals(Robot.air.bind)
					&& list.get(i).isOnline()) {
				rs.add(list.get(i));
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getAir().equals(Robot.air.bind)
					&& !list.get(i).isOnline()) {
				rs.add(list.get(i));
			}
		}
		return rs;

	}

	Robot unbind = null;

	// 适配listview
	private void setadapter() {

		list_robots = sort(list_robots);
		robot = new RobotAdapter(ConnectActivity.this, list_robots);
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		robots_bind.setMenuCreator(creator, refreshableView);
		robots_bind.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onMenuItemClick(final int position, SwipeMenu menu,
					int index) {
				View v = robots_bind.getChildAt(position);
				v.setBackgroundColor(Color.BLACK);
				TranslateAnimation tran = new TranslateAnimation(-(menu
						.getMenuItem(0).getWidth()), -(v.getLeft()
						+ v.getWidth() + menu.getMenuItem(0).getWidth()), 0, 0);
				AlphaAnimation al = new AlphaAnimation(1, 0);
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(tran);
				set.addAnimation(al);
				set.setDuration(500);
				set.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						unbind = list_robots.get(position);

						// 解除绑定
						final Map<String, String> param = new HashMap<String, String>();
						param.put("id",
								getSharedPreferences("userinfo", MODE_PRIVATE)
										.getInt("id", 0) + "");
						param.put("session",
								getSharedPreferences("userinfo", MODE_PRIVATE)
										.getString("session", null));
						param.put("robot_id", list_robots.get(position).getId()
								+ "");
						param.put("robot_serial", list_robots.get(position)
								.getRobot_serial());
						list_robots.remove(position);
						robot.notifyDataSetChanged();
						ThreadPool.execute(new Runnable() {

							@Override
							public void run() {
								try {
									NetUtil.getinstance().http(
											getString(R.string.url)
													+ "/robot/unbind", param,
											new callback() {

												@Override
												public void success(
														JSONObject json) {

													Log.i("Success",
															json.toString());
													list_robots.clear();
													getrobotinfo();
													refreshableView
															.setRefreshing(false);
												}

												@Override
												public void error(
														String errorresult) {
													HandlerUtil.sendmsg(
															handler,
															errorresult, 5);
													Log.i("Error", "Error");
												}
											}, ConnectActivity.this);
								} catch (SocketTimeoutException e) {
									HandlerUtil.sendmsg(handler, "请求超时", 5);
									e.printStackTrace();

								}
							}
						});

					}
				});
				v.startAnimation(set);
			}
		});

		robots_bind.setAdapter(robot);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		stopService(new Intent(this, SocketService.class));
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
