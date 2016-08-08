package com.meituxiuxiu.android.ui;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.photo.model.CameraSdkParameterInfo;
import com.meituxiuxiu.android.utils.Util;

/**
 * 美化图片主界面
 * 
 * @author qiuchenlong on 2016.04.06
 *
 */
public class HomeActivity extends Activity implements OnClickListener{
	
	/**跳转处理图像的requestCode*/
	public static final int AUTO_MEIHUA_ACTIVITY_REQUEST_CODE = 1; // 智能优化
	public static final int EDIT_ACTIVITY_REQUEST_CODE = 2; // 编辑
	public static final int ENHANCE_ACTIVITY_REQUEST_CODE = 3; // 增强
	public static final int FILTER_ACTIVITY_REQUEST_CODE = 4; // 特效
	public static final int FRAME_ACTIVITY_REQUEST_CODE = 5; // 边框
	public static final int Pen_ACTIVITY_REQUEST_CODE = 6; // 魔幻笔
	public static final int Mosaic_ACTIVITY_REQUEST_CODE = 7; // 马赛克
	public static final int WORD_ACTIVITY_REQUEST_CODE = 8; // 文字
	public static final int BLUR_ACTIVITY_REQUEST_CODE = 9; // 背景虚化
	
	private Bitmap bitmap;
	private Bitmap orgBitmap;
	private ImageView mImageView;
	private byte[] bitmapByte;
	private int bitmapWidth, bitmapHeight;
	private int orgBitmapWidth, orgBitmapHeight;
	
	
	/**处理过的bitmap数组*/
	private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
	private List<Bitmap> redoBitmapList = new ArrayList<Bitmap>();
	
	private ImageView undoBtn; // 撤销
	private ImageView redoBtn; // 重做
	
	
	/**对比按钮*/
	private LinearLayout contrastBtn;
	private boolean isContrastBtnEnable;
	
	
	/**加载进HomeActivity中的bitmap路径*/
	private String bitmapPath;
	
	/**progress layout*/
	private LinearLayout mProgressLayout;
	
	
	private CameraSdkParameterInfo mCameraSdkParameterInfo=new CameraSdkParameterInfo();
	private ArrayList<String> resultList = new ArrayList<String>();// 结果数据
	
	private View mViewStub;// 对比
	
	// 是否数据处理完毕
	private boolean isHandleDataDone = false;
	
	// 放大尺寸
	int isScaleSize = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.e("TAG", "onCreate...");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		String[] strArray = getResources().getStringArray(R.array.activity_home_menu_title_name);
		int[] intArray = new int[]{R.drawable.btn_img_edit_selector, R.drawable.btn_img_enhance_selector, R.drawable.btn_img_filter_selector, 
										R.drawable.btn_img_frame_selector, R.drawable.btn_img_magic_pen_selector, R.drawable.btn_img_mosaic_selector, 
										R.drawable.btn_img_word_selector, R.drawable.btn_img_blur_selector};
		final List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<strArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("menu_title_icon", intArray[i]);
			map.put("menu_title_name", strArray[i]);
			lists.add(map);
		}
		
		
		SimpleAdapter adapter = new SimpleAdapter(this, lists , R.layout.activity_home_menu_layout, 
				new String[]{"menu_title_name" ,"menu_title_icon"}, 
				new int[]{R.id.activity_home_menu_title_name, R.id.activity_home_menu_title_icon});
		
		
		GridView menuTitle = new GridView(this);
		int width = Util.dip2px(HomeActivity.this, 65);
		menuTitle.setColumnWidth(width);
		menuTitle.setNumColumns(GridView.AUTO_FIT);
		menuTitle.setGravity(Gravity.CENTER);
		menuTitle.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		menuTitle.setLayoutParams(params);
		
		menuTitle.setAdapter(adapter);
		
		menuTitle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
