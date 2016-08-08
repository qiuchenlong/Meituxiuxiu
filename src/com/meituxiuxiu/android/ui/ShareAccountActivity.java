package com.meituxiuxiu.android.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.service.NetworkService;
import com.meituxiuxiu.android.sina.weibo.sdk.AccessTokenKeeper;
import com.meituxiuxiu.android.sina.weibo.sdk.openapi.LogoutAPI;
import com.meituxiuxiu.android.sina.weibo.sdk.openapi.UsersAPI;
import com.meituxiuxiu.android.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.meituxiuxiu.android.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.open.SocialConstants;
import com.tencent.open.utils.HttpUtils.HttpStatusException;
import com.tencent.open.utils.HttpUtils.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShareAccountActivity extends Activity implements OnClickListener{
	
	private Tencent mTencent;
	private String APP_ID = "1105343394";
	private static final String SCOPE = "get_simple_userinfo";// 读取用户信息 
	private Dialog mProgressDialog;
	
	
	
	private SsoHandler mSsoHandler;
	
	
	
	
	private LinearLayout qqLinearlayout, weiboLinearlayout;
	private TextView qqLoginState, weiboLoginState;
	private TextView qqNickname, weiboNickname;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_account);
		
		
		/**initData*/
		String qqNicknameStr = BaseApplication.getString(Constant.SET_SHARE_QQ_ACCOUNT);
		String weiboNicknameStr = BaseApplication.getString(Constant.SET_SHARE_SINA_WEIBO_ACCOUNT);
		
		
		/**initUI*/
		qqLinearlayout = (LinearLayout) findViewById(R.id.activity_share_account_qq_btn_login);
		qqLoginState = (TextView) findViewById(R.id.activity_share_account_qq_btn_login_text);
		qqNickname = (TextView) findViewById(R.id.activity_share_account_qq_account_nickname);
		
		weiboLinearlayout = (LinearLayout) findViewById(R.id.activity_share_account_xinlangweibo_btn_login);
		weiboLoginState = (TextView) findViewById(R.id.activity_share_account_xinlangweibo_btn_login_text);
		weiboNickname = (TextView) findViewById(R.id.activity_share_account_xinlangweibo_account_nickname);
		
		
		if(TextUtils.isEmpty(qqNicknameStr)){// 还没有登录过
			qqLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg);
			qqLoginState.setText("登录");
			qqNickname.setText("...");
		}else{// 登录过了
			qqLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg2);
			qqLoginState.setText("注销");
			qqNickname.setText(qqNicknameStr);
		}
		
		
		if(TextUtils.isEmpty(weiboNicknameStr)){// 还没有登录过
			weiboLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg);
			weiboLoginState.setText("登录");
			weiboNickname.setText("...");
		}else{// 登录过了
			weiboLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg2);
			weiboLoginState.setText("注销");
			weiboNickname.setText(weiboNicknameStr);
		}
		
		
		
		
		/**set title bar*/
		setTitleBar(R.drawable.icon_back_selector, "返回", "分享账户", null, 0);
		
		/**Set Event*/
		findViewById(R.id.activity_share_account_qq_btn_login).setOnClickListener(this);
		findViewById(R.id.activity_share_account_xinlangweibo_btn_login).setOnClickListener(this);
		findViewById(R.id.activity_share_account_baiduyun_btn_login).setOnClickListener(this);
		findViewById(R.id.activity_share_account_facebook_btn_login).setOnClickListener(this);
		findViewById(R.id.activity_share_account_twitter_btn_login).setOnClickListener(this);
		
		
		
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
	
