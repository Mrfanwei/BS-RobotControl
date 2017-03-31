package com.robotcontrol.activity;

import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.Type;
import com.easemob.exceptions.EaseMobException;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.StartUtil;

import android.os.Bundle;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class PowerListActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout video_chat;
	private RelativeLayout video_monitor;
	//private RelativeLayout power_chat;
	private RelativeLayout power_task;
	//private RelativeLayout power_address_book;
	private RelativeLayout power_photo;
	private RelativeLayout power_setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_power_list);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {

		super.onStart();
	}

	@Override
	public void initlayout(OnRefreshListener onRefreshListener) {
		video_chat = (RelativeLayout) findViewById(R.id.video_chat);
		video_chat.setOnClickListener(this);
		video_chat.setOnTouchListener(ontouch);
		video_monitor = (RelativeLayout) findViewById(R.id.video_monitor);
		video_monitor.setOnClickListener(this);
		video_monitor.setOnTouchListener(ontouch);
		power_task = (RelativeLayout) findViewById(R.id.power_task);
		power_task.setOnClickListener(this);
		power_task.setOnTouchListener(ontouch);
		 power_photo = (RelativeLayout) findViewById(R.id.power_photo);
		 power_photo.setOnTouchListener(ontouch);
		 power_photo.setOnClickListener(this);
		power_setting = (RelativeLayout) findViewById(R.id.power_setting);
		power_setting.setOnClickListener(this);
		power_setting.setOnTouchListener(ontouch);
	}

	long now = 0;
	long secc = 0;
	boolean flag = true;

	public void downanimaction(View view) {
		view.setPivotX(0);
		view.setPivotY(0);
		view.invalidate();
		ObjectAnimator.ofFloat(view, "rotationX", 0.0f, -30.0f)
				.setDuration(500).start();
	}

	private OnTouchListener ontouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				v.getBackground().setAlpha(100);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				v.getBackground().setAlpha(255);
			}
			return false;
		}
	};
	int width;
	int height;

	public void sendmsg(String mode, String touser) {
		EMMessage msg = EMMessage.createSendMessage(Type.CMD);
		msg.setReceipt(touser);
		msg.setAttribute("mode", mode);
		CmdMessageBody cmd = new CmdMessageBody(Constants.Video_Mode);
		msg.addBody(cmd);
		try {
			EMChatManager.getInstance().sendMessage(msg);
		} catch (EaseMobException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		Bundle params = new Bundle();

		switch (v.getId()) {
		case R.id.video_chat:
			tovideo("chat");
			break;
		case R.id.video_monitor:
			tovideo("control");
			break;
		 case R.id.power_photo:
		 StartUtil.startintent(this, PhotoActivity.class, "no");
		 break;
		case R.id.power_setting:
			params.putString("flag", "main");
			StartUtil.startintent(this, SettingActivity.class, "no", params);
			break;
		case R.id.power_task:
			StartUtil.startintent(this, TaskRemindActivity.class, "no");
			break;
		default:
			break;
		}
	}

	private void tovideo(String mode) {

		sendmsg(mode,
				getSharedPreferences("Receipt", MODE_PRIVATE).getString(
						"username", null));
		Bundle params = new Bundle();
		params.putString("mode", mode);
		StartUtil.startintent(this, ControlActivity.class, "no", params);
	}

	@Override
	public void onBackPressed() {
		sendBroadcast(new Intent(Constants.Stop));
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}
