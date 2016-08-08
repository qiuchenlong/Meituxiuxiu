package com.meituxiuxiu.android.base;

import com.meituxiuxiu.android.R;
import com.samsung.android.sdk.multiwindow.SMultiWindow;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseApplication extends Application {
	
	
	
	private static String lastToast = "";
    private static long lastToastTime;
	
	private SMultiWindow mMultiWindow = null;
	
	private static BaseApplication meituxiuxiu;
	
	public static synchronized BaseApplication context(){
		return meituxiuxiu;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		meituxiuxiu = this;
		
		/** 初始化分屏窗口SDK */
		
		mMultiWindow = new SMultiWindow();
		
		/** 检查是否支持分屏 */
		if(mMultiWindow.isFeatureEnabled(SMultiWindow.MULTIWINDOW)){
//			Toast.makeText(this, "诚邀体验三星多窗口功能！", Toast.LENGTH_SHORT).show();
		}else{
//			Toast.makeText(this, "此设备不支持多窗口功能！", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	/**K-V（字符串）*/
	public static boolean putString(String key, String value){
		SharedPreferences settings = meituxiuxiu.getSharedPreferences("Mt_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	
	public static String getString(String key){
		SharedPreferences settings = meituxiuxiu.getSharedPreferences("Mt_info", MODE_PRIVATE);
		return settings.getString(key, "");
	}
	
	
	/**K-V（布尔值）*/
	public static boolean putBoolean(String key, boolean value){
		SharedPreferences settings = meituxiuxiu.getSharedPreferences("Mt_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	
	public static boolean getBoolean(String key){
		SharedPreferences settings = meituxiuxiu.getSharedPreferences("Mt_info", MODE_PRIVATE);
		return settings.getBoolean(key, false);
	}
	
	
	public static void showToast(int message) {
        showToast(message, Toast.LENGTH_LONG, 0);
    }

    public static void showToast(String message) {
        showToast(message, Toast.LENGTH_LONG, 0, Gravity.BOTTOM);
    }

    public static void showToast(int message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon);
    }

    public static void showToast(String message, int icon) {
        showToast(message, Toast.LENGTH_LONG, icon, Gravity.BOTTOM);
    }

    public static void showToastShort(int message) {
        showToast(message, Toast.LENGTH_SHORT, 0);
    }

    public static void showToastShort(String message) {
        showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
    }

    public static void showToastShort(int message, Object... args) {
        showToast(message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM, args);
    }

    public static void showToast(int message, int duration, int icon) {
        showToast(message, duration, icon, Gravity.BOTTOM);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity) {
        showToast(context().getString(message), duration, icon, gravity);
    }

    public static void showToast(int message, int duration, int icon,
                                 int gravity, Object... args) {
        showToast(context().getString(message, args), duration, icon, gravity);
    }

    public static void showToast(String message, int duration, int icon,
                                 int gravity) {
        if (message != null && !message.equalsIgnoreCase("")) {
            long time = System.currentTimeMillis();
            if (!message.equalsIgnoreCase(lastToast)
                    || Math.abs(time - lastToastTime) > 2000) {
                View view = LayoutInflater.from(context()).inflate(
                        R.layout.view_toast, null);
                ((TextView) view.findViewById(R.id.title_tv)).setText(message);
                if (icon != 0) {
                    ((ImageView) view.findViewById(R.id.icon_iv))
                            .setImageResource(icon);
                    ((ImageView) view.findViewById(R.id.icon_iv))
                            .setVisibility(View.VISIBLE);
                }
                Toast toast = new Toast(context());
                toast.setView(view);
                if (gravity == Gravity.CENTER) {
                    toast.setGravity(gravity, 0, 0);
                } else {
                    toast.setGravity(gravity, 0, 35);
                }

                toast.setDuration(duration);
                toast.show();
                lastToast = message;
                lastToastTime = System.currentTimeMillis();
            }
        }
    }
	
	
}
