package com.meituxiuxiu.android.ui.fragment;

import com.meituxiuxiu.android.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class TuPianSpliceFragment extends Fragment {
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onCreateView...");
		View view = inflater.inflate(R.layout.fragment_pinjie, null);
		
		
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
		
		final ScrollView sl = (ScrollView) getActivity().findViewById(R.id.fragment_pinjie_scrollview);
		
		LinearLayout ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		
		ImageView image1 = new ImageView(getActivity());
		ImageView image2 = new ImageView(getActivity());
		ImageView image3 = new ImageView(getActivity());
		ImageView image4 = new ImageView(getActivity());
		ImageView image5 = new ImageView(getActivity());
		ImageView image6 = new ImageView(getActivity());
		
		image1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hemanting));
		image2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		image3.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hemanting));
		image4.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		image5.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hemanting));
		image6.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		
		
		ll.addView(image1);
		ll.addView(image2);
		ll.addView(image3);
		ll.addView(image4);
		ll.addView(image5);
		ll.addView(image6);
		
		
		sl.addView(ll);
		
	}
	
	

}
