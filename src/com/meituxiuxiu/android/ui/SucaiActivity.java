package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.SucaiAdapter;
import com.meituxiuxiu.android.jsonbean.AppInfo;
import com.meituxiuxiu.android.jsonbean.AppSucai;
import com.meituxiuxiu.android.service.NetworkService;
import com.meituxiuxiu.android.utils.tools.FileCache;
import com.meituxiuxiu.android.utils.tools.HttpCache;
import com.meituxiuxiu.android.utils.tools.MemoryCache;

public class SucaiActivity extends Activity implements OnClickListener{
	
	private Context mContext;
	private WebView mWebView;
	
	private ListView mListView;
	private SucaiAdapter adapter;
	private List<Map<String, Object>> list;
	
	private ProgressDialog mProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sucai);
		
		mContext = this;
		
		memoryCache = new MemoryCache(mContext);
		
		String title = getIntent().getStringExtra("title");
		String urlLink = getIntent().getStringExtra("linkurl");
		
		/**set title bar*/
		setTitleBar(R.drawable.icon_home_a, "首页", title, null, 0);
		/**set title bar background color : Red*/
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.widget_common_top_title_layout);
		rl.setBackgroundColor(Color.rgb(255, 71, 71));


		if(!NetworkService.isEnableNetwork){ // 无网络
			Builder builder = new AlertDialog.Builder(this).setTitle("系统提示").setMessage("请检查网络状况");
			builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					// TODO Auto-generated method stub
					Intent sysSetIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
					startActivity(sysSetIntent);
					dialog.dismiss();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int arg1) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
			builder.show();
			
			return;
		}
		
	
		
		
		mProgressDialog = ProgressDialog.show(mContext, null, "加载中，请稍后...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				SucaiActivity.this.finish();
			}
		});
		
		
		Map<String, String> header = new HashMap<String, String>();
		header.put("device", "android");
		
		mWebView = (WebView) findViewById(R.id.activity_sucai_webview);
		mWebView.loadUrl(urlLink, header);
		
		 //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		mWebView.setWebViewClient(new WebViewClient(){
	           @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	            // TODO Auto-generated method stub
	               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
	             view.loadUrl(url);
	             
	             Log.w("TAG", "url="+url);
	             
	             if(!url.contains("http://"))
	            	 return true;
	            	 
	             /**响应a标签，去下载apk*/
	             startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	             
	            return true;
	        }
	           
	           @Override
	        public void onPageFinished(WebView view, String url) {
	        	// TODO Auto-generated method stub
	        	super.onPageFinished(view, url);
	        }
	           
	           
	           @Override
	        public void onReceivedError(WebView view, int errorCode,
	        		String description, String failingUrl) {
	        	// TODO Auto-generated method stub
	        	super.onReceivedError(view, errorCode, description, failingUrl);
	        	/**设置webview不可见*/
//	        	mWebView.setVisibility(View.GONE);
	        }
	           
	           
	       });
		
		//启用支持javascript
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		/** 设置适应屏幕 */
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		
		
		/**初始化ListView*/
		mListView = (ListView) findViewById(R.id.activity_sucai_listview);
		list = new ArrayList<Map<String,Object>>();
		adapter = new SucaiAdapter(mContext, list);
		mListView.setAdapter(adapter);
		
		
		/**http://sucai.mobile.meitu.com/sucai/v3/androidxx/list.json*/
		/**请求数据*/
		new AsyncTask<String, Void, String>() {

			
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}
			
			
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
				if(!TextUtils.isEmpty(result)){
					Gson gson = new Gson();
					final AppSucai appSucai = gson.fromJson(result, AppSucai.class);
					
					
					mWebView.loadUrl(appSucai.getBanner_data().getInfo()[0].getUrl());
					/**general 列表里面的每一项数据*/
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
					
					for(int i=0; i<appSucai.getGeneral().getInfo().length; i++){
						Map<String, Object> m = new HashMap<String, Object>();
						
						Bitmap bmp = null;
						
								// TODO Auto-generated method stub
								bmp = getBitmap(appSucai.getGeneral().getPre_thum_url() + appSucai.getGeneral().getInfo()[i].getThumbnail());
							
						
						m.put("image",  bmp);
						m.put("count", appSucai.getGeneral().getInfo()[i].getCount());
						m.put("title", appSucai.getGeneral().getInfo()[i].getName());
						m.put("content", "总计"+appSucai.getGeneral().getInfo()[i].getCount()+"张");
						list.add(m);
					}
					
					
					Message msg = new Message();
					msg.what = 0x0001;
					handler.sendMessage(msg);
					
						}
					}).start();
					
					/*new AsyncTask<String, Void, Bitmap>(){

						@Override
						protected Bitmap doInBackground(String... params) {
							// TODO Auto-generated method stub
							return getHttpBitmap(params[0]);
						}
						
						@Override
						protected void onPostExecute(Bitmap result) {
							if(result != null){

							}
						};
						
					}.execute(appSucai.getGeneral().getPre_thum_url() + appSucai.getGeneral().getInfo()[0].getThumbnail());*/
					
					
					
					if(adapter != null){
						adapter.notifyDataSetChanged();
						setListViewHeightBasedOnChildren(mListView);
					}
					
				}
			}
			
		}.execute("http://sucai.mobile.meitu.com/sucai/v3/androidxx/list.json");
		
	
		
		
	}
	
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 0x0001)
			if(adapter != null){
				adapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(mListView);
				
				mProgressDialog.dismiss();
			}
		};
	};
	
	/**
	* 动态设置ListView的高度
	* @param listView
	*/
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    if(listView == null) return;
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null) {
	        // pre-condition
	        return;
	    }
	    int totalHeight = 0;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        View listItem = listAdapter.getView(i, null, listView);
	        listItem.measure(0, 0);
	        totalHeight += listItem.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	}
	
	private MemoryCache memoryCache;
	private FileCache fileCache = new FileCache();
	private HttpCache httpCache = new HttpCache();
	
	/*** 获得一张图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取 ***/
	public Bitmap getBitmap(String url) {
		// 1.从内存缓存中获取图片
		Bitmap resultBitmap = memoryCache.getBitmap(url);
		if (resultBitmap == null) {
			// 2.文件缓存中获取
			resultBitmap = fileCache.getBitmap(url);
			if (resultBitmap == null) {
				// 3.从网络获取
				resultBitmap = HttpCache.downloadBitmap(url);
				if (resultBitmap != null) {
					fileCache.saveBitmap(url, resultBitmap);
					memoryCache.saveBitmap(url, resultBitmap);
					System.out.println("3.网络缓存中获取图片");
				}
			} else {
				// 添加到内存缓存
				memoryCache.saveBitmap(url, resultBitmap);
				System.out.println("2.文件缓存中获取图片");
			}
		} else {
			System.out.println("1.内存缓存中获取图片");
		}
		return resultBitmap;
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
	
	
	private void setTitleBar(int leftResId, String leftStr, String titleStr, String rightStr, int rightResId){
		ImageView leftIcon = (ImageView) findViewById(R.id.widget_common_top_title_layout_left_icon);
		TextView leftWord = (TextView) findViewById(R.id.widget_common_top_title_layout_left_word);
		TextView title = (TextView) findViewById(R.id.widget_common_top_title_layout_title);
		TextView rightWord = (TextView) findViewById(R.id.widget_common_top_title_layout_right_word);
		ImageView rightIcon = (ImageView) findViewById(R.id.widget_common_top_title_layout_right_icon);
		
		LinearLayout leftLayout = (LinearLayout) findViewById(R.id.widget_common_top_title_layout_left_layout);
		LinearLayout rightLayout = (LinearLayout) findViewById(R.id.widget_common_top_title_layout_right_layout);
		
		/**判断是否要显示控件*/
		if(leftResId == 0){
			leftIcon.setVisibility(View.GONE);
		}else{
			leftIcon.setImageResource(leftResId);
		}
		
		if("".equals(leftStr) || leftStr == null){
			leftWord.setVisibility(View.GONE);
		}else{
			leftWord.setText(leftStr);
		}
		
		if("".equals(titleStr) || titleStr == null){
			title.setVisibility(View.GONE);
		}else{
			title.setText(titleStr);
		}
		
		if("".equals(rightStr) || rightStr == null){
			rightWord.setVisibility(View.GONE);
		}else{
			rightWord.setText(rightStr);
		}
		
		if(rightResId == 0){
			rightIcon.setVisibility(View.GONE);
		}else{
			rightIcon.setImageResource(rightResId);
		}
		
		
		if(leftIcon.getVisibility() == View.VISIBLE || leftWord.getVisibility() == View.VISIBLE){
			leftLayout.setOnClickListener(this);
		}
		if(rightIcon.getVisibility() == View.VISIBLE || rightWord.getVisibility() == View.VISIBLE){
			rightLayout.setOnClickListener(this);
		}
		
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.widget_common_top_title_layout_left_layout:
			finish();
			break;

		default:
			break;
		}
	}
	
}