//				Toast.makeText(HomeActivity.this, ""+lists.get(position).get("menu_title_name"), Toast.LENGTH_SHORT).show();

				// 数据还没有准备好，禁止跳转
				if(isHandleDataDone == false)
					return;
				
				switch (lists.get(position).get("menu_title_name").toString()) {
				case "编辑":
					Intent editIntent = new Intent(HomeActivity.this, EditActivity.class);
//					editIntent.putExtra("bitmapByte", bitmapByte);
					editIntent.putExtra("bitmapWidth", bitmapWidth);
					editIntent.putExtra("bitmapHeight", bitmapHeight);
					startActivityForResult(editIntent, EDIT_ACTIVITY_REQUEST_CODE);
					
//					Intent intent = new Intent(HomeActivity.this, TestOpenGLActivity.class);
//					intent.putExtra("bitmapByte", bitmapByte);
//					startActivity(intent);
					
//					Intent intent = new Intent(HomeActivity.this, EffectsFilterActivity.class);
//					startActivity(intent);
					
					
					break;
				case "增强":
					Intent enhanceIntent = new Intent(HomeActivity.this, EnhanceActivity.class);
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//					byte[] bitmapByte = baos.toByteArray();
					System.out.println("发送时的length="+bitmapByte.length);
//					enhanceIntent.putExtra("bitmapByte", bitmapByte);
					startActivityForResult(enhanceIntent, ENHANCE_ACTIVITY_REQUEST_CODE);
					break;
				case "特效":
					Intent filterIntent = new Intent(HomeActivity.this, FilterActivity.class);
//					filterIntent.putExtra("bitmapByte", bitmapByte);
					startActivityForResult(filterIntent, FILTER_ACTIVITY_REQUEST_CODE);
					break;
				case "边框":
					Intent frameIntent = new Intent(HomeActivity.this, FrameActivity.class);
//					frameIntent.putExtra("bitmapByte", bitmapByte);
//					startActivity(frameIntent);
					startActivityForResult(frameIntent, FRAME_ACTIVITY_REQUEST_CODE);
					break;
				case "魔幻笔":
					Intent penIntent = new Intent(HomeActivity.this, PenActivity.class);
//					penIntent.putExtra("bitmapByte", bitmapByte);
					startActivityForResult(penIntent, Pen_ACTIVITY_REQUEST_CODE);
					break;
				case "马赛克":
					Intent mosaicIntent = new Intent(HomeActivity.this, MosaicActivity.class);
//					mosaicIntent.putExtra("bitmapByte", bitmapByte);
					startActivityForResult(mosaicIntent, Mosaic_ACTIVITY_REQUEST_CODE);
					break;
				case "文字":
					Intent wordIntent = new Intent(HomeActivity.this, WordActivity.class);
//					wordIntent.putExtra("bitmapByte", bitmapByte);
					
					resultList.add(getIntent().getStringExtra("bitmapPath"));
					
					mCameraSdkParameterInfo.setImage_list(resultList);
					Bundle b=new Bundle();
					b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER, mCameraSdkParameterInfo);
					
					wordIntent.putExtras(b);
					
					startActivityForResult(wordIntent, WORD_ACTIVITY_REQUEST_CODE);
					break;
				case "背景虚化":
					Intent blurIntent = new Intent(HomeActivity.this, BlurActivity.class);
