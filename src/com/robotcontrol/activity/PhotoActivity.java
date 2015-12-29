package com.robotcontrol.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.robotcontrol.bean.Result;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.StartUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

public class PhotoActivity extends Activity implements OnClickListener {

	private GridView photos;
	private TextView refersh;
	private SimpleAdapter simpleAdapter;
	private List<String> names = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		photos = (GridView) findViewById(R.id.photo);
		photos.setOnItemClickListener(clickListener);
		refersh = (TextView) findViewById(R.id.photo_refersh);
		refersh.setOnClickListener(this);
		if (getExternalFilesDir(null).listFiles().length > 0) {
			setadapter("local", new Intent().putExtra("path",
					getExternalFilesDir(null).getAbsolutePath()));
		}
		query_photo_name();
		BroadcastReceiverRegister.reg(this,
				new String[] { Constants.Photo_Reply }, photo_setadapter);
		BroadcastReceiverRegister.reg(this,
				new String[] { Constants.Photo_Reply_Names }, names_reply);

	}

	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position,
				long arg3) {
			Bundle params = new Bundle();
			params.putString("url", "");
			StartUtil.startintent(PhotoActivity.this, ShowBigImage.class, "no",
					params);
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_refersh:
			query_photo();
			break;
		default:
			break;
		}
	}

	BroadcastReceiver names_reply = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			names = intent.getStringArrayListExtra("result");
		}
	};
	List<Map<String, Object>> photo_data = new ArrayList<Map<String, Object>>();

	private void setadapter(String flag, Intent intent) {
		simpleAdapter = new SimpleAdapter(PhotoActivity.this, getdata(flag,
				intent), R.layout.photo, new String[] { "id", "img" },
				new int[] { R.id.photo_id, R.id.photo_img });
		photos.setAdapter(simpleAdapter);
		simpleAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object bitmap, String arg2) {
				if (view instanceof ImageView) {
					ImageView imgview = (ImageView) view;
					imgview.setImageBitmap((Bitmap) bitmap);
				}
				return true;
			}
		});
	}

	private List<Map<String, Object>> getdata(String flag, Intent intent) {

		if (flag.equals("result")) {
			final Result result = intent.getParcelableExtra("result");
			Log.i("json", result.getName());
			final String name = result.getName();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("id", name);
			params.put("img", result.getDw());
			photo_data.add(params);
		} else {
			File file = new File(intent.getStringExtra("path"));
			String[] fs = file.list();
			for (int i = 0; i < fs.length; i++) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("id", fs[i].substring(0, fs[i].indexOf(".") + 1));
				params.put(
						"img",
						BitmapFactory.decodeFile(file.getAbsolutePath() + "/"
								+ fs[i]));
				photo_data.add(params);
			}
		}

		return photo_data;
	}

	private void query_photo_name() {
		sendBroadcast(new Intent(Constants.Photo_Query_Name));
	}

	private int index = 0;

	private long starttime;

	private void query_photo() {
		if (index < names.size()) {
			sendBroadcast(new Intent(Constants.Photo_Query).putExtra("name",
					names.get(index)));
			index++;
		}
		starttime = System.currentTimeMillis();
	}

	BroadcastReceiver photo_setadapter = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Photo_Reply)) {
				Log.i("Time", (System.currentTimeMillis() - starttime) / 1000
						+ "秒");
				setadapter("result", intent);
				query_photo();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.photo, menu);
		return true;
	}

}
