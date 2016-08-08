package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.EnhanceCustomSimpleAdapter;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.widget.BubbleSeekBar;
import com.meituxiuxiu.android.ui.widget.BubbleSeekBar.OnBubbleSeekBarChangeListener;
import com.meituxiuxiu.android.utils.Util;

/**
 *  增强页面
 *  
 * @author qiuchenlong on 2016.04.09
 *
 */
public class EnhanceActivity extends Activity implements SeekBar.OnSeekBarChangeListener{
	
	private Context mContext;
	private Bitmap bitmap;
	private Bitmap bitmapTemp;
	private ImageView imageView;
	private SeekBar mSeekBar = null;
	
	private BubbleSeekBar bubbleSeekBar;
	
	/**色调，饱和度，亮度*/
	private float hue =1,sat = 1,lum = 1,con = 1;
	private boolean isHue, isSat, isLum, isCon;
    private static int MAX_VALUE = 255;
    private static int MID_VALUE = 127;
    
    private int hueProgress = MID_VALUE;
	private int satProgress = MID_VALUE;
	private int lumProgress = MID_VALUE;
	private int conProgress = MID_VALUE;
	
	
	private int[] menuIcons = new int[]{R.drawable.icon_brightness, R.drawable.icon_brightness_a,
																				R.drawable.icon_constrast, R.drawable.icon_constrast_a,
																				R.drawable.icon_color_temperature, R.drawable.icon_color_temperature_a,
																				R.drawable.icon_saturation, R.drawable.icon_saturation_a,
																				R.drawable.icon_bloom, R.drawable.icon_bloom_a,
																				R.drawable.icon_dark, R.drawable.icon_dark_a,
																				R.drawable.icon_smart_exposure, R.drawable.icon_smart_exposure_a};
	
	private byte[] bitmapByte;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enhance);
		
		mContext = this;
		
		/**获取传递来的bitmap字节数组*/
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收2时的length="+bitmapByte.length);
		
		
		// get sharedpreferences
		String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
		bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
				
		
		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
//		bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.hemanting);
		imageView = (ImageView) findViewById(R.id.activity_enhance_imageview);
		
		
		mSeekBar = (SeekBar) findViewById(R.id.activity_enhance_seekbar);
		mSeekBar.setOnSeekBarChangeListener(this);
		
		
		bubbleSeekBar = (BubbleSeekBar) findViewById(R.id.activity_enhance_widget_bubbleseekbar);
		bubbleSeekBar.setOnBubbleSeekBarChangeListener(mOnColorBubbleSeekBarChangeListener);
		
		bubbleSeekBar.setMax(MAX_VALUE);
		bubbleSeekBar.setProgress(MID_VALUE);
		
		
		mSeekBar.setMax(MAX_VALUE);
		mSeekBar.setProgress(MID_VALUE);
		
//		imageView.setImageBitmap(bitmap);
		imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
		Log.e("TAG", "width3="+bitmap.getWidth());
        Log.e("TAG", "Height3="+bitmap.getHeight());
		
		
		String[] strArray = getResources().getStringArray(R.array.activity_enhance_menu_title_name);
		int[] intArray = new int[]{R.drawable.icon_brightness_a, R.drawable.icon_constrast, R.drawable.icon_color_temperature, 
										R.drawable.icon_saturation, R.drawable.icon_bloom, R.drawable.icon_dark, 
										R.drawable.icon_smart_exposure};
		final List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<strArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("menu_title_icon", intArray[i]);
			map.put("menu_title_name", strArray[i]);
			lists.add(map);
		}
		
		
		EnhanceCustomSimpleAdapter adapter = new EnhanceCustomSimpleAdapter(this, lists , R.layout.activity_home_menu_layout, 
				new String[]{"menu_title_name" ,"menu_title_icon"}, 
				new int[]{R.id.activity_home_menu_title_name, R.id.activity_home_menu_title_icon});
		
		
		GridView menuTitle = new GridView(this);
		int width = Util.dip2px(EnhanceActivity.this, 65);
		menuTitle.setColumnWidth(width);
		menuTitle.setNumColumns(GridView.AUTO_FIT);
		menuTitle.setGravity(Gravity.CENTER);
		menuTitle.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		menuTitle.setLayoutParams(params);
		
		menuTitle.setAdapter(adapter);
		
		
		isLum = true;
		menuTitle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				LinearLayout menuTitleLayout;
				TextView menuTitle;
				ImageView menuIcon;
				
				
				Adapter adapter = parent.getAdapter();
				for(int i=0; i<adapter.getCount(); i++){
					menuTitleLayout = (LinearLayout) parent.getChildAt(i);
					menuTitle = (TextView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_name);
					menuIcon = (ImageView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_icon);
					menuTitle.setTextColor(Color.argb(170, 255, 255, 255));
					menuIcon.setImageResource(menuIcons[i*2]);
				}
				
//				menuTitle = (TextView) view.findViewById(R.id.activity_home_menu_title_name);
				menuTitleLayout = (LinearLayout) parent.getChildAt(position);
				menuTitle = (TextView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_name);
				menuIcon = (ImageView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_icon);
				menuTitle.setTextColor(Color.rgb(17, 129, 243));
				menuIcon.setImageResource(menuIcons[position*2+1]);
				
				isHue = false; 
				isSat = false;
				isLum = false;
				isCon = false;
				
