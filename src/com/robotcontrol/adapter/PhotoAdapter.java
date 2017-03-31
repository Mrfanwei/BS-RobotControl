package com.robotcontrol.adapter;

import com.robotcontrol.activity.PhotoActivity;
import com.robotcontrol.activity.R;
import com.robotcontrol.utils.ImageLoader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PhotoAdapter extends BaseAdapter {

	private PhotoActivity activity;
	private String[] paths;
	private ImageLoader loader;
	private callback back;

	public PhotoAdapter(PhotoActivity activity, String[] paths,
			ImageLoader loader, callback back) {
		super();
		this.activity = activity;
		this.paths = paths;
		this.loader = loader;
		this.back = back;
	}

	@Override
	public int getCount() {
		return paths.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	LayoutInflater layout = null;

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (layout == null) {
			layout = LayoutInflater.from(activity);
		}
		if (convertView == null) {
			convertView = layout.inflate(R.layout.photo, null);
		}
		String convertpath = paths[position];
		ImageView image = (ImageView) convertView.findViewById(R.id.photo_img);
		TextView text = (TextView) convertView.findViewById(R.id.photo_name);
		final String name = convertpath.substring(
				convertpath.lastIndexOf("/") + 1, convertpath.length());
		text.setText(name);
		CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
		if (back.IslongClick()) {
			check.setVisibility(View.VISIBLE);
		} else {
			check.setVisibility(View.GONE);
		}
		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean ischecked) {
				if (ischecked) {
					activity.checked(name);
				} else {
					activity.notcheck(name);
				}
			}
		});
		check.setChecked(false);
		loader.loadImage(convertpath, image, false);
		return convertView;
	}

	public interface callback {
		public boolean IslongClick();
	}
}