//					blurIntent.putExtra("bitmapByte", bitmapByte);
					startActivityForResult(blurIntent, FRAME_ACTIVITY_REQUEST_CODE);
					break;
				}
			}
		});
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_home_menu_item_layout);
		categoryLayout.addView(menuTitle);
		
		
		/** 返回、保存于分享 */
		findViewById(R.id.activity_index_top_left_layout).setOnClickListener(this);
		findViewById(R.id.activity_index_top_right_layout).setOnClickListener(this);
		
		/** 对比按钮 */
		contrastBtn = (LinearLayout)findViewById(R.id.activity_home_btn_contrast);
		contrastBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
				if(arg1.getAction() == MotionEvent.ACTION_DOWN){
					// 只可以findview一次，第二次findviewById 时  view为null
					if (null == mViewStub){
						 if(orgBitmap != null){
			                ViewStub view = (ViewStub)findViewById(R.id.activity_home_view_stub);
			                mViewStub = (View) view.inflate();
			                ImageView image = (ImageView) mViewStub.findViewById(R.id.activity_home_sub_view_imageview);
			                Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
			                matrix.postScale(isScaleSize, isScaleSize);
		               
		                	image.setImageBitmap(Bitmap.createBitmap(orgBitmap, 0, 0, orgBitmapWidth, orgBitmapHeight, matrix, true));
						 }
		            }else{
		            	mViewStub.setVisibility(View.VISIBLE);
		            }
					
					mImageView.setVisibility(View.GONE);
				}else if(arg1.getAction() == MotionEvent.ACTION_UP){
					if (null != mViewStub){
						mViewStub.setVisibility(View.GONE);
			         }
					
					mImageView.setVisibility(View.VISIBLE);
				}
				return true;
			}
		});
		
		
		/**撤销和重做*/
		undoBtn = (ImageView) findViewById(R.id.activity_home_btn_undo);
		redoBtn = (ImageView) findViewById(R.id.activity_home_btn_redo);
		
			
		/**智能优化*/
		findViewById(R.id.activity_home_znyh).setOnClickListener(this);
		
		/**imageview*/
		mImageView = (ImageView) findViewById(R.id.activity_home_imageview);
		
		mProgressLayout = (LinearLayout) findViewById(R.id.activity_home_progress_layout);
		
//		/**获取传递来的bitmap字节数组*/
//		bitmapByte = getIntent().getByteArrayExtra("bitmapByte");
//		System.out.println("接收0时的length="+bitmapByte.length);
//		bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//		matrix.postScale(2.0f, 2.0f);
//		bitmapWidth = bitmap.getWidth();
//		bitmapHeight = bitmap.getHeight();
//		mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true));
//		Log.e("TAG", "width2="+bitmap.getWidth());
//        Log.e("TAG", "Height2="+bitmap.getHeight());
        
        
        /**获取传递来的bitmap路径*/
        bitmapPath = getIntent().getStringExtra("bitmapPath");

        
        
        
        
//        new Handler().post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//		        bitmap = BitmapFactory.decodeFile(bitmapPath);
////		        mImageView.setImageBitmap(bitmap);
//		        
//		        ByteArrayOutputStream os = new ByteArrayOutputStream();
//		        bitmap.compress(CompressFormat.PNG, 100, os);
//		        bitmapByte = os.toByteArray();
//				
//				
//				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//				
//				int w = bitmap.getWidth();
//		        int h = bitmap.getHeight();
//				
//				if(bitmapByte.length>=1024*1024 && bitmapByte.length < 1024*1024*2 || w>=1024 && h>=1024){
//					Options o = new Options();
//					o.inSampleSize = 2;
//					
//					bitmap = BitmapFactory.decodeFile(bitmapPath, o);
//		            
//		            Log.e("TAG", "width="+bitmap.getWidth());
//		            Log.e("TAG", "Height="+bitmap.getHeight());
//		            
//		            baos = new ByteArrayOutputStream();
//					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//					
//					
//					bitmapByte = baos.toByteArray();
//				} else if(bitmapByte.length >= 1024*1024*2 && bitmapByte.length < 1024*1024*4 || w>=1024*2 && h>=1024*2){
//					Options o = new Options();
//					o.inSampleSize = 4;
//		            
//					bitmap = BitmapFactory.decodeFile(bitmapPath, o);
//					
//		            Log.e("TAG", "width="+bitmap.getWidth());
//		            Log.e("TAG", "Height="+bitmap.getHeight());
//		            
//		            baos = new ByteArrayOutputStream();
//					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//					
//					
//					bitmapByte = baos.toByteArray();
//				} else if(bitmapByte.length >= 1024*1024*4 || w>=1024*4 && h>=1024*4){
//					Options o = new Options();
//					o.inSampleSize = 10;
//		            
//					bitmap = BitmapFactory.decodeFile(bitmapPath, o);
//					
//		            Log.e("TAG", "width="+bitmap.getWidth());
//		            Log.e("TAG", "Height="+bitmap.getHeight());
//		            
//		            baos = new ByteArrayOutputStream();
//					bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//					
//					
//					bitmapByte = baos.toByteArray();
//				} 
//				
//				
//				mProgressLayout.setVisibility(View.GONE);
//				mImageView.setVisibility(View.VISIBLE);
//				
//				mImageView.setImageBitmap(bitmap);
//				
//				bitmapWidth = bitmap.getWidth();
//				bitmapHeight = bitmap.getHeight();
//				
//				try {
//					baos.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
        
