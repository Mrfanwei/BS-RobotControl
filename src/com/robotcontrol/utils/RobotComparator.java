package com.robotcontrol.utils;

import java.util.Comparator;

import com.robotcontrol.bean.Robot;

public class RobotComparator implements Comparator<Robot> {
	@Override
	public int compare(Robot o1, Robot o2) {
		int o1flag=0;
		int o2flag=0;
		if(o1.isOnline()){
			o1flag=1;
		}
		if(o2.isOnline()){
			o2flag=1;
		}
		if(o1flag>o2flag){
			return 1;
		}else if(o1flag==o2flag){
			return 0;
		}else{
			return -1;
		}
	}
}
