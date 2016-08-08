package com.meituxiuxiu.android.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.common.SysOSUtil;
import com.google.gson.Gson;
import com.meituxiuxiu.android.AppConfig;
import com.meituxiuxiu.android.AppContext;
import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.bean.Update;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.jsonbean.AppInfo;
import com.meituxiuxiu.android.jsonbean.Info;
import com.meituxiuxiu.android.service.LocationService;
import com.meituxiuxiu.android.service.NetworkService;
import com.meituxiuxiu.android.ui.widget.RoundImageView;
import com.meituxiuxiu.android.utils.DialogHelp;
import com.meituxiuxiu.android.utils.UIHelper;
import com.meituxiuxiu.android.utils.XmlUtils;
import com.samsung.android.sdk.multiwindow.SMultiWindow;
import com.samsung.android.sdk.multiwindow.SMultiWindowActivity;
import com.samsung.android.sdk.multiwindow.SMultiWindowActivity.StateChangeListener;

/**
 * 首页界面
 * 
 * @author qiuchenlong on 2016.03.27
 *
 */
public class MainActivity extends Activity implements OnClickListener{

	
	/**顶部轮播图片*/
	private ViewPager viewPager; // android-support-v4中的滑动组件
	private List<GridView> gridViews; // 滑动的图片集合

	private List<View> dots; // 图片标题正文的那些点

	private TextView tv_title;
	private int currentItem = 0; // 当前图片的索引号
	
	// An ExecutorService that can schedule commands to run after a given delay,
	// or to execute periodically.
	private ScheduledExecutorService scheduledExecutorService;
	
	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			viewPager.setCurrentItem(currentItem);// 切换当前显示的图片
		};
	};
	
	private List<Map<String,Object>> listView;
	private int next = 0;
	private AdPageAdapter adapter;
	private List<View> gridViewlist = new ArrayList<View>();
	
//	private GridView mGridView;
//	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	// 图片封装为一个数组
/*    private int[] bgs = { R.drawable.item_bg_blue_a, R.drawable.item_bg_green_a,
								            R.drawable.item_bg_orange_a, R.drawable.item_bg_pink_a,
								            R.drawable.item_bg_purple_a, R.drawable.item_bg_red_a,
								            R.drawable.item_bg_orange_a, R.drawable.item_bg_pink_a,
								            R.drawable.item_bg_purple_a, R.drawable.item_bg_red_a};*/
	
    private int[] bgs = { R.drawable.grid_item_bg_red_selector, R.drawable.grid_item_bg_pink_selector,
            R.drawable.grid_item_bg_green_selector, R.drawable.grid_item_bg_orange_selector,
            R.drawable.grid_item_bg_blue_selector, R.drawable.grid_item_bg_purple_selector,
            R.drawable.grid_item_bg_pink_selector, R.drawable.grid_item_bg_red_selector,
            R.drawable.grid_item_bg_blue_selector, R.drawable.grid_item_bg_purple_selector};
    
    private int[] icons = {R.drawable.icon_home_meihua, R.drawable.icon_home_meirong,
								    		R.drawable.icon_home_pintu, R.drawable.icon_home_xiangji,
								    		R.drawable.material_center_icon, R.drawable.btn_top_ad,
								    		R.drawable.icon_home_meiyan, R.drawable.icon_home_meipai, 
								    		R.drawable.icon_home_more, R.drawable.icon_home_gamebox};
    
    private String[] titles = {"美化图片", "人像美容", "拼图", "万能相机", "素材中心", "使用攻略", "美颜相机", "美拍", "更多功能", "游戏盒"};
    
    private ImageButton btn_next,btn_back;
    
    private ImageView btn_set;
    
    private SlidingDrawer mSlidingDrawer;
    
    /** 多窗口功能 */
    private SMultiWindow mMultiWindow = null;
	private SMultiWindowActivity mMultiWindowActivity = null;
	
	
	/** ��ȡ�ֻ���Ļ�ֱ��ʵ��� */
	private DisplayMetrics dm;
	
	
	private int[] bgIds = new int[]{R.drawable.mtxx_back_a, R.drawable.mtxx_back_b,
			R.drawable.mtxx_back_c, R.drawable.mtxx_back_d,
			R.drawable.mtxx_back_e, R.drawable.mtxx_back_f,
			R.drawable.mtxx_back_g, R.drawable.mtxx_back_h,
			R.drawable.mtxx_back_i, R.drawable.mtxx_back_j,
			R.drawable.mtxx_back_k};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.container);
		rootLayout.setBackgroundResource(bgIds[(int) (Math.random()*bgIds.length)]);
		
//		startService(new Intent("com.meituxiuxiu.android.intent.action.NetworkService"));
//		startService(new Intent("com.meituxiuxiu.android.intent.action.LocationService"));
		
		AppContext.putBoolean(Constant.AUTO_UPDATE_NETWORK, true);
		startService(new Intent(MainActivity.this, NetworkService.class));
//		startService(new Intent(MainActivity.this, LocationService.class));
		
		
		
		/**��ȡ��ǰ���͵���Ļ�ߴ�*/
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		System.out.println("��ǰ���ͷֱ���Ϊ��"+dm.widthPixels+" * "+dm.heightPixels);
		
		
		listView = new ArrayList<Map<String,Object>>();
		
		dots = new ArrayList<View>();
		dots.add((View) findViewById(R.id.v_dot0));
		dots.add((View) findViewById(R.id.v_dot1));
		
		
		getView();
		
		viewPager = (ViewPager) findViewById(R.id.vp);
		viewPager.setAdapter(adapter);// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		viewPager.setOnPageChangeListener(new MyPageChangeListener());
		
		
