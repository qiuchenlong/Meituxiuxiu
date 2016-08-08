package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.common.SysOSUtil;
import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.adapter.FilterAdapter;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.AutoMeihuaTextureRenderer;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.TextureRenderer;
import com.meituxiuxiu.android.utils.Util;

/**
 * 图片特效界面
 * 
 * @author qiuchenlong on 2016.04.06
 *
 */
public class FilterActivity extends Activity implements OnClickListener{

	private Context mContext;
	private Bitmap bitmap;
	private Bitmap bitmapTemp;
	private Bitmap bitmapSquare;
//	private ImageView imageView;
	
	private List<Map<String,Object>> lists; 
	
	private FilterAdapter adapter;
	
	private String[] lomoStrArray;
	private String[] meiyanStrArray;
	private String[] gediaoStrArray;
	private String[] yishuStrArray;
	
	private byte[] bitmapByte;
	
	private GridView lomoSubMenu;
	
	
	private int gaussianBlurValue = 100;
	private int blackWhiteValue = 100;
	
	private GLSurfaceView mEffectView;
    private TextureRenderer renderer;
    
    int[] idInt = new int[]{R.id.none, R.id.autofix, R.id.bw,
			 R.id.brightness, R.id.contrast, R.id.crossprocess,
			 R.id.documentary, R.id.duotone, R.id.filllight,
			 R.id.fisheye, R.id.flipvert, R.id.fliphor,
			 R.id.grain, R.id.grayscale, R.id.lomoish,
			 R.id.negative, R.id.posterize, R.id.rotate,
			 R.id.saturate, R.id.sepia, R.id.sharpen,
			 R.id.temperature, R.id.tint, R.id.vignette};
    
//    private GLSurfaceView mmEffectView;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		
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
		
		
//		imageView = (ImageView) findViewById(R.id.activity_filter_imageview);
//		
//		imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
		
		
		
		renderer = new TextureRenderer();
        renderer.setImageBitmap(bitmap);
        renderer.setCurrentEffect(R.id.none);
        
        mEffectView = (GLSurfaceView) findViewById(R.id.activity_filter_imageview);
        //mEffectView = new GLSurfaceView(this);
        mEffectView.setEGLContextClientVersion(2);
        //mEffectView.setRenderer(this);
        mEffectView.setRenderer(renderer);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		
		
		/**LOMO、美颜、格调、艺术 的 子菜单*/
		lomoStrArray = getResources().getStringArray(R.array.activity_filter_lomo_submenu_title_name);
		meiyanStrArray = getResources().getStringArray(R.array.activity_filter_meiyan_submenu_title_name);
		gediaoStrArray = getResources().getStringArray(R.array.activity_filter_gediao_submenu_title_name);
		yishuStrArray = getResources().getStringArray(R.array.activity_filter_yishu_submenu_title_name);
//		int[] intArray = new int[]{R.drawable.icon_brightness_a, R.drawable.icon_constrast, R.drawable.icon_color_temperature, 
//										R.drawable.icon_saturation, R.drawable.icon_bloom, R.drawable.icon_dark, 
//										R.drawable.icon_smart_exposure};
		
		bitmapSquare = bitmap;
		lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<lomoStrArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lomo_submenu_title_icon",  ImageHelper.centerSquareScaleBitmap(bitmapSquare));
			map.put("lomo_submenu_title_name", lomoStrArray[i]);
			lists.add(map);
		}
		
		
//		adapter = new SimpleAdapter(this, lists , R.layout.activity_filter_lomo_submenu_layout, 
//				new String[]{"lomo_submenu_title_icon" ,"lomo_submenu_title_name"}, 
//				new int[]{R.id.activity_filter_lomo_submenu_title_icon, R.id.activity_filter_lomo_submenu_title_name});
		
		adapter = new FilterAdapter(mContext, lists);
		
		
		lomoSubMenu = new GridView(this);
		int width = Util.dip2px(mContext, 80);
		lomoSubMenu.setColumnWidth(width);
		lomoSubMenu.setNumColumns(GridView.AUTO_FIT);
		lomoSubMenu.setGravity(Gravity.CENTER);
		lomoSubMenu.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		lomoSubMenu.setLayoutParams(params);
		
		lomoSubMenu.setAdapter(adapter);
		
		
		
		
		
		/**SubMenu的item进行图片处理*/
