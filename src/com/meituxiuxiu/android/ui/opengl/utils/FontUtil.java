package com.meituxiuxiu.android.ui.opengl.utils;

import com.meituxiuxiu.android.ui.FrameActivity;
import com.meituxiuxiu.android.utils.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;

/**
 * OpenGL ES 文字工具类
 * 
 * @author qiuchenlong on 2016.04.10
 *
 */
public class FontUtil {
	
	public static int cIndex = 0;
	static final float textSize = 65;
	static int R = 255;
	static int G = 255;
	static int B = 255;

	/**生成文本纹理图*/
	public static Bitmap generateWLT(Context context ,String[] str, int width, int height){
		Paint paint = new Paint();
		paint.setARGB(255, 0, 0, 0);
		paint.setTextSize(FrameActivity.wordsTextSize);
//		String familyName = "黑体";
//		Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
//		paint.setTypeface(font);
		paint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Arial.ttf")); // 设置自己的字体
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		for(int i=0; i<str.length; i++){
			String s = str[0];
			int len = str[0].length();
			try {
				len = Util.getChineseLength(s, "utf-8");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			canvas.drawText(str[i].toString(), (float) ((width-FrameActivity.wordsTextSize*len/2)/2.0), FrameActivity.wordsTextSize * (i+1), paint); // 居中显示文字(中文混排会出现长度不一致)
		}
//		paint.setARGB(255, 255, 0, 0);
//		canvas.drawRect(0.0f, 0.0f, textSize * str[0].length(), textSize, paint);
		return bmp;
	}
	
	public static String[] content = {
	"The earth"  //"MeiTu,Inc"
	};
	
	
	public static void setContent(String[] content) {
		FontUtil.content = content;
	}

	public static String[] getContent(int lenght, String[] content){
		String[] result = new String[lenght+1];
		for(int i=0;i<=lenght;i++){
			result[i] = content[i];
		}
		return result;
	}
	
	public static void updateRGB(){
		R = (int)(255*Math.random());
		G = (int)(255*Math.random());
		B = (int)(255*Math.random());
	}
	
}