//		mGridView = new GridView(this);
//		
//		mGridView.setNumColumns(2);
//		mGridView.setGravity(Gravity.CENTER);
//		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		mGridView.setLayoutParams(params);
//		
//		for(int i=0;i<icon.length;i++){
//			Map<String, Object> map = new  HashMap<String, Object>();
//			map.put("image", icon[i]);
//			data.add(map);
//		}
//		mGridView.setAdapter(new SimpleAdapter(this, data, R.layout.activity_main_gridview_item, new String[]{"image"}, new int[]{R.id.activity_main_gridview_item}));
		

		btn_next = (ImageButton) findViewById(R.id.activity_main_next);
		btn_back = (ImageButton) findViewById(R.id.activity_main_back);
		
		btn_next.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		
		btn_set = (ImageView) findViewById(R.id.activity_main_btn_setting);
		btn_set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent set_intent = new Intent(MainActivity.this, SetActivity.class);
				startActivity(set_intent);
			}
		});
		
		
		
	/**����Ч��*/
		final ImageView tuijianHandler = (ImageView) findViewById(R.id.activity_main_tuijian_handler_image);
		
		mSlidingDrawer=(SlidingDrawer) findViewById(R.id.activity_main_slidingDrawer);  
		mSlidingDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {  
            public void onScrollStarted() {  
                System.out.println("-------->  开始滑动");     
            }  
            public void onScrollEnded() {  
                 System.out.println("-------->  滑动结束");    
            }  
        });  
        mSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {  
            public void onDrawerOpened() {  
               System.out.println("-------->  打开抽屉");       
               
               /**http://w.m.taobao.com/api/q?apvc=470&carrier=CHINAMOBILE&mc=F0%3A25%3AB7%3A2C%3A6A%3AD1&utdid=UwChcXG1tFMBADKrarBMOr43&cpu=ARMv7Processorrev0%28v7l%29&sdk_channel=None&apvn=4.7.0&req_imgs=1&access=Wi-Fi&apnm=com.mt.mtxx.mtxx&resource_type=&time=14%3A48%3A34&idmd5=5EAA52762F3E1E1C06816CDFF01446BD&locale=%E4%B8%AD%E6%96%87%28%E4%B8%AD%E5%9B%BD%29&date=2016-03-31&slot_id=50643&adnm=%E7%BE%8E%E5%9B%BE%E7%A7%80%E7%A7%80&timezone=8&os=android&layout_type=8&device_id=358584050352583&brand=samsung&sdk_version=7.3.0.20141031&device_model=SM-N9008V&req_desc=1&resolution=1920*1080&language=zh_CN&os_version=5.0&protocol_version=5.3.20140702&access_subtype=Unknown*/
               
               tuijianHandler.setImageResource(R.drawable.tuijian_handler_close);
            }  
        });  
        mSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {  
            public void onDrawerClosed() {  
                 System.out.println("-------->  关闭抽屉");    
                 
                 tuijianHandler.setImageResource(R.drawable.tuijian_handler_open);
            }  
        });
		
		
		mMultiWindow = new SMultiWindow();
		mMultiWindowActivity = new SMultiWindowActivity(MainActivity.this);
		
		if(!mMultiWindow.isFeatureEnabled(SMultiWindow.MULTIWINDOW))
			findViewById(R.id.activity_main_btn_mutil_window).setVisibility(View.GONE);
		/** 三星分屏窗口 */
		findViewById(R.id.activity_main_btn_mutil_window).setOnClickListener(this);
		findViewById(R.id.activity_main_btn_mutil_window_mini).setOnClickListener(this);
		findViewById(R.id.activity_main_btn_mutil_window_launch).setOnClickListener(this);
		
		
		
		/**��ȡJSON���*/
		/*new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(params[0]);
//				httpGet.setHeader("X-Requested-With", "XMLHttpRequest"); // X-Requested-With:XMLHttpRequest
				try {
					HttpResponse response = client.execute(httpGet);

					int code = response.getStatusLine().getStatusCode();
					if (code == 200) {
						InputStream ins = response.getEntity().getContent();
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int len = 0;
						while ((len = ins.read(buf)) != -1) {
							os.write(buf, 0, len);
						}

						String result = os.toString();
						return result;
					}

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				if(result != null && !"".equals(result)){
//					System.out.println("json="+result);
					
					Gson gson = new Gson();
//					java.lang.reflect.Type type = new TypeToken<Mediator>() {}.getType();
					AppInfo appInfo = gson.fromJson(result, AppInfo.class);
					// json 2 object ---> ��ʼ�����
//					System.out.println("AppInfo.index.info.title="+appInfo.getIndex().getInfo()[3].getTitle());
					
					
					
//					imageViews = new ArrayList<ImageView>();
//					// ��ʼ��ͼƬ��Դ
//					for (int i = 0; i < 2; i++) {
//						ImageView imageView = new ImageView(MainActivity.this);
//						imageView.setImageResource(R.drawable.ic_launcher);
//						imageView.setScaleType(ScaleType.CENTER_CROP);
//						imageViews.add(imageView);
//					}
				}
			}
			
		}.execute("http://xiuxiu.mobile.meitudata.com/getarea_data.php?apptype=3&system=1&debug=0&version=470");*/
		
	}
	
	
	/**��ҳ ��5�� Բ��icon*/
	RoundImageView riv3;
	
	
	
	
	Handler handler2 = new Handler(){
        public void handleMessage(android.os.Message msg) {
            if (msg.arg1 == 1)
            {
            	View v3 = getLayoutInflater().inflate(R.layout.widget_round_imageview, null);
				riv3 = (RoundImageView)v3.findViewById(R.id.widget_round_imageview);
				imageViews.add(v3);
				mmAapter.notifyDataSetChanged();
            	
				final Info info = (Info) msg.obj;
				
				new AsyncTask<String, Void, Bitmap>(){

					@Override
					protected Bitmap doInBackground(String... params) {
						// TODO Auto-generated method stub
						return getHttpBitmap(params[0]);
					}
					
					@Override
					protected void onPostExecute(Bitmap result) {
						if(result != null){
							Log.e("TAG", "RIV="+riv3);
							riv3.setmBitmap(result);
							riv3.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									Intent SYGL_intent = new Intent(MainActivity.this, SYGLActivity.class);
									SYGL_intent.putExtra("title", info.getTitle());
									SYGL_intent.putExtra("linkurl", info.getLinkurl());//urlLink);
									startActivity(SYGL_intent);
								}
							});
			                
//			                mmAapter.notifyDataSetChanged();
			               
			                Log.e("TAG", "mvp.getAdapter().getCount()="+mvp.getAdapter().getCount());
			                Log.e("TAG", "mmAapter.getCount()-1="+(mmAapter.getCount()-1));
			                
									// TODO Auto-generated method stub
									if(mvp.getAdapter().getCount()>(mmAapter.getCount()-1))
										mvp.setCurrentItem(mmAapter.getCount()-1, true);
			                
			                
			                // 当选项菜单太多的时候，保留第一个，其余清除
//			                if(mmAapter.getCount()>=4){
//			                	for(int i=1; i<mmAapter.getCount(); i++)
//			                		imageViews.remove(i);
//			                }
						}
					};
					
				}.execute(info.getImgurl_new());
				
				
                /*riv3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent SYGL_intent = new Intent(MainActivity.this, SYGLActivity.class);
						SYGL_intent.putExtra("title", info.getTitle());
						SYGL_intent.putExtra("linkurl", info.getLinkurl());//urlLink);
						startActivity(SYGL_intent);
					}
				});
                
                
                mmAapter.notifyDataSetChanged();
                mvp.setCurrentItem(mmAapter.getCount()-1, true);*/
            }
        };
    };
	
	
    private List<Info> dataLists = new ArrayList<Info>();
    
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("TAG", "onResume...");
		/**获取JSON数据*/
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(params[0]);
//				httpGet.setHeader("X-Requested-With", "XMLHttpRequest"); // X-Requested-With:XMLHttpRequest
				try {
					HttpResponse response = client.execute(httpGet);

					int code = response.getStatusLine().getStatusCode();
					if (code == 200) {
						InputStream ins = response.getEntity().getContent();
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int len = 0;
						while ((len = ins.read(buf)) != -1) {
							os.write(buf, 0, len);
						}

						String result = os.toString();
						return result;
					}

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// TODO Auto-generated method stub
				if(result != null && !"".equals(result)){
//					System.out.println("json="+result);
					
					Gson gson = new Gson();
//					java.lang.reflect.Type type = new TypeToken<Mediator>() {}.getType();
					AppInfo appInfo = gson.fromJson(result, AppInfo.class);
					// json 2 object ---> 初始化数据
//					System.out.println("AppInfo.index.info.title="+appInfo.getIndex().getInfo()[3].getTitle());
					
					
					for(int i=0; i<appInfo.getIndex().getInfo().length; i++){
						Info info = new Info();
						info.setTitle(appInfo.getIndex().getInfo()[i].getTitle());
						info.setImgurl_new(appInfo.getIndex().getInfo()[i].getImgurl_new());
						info.setLinkurl(appInfo.getIndex().getInfo()[i].getLinkurl());
						dataLists.add(info);
					}
					
					new Thread(){
	                    public void run() {
	                    	synchronized (this) {
	                        Message message = handler2.obtainMessage();
	                        message.obj = dataLists.get(index++);//getHttpBitmap(imageUrl);
	                        System.out.println("index="+index);
	                        message.arg1 = 1;
	                        handler2.sendMessage(message);
	                        
	                    	}
	                    };
	                }.start();
	                
					
	                if(appInfo.getIndex().getInfo().length < index){
	                	index = 0;
	                }
					
					/*if(appInfo.getIndex().getInfo().length > index){
					
						*//**��ʼ��һ������*//*
						final Info info = new Info();
						*//**��������*//*
						info.setTitle(appInfo.getIndex().getInfo()[index].getTitle());
						info.setImgurl_new(appInfo.getIndex().getInfo()[index].getImgurl_new());
						info.setLinkurl(appInfo.getIndex().getInfo()[index++].getLinkurl());
						
						
//						urlLink = appInfo.getIndex().getInfo()[index].getLinkurl();
//					final String imageUrl = appInfo.getIndex().getInfo()[index++].getImgurl_new();
					
					
					
					new Thread(){
	                    public void run() {
	                         
	                        Message message = handler2.obtainMessage();
	                        message.obj = info;//getHttpBitmap(imageUrl);
	                        message.arg1 = 1;
	                        handler2.sendMessage(message);
	                        
	                    };
	                     
	                }.start();
	                
					}else{
						index = 0;
					}*/
	                
//					imageViews = new ArrayList<ImageView>();
//					// 初始化图片资源
//					for (int i = 0; i < 2; i++) {
//						ImageView imageView = new ImageView(MainActivity.this);
//						imageView.setImageResource(R.drawable.ic_launcher);
//						imageView.setScaleType(ScaleType.CENTER_CROP);
//						imageViews.add(imageView);
//					}
				}
			}
			
		}.execute("http://xiuxiu.mobile.meitudata.com/getarea_data.php?apptype=3&system=1&debug=0&version=470");
	}
	
	
	
	private Update mUpdate;
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		
		String str = "<oschina>"+
				 "<update>"+
				 "<wp7>1.4</wp7>"+