//        mProgressLayout.setVisibility(View.GONE);
//		mImageView.setVisibility(View.VISIBLE);
//		
//		Options o = new Options();
//		o.inJustDecodeBounds = false;
//		
//		bitmap = BitmapFactory.decodeFile(bitmapPath);
//		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//		matrix.postScale(2.0f, 2.0f);
//		bitmapWidth = bitmap.getWidth();
//		bitmapHeight = bitmap.getHeight();
//		try{
//			mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true));
//		}catch(Exception e){
//			e.printStackTrace();
//			o.inSampleSize = 2;
//			bitmap = BitmapFactory.decodeFile(bitmapPath, o);
//			try{
//				mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true));
//			}catch(Exception e1){
//				e1.printStackTrace();
//				o.inSampleSize = 10;
//				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
//			}
//		}
        
        
        
        new AsyncTask<Void, Void, Bitmap>() {

        	
        	
			@Override
			protected Bitmap doInBackground(Void... params) {
				// TODO Auto-generated method stub
				// 2016.05.14 采用先行计算合适的Options.inSampleSize值来加载bitmap
				
				// 根据图片的路径获取ExifInterface对象
				ExifInterface exif = null;
				try {
					exif = new ExifInterface(bitmapPath);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					exif = null;
				}
				// 获取图片的方向角度
				int digree = 0;
				if(exif != null){
					// 读取图片中相机方向信息  
		             int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
		                     ExifInterface.ORIENTATION_UNDEFINED);  
		             // 计算旋转角度  
		             switch (ori) {  
			             case ExifInterface.ORIENTATION_ROTATE_90:  
			                 digree = 90;  
			                 break;  
			             case ExifInterface.ORIENTATION_ROTATE_180:  
			                 digree = 180;  
			                 break;  
			             case ExifInterface.ORIENTATION_ROTATE_270:  
			                 digree = 270;  
			                 break;  
			             default:  
			                 digree = 0;  
			                 break;  
					}
				}
				
				
				
				int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);   
				Log.d("TAG", "Max memory is " + maxMemory + "KB"); 
				
				
				BitmapFactory.Options opts = new Options();
				opts.inJustDecodeBounds = true;
				// 获取了bitmap的宽高，以便进行计算合适的inSampleSize
				BitmapFactory.decodeFile(bitmapPath, opts);
				// 假设我们的设备是1080*1920的手机
				int minSideLength = Math.min(1080/2, 1920/2);
				opts.inSampleSize=computeSampleSize(opts, minSideLength, 1080*1920/4);
				Log.e("TAG", "opts.inSampleSize="+opts.inSampleSize);
				
				if(opts.inSampleSize>2)
					isScaleSize = opts.inSampleSize/2;
				
				
				opts.inJustDecodeBounds = false;
				// 以下options的两个属性必须联合使用才会有效果
				opts.inInputShareable = true;
				opts.inPurgeable = true; // 设置图片可以被回收
				
				try {
					bitmap = BitmapFactory.decodeFile(bitmapPath, opts);
				} catch (OutOfMemoryError e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
				
				// 根据角度来旋转图片
				if (digree != 0) {  
		             // 旋转图片  
		             Matrix m = new Matrix();  
		             m.postRotate(digree);  
		             bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),  
		                     bitmap.getHeight(), m, true);  
		         }
				
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				bitmapByte = baos.toByteArray();
				
				Log.e("TAG", "bitmapByte.length="+bitmapByte.length);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				BaseApplication.putString(Constant.BITMAP_TO_STRING_ORG, bitmapString);
				
				return bitmap;
				
				/*bitmap = BitmapFactory.decodeFile(bitmapPath);

			int w = bitmap.getWidth();
	        int h = bitmap.getHeight();
	        
	        bitmapWidth = w;
	        bitmapHeight = h;
	        
	        Log.e("TAG", "w="+w+" , h="+h+", bitmap.getByteCount()="+bitmap.getByteCount());

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();;
	        
	        if((bitmap.getByteCount()/4 >= 1024*1024*6) || (w>=1024*6 || h>=1024*6)){ // 6	MB
				Log.e("TAG", "--4--");
				Options o = new Options();
				o.inSampleSize = 16;
	            
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
				
	            Log.e("TAG", "width4="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			} else if((bitmap.getByteCount()/4 >= 1024*1024*5 && bitmap.getByteCount()/4 < 1024*1024*6) || (w>=1024*5 || h>=1024*5)){ // 5	MB
				Log.e("TAG", "--4--");
				Options o = new Options();
				o.inSampleSize = 12;
	            
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
				
	            Log.e("TAG", "width4="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			} else if((bitmap.getByteCount()/4 >= 1024*1024*4 && bitmap.getByteCount()/4 < 1024*1024*5) || (w>=1024*4 || h>=1024*4)){ // 4	MB
				Log.e("TAG", "--4--");
				Options o = new Options();
				o.inSampleSize = 10;
	            
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
				
	            Log.e("TAG", "width4="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			} else if((bitmap.getByteCount()/4 >= 1024*1024*3 && bitmap.getByteCount()/4 < 1024*1024*4) || (w>=1024*3 || h>=1024*3)){ // 3	MB
				Log.e("TAG", "--4--");
				Options o = new Options();
				o.inSampleSize = 8;
	            
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
				
	            Log.e("TAG", "width4="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			} else if((bitmap.getByteCount()/4 >= 1024*1024*2 && bitmap.getByteCount()/4 < 1024*1024*3) || (w>=1024*2 && h>=1024*2)){ // 2	MB
				Log.e("TAG", "--2--");
				Options o = new Options();
				o.inSampleSize = 6;
	            
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
				
	            Log.e("TAG", "width2="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			} else if((bitmap.getByteCount()/4>=1024*1024 && bitmap.getByteCount()/4 < 1024*1024*2) || (w>=1024 && h>=1024)){ // 1	MB
				Log.e("TAG", "--1--");
				Options o = new Options();
				o.inSampleSize = 4;
				
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
	            
	            Log.e("TAG", "width1="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			} else if((bitmap.getByteCount()/4>=1024*1024/2 && bitmap.getByteCount()/4 < 1024*1024) || (w>=512 && h>=512)){ // 500 KB
				Log.e("TAG", "--1--");
				Options o = new Options();
				o.inSampleSize = 2;
				
				bitmap = BitmapFactory.decodeFile(bitmapPath, o);
	            
	            Log.e("TAG", "width1="+bitmap.getWidth());
	            Log.e("TAG", "Height="+bitmap.getHeight());
	            
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
				
				
				bitmapByte = baos.toByteArray();
			}
	        
	        isScaleFlag = false;
	        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
	        bitmapByte = baos.toByteArray();
	        
			try {
				baos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
				return bitmap;*/
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				if(result != null){
				mProgressLayout.setVisibility(View.GONE);
				mImageView.setVisibility(View.VISIBLE);
				
		        Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		        	
		        matrix.postScale(isScaleSize, isScaleSize, 0f, 0f);
		        
		        bitmapWidth = result.getWidth();
				bitmapHeight = result.getHeight();
				
				orgBitmapWidth = bitmapWidth;
				orgBitmapHeight = bitmapHeight;
				
//				if(bitmapWidth*2<4096 && bitmapHeight*2<4096){
					mImageView.setImageBitmap(Bitmap.createBitmap(result, 0, 0, bitmapWidth, bitmapHeight, matrix, true));
					
					bitmapList.add(bitmap); // add to bitmapList
					
//				}else{
//					mImageView.setImageBitmap(result);
//				}
//				mImageView.setImageBitmap(result);
				
				bitmapWidth = result.getWidth();
				bitmapHeight = result.getHeight();
				
				orgBitmap = result;
				
				isHandleDataDone = true;
				
				}
			}
        	
		}.execute();
		
        
		/**read media*/
