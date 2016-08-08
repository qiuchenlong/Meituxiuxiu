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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.PenAdapter;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.ui.widget.PenView;
import com.meituxiuxiu.android.utils.Util;

/**
 * 魔幻笔页面
 * 
 * @author qiuchenlong on 2016.04.06
 *
 */
public class PenActivity extends Activity {
	
	private Context mContext;
	
	public static Bitmap bitmap;
	private byte[] bitmapByte;
	private ImageView imageView;

	/**魔幻笔 名称*/
	private List<Map<String,Object>> lists; 
	
	private PenAdapter adapter;
	
	private GridView penMenu;
	
	
	private String[] lomoStrArray = {"油漆笔","烟花棒","炫光","丝带",
			"彩虹", "四叶草"};
	
	private ImageView qzbPen;
	private Boolean qzbIsSelected = false;
	
	private PenView mPenView;
//	private ImageView mPenView0;
	
	public static List<Paint> undoPaintList = new ArrayList<Paint>();
	public static List<Path> undoPathList = new ArrayList<Path>();
	
	public static Bitmap getBitmap(){
		return bitmap;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	
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
		
		setContentView(R.layout.activity_pen);
		
//		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		
		imageView = (ImageView) findViewById(R.id.activity_pen_imageview0); // 背景（原图）
		imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
		
		
		/**画板*/
		mPenView = (PenView) findViewById(R.id.activity_pen_imageview);
//		mPenView0 = (ImageView) findViewById(R.id.activity_pen_imageview0); // 背景（原图）
		
		
		qzbPen = (ImageView) findViewById(R.id.activity_pen_qzb_icon);
		/**设置签字笔的样式：向下平移20个单位*/
		qzbPen.setTranslationY(Util.dip2px(mContext, 25));
		/**签字笔点击事件*/
		qzbPen.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				qzbIsSelected = !qzbIsSelected;
//				qzbPen.setTranslationY(Util.dip2px(mContext, (qzbIsSelected)?0:25));
				qzbPen.setTranslationY(Util.dip2px(mContext, 0));
				
				
				FrameLayout fl ;
				View v2 ;
				
				for(int i=0; i<penMenu.getAdapter().getCount(); i++){
					fl = (FrameLayout) penMenu.getChildAt(i);
					v2 = fl.findViewById(R.id.activity_pen_menu_layout_title_icon);
					/**复位效果实现*/
					if(mListener != null){
						mListener.onReset(fl, v2);
					}
				}
				
				
				
				/**改变画板中画笔的颜色*/
//				mPenView.setPaintColor(Color.BLACK);
				
				
				Paint paint = new Paint();
		        /**打开抗锯齿效果*/
		        paint.setAntiAlias(true);
		        /**打开图像抖动效果，绘制出来的图片颜色更加平滑、饱和和清晰*/
		        paint.setDither(true);
		        
		        // 设置光源方向
		        float[] direction = new float[]{1, 1, 1};
		        // 设置环境光亮度
		        float light = 0.4f;
		        // 设置反射等级
		        float specular = 6;
		        // 设置模糊度
		        float blur = 3.5f;
		        // 创建一个MaskFilter对象
		        EmbossMaskFilter maskfilter = new EmbossMaskFilter(direction, light, specular, blur);
		        /**设置MaskFilter，实现滤镜效果*/
		        paint.setMaskFilter(maskfilter);
		        
		        paint.setStrokeWidth(3);
		        paint.setStyle(Paint.Style.STROKE);
		        paint.setColor(Color.BLACK);
				/**设置画笔*/
				mPenView.setPaintObject(paint);
				
/*				if(qzbIsSelected){
					mPenView0.setVisibility(View.GONE);
					mPenView.setVisibility(View.VISIBLE);
				}else{
					mPenView0.setVisibility(View.VISIBLE);
					mPenView.setVisibility(View.GONE);
				}*/
				
			}
		});
		
		
		
		 
		 /**魔幻笔 子菜单*/
		 lomoStrArray = getResources().getStringArray(R.array.activity_pen_menu_title_name);
		 int[] intArray = new int[]{R.drawable.mpen_21, R.drawable.mpen_8, R.drawable.mpen_14,
				 R.drawable.mpen_18, R.drawable.mpen_19, R.drawable.mpen_12,
				 R.drawable.mpen_15, R.drawable.mpen_16, R.drawable.mpen_11,
				 R.drawable.mpen_0, R.drawable.mpen_1, R.drawable.mpen_2,
				 R.drawable.mpen_3, R.drawable.mpen_4, R.drawable.mpen_5,
				 R.drawable.mpen_6, R.drawable.mpen_7, R.drawable.mpen_9,
				 R.drawable.mpen_10};
		
		lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<lomoStrArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pen_menu_title_icon",  intArray[i]);
			map.put("pen_menu_title_name", lomoStrArray[i]);
			lists.add(map);
		}
		
		adapter = new PenAdapter(mContext, lists);
		
		
		penMenu = new GridView(this);
		int width = Util.dip2px(mContext, 55);
		penMenu.setColumnWidth(width);
		penMenu.setNumColumns(GridView.AUTO_FIT);
		penMenu.setGravity(Gravity.CENTER);
		penMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		penMenu.setLayoutParams(params);
		
		penMenu.setAdapter(adapter);
		
		
		penMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				qzbPen.setTranslationY(Util.dip2px(mContext, 25));
				
				
				FrameLayout fl ;
				View v ;
				
				Adapter adapter = parent.getAdapter();
				for(int i=0; i<adapter.getCount(); i++){
					fl = (FrameLayout) parent.getChildAt(i);
					v = fl.findViewById(R.id.activity_pen_menu_layout_title_icon);
					/**复位效果实现*/
					if(mListener != null){
						mListener.onReset(fl, v);
					}
				}
				
				fl = (FrameLayout) view.findViewById(R.id.activity_pen_menu_layout);
				v = view.findViewById(R.id.activity_pen_menu_layout_title_icon);
				
				/**选中时的效果实现*/
				if(mListener != null){
					mListener.onChangeVisibility(fl, v);
				}
				
				
				Paint paint = new Paint();
				 /**打开抗锯齿效果*/
		        paint.setAntiAlias(true);
		        /**打开图像抖动效果，绘制出来的图片颜色更加平滑、饱和和清晰*/
		        paint.setDither(true);
		        /**绘制时加入平滑效果*/
		        paint.setStrokeJoin(android.graphics.Paint.Join.ROUND);
		        paint.setStrokeWidth(7);
		        paint.setStyle(Paint.Style.STROKE);
		        
		        // 设置光源方向
		        float[] direction = new float[]{1, 1, 1};
		        // 设置环境光亮度
		        float light = 0.4f;
		        // 设置反射等级
		        float specular = 6;
		        // 设置模糊度
		        float blur = 3.5f;
		        // 创建一个MaskFilter对象
		        EmbossMaskFilter maskfilter = new EmbossMaskFilter(direction, light, specular, blur);
		        /**设置MaskFilter，实现滤镜效果*/
		        paint.setMaskFilter(maskfilter);

		        switch (lists.get(position).get("pen_menu_title_name").toString()) {
				case "油漆笔":
					
			        paint.setColor(Color.RED);
					break;
				case "烟花棒":
					
			        paint.setColor(Color.BLUE);
					break;
				case "眩光":
					
			        // 创建一个MaskFilter对象
			        BlurMaskFilter maskfilter3 = new BlurMaskFilter(20, BlurMaskFilter.Blur.OUTER);
			        /**设置MaskFilter，实现滤镜效果*/
			        paint.setMaskFilter(maskfilter3);
			        paint.setColor(Color.argb(255, 199, 23, 172)); // 紫色
					break;
				case "丝带":
					
			        paint.setColor(Color.YELLOW);
					break;
				case "橡皮擦":
					
					Log.e("TAG", "橡皮擦....");
					paint.setStrokeWidth(35);
					Xfermode xfermode = new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR);
					paint.setXfermode(xfermode);
					break;
					
				}
				
				/**设置画笔*/
				mPenView.setPaintObject(paint);
				
			}
		});
		
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_pen_menu_item_layout);
		categoryLayout.addView(penMenu);
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_pen_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PenActivity.this.finish();
			}
		});
		findViewById(R.id.activity_pen_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				/*
				imageView.setDrawingCacheEnabled(true);
//				bitmap = imageView.getDrawingCache();
				bitmapTemp.compress(Bitmap.CompressFormat.PNG, 100, baos);
				imageView.setDrawingCacheEnabled(false);*/
				mPenView.getmBmp().compress(CompressFormat.PNG, 100, baos);
				bitmapByte = baos.toByteArray();
				System.out.println("发送2时的length="+bitmapByte.length);
				
//				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.Pen_ACTIVITY_REQUEST_CODE, intent);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				PenActivity.this.finish();
			}
		});
		
		
	}

	/**撤销*/
	public void btn_undo(View view){
		int paintListSize = mPenView.paintList.size();
		int pathListSize = mPenView.pathList.size();
		if(paintListSize>0 && pathListSize >0){
			undoPaintList.add(mPenView.paintList.get(paintListSize-1));
			mPenView.paintList.remove(paintListSize-1);
			
			
			undoPathList.add(mPenView.pathList.get(pathListSize-1));
			mPenView.pathList.remove(pathListSize-1);
			
			mPenView.reflush();
		}
	}
	
	/**重做*/
	public void btn_redo(View view){
		if(undoPaintList.size()>0){
			mPenView.paintList.add(undoPaintList.get(undoPaintList.size()-1));
			mPenView.pathList.add(undoPathList.get(undoPathList.size()-1));
			
			undoPaintList.remove(undoPaintList.size()-1);
			undoPathList.remove(undoPathList.size()-1);
			
			mPenView.reflush();
		}
	}
	
	
	/**观察者模式*/
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
	
	
}
