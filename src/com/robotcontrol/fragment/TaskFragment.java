package com.robotcontrol.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.robotcontrol.activity.AddTaskActivity;
import com.robotcontrol.activity.R;
import com.robotcontrol.activity.TaskRemindActivity;
import com.robotcontrol.activity.TaskRemindActivity.OnFragmentRefresh;
import com.robotcontrol.adapter.TaskAdapter;
import com.robotcontrol.bean.Alarm;
import com.robotcontrol.bean.Remind;
import com.robotcontrol.bean.Task;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.StartUtil;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class TaskFragment extends Fragment implements OnFragmentRefresh {

	private SwipeRefreshLayout refreshLayout;
	private SwipeMenuListView task_listview;
	private TaskAdapter taskadapter = null;
	private List<Task> list_task = new ArrayList();
	private TaskRemindActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.from(getActivity()).inflate(R.layout.remind_fragment,
				null);
		refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.task_refresh);
		refreshLayout.setOnRefreshListener(onRefreshListener);
		task_listview = (SwipeMenuListView) v.findViewById(R.id.tasklist);
		task_listview.setOnItemClickListener(onitemclick);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (TaskRemindActivity) activity;
	}

	private OnRefreshListener onRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {
			Intent taskaction = new Intent(Constants.Task_Query);
			getActivity().sendBroadcast(taskaction);
			list_task.clear();
			Timer time = new Timer();
			time.schedule(new TimerTask() {

				@Override
				public void run() {
					if (list_task.size() == 0) {
						if (refreshLayout.isRefreshing()) {
							handler.sendEmptyMessage(0);
						}

					}
				}
			}, 10000);
		}
	};

	Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {
			if (msg.what == 0) {
				refreshLayout.setRefreshing(false);
			}
		};
	};

	@Override
	public void OnRefresh(final List tasks) {
		refreshLayout.setRefreshing(false);

		list_task = tasks;
		taskadapter = new TaskAdapter(getActivity(), list_task);

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
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

		task_listview.setMenuCreator(creator, refreshLayout);
		task_listview.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onMenuItemClick(final int position, SwipeMenu menu,
					int index) {
				View v = task_listview.getChildAt(position);
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

					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						if (activity.returnflag() == 0) {

						}
						Constants.task = list_task.get(position);
						tasks.remove(position);
						taskadapter.notifyDataSetChanged();
						Intent intent = new Intent();
						intent.setAction(Constants.Task_Remove);
						getActivity().sendBroadcast(intent);
					}
				});
				v.startAnimation(set);
			}
		});
		task_listview.setAdapter(taskadapter);
	}

	private OnItemClickListener onitemclick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int index,
				long arg3) {
			Bundle bundle = new Bundle();
			bundle.putString("state", Constants.Update);
			if (activity.returnflag() == 0) {
				bundle.putString("mode", "remind");
				bundle.putParcelable("task", list_task.get(index));
			} else {
				bundle.putString("mode", "alarm");
				bundle.putParcelable("task", (Alarm) list_task.get(index));
			}
			bundle.putInt("index", index);
			StartUtil
					.startintentforresult(getActivity(), AddTaskActivity.class,
							bundle, Constants.update_RequestCode);
		}
	};

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}

}
