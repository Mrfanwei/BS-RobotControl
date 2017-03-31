package com.robotcontrol.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.robotcontrol.activity.AddTaskActivity.onChooseListener;
import com.robotcontrol.activity.R;
import com.robotcontrol.activity.TaskRemindActivity;
import com.robotcontrol.bean.Remind;
import com.robotcontrol.utils.Constants;
import com.robotcontrol.utils.ToastUtil;
import com.robotcontrol.widget.RobotDialog;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AddremindFragment extends Fragment implements onChooseListener {

	public static final String DATEPICKER_TAG = "datepicker";
	public static final String TIMEPICKER_TAG = "timepicker";
	private Button date;
	private Button time;
	private Remind task;
	private EditText taskcontent;
	private String state;
	private int index;
	private EditText edit_title;
	private Calendar settime;
	boolean dateflag = false;
	boolean timeflag = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.from(getActivity()).inflate(
				R.layout.add_remind_fragment, null);
		state = getActivity().getIntent().getExtras().getString("state");
		date = (Button) v.findViewById(R.id.date);
		time = (Button) v.findViewById(R.id.time);
		taskcontent = (EditText) v.findViewById(R.id.taskcontent);
		edit_title = (EditText) v.findViewById(R.id.task_title);
		if (state.equals(Constants.Update)) {
			task = getActivity().getIntent().getParcelableExtra("task");
			settime = Calendar.getInstance();
			settime.setTimeInMillis(Long.parseLong(task.getSettime()));
			edit_title.setText(task.getTitle());
			date.setText(settime.get(Calendar.YEAR) + "年"
					+ (settime.get(Calendar.MONTH) + 1) + "月"
					+ settime.get(Calendar.DATE) + "日");
			time.setText(settime.get(Calendar.HOUR_OF_DAY) + ":"
					+ settime.get(Calendar.MINUTE));
			taskcontent.setText(task.getContent() + "");
			index = getActivity().getIntent().getExtras().getInt("index");
			timeflag = true;
			dateflag = true;
		} else {
			task = new Remind();
			settime = Calendar.getInstance();
		}
		return v;
	}

	RobotDialog alert = null;

	@Override
	public void onDate(int year, int monthOfYear, int dayOfMonth) {
		settime.set(year, monthOfYear, dayOfMonth);
		dateflag = true;
		String month = "";
		String day = "";
		if (monthOfYear < 10) {
			month = "0" + (monthOfYear + 1);
		} else {
			month = "" + (monthOfYear + 1);
		}
		if (dayOfMonth < 10) {
			day = "0" + dayOfMonth;
		} else {
			day = "" + dayOfMonth;
		}
		date.setText(year + "年" + month + "月" + day + "日");

	}

	@Override
	public void onTime(int hourOfDay, int minute) {
		settime.set(settime.get(Calendar.YEAR), settime.get(Calendar.MONTH),
				settime.get(Calendar.DATE), hourOfDay, minute, 0);
		if (minute < 10) {
			time.setText(hourOfDay + ":0" + minute);
		} else {
			time.setText(hourOfDay + ":" + minute);
		}

		timeflag = true;

	}

	@Override
	public void onOver() {
		String content = taskcontent.getText().toString().trim();
		String title = edit_title.getText().toString().trim();
		if (content == null || content.equals("")) {
			ToastUtil.showtomain(getActivity(), "请输入提醒内容");
			return;
		}
		if (title == null || content.equals("")) {
			ToastUtil.showtomain(getActivity(), "请输入标题");
			return;
		}
		if (state.equals(Constants.Add)) {
			if (dateflag == false && timeflag == false) {
				ToastUtil.showtomain(getActivity(), "请选择时间");
				return;
			}
		}
		if (settime.getTimeInMillis() < System.currentTimeMillis()) {
			ToastUtil.showtomain(getActivity(), "设置的时间不能小于当前时间");
			return;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String datatime = dateFormat.format(settime.getTimeInMillis());
		task.setSettime(datatime);
		task.setTitle(title);
		task.setContent(content);
		Intent in = new Intent();
		Intent intent = new Intent(getActivity(), TaskRemindActivity.class);
		intent.putExtra("task", task);
		Constants.task = task;
		if (state.equals(Constants.Update)) {
			intent.putExtra("index", index);
			in.setAction(Constants.Task_Updata);
		} else {
			in.setAction(Constants.Task_Add);
		}
		getActivity().sendBroadcast(in);
		getActivity().setResult(Constants.IS_OK, intent);
		getActivity().finish();

	}

	@Override
	public void onChoose(String text) {
		edit_title.setText(text);

	}

}
