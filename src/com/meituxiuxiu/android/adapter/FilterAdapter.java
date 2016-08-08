package com.meituxiuxiu.android.adapter;

import java.io.Flushable;
import java.util.List;
import java.util.Map;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.FilterActivity;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.TextureRenderer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
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

public class FilterAdapter extends BaseAdapter implements FilterActivity.SelectedListener{

	private Context mContext;
	private LayoutInflater mInflater;
	
	private List<Map<String, Object>> mList;
	
	 private TextureRenderer renderer;
	 
	 int[] idInt = new int[]{R.id.none, R.id.autofix, R.id.bw,
			 R.id.brightness, R.id.contrast, R.id.crossprocess,
			 R.id.documentary, R.id.duotone, R.id.filllight,
			 R.id.fisheye, R.id.flipvert, R.id.fliphor,
			 R.id.grain, R.id.grayscale, R.id.lomoish,
			 R.id.negative, R.id.posterize, R.id.rotate,
			 R.id.saturate, R.id.sepia, R.id.sharpen,
			 R.id.temperature, R.id.tint, R.id.vignette};
	
	
	public FilterAdapter(Context mContext, List<Map<String, Object>> mList) {
		// TODO Auto-generated constructor stub
		this.mContext = mContext;
		mInflater = LayoutInflater.from(mContext);
		this.mList = mList;
		
		/**注册观察者模式*/
		FilterActivity.setSelectedListener(this);
		
		
		
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		
		
		
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.activity_filter_lomo_submenu_layout, null);
			viewHolder.layout = (FrameLayout) convertView.findViewById(R.id.activity_filter_lomo_submenu_layout);
			viewHolder.icon= (ImageView) convertView.findViewById(R.id.activity_filter_lomo_submenu_title_icon);
			
//			viewHolder.icon2= (GLSurfaceView) convertView.findViewById(R.id.activity_filter_lomo_submenu_title_icon2);
			
			viewHolder.name= (TextView) convertView.findViewById(R.id.activity_filter_lomo_submenu_title_name);
			viewHolder.indicator = convertView.findViewById(R.id.activity_filter_lomo_submenu_indicator);
			
			

//					renderer = new TextureRenderer();
//					renderer.setImageBitmap((Bitmap)mList.get(position).get("lomo_submenu_title_icon"));
//					renderer.setCurrentEffect(idInt[position]);
//					
//					viewHolder.icon2.setEGLContextClientVersion(2);
//					viewHolder.icon2.setRenderer(renderer);
//					viewHolder.icon2.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
				
			
			
			
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder =  (ViewHolder) convertView.getTag();
		}
		
		viewHolder.icon.setImageBitmap((Bitmap)mList.get(position).get("lomo_submenu_title_icon"));
		
		
		
//		viewHolder.icon.setVisibility(View.GONE);
//		viewHolder.icon2.setVisibility(View.VISIBLE);
		
		
//		renderer.setCurrentEffect(idInt[position]);
//		viewHolder.icon2.requestRender();
		
		
//		viewHolder.icon.requestRender();
		
		
		viewHolder.name.setText(mList.get(position).get("lomo_submenu_title_name").toString());
		
		
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
//		GLSurfaceView icon2;
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
