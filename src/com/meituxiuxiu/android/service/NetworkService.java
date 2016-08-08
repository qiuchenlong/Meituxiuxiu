package com.meituxiuxiu.android.service;

import com.meituxiuxiu.android.AppConfig;
import com.meituxiuxiu.android.AppContext;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.broadcast.NetworkBroadcastReceiver;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.utils.TDevice;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * 判断网络状态（后台服务）
 * 
 * @author qiuchenlong on 2016.04.08
 *
 */
public class NetworkService extends IntentService {

	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	public static boolean isEnableNetwork = false;
	
	public NetworkService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public NetworkService() {
		// TODO Auto-generated constructor stub
		super("NetworkService");
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		String ALARM_ACTION = NetworkBroadcastReceiver.ACTION_NETWORK_BROADCASTRECEIVER;
		Intent intent = new Intent(ALARM_ACTION);
		pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		
		// autoUpdate
		if(AppContext.getBoolean(Constant.AUTO_UPDATE_NETWORK)){
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			long timeToRefresh = SystemClock.elapsedRealtime() + 2 * 1000;
			alarmManager.setInexactRepeating(alarmType, timeToRefresh, 2 * 1000, pendingIntent);
		}else{
			alarmManager.cancel(pendingIntent);
		}
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				autoUpdateNetWork();
			}
		});
		t.start();
		
		
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void autoUpdateNetWork() {
		// TODO Auto-generated method stub

		
		isEnableNetwork = isNetworkAvailable(getApplicationContext());
		
		// 检查网络状况
//		System.out .println("当前网络状况是否可用11=" + TDevice.isWifiOpen());
//		
//		
//		System.out .println("------>>>>" + TDevice.getNetworkType());
		
//		isNetworkAvailable(getApplicationContext());
		
		stopSelf();
		
	}
	
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		// TODO Auto-generated method stub

//		// autoUpdate
//		int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
//		long timeToRefresh = SystemClock.elapsedRealtime() + 2 * 1000;
//		alarmManager.setInexactRepeating(alarmType, timeToRefresh, 2 * 1000, pendingIntent);
//
//		// 检查网络状况
//		System.out .println("当前网络状况是否可用11=" + isNetworkAvailable(getApplicationContext()));
//		
//		isEnableNetwork = isNetworkAvailable(getApplicationContext());
		
		
//		new Handler().post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				Toast.makeText(getApplicationContext(), "来自Meitu的提醒，当前网络不可用！", Toast.LENGTH_SHORT).show();
//			}
//		});
		
//		if(!isEnableNetwork){
//				Toast.makeText(getApplicationContext(), "来自Meitu的提醒，当前网络不可用！", Toast.LENGTH_SHORT).show();
//				Log.w("TAG", "111...");
//		} else {
//				Toast.makeText(getApplicationContext(), "恭喜你，网络恢复正常了！", Toast.LENGTH_SHORT).show();
//				Log.w("TAG", "000...");	
//		}
		
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
	
	
	/**
     * 检查当前网络是否可用
     * 
     * @param context
     * @return
     */
    
    public boolean isNetworkAvailable(Context context)
    {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
//                    System.out.println(i + "===状态===" + networkInfo[i].getState());
//                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