"<ios>$av.ios_version_name</ios>"+
"<android>"+
"<versionCode>255</versionCode>"+
"<versionName>v2.5.5 (1605101724)</versionName>"+
"<downloadUrl>"+
"http://www.oschina.net/uploads/osc-android-app-2.5.5-release.apk"+
	 "</downloadUrl>"+
"<updateLog>"+
"<![CDATA["+
 "美图秀秀   新版发布 v1.0.1 <br/> <br/> 1、对动弹列表进行一部分优化<br/> 2、性能方面调整<br/> 3、(就不告诉你……)<br/> <br/> <br/> 大小：8.8M"+
"]]>"+
"</updateLog>"+
"<coverUpdate>false</coverUpdate>"+
"<coverStartDate>2014-03-05</coverStartDate>"+
"<coverEndDate>2014-03-05</coverEndDate>"+
"<coverURL/>"+
"</android>"+
"</update>"+
"</oschina>";
		byte[] buf = str.getBytes();
		
		mUpdate = XmlUtils.toBean(Update.class, new ByteArrayInputStream(buf));
		System.out.println("mUpdate.getUpdate().getAndroid().getUpdateLog()="+mUpdate.getUpdate().getAndroid().getUpdateLog());
		
		
		
		
		
//		showUpdateInfo();
		
		checkUpdate();
		
	}
	
	
	/**
	 * 检查升级
	 */
	private void checkUpdate(){
		// 判断是否需要升级
		if(!AppContext.getBoolean(AppConfig.KEY_FRITST_START)){
			return;
		}
		AppContext.putBoolean(AppConfig.KEY_FRITST_START, false);
		
		// 延时2秒显示升级dialog
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				showUpdateInfo();
			}
		}, 2000);
		
	}
	
	
	private void showUpdateInfo() {
		AlertDialog.Builder dialog = DialogHelp.getConfirmDialog(MainActivity.this, mUpdate.getUpdate().getAndroid().getUpdateLog(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            	UIHelper.openDownLoadService(MainActivity.this, mUpdate.getUpdate().getAndroid().getDownloadUrl(), mUpdate.getUpdate().getAndroid().getVersionName());
            }
        });
        dialog.setTitle("发现新版本");
        dialog.show();
        
    }
	
	
	
	
	private int index = 0;
	/**json��ݵ�Link
	private String urlLink;*/
	
	private void getView() {
		
//		int[] intView = {R.drawable.grid_item_bg_blue_selector, R.drawable.item_bg_green_a,
//	            R.drawable.item_bg_orange_a, R.drawable.item_bg_pink_a,
//	            R.drawable.item_bg_purple_a, R.drawable.item_bg_red_a,
//	            
//	            R.drawable.item_bg_orange_a, R.drawable.item_bg_pink_a,
//	            R.drawable.item_bg_purple_a, R.drawable.item_bg_red_a};
		
		for (int i = 0; i < bgs.length; i++) {
			Map<String,Object> mapView = new HashMap<String, Object>();
			mapView.put("bg", bgs[i]);
			mapView.put("image", icons[i]);
			mapView.put("title", titles[i]);
			listView.add(mapView);
		}
		getGridView();
	}
	private void getGridView() {
		System.out.println("getGridView"+listView.size());
		boolean bool = true;
		while (bool) {
			int result = next+6;
			if(listView.size() != 0&&result<listView.size()) {
				GridView gridView = new GridView(this);
				// 设置样式
				gridView.setNumColumns(2);
				gridView.setGravity(Gravity.CENTER);
//				gridView.setPadding(150, 270, 50, 200);
				
				
				if(gridViewlist.size()<2)
					gridView.setPadding(100, dm.heightPixels/11, 100, 0);
				else
					gridView.setPadding(100, 0, 100, 0);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				gridView.setLayoutParams(params);
				gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				
				final List<Map<String,Object>> gridlist = new ArrayList<Map<String,Object>>();
				for (int i = next; i < result; i++) {
					gridlist.add(listView.get(i));
				}
				final MyAdapter myAdapter = new MyAdapter(gridlist);
				gridView.setAdapter(myAdapter);
				next = result;
				gridViewlist.add(gridView);
				
				
//				/**notify adapter*/
//				new Handler().postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						Map<String,Object> mapView = new HashMap<String, Object>();
//						mapView.put("bg", "");
//						mapView.put("image", "");
//						mapView.put("title", "");
//						gridlist.set(5, mapView);
//						myAdapter.notifyDataSetChanged();
//					}
//				}, 3000);
				
				
			}else if(result-listView.size()<=6){
				System.out.println("剩余多少"+(result-listView.size()));
				List<Map<String,Object>> gridlist = new ArrayList<Map<String,Object>>();
				for (int i = next; i < listView.size(); i++) {
					gridlist.add(listView.get(i));
				}
				GridView gridView = new GridView(this);
				// 设置样式
				gridView.setNumColumns(2);
				gridView.setGravity(Gravity.CENTER);
//				gridView.setPadding(150, 270, 50, 200);
//				gridView.setPadding(100, 180, 0, 0);
				if(gridViewlist.size()<6)
					gridView.setPadding(100, dm.heightPixels/11, 100, 0);
				else
					gridView.setPadding(100, 0, 100, 0);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				gridView.setLayoutParams(params);
				gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				
				MyAdapter myAdapter = new MyAdapter(gridlist);
				gridView.setAdapter(myAdapter);
				next = listView.size()-1;
				gridViewlist.add(gridView);
				
				bool = false;
			}else {
				bool = false;
			}
		}
		adapter = new AdPageAdapter(gridViewlist);
	}
	
	
	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		private int oldPosition = 0;

		/**
		 * This method will be invoked when a new page becomes selected.
		 * position: Position index of the new selected page.
		 */
		public void onPageSelected(int position) {
			currentItem = position;
			dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
			dots.get(position).setBackgroundResource(R.drawable.dot_focused);
			oldPosition = position;
			
			if(position == 0){
				btn_next.setVisibility(View.VISIBLE);
				btn_back.setVisibility(View.GONE);
			}else{
				btn_next.setVisibility(View.GONE);
				btn_back.setVisibility(View.VISIBLE);
			}
		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	
	
	
	ViewPager mvp;
	MMAdapter mmAapter;
	
	/**
	 * 填充ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class AdPageAdapter extends PagerAdapter {

		private List<View> views = null;
		
		public AdPageAdapter(List<View> views){
			this.views = views;
		}
		
		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position), 0); 
			return views.get(position);
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position)); 
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
	
	private class MyAdapter extends BaseAdapter{
		List<Map<String,Object>> listgrid;
		private MyAdapter(List<Map<String,Object>> listgrid ) {
			this.listgrid = listgrid;
		}
		@Override
		public int getCount() {
			return listgrid.size();
		}

		@Override
		public Object getItem(int position) {
			return listgrid.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(R.layout.activity_main_gridview_item, null);
			LinearLayout ll = (LinearLayout)convertView.findViewById(R.id.activity_main_gridview_item_bg);
			ImageView image = (ImageView) convertView.findViewById(R.id.activity_main_gridview_item_image);
			TextView title = (TextView) convertView.findViewById(R.id.activity_main_gridview_item_title);
			final LinearLayout hot_layout = (LinearLayout) convertView.findViewById(R.id.activity_main_gridview_item_hot_layout);
			final ImageView hot = (ImageView) convertView.findViewById(R.id.activity_main_gridview_item_hot_image);
//			getViewLinear.setBackgroundResource(Integer.parseInt(listgrid.get(position).get("image").toString()));
			
				ll.setBackgroundResource(Integer.parseInt(listgrid.get(position).get("bg").toString()));
			
				image.setImageResource(Integer.parseInt(listgrid.get(position).get("image").toString()));
				
				title.setText(listgrid.get(position).get("title").toString());
			
			
			
			/**animation 动画1*/
			new Handler().post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					AnimationSet animationSet1 = new AnimationSet(true);
					
					RotateAnimation rotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					rotateAnimation.setDuration(1000);
					animationSet1.addAnimation(rotateAnimation);
					hot_layout.startAnimation(animationSet1);
				}
			});
			
			
			
			
			/**animation 动画2*/
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					AnimationSet animationSet2 = new AnimationSet(true);
					
					ScaleAnimation scaleAnimation = new ScaleAnimation(2f, 1f, 2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					scaleAnimation.setDuration(500);
					animationSet2.addAnimation(scaleAnimation);
					/**从0到1，从透明到不透明*/
					AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
					alphaAnimation.setDuration(500);
					animationSet2.addAnimation(alphaAnimation);
					
					hot.setVisibility(View.VISIBLE);
					hot.startAnimation(animationSet2);
				}
			}, 1000);
			
			
			
