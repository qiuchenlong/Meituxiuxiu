package com.meituxiuxiu.android.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.MoreAdapter;

import android.app.Activity;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MoreActivity extends Activity implements OnClickListener{

	private ListView mListView;
	private MoreAdapter adapter;
	private List<Map<String, Object>> list;
	
	private int[] images = new int[]{R.drawable.item_app_center,R.drawable.item_app_meitu_play,R.drawable.item_app_grid_puzzle,
																R.drawable.item_app_hbgc,R.drawable.item_app_mttt,R.drawable.item_app_bqgc};
	private String[] titles = new String[]{"美图秀秀小应用", "美图游戏盒", "九格切图", "海报工厂", "美图贴贴", "表情工厂"};
	private String[] contents = new String[]{"", "", "朋友圈新玩法！", "照片瞬间高大上", "卖萌贴图神器", "微信必备表情制作神器"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		
		
		/**set title bar*/
		setTitleBar(0, null, "更多功能", "关闭", 0);
		
		

		mListView = (ListView) findViewById(R.id.activity_more_listview);
		
		/**初始化数据*/
		list = new ArrayList<Map<String, Object>>();
		for(int i=0; i<titles.length; i++){
			Map<String, Object> m = new HashMap<String, Object>();
			m.put("image", images[i]);
			m.put("title", titles[i]);
			m.put("content", contents[i]);
			list.add(m);
		}
		
		adapter = new MoreAdapter(this, list);
		mListView.setAdapter(adapter);
		
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
		case R.id.widget_common_top_title_layout_right_layout:
			finish();
			break;

		default:
			break;
		}
	}

}
