package com.robotcontrol.activity;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.robotcontrol.huanxin.CommonUtils;
import com.robotcontrol.huanxin.Constant;
import com.robotcontrol.huanxin.DemoApplication;
import com.robotcontrol.huanxin.DemoHXSDKHelper;
import com.robotcontrol.huanxin.HXSDKHelper;
import com.robotcontrol.huanxin.User;
import com.robotcontrol.huanxin.UserDao;
import com.robotcontrol.utils.HandlerUtil;
import com.robotcontrol.utils.NetUtil;
import com.robotcontrol.utils.NetUtil.callback;
import com.robotcontrol.utils.SmsContent;
import com.robotcontrol.utils.StartUtil;
import com.robotcontrol.utils.ThreadPool;
import com.robotcontrol.utils.ToastUtil;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("NewApi")
public class LoginActivity extends Activity implements OnClickListener {

	private EditText edit_phonenum;
	private EditText edit_vaildcode;
	private Button login;
	private Button getvaild;
	private SmsContent smsContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		SharedPreferences sharedPreferences = getSharedPreferences("userinfo",
				MODE_PRIVATE);
		int id = sharedPreferences.getInt("id", 0);
		// 如果本地存在记录则自动跳转
		if (id != 0 && DemoHXSDKHelper.getInstance().isLogined()) {
			StartUtil.startintent(this, ConnectActivity.class, "finish");
			return;
		}
		edit_phonenum = (EditText) findViewById(R.id.edit_phonenumber);

