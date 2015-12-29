package com.robotcontrol.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.robotcontrol.adapter.TaskAdapter;
import com.robotcontrol.bean.Task;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.StartUtil;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

public class TaskRemindActivity extends Activity {

	private SwipeMenuListView tasks;
	private SwipeRefreshLayout refreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_remind);
		tasks = (SwipeMenuListView) findViewById(R.id.tasklist);
		tasks.setOnItemClickListener(onitemclick);
		refreshLayout = (SwipeRefreshLayout) findViewById(R.id.task_refresh);
		refreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				Intent taskaction = new Intent(Constants.Task_Query);
				sendBroadcast(taskaction);
				time = new Timer();
				time.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						handler.sendEmptyMessage(0);
					}
				}, 5000);
			}
		});

		BroadcastReceiverRegister.reg(this, new String[] { Constants.Result },
				refreshUI);
	}

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (refreshLayout.isRefreshing()) {
				refreshLayout.setRefreshing(false);
			}
		};
	};
	Timer time = new Timer();
	BroadcastReceiver refreshUI = new BroadcastReceiver() {
		public void onReceive(android.content.Context arg0, Intent intent) {
			if (intent.getAction().equals(Constants.Result)) {
				list_task.clear();
				refreshLayout.setRefreshing(false);
				String result = intent.getStringExtra("result");
				Log.i("Result", result);
				try {
					JSONArray tasks = new JSONArray(result);
					for (int i = 0; i < tasks.length(); i++) {
						Task task = new Task();
						task.setId(tasks.getJSONObject(i).getInt("id"));
						task.setSettime(tasks.getJSONObject(i)
								.getString("time"));
						task.setTitle(tasks.getJSONObject(i).getString("title"));
						task.setContent(tasks.getJSONObject(i).getString(
								"content"));
						task.setIsaway(0);
						list_task.add(task);
					}
					setadapter();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	};

	@Override
	protected void onResume() {
		sendBroadcast(new Intent(Constants.Task_Query));
		super.onResume();
	}

	private OnItemClickListener onitemclick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
			Bundle bundle = new Bundle();
			bundle.putString("state", Constants.Update);
			bundle.putParcelable("task", list_task.get(index));
			bundle.putInt("index", index);
			StartUtil
					.startintentforresult(TaskRemindActivity.this,
							AddTaskActivity.class, bundle,
							Constants.update_RequestCode);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.task_remind, menu);
		return true;
	}

	private List<Task> list_task = new ArrayList<Task>();
	private TaskAdapter taskadapter = null;

	private void setadapter() {
		taskadapter = new TaskAdapter(this, list_task);

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(dp2px(90));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};

		tasks.setMenuCreator(creator, refreshLayout);
		tasks.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onMenuItemClick(final int position, SwipeMenu menu,
					int index) {
				View v = tasks.getChildAt(position);
				v.setBackgroundColor(Color.BLACK);
				TranslateAnimation tran = new TranslateAnimation(v.getLeft(),
						-(v.getLeft() + v.getWidth()), 0, 0);
				AlphaAnimation al = new AlphaAnimation(1, 0);
				AnimationSet set = new AnimationSet(true);
				set.addAnimation(tran);
				set.addAnimation(al);
				set.setDuration(500);
				set.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub

						Constants.task = list_task.get(position);
						list_task.remove(position);
						taskadapter.notifyDataSetChanged();
						Intent intent = new Intent();
						intent.setAction(Constants.Task_Remove);
						sendBroadcast(intent);
					}
				});
				v.startAnimation(set);
			}
		});
		tasks.setAdapter(taskadapter);
	};

	public void add(View view) {

		Bundle params = new Bundle();
		params.putString("state", Constants.Add);
		StartUtil.startintentforresult(this, AddTaskActivity.class, params,
				Constants.add_RequestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constants.update_RequestCode
				&& resultCode == Constants.IS_OK) {
			list_task.set(data.getIntExtra("index", 0),
					(Task) data.getParcelableExtra("task"));
			taskadapter.notifyDataSetChanged();
			tasks.setAdapter(taskadapter);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		if(refreshUI!=null){
			unregisterReceiver(refreshUI);
		}
		super.onDestroy();
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
}
