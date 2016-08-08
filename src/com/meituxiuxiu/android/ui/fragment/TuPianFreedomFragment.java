package com.meituxiuxiu.android.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.widget.DragView;
import com.meituxiuxiu.android.utils.VibratorUtil;

public class TuPianFreedomFragment extends Fragment {
	
	private static List<Integer> list = new ArrayList<Integer>();
	private static List<String> keyGroup = new ArrayList<String>();
	
    private List<Integer> navList = new ArrayList<Integer>();
    private List<String> moreList = new ArrayList<String>();
	
	private DragListAdapter adapter = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onCreateView...");
		View view = inflater.inflate(R.layout.fragment_freedom, null);
		
		
		return view;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("TAG", "OnCreate...");
	}
	
	
	
	Handler mHandler = new Handler();
	
	Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			VibratorUtil.Vibrate(getActivity(), 200);
		}
	};
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.e("TAG", "onActivityCreated...");
		
		
		
		//初始化数据
				initData();
				ListView dragView = (ListView) getActivity().findViewById(R.id.dragView);
				adapter = new DragListAdapter(getActivity(), list);
				dragView.setAdapter(adapter);
				dragView.setSelector(new ColorDrawable(Color.TRANSPARENT));
				
				
				dragView.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						// TODO Auto-generated method stub
						
						int action = arg1.getAction();
						switch (action) {
						case MotionEvent.ACTION_DOWN:
							DragView.isActionDown = true;
							
							mHandler.postDelayed(mRunnable, 1000);
							
							break;
						case MotionEvent.ACTION_MOVE:
							break;
						case MotionEvent.ACTION_UP:
							
							mHandler.removeCallbacks(mRunnable);
							
							DragView.isActionDown = false;
							if(mListener != null){
								mListener.remove();
							}
							break;
						}
						
						return false;
					}
				});
				
	}
	
	
	
	private static TouchListener mListener;
    
    public interface TouchListener{
    	public void remove();
    }
    public static void setTouchListener(TouchListener listener){
    	mListener = listener;
    }
	
	
	private int[] resId = new int[]{R.drawable.tansongyun, R.drawable.tansongyun3};
	//初始化数据
		public void initData(){
			
			list = new ArrayList<Integer>();
			navList = new ArrayList<Integer>();
			
			keyGroup.add("A组");
			for(int i = 0;i< 6; i++){
				navList.add(resId[i%2]);
			}
//			keyGroup.add("B组");
//			for(int i = 0;i< 8; i++){
//				moreList.add("B组Data0"+i);
//			}
//			list.add(R.drawable.hemanting);
			list.addAll(navList);
//			list.add("B组");
//			list.addAll(moreList);
		}
		
	
	//渲染不同的view
		public static class DragListAdapter extends ArrayAdapter<Integer>{
			
			
			public DragListAdapter(Context context,List<Integer> objects) {
				super(context, 0, objects);
			}
			public List<Integer> getList(){
				return list;
			}


			//检查数据项是否为标题项然后标记是否可以更改
			@Override
			public boolean isEnabled(int position) {
				 if(keyGroup.contains(getItem(position))){
					 return false;
				 }
				return super.isEnabled(position);
			}

			
			
			//利用模板布局不同的listview
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = convertView;
//				if(keyGroup.contains(getItem(position))){
//					//标题组
////					view = LayoutInflater.from(getContext()).inflate(R.layout.textview, null);
//				}else{
					//图片组
					view = LayoutInflater.from(getContext()).inflate(R.layout.textandimage, null);
//				}
//				TextView textView = (TextView) view.findViewById(R.id.headtext);
//				textView.setText(getItem(position));
					
				ImageView v = (ImageView) view.findViewById(R.id.imageView1);
				v.setImageResource(getItem(position));
					
				return view;
			}
		}
	

}
