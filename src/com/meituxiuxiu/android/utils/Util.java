package com.meituxiuxiu.android.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.photo.model.Constants;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

public class Util {

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	

	public static void saveFile(Bitmap bm, String fileName) throws IOException { // , String path
		String subForder = Constants.SAVE_REAL_PATH ; //+ path;
		File foder = new File(subForder);
		if (!foder.exists()) {
			foder.mkdirs();
		}
		File myCaptureFile = new File(subForder, fileName);
		if (!myCaptureFile.exists()) {
			myCaptureFile.createNewFile();
		}
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
		
		notifyPicture(BaseApplication.context(), myCaptureFile);
	}

	public static void notifyPicture(Context context, File file){
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		context.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！，记得要传你更新的file哦

	}
	
	
	// 中文字符长度问题的解决方案
	// 基本思想是根据中文编码的编码区间，判断字符是否为中文字符，然后再作进一步的处理。
	public static int getChineseLength( String name , String endcoding )
            throws Exception{
        int len = 0 ; //定义返回的字符串长度
        int j = 0 ;
        //按照指定编码得到byte[]
        byte [] b_name = name.getBytes( endcoding ) ;
        while ( true ){
            short tmpst = (short) ( b_name[ j ] & 0xF0 ) ;
            if ( tmpst >= 0xB0 ){
                if ( tmpst < 0xC0 ){
                    j += 2 ;
                    len += 2 ;
                }
                else if ( ( tmpst == 0xC0 ) || ( tmpst == 0xD0 ) ){
                    j += 2 ;
                    len += 2 ;
                }
                else if ( tmpst == 0xE0 ){
                    j += 3 ;
                    len += 2 ;
                }
                else if ( tmpst == 0xF0 ){
                    short tmpst0 = (short) ( ( (short) b_name[ j ] ) & 0x0F ) ;
                    if ( tmpst0 == 0 ){
                        j += 4 ;
                        len += 2 ;
                    }
                    else if ( ( tmpst0 > 0 ) && ( tmpst0 < 12 ) ){
                        j += 5 ;
                        len += 2 ;
                    }
                    else if ( tmpst0 > 11 ){
                        j += 6 ;
                        len += 2 ;
                    }
                }
            }
            else{
                j += 1 ;
                len += 1 ;
            }
            if ( j > b_name.length - 1 ){
                break ;
            }
        }
        return len ;
    }
	
	
}
