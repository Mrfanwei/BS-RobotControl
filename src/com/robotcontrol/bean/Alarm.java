package com.robotcontrol.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Alarm extends Task {

	private String week;
	private int isaways;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getIsaways() {
		return isaways;
	}

	public void setIsaways(int isaways) {
		this.isaways = isaways;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(super.getContent());
		out.writeString(super.getTitle());
		out.writeString(super.getSettime());
		out.writeInt(super.getId());
		out.writeString(week);
		out.writeInt(isaways);
	}

	public Alarm(Parcel in) {
		super.setContent(in.readString());
		super.setTitle(in.readString());
		super.setSettime(in.readString());
		super.setId(in.readInt());
		week = in.readString();
		isaways = in.readInt();
	}

	public Alarm() {
	}

	public static final Parcelable.Creator<Alarm> CREATOR = new Creator<Alarm>() {
		@Override
		public Alarm[] newArray(int size) {
			return new Alarm[size];
		}

		@Override
		public Alarm createFromParcel(Parcel in) {
			return new Alarm(in);
		}
	};

}
