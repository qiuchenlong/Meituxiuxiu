package com.meituxiuxiu.android.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.MainActivity;

public class ThreeFragment extends Fragment {

	ImageView mImageView1;
	LinearLayout mLinearLayout2;
	Animation mTranslateAnimation;
	Animation mScaleAnimation;
	Animation mScaleAnimation1;
	
	private boolean flag = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_three, null);
		
		mImageView1 = (ImageView) view.findViewById(R.id.fragment_three_imageview1);
		mLinearLayout2 = (LinearLayout) view.findViewById(R.id.fragment_three_imageview2_layout);
		mTranslateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.translate_anim);
		mScaleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim_center);
		mScaleAnimation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.scale_anim_center_1);
		
		
		if(flag == true){
			mImageView1.setVisibility(View.VISIBLE);
			mLinearLayout2.setVisibility(View.VISIBLE);
		}
		
		
		return view;
	}
	
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		
		/*if(mImageView1!=null && mLinearLayout2!=null){
			mImageView1.setVisibility(View.INVISIBLE);
			mLinearLayout2.setVisibility(View.INVISIBLE);
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
					mLinearLayout2.setVisibility(View.VISIBLE);
					mLinearLayout2.startAnimation(mScaleAnimation);
				}
			}, 1000);
			
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mLinearLayout2.setVisibility(View.VISIBLE);
					mLinearLayout2.startAnimation(mScaleAnimation1);
				}
			}, 1300);
			
			flag = true;
			
		}else{
			if(mImageView1!=null && mLinearLayout2!=null){
			mImageView1.setVisibility(View.VISIBLE);
			mLinearLayout2.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		mLinearLayout2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent main_intent = new Intent(getActivity(), MainActivity.class);
				startActivity(main_intent);
				getActivity().finish();
			}
		});
	}
	
}
