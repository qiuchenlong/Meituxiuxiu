package com.meituxiuxiu.android.service;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.meituxiuxiu.android.helper.Hanzi2PinyinHelper;

import android.app.Service;
import android.content.Intent;
import android.location.Geocoder;
import android.os.IBinder;
import android.util.Log;

/**
 * 百度地图 定位服务
 * 
 * @author qiuchenlong on 2016.04.21
 *
 */
public class LocationService extends Service implements BDLocationListener{

	public final static String TAG = "LocationService";
	
	public LocationClient mLocationClient = null;
	
	public double longitude = 0, latitude = 0;
    public float radius = 1000;
    
    public boolean isFirstLoc = true;// 是否首次定位
	
    
//    public static String addressStr;
    
    
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
        Log.e(TAG, "onCreate...");
        // 在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        
        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClie
        mLocationClient.registerLocationListener(this); // 注册监听函数
        
        // 定位初始化
        LocationClientOption option = new LocationClientOption();
        // 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationMode.Hight_Accuracy);
        // 可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll"); // 设置坐标类型
      //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span=1000;
        option.setScanSpan(span);
      //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
      //可选，默认false,设置是否使用gps
        option.setOpenGps(true);// 打开gps
        
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        
       
        mLocationClient.setLocOption(option);
       
        // 开始定位
        mLocationClient.start();
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		
		mSearch.destroy();
		mLocationClient.stop();
	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		// TODO Auto-generated method stub
		
		
		
		if (arg0 == null)
            return;

        longitude = arg0.getLongitude();
        latitude = arg0.getLatitude();
        radius = arg0.getRadius();
        isFirstLoc = false;
        
        Log.e("TAG", "定位结果：纬度="+latitude+", 经度="+longitude);
        
        // （反向）地理编码
        Log.e("TAG", "反向地理编码...");
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(listener);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(latitude, longitude)));
//        mSearch.geocode(new GeoCodeOption().city("北京").address("海淀区上地十街10号"));
//        mSearch.destroy();
        
        
        
//        Option.getOption(this).setLocation(longitude, latitude);
	}
	
	GeoCoder mSearch;
	
	// 地理编码监听器
	private OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {  
	    public void onGetGeoCodeResult(GeoCodeResult result) {  
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	        	Log.e(TAG, "没有检索到结果 ...  ");
	        }  
	        Log.e(TAG, "获取地理编码结果 ...  ");
	    }  
	 
	    @Override  
	    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {  
	        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	        	Log.e(TAG, "没有找到检索结果...  ");
	        }  
	        Log.e(TAG, "获取反向地理编码结果...  ");
	        String Address = result.getAddress();
	        
	        String province = result.getAddressDetail().province; // 省份
	        String city = result.getAddressDetail().city; // 城市
	        String district = result.getAddressDetail().district; // 地区、县级市
	        String street = result.getAddressDetail().street; // 街道
	        String streetNumber = result.getAddressDetail().streetNumber; // 街道门牌号
	        
	        Log.e(TAG, "Address="+Address);
	        
	        Log.d(TAG, "province="+province+", city="+city+", district="+district+", street="+street+
	        		", streetNumber="+streetNumber);
	        
	        
	        // 汉字转拼音中...
	        if(Hanzi2PinyinHelper.getPinyinFirstToUpperCase(city).contains(",")){
	        	Log.e("TAG", "1"+Hanzi2PinyinHelper.getPinyinFirstToUpperCase(city).split(",")[0]);
//	        	addressStr = Hanzi2PinyinHelper.getPinyinFirstToUpperCase(city).split(",")[0];
	        }else{
	        	Log.e("TAG", "2"+Hanzi2PinyinHelper.getPinyinFirstToUpperCase(city));
//	        	addressStr = Hanzi2PinyinHelper.getPinyinFirstToUpperCase(city);
	        }
	        
	        
	        
	    }
	};

}