//	private boolean isQQLogin = false;
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(isConnect(ShareAccountActivity.this)){ // 有网络
			
		switch (v.getId()) {
		case R.id.widget_common_top_title_layout_left_layout:
			finish();
			break;
			
			
			/**QQ、微博、百度云、Fackbook、Twitter 登录按钮事件*/
		case R.id.activity_share_account_qq_btn_login:
//			if (!isQQLogin) {
			
			// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
			// 其中APP_ID是分配给第三方应用的appid，类型为String。
			// 1.4版本:此处需新增参数，传入应用程序的全局context，可通过activity的getApplicationContext方法获取
			mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
			
			if(TextUtils.equals(qqLoginState.getText(), "登录")) {
				mProgressDialog = createLoadingDialog(ShareAccountActivity.this, "登录中...");
				mProgressDialog.show();
				qqLogin();
			} else {
				qqLogout();
			}
//			isQQLogin = !isQQLogin;
			break;
			
			
		case R.id.activity_share_account_xinlangweibo_btn_login:
			
			if(TextUtils.equals(weiboLoginState.getText(), "登录")) {
				mProgressDialog = createLoadingDialog(ShareAccountActivity.this, "登录中...");
				mProgressDialog.show();
				weiboLogin();
			} else {
				weiboLogout();
			}
			
			
			
			break;
		case R.id.activity_share_account_baiduyun_btn_login:
//			mProgressDialog = createLoadingDialog(ShareAccountActivity.this, "登录中...");
//			mProgressDialog.show();
//			
//			break;
		case R.id.activity_share_account_facebook_btn_login:
//			mProgressDialog = createLoadingDialog(ShareAccountActivity.this, "登录中...");
//			mProgressDialog.show();
//			
//			break;
		case R.id.activity_share_account_twitter_btn_login:
			mProgressDialog = createLoadingDialog(ShareAccountActivity.this, "登录中...");
			mProgressDialog.show();
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mProgressDialog.dismiss();
					Toast.makeText(ShareAccountActivity.this, "登录出错", Toast.LENGTH_SHORT).show();
				}
			}, 3000);
			
			break;
		}
		
		}else{ // 无网络
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
		}
		
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
     * 得到自定义的progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
    public static Dialog createLoadingDialog(Context context, String msg) {  
  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.wedget_loading_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.wedget_loading_animation);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);// 设置加载信息  
  
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
  
        loadingDialog.setCancelable(true);// 可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局  
        
        return loadingDialog;  
  
    }
    
    
    
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == Constants.REQUEST_LOGIN) {
			mTencent.onActivityResultData(requestCode, resultCode, data, new BaseUiListener());
		}
		if (mSsoHandler != null) {
	        mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }

		super.onActivityResult(requestCode, resultCode, data);
	}
    
    
    
    
    
	
	
	/**--------------------------------QQ-----------------------------------*/
	
	/**
	 * 登录
	 */
	private void qqLogin(){
		
		if (!mTencent.isSessionValid())
		{
			IUiListener listener = new BaseUiListener();
			mTencent.login(this, SCOPE, listener);
		} 
		
		
	}
	
	
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
			// TODO Auto-generated method stub
			Log.w("TAG", "response="+response.toString());
			mProgressDialog.dismiss();
			
			try { 
				JSONObject jo = (JSONObject) response;  
	            int ret = jo.getInt("ret");
				if (ret == 0) {  
	                String openID = jo.getString("openid");  
	                String accessToken = jo.getString("access_token");  
	                String expires = jo.getString("expires_in");  
	                mTencent.setOpenId(openID);  
	                mTencent.setAccessToken(accessToken, expires);  
	            }  
			} catch (Exception e) {  
                // TODO: handle exception  
            } 
			
			UserInfo userInfo = new UserInfo(ShareAccountActivity.this, mTencent.getQQToken());
			userInfo.getUserInfo(userInfoListener);
			
