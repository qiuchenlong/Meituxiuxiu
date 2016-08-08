package com.meituxiuxiu.android.ui;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView222;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;

public class PinTuActivity extends Activity {

private static final String TAG = PinTuActivity.class.getName();
	
	private Bitmap bitmap;
	private byte[] bitmapByte;
	
	private FrameSurfaceView222 mView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blur);
		
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收2时的length="+bitmapByte.length);
//		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//		matrix.postScale(2.0f, 2.0f);
//		
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hemanting);
		
		mView = new FrameSurfaceView222(this, bitmap);
		mView.requestFocus();
		mView.setFocusableInTouchMode(true);
		
		
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_blur_image_layout);
		rl.addView(mView);
	
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mView.onResume();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mView.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
}
