package com.robotcontrol.activity;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ChatRoomDetailsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_room_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat_room_details, menu);
		return true;
	}

}
