package com.meituxiuxiu.android.adapter;

import java.io.Flushable;
import java.util.List;
import java.util.Map;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.FilterActivity;
import com.meituxiuxiu.android.ui.PenActivity;
import com.meituxiuxiu.android.utils.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PenAdapter extends BaseAdapter implements PenActivity.SelectedListener{

	private Context mContext;
	private LayoutInflater mInflater;
	
	private List<Map<String, Object>> mList;
	
	public PenAdapter(Context mContext, List<Map<String, Object>> mList) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
		this.mList = mList;
		
		/**注册观察者模式*/
		PenActivity.setSelectedListener(this);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	
	private ViewHolder viewHolder = null;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_pen_menu_layout, null);
			viewHolder.icon= (ImageView) convertView.findViewById(R.id.activity_pen_menu_layout_title_icon);
			viewHolder.name= (TextView) convertView.findViewById(R.id.activity_pen_menu_layout_title_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder =  (ViewHolder) convertView.getTag();
		}
		
		viewHolder.icon.setImageResource((Integer)mList.get(position).get("pen_menu_title_icon"));
		viewHolder.icon.setTranslationY(Util.dip2px(mContext, 20));
		viewHolder.name.setText(mList.get(position).get("pen_menu_title_name").toString());
		
		if(position == 1){
			viewHolder.icon.setTranslationY(Util.dip2px(mContext, 0));
		}
		
		
		
		
		
/*		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "3秒后执行的代码...", Toast.LENGTH_SHORT).show();
//				viewHolder.icon.setImageBitmap(ImageHelper.HandleImagePixelsOldPhoto((Bitmap)mList.get(1).get("lomo_submenu_title_icon")));
				viewHolder.icon.setImageResource(R.drawable.ic_launcher);
			}
		});*/

		
		
		return convertView;
	}
	
	static class ViewHolder{
		ImageView icon;
		TextView name;
	}

	@Override
	public void onChangeVisibility(FrameLayout fl, View v) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onChangerVisibility...");
		((ImageView)v).setTranslationY(Util.dip2px(mContext, 0));
		
	}
	
	@Override
	public void onReset(FrameLayout fl, View v) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onReset...");
		((ImageView)v).setTranslationY(Util.dip2px(mContext, 20));
	}
	

}
