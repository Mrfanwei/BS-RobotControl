package com.robotcontrol.animaction;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ClickAnimation extends Animation {

	private final float mFromDegrees;
	// 结束角度
	private final float mToDegrees;
	// 中心点
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	// 是否需要扭曲
	private final boolean mReverse;
	// 摄像头
	private Camera mCamera;

	public ClickAnimation(float fromDegrees, float toDegrees, float centerX,
			float centerY, float depthZ, boolean reverse) {
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		mReverse = reverse;
	}

	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
		setDuration(500);
	}

	// 生成Transformation
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		final float fromDegrees = mFromDegrees;
		// 生成中间角度

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		 if (mReverse) {
		 
		 } else {
		 
		 }
		float degrees =0.0f;
		if (mReverse) {
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
			degrees= fromDegrees * (1 - interpolatedTime);
			camera.rotateX(degrees);
		} else {
			degrees = mToDegrees * interpolatedTime;
			if (interpolatedTime >= 0.95) {
				camera.rotateX(-10);
			} else {
				camera.rotateX(degrees);
				camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
			}
		}

		// 取得变换后的矩阵
		camera.getMatrix(matrix);
		camera.restore();
//
//		matrix.preTranslate(-centerX, -centerY);
//		matrix.postTranslate(centerX, centerY);

	}

}
