package com.robotcontrol.utils;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil {

	public static void execute(Timer timer, final timertask task, int time) {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				task.exe();
			}
		}, new Date(), time);
	}

	public static void execute(Timer timer, final timertask task, Date when) {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				task.exe();
			}
		}, when);
	}

	public interface timertask {
		public void exe();
	}
}
