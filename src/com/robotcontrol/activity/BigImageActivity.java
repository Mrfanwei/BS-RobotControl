package com.robotcontrol.activity;

import java.io.File;

import com.robotcontrol.bean.Result;
import com.robotcontrol.utils.BroadcastReceiverRegister;
import com.robotcontrol.utils.Constants;
import com.ryanharter.viewpager.PagerAdapter;
import com.uk.co.senab.photoview.HackyViewPager;
import com.uk.co.senab.photoview.PhotoView;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class BigImageActivity extends Activity {

	private HackyViewPager image;
	private File file = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_big_image);
		file = new File(getExternalFilesDir(null)
				+ "/"
				+ getSharedPreferences("Receipt", MODE_PRIVATE).getString(
						"username", null) + "small");
		image = (HackyViewPager) findViewById(R.id.bigimage);
		image.setLocked(false);
		setviewpager(image);
		image.setCurrentItem(getIntent().getExtras().getInt("position"));
	}

	public void setviewpager(HackyViewPager viewPager) {

		viewPager.setAdapter(new PagerAdapter() {

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return file.list().length;
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				String[] fs = file.list();
				PhotoView imageView = new PhotoView(BigImageActivity.this);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath()
						+ "/" + fs[position], options);
				Matrix matrix = new Matrix();
				matrix.setRotate(-90);
				Bitmap bm = b.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), matrix, true);
				b.recycle();
				imageView.setImageBitmap(bm);
				container.addView(imageView, LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT);
				return imageView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView((View) object);
			}
		});
		viewPager.setOffscreenPageLimit(3);

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	protected void onDestroy() {
		super.onDestroy();
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public enum mode {
		None, Drag, Zoom;
	}
}
