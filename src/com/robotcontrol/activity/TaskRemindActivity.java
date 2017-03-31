//package com.robotcontrol.activity;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import com.baoyz.swipemenulistview.SwipeMenu;
//import com.baoyz.swipemenulistview.SwipeMenuCreator;
//import com.baoyz.swipemenulistview.SwipeMenuItem;
//import com.baoyz.swipemenulistview.SwipeMenuListView;
//import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
//import com.robotcontrol.adapter.TaskAdapter;
//import com.robotcontrol.bean.Task;
//import com.robotcontrol.utils.BroadcastReceiverRegister;
//import com.robotcontrol.utils.Constants;
//import com.robotcontrol.utils.StartUtil;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Menu;
//import android.view.View;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.AnimationSet;
//import android.view.animation.TranslateAnimation;
//import android.view.animation.Animation.AnimationListener;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.AdapterView;
//
//public class TaskRemindActivity extends Activity {
//
//	private SwipeMenuListView tasks;
//	private SwipeRefreshLayout refreshLayout;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_task_remind);
//		tasks = (SwipeMenuListView) findViewById(R.id.tasklist);
//		tasks.setOnItemClickListener(onitemclick);
//		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.task_refresh);
//		refreshLayout.setOnRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				Intent taskaction = new Intent(Constants.Task_Query);
//				sendBroadcast(taskaction);
//				time = new Timer();
//				time.schedule(new TimerTask() {
//
//					@Override
//					public void run() {
//						handler.sendEmptyMessage(0);
//					}
//				}, 5000);
//			}
//		});
//
//		BroadcastReceiverRegister.reg(this, new String[] { Constants.Result },
//				refreshUI);
//	}
//
//	Handler handler = new Handler() {
//		public void dispatchMessage(android.os.Message msg) {
//			if (refreshLayout.isRefreshing()) {
//				refreshLayout.setRefreshing(false);
//			}
//		};
//	};
//	Timer time = new Timer();
//	BroadcastReceiver refreshUI = new BroadcastReceiver() {
//		public void onReceive(android.content.Context arg0, Intent intent) {
//			if (intent.getAction().equals(Constants.Result)) {
//				list_task.clear();
//				refreshLayout.setRefreshing(false);
//				String result = intent.getStringExtra("result");
//				Log.i("Result", result);
//				try {
//					JSONArray tasks = new JSONArray(result);
//					for (int i = 0; i < tasks.length(); i++) {
//						Task task = new Task();
//						task.setId(tasks.getJSONObject(i).getInt("id"));
//						task.setSettime(tasks.getJSONObject(i)
//								.getString("time"));
//						task.setTitle(tasks.getJSONObject(i).getString("title"));
//						task.setContent(tasks.getJSONObject(i).getString(
//								"content"));
//						task.setIsaways(0);
//						list_task.add(task);
//					}
//					setadapter();
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		};
//	};
//
//	@Override
//	protected void onResume() {
//		sendBroadcast(new Intent(Constants.Task_Query));
//		super.onResume();
//	}
//
//	private OnItemClickListener onitemclick = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
//				long arg3) {
//			Bundle bundle = new Bundle();
//			bundle.putString("state", Constants.Update);
//			bundle.putParcelable("task", list_task.get(index));
//			bundle.putInt("index", index);
//			StartUtil
//					.startintentforresult(TaskRemindActivity.this,
//							AddTaskActivity.class, bundle,
//							Constants.update_RequestCode);
//		}
//	};
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		return true;
//	}
//
//	private List<Task> list_task = new ArrayList<Task>();
//	private TaskAdapter taskadapter = null;
//
//	private void setadapter() {
//		taskadapter = new TaskAdapter(this, list_task);
//
//		SwipeMenuCreator creator = new SwipeMenuCreator() {
//
//			@Override
//			public void create(SwipeMenu menu) {
//
//				// create "delete" item
//				SwipeMenuItem deleteItem = new SwipeMenuItem(
//						getApplicationContext());
//				// set item background
//				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
//						0x3F, 0x25)));
//				// set item width
//				deleteItem.setWidth(dp2px(90));
//				// set a icon
//				deleteItem.setIcon(R.drawable.ic_delete);
//				// add to menu
//				menu.addMenuItem(deleteItem);
//			}
//		};
//
//		tasks.setMenuCreator(creator, refreshLayout);
//		tasks.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//			@Override
//			public void onMenuItemClick(final int position, SwipeMenu menu,
//					int index) {
//				View v = tasks.getChildAt(position);
//				v.setBackgroundColor(Color.BLACK);
//				TranslateAnimation tran = new TranslateAnimation(v.getLeft(),
//						-(v.getLeft() + v.getWidth()), 0, 0);
//				AlphaAnimation al = new AlphaAnimation(1, 0);
//				AnimationSet set = new AnimationSet(true);
//				set.addAnimation(tran);
//				set.addAnimation(al);
//				set.setDuration(500);
//				set.setAnimationListener(new AnimationListener() {
//
//					@Override
//					public void onAnimationStart(Animation animation) {
//
//					}
//
//					@Override
//					public void onAnimationRepeat(Animation animation) {
//
//					}
//
//					@Override
//					public void onAnimationEnd(Animation animation) {
//
//						Constants.task = list_task.get(position);
//						list_task.remove(position);
//						taskadapter.notifyDataSetChanged();
//						Intent intent = new Intent();
//						intent.setAction(Constants.Task_Remove);
//						sendBroadcast(intent);
//					}
//				});
//				v.startAnimation(set);
//			}
//		});
//		tasks.setAdapter(taskadapter);
//	};
//
//	public void add(View view) {
//		Bundle params = new Bundle();
//		params.putString("state", Constants.Add);
//		StartUtil.startintentforresult(this, AddTaskActivity.class, params,
//				Constants.add_RequestCode);
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//		if (requestCode == Constants.update_RequestCode
//				&& resultCode == Constants.IS_OK) {
//			list_task.set(data.getIntExtra("index", 0),
//					(Task) data.getParcelableExtra("task"));
//			taskadapter.notifyDataSetChanged();
//			tasks.setAdapter(taskadapter);
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	protected void onStop() {
//		super.onStop();
//	}
//
//	@Override
//	protected void onDestroy() {
//		if (refreshUI != null) {
//			unregisterReceiver(refreshUI);
//		}
//		super.onDestroy();
//	}
//
//	private int dp2px(int dp) {
//		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
//				getResources().getDisplayMetrics());
//	}
//}

package com.robotcontrol.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.json.JSONException;

import com.robotcontrol.bean.Alarm;
import com.robotcontrol.bean.Remind;
import com.robotcontrol.biz.Biz;
import com.robotcontrol.fragment.TaskFragment;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.StartUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TaskRemindActivity extends BaseActivity implements OnClickListener {

	public static final int REMIND = 1;
	public static final int ALARM = 2;
	
	private TaskFragment remind;
	private OnFragmentRefresh onFragmentRefresh;
	private RelativeLayout reminditem;
	private RelativeLayout alarmitem;
	private FragmentManager manager;
	private static int flag = 0;

	ImageView iv_remind;
	ImageView iv_clock;

	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub			
			switch(msg.what){
			case REMIND:							
				iv_remind.setBackgroundResource(R.drawable.tx60);
				iv_clock.setBackgroundResource(R.drawable.nz);
				break;
			case ALARM:
				
				iv_clock.setBackgroundResource(R.drawable.nz60);
				iv_remind.setBackgroundResource(R.drawable.tx);
				break;
			}
			super.handleMessage(msg);			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_task_remind);
		initView();
		super.onCreate(savedInstanceState);
	}

	private void initView() {
		// TODO Auto-generated method stub
		iv_remind = (ImageView) findViewById(R.id.reminditem_img);
		iv_clock = (ImageView) findViewById(R.id.alarmitem_img);	
		iv_remind.setBackgroundResource(R.drawable.tx60);
	}

	public void back(View view) {
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reminditem:
			flag = 0;			
			mHandler.sendEmptyMessage(REMIND);
			onFragmentRefresh.OnRefresh(list_task);
			break;
		case R.id.alarmitem:
			flag = 1;		
			mHandler.sendEmptyMessage(ALARM);
			onFragmentRefresh.OnRefresh(alarms);
			break;
		default:
			break;
		}

	}

	public Timer time = new Timer();
	BroadcastReceiver refreshUI = new BroadcastReceiver() {
		public void onReceive(android.content.Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Result)) {
				list_task.clear();
				alarms.clear();
				String result = intent.getStringExtra("result");
				Log.i("Result", result);
				try {
					Biz.adapter_task(result, list_task, alarms);
					if (flag == 0) {
						onFragmentRefresh.OnRefresh(list_task);
					} else if (flag == 1) {
						onFragmentRefresh.OnRefresh(alarms);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		};
	};

	public void onAttachFragment(android.app.Fragment fragment) {
		onFragmentRefresh = (OnFragmentRefresh) fragment;
	};

	@Override
	protected void onResume() {
		if (list_task != null) {
			list_task.clear();
		}
		if (alarms != null) {
			alarms.clear();
		}
		sendBroadcast(new Intent(Constants.Task_Query));
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	private List<Remind> list_task = new ArrayList<Remind>();
	private List<Alarm> alarms = new ArrayList<Alarm>();

	public void add(View view) {
		Bundle params = new Bundle();
		params.putString("state", Constants.Add);
		if (flag == 1) {
			params.putString("mode", "alarm");
		} else {
			params.putString("mode", "remind");
		}
		params.putInt("flag", flag);
		StartUtil.startintentforresult(this, AddTaskActivity.class, params,
				Constants.add_RequestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.update_RequestCode
				&& resultCode == Constants.IS_OK) {
			;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public int returnflag() {
		return flag;
	}

	@Override
	protected void onDestroy() {
		if (refreshUI != null) {
			unregisterReceiver(refreshUI);
		}
		super.onDestroy();
	}

	@Override
	public void initlayout(OnRefreshListener onRefreshListener) {
		remind = new TaskFragment();
		reminditem = (RelativeLayout) findViewById(R.id.reminditem);
		reminditem.setOnClickListener(this);
		alarmitem = (RelativeLayout) findViewById(R.id.alarmitem);
		alarmitem.setOnClickListener(this);
		manager = getFragmentManager();
		manager.beginTransaction().replace(R.id.task_content, remind).commit();
		BroadcastReceiverRegister.reg(this, new String[] { Constants.Result },
				refreshUI);
	}

	public interface OnFragmentRefresh {
		public void OnRefresh(List<?> tasks);
	}
}
