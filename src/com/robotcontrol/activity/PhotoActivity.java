package com.robotcontrol.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.robotcontrol.adapter.PhotoAdapter;
import com.robotcontrol.adapter.PhotoAdapter.callback;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.ImageLoader;
import com.robotcontrol.utils.ThreadPool;
import com.robotcontrol.utils.ImageLoader.Type;
import com.robotcontrol.utils.StartUtil;
import com.robotcontrol.utils.ToastUtil;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PhotoActivity extends Activity implements OnClickListener {

	private Button refersh;
	private List<String> names = new ArrayList<String>();
	private File file;
	private ImageLoader loader;
	private String[] paths;
	private GridView photo_grid;
	private PhotoAdapter simpleAdapter;
	private Button delete;
	private String[] localphotos;
	private boolean choosestate = false;
	private ArrayList<String> delete_list = new ArrayList<String>();
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);
		getfile();
		getlocalphoto();
		refersh = (Button) findViewById(R.id.photo_refersh);
		refersh.setOnClickListener(this);
		delete = (Button) findViewById(R.id.photo_delete);
		delete.setOnClickListener(this);
		BroadcastReceiverRegister.reg(this,
				new String[] { Constants.Photo_Reply }, photo_setadapter);
		BroadcastReceiverRegister.reg(this,
				new String[] { Constants.Photo_Reply_Names }, names_reply);
		loader = ImageLoader.getInstance(3, Type.LIFO);
		photo_grid = (GridView) findViewById(R.id.photo);
		photo_grid.setOnItemClickListener(clickListener);
		photo_grid.setOnItemLongClickListener(onlongclick);
		progressDialog = new ProgressDialog(this);
		if (!file.exists()) {
			file.mkdirs();
		}

		super.onCreate(savedInstanceState);
	}

	public void getfile() {
		file = new File(this.getExternalFilesDir(null).getAbsolutePath()
				+ "/"
				+ getSharedPreferences("Receipt", MODE_PRIVATE).getString(
						"username", null) + "small");
	}

	private void getlocalphoto() {
		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				localphotos = new String[] {};
				localphotos = file.list();
			}
		});

	}

	@Override
	protected void onStart() {
		query_photo_name();
		setadapter();
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photo_refersh:
			if (refersh.getText().toString().equals("刷新")) {
				if (names.size() != 0) {
					progressDialog.setMessage("获取图片中......");
					progressDialog.show();
					progressDialog.setCanceledOnTouchOutside(false);
					query_photo();
					refersh.setEnabled(false);
				} else {
					ToastUtil.showtomain(this, "没有照片了！");
				}
			} else {
				back();
			}

			break;
		case R.id.photo_delete:
			if (delete_list.size() > 0) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("删除中......");
				progressDialog.show();
				progressDialog.setCanceledOnTouchOutside(false);
				sendBroadcast(new Intent(Constants.Photo_Delete).putExtra(
						"delete_names", delete_list.toArray(new String[] {})));
				delete.setEnabled(false);
				ThreadPool.execute(new Runnable() {

					@Override
					public void run() {
						for (int i = 0; i < delete_list.size(); i++) {
							File f = new File(file.getAbsolutePath() + "/"
									+ delete_list.get(i));
							f.delete();
						}
						handler.sendEmptyMessage(0);
					}
				});

			} else {
				ToastUtil.showtomain(this, "请选择图片");
			}
			break;
		default:
			break;
		}
	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			delete.setEnabled(true);
			back();
			setadapter();
		};
	};

	public void back() {
		choosestate = false;
		simpleAdapter.notifyDataSetChanged();
		delete.setVisibility(View.GONE);
		refersh.setText("刷新");
	}

	BroadcastReceiver names_reply = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			names = intent.getStringArrayListExtra("result");
			ThreadPool.execute(new Runnable() {

				@Override
				public void run() {
					if (names.size() == 0) {
						return;
					}
					for (int i = 0; i < names.size(); i++) {
						for (int j = 0; j < localphotos.length; j++) {
							if (names.get(i).equals(localphotos[j])) {
								names.remove(i);
							}
						}
					}
					Log.i("nameslength", names.size() + "");
				}
			});
		}
	};

	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View v, int position,
				long arg3) {
			if (choosestate) {

			} else {
				Bundle params = new Bundle();
				params.putInt("position", position);
				StartUtil.startintent(PhotoActivity.this,
						BigImageActivity.class, "no", params);
			}
		}
	};

	private void setadapter() {
		getfile();
		if (file.list().length == 0) {
			paths = new String[] {};
		} else {
			paths = new String[file.list().length];
			for (int i = 0; i < paths.length; i++) {
				paths[i] = file.getAbsolutePath() + "/" + file.list()[i];
			}
		}
		simpleAdapter = new PhotoAdapter(PhotoActivity.this, paths, loader,
				back);
		photo_grid.setAdapter(simpleAdapter);

	}

	private callback back = new callback() {

		@Override
		public boolean IslongClick() {

			return choosestate;
		}
	};

	BroadcastReceiver photo_setadapter = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Photo_Reply)) {
				setadapter();
				if (index == names.size()) {
					index = 0;
					names.clear();
					refersh.setEnabled(true);
					if (progressDialog != null) {
						progressDialog.dismiss();
					}
				} else {
					query_photo();
				}
			}
		}
	};

	private OnItemLongClickListener onlongclick = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> adapter, View v,
				int position, long arg3) {
			if (choosestate) {
				choosestate = false;
			} else {
				choosestate = true;
			}
			simpleAdapter.notifyDataSetChanged();
			delete.setVisibility(View.VISIBLE);
			refersh.setText("取消");
			return false;
		}
	};

	public void checked(String name) {
		delete_list.add(name);
	}

	public void notcheck(String name) {
		delete_list.remove(name);
	}

	private int index = 0;

	public void query_photo() {

		if (index < names.size()) {
			this.sendBroadcast(new Intent(Constants.Photo_Query).putExtra(
					"name", names.get(index)));
		}
		index++;
		Log.i("index", index + "");
	}

	private void query_photo_name() {
		sendBroadcast(new Intent(Constants.Photo_Query_Name));
	}

	public void back(View view) {
		finish();
	}

	protected void onDestroy() {
		if (names_reply != null) {
			unregisterReceiver(names_reply);
		}
		if (photo_setadapter != null) {
			unregisterReceiver(photo_setadapter);
		}
		super.onDestroy();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

}