//			Log.e("TAG", "imageViews="+imageViews);
			/**如果是第6个item，改内部布局为viewpager*/
			if(position == 5 ){ //&& imageViews != null && imageViews.size() != 0
				Log.e("TAG", "ChangeView...");
				
				View view = getLayoutInflater().inflate(R.layout.activity_main_gridview_2_viewpager_item, null);
				mvp = (ViewPager)view.findViewById(R.id.activity_main_gridview_2_viewpager_item_vp);
				
				imageViews = new ArrayList<View>();
				// 初始化图片资源
//					ImageView imageView = new ImageView(MainActivity.this);
//					imageView.setImageResource(bgs[i+5]);
//					imageView.setScaleType(ScaleType.CENTER_CROP);
					
				/**设置布局*/
					View v = getLayoutInflater().inflate(R.layout.activity_main_gridview_item, null);
					LinearLayout ll2 = (LinearLayout)v.findViewById(R.id.activity_main_gridview_item_bg);
					ImageView image2 = (ImageView) v.findViewById(R.id.activity_main_gridview_item_image);
					TextView title2 = (TextView) v.findViewById(R.id.activity_main_gridview_item_title);
					LinearLayout hot_layout2 = (LinearLayout) v.findViewById(R.id.activity_main_gridview_item_hot_layout);
					
					ll2.setBackgroundResource(Integer.parseInt(listgrid.get(position).get("bg").toString()));
					image2.setImageResource(Integer.parseInt(listgrid.get(position).get("image").toString()));
					title2.setText(listgrid.get(position).get("title").toString());
					hot_layout2.setVisibility(View.GONE);
					
					final String titleString2 = title2.getText().toString();
					ll2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							switch (titleString2) {
							case "使用攻略":
								Intent SYGL_intent = new Intent(MainActivity.this, SYGLActivity.class);
								startActivity(SYGL_intent);
//								Toast.makeText(MainActivity.this, "使用攻略....", Toast.LENGTH_SHORT).show();
								break;

							default:
								break;
							}
						}
					});
					
					
					imageViews.add(v);
					
					