//		String path = Environment.getExternalStorageDirectory().getPath();
//		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//		retriever.setDataSource(path+"/movie.mp4");
//		
//		Bitmap bitmap = retriever.getFrameAtTime();
//		
//		mImageView.setImageBitmap(bitmap);
		
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(isContrastBtnEnable){
			contrastBtn.setEnabled(true);
		}else{
			contrastBtn.setEnabled(false);
		}
		
		
		
		undoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int size = bitmapList.size();
				if(size>=2){
					undoBtn.setEnabled(true);
					undoBtn.setImageResource(R.drawable.btn_undo_a);
					
					redoBitmapList.add(bitmapList.get(size-1)); // add to redoBitmapList
					
					Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
			        matrix.postScale(isScaleSize, isScaleSize, 0f, 0f);
					mImageView.setImageBitmap(Bitmap.createBitmap(bitmapList.get(size-2), 0, 0, bitmapWidth, bitmapHeight, matrix, true));
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					bitmapList.get(size-2).compress(Bitmap.CompressFormat.PNG, 100, baos);
					bitmapByte = baos.toByteArray();
					// save to sharedpreferences
					String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
					BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
					
					
					bitmapList.remove(size-1);
					
					if(bitmapList.size() == 1){
						undoBtn.setEnabled(false);
						undoBtn.setImageResource(R.drawable.btn_undo_c);
						
						isContrastBtnEnable = false;
						contrastBtn.setBackgroundResource(R.drawable.btn_contrast_a);
					}
				}
				
				
				if(redoBitmapList.size() >=1){
					redoBtn.setEnabled(true);
					redoBtn.setImageResource(R.drawable.btn_redo_a);
				}
			}
		});
		
		redoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int size = redoBitmapList.size();
				if(size>=1){
					
					bitmapList.add(redoBitmapList.get(size-1)); // add to undoBitmapList
					
					Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
			        matrix.postScale(isScaleSize, isScaleSize, 0f, 0f);
					mImageView.setImageBitmap(Bitmap.createBitmap(redoBitmapList.get(size-1), 0, 0, bitmapWidth, bitmapHeight, matrix, true));
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					redoBitmapList.get(size-1).compress(Bitmap.CompressFormat.PNG, 100, baos);
					bitmapByte = baos.toByteArray();
					// save to sharedpreferences
					String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
					BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
					
					redoBitmapList.remove(size-1);
					
					if(redoBitmapList.size() == 0){
						redoBtn.setEnabled(false);
						redoBtn.setImageResource(R.drawable.btn_redo_c);
					}
					
					isContrastBtnEnable = true;
					contrastBtn.setBackgroundResource(R.drawable.btn_contrast_b);
					
				}
					
				
				if(bitmapList.size() >=2){
					undoBtn.setEnabled(true);
					undoBtn.setImageResource(R.drawable.btn_undo_a);
				}
			}
		});
		
	}
	
	
	
	
	/**
	 * 计算合理的inSampleSize
	 * */
	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels){
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if(initialSize <= 8){
			roundedSize = 1;
			while(roundedSize < initialSize){
				roundedSize <<= 1;
			}
		}else{
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		
		return roundedSize;
	}
	
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels){
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if(upperBound < lowerBound){
			return lowerBound;
		}
		
		if((maxNumOfPixels == -1) && (minSideLength == -1)){
			return 1;
		}else if(minSideLength == -1){
			return lowerBound;
		}else{
			return upperBound;
		}
		
	}
	
	
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.e("TAG", "HomeActivity onDestroy...");
		super.onDestroy();
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		if(orgBitmap != null && !orgBitmap.isRecycled()){
			orgBitmap.recycle();
			orgBitmap = null;
		}
	}
	
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_index_top_left_layout:
			finish();
			break;
		case R.id.activity_index_top_right_layout:
			
			// 数据还没有准备好，禁止跳转
			if(isHandleDataDone == false)
				return;
			
			try {
				
				// get sharedpreferences
				String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
				bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
				bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
				Util.saveFile(bitmap, "Meitu_"+System.currentTimeMillis()+".jpg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Intent ShareSaved_Intent = new Intent(HomeActivity.this, ShareSavedActivity.class);
			startActivity(ShareSaved_Intent);
			
//			Toast.makeText(HomeActivity.this, "保存于分享中...", Toast.LENGTH_SHORT).show();
			break;
		case R.id.activity_home_znyh:
			
			// 数据还没有准备好，禁止跳转
			if(isHandleDataDone == false)
				return;
			
			Intent autoMeihuaIntent = new Intent(HomeActivity.this, AutoMeihuaActivity.class);
//			autoMeihuaIntent.putExtra("bitmapByte", bitmapByte);
			autoMeihuaIntent.putExtra("bitmapWidth", bitmapWidth);
			autoMeihuaIntent.putExtra("bitmapHeight", bitmapHeight);
			startActivityForResult(autoMeihuaIntent, AUTO_MEIHUA_ACTIVITY_REQUEST_CODE);
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//		matrix.postScale(2.0f, 2.0f);
		
		Log.e("TAG", "onActivityResult...");
//		if(resultCode == Activity.RESULT_OK){
//			Log.e("TAG", "result_ok...");
			switch (requestCode) {
			case AUTO_MEIHUA_ACTIVITY_REQUEST_CODE:
			case EDIT_ACTIVITY_REQUEST_CODE:
			case ENHANCE_ACTIVITY_REQUEST_CODE:
			case FILTER_ACTIVITY_REQUEST_CODE:
			case Pen_ACTIVITY_REQUEST_CODE:
			case Mosaic_ACTIVITY_REQUEST_CODE:
			case WORD_ACTIVITY_REQUEST_CODE:
				if(data != null){
					// get sharedpreferences
					String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
					bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
//					bitmapByte = data.getByteArrayExtra("bitmapByte");
					bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
					matrix.postScale(isScaleSize, isScaleSize);
					bitmapWidth = bitmap.getWidth();
					bitmapHeight = bitmap.getHeight();
					mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true));
					
					
					bitmapList.add(bitmap); // add to bitmapList
				}
				break;
			
			
//				if(data != null){
//					bitmapByte = data.getByteArrayExtra("bitmapByte");
//					System.out.println("接收时的length="+bitmapByte.length);
////					Options o = new Options();
////					o.inSampleSize = 2;
//					bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//					System.out.println("bitmap.getWidth()="+bitmap.getWidth());
//					System.out.println("bitmap.getHeight()="+bitmap.getHeight());
//					if(bitmap!=null)
//					mImageView.setImageBitmap(bitmap);
//				}
//				break;
			
			
//				Log.e("TAG", "增强界面返回bitmap了");
//				if(data != null){
//					// get sharedpreferences
//					String bitmapString = Meituxiuxiu.getString(Constant.BITMAP_TO_STRING);
//					bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
////					bitmapByte = data.getByteArrayExtra("bitmapByte");
//					System.out.println("接收时的length="+bitmapByte.length);
//					bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
////					Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
//					matrix.postScale(2.0f, 2.0f);
//					System.out.println("bitmap.getWidth()="+bitmap.getWidth());
//					System.out.println("bitmap.getHeight()="+bitmap.getHeight());
//					System.out.println("bitmapWidth="+bitmapWidth);
//					if(bitmap!=null)
//					mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, true));
//				}
			case FRAME_ACTIVITY_REQUEST_CODE: // Frame By OpenGL
				if(data != null){
					// get sharedpreferences
					String bitmapString = BaseApplication.getString(Constant.BITMAP_TO_STRING);
					bitmapByte = Base64.decode(bitmapString, Base64.DEFAULT);
//					bitmapByte = data.getByteArrayExtra("bitmapByte");
					System.out.println("接收时的length="+bitmapByte.length);
					bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
//					mImageView.setImageBitmap(bitmap);
//					Matrix matrix = new Matrix(); //接收图片之后放大 1.5倍
					matrix.postScale(1.5f, 1.5f);
					System.out.println("bitmap="+bitmap);
					mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
					mImageView.setScaleType(ScaleType.FIT_CENTER);
					
					
					bitmapList.add(bitmap); // add to bitmapList
					
				}
				break;
			}
