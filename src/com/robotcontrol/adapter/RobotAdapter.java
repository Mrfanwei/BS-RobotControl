package com.robotcontrol.adapter;

import java.util.List;

import com.robotcontrol.activity.R;
import com.robotcontrol.activity.R.color;
import com.robotcontrol.bean.Robot;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RobotAdapter extends BaseAdapter {

	private Context context;
	private List<Robot> robots;

	public RobotAdapter(Context context, List<Robot> robots) {
		super();
		this.context = context;
		this.robots = robots;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return robots.size();
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

	LayoutInflater layout = null;
	RobotHolder robotholder = null;

	@Override
	public View getView(int index, View v, ViewGroup arg2) {
		if (layout == null) {
			layout = LayoutInflater.from(context);
		}
		if (v == null && context != null&&index<=robots.size()) {
			v = layout.inflate(R.layout.robotitem, null);
			robotholder = new RobotHolder();
			robotholder.robotname = (TextView) v.findViewById(R.id.robot_item);
			robotholder.rid = (TextView) v.findViewById(R.id.rid);
			robotholder.online = (TextView) v.findViewById(R.id.online);
			robotholder.control = (TextView) v.findViewById(R.id.robot_control);
			if (robots.size() != 0) {
				robotholder.robotname
						.setText(robots.get(index).getRname() + "");
				robotholder.rid.setText(robots.get(index).getId() + "");
				if (robots.get(index).getController() == 0) {
					robotholder.control.setText("未被控制");
					robotholder.control.setTextColor(color.back_perss);
				} else {
					robotholder.control.setText("已被控制");
				}
				if (robots.get(index).isOnline()) {
					robotholder.online.setTextColor(Color.GREEN);
					robotholder.online.setText("在线");
				} else {
					robotholder.online.setTextColor(Color.RED);
					robotholder.online.setText("离线");
				}
				if (robots.get(index).getAir().equals(Robot.air.bind)) {

				} else {

				}
				v.setTag(robotholder);
			}
		}else if(index==robots.size()-1){
			return null;
		}

		return v;
	}

	class RobotHolder {
		private TextView robotname;
		private TextView rid;
		private TextView online;
		private TextView control;
	}

}
