package com.meituxiuxiu.android.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.WanNengCameraAdapter;
import com.meituxiuxiu.android.ui.opengl.surfaceview.CameraGLSurfaceView;
import com.meituxiuxiu.android.ui.widget.PopWindowTip;
import com.meituxiuxiu.android.utils.Util;

/**
 * 万能相机(OpenGL ES, GLSurfaceView)
 * 
 * @author qiuchenlong on 2016.04.26
 *
 */
public class XiangJi2Activity extends Activity implements OnClickListener{
	
	
	private Context mContext;
	
	private CameraGLSurfaceView glSurfaceView;
	
	
	private ImageView flashSetting, switchCamera, cameraSetting;
	private ImageView btnClose;
	private LinearLayout btnTakePicture, btnPictureFilter;
	
	
	private List<Map<String,Object>> lists;
	private WanNengCameraAdapter adapter;
	
//	 private GLSurfaceView mEffectView;
	
//	 private TextureRenderer renderer;
	
	private boolean isShowScrollview = true;
	HorizontalScrollView scrollView;
	
	 
	private MagicCameraDisplay mMagicCameraDisplay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_xiangji2);
		
		mContext = this;
		
		
		GLSurfaceView glSurfaceView = (GLSurfaceView)findViewById(R.id.glsurfaceview_camera);
		FrameLayout.LayoutParams params = new LayoutParams(1080, 1920);
		glSurfaceView.setLayoutParams(params);
		mMagicCameraDisplay = new MagicCameraDisplay(this, glSurfaceView);
	
		
//		mMagicCameraDisplay.setFilter(MagicFilterType.WHITECAT);
		
		/*glSurfaceView = (CameraGLSurfaceView) findViewById(R.id.camera_textureview);*/
		
		// initUI
		btnTakePicture = (LinearLayout) findViewById(R.id.camera_bottom_bar_btn_take_picture);
		btnPictureFilter = (LinearLayout) findViewById(R.id.camera_bottom_bar_btn_filter);
		scrollView = (HorizontalScrollView) findViewById(R.id.camera_bottom_bar_scrollview);
		switchCamera = (ImageView) findViewById(R.id.camera_header_bar_btn_switch_camera);
		flashSetting = (ImageView) findViewById(R.id.camera_header_bar_btn_set_flash_mode);
		btnClose = (ImageView) findViewById(R.id.camera_bottom_bar_btn_close);
		
		flashSetting.setVisibility(View.GONE);
		
		
		// Add Event
		btnTakePicture.setOnClickListener(this);
		btnPictureFilter.setOnClickListener(this);
		switchCamera.setOnClickListener(this);
		flashSetting.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		
		
		// Init Sound
		initSoundPool();
		
		
