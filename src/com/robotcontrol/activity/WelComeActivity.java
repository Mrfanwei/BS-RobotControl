package com.robotcontrol.activity;

import java.io.InputStream;

import org.json.JSONObject;

import com.robotcontrol.service.UpdateService;
import com.robotcontrol.utils.HandlerUtil;
import com.robotcontrol.utils.NetUtil;
import com.robotcontrol.utils.NetUtil.callback;
import com.robotcontrol.utils.StartUtil;
import com.robotcontrol.utils.ThreadPool;
import com.robotcontrol.utils.ToastUtil;
import com.robotcontrol.utils.XmlUtil;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;

public class WelComeActivity extends Activity {

	private InputStream input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wel_come);
		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				ThreadPool.execute(new Runnable() {

					@Override
					public void run() {

						try {

							String version = XmlUtil.xml(
									NetUtil.getinstance().downloadfile(
											WelComeActivity.this,
											getString(R.string.version_url),
											new callback() {

												@Override
												public void success(
														JSONObject json) {

												}

												@Override
												public void error(
														String errorresult) {
													HandlerUtil.sendmsg(
															handler,
															errorresult, 1);
												}
											}), "xin");
							PackageManager packageManager = getPackageManager();
							PackageInfo info = packageManager.getPackageInfo(
									getPackageName(), 0);
							if (Double.parseDouble(version) > Double
									.parseDouble(info.versionName)) {
								handler.sendEmptyMessage(0);
							} else {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								startActivity(new Intent(WelComeActivity.this,
										GuideActivity.class));
								overridePendingTransition(
										android.R.anim.fade_in,
										android.R.anim.fade_out);
								finish();
							}
						} catch (NameNotFoundException e) {
							e.printStackTrace();
						}

					}
				});
			}
		});
	}

	private AlertDialog alert;

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == 0) {
				AlertDialog.Builder builder = new Builder(WelComeActivity.this);
				builder.setMessage("发现新版本,是否更新？");
				builder.setTitle("版本更新");
				builder.setPositiveButton("是",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								startService(new Intent(WelComeActivity.this,
										UpdateService.class));
								StartUtil.startintent(WelComeActivity.this,
										GuideActivity.class, "finish");

							}
						});
				builder.setNegativeButton("否",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								alert.dismiss();
								StartUtil.startintent(WelComeActivity.this,
										GuideActivity.class, "finish");
							}
						});
				alert = builder.create();
				alert.show();
			} else if (msg.what == 1) {
				ToastUtil.showtomain(WelComeActivity.this, msg.getData()
						.getString("result"));
				StartUtil.startintent(WelComeActivity.this,
						GuideActivity.class, "finish");
			}
		};
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wel_come, menu);
		return true;
	}

}