//		new Handler().post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				FrameLayout ll = (FrameLayout) adapter.getView(1, null, null);
//				ImageView iv = (ImageView) ll.findViewById(R.id.activity_filter_lomo_submenu_title_icon);
//				GLSurfaceView gv = (GLSurfaceView) ll.findViewById(R.id.activity_filter_lomo_submenu_title_icon2);
//				
//				iv.setVisibility(View.GONE);
////				gv.setVisibility(View.VISIBLE);
//				
//				
//				
//				
////				iv.setDrawingCacheEnabled(true);
////				Bitmap bmp = iv.getDrawingCache();
////				iv.setDrawingCacheEnabled(false);
//				
//				
//				for(int i=0; i<lists.size(); i++){
//					
//					Bitmap icon = (Bitmap)lists.get(i).get("lomo_submenu_title_icon");
////					String name = lists.get(i).get("lomo_submenu_title_name").toString();
//					
//					switch (lists.get(i).get("lomo_submenu_title_name").toString()) {
//					case "经典LOMO":
//						
//						iv.setImageBitmap(ImageHelper.HandleImageBlackWhite(icon, blackWhiteValue));
//						
////						icon = ImageHelper.HandleImageBlackWhite(icon, blackWhiteValue);//ImageHelper.HandleImageLomoFilter(icon);
//						
//						break;
//					case "流年":
//						
//						break;
//					case "黑白":
//						
////						icon = ImageHelper.HandleImageBlackWhite(icon, blackWhiteValue);
//						
//						
//						break;
//					
//					}
//					
////					Map<String, Object> map = new HashMap<String, Object>();
////					map.put("lomo_submenu_title_icon", icon);
////					map.put("lomo_submenu_title_name", name);
////					lists.set(i, map);
//						
//				}
//
//
////				adapter.notifyDataSetChanged();
//				
//			}
//		});
		
		
		lomoSubMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				FrameLayout fl ;
				View v ;
				
				Adapter adapter = parent.getAdapter();
				for(int i=0; i<adapter.getCount(); i++){
					fl = (FrameLayout) parent.getChildAt(i);
					v = fl.findViewById(R.id.activity_filter_lomo_submenu_indicator);
					/**复位效果实现*/
					if(mListener != null){
						mListener.onReset(fl, v);
					}
				}
				
				
				fl = (FrameLayout) view.findViewById(R.id.activity_filter_lomo_submenu_layout);
				v = view.findViewById(R.id.activity_filter_lomo_submenu_indicator);
				
				/**选中时的效果实现*/
				if(mListener != null){
					mListener.onChangeVisibility(fl, v);
				}
				
				
				final int tmpint = position;
				final int tmpitem = view.getWidth();
//				lomoSubMenu.scrollTo(view.getWidth()*position, 0);
				scrollView = (HorizontalScrollView)findViewById(R.id.activity_filter_scrollview);