//		renderer = new TextureRenderer();
//				renderer.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
//				
//				renderer.setCurrentEffect(R.id.none);
//		        
//		        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
//		        //mEffectView = new GLSurfaceView(this);
//		        mEffectView.setEGLContextClientVersion(2);
//		        //mEffectView.setRenderer(this);
//		        mEffectView.setRenderer(renderer);
//		        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
        
		String[] strArray = getResources().getStringArray(R.array.activity_xiangji2_filter_item_name);
		int[] intArray = new int[]{R.drawable.camera_filter_1_0, R.drawable.camera_filter_1_118, R.drawable.camera_filter_1_122, 
				R.drawable.camera_filter_1_120, R.drawable.camera_filter_1_124, R.drawable.camera_filter_1_126,
				R.drawable.camera_filter_1_130, R.drawable.camera_filter_1_132, R.drawable.camera_filter_1_158,
				R.drawable.camera_filter_1_160,	R.drawable.camera_filter_1_161,
				R.drawable.camera_filter_1_134, R.drawable.camera_filter_1_116, R.drawable.camera_filter_1_113};
		
		lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<intArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("filter_item_icon",  intArray[i]);
			map.put("filter_item_name", strArray[i]);
			lists.add(map);
		}
		
		adapter = new WanNengCameraAdapter(mContext, lists);
		
		
		GridView filterItem = new GridView(this);
		int width = Util.dip2px(XiangJi2Activity.this, 80);
		filterItem.setColumnWidth(width);
		filterItem.setNumColumns(GridView.AUTO_FIT);
		filterItem.setGravity(Gravity.CENTER);
		filterItem.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams mLayoutParams = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		filterItem.setLayoutParams(mLayoutParams);
		
		filterItem.setAdapter(adapter);
		
		filterItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				LinearLayout ll;
				FrameLayout fl ;
				View v ;
				
				Adapter adapter = parent.getAdapter();
				for(int i=0; i<adapter.getCount(); i++){
					ll = (LinearLayout) parent.getChildAt(i);
					fl = (FrameLayout) ll.findViewById(R.id.activity_xiangji2_filter_item_layout);
					v = fl.findViewById(R.id.activity_xiangji2_filter_item_layout_indicator);
					/**复位效果实现*/
					if(mListener != null){
						mListener.onReset(fl, v);
					}
				}
				
				
				fl = (FrameLayout) view.findViewById(R.id.activity_xiangji2_filter_item_layout);
				v = view.findViewById(R.id.activity_xiangji2_filter_item_layout_indicator);
				
				/**选中时的效果实现*/
				if(mListener != null){
					mListener.onChangeVisibility(fl, v);
				}
				
				
				final int tmpint = position;
				final int tmpitem = view.getWidth();
//				lomoSubMenu.scrollTo(view.getWidth()*position, 0);
				
				/*scrollView = (HorizontalScrollView)findViewById(R.id.camera_bottom_bar_scrollview);*/
				
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
							scrollView.scrollTo((int) (orgX+((tmpitem * (tmpint - 1) - tmpitem / 4.0 - 320 )-orgX)*(1000-millisUntilFinished)/(1000)), 0);
						}
						
						@Override
						public void onFinish() {
							// TODO Auto-generated method stub
							scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4 - 320 );
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
				
				
				
				switch (position) {
				case 0:
					mMagicCameraDisplay.setFilter(MagicFilterType.NONE);
					break;
				case 1:
					mMagicCameraDisplay.setFilter(MagicFilterType.AMARO);
					break;
				case 2:
					mMagicCameraDisplay.setFilter(MagicFilterType.ANTIQUE);
					break;
				case 3:
					mMagicCameraDisplay.setFilter(MagicFilterType.BEAUTY);
					break;
				case 4:
					mMagicCameraDisplay.setFilter(MagicFilterType.BLACKCAT);
					break;
				case 5:
					mMagicCameraDisplay.setFilter(MagicFilterType.BRIGHTNESS);
					break;
				case 6:
					mMagicCameraDisplay.setFilter(MagicFilterType.BROOKLYN);
					break;
				case 7:
					mMagicCameraDisplay.setFilter(MagicFilterType.CALM);
					break;
				case 8:
					mMagicCameraDisplay.setFilter(MagicFilterType.CONTRAST);
					break;
				case 9:
					mMagicCameraDisplay.setFilter(MagicFilterType.LOMO);
					break;
				case 10:
					mMagicCameraDisplay.setFilter(MagicFilterType.SWEETS);
					break;
				case 11:
					mMagicCameraDisplay.setFilter(MagicFilterType.WHITECAT);
					break;
				case 12:
					mMagicCameraDisplay.setFilter(MagicFilterType.SKETCH);
					break;
				case 13:
					mMagicCameraDisplay.setFilter(MagicFilterType.INKWELL);
					break;
				}
				// 播放音效
				playSound(2, 0);
				
			}
			
		});
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.camera_bottom_bar_linearlayout);
		categoryLayout.addView(filterItem);
		
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		glSurfaceView.bringToFront();  
//		glSurfaceView.onResume();
//		mEffectView.onResume();
		
		if(mMagicCameraDisplay != null)
			mMagicCameraDisplay.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		glSurfaceView.onPause();
