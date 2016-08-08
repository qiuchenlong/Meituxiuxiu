package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;
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
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Adapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.AutoMeihuaCustomSimpleAdapter;
import com.meituxiuxiu.android.adapter.EnhanceCustomSimpleAdapter;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.AutoMeihuaTextureRenderer;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.TextureRenderer;
import com.meituxiuxiu.android.utils.Util;

/**
 * 智能优化界面
 * 
 * @author qiuchenlong on 2016.03.23
 *
 */
public class AutoMeihuaActivity extends Activity{
	
	private Context mContext;
	
	private Bitmap bitmap;
	private byte[] bitmapByte;
	
	private GLSurfaceView imageview;
    private AutoMeihuaTextureRenderer renderer;
	
    
    // 人脸识别类
    private FaceDetector mFaceDetector;
    private Face[] mFaces;
    int numberOfFaceDetector = 0; // 实际检测到的人脸数
	
	
	private int[] menuIcons = new int[]{R.drawable.auto_meihua_icon_normal, R.drawable.auto_meihua_icon_normal_b,
																		R.drawable.auto_meihua_icon_auto, R.drawable.auto_meihua_icon_auto_b,
																		R.drawable.auto_meihua_icon_food, R.drawable.auto_meihua_icon_food_b,
																		R.drawable.auto_meihua_icon_jingwu, R.drawable.auto_meihua_icon_jingwu_b,
																		R.drawable.auto_meihua_icon_landscape, R.drawable.auto_meihua_icon_landscape_b,
																		R.drawable.auto_meihua_icon_quwu, R.drawable.auto_meihua_icon_quwu_b,
																		R.drawable.auto_meihua_icon_person, R.drawable.auto_meihua_icon_person_b};
//    android:id="@+id/none"
//    android:id="@+id/autofix"
//    android:id="@+id/bw"
//    android:id="@+id/brightness"
//    android:id="@+id/contrast"
//    android:id="@+id/crossprocess"
//    android:id="@+id/documentary"
//    android:id="@+id/duotone"
//    android:id="@+id/filllight"
//    android:id="@+id/fisheye"
//    android:id="@+id/flipvert"
//    android:id="@+id/fliphor"
//    android:id="@+id/grain"
//    android:id="@+id/grayscale"
//    android:id="@+id/lomoish"
//    android:id="@+id/negative"
//    android:id="@+id/posterize"
//    android:id="@+id/rotate"
//    android:id="@+id/saturate"
//    android:id="@+id/sepia"
//    android:id="@+id/sharpen"
//    android:id="@+id/temperature"
//    android:id="@+id/tint"
//    android:id="@+id/vignette"
    
    
    
	private int[] menuIds = new int[]{R.id.none, // 原图
															   R.id.autofix, // 自动
															   R.id.bw, // 美食 
															   R.id.brightness, // 静物 
															   R.id.sharpen, // 风景 
															   R.id.temperature,//  去雾
															   R.id.contrast}; //  人物
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_meihua);
		
		mContext = this;
		
		/**获取传递来的bitmap字节数组*/
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
		int bmpWidth = getIntent().getIntExtra("bitmapWidth", 0);
		int bmpHeight = getIntent().getIntExtra("bitmapHeight", 0);
//		System.out.println("接收2时的length="+bitmapByte.length);
		
		// get sharedpreferences
		String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
		bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
		
		
		
		
		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//		matrix.postScale(2.0f, 2.0f);
//		
//		
//		Log.e("TAG", "width3="+bitmap.getWidth());
//        Log.e("TAG", "Height3="+bitmap.getHeight());
		
//		Bitmap bb = BitmapFactory.decodeResource(getResources(), R.drawable.hemanting);
		
		
		
//		bb.recycle(); 
        
        
        renderer = new AutoMeihuaTextureRenderer();
//        renderer.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
        renderer.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, null, true));
