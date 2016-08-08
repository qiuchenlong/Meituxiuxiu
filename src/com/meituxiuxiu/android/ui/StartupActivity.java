package com.meituxiuxiu.android.ui;

import java.lang.reflect.Field;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotDialogListener;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.meituxiuxiu.android.AppConfig;
import com.meituxiuxiu.android.AppContext;
import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.utils.BadgeUtil;
import com.meituxiuxiu.android.utils.TDevice;

/**
 * 闪屏界面
 * 
 * @author qiuchenlong on 2016.03.30
 *
 */
public class StartupActivity extends Activity {

	//必须使用，Activity启动页
    private final static String lancherActivityClassName = StartupActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startup);
		
		
		AdManager.getInstance(this).init("dcb313664293d49b", "bdf623ad637522a3", true);
		SpotManager.getInstance(this).loadSpotAds();
		
//		SpotManager.getInstance(this).showSpotAds(this);
//		SpotManager.getInstance(this).loadSplashSpotAds();
		
		
		
		
		if(!BaseApplication.getBoolean(Constant.IS_NOT_FIRST_STARTUP)){ // 如果不是不是首次进入==首次进入
			// 创建快捷方式
			Intent addIntent=new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
	        Parcelable icon=Intent.ShortcutIconResource.fromContext(this, R.drawable.mtxx_logo); //获取快捷键的图标
	        Intent myIntent=new Intent(this, StartupActivity.class);
	        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "美图秀秀");//快捷方式的标题
	        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);//快捷方式的图标
	        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);//快捷方式的动作
	        sendBroadcast(addIntent);//发送广播
	        
//	        Toast.makeText(StartupActivity.this, "已生成快捷方式，可以快速进入应用哟", Toast.LENGTH_SHORT).show();
	        
	     // 跳转至引导页
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					startActivity(new Intent(StartupActivity.this, GuideActivity.class)); //
					StartupActivity.this.finish();
				}
			}, 3000);
	        
			BaseApplication.putBoolean(Constant.IS_NOT_FIRST_STARTUP, true);
			// 设置界面 画质设置 普通
			BaseApplication.putString(Constant.SET_HUAZI_INFO, "普通");
			
		}else{
			
			
			if(isConnect(StartupActivity.this)){
			
			// 加载广告页
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					loadAd();
				}
			}, 1500);
			
			}else{
				
			// 跳转至首页
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					startActivity(new Intent(StartupActivity.this, MainActivity.class)); //TutorialOnFaceDetect
					StartupActivity.this.finish();
				}
			}, 3000);
			
			}
		}
		
		
		
		// 设置桌面图标小红点
		
		BadgeUtil.setBadgeCount(this, 0);
		
		
	}
	
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		int currentVersion = TDevice.getVersionCode();
		
		AppContext.putBoolean(AppConfig.KEY_FRITST_START, true);
		
		
	}





	private void loadAd() {
		// TODO Auto-generated method stub
		SpotManager.getInstance(StartupActivity.this).showSplashSpotAds(StartupActivity.this, MainActivity.class);
	}
	
	
	/**
	 * 网络状态的判断
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnect(Context context) {
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.v("error", e.toString());
		}
		return false;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/**屏蔽back键*/
		if(keyCode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.onKeyDown(keyCode, event);
	}
    

}
