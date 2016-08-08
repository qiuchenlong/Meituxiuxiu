package com.meituxiuxiu.android.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FragmentPosterSurfaceView;

public class TuPianPosterFragment extends Fragment {
	
	private FragmentPosterSurfaceView mSurfaceView;
	
	
	private Bitmap bmp;
	private Bitmap bmp2;
	private Bitmap bmp3;
	private Bitmap bmp4;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onCreateView...");
		View view = inflater.inflate(R.layout.fragment_poster, null);
		
		
		return view;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("TAG", "OnCreate...");
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.e("TAG", "onActivityCreated...");
		
		
		bmp = BitmapFactory.decodeResource(getResources(), R.drawable.tansongyun);
		bmp2 = BitmapFactory.decodeResource(getResources(), R.drawable.tansongyun1);
		bmp3 = BitmapFactory.decodeResource(getResources(), R.drawable.tansongyun2);
		bmp4 = BitmapFactory.decodeResource(getResources(), R.drawable.tansongyun3);
		
		mSurfaceView = new FragmentPosterSurfaceView(getActivity(), bmp, bmp2, bmp3, bmp4);
		mSurfaceView.requestFocus();
		mSurfaceView.setFocusableInTouchMode(true);
		
		final RelativeLayout rl = (RelativeLayout) getActivity().findViewById(R.id.fragment_poster_image_layout);
		rl.addView(mSurfaceView);
		
	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSurfaceView.onResume();
	}
	
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSurfaceView.onPause();
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(bmp != null && !bmp.isRecycled()){
			bmp.recycle();
			bmp = null;
		}
		if(bmp2 != null && !bmp2.isRecycled()){
			bmp2.recycle();
			bmp2 = null;
		}
		if(bmp3 != null && !bmp3.isRecycled()){
			bmp3.recycle();
			bmp3 = null;
		}
		if(bmp4 != null && !bmp4.isRecycled()){
			bmp4.recycle();
			bmp4 = null;
		}
		
	}
	

}