//			getUserInfo();
			
			qqLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg2);// ???
			qqLoginState.setText("注销");
		}

		@Override
		public void onError(UiError e) {
			// TODO Auto-generated method stub
			Log.w("TAG", "onError..."+"code:" + e.errorCode + ", msg:"
					+ e.errorMessage + ", detail:" + e.errorDetail);
			mProgressDialog.dismiss();
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			Log.w("TAG", "onCancel...");
			mProgressDialog.dismiss();
//			isQQLogin = false;
			Toast.makeText(ShareAccountActivity.this, "已取消登录", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * 登出
	 */
	private void qqLogout(){
		mTencent.logout(ShareAccountActivity.this);
		
		qqLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg);
		qqLoginState.setText("登录");
		
		qqNickname.setText("...");
		// 保存QQ登录后的昵称信息
        BaseApplication.putString(Constant.SET_SHARE_QQ_ACCOUNT, "");
	}
	
	private IUiListener userInfoListener = new IUiListener() {  
        
        @Override  
        public void onError(UiError arg0) {  
            // TODO Auto-generated method stub  
              
        }  
          
        /** 
         * 返回用户信息样例 
         *  
         * {"is_yellow_year_vip":"0","ret":0, 
         * "figureurl_qq_1":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/40", 
         * "figureurl_qq_2":"http:\/\/q.qlogo.cn\/qqapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100", 
         * "nickname":"攀爬←蜗牛","yellow_vip_level":"0","is_lost":0,"msg":"", 
         * "city":"黄冈"," 
         * figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/50", 
         * "vip":"0","level":"0", 
         * "figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/100", 
         * "province":"湖北", 
         * "is_yellow_vip":"0","gender":"男", 
         * "figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/1104732758\/015A22DED93BD15E0E6B0DDB3E59DE2D\/30"} 
         */  
        @Override  
        public void onComplete(Object arg0) {  
            // TODO Auto-generated method stub  
            if(arg0 == null){  
                return;  
            }  
            try {  
                JSONObject jo = (JSONObject) arg0;  
                int ret = jo.getInt("ret");  
                System.out.println("json=" + String.valueOf(jo));  
                String nickName = jo.getString("nickname");  
                String gender = jo.getString("gender");  
                
                
                qqNickname.setText(nickName);
                // 保存QQ登录后的昵称信息
                BaseApplication.putString(Constant.SET_SHARE_QQ_ACCOUNT, nickName);
                
            } catch (Exception e) {  
                // TODO: handle exception  
            }  
              
              
        }  
          
        @Override  
        public void onCancel() {  
            // TODO Auto-generated method stub  
              
        }  
    };  
	
	/**获取用户信息(异步方式调用)*/
//	public void getUserInfo()
//	{
//		mTencent.requestAsync(Constants.GRAPH_BASE, null,
//		Constants.HTTP_GET, new IRequestListener() {
//			
//			@Override
//			public void onUnknowException(Exception arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onSocketTimeoutException(SocketTimeoutException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onNetworkUnavailableException(NetworkUnavailableException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onMalformedURLException(MalformedURLException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onJSONException(JSONException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onIOException(IOException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onHttpStatusException(HttpStatusException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onConnectTimeoutException(ConnectTimeoutException arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onComplete(JSONObject arg0) {
//				// TODO Auto-generated method stub
//				Log.w("TAG", "JSONObject="+arg0.toString());
//			}
//		},
//		null);
//	}
	
	/**--------------------------------weibo-----------------------------------*/

    private void weiboLogin(){
    	AuthInfo mWeiboAuth = new AuthInfo(this, Constant.APP_KEY, Constant.REDIRECT_URL, Constant.SCOPE);
		
		mSsoHandler = new SsoHandler(this, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());
    }
    
    
    /** 登出操作对应的listener */
    private LogOutRequestListener mLogoutListener = new LogOutRequestListener();
    
    private void weiboLogout(){
    	new LogoutAPI(ShareAccountActivity.this, Constant.APP_KEY, AccessTokenKeeper.readAccessToken(ShareAccountActivity.this)).logout(mLogoutListener);
    }
    
    
    private Oauth2AccessToken mAccessToken;
    private UsersAPI mUsersAPI;
    
    
    private class AuthListener implements WeiboAuthListener {
        
    	@Override
	    public void onComplete(Bundle values) {
	    		mAccessToken = Oauth2AccessToken.parseAccessToken(values); // 从 Bundle 中解析 Token
	    		if (mAccessToken.isSessionValid()) {
	            	Log.e("TAG", "收到的Token值为："+mAccessToken);
	            	AccessTokenKeeper.writeAccessToken(ShareAccountActivity.this, mAccessToken); // 保存Token值
	            	mUsersAPI = new UsersAPI(ShareAccountActivity.this, Constant.APP_KEY, mAccessToken); // 获取用户信息接口
	            	
	            	long uid = Long.parseLong(mAccessToken.getUid());
	            	mUsersAPI.show(uid, mListener);

	            } else {
	    		    // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
	                String code = values.getString("code", "");
	            }
	    }

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			mProgressDialog.dismiss();
		}

		@Override
		public void onWeiboException(WeiboException arg0) {
			// TODO Auto-generated method stub
			mProgressDialog.dismiss();
		}
    }

    
    private RequestListener mListener = new RequestListener() {
       
    	@Override
        public void onComplete(String response) {
    		mProgressDialog.dismiss();
    		
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                	Log.e("TAG", "获取User信息成功，用户昵称：" + user.screen_name);
                	weiboNickname.setText(user.screen_name);
                	// 保存微博登录后的昵称信息
                    BaseApplication.putString(Constant.SET_SHARE_SINA_WEIBO_ACCOUNT, user.screen_name);
                    
                    weiboLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg2);// ???
        			weiboLoginState.setText("注销");
                    
                } else {
                	
                }
            }
        }

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			mProgressDialog.dismiss();
			
			LogUtil.e("TAG", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(ShareAccountActivity.this, info.toString(), Toast.LENGTH_LONG).show();
		}
    };

    
    /**
     * 登出按钮的监听器，接收登出处理结果。（API 请求结果的监听器）
     */
    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
        	Log.e("TAG", response);
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");

                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(ShareAccountActivity.this);
                        weiboLoginState.setText("登录");
                        
                        weiboNickname.setText("...");
                    	// 保存微博登录后的昵称信息
                        BaseApplication.putString(Constant.SET_SHARE_SINA_WEIBO_ACCOUNT, "");
                        
                        weiboLinearlayout.setBackgroundResource(R.drawable.tipsbar_text_bg);// ???
                        
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }     

        @Override
        public void onWeiboException(WeiboException e) {
        	weiboLoginState.setText("登出失败");
        }
    }
    
    
}
