package com.robotcontrol.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class VideoCallActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_call);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video_call, menu);
		return true;
	}

}
