package com.meituxiuxiu.android;

import com.meituxiuxiu.android.base.BaseApplication;

public class AppContext extends BaseApplication{

	private static AppContext instance;
	
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		instance = this;
	}
	
	/**
	 * 获得当前app运行的AppContext
	 * 
	 * @return
	 */
	public static AppContext getInstance(){
		return instance;
	}
}