//        renderer.setImageBitmap(bitmap);
        renderer.setCurrentEffect(R.id.none);
        
        imageview = (GLSurfaceView) findViewById(R.id.activity_auto_meihua_imageview);
        imageview.setEGLContextClientVersion(2);
        imageview.setRenderer(renderer);
        imageview.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        
        // 检测人脸
        Bitmap b = bitmap.copy(Bitmap.Config.RGB_565, true);
		mFaceDetector = new FaceDetector(b.getWidth(), b.getHeight(), 1);
		mFaces =new Face[1];
		numberOfFaceDetector = mFaceDetector.findFaces(b, mFaces);
		System.out.println("numberOfFaceDetector="+numberOfFaceDetector);
		if(numberOfFaceDetector == 1){
			renderer.setCurrentEffect(R.id.contrast);
//			Toast.makeText(mContext, "Android api识别到人脸", Toast.LENGTH_SHORT).show();
		} else{
			renderer.setCurrentEffect(R.id.autofix);
		}
        
		
		
		String[] strArray = getResources().getStringArray(R.array.activity_auto_meihua_menu_title_name);
		int[] intArray = new int[]{R.drawable.auto_meihua_icon_normal, R.drawable.auto_meihua_icon_auto, R.drawable.auto_meihua_icon_food, 
										R.drawable.auto_meihua_icon_jingwu, R.drawable.auto_meihua_icon_landscape, R.drawable.auto_meihua_icon_quwu, 
										R.drawable.auto_meihua_icon_person};
		final List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<strArray.length; i++){
			
			if(numberOfFaceDetector == 1){
				intArray[6] = R.drawable.auto_meihua_icon_person_b;
			}else{
				intArray[1] = R.drawable.auto_meihua_icon_auto_b;
			}
			
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("menu_title_icon", intArray[i]);
			map.put("menu_title_name", strArray[i]);
			lists.add(map);
		}
		
		
		AutoMeihuaCustomSimpleAdapter adapter;
		
		if(numberOfFaceDetector == 1){
			adapter = new AutoMeihuaCustomSimpleAdapter(this, lists , R.layout.activity_auto_meihua_menu_layout, 
					new String[]{"menu_title_name" ,"menu_title_icon"}, 
					new int[]{R.id.activity_auto_meihua_menu_title_name, R.id.activity_auto_meihua_menu_title_icon}, 6);
		} else{
			adapter = new AutoMeihuaCustomSimpleAdapter(this, lists , R.layout.activity_auto_meihua_menu_layout, 
					new String[]{"menu_title_name" ,"menu_title_icon"}, 
					new int[]{R.id.activity_auto_meihua_menu_title_name, R.id.activity_auto_meihua_menu_title_icon}, 1);
		}
		
		
		final GridView menuTitle = new GridView(this);
		int width = Util.dip2px(AutoMeihuaActivity.this, 65);
		menuTitle.setColumnWidth(width);
		menuTitle.setNumColumns(GridView.AUTO_FIT);
		menuTitle.setGravity(Gravity.CENTER);
		menuTitle.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		menuTitle.setLayoutParams(params);
		
		menuTitle.setAdapter(adapter);
		
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
					menuTitle = (TextView) menuTitleLayout.findViewById(R.id.activity_auto_meihua_menu_title_name);
					menuIcon = (ImageView) menuTitleLayout.findViewById(R.id.activity_auto_meihua_menu_title_icon);
					menuTitle.setTextColor(Color.rgb(255, 255, 255));
					menuIcon.setImageResource(menuIcons[i*2]);
				}
				
				menuTitleLayout = (LinearLayout) parent.getChildAt(position);
				menuTitle = (TextView) menuTitleLayout.findViewById(R.id.activity_auto_meihua_menu_title_name);
				menuIcon = (ImageView) menuTitleLayout.findViewById(R.id.activity_auto_meihua_menu_title_icon);
				menuTitle.setTextColor(Color.rgb(17, 129, 243));
				menuIcon.setImageResource(menuIcons[position*2+1]);
				
				// GLSurfaceView 图像处理效果
				renderer.setCurrentEffect(menuIds[position]);
		        imageview.requestRender();
				
//				Toast.makeText(AutoMeihuaActivity.this, ""+lists.get(position).get("menu_title_name"), Toast.LENGTH_SHORT).show();
			}
		});
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_auto_meihua_menu_item_layout);
		categoryLayout.addView(menuTitle);
		
		
		final HorizontalScrollView hScrollView = (HorizontalScrollView) findViewById(R.id.activity_auto_meihua_scrollview);
		hScrollView.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(numberOfFaceDetector == 1){
					hScrollView.setScrollX(Util.dip2px(AutoMeihuaActivity.this, 65) * 6);
				}else{
//					hScrollView.setScrollX(Util.dip2px(AutoMeihuaActivity.this, 65) * 1);
				}
			}
		});
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_auto_meihua_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AutoMeihuaActivity.this.finish();
			}
		});
		findViewById(R.id.activity_auto_meihua_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				
				AutoMeihuaTextureRenderer.printOptionEnable = true;
				
				while(AutoMeihuaTextureRenderer.tempBitmapByte==null){
					imageview.requestRender(); // reflush
					bitmapByte = AutoMeihuaTextureRenderer.tempBitmapByte;
				}
				bitmapByte = AutoMeihuaTextureRenderer.tempBitmapByte;
				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.AUTO_MEIHUA_ACTIVITY_REQUEST_CODE, intent);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				AutoMeihuaActivity.this.finish();
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
}
