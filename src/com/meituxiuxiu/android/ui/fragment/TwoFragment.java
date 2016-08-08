package com.meituxiuxiu.android.ui.fragment;

import com.meituxiuxiu.android.R;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class TwoFragment extends Fragment {

	ImageView mImageView1;
	ImageView mImageView2;
	ImageView mImageView3;
	Animation mTranslateAnimation;
	Animation mTranslateAnimation2;
	Animation mScaleAnimation;
	Animation mScaleAnimation1;
	
	private boolean flag = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onCreateView...");
		View view = inflater.inflate(R.layout.fragment_two, null);
		
		
		mImageView1 = (ImageView) view.findViewById(R.id.fragment_two_imageview1);
		mImageView2 = (ImageView) view.findViewById(R.id.fragment_two_imageview2);
		mImageView3 = (ImageView) view.findViewById(R.id.fragment_two_imageview3);
		mTranslateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_anim);
		mTranslateAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_anim);
		mScaleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim_right);
		mScaleAnimation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim_right_1);
		
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("TAG", "onCreate...");
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e("TAG", "onResume...");
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		Log.e("TAG", "onViewCreated...");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		Log.e("TAG", "onConfigurationChanged...");
	}
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		Log.e("TAG", "onSaveInstanceState...");
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.e("TAG", "onPause...");
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		Log.e("TAG", "setUserVisibleHint..."+isVisibleToUser);
		
		/*if(mImageView1!=null && mImageView2!=null && mImageView3!=null){
			mImageView1.setVisibility(View.INVISIBLE);
			mImageView2.setVisibility(View.INVISIBLE);
			mImageView3.setVisibility(View.INVISIBLE);
			}*/
		
		if(isVisibleToUser && flag!=true){ 
			
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mImageView1.setVisibility(View.VISIBLE);
				mImageView1.startAnimation(mTranslateAnimation);
			}
		}, 300);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mImageView2.setVisibility(View.VISIBLE);
				mImageView2.startAnimation(mTranslateAnimation2);
			}
		}, 1000);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mImageView3.setVisibility(View.VISIBLE);
				mImageView3.startAnimation(mScaleAnimation);
			}
		}, 1500);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mImageView3.setVisibility(View.VISIBLE);
				mImageView3.startAnimation(mScaleAnimation1);
			}
		}, 1800);
		
		
		flag = true;
		
		}else{
			
		}
		
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
//		super.onHiddenChanged(hidden);
		Log.e("TAG", "hidden="+hidden);
		if(hidden == false){
			
		}
	}
	
}
