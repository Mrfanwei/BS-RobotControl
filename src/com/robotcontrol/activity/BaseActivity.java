package com.robotcontrol.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;

public abstract class BaseActivity extends Activity {
	final String TAG = "Robotfw/BaseActivity";

	public  void setadapter(){
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initlayout(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				BaseActivity.this.onRefresh();
			}

		});
	}

	public void onRefresh() {

	}
	
	Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			onHandlerMessage(msg);
		};
	};

	public  void onHandlerMessage(Message msg){
		
	};

	public abstract void initlayout(OnRefreshListener onRefreshListener);
	

}
