package com.robotcontrol.bean;

/**
 * Created by Administrator on 2015/10/15 0015.
 */
public class Robot {

	public int getRid() {
		return rid;
	}

	public void setRid(int rid) {
		this.rid = rid;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public int getController() {
		return controller;
	}

	public void setController(int controller) {
		this.controller = controller;
	}

	private String id;
	private String address;
	private int rid;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String rname;
	private boolean online;
	private String robot_serial;

	public String getRobot_serial() {
		return robot_serial;
	}

	public void setRobot_serial(String robot_serial) {
		this.robot_serial = robot_serial;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	private int controller;
	private air air;

	public air getAir() {
		return air;
	}

	public void setAir(air air) {
		this.air = air;
	}

	public enum air {
		nearby, bind
	}
}
