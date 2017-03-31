package com.robotcontrol.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class Remind extends Task {

	private int msg;

	public int getMsg() {
		return msg;
	}

	public void setMsg(int msg) {
		this.msg = msg;
	}

	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(super.getContent());
		out.writeString(super.getTitle());
		out.writeString(super.getSettime());
		out.writeInt(super.getId());
		out.writeInt(msg);
	}

	public Remind(Parcel in) {
		super.setContent(in.readString());
		super.setTitle(in.readString());
		super.setSettime(in.readString());
		super.setId(in.readInt());
		msg = in.readInt();
	}

	public Remind() {
	}

	public static final Parcelable.Creator<Remind> CREATOR = new Creator<Remind>() {
		@Override
		public Remind[] newArray(int size) {
			return new Remind[size];
		}

		@Override
		public Remind createFromParcel(Parcel in) {
			return new Remind(in);
		}
	};
}
