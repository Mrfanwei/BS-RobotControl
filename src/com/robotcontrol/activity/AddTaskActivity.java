package com.robotcontrol.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.robotcontrol.bean.Task;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.ToastUtil;
import com.robotcontrol.widget.RobotDialog;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class AddTaskActivity extends FragmentActivity implements
		OnClickListener, DatePickerDialog.OnDateSetListener,
		TimePickerDialog.OnTimeSetListener {
	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";
	private Button date;
	private Button time;
	private Task task;
	private EditText taskcontent;
	private String state;
	private int index;
	private EditText edit_title;
	private DatePickerDialog datePickerDialog;
	private TimePickerDialog timePickerDialog;
	private Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);
		state = getIntent().getExtras().getString("state");
		date = (Button) findViewById(R.id.date);
		date.setOnClickListener(this);
		time = (Button) findViewById(R.id.time);
		time.setOnClickListener(this);
		taskcontent = (EditText) findViewById(R.id.taskcontent);
		edit_title = (EditText) findViewById(R.id.task_title);
		calendar = Calendar.getInstance();
		datePickerDialog = DatePickerDialog.newInstance(this,
				calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
		timePickerDialog = TimePickerDialog.newInstance(this,
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), false);
		if (state.equals(Constants.Update)) {
			task = getIntent().getParcelableExtra("task");
			settime = Calendar.getInstance();
			settime.setTimeInMillis(Long.parseLong(task.getSettime()));

			date.setText(settime.get(Calendar.YEAR) + "年"
					+ (settime.get(Calendar.MONTH) + 1) + "月"
					+ settime.get(Calendar.DATE) + "日");
			time.setText(settime.get(Calendar.HOUR_OF_DAY) + ":"
					+ settime.get(Calendar.MINUTE));
			taskcontent.setText(task.getContent() + "");
			index = getIntent().getExtras().getInt("index");
		} else {
			task = new Task();

		}
		settime = Calendar.getInstance();
	}

	/**
	 * @param view
	 */
	public void over(View view) {
		String content = taskcontent.getText().toString().trim();
		String title = edit_title.getText().toString().trim();
		if (content == null || content.equals("")) {
			ToastUtil.showtomain(this, "请输入提醒内容");
			return;
		}
		if (title == null || content.equals("")) {
			ToastUtil.showtomain(this, "请输入标题");
			return;
		}
		if (state.equals(Constants.Add)) {
			if (dateflag == false && timeflag == false) {
				ToastUtil.showtomain(this, "请选择时间");
				return;
			}
		}
		if (settime.getTimeInMillis() < System.currentTimeMillis()) {
			ToastUtil.showtomain(AddTaskActivity.this, "设置的时间不能小于当前时间");
			return;
		}
		task.setSettime(settime.getTimeInMillis() + "");
		task.setTitle(title);
		task.setContent(content);
		task.setIsaway(0);
		Intent in = new Intent();
		Intent intent = new Intent(this, TaskRemindActivity.class);
		intent.putExtra("task", task);
		Constants.task = task;
		if (state.equals(Constants.Update)) {
			intent.putExtra("index", index);
			in.setAction(Constants.Task_Updata);
		} else {
			in.setAction(Constants.Task_Add);
		}
		sendBroadcast(in);
		setResult(Constants.IS_OK, intent);
		finish();
	}

	Calendar settime = null;

	boolean dateflag = false;
	boolean timeflag = false;

	@Override
	public void onDateSet(DatePickerDialog arg0, int year, int month, int day) {
		settime.set(year, month, day);
		dateflag = true;
		date.setText(year + "年" + (month + 1) + "月" + day + "日");
	}

	RobotDialog alert = null;

	public void choose(View view) {

		List<String> types = new ArrayList<String>();
		types.add("用药提醒");
		types.add("上班提醒");
		types.add("做饭提醒");
		types.add("休息提醒");
		types.add("追剧提醒");
		types.add("接人/送人提醒");
		types.add("运动健身提醒");
		types.add("吃饭提醒");
		types.add("起床提醒");
		alert = new RobotDialog(AddTaskActivity.this);
		View v = LayoutInflater.from(this).inflate(R.layout.tasktype_dialog,
				null);
		ListView type = (ListView) v.findViewById(R.id.typelist);
		type.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View text, int arg2,
					long arg3) {
				edit_title.setText(((TextView) text
						.findViewById(android.R.id.text1)).getText().toString());
				alert.dismiss();
			}
		});
		type.setAdapter(new ArrayAdapter<String>(this, R.layout.type, types));
		alert.addContentView(v, new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		alert.showdialog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.date:
			DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH)).show(
					getFragmentManager(), "datePicker");
			break;
		case R.id.time:
			TimePickerDialog.newInstance(this,
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE), true).show(
					getFragmentManager(), "timePicker");
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_task, menu);
		return true;
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		settime.set(settime.get(Calendar.YEAR), settime.get(Calendar.MONTH),
				settime.get(Calendar.DATE), hourOfDay, minute, 0);
		if (minute < 10) {
			time.setText(hourOfDay + ":0" + minute);
		} else {
			time.setText(hourOfDay + ":" + minute);
		}

		timeflag = true;
	}

}
