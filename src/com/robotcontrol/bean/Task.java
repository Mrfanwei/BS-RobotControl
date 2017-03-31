package com.robotcontrol.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {

	private int id;
	private String settime;
	private String title;
	private String content;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSettime() {
		return settime;
	}

	public void setSettime(String settime) {
		this.settime = settime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(content);
		out.writeString(title);
		out.writeString(settime);
		out.writeInt(id);
	}

	public Task(Parcel in) {
		content = in.readString();
		title = in.readString();
		settime = in.readString();
		id = in.readInt();
	}

	public Task() {
	}

	public static final Parcelable.Creator<Task> CREATOR = new Creator<Task>() {
		@Override
		public Task[] newArray(int size) {
			return new Task[size];
		}

		@Override
		public Task createFromParcel(Parcel in) {
			return new Task(in);
		}
	};
}
