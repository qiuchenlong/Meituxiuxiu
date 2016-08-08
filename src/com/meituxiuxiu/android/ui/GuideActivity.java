package com.meituxiuxiu.android.ui;

import java.util.ArrayList;
import java.util.List;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.ViewPagerAdapter;
import com.meituxiuxiu.android.ui.fragment.OneFragment;
import com.meituxiuxiu.android.ui.fragment.ThreeFragment;
import com.meituxiuxiu.android.ui.fragment.TwoFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class GuideActivity extends FragmentActivity {
	
	private ViewPager mViewPager;
	private List<Fragment> fragmentList;
	
	private List<View> dots; // 图片标题正文的那些点
	
	private int currentItem = 0; // 当前图片的索引号
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		
		
		dots = new ArrayList<View>();
		dots.add((View) findViewById(R.id.activity_guide_v_dot0));
		dots.add((View) findViewById(R.id.activity_guide_v_dot1));
		dots.add((View) findViewById(R.id.activity_guide_v_dot2));

		
		
		
		mViewPager = (ViewPager) findViewById(R.id.activity_guide_viewpage);
		
		fragmentList = new ArrayList<Fragment>();
		OneFragment oneFragment = new OneFragment();
		TwoFragment twoFragment = new TwoFragment();
		ThreeFragment threeFragment = new ThreeFragment();
		
		fragmentList.add(oneFragment);
		fragmentList.add(twoFragment);
		fragmentList.add(threeFragment);
		
		mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragmentList));
		mViewPager.setCurrentItem(0);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			private int oldPosition = 0;
			
			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				currentItem = position;
				dots.get(oldPosition).setBackgroundResource(R.drawable.activity_guide_dot_normal);
				dots.get(position).setBackgroundResource(R.drawable.activity_guide_dot_focused);
				oldPosition = position;
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		/**屏蔽back键*/
		if(keyCode == KeyEvent.KEYCODE_BACK)
			return true;
		return super.onKeyDown(keyCode, event);
	}

}