//		mEffectView.onPause();
		
		if(mMagicCameraDisplay != null)
			mMagicCameraDisplay.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(mMagicCameraDisplay != null)
			mMagicCameraDisplay.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.camera_header_bar_btn_switch_camera:
//			CameraGLSurfaceView.switchCamera();
			MagicCameraDisplay.switchCamera();
			
			flashSetting.setVisibility(CameraEngine.getCameraPosition()?View.GONE:View.VISIBLE);
			break;
		case R.id.camera_bottom_bar_btn_take_picture:
//			CameraGLSurfaceView.printOptionEnable = true;
			MagicCameraDisplay.printOptionEnable = true;
//			Toast.makeText(XiangJi2Activity.this, "take_picture", Toast.LENGTH_SHORT).show();
			
			playSound(1, 0);
			
			
			break;
			// 打开闪光灯
		case R.id.camera_header_bar_btn_set_flash_mode:
			// show popupwindow...
			showPopupwindow();
			
			break;
		case R.id.camera_bottom_bar_btn_filter:
			isShowScrollview =! isShowScrollview;
			scrollView.setVisibility(isShowScrollview?View.VISIBLE:View.GONE);
			
			break;
		case R.id.camera_bottom_bar_btn_close:
			XiangJi2Activity.this.finish();
			break;
		}
	}
	
	private PopWindowTip mPopWindowTip;
	private void showPopupwindow(){
		if (mPopWindowTip == null) {  
		    //自定义的单击事件  
		    OnClickLintener paramOnClickListener = new OnClickLintener();  
		    mPopWindowTip = new PopWindowTip(XiangJi2Activity.this, paramOnClickListener, Util.dip2px(mContext, 160), Util.dip2px(mContext, 160));  
		    //监听窗口的焦点事件，点击窗口外面则取消显示  
		    mPopWindowTip.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {  
		          
		        @Override  
		        public void onFocusChange(View v, boolean hasFocus) {  
		            if (!hasFocus) {  
		            	mPopWindowTip.dismiss();  
		            }  
		        }  
		    });  
		}  
		//设置默认获取焦点  
		mPopWindowTip.setFocusable(true);  
		//以某个控件的x和y的偏移量位置开始显示窗口  
		mPopWindowTip.showAsDropDown(flashSetting, -80, 0);  
		//如果窗口存在，则更新  
		mPopWindowTip.update(); 
	}
	
	
	class OnClickLintener implements OnClickListener{  
		  
        @Override  
        public void onClick(View v) {  
            switch (v.getId()) {  
            case R.id.widget_popwindowtip_flash_on:
            	MagicCameraDisplay.setFlashOn();
            	break;
            case R.id.widget_popwindowtip_flash_off:  
            	MagicCameraDisplay.setFlashOff();
                break;  
            }  
              
        }  
          
    } 
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("info", "menu create");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//    	glSurfaceView.setCurrentEffect(item.getItemId());
//        glSurfaceView.requestRender();
    	
    	mMagicCameraDisplay.setFilter(MagicFilterType.WHITECAT);
    	
        return true;
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
	
	
	
	
	private SoundPool sp;
	private HashMap<Integer, Integer> hm;
	private int currStreamId;
	
	private void initSoundPool(){
		sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		hm = new HashMap<Integer, Integer>();
		hm.put(1, sp.load(this, R.raw.camera_timing, 1));
		hm.put(2, sp.load(this, R.raw.camera_beauty_sucess, 1)); // 出来照片效果的音效 key--->2
	}
	
    private void playSound(int sound, int loop){
    	AudioManager am = (AudioManager) XiangJi2Activity.this.getSystemService(Context.AUDIO_SERVICE);
    	float streamVolumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    	float streamVolumeMax = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	float volume = streamVolumeCurrent / streamVolumeMax;
    	currStreamId = sp.play(hm.get(sound), volume, volume, 1, loop, 1.0f);
    }

}
