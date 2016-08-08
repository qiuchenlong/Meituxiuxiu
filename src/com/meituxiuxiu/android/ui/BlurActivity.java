package com.meituxiuxiu.android.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;

/**
 * 背景虚化（高斯模糊）
 * 
 * @author qiuchenlong on 2016.04.23
 *
 */
public class BlurActivity extends Activity {

	private static final String TAG = BlurActivity.class.getName();
	
	private Bitmap bitmap;
	private byte[] bitmapByte;
	
	private FrameSurfaceView mView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blur);
		
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收2时的length="+bitmapByte.length);
		
		
		// get sharedpreferences
		String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
		bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
				
		
		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		
		
		
		ImageView imageview = (ImageView) findViewById(R.id.activity_blur_imageview);
		
		imageview.setImageDrawable(BlurImages(bitmap, BlurActivity.this));
		
		
//		mView = new FrameSurfaceView(this, bitmap);
//		mView.requestFocus();
//		mView.setFocusableInTouchMode(true);
//		
//		
//		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_blur_image_layout);
//		rl.addView(mView);
	
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		mView.onResume();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		mView.onPause();
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
	
	
	
	
	/** 水平方向模糊度 */
	 private static float hRadius = 5;
	 /** 竖直方向模糊度 */
	 private static float vRadius = 5;
	 /** 模糊迭代度 */
	 private static int iterations =3;

	 /**
	  * 图片高斯模糊处理
	  * 
	  */
	 public static Drawable BlurImages(Bitmap bmp, Context context) {

	  int width = bmp.getWidth();
	  int height = bmp.getHeight();
	  int[] inPixels = new int[width * height];
	  int[] outPixels = new int[width * height];
	  Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	  bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
	  for (int i = 0; i < iterations; i++) {
	   blur(inPixels, outPixels, width, height, hRadius);
	   blur(outPixels, inPixels, height, width, vRadius);
	  }
	  blurFractional(inPixels, outPixels, width, height, hRadius);
	  blurFractional(outPixels, inPixels, height, width, vRadius);
	  bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
	  Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
	  return drawable;
	 }

	 /**
	  * 图片高斯模糊算法
	  */
	 public static void blur(int[] in, int[] out, int width, int height, float radius) {
	  int widthMinus1 = width - 1;
	  int r = (int) radius;
	  int tableSize = 2 * r + 1;
	  int divide[] = new int[256 * tableSize];

	  for (int i = 0; i < 256 * tableSize; i++)
	   divide[i] = i / tableSize;

	  int inIndex = 0;

	  for (int y = 0; y < height; y++) {
	   int outIndex = y;
	   int ta = 0, tr = 0, tg = 0, tb = 0;

	   for (int i = -r; i <= r; i++) {
	    int rgb = in[inIndex + clamp(i, 0, width - 1)];
	    ta += (rgb >> 24) & 0xff;
	    tr += (rgb >> 16) & 0xff;
	    tg += (rgb >> 8) & 0xff;
	    tb += rgb & 0xff;
	   }

	   for (int x = 0; x < width; x++) {
	    out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];

	    int i1 = x + r + 1;
	    if (i1 > widthMinus1)
	     i1 = widthMinus1;
	    int i2 = x - r;
	    if (i2 < 0)
	     i2 = 0;
	    int rgb1 = in[inIndex + i1];
	    int rgb2 = in[inIndex + i2];

	    ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
	    tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
	    tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
	    tb += (rgb1 & 0xff) - (rgb2 & 0xff);
	    outIndex += height;
	   }
	   inIndex += width;
	  }
	 }
	 
	 /**
	  * 图片高斯模糊算法
	  * 
	  */
	 public static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
	  radius -= (int) radius;
	  float f = 1.0f / (1 + 2 * radius);
	  int inIndex = 0;

	  for (int y = 0; y < height; y++) {
	   int outIndex = y;

	   out[outIndex] = in[0];
	   outIndex += height;
	   for (int x = 1; x < width - 1; x++) {
	    int i = inIndex + x;
	    int rgb1 = in[i - 1];
	    int rgb2 = in[i];
	    int rgb3 = in[i + 1];

	    int a1 = (rgb1 >> 24) & 0xff;
	    int r1 = (rgb1 >> 16) & 0xff;
	    int g1 = (rgb1 >> 8) & 0xff;
	    int b1 = rgb1 & 0xff;
	    int a2 = (rgb2 >> 24) & 0xff;
	    int r2 = (rgb2 >> 16) & 0xff;
	    int g2 = (rgb2 >> 8) & 0xff;
	    int b2 = rgb2 & 0xff;
	    int a3 = (rgb3 >> 24) & 0xff;
	    int r3 = (rgb3 >> 16) & 0xff;
	    int g3 = (rgb3 >> 8) & 0xff;
	    int b3 = rgb3 & 0xff;
	    a1 = a2 + (int) ((a1 + a3) * radius);
	    r1 = r2 + (int) ((r1 + r3) * radius);
	    g1 = g2 + (int) ((g1 + g3) * radius);
	    b1 = b2 + (int) ((b1 + b3) * radius);
	    a1 *= f;
	    r1 *= f;
	    g1 *= f;
	    b1 *= f;
	    out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
	    outIndex += height;
	   }
	   out[outIndex] = in[width - 1];
	   inIndex += width;
	  }
	 }
	 public static int clamp(int x, int a, int b) {
	  return (x < a) ? a : (x > b) ? b : x;
	 }
	
	
	
}
