package com.meituxiuxiu.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MyImageView extends ImageView {

	private Paint mPaint;
	private int[] mPX = new int[2];
	private int[] mPY = new int[2];
	private int mDisplayStyle;
	
	private int size = 50;
	
	public MyImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		// 画边框  
        Rect rec = canvas.getClipBounds();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE); 
		mPaint.setStrokeWidth(5.0f);
		canvas.drawRect(rec, mPaint);
		
		if(mPX != null)
		for(int i=0; i<mPX.length; i++){
//			canvas.drawPoint(mPX[i], mPY[i], mPaint);
			canvas.drawRect(mPX[i]-size, mPY[i]-size, mPX[i]+size, mPY[i]+size, mPaint);
		}
		
//		canvas.drawRect(left, top, right, bottom, mPaint);
		
	}
	
	
	
	
	// set up detected face features for display 
	public void setDisplayPoints(int [] xx, int [] yy, int total, int style) { 
		 mDisplayStyle = style; 
		 mPX = null; 
		 mPY = null; 
	 
		if (xx != null && yy != null && total > 0) { 
		mPX = new int[total]; 
		mPY = new int[total]; 
		 
			for (int i = 0; i < total; i++) { 
			mPX[i] = xx[i]; 
			mPY[i] = yy[i]; 
			} 
		}
		
		invalidate();
		
	} 

}
