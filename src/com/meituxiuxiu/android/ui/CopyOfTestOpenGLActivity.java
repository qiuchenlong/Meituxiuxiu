package com.meituxiuxiu.android.ui;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.surfaceview.AirHockeyRenderer;
import com.meituxiuxiu.android.ui.opengl.surfaceview.MyGLSurfaceView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CopyOfTestOpenGLActivity extends Activity {

	
	private GLSurfaceView glSurfaceView = null;
	private boolean rendererSet = false;
	
	private Bitmap bitmap;
	
	private byte[] bitmapByte;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_test_opengl);
		glSurfaceView = new GLSurfaceView(this);
		
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();

		boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 
				&& (Build.FINGERPRINT.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator") 
						|| Build.MODEL.contains("Android SDK built for x86")));
		
		final AirHockeyRenderer airHockeyRenderer = new AirHockeyRenderer(this);
		
		if (supportsEs2) {
			glSurfaceView.setEGLContextClientVersion(2);

			// glSurfaceView.setRenderer(new AirHockeyRenderer(this));
			glSurfaceView.setRenderer(airHockeyRenderer);

			rendererSet = true;
		} else {
			Toast.makeText(this, "This Device does not support OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
			return;
		}
		
		setContentView(glSurfaceView);
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收2时的length="+bitmapByte.length);
//		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//		matrix.postScale(2.0f, 2.0f);
//		
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//		
//		mView = new MyGLSurfaceView(this, bitmap);
//		mView.requestFocus();
//		mView.setFocusableInTouchMode(true);
//		
////		LinearLayout ll = (LinearLayout) findViewById(R.id.container);
////		ll.addView(mView);
//		
//		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_test_image_layout);
//		rl.addView(mView);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (rendererSet) {
			glSurfaceView.onResume();
		}
	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (rendererSet) {
			glSurfaceView.onPause();
		}
	}
	
}
