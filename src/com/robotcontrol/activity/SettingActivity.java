package com.robotcontrol.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.robotcontrol.huanxin.DemoHXSDKHelper;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.ToastUtil;
import com.robotcontrol.utils.StartUtil;
import com.robotcontrol.utils.ThreadPool;
import com.robotcontrol.widget.SwitchButton;

public class SettingActivity extends BaseActivity implements
		View.OnClickListener {

	private TextView exit;	
	private TextView userid;
	private TextView about;
	private SwitchButton wifi;
	private Button back;
	private TextView edit;
	private EditText robotname;
	private TextView versionname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onStart() {
		BroadcastReceiverRegister.reg(this, new String[] { "flush" }, flush);
		super.onStart();
	}

	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton button, boolean flag) {
			getSharedPreferences("setting", MODE_PRIVATE).edit()
					.putBoolean("wificheck", flag).commit();
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_exit:
			sendBroadcast(new Intent(Constants.Stop));
			ThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					DemoHXSDKHelper.getInstance().logout(false,
							new EMCallBack() {

								@Override
								public void onSuccess() {
									SharedPreferences sharedPreferences = getSharedPreferences(
											"userinfo", Activity.MODE_PRIVATE);
									SharedPreferences.Editor editor = sharedPreferences
											.edit();
									editor.clear();
									editor.commit();
									startActivity(new Intent(
											SettingActivity.this,
											LoginActivity.class)
											.setFlags(
													Intent.FLAG_ACTIVITY_NEW_TASK)
											.addFlags(
													Intent.FLAG_ACTIVITY_CLEAR_TASK));

								}

								@Override
								public void onProgress(int arg0, String arg1) {

								}

								@Override
								public void onError(int arg0, String arg1) {

								}
							});
				}
			});
			break;
		case R.id.setting_back:
			this.onBackPressed();
			break;
		case R.id.about:
			StartUtil.startintent(this, AboutActivity.class, "no");
			break;
		case R.id.editname:
			if (edit.getText().toString().equals("编辑")) {
				edit.setText("完成");
				robotname.setEnabled(true);
				robotname.requestFocus();
			} else if (edit.getText().toString().equals("完成")) {
				String name = robotname.getText().toString().trim();
				if (name.equals("")) {
					ToastUtil.showtomain(this, "名字不能为空");
					return;
				}
				getSharedPreferences("robotname", MODE_PRIVATE).edit()
						.putString("name", name).commit();
				sendBroadcast(new Intent(Constants.Robot_Info_Update).putExtra(
						"name", name));
				edit.setText("编辑");
				robotname.setEnabled(false);
			}

			break;
		}
	}

	BroadcastReceiver flush = new BroadcastReceiver() {
		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getStringExtra("result").equals("success")) {
				ToastUtil.showtomain(SettingActivity.this, "修改成功");
			}
		}
	};

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	protected void onStop() {
		if (flush != null)
			unregisterReceiver(flush);
		super.onStop();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void initlayout(OnRefreshListener onRefreshListener) {
		setContentView(R.layout.activity_setting);
		exit = (TextView) findViewById(R.id.setting_exit);
		exit.setOnClickListener(this);
		versionname = (TextView) findViewById(R.id.versionname);
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(getPackageName(),
					0);
			versionname.setText(info.versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		about = (TextView) findViewById(R.id.about);
		about.setOnClickListener(this);
		wifi = (SwitchButton) findViewById(R.id.wifisetting);
		wifi.setOnCheckedChangeListener(changeListener);
		userid = (TextView) findViewById(R.id.userid);
		back = (Button) findViewById(R.id.setting_back);
		back.setOnClickListener(this);
		edit = (TextView) findViewById(R.id.editname);
		edit.setOnClickListener(this);
		robotname = (EditText) findViewById(R.id.robotname);
		String username = getSharedPreferences("userinfo", MODE_PRIVATE)
				.getString("phonenumber", null);
		if (!username.equals(null)) {
			userid.setText(username);
		}
		SharedPreferences sharedPreferences = getSharedPreferences("setting", 0);

		if (sharedPreferences != null) {
			wifi.setChecked(sharedPreferences.getBoolean("wificheck", true));
		} else {
			wifi.setChecked(true);
		}
		if (getIntent().getExtras().getString("flag").equals("main")) {
			(findViewById(R.id.robot_name)).setVisibility(View.VISIBLE);
			robotname.setText(getSharedPreferences("robotname", MODE_PRIVATE)
					.getString("name", null));
		} else {
			(findViewById(R.id.robot_name)).setVisibility(View.GONE);
		}
	}

}