//		}
			
			
			String bitmap_to_string_org = BaseApplication.getString(Constant.BITMAP_TO_STRING_ORG);
			String bitmap_to_string = BaseApplication.getString(Constant.BITMAP_TO_STRING);
			if(!TextUtils.equals(bitmap_to_string_org, bitmap_to_string)){
				isContrastBtnEnable = true;
				contrastBtn.setBackgroundResource(R.drawable.btn_contrast_b);
				
				undoBtn.setEnabled(true);
				undoBtn.setImageResource(R.drawable.btn_undo_a);
			
			}else{
				isContrastBtnEnable = false;
				contrastBtn.setBackgroundResource(R.drawable.btn_contrast_a);
			}
			
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		if(event.getAction() == MotionEvent.BUTTON_BACK){
//			showDialog("", "是否放弃当前操作图片？");
//		}
//		
//		
//		return super.onTouchEvent(event);
//	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(isContrastBtnEnable){
				showDialog("", "是否放弃当前操作图片？", "确定");
				return true;
			}
		}
		
		
		return super.onKeyUp(keyCode, event);
	}
	
	private void showDialog(String titleStr, String contentStr, String btn1Str){
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
//		builder.setCustomTitle(MainActivity.this.getLayoutInflater().inflate(R.layout.activity_main_custom_dialog_title, null));
//		builder.setTitle("美图出品【美颜相机】");
		builder.setTitle(null);
		/**为dialog设置指定的布局文件*/
        View view = HomeActivity.this.getLayoutInflater().inflate(R.layout.activity_main_custom_dialog, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        
        
        TextView title = (TextView) view.findViewById(R.id.activity_main_custom_dialog_title);
        title.setText(titleStr);
        if(TextUtils.isEmpty(titleStr)){
        	title.setVisibility(View.GONE);
        	view.findViewById(R.id.activity_main_custom_dialog_line).setBackgroundColor(Color.TRANSPARENT);
        }
        
        TextView content = (TextView) view.findViewById(R.id.activity_main_custom_dialog_content);
        content.setText(contentStr);
        if(TextUtils.isEmpty(contentStr)){
        	title.setVisibility(View.GONE);
        	view.findViewById(R.id.activity_main_custom_dialog_line).setBackgroundColor(Color.TRANSPARENT);
        }
        
        
        /**添加布局控件点击事件的监听器*/
        Button btn1 = (Button) view.findViewById(R.id.activity_main_custom_dialog_btn_download);
        btn1.setText(btn1Str);
        view.findViewById(R.id.activity_main_custom_dialog_btn_download).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
				HomeActivity.this.finish();
			}
		});
        view.findViewById(R.id.activity_main_custom_dialog_btn_cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
        alertDialog.show(); 
	}
	
	
}
