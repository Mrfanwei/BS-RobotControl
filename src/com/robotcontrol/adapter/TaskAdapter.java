package com.robotcontrol.adapter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.robotcontrol.activity.R;
import com.robotcontrol.bean.Task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TaskAdapter extends BaseAdapter {

	private List<Task> tasks;
	private Context context;

	public TaskAdapter(Context context, List<Task> tasks) {
		super();
		this.context = context;
		this.tasks = tasks;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tasks.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	TaskHolder holder;
	LayoutInflater layout = null;

	@Override
	public View getView(int index, View v, ViewGroup arg2) {
		if (layout == null) {
			layout = LayoutInflater.from(context);
		}
		holder = new TaskHolder();
		v = layout.inflate(R.layout.taskitem, null);
		holder.textview_settime = (TextView) v.findViewById(R.id.time);
		holder.textView_content = (TextView) v.findViewById(R.id.contenttask);
		holder.textView_istoday = (TextView) v.findViewById(R.id.istoday);
		holder.textView_finishtime = (TextView) v.findViewById(R.id.settime);

		Calendar finishtime = Calendar.getInstance();
		finishtime.setTimeInMillis(Long
				.parseLong(tasks.get(index).getSettime()));
		holder.textView_istoday.setText("当天");
		
		holder.textview_settime.setText(finishtime.get(Calendar.YEAR) + "."
				+ (finishtime.get(Calendar.MONTH)+1) + "."
				+ finishtime.get(Calendar.DATE) + " "
				+ finishtime.get(Calendar.HOUR_OF_DAY) + ":"
				+ finishtime.get(Calendar.MINUTE));
		holder.textView_content.setText("[" + tasks.get(index).getTitle() + "] "
				+ tasks.get(index).getContent());
		v.setTag(holder);
		return v;
	}

	class TaskHolder {
		private TextView textview_settime;
		private TextView textView_content;
		private TextView textView_istoday;
		private TextView textView_finishtime;
	}

}
