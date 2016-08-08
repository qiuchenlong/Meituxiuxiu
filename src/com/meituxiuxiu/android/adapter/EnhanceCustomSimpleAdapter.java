package com.meituxiuxiu.android.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.meituxiuxiu.android.R;

public class EnhanceCustomSimpleAdapter extends SimpleAdapter {

	public EnhanceCustomSimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = super.getView(position, convertView, parent);
		TextView menuTitle = (TextView) view.findViewById(R.id.activity_home_menu_title_name);
		
		if(position == 0){
			menuTitle.setTextColor(Color.rgb(17, 129, 243));
		}
		
		return view;
	}
	
}
