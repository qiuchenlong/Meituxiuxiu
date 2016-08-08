package com.meituxiuxiu.android.broadcast;

import com.meituxiuxiu.android.service.NetworkService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

	public static final String ACTION_NETWORK_BROADCASTRECEIVER="com.meituxiuxiu.android.intent.action.NetworkBroadcastReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent serviceIntent = new Intent(context, NetworkService.class);
		context.startService(serviceIntent); // 开启消息Service
	}

}
