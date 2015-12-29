package com.robotcontrol.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mining.app.zxing.camera.CameraManager;
import com.mining.app.zxing.decoding.CaptureActivityHandler;
import com.mining.app.zxing.decoding.InactivityTimer;
import com.mining.app.zxing.view.ViewfinderView;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.HandlerUtil;
import com.robotcontrol.utils.NetUtil;
import com.robotcontrol.utils.ThreadPool;
import com.robotcontrol.utils.ToastUtil;
import com.robotcontrol.utils.NetUtil.callback;

/**
 * Initial the camera
 * 
 * @author Ryan.Tang
 */
public class BindRobotActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Button bind;
	private EditText edit_robot_id;
	private EditText edit_robot_serial;
	private Timer timer = new Timer();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_robot);
		edit_robot_id = (EditText) findViewById(R.id.robot_id);
		edit_robot_serial = (EditText) findViewById(R.id.robot_serial);
		bind = (Button) findViewById(R.id.bind);
		bind.setOnClickListener(clickListener);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String id = edit_robot_id.getText().toString().trim();
			String robot_serial = edit_robot_serial.getText().toString().trim();
			bindrobot(id, robot_serial);
		}
	};

	public void bindrobot(final String id, final String serial) {
		ThreadPool.execute(new Runnable() {

			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("id", getSharedPreferences("userinfo", MODE_PRIVATE)
						.getInt("id", 0) + "");
				Log.i("id", getSharedPreferences("userinfo", MODE_PRIVATE)
						.getInt("id", 0) + "");
				params.put("session",
						getSharedPreferences("userinfo", MODE_PRIVATE)
								.getString("session", null));
				params.put("robot_id", id);
				params.put("robot_serial", serial);
				try {
					NetUtil.getinstance().http(
							getString(R.string.url) + "/robot/bind", params,
							new callback() {

								@Override
								public void success(JSONObject json) {
									try {
										String ret = json.getString("ret");
										if (ret.equals("1")) {
											handletoast.sendEmptyMessage(1);
										} else if (ret.equals("0")) {
											Intent intent = new Intent(
													BindRobotActivity.this,
													ConnectActivity.class);
											setResult(
													Constants.bindrobot_RequestCode,
													intent);
											finish();
										} else if (ret.equals("3")) {
											handletoast.sendEmptyMessage(10);
										} else if (ret.equals("4")) {
											handletoast.sendEmptyMessage(11);
										} else if (ret.equals("2")) {
											handletoast.sendEmptyMessage(12);
										}

									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

								@Override
								public void error(String errorresult) {
									HandlerUtil
											.sendmsg(handler, errorresult, 5);
								}
							}, BindRobotActivity.this);
				} catch (SocketTimeoutException e) {
					HandlerUtil.sendmsg(handler, "请求超时", 5);
					e.printStackTrace();
				}

			}
		});
	}

	Handler handletoast = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (handler != null) {
					handler.restartPreviewAndDecode();
				}
				break;
			case 1:
				ToastUtil.showtomain(BindRobotActivity.this, "机器人不存在");
				break;
			case 5:
				ToastUtil.showtomain(BindRobotActivity.this, msg.getData()
						.getString("result"));
				break;
			case 10:
				ToastUtil.showtomain(BindRobotActivity.this, "机器人已被绑定！");
				break;
			case 11:
				ToastUtil.showtomain(BindRobotActivity.this, "已超过绑定数量！");
				break;
			case 12:
				ToastUtil.showtomain(BindRobotActivity.this, "绑定参数不正确！");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.qrcodesurface);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * ����ɨ����
	 * 
	 * @param result
	 * @param barcode
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		if (resultString.equals("")) {
			Toast.makeText(BindRobotActivity.this, "Scan failed!",
					Toast.LENGTH_SHORT).show();
		} else {
			resultString = resultString.trim();
			String[] robotinfo = null;
			try {
				robotinfo = resultString.split("-");
				bindrobot(robotinfo[0], robotinfo[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ToastUtil.showtomain(this, "请扫描正确的二维码！");
			}

			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					handletoast.sendEmptyMessage(0);
				}
			}, 1500);

		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

}