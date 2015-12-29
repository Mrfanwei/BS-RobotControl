package com.robotcontrol.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShowNormalFileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_normal_file);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_normal_file, menu);
		return true;
	}

}
