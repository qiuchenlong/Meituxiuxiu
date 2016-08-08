package com.meituxiuxiu.android.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

/**
 * 保存/分享界面
 * @author qiuchenlong on 2016.03.20
 *
 */
public class ShareSavedActivity extends Activity implements OnClickListener{

	private int[] itemIconArray;
	private String[] itemNameArray;
	private List<Map<String,Object>> lists;
	private SimpleAdapter adapter;
	private LinearLayout mLinearLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_saved);
		
		
		/**添加顶部btn按钮事件监听*/
		findViewById(R.id.activity_share_saved_top_left_layout).setOnClickListener(this);
		findViewById(R.id.activity_share_saved_top_right_layout).setOnClickListener(this);
		
		
		/**分享至 item*/
		// 准备数据集
		itemNameArray = getResources().getStringArray(R.array.activity_share_saved_item_name);
		itemIconArray = new int[]{R.drawable.share_wexin_timeline, R.drawable.share_weixin, R.drawable.share_qzone, 
										R.drawable.share_sina_weibo, R.drawable.share_qq_head, R.drawable.share_facebook, 
										R.drawable.share_twitter, R.drawable.share_more1, R.drawable.share_instagram,
										R.drawable.share_line, R.drawable.share_baidu, R.drawable.share_more2};
		lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<itemNameArray.length - 4; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("share_icon", itemIconArray[i]);
			map.put("share_name", itemNameArray[i]);
			lists.add(map);
		}
		// 设置adapter
		adapter = new SimpleAdapter(this, lists , R.layout.activity_share_saved_item_layout, 
				new String[]{"share_icon" ,"share_name"}, 
				new int[]{R.id.activity_share_saved_item_layout_icon, R.id.activity_share_saved_item_layout_name});
		
		final GridView shareItem = new GridView(this);
//		int width = Util.dip2px(ShareSavedActivity.this, 65);
//		shareItem.setColumnWidth(width);
		shareItem.setNumColumns(4);
		shareItem.setGravity(Gravity.CENTER);
		shareItem.setPadding(1, 1, 1, 1);
		shareItem.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		shareItem.setLayoutParams(params);
		
		shareItem.setAdapter(adapter);
		
		/**添加share item的监听事件*/
		shareItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				
				
				
				switch (lists.get(position).get("share_name").toString()) {
				case "更多":
					System.out.println("更多...");
					lists = new ArrayList<Map<String,Object>>();
					for(int i=0; i<itemNameArray.length; i++){
						if(i == itemNameArray.length-4-1){
							continue;
						}
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("share_icon", itemIconArray[i]);
						map.put("share_name", itemNameArray[i]);
						lists.add(map);
					}
					adapter = new SimpleAdapter(ShareSavedActivity.this, lists , R.layout.activity_share_saved_item_layout, 
							new String[]{"share_icon" ,"share_name"}, 
							new int[]{R.id.activity_share_saved_item_layout_icon, R.id.activity_share_saved_item_layout_name});
					shareItem.setAdapter(adapter);
//					/**及时更新数据集adapter*/ 居然不管用，暂时用上面两行的老办法解决
//					adapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			}
		});
		
		/**为linearlayout控件添加girdview*/
		LinearLayout mLinearLayout = (LinearLayout) findViewById(R.id.activity_share_saved_linearlayout);
		mLinearLayout.addView(shareItem);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_share_saved_top_left_layout:
			/**返回按钮*/
			finish();
			break;
		case R.id.activity_share_saved_top_right_layout:
			/**首页按钮*/
			Intent main_Intent = new Intent(ShareSavedActivity.this, MainActivity.class);
			/**为intent添加flags，目的：跳转至MainActivity的同时，清除栈中原有的HomeActivity的实例*/
			/**FLAG_ACTIVITY_CLEAR_TOP：原来栈中存在同样的实例，
			 * 则清除此实例上的所有activity实例，
			 * 以便于我们要的activity实例位于栈的顶部。*/
			main_Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(main_Intent);
			break;
		}
	}
	
}