		edit_vaildcode = (EditText) findViewById(R.id.edit_vaildcode);
		login = (Button) findViewById(R.id.btn_login);
		login.setOnClickListener(this);
		getvaild = (Button) findViewById(R.id.getvaild);
		getvaild.setOnClickListener(this);
		// 开启短信的contentresolver
		smsContent = new SmsContent(LoginActivity.this, new Handler(),
				edit_vaildcode);
		this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), true, smsContent);
	}

	ProgressDialog progress = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			// 手机号验证
			if (edit_phonenum.getText() == null
					|| edit_phonenum.getText().toString().equals("")) {
				ToastUtil.showtomain(LoginActivity.this, "请输入手机号");
				return;
			}
			if (edit_vaildcode.getText() == null
					|| edit_vaildcode.getText().toString().equals("")) {
				ToastUtil.showtomain(LoginActivity.this, "请输入验证码");
				return;
			}
			progress = new ProgressDialog(this);
			progress.setMessage("正在登录.......");
			progress.show();
			ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					if (TextUtils.isEmpty(edit_vaildcode.getText())
							|| TextUtils.isEmpty(edit_vaildcode.getText())) {
					}
					progress.show();

					// 设置参数
					Map<String, String> parmas = new HashMap<String, String>();
					parmas.put("phone", edit_phonenum.getText().toString());
					parmas.put("verify", edit_vaildcode.getText().toString());
					// 开始请求
					try {
						NetUtil.getinstance().http(
								getString(R.string.url) + "/sms/login/verify",
								parmas, new callback() {

									@Override
									public void success(JSONObject json) {
										// TODO Auto-generated method stub

										Log.i("json", json.toString());
										try {
											if (json.getInt("ret") == 0) {

												into(json);
												huanxinlogin(edit_phonenum
														.getText().toString(),
														edit_phonenum.getText()
																.toString());
												if (timer != null) {
													timer.cancel();
												}
											} else if (json.getInt("ret") == 1) {
												progress.dismiss();
												HandlerUtil.sendmsg(
														handlercode, "验证码已过期",
														2);
											} else if (json.getInt("ret") == 2) {
												progress.dismiss();
												HandlerUtil
														.sendmsg(handlercode,
																"验证码错误", 2);
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}

									}

									@Override
									public void error(String errorresult) {
										// TODO Auto-generated method stub
										HandlerUtil.sendmsg(handlercode,
												errorresult, 2);
									}
								}, LoginActivity.this);
					} catch (SocketTimeoutException e) {
						HandlerUtil.sendmsg(handlercode, "请求超时！", 2);
						e.printStackTrace();
					}
				}
			});
			break;
		case R.id.getvaild:
			// 账号过滤
			if (TextUtils.isEmpty(edit_phonenum.getText())
					|| edit_phonenum.getText().equals("")) {
				ToastUtil.showtomain(this, "请输入手机号");
				return;
			}
			if (edit_phonenum.getText().toString().length() < 11) {
				ToastUtil.showtomain(this, "手机号长度不对");
				return;
			}
			if (!edit_phonenum.getText().toString().substring(0, 1).equals("1")) {
				ToastUtil.showtomain(this, "请输入正确的手机号");
				return;
			}

			ThreadPool.execute(new Runnable() {
				@Override
				public void run() {
					Map<String, String> params = new HashMap<String, String>();
					params.put("phone", edit_phonenum.getText().toString());
					try {
						NetUtil.getinstance().http(
								getString(R.string.url) + "/sms/send/verify",
								params, new callback() {

									// 请求成功
									@Override
									public void success(JSONObject json) {
										// TODO Auto-generated method stub
										try {
											int ret = json.getInt("ret");
											if (ret == 0) {
												Log.i("verify", "验证码请求成功");
												handlercode.sendEmptyMessage(3);
											} else if (ret == 1) {
												handlercode.sendEmptyMessage(5);
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}

									// 请求失败回掉
									@Override
									public void error(String errorresult) {
										// TODO Auto-generated method stub
										HandlerUtil.sendmsg(handlercode,
												errorresult, 2);
									}
								}, LoginActivity.this);
					} catch (SocketTimeoutException e) {
						HandlerUtil.sendmsg(handlercode, "请求超时", 2);
						e.printStackTrace();
					}
				}
			});

			break;
		}
	}

	Timer timer = null;

	private void huanxinlogin(final String currentUsername,
			final String currentPassword) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, R.string.User_name_cannot_be_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, R.string.Password_cannot_be_empty,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// 调用sdk登陆方法登陆聊天服务器
		if (getSharedPreferences("huanxin", MODE_PRIVATE).getString("username",
				null) == null) {
			try {
				EMChatManager.getInstance().createAccountOnServer(
						currentUsername, currentUsername);
			} catch (EaseMobException e1) {
				e1.printStackTrace();

			}
			getSharedPreferences("huanxin", MODE_PRIVATE).edit()
					.putString("username", currentUsername)
					.putString("password", currentPassword).commit();
		}

		EMChatManager.getInstance().login(
				getSharedPreferences("huanxin", MODE_PRIVATE).getString(
						"username", null),
				getSharedPreferences("huanxin", MODE_PRIVATE).getString(
						"password", null), new EMCallBack() {

					@Override
					public void onSuccess() {

						// 登陆成功，保存用户名密码
						DemoApplication.getInstance().setUserName(
								currentUsername);
						DemoApplication.getInstance().setPassword(
								currentPassword);

						try {
							// ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
							// ** manually load all local groups and
							EMGroupManager.getInstance().loadAllGroups();
							EMChatManager.getInstance().loadAllConversations();
							// 处理好友和群组
							initializeContacts();
						} catch (Exception e) {
							e.printStackTrace();
							// 取好友或者群聊失败，不让进入主页面
							runOnUiThread(new Runnable() {
								public void run() {
									DemoHXSDKHelper.getInstance().logout(true,
											null);
									Toast.makeText(getApplicationContext(),
											R.string.login_failure_failed,
											Toast.LENGTH_SHORT).show();
								}
							});
							return;
						}
						// 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
						boolean updatenick = EMChatManager.getInstance()
								.updateCurrentUserNick(
										DemoApplication.currentUserNick.trim());
						if (!updatenick) {
							Log.e("LoginActivity",
									"update current user nick fail");
						}
						progress.dismiss();
						if (timer != null) {
							timer.cancel();
						}

						// 进入主页面
						handlercode.sendEmptyMessage(4);
					}

					@Override
					public void onProgress(int progress, String status) {
					}

					@Override
					public void onError(final int code, final String message) {
						runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(
										getApplicationContext(),
										getString(R.string.Login_failed)
												+ message, Toast.LENGTH_SHORT)
										.show();
							}
						});
					}
				});
	}

	private void initializeContacts() {
		Map<String, User> userlist = new HashMap<String, User>();
		// 添加user"申请与通知"
		User newFriends = new User();
		newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
		String strChat = getResources().getString(
				R.string.Application_and_notify);
		newFriends.setNick(strChat);

		userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
		// 添加"群聊"
		User groupUser = new User();
		String strGroup = getResources().getString(R.string.group_chat);
		groupUser.setUsername(Constant.GROUP_USERNAME);
		groupUser.setNick(strGroup);
		groupUser.setHeader("");
		userlist.put(Constant.GROUP_USERNAME, groupUser);

		// 添加"Robot"
		User robotUser = new User();
		String strRobot = getResources().getString(R.string.robot_chat);
		robotUser.setUsername(Constant.CHAT_ROBOT);
		robotUser.setNick(strRobot);
		robotUser.setHeader("");
		userlist.put(Constant.CHAT_ROBOT, robotUser);

		// 存入内存
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
		// 存入db
		UserDao dao = new UserDao(LoginActivity.this);
		List<User> users = new ArrayList<User>(userlist.values());
		dao.saveContactList(users);
	}

	// 用户信息存入db
	public void into(JSONObject json) {
		try {
			getSharedPreferences("userinfo", MODE_PRIVATE)
					.edit()
					.putInt("id", json.getInt("id"))
					.putString("session", json.getString("session"))
					.putString("phonenumber",
							edit_phonenum.getText().toString().trim()).commit();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	int flag = 0;
	// 设置按钮内容
	Handler handlercode = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == 0) {
				getvaild.setText("剩余" + index + "秒");
			} else if (msg.what == 1) {
				getvaild.getBackground().setAlpha(255);
				getvaild.setText("重新获取");
				getvaild.setEnabled(true);
				index = 60;
			} else if (msg.what == 2) {
				ToastUtil.showtomain(LoginActivity.this, msg.getData()
						.getString("result"));
			} else if (msg.what == 3) {
				getvaild.setEnabled(false);
				getvaild.getBackground().setAlpha(80);
				edit_vaildcode.requestFocus();
				// 开启timer任务
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (index == 0) {
							handlercode.sendEmptyMessage(1);
							if (timer != null) {
								timer.cancel();
							}

							return;
						}
						Log.i("Timer", index + "");
						handlercode.sendEmptyMessage(0);
						index--;
					}
				}, new Date(), 1000);

			} else if (msg.what == 4) {
				getvaild.setEnabled(true);
				index = 60;
				getvaild.getBackground().setAlpha(255);
				StartUtil.startintent(LoginActivity.this,
						ConnectActivity.class, "finish");
			} else if (msg.what == 5) {
				ToastUtil.showtomain(LoginActivity.this, "太频繁啦！请稍后再试试！");
			}
		};
	};
	// 倒计时时间
	int index = 60;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// 页面销毁，销毁短信获取对象
		if (smsContent != null) {
			this.getContentResolver().unregisterContentObserver(smsContent);
		}
		super.onDestroy();
	}
}
