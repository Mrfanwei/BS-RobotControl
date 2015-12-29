package com.robotcontrol.bean;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Task implements Parcelable {

	private static final long serialVersionUID = -7060210544600464481L;
	private int id;
	private String settime;
	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private String content;
	private int isaway;

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

	public int getIsaway() {
		return isaway;
	}

	public void setIsaway(int isaway) {
		this.isaway = isaway;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(content);
		out.writeString(title);
		out.writeString(settime);
		out.writeInt(isaway);
		out.writeInt(id);

	}

	public Task(Parcel in) {
		content = in.readString();
		title = in.readString();
		settime = in.readString();
		isaway = in.readInt();
		id = in.readInt();
	}

	public Task() {
		// TODO Auto-generated constructor stub
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
