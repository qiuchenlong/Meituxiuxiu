package com.meituxiuxiu.android.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.utils.Util;

public class RoundImageView extends ImageView {

	private int type;
	
	private Bitmap mBitmap;
	private int mRadius;
	private int mWidth;
	private int mHeight;
	
	public RoundImageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public RoundImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	
	
	public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomImageView, defStyle, 0);
		
		int n = a.getIndexCount();
		for(int i=0; i<n; i++){
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.CustomImageView_src:
				mBitmap = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
				mBitmap = Bitmap.createBitmap(mBitmap);
				break;
			case R.styleable.CustomImageView_type:
				type = a.getInt(attr, 0);
				break;
			case R.styleable.CustomImageView_borderRadius:
				mRadius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f,  
	                    getResources().getDisplayMetrics()));// 默认为10DP
				break;
			}
		}
		a.recycle();
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		
		if(specMode == MeasureSpec.EXACTLY){
			mWidth = specSize;
		}else{
			int desireByImg = getPaddingLeft() + getPaddingRight()
					+ mBitmap.getWidth();
			if (specMode == MeasureSpec.AT_MOST)// wrap_content
			{
				mWidth = Math.min(desireByImg, specSize);
			} else {
				mWidth = desireByImg;
			}
		}
		/*** 
         * 设置高度 
         */  
  
        specMode = MeasureSpec.getMode(heightMeasureSpec);  
        specSize = MeasureSpec.getSize(heightMeasureSpec);  
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate  
        {  
            mHeight = specSize;  
        } else  
        {  
            int desire = getPaddingTop() + getPaddingBottom()  
                    + mBitmap.getHeight();  
  
            if (specMode == MeasureSpec.AT_MOST)// wrap_content  
            {  
                mHeight = Math.min(desire, specSize);  
            } else  
                mHeight = desire;  
        }  
  
        setMeasuredDimension(mWidth, mHeight);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
//		switch (type) {
//		// 如果是TYPE_CIRCLE绘制圆形
//		case TYPE_CIRCLE:
//
//			int min = Math.min(mWidth, mHeight);
//			/**
//			 * 长度如果不一致，按小的值进行压缩
//			 */
//			mSrc = Bitmap.createScaledBitmap(mSrc, min, min, false);
//
//			canvas.drawBitmap(createCircleImage(mSrc, min), 0, 0, null);
//			break;
//		case TYPE_ROUND:
			canvas.drawBitmap(createRoundConerImage(mBitmap), 0, 0, null);
//			break;
//		}
	}
	
	
	/** 
     * 根据原图添加圆角 
     *  
     * @param source 
     * @return 
     */  
    private Bitmap createRoundConerImage(Bitmap source)  
    {  
        final Paint paint = new Paint();  
        paint.setAntiAlias(true);  
        Bitmap target = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);  
        Canvas canvas = new Canvas(target);  
        RectF rect = new RectF(0+20, 0+20, source.getWidth()+20, source.getHeight()+20);  
        canvas.drawRoundRect(rect, mRadius, mRadius, paint);  
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));  
        canvas.drawBitmap(source, 20, 20, paint);  
        return target;  
    }
    
    public void setmBitmap(Bitmap mBitmap){
    	this.mBitmap = mBitmap;

    	
//    	mWidth = mBitmap.getWidth();
//    	mHeight = mBitmap.getHeight();

    	Log.e("TAG", "setmBitmap...");
    	postInvalidate();
    }
	
	
}
