package com.meituxiuxiu.android.ui;

import android.app.Activity;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meituxiuxiu.android.R;

public class AboutActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		
		/**set title bar*/
		setTitleBar(R.drawable.icon_back_selector, "返回", "关于美图秀秀", null, 0);
		
		
		ImageView imageView2 = (ImageView) findViewById(R.id.activity_about_imageview);  
        Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.mtxx_logo)).getBitmap();  
       
        Matrix matrix = new Matrix();
        matrix.postScale(2.0f, 2.0f, 0f, 0f);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        
        imageView2.setImageBitmap(createReflectedImage(bmp));
	}
	
	public static Bitmap createReflectedImage(Bitmap originalImage)  
    {  
        int width = originalImage.getWidth();  
        int height = originalImage.getHeight();  
        Matrix matrix = new Matrix();  
        // 实现图片翻转90度  
        matrix.preScale(1, -1);  
        // 创建倒影图片（是原始图片的一半大小）  
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);  
        // 创建总图片（原图片 + 倒影图片）  
        Bitmap finalReflection = Bitmap.createBitmap(width, (height + height / 5), Config.ARGB_8888);  
        // 创建画布  
        Canvas canvas = new Canvas(finalReflection);  
        canvas.drawBitmap(originalImage, 0, 0, null);  
        //把倒影图片画到画布上  
        canvas.drawBitmap(reflectionImage, 0, height + 1, null);  
        Paint shaderPaint = new Paint();  
        //创建线性渐变LinearGradient对象  
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, finalReflection.getHeight() + 1, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);  
        shaderPaint.setShader(shader);
        shaderPaint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));  
        //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果。  
        canvas.drawRect(0, height + 1, width, finalReflection.getHeight(), shaderPaint);  
        return finalReflection;  
    }

	private void setTitleBar(int leftResId, String leftStr, String titleStr, String rightStr, int rightResId){
		ImageView leftIcon = (ImageView) findViewById(R.id.widget_common_top_title_layout_left_icon);
		TextView leftWord = (TextView) findViewById(R.id.widget_common_top_title_layout_left_word);
		TextView title = (TextView) findViewById(R.id.widget_common_top_title_layout_title);
		TextView rightWord = (TextView) findViewById(R.id.widget_common_top_title_layout_right_word);
		ImageView rightIcon = (ImageView) findViewById(R.id.widget_common_top_title_layout_right_icon);
		
		LinearLayout leftLayout = (LinearLayout) findViewById(R.id.widget_common_top_title_layout_left_layout);
		LinearLayout rightLayout = (LinearLayout) findViewById(R.id.widget_common_top_title_layout_right_layout);
		
		/**判断是否要显示控件*/
		if(leftResId == 0){
			leftIcon.setVisibility(View.GONE);
		}else{
			leftIcon.setImageResource(leftResId);
		}
		
		if("".equals(leftStr) || leftStr == null){
			leftWord.setVisibility(View.GONE);
		}else{
			leftWord.setText(leftStr);
		}
		
		if("".equals(titleStr) || titleStr == null){
			title.setVisibility(View.GONE);
		}else{
			title.setText(titleStr);
		}
		
		if("".equals(rightStr) || rightStr == null){
			rightWord.setVisibility(View.GONE);
		}else{
			rightWord.setText(rightStr);
		}
		
		if(rightResId == 0){
			rightIcon.setVisibility(View.GONE);
		}else{
			rightIcon.setImageResource(rightResId);
		}
		
		
		if(leftIcon.getVisibility() == View.VISIBLE || leftWord.getVisibility() == View.VISIBLE){
			leftLayout.setOnClickListener(this);
		}
		if(rightIcon.getVisibility() == View.VISIBLE || rightWord.getVisibility() == View.VISIBLE){
			rightLayout.setOnClickListener(this);
		}
	}
		
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.widget_common_top_title_layout_left_layout:
			finish();
			break;
		
		}
	}
	
}