//				Toast.makeText(EnhanceActivity.this, ""+lists.get(position).get("menu_title_name"), Toast.LENGTH_SHORT).show();
				switch (lists.get(position).get("menu_title_name").toString()) {
					 
				case "亮度":
					isLum = true;
					mSeekBar.setProgress(lumProgress);
					bubbleSeekBar.setProgress(lumProgress);
					break;
				case "对比度":
					isCon = true;
					mSeekBar.setProgress(conProgress);
					bubbleSeekBar.setProgress(conProgress);
					break;
				case "色温":
					isHue = true;
					mSeekBar.setProgress(hueProgress);
					bubbleSeekBar.setProgress(hueProgress);
					break;
				case "饱和度":
					isSat = true;
					mSeekBar.setProgress(satProgress);
					bubbleSeekBar.setProgress(satProgress);
					break;
				case "高光":
					break;
				case "暗部":
					break;
				case "智能补光":
					break;
				}
			}
		});
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_enhance_menu_item_layout);
		categoryLayout.addView(menuTitle);
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_enhance_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EnhanceActivity.this.finish();
			}
		});
		findViewById(R.id.activity_enhance_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				imageView.setDrawingCacheEnabled(true);
//				bitmap = imageView.getDrawingCache();
				bitmapTemp.compress(Bitmap.CompressFormat.PNG, 100, baos);
				imageView.setDrawingCacheEnabled(false);
				
				bitmapByte = baos.toByteArray();
				System.out.println("发送2时的length="+bitmapByte.length);
				
//				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.ENHANCE_ACTIVITY_REQUEST_CODE, intent);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				EnhanceActivity.this.finish();
			}
		});
	}
	
	
private OnBubbleSeekBarChangeListener mOnColorBubbleSeekBarChangeListener = new OnBubbleSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int progress = (int) (seekBar.getProgress() ); //  / 20.0f
			Log.e("TAG","progress="+progress);
			if(isLum){
				// 亮度
				lum = (float)(progress*1.0f/MID_VALUE); 
				lumProgress = progress;
			}
			if(isCon){
				// 对比度
				con = (float)((progress+MID_VALUE)*1.0f/MAX_VALUE); 
				conProgress = progress;
			}
			if(isHue){
				// 色调
				hue = (float)((progress-MID_VALUE)*1.0f/MID_VALUE*180);
				hueProgress = progress;
			}
			if(isSat){
				// 饱和度
				sat = (float)(progress*1.0f/MID_VALUE);
				satProgress = progress;
			}
			Log.e("TAG","hue="+hue+",sat="+sat+",lum="+lum);
			
			
			/**进行图形效果变换*/
			bitmapTemp = ImageHelper.HandleImageEffect(bitmap, hue, sat, lum, con);
			Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
			matrix.postScale(2.0f, 2.0f);
//			imageView.setImageBitmap(bitmapTemp);
			imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
			
			
			if(popupWindow != null){
				onChangePopupWindowPosition(seekBar, progress);
				onChangePopupWindowValue(progress);
			}
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		if(bitmapTemp != null && !bitmapTemp.isRecycled()){
			bitmapTemp.recycle();
			bitmapTemp = null;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Auto-generated method stub
		if(isLum){
			// 亮度
			lum = (float)(progress*1.0f/MID_VALUE); 
			lumProgress = progress;
		}
		if(isCon){
			// 对比度
			con = (float)((progress+MID_VALUE)*1.0f/MAX_VALUE); 
			conProgress = progress;
		}
		if(isHue){
			// 色调
			hue = (float)((progress-MID_VALUE)*1.0f/MID_VALUE*180);
			hueProgress = progress;
		}
		if(isSat){
			// 饱和度
			sat = (float)(progress*1.0f/MID_VALUE);
			satProgress = progress;
		}
		Log.e("TAG","hue="+hue+",sat="+sat+",lum="+lum);
		
		
		/**进行图形效果变换*/
		bitmapTemp = ImageHelper.HandleImageEffect(bitmap, hue, sat, lum, con);
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
//		imageView.setImageBitmap(bitmapTemp);
		imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
		
		
		if(popupWindow != null){
			onChangePopupWindowPosition(seekBar, progress);
			onChangePopupWindowValue(progress);
		}
		
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		// Show Popupwindow
		showPopupWindow(seekBar, seekBar.getProgress());
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		popupWindow.dismiss();
	}
	
	private PopupWindow popupWindow;
	private TextView tipValue;
	
	private void showPopupWindow(View view, int value) {

        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.widget_seekbar_tip, null);
        
        tipValue = (TextView)contentView.findViewById(R.id.widget_seek_tip_value);
        tipValue.setText(String.valueOf((value-MID_VALUE)*100*2/MAX_VALUE));

        popupWindow = new PopupWindow(contentView,LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(false);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.seekbar_tip));

        // 设置好参数之后再show
//        popupWindow.showAsDropDown(view);
//        popupWindow.showAtLocation(view, Gravity.CENTER, ((SeekBar)view).getProgress(), 400);
        
        popupWindow.showAsDropDown(view, (int)(((SeekBar)view).getX()+((SeekBar)view).getWidth()+55), -((int)((SeekBar)view).getHeight()+100));
        
//        popupWindow.showAsDropDown(contentView, 0, 0, Gravity.CENTER);

    }
	
	private void onChangePopupWindowValue(int value){
		tipValue.setText(String.valueOf((value-MID_VALUE)*100*2/MAX_VALUE));
	}

	private void onChangePopupWindowPosition(View view, int value){
//		popupWindow.showAtLocation(view, Gravity.CENTER, value, 400);
		popupWindow.update(view, (int)(value*2.4)+180, -((int)((SeekBar)view).getHeight()+100), popupWindow.getWidth(), popupWindow.getHeight());
	}
	
}
