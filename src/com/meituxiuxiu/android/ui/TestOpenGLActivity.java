package com.meituxiuxiu.android.ui;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.surfaceview.MyGLSurfaceView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TestOpenGLActivity extends Activity {

	
	private MyGLSurfaceView mView;
	
	private Bitmap bitmap;
	
	private byte[] bitmapByte;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_opengl);
		
		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
		System.out.println("接收2时的length="+bitmapByte.length);
		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		mView = new MyGLSurfaceView(this, bitmap);
		mView.requestFocus();
		mView.setFocusableInTouchMode(true);
		
//		LinearLayout ll = (LinearLayout) findViewById(R.id.container);
//		ll.addView(mView);
		
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_test_image_layout);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("info", "menu create");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        renderer.setCurrentEffect(item.getItemId());
//        mEffectView.requestRender();
        return true;
    }
	
}
