package com.meituxiuxiu.android.ui;

import java.io.File;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.service.NetworkService;
import com.meituxiuxiu.android.ui.manager.DataCleanManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 设置界面
 * 
 * @author qiuchenlong on 2016.03.30
 *
 */
public class SetActivity extends Activity implements OnClickListener{
	
	private TextView huazi;
	private TextView cacheFileSize;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		
		/**ininData*/
		String huaziInfo = BaseApplication.getString(Constant.SET_HUAZI_INFO);
		
		
		/**initUI*/
	    huazi = (TextView) findViewById(R.id.activity_set_gnsz_hzsz_text);
	    huazi.setText(huaziInfo);
		cacheFileSize = (TextView) findViewById(R.id.activity_set_qt_qlhc_content);
		
		
		
		
		
		try {
			cacheFileSize.setText(DataCleanManager.getCacheSize(SetActivity.this.getExternalCacheDir(), 
					SetActivity.this.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
					this.getCacheDir(),
					new File("/data/data/" + this.getPackageName() + "/databases"),
					new File("/data/data/" + this.getPackageName() + "/shared_prefs")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/**set title bar*/
		setTitleBar(R.drawable.icon_home_selector, "首页", "设置", null, 0);
		
		/**Set Event*/
		findViewById(R.id.activity_set_btn_share_account).setOnClickListener(this);
		
		findViewById(R.id.activity_set_btn_gnsz_hzsz).setOnClickListener(this);
		
		
		findViewById(R.id.activity_set_btn_qt_yjfk).setOnClickListener(this);
		findViewById(R.id.activity_set_btn_about_me).setOnClickListener(this);
		findViewById(R.id.activity_set_btn_kill_cache).setOnClickListener(this);
		
		
		
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

		case R.id.activity_set_btn_share_account:
			Intent shareIntent = new Intent(SetActivity.this, ShareAccountActivity.class);
			startActivity(shareIntent);
			break;
			
		case R.id.activity_set_btn_gnsz_hzsz:
			
			
			final String[] items = new String[]{"一般", "普通", "高清"};
			int checkedItem = 0;
			for(int i=0; i<items.length; i++){
				if(TextUtils.equals(huazi.getText(), items[i])){
					checkedItem = i;
					break;
				}
			}
			
			new AlertDialog.Builder(this).setTitle("画质设置").setSingleChoiceItems(items, checkedItem, new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					huazi.setText(items[arg1]);
					BaseApplication.putString(Constant.SET_HUAZI_INFO, items[arg1]);
				}
			})
			.setNegativeButton("取消", null).show();
			break;
			
		case R.id.activity_set_btn_qt_yjfk:
			if(NetworkService.isEnableNetwork){ // 有网络
				//...
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
			break;
			
		case R.id.activity_set_btn_about_me:
			Intent aboutIntent = new Intent(SetActivity.this, AboutActivity.class);
			startActivity(aboutIntent);
			break;
			
		case R.id.activity_set_btn_kill_cache:
			DataCleanManager.cleanApplicationData(SetActivity.this, null);
			if(TextUtils.equals(cacheFileSize.getText().toString(), "")){
				showDialog("暂无缓存可清理");
			}else{
				showDialog("共节约了"+cacheFileSize.getText().toString()+"大小");
			}
			cacheFileSize.setText("");
			break;
			
		default:
			break;
		}
	}
	
	private void showDialog(String str){
		AlertDialog.Builder builder = new AlertDialog.Builder(SetActivity.this);
//		builder.setCustomTitle(MainActivity.this.getLayoutInflater().inflate(R.layout.activity_main_custom_dialog_title, null));
//		builder.setTitle("美图出品【美颜相机】");
		builder.setTitle(null);
		/**为dialog设置指定的布局文件*/
        View view = SetActivity.this.getLayoutInflater().inflate(R.layout.activity_main_custom_dialog, null);
        builder.setView(view);
        
        TextView title = (TextView) view.findViewById(R.id.activity_main_custom_dialog_title);
        title.setText("清理缓存");
        
        TextView content = (TextView) view.findViewById(R.id.activity_main_custom_dialog_content);
        content.setText(str);
        
        
        
        final AlertDialog alertDialog = builder.create();
        
        ((Button)view.findViewById(R.id.activity_main_custom_dialog_btn_download)).setText("确定");
        /**添加布局控件点击事件的监听器*/
        view.findViewById(R.id.activity_main_custom_dialog_btn_download).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				
			}
		});
        view.findViewById(R.id.activity_main_custom_dialog_btn_cancel).setVisibility(View.GONE);
//        view.findViewById(R.id.activity_main_custom_dialog_btn_download).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				alertDialog.dismiss();
//			}
//		});
        alertDialog.show(); 
	}

}
