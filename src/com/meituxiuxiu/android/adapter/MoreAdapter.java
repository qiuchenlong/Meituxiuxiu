package com.meituxiuxiu.android.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meituxiuxiu.android.R;

public class MoreAdapter extends BaseAdapter {

	private Context context;
	private List<Map<String, Object>> list;
	
	public MoreAdapter(Context context, List<Map<String, Object>> list) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.activity_more_item_layout, null);
			holder.layout = (LinearLayout) convertView.findViewById(R.id.activity_more_item_layout);
			holder.image = (ImageView) convertView.findViewById(R.id.activity_more_item_layout_image);
			holder.title = (TextView) convertView.findViewById(R.id.activity_more_item_layout_title);
			holder.content = (TextView) convertView.findViewById(R.id.activity_more_item_layout_content);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.image.setImageResource(Integer.parseInt(list.get(position).get("image").toString()));
		holder.title.setText(list.get(position).get("title").toString());
		holder.content.setText(list.get(position).get("content").toString());
		
		if(TextUtils.isEmpty(holder.content.getText()) || "".equals(holder.content.getText().toString())){
			holder.content.setVisibility(View.GONE);
		}else{
			holder.content.setVisibility(View.VISIBLE);
		}
		
		holder.layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return convertView;
	}
	
	static class ViewHolder{
		LinearLayout layout;
		ImageView image;
		TextView title;
		TextView content;
		ImageView detail;
	}

}