//				scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4);
				System.out.println("为负，向--->滑动"+(scrollView.getScrollX()-(tmpitem * (tmpint - 1) - tmpitem / 4)));
				int subValue = scrollView.getScrollX()-(tmpitem * (tmpint - 1) - tmpitem / 4);
				
				final int orgX = scrollView.getScrollX();
				
				if(subValue<0){
//					scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4 - 40 );
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
//					scrollView.setScrollX(tmpitem * (tmpint - 1) - tmpitem / 4 + 40 );
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
				
				
				
				bitmapTemp = bitmap;
				
				final Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
				matrix.postScale(2.0f, 2.0f);
				
				
				
				
				
				new AsyncTask<Integer, Void, Bitmap>() {

					private Dialog mProgressDialog;

					@Override
					protected void onPreExecute() {
						// TODO Auto-generated method stub
						mProgressDialog = createLoadingDialog(FilterActivity.this, "");
						mProgressDialog.show();
					}

					@Override
					protected Bitmap doInBackground(Integer... params) {
						// TODO Auto-generated method stub
						renderer.setCurrentEffect(params[0]);
						return bitmapTemp;
					}

					@Override
					protected void onPostExecute(Bitmap result) {
						// TODO Auto-generated method stub
						mProgressDialog.dismiss();
						mEffectView.requestRender();
						super.onPostExecute(result);
					}

				}.execute(idInt[position]);
				
				
//				renderer.setCurrentEffect(idInt[position]);
//		        mEffectView.requestRender();
				
				switch (lists.get(position).get("lomo_submenu_title_name").toString()) {
				case "原图":
//					imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
					break;
				case "经典LOMO":
//					new AsyncTask<Bitmap, Void, Bitmap>() {
//
//						private ProgressDialog dialog;
//						@Override
//						protected void onPreExecute() {
//							// TODO Auto-generated method stub
//							dialog = ProgressDialog.show(FilterActivity.this, null, null);
//						}
//						
//						@Override
//						protected Bitmap doInBackground(Bitmap... params) {
//							// TODO Auto-generated method stub
//							bitmapTemp = ImageHelper.HandleImageLomoFilter(params[0]);
//							return bitmapTemp;
//						}
//
//						@Override
//						protected void onPostExecute(Bitmap result) {
//							// TODO Auto-generated method stub
//							dialog.dismiss();
//							if(result != null)
////							imageView.setImageBitmap(Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true));
//							super.onPostExecute(result);
//						}
//						
//					}.execute(bitmap);
					break;
				case "流年":
//					Toast.makeText(mContext, "电影海报效果", Toast.LENGTH_SHORT).show();
//					bitmapTemp = ImageHelper.HandleImageMoviePoster(bitmap);
					
//					imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
					break;
				case "淡雅":
//					Toast.makeText(mContext, "朦胧柔化效果", Toast.LENGTH_SHORT).show();
//					bitmapTemp = ImageHelper.HandleImageGaussianBlur(mContext, bitmap, gaussianBlurValue/4.0f);
					
//					imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
					break;
				case "黑白":
//					bitmapTemp = ImageHelper.HandleImageBlackWhite(bitmap, blackWhiteValue);
					
//					imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
					break;
				}
			}
			
		});
		
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_filter_submenu_item_layout);
		categoryLayout.addView(lomoSubMenu);
		
		
		
		/**Lomo、美颜、格调、艺术 按钮*/
		findViewById(R.id.activity_filter_btn_lomo).setOnClickListener(this);
		findViewById(R.id.activity_filter_btn_meiyan).setOnClickListener(this);
		findViewById(R.id.activity_filter_btn_gediao).setOnClickListener(this);
		findViewById(R.id.activity_filter_btn_yishu).setOnClickListener(this);
		((TextView) findViewById(R.id.activity_filter_btn_lomo)).setTextColor(Color.rgb(17, 129, 243));;
		
		
		/**seekbar*/
		SeekBar mSeekBar = (SeekBar) findViewById(R.id.activity_filter_seekbar);
		mSeekBar.setMax(100);
		mSeekBar.setProgress(100);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
				gaussianBlurValue = progress;
				bitmapTemp = ImageHelper.HandleImageGaussianBlur(mContext, bitmap, gaussianBlurValue/4.0f);
				
				blackWhiteValue = progress;
//				bitmapTemp = ImageHelper.HandleImageBlackWhite(bitmap, blackWhiteValue);
				Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
				matrix.postScale(2.0f, 2.0f);
