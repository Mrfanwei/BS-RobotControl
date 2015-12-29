package com.robotcontrol.animaction;

import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @author Roger Zhang
 */

public class TVOffAnimation extends Animation {

	private int halfWidth;

	private int halfHeight;

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {

		super.initialize(width, height, parentWidth, parentHeight);
		setDuration(1000);
		setFillAfter(true);
		halfWidth = width / 2;
		halfHeight = height / 2;
		setInterpolator(new AccelerateDecelerateInterpolator());

	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		final Matrix matrix = t.getMatrix();
		
			matrix.preScale(1 * interpolatedTime, 1 * interpolatedTime, 0, 0);
		
		t.setAlpha(interpolatedTime);
	}
}