package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.cloud.LocalSearchInfo;
import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.FilterAdapter;
import com.meituxiuxiu.android.adapter.FrameAdapter;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.service.LocationService;
import com.meituxiuxiu.android.ui.FilterActivity.SelectedListener;
import com.meituxiuxiu.android.ui.opengl.object.SquareByTexture;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView.wordStateOnClickListener;
import com.meituxiuxiu.android.ui.opengl.utils.FontUtil;
import com.meituxiuxiu.android.ui.widget.BottomEditWidget;
import com.meituxiuxiu.android.utils.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.LocalServerSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 边框界面（OpenGL ES 2.0）
 * 
 * @author qiuchenlong on 2016.04.01
 *
 */
public class FrameActivity extends Activity implements OnClickListener, wordStateOnClickListener{
	
	private Context mContext;
	private FrameSurfaceView mView;
	
	private Bitmap bitmap;
	
	private byte[] bitmapByte;
	
	
	private List<Map<String,Object>> lists; 
	
	private FrameAdapter adapter;
	
	
	private GridView frameSubMenu;
	
	
	
	
	private static LinearLayout bottonMenuLayout;
	private static LinearLayout changeWordLayout;
	
	private EditText edittext;
	private TextView btn;
	
	
	
	
	/**全局变量给SurfaceView调用*/
	public static int wordsWidth = (int)(800);
	public static int wordsHeight = 512;
	
	public static float wordsStrideX = 0;
	public static float wordsStrideY = -3.25f;
	
	public static float wordsTextSize = 65f;
	
	
	public static float wordsFrameTop = 0.6f;
	public static float wordsFrameBottom = 0.5f;
	public static float wordsFrameLeft = -1.0f;
	public static float wordsFrameRight = 1.0f;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		
		mContext = this;
		
		
//		startService(new Intent("com.meituxiuxiu.android.intent.action.LocationService"));
		
		
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收2时的length="+bitmapByte.length);
		
		
		// get sharedpreferences
		String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
		bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
		
		
		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		
		mView = new FrameSurfaceView(this, bitmap);
		mView.requestFocus();
		mView.setFocusableInTouchMode(true);
		
		
		
		
		final RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_frame_image_layout);
		rl.addView(mView);
		
//		setContentView(mView);
		
		
		mView.inflate(mContext, R.layout.widget_bottom_edit, null);
		
//		Button b = new Button(this);
//        b.setText( "hello world");
//        b.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
        
		
//		this.addContentView(b, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		
		
		rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "onClick....", Toast.LENGTH_SHORT).show();
				
//				ImageView imageview = (ImageView) findViewById(R.id.activity_frame_image);
//				
//				System.out.println("bitmap = "+mView.getSurfaceViewBitmap(400, 500));
//				
//				imageview.setImageBitmap(mView.getSurfaceViewBitmap(400, 500));
//				mView.setVisibility(View.GONE);
				
			}
		});
		
		
		
		int[] intArray = new int[]{R.drawable.icon_frame_001, R.drawable.icon_frame_002, R.drawable.icon_frame_003, 
				R.drawable.icon_frame_004, R.drawable.icon_frame_005, R.drawable.icon_frame_006, 
				R.drawable.icon_frame_007, R.drawable.icon_frame_008, R.drawable.icon_frame_009};
		
		lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<intArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("frame_title_icon",  intArray[i]);
//			map.put("lomo_submenu_title_name", lomoStrArray[i]);
			lists.add(map);
		}
		
		adapter = new FrameAdapter(mContext, lists);
		
		
		frameSubMenu = new GridView(this);
		int width = Util.dip2px(mContext, 80);
		frameSubMenu.setColumnWidth(width);
		frameSubMenu.setNumColumns(GridView.AUTO_FIT);
		frameSubMenu.setGravity(Gravity.CENTER);
		frameSubMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		frameSubMenu.setLayoutParams(params);
		
		frameSubMenu.setAdapter(adapter);
		
		frameSubMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				FrameLayout fl ;
				View v ;
				
				Adapter adapter = parent.getAdapter();
				for(int i=0; i<adapter.getCount(); i++){
					fl = (FrameLayout) parent.getChildAt(i);
					v = fl.findViewById(R.id.activity_frame_submenu_indicator);
					/**复位效果实现*/
					if(mListener != null){
						mListener.onReset(fl, v);
					}
				}
				
				
				fl = (FrameLayout) view.findViewById(R.id.activity_frame_submenu_layout);
				v = view.findViewById(R.id.activity_frame_submenu_indicator);
				
				/**选中时的效果实现*/
				if(mListener != null){
					mListener.onChangeVisibility(fl, v);
				}
				
				
				final int tmpint = position;
				final int tmpitem = view.getWidth();
//				lomoSubMenu.scrollTo(view.getWidth()*position, 0);
				scrollView = (HorizontalScrollView)findViewById(R.id.activity_frame_scrollview);
//				scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4);
				System.out.println("为负，向--->滑动"+(scrollView.getScrollX()-(tmpitem * (tmpint - 1) - tmpitem / 4)));
				int subValue = scrollView.getScrollX()-(tmpitem * (tmpint - 1) - tmpitem / 4);
				
				final int orgX = scrollView.getScrollX();
				
				if(subValue<0){
					/**倒计时类*/
					new CountDownTimer(1000, 5) {
						
						@Override
						public void onTick(long millisUntilFinished) {
							// TODO Auto-generated method stub
							scrollView.scrollTo((int) (orgX+((tmpitem * (tmpint - 1) - tmpitem / 4.0 - 40 )-orgX)*(1000-millisUntilFinished)/(1000)), 0);
						}
						
						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4 - 40 );
						}
					}.start();
				}else{
					new CountDownTimer(1000, 5) {
						
						@Override
						public void onTick(long millisUntilFinished) {
							// TODO Auto-generated method stub
							Log.e("TAG", "-"+millisUntilFinished/10);
							scrollView.scrollTo((int) (orgX+((tmpitem * (tmpint - 1) - tmpitem / 4.0 + 40 )-orgX)*(1000-millisUntilFinished)/(1000)), 0);
						}
						
						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4 + 40 );
						}
					}.start();
				}
				
				
				// FrameSurfaceView 暴露出来的参数，以便我们可以方便的替换纹理图片
				FrameSurfaceView.isSelected = true;
				FrameSurfaceView.selectIndex = (position+1);
				
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				
				
				switch (position) {
				case 0:
					
					FontUtil.setContent(new String[]{"The earth"});
					
					
					
					wordsWidth = (int)(800);
					wordsHeight = 512;
					
					wordsStrideX = 0;
					wordsStrideY = -3.25f;
					
					wordsTextSize = 65f;
					
					wordsFrameTop = 0.6f;
					wordsFrameBottom = 0.5f;
					wordsFrameLeft = -1.0f;
					wordsFrameRight = 1.0f;
					
					mView.zoom = 1.0f;
					
					break;
				case 1:
					/**改变文字的位置，大小*/
					FontUtil.setContent(new String[]{"NA"});
					
					wordsWidth = 800;
					wordsHeight = 512;
					
					wordsStrideX = -1.75f;	
					wordsStrideY = 2.25f;
					
					wordsTextSize = 350f;
					
					wordsFrameTop = -0.4f;
					wordsFrameBottom = -0.7f; // 如果点击的位置在屏幕的上半部分，Top和Bottom交换位置
					wordsFrameLeft = -1.0f;
					wordsFrameRight = 0.0f;
					
					mView.zoom = 1.6f;
					
					break;
				case 2:
					
					mView.zoom = 0.75f;
					
					break;
				case 3:
					
					
					mView.zoom = 1.8f;
					
					break;
				case 4:
					
					
					mView.zoom = 1.5f;
					
					break;
				case 5:
					
					
					mView.zoom = 1.8f;
					
					break;
				case 6:
					
					
					mView.zoom = 1.5f;
					
					break;
				case 7:
					
					
					mView.zoom = 1.8f;
					
					break;
				case 8:
					
					
					mView.zoom = 1.8f;
					
					break;
				}
			}
			
		});
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_frame_submenu_item_layout);
		categoryLayout.addView(frameSubMenu);
		
		
//		TextView tv = new TextView(mContext);
//		tv.setText("福州大学");
//		tv.setBackgroundColor(Color.RED);
//		tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//		rl.addView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
//
//		rl.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Log.w("TAG", "view="+v);
//			}
//		});
		
		
//		haibaoSubMenu = new GridView(this);
//		int width = Util.dip2px(mContext, 80);
//		haibaoSubMenu.setColumnWidth(width);
//		haibaoSubMenu.setNumColumns(GridView.AUTO_FIT);
//		haibaoSubMenu.setGravity(Gravity.CENTER);
//		haibaoSubMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
//		
//		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
//		haibaoSubMenu.setLayoutParams(params);
//		
//		haibaoSubMenu.setAdapter(adapter);
		
		
		/**海报、简单、炫彩 按钮*/
		findViewById(R.id.activity_frame_btn_haibao).setOnClickListener(this);
		findViewById(R.id.activity_frame_btn_jiandan).setOnClickListener(this);
		findViewById(R.id.activity_frame_btn_xuancai).setOnClickListener(this);
		((TextView) findViewById(R.id.activity_frame_btn_haibao)).setTextColor(Color.rgb(17, 129, 243));;
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_frame_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FrameActivity.this.finish();
			}
		});
		findViewById(R.id.activity_frame_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				FrameSurfaceView.printOptionEnable = true;
				
				while(FrameSurfaceView.tempBitmapByte==null){
					bitmapByte = FrameSurfaceView.tempBitmapByte;
					System.out.println("0--->>"+FrameSurfaceView.tempBitmapByte);
				}
				bitmapByte = FrameSurfaceView.tempBitmapByte;
				System.out.println("1--->>"+FrameSurfaceView.tempBitmapByte);
						
				System.out.println("发送2时的length="+bitmapByte.length);
				
				
//				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.FRAME_ACTIVITY_REQUEST_CODE, intent);
				
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				
				FrameActivity.this.finish();
				
			}
		});
		
		
		/*// Bottom Layout
		bottonMenuLayout = (LinearLayout) findViewById(R.id.activity_frame_bottom_mean_layout);
		changeWordLayout = (LinearLayout) findViewById(R.id.activity_frame_hide_change_word_layout);
		
		
		// Bottom Widget
		edittext = (EditText) findViewById(R.id.activity_frame_hide_change_word_edittext);
		btn = (TextView) findViewById(R.id.activity_frame_hide_change_word_btn);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String[] content = new String[1];
				content[0] = edittext.getText().toString();
				FontUtil.setContent(content);
				
				bottonMenuLayout.setVisibility(View.VISIBLE);
				changeWordLayout.setVisibility(View.GONE);
			}
		});*/
		
		
		// 注册观察者模式为本身
		FrameSurfaceView.setWordOnClickListener(this);
		
	}
	
	
/*	public static void show(){
		bottonMenuLayout.setVisibility(View.GONE);
		changeWordLayout.setVisibility(View.VISIBLE);
	}*/
	
	
	private BottomEditWidget editWidget;
	
	/**
	 * 显示底部编辑文字控件
	 */
	private void showBottomWidget(){
		
		editWidget = new BottomEditWidget(FrameActivity.this, itemsOnClick);

		// 显示窗口
		editWidget.showAtLocation(findViewById(R.id.activity_frame_container), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

		editWidget.setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						
					}
				});
		
		
		/**弹出软键盘*/
	    InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
//		im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);
		im.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
		
	}
	
	
	// 为弹出窗口实现监听类
		private OnClickListener itemsOnClick = new OnClickListener() {

			public void onClick(View v) {
				editWidget.dismiss();
				switch (v.getId()) {
				case R.id.activity_frame_hide_change_word_btn:
					Log.e("TAG", "v="+v);
//					String[] content = new String[1];
//					content[0] = editWidget.getContent();
//					FontUtil.setContent(content);
					break;
				}
			}

		};
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mView.onResume();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mView.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		
		
//		stopService(new Intent("com.meituxiuxiu.android.intent.action.LocationService"));
		
	}
	
	
	private static SelectedListener mListener;
	public static void setSelectedListener(SelectedListener listener){
		mListener = listener;
	}
	
	public interface SelectedListener{
		/**更改layout和indicator的颜色*/
		public void onChangeVisibility(FrameLayout fl, View v);
		/**复位操作*/
		public void onReset(FrameLayout fl, View v);
	}
	
	HorizontalScrollView scrollView;
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		reset2MenuTextColor();
		switch (v.getId()) {
		case R.id.activity_frame_btn_haibao:
			((TextView) findViewById(R.id.activity_frame_btn_haibao)).setTextColor(Color.rgb(17, 129, 243));;
			
			
			break;
		case R.id.activity_frame_btn_jiandan:
			((TextView) findViewById(R.id.activity_frame_btn_jiandan)).setTextColor(Color.rgb(17, 129, 243));;
			
			
			break;
		case R.id.activity_frame_btn_xuancai:
			((TextView) findViewById(R.id.activity_frame_btn_xuancai)).setTextColor(Color.rgb(17, 129, 243));;
			
			
			break;
		}
	}
	
	/**4个menu的颜色复原*/
	private void reset2MenuTextColor(){
		((TextView) findViewById(R.id.activity_frame_btn_haibao)).setTextColor(Color.argb(170, 255, 255, 255));;
		((TextView) findViewById(R.id.activity_frame_btn_jiandan)).setTextColor(Color.argb(170, 255, 255, 255));;
		((TextView) findViewById(R.id.activity_frame_btn_xuancai)).setTextColor(Color.argb(170, 255, 255, 255));;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
//		return super.onTouchEvent(event);
		return false;
	}


	@Override
	public void showEditWidget() {
		// TODO Auto-generated method stub
		showBottomWidget();
	}
	
	
}
