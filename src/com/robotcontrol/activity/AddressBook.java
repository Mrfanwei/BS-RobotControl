package com.robotcontrol.activity;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.robotcontrol.utils.NetUtil;
import com.robotcontrol.utils.NetUtil.callback;
import com.robotcontrol.utils.ThreadPool;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddressBook extends Activity {

	private EditText editfather;
	private EditText editmother;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_book);
		editfather = (EditText) findViewById(R.id.father);
		editmother = (EditText) findViewById(R.id.mother);
		
	}

	public void ok(View view) {
		String fathernumber = editfather.getText().toString().trim();
		String mothernumber = editmother.getText().toString().trim();
		if (fathernumber == null || fathernumber.equals("")) {
			Toast.makeText(this, "请输入父亲电话", Toast.LENGTH_SHORT).show();
			return;
		}
		if (mothernumber == null || mothernumber.equals("")) {
			Toast.makeText(this, "请输入母亲电话", Toast.LENGTH_SHORT).show();
			return;
		}
		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {

				Map<String, String> params = new HashMap<String, String>();
				try {
					NetUtil.getinstance().http(getString(R.string.url), params,
							new callback() {

								@Override
								public void success(JSONObject json) {
									// TODO Auto-generated method stub

								}

								@Override
								public void error(String errorresult) {
									// TODO Auto-generated method stub
									
								}
							},AddressBook.this);
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.address_book, menu);
		return true;
	}

}
