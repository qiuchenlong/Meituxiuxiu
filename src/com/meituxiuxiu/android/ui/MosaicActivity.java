package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.helper.MosaicHelper;
import com.meituxiuxiu.android.ui.widget.BubbleSeekBar;
import com.meituxiuxiu.android.ui.widget.BubbleSeekBar.OnBubbleSeekBarChangeListener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * 马赛克页面
 * 
 * @author qiuchenlong on 2016.04.29
 *
 */
public class MosaicActivity extends Activity {

	private ImageView imageView;
	
	private Bitmap bitmap;
	private Bitmap bitmapTemp;
	private byte[] bitmapByte;
	
	
	private BubbleSeekBar bubbleSeekBar;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mosaic);
		
		/**获取传递来的bitmap字节数组*/
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收2时的length="+bitmapByte.length);
		
		
		// get sharedpreferences
		String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
		bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
		
		System.out.println("接收2时的length=--->>"+bitmapByte.length);
		
		
		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		
		imageView = (ImageView) findViewById(R.id.activity_mosaic_imageview);
		imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
		
		
		
		bubbleSeekBar = (BubbleSeekBar) findViewById(R.id.activity_auto_meihua_widget_bubbleseekbar);
		bubbleSeekBar.setOnBubbleSeekBarChangeListener(mOnColorBubbleSeekBarChangeListener);
		
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_auto_meihua_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MosaicActivity.this.finish();
			}
		});
		findViewById(R.id.activity_auto_meihua_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				bitmapTemp.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				bitmapByte = baos.toByteArray();
				System.out.println("发送2时的length="+bitmapByte.length);
				
//				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.Mosaic_ACTIVITY_REQUEST_CODE, intent);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				MosaicActivity.this.finish();
			}
		});
		
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		float orgX = event.getX();
		float orgY = event.getY();
		
		float positionX = orgX - imageView.getX();
		float positionY = orgY - imageView.getY();
		
		
		
		
		/*if(event.getAction() == MotionEvent.ACTION_MOVE){
			Log.w("TAG", "(int)event.getX()="+(int)event.getX());
			Log.w("TAG", "(int)event.getY()="+(int)event.getY());
			imageView.setImageBitmap(Bitmap.createBitmap(
					MosaicHelper.makeMosaic(bitmap, 
							null, 20)
					, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
		}*/
		return super.onTouchEvent(event);
	}
	
	
	
private OnBubbleSeekBarChangeListener mOnColorBubbleSeekBarChangeListener = new OnBubbleSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = (int) (seekBar.getProgress() ); //  / 20.0f
			Log.e("TAG","progress="+progress);
			
			
			Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
			matrix.postScale(2.0f, 2.0f);
			
			
//			bitmapTemp = Bitmap.createBitmap(
//					MosaicHelper.makeMosaic(bitmap, 
//							null, progress), 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			
			
			bitmapTemp = MosaicHelper.makeMosaic(bitmap, 
					null, progress);
			
			
			imageView.setImageBitmap(Bitmap.createBitmap(
					bitmapTemp, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
			
//			if(bitmap != null && !bitmap.isRecycled()){
//				bitmap.recycle();
//				bitmap = null;
//			}
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
//			bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		if(bitmapTemp != null && !bitmapTemp.isRecycled()){
			bitmapTemp.recycle();
			bitmapTemp = null;
		}
	}
	
	
}