//				imageView.setImageBitmap(Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), matrix, true));
			}
		});
		
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_filter_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FilterActivity.this.finish();
			}
		});
		findViewById(R.id.activity_filter_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				TextureRenderer.printOptionEnable = true;
				
				while(TextureRenderer.tempBitmapByte==null){
					mEffectView.requestRender(); // reflush
					bitmapByte = TextureRenderer.tempBitmapByte;
				}
				bitmapByte = TextureRenderer.tempBitmapByte;
				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.FILTER_ACTIVITY_REQUEST_CODE, intent);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				FilterActivity.this.finish();
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
		if(bitmapTemp != null && !bitmapTemp.isRecycled()){
			bitmapTemp.recycle();
			bitmapTemp = null;
		}
		if(bitmapSquare != null && !bitmapSquare.isRecycled()){
			bitmapSquare.recycle();
			bitmapSquare = null;
		}
		
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		reset2MenuTextColor();
		switch (v.getId()) {
		case R.id.activity_filter_btn_lomo:
			((TextView) findViewById(R.id.activity_filter_btn_lomo)).setTextColor(Color.rgb(17, 129, 243));;
			reset2SubMenuIcon(lomoStrArray);
			
			
			break;
		case R.id.activity_filter_btn_meiyan:
			((TextView) findViewById(R.id.activity_filter_btn_meiyan)).setTextColor(Color.rgb(17, 129, 243));;
			reset2SubMenuIcon(meiyanStrArray);
			
			
//			adapter.notifyDataSetChanged();
			break;
		case R.id.activity_filter_btn_gediao:
			((TextView) findViewById(R.id.activity_filter_btn_gediao)).setTextColor(Color.rgb(17, 129, 243));;
			reset2SubMenuIcon(gediaoStrArray);
			
			
			break;
		case R.id.activity_filter_btn_yishu:
			((TextView) findViewById(R.id.activity_filter_btn_yishu)).setTextColor(Color.rgb(17, 129, 243));;
			reset2SubMenuIcon(yishuStrArray);
			
			
			break;
		}
	}
	
	
	/**4个menu的颜色复原*/
	private void reset2MenuTextColor(){
		((TextView) findViewById(R.id.activity_filter_btn_lomo)).setTextColor(Color.argb(170, 255, 255, 255));;
		((TextView) findViewById(R.id.activity_filter_btn_meiyan)).setTextColor(Color.argb(170, 255, 255, 255));;
		((TextView) findViewById(R.id.activity_filter_btn_gediao)).setTextColor(Color.argb(170, 255, 255, 255));;
		((TextView) findViewById(R.id.activity_filter_btn_yishu)).setTextColor(Color.argb(170, 255, 255, 255));;
	}
	
	HorizontalScrollView scrollView;
	
	private void reset2SubMenuIcon(String[] strArray){
//		lists = new ArrayList<Map<String,Object>>();
		/**清楚数据lists*/
		lists.clear();
		for(int i=0; i<strArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("lomo_submenu_title_icon",  ImageHelper.centerSquareScaleBitmap(bitmapSquare));
			map.put("lomo_submenu_title_name", strArray[i]);
			lists.add(map);
		}
//		adapter = new FilterAdapter(mContext, lists);
		int width = Util.dip2px(mContext, 80);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		lomoSubMenu.setLayoutParams(params);
//		lomoSubMenu.setAdapter(adapter);
		
		/**更新适配器*/
		adapter.notifyDataSetChanged();
		
		scrollView = (HorizontalScrollView)findViewById(R.id.activity_filter_scrollview);
		scrollView.setScrollX(0);
	}
	
	
	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
		Toast.makeText(mContext, "内存吃紧了...", Toast.LENGTH_SHORT).show();
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
    	System.out.println("item.getItemId()="+item.getItemId());
        renderer.setCurrentEffect(item.getItemId());
        mEffectView.requestRender();
        return true;
    }
    
    
    
    
    /** 
     * 得到自定义的progressDialog 
     * @param context 
     * @param msg 
     * @return 
     */  
    public static Dialog createLoadingDialog(Context context, String msg) {  
  
        LayoutInflater inflater = LayoutInflater.from(context);  
        View v = inflater.inflate(R.layout.wedget_loading_dialog, null);// 得到加载view  
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局  
        // main.xml中的ImageView  
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);  
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字  
        // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                context, R.anim.wedget_loading_animation);  
        // 使用ImageView显示动画  
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);  
        tipTextView.setText(msg);// 设置加载信息  
  
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog  
  
        loadingDialog.setCancelable(true);// 可以用“返回键”取消  
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LinearLayout.LayoutParams.FILL_PARENT,  
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局  
        
        return loadingDialog;  
  
    }
    
	
}
