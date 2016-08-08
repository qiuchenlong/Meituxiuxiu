package com.meituxiuxiu.android.photo.model;

import android.os.Environment;

public class Constants {

	/** 首先默认个文件保存路径 */
	public static final String SAVE_PIC_PATH=Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory().getAbsolutePath() : "/mnt/sdcard";//保存到SD卡
	public static final String SAVE_REAL_PATH = SAVE_PIC_PATH+ "/meituxiuxiu/";//保存的确切位置
	
	public static final int BYTES_PER_FLOAT = 4;
	
}
