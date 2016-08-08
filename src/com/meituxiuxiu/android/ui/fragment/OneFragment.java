package com.meituxiuxiu.android.ui.fragment;

import com.meituxiuxiu.android.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;

public class OneFragment extends Fragment {

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
		View view = inflater.inflate(R.layout.fragment_one, null);
		
		mImageView1 = (ImageView) view.findViewById(R.id.fragment_one_imageview1);
		mImageView2 = (ImageView) view.findViewById(R.id.fragment_one_imageview2);
		mImageView3 = (ImageView) view.findViewById(R.id.fragment_one_imageview3);
		mTranslateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_anim);
		mTranslateAnimation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_anim);
		mScaleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim);
		mScaleAnimation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim_1);
		
//		mImageView1.setVisibility(View.INVISIBLE);
//		mImageView2.setVisibility(View.INVISIBLE);
//		mImageView3.setVisibility(View.INVISIBLE);
		
		Log.e("TAG", "onCreateView...");
		if(flag == true){
			mImageView1.setVisibility(View.VISIBLE);
			mImageView2.setVisibility(View.VISIBLE);
			mImageView3.setVisibility(View.VISIBLE);
		}
		
		return view;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		
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
					flag = true;
				}
			}, 1800);
			
			
			
			
		}else{
			if(mImageView1!=null && mImageView2!=null && mImageView3!=null){
			mImageView1.setVisibility(View.VISIBLE);
			mImageView2.setVisibility(View.VISIBLE);
			mImageView3.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		Log.e("TAG", "onHiddenChanged...");
		if(mImageView1!=null && mImageView2!=null && mImageView3!=null){
			mImageView1.setVisibility(View.VISIBLE);
			mImageView2.setVisibility(View.VISIBLE);
			mImageView3.setVisibility(View.VISIBLE);
		}
	}
	
}
