package com.meituxiuxiu.android.adapter;

import java.io.Flushable;
import java.util.List;
import java.util.Map;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.XiangJi2Activity;

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

public class WanNengCameraAdapter extends BaseAdapter implements XiangJi2Activity.SelectedListener{

	private Context mContext;
	private LayoutInflater mInflater;
	
	private List<Map<String, Object>> mList;
	
	public WanNengCameraAdapter(Context mContext, List<Map<String, Object>> mList) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
		this.mList = mList;
		

		/**注册观察者模式*/
		XiangJi2Activity.setSelectedListener(this);
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
			convertView = mInflater.inflate(R.layout.activity_xiangji2_filter_item_layout, null);
			viewHolder.layout = (FrameLayout) convertView.findViewById(R.id.activity_xiangji2_filter_item_layout);
			viewHolder.icon= (ImageView) convertView.findViewById(R.id.activity_xiangji2_filter_item_layout_imageview);
			viewHolder.name= (TextView) convertView.findViewById(R.id.activity_xiangji2_filter_item_layout_title);
			viewHolder.indicator = convertView.findViewById(R.id.activity_xiangji2_filter_item_layout_indicator);
			convertView.setTag(viewHolder);
		}else{
			viewHolder =  (ViewHolder) convertView.getTag();
		}
		
		viewHolder.icon.setImageResource((int) mList.get(position).get("filter_item_icon"));
//		viewHolder.icon.setImageBitmap((Bitmap)mList.get(position).get("filter_item_name"));
		viewHolder.name.setText(mList.get(position).get("filter_item_name").toString());
		
		
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
		FrameLayout layout;
		ImageView icon;
		TextView name;
		View indicator;
	}

	@Override
	public void onChangeVisibility(FrameLayout fl, View v) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onChangerVisibility...");
		fl.setBackgroundColor(Color.rgb(17, 129, 243));
//		viewHolder.indicator.setBackgroundColor(Color.rgb(0, 0, 255));
		v.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onReset(FrameLayout fl, View v) {
		// TODO Auto-generated method stub
		fl.setBackgroundColor(Color.rgb(00, 00, 00));
		v.setVisibility(View.INVISIBLE);
	}
	

}
