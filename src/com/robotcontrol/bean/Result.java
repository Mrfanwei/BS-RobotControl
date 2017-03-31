package com.robotcontrol.bean;


import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Result implements Parcelable {
	private Bitmap dw;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Bitmap getDw() {
		return dw;
	}

	public void setDw(Bitmap dw) {
		this.dw = dw;
	}

	public static final Parcelable.Creator<Result> CREATOR = new Creator<Result>() {
		public Result createFromParcel(Parcel source) {
			Result pb = new Result();
			pb.name = source.readString();
			pb.dw = Bitmap.CREATOR.createFromParcel(source);
			return pb;
		}

		public Result[] newArray(int size) {
			return new Result[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(name);
		dw.writeToParcel(parcel, 0);
	}
}