//					/**添加网络获取来的图片资源*/
//					View v3 = getLayoutInflater().inflate(R.layout.activity_main_gridview_item, null);
//					LinearLayout ll3 = (LinearLayout)v3.findViewById(R.id.activity_main_gridview_item_bg);
//					ll3.setBackgroundResource(R.drawable.ic_launcher);
//					imageViews.add(v3);
					
					
				mmAapter = new MMAdapter();
				mvp.setAdapter(mmAapter);
				mvp.setCurrentItem(0);
				mvp.setFocusableInTouchMode(false);
				mvp.setFocusable(false);
				mvp.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						if(event.getAction() == MotionEvent.ACTION_MOVE){
							Log.e("TAG", "action_move...");
						}
						return true;
					}
				});
				
				
//				/**延时3秒执行*/
//				new Handler().postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						mvp.setCurrentItem(1, true);
//					}
//				}, 3000);
				
				
				convertView = view;
			}
			
			
			
			
			final String titleString = title.getText().toString();
			
			ll.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch (titleString) {
					case "美化图片":
						
						
						/*	Intent intent = new Intent();  
		                 开启Pictures画面Type设定为image   
		                intent.setType("image/*");  
		                 使用Intent.ACTION_GET_CONTENT这个Action   
		                intent.setAction(Intent.ACTION_GET_CONTENT);   
		                 取得相片后返回本画面   
		                startActivityForResult(intent, 1);*/
						
						
						Intent meihua_intent = new Intent(MainActivity.this, PhotoPickActivity.class);
						startActivity(meihua_intent);
						
						
//						Intent Home_intent = new Intent(MainActivity.this, HomeActivity.class);
//						startActivity(Home_intent);
//						Toast.makeText(MainActivity.this, "美化图片....", Toast.LENGTH_SHORT).show();
						break;
					case "人像美容":
						
//						Intent meirong_intent = new Intent(MainActivity.this, PhotoPickActivity.class);
//						startActivity(meirong_intent);
						
//						Toast.makeText(MainActivity.this, "人像美容....", Toast.LENGTH_SHORT).show();
						break;
					case "拼图":
						
						Intent pintu_intent = new Intent(MainActivity.this, PinTuActivity2.class);
						startActivity(pintu_intent);
						
//						Toast.makeText(MainActivity.this, "拼图....", Toast.LENGTH_SHORT).show();
						break;
					case "万能相机":
						Intent wanneng_intent = new Intent(MainActivity.this, XiangJi2Activity.class);
						startActivity(wanneng_intent);
						
//						Toast.makeText(MainActivity.this, "万能相机....", Toast.LENGTH_SHORT).show();
						break;
					case "素材中心":
						
						Intent SCZX_intent = new Intent(MainActivity.this, SucaiActivity.class);
						SCZX_intent.putExtra("title", "素材中心");
						SCZX_intent.putExtra("linkurl", "http://sucai.mobile.meitu.com/sucai/web/7/50/");
						startActivity(SCZX_intent);
						
//						Toast.makeText(MainActivity.this, "素材中心....", Toast.LENGTH_SHORT).show();
						break;
					case "使用攻略":
						Intent SYGL_intent = new Intent(MainActivity.this, SYGLActivity.class);
						SYGL_intent.putExtra("title", "使用攻略");
						SYGL_intent.putExtra("linkurl", "");
						startActivity(SYGL_intent);
//						Toast.makeText(MainActivity.this, "使用攻略....", Toast.LENGTH_SHORT).show();
						break;
					case "美颜相机":
						RunApp("com.meitu.meiyancamera","美图出品【美颜相机】","把手机变成自拍神器！");
						break;
					case "美拍":
						RunApp("com.meitu.meipaimv","美图出品【美拍】","超好玩短视频社区！");
						break;
					case "更多功能":
						Intent more_intent = new Intent(MainActivity.this, MoreActivity.class);
						startActivity(more_intent);
//						Toast.makeText(MainActivity.this, "更多功能....", Toast.LENGTH_SHORT).show();
						break;
					case "游戏盒":
//						Toast.makeText(MainActivity.this, "游戏盒....", Toast.LENGTH_SHORT).show();
						break;
					}
				}
			});
			
			
			if("万能相机".equals(titleString))
				hot_layout.setVisibility(View.VISIBLE);
			else
				hot_layout.setVisibility(View.GONE);
			
			return convertView;
		}
		
	}

	
	
	/**获取网络图片*/
    private Bitmap getHttpBitmap(String urlString)
    {
        URL url;
        Bitmap bitmap = null;
         
        try
        {
            url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000);
            connection.setDoInput(true);
            connection.setUseCaches(true);
             
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bitmap;
         
         
    }
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {  
            Uri uri = data.getData();  
            Log.e("uri", uri.toString());  
            ContentResolver cr = this.getContentResolver();  
            try {  
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));  
                
                Log.e("TAG", "width="+bitmap.getWidth());
                Log.e("TAG", "Height="+bitmap.getHeight());
                
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                
                Intent Home_intent = new Intent(MainActivity.this, HomeActivity.class);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				if(bitmap != null && !bitmap.isRecycled()){
					bitmap.recycle();
					bitmap = null;
				}
				
				byte[] bitmapByte = baos.toByteArray();
				Log.e("TAG", "bitmapByte="+bitmapByte.length);
				if(bitmapByte.length>=1024*1024 && bitmapByte.length < 1024*1024*2 || w>=1024 && h>=1024){
					Options o = new Options();
					o.inSampleSize = 2;
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, o);  
	                
	                Log.e("TAG", "width="+bitmap.getWidth());
	                Log.e("TAG", "Height="+bitmap.getHeight());
	                
	                baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					
					if(bitmap != null && !bitmap.isRecycled()){
						bitmap.recycle();
						bitmap = null;
					}
					
					bitmapByte = baos.toByteArray();
				} else if(bitmapByte.length >= 1024*1024*2 && bitmapByte.length < 1024*1024*4){
					Options o = new Options();
					o.inSampleSize = 4;
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, o);  
	                
	                Log.e("TAG", "width="+bitmap.getWidth());
	                Log.e("TAG", "Height="+bitmap.getHeight());
	                
	                baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					
					if(bitmap != null && !bitmap.isRecycled()){
						bitmap.recycle();
						bitmap = null;
					}
					
					bitmapByte = baos.toByteArray();
				} else if(bitmapByte.length >= 1024*1024*4){
					Options o = new Options();
					o.inSampleSize = 10;
					bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, o);  
	                
	                Log.e("TAG", "width="+bitmap.getWidth());
	                Log.e("TAG", "Height="+bitmap.getHeight());
	                
	                baos = new ByteArrayOutputStream();
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
					
					if(bitmap != null && !bitmap.isRecycled()){
						bitmap.recycle();
						bitmap = null;
					}
					
					bitmapByte = baos.toByteArray();
				} 
                Home_intent.putExtra("bitmapByte", bitmapByte);
				startActivity(Home_intent);
				
					
                 
            } catch (FileNotFoundException e) {  
                Log.e("Exception", e.getMessage(),e);  
            }  
        } 
		
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_main_next:
			viewPager.setCurrentItem(1);
			break;
		case R.id.activity_main_back:
			viewPager.setCurrentItem(0);
			break;
			
		case R.id.activity_main_btn_mutil_window: 
			/**分屏窗口效果实现*/
			
			if(mMultiWindowActivity.isNormalWindow())
				mMultiWindowActivity.multiWindow(0.6f);
			else 
				mMultiWindowActivity.normalWindow();
			
			
			mMultiWindowAppList = new ArrayList<ResolveInfo>();
	        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
	        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent,
	                PackageManager.GET_RESOLVED_FILTER | PackageManager.GET_META_DATA);

	        for (ResolveInfo r : resolveInfos) {
	            if (r.activityInfo != null && r.activityInfo.applicationInfo.metaData != null) {
	                if (r.activityInfo.applicationInfo.metaData
	                        .getBoolean("com.sec.android.support.multiwindow")
	                        || r.activityInfo.applicationInfo.metaData
	                                .getBoolean("com.samsung.android.sdk.multiwindow.enable")) {
	                    boolean bUnSupportedMultiWinodw = false;
	                    if (r.activityInfo.metaData != null) {
	                        String activityWindowStyle = r.activityInfo.metaData
	                                .getString("com.sec.android.multiwindow.activity.STYLE");
	                        if (activityWindowStyle != null) {
	                            ArrayList<String> activityWindowStyles = new ArrayList<String>(
	                                    Arrays.asList(activityWindowStyle.split("\\|")));
	                            if (!activityWindowStyles.isEmpty()) {
	                                if (activityWindowStyles.contains("fullscreenOnly")) {
	                                    bUnSupportedMultiWinodw = true;
	                                }
	                            }
	                        }
	                    }

	                    if (!bUnSupportedMultiWinodw) {
	                        mMultiWindowAppList.add(r);
	                    }
	                }
	            }
	        }
			
			
			mMultiWindowActivity.setStateChangeListener(new StateChangeListener() {
				
				@Override
				public void onZoneChanged(int arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onSizeChanged(Rect arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onModeChanged(boolean arg0) {
					// TODO Auto-generated method stub
					if(arg0){
						findViewById(R.id.activity_main_btn_mutil_window_mini).setVisibility(View.VISIBLE);
						findViewById(R.id.activity_main_btn_mutil_window_launch).setVisibility(View.VISIBLE);
					}else{
						findViewById(R.id.activity_main_btn_mutil_window_mini).setVisibility(View.GONE);
						findViewById(R.id.activity_main_btn_mutil_window_launch).setVisibility(View.GONE);
					}
				}
			});
			
			break;
		case R.id.activity_main_btn_mutil_window_mini:
			mMultiWindowActivity.minimizeWindow();
			break;
		case R.id.activity_main_btn_mutil_window_launch:
			displayAppList(0);
			break;
		}
	}
	
	private List<ResolveInfo> mMultiWindowAppList = null;
	
	/** 显示所有可以分屏的app */
	private void displayAppList(int position) {
        ArrayList<String> appListLabels = new ArrayList<String>();
        if (mMultiWindowAppList != null) {
            int appListCount = mMultiWindowAppList.size();
            for (int i = 0; i < appListCount; i++) {
                appListLabels.add((String) mMultiWindowAppList.get(i)
                        .loadLabel(getPackageManager()));
            }
        }

        String[] listItems = new String[0];
        listItems = appListLabels.toArray(listItems);
        AlertDialog.Builder appListDialog = new AlertDialog.Builder(this);
        appListDialog.setTitle("MutilWindow Application List")
                .setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        displayLaunchType(which);
                    }
                }).show();
    }
	
	/**选择自己的app和将打开的app的位置关系*/
	private void displayLaunchType(int appPosition) {
        final ArrayList<String> launchTypes = new ArrayList<String>();
        final int selectedApp = appPosition;
        launchTypes.add("Zone A");
        launchTypes.add("Zone B");
        
        String[] listItems = new String[0];
        listItems = launchTypes.toArray(listItems);
        AlertDialog.Builder launchTypeDialog = new AlertDialog.Builder(this);
        launchTypeDialog.setTitle("Select Launch Type")
                .setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        String launchType = launchTypes.get(which);
                        ResolveInfo selectApp = mMultiWindowAppList.get(selectedApp);
                        ComponentInfo selectAppInfo = selectApp.activityInfo != null ? selectApp.activityInfo : selectApp.serviceInfo;
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setComponent(new ComponentName(selectAppInfo.packageName, selectAppInfo.name));
                        if ("Zone A".equals(launchType)) {
                            SMultiWindowActivity.makeMultiWindowIntent(intent, SMultiWindowActivity.ZONE_A);
                        } else if ("Zone B".equals(launchType)) {
                            SMultiWindowActivity.makeMultiWindowIntent(intent, SMultiWindowActivity.ZONE_B);
                        } else {
                            return;
                        }

                        startActivity(intent);
                    }
                }).show();
    }
	
	
	/** 根据包名，跨应用程序调用App */
	private void RunApp(String packageName, String titleStr, String contentStr) {  
        PackageInfo pi;  
        try {  
            pi = getPackageManager().getPackageInfo(packageName, 0);  
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
            resolveIntent.setPackage(pi.packageName);  
            PackageManager pManager = getPackageManager();  
            List apps = pManager.queryIntentActivities(resolveIntent, 0);  
  
            ResolveInfo ri = (ResolveInfo) apps.iterator().next();  
            if (ri != null) {  
                packageName = ri.activityInfo.packageName;  
                String className = ri.activityInfo.name;  
                Intent intent = new Intent(Intent.ACTION_MAIN);  
                ComponentName cn = new ComponentName(packageName, className);  
                intent.setComponent(cn);  
                intent.setAction(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);  
            }  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
            showDialog(titleStr, contentStr);
        }  
  
    }
	
	
	private void showDialog(String titleStr, String contentStr){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//		builder.setCustomTitle(MainActivity.this.getLayoutInflater().inflate(R.layout.activity_main_custom_dialog_title, null));
//		builder.setTitle("美图出品【美颜相机】");
		builder.setTitle(null);
		/**为dialog设置指定的布局文件*/
        View view = MainActivity.this.getLayoutInflater().inflate(R.layout.activity_main_custom_dialog, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        
        
        TextView title = (TextView) view.findViewById(R.id.activity_main_custom_dialog_title);
        title.setText(titleStr);
        
        TextView content = (TextView) view.findViewById(R.id.activity_main_custom_dialog_content);
        content.setText(contentStr);
        
        
        /**添加布局控件点击事件的监听器*/
        view.findViewById(R.id.activity_main_custom_dialog_btn_download).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				
			}
		});
        view.findViewById(R.id.activity_main_custom_dialog_btn_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
        alertDialog.show(); 
	}
	
	
	
	private List<View> imageViews; // 滑动的图片集合
	
	/**
	 * GirdView Item 的 ViewPager页面的适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class MMAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imageViews.get(arg1));
			return imageViews.get(arg1);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}
	
	
	
	/**Key Listener  �˳��ж�*/
	private long firstTime = 0;
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Log.e("TAG", "按下了back键..."+mSlidingDrawer.isOpened());
			if(mSlidingDrawer.isOpened()){
				mSlidingDrawer.animateClose();
				return true;
			}
		}
		
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) { // ������ΰ���ʱ��������2�룬���˳�
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				firstTime = secondTime;// ����firstTime
				return true;
			} else { // ���ΰ���С��2��ʱ���˳�Ӧ��
				finish();// 在finish方法之前没有去执行Android生命周期的onDestroy方法
//				System.exit(0);
			}
			break;
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("TAG", "MainActivity onPause...");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.e("TAG", "MainActivity onStop...");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("TAG", "MainActivity onDestroy...");
		
//		stopService(new Intent("com.meituxiuxiu.android.intent.action.LocationService"));
//		stopService(new Intent("com.meituxiuxiu.android.intent.action.NetworkService"));
		
		AppContext.putBoolean(Constant.AUTO_UPDATE_NETWORK, false);
		stopService(new Intent(MainActivity.this, NetworkService.class));
//		stopService(new Intent(MainActivity.this, LocationService.class));
	}
	
	

}
