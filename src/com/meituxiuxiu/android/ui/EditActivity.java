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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.base.BaseApplication;
import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.cropimage.CropImageView;
import com.meituxiuxiu.android.helper.ImageHelper;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.AutoMeihuaTextureRenderer;
import com.meituxiuxiu.android.utils.Bitmap2DrawableUtil;
import com.meituxiuxiu.android.utils.Util;

/**
 * 编辑页面
 * 
 * @author qiuchenlong on 2016.04.06
 *
 */
public class EditActivity extends Activity implements OnClickListener{
	
	
	private Context mContext;
	private Bitmap bitmap;
	
//	private ImageView imageView;
	
	private CropImageView imageView;
	
	
	private byte[] bitmapByte;
	
	
	private RelativeLayout clipLayout, rotateLayout, sharpLayout;
	private TextView clipText, rotateText, sharpText;
	
	
	private int[] menuIcons = new int[]{R.drawable.edit_cut_crop_freedom_a, R.drawable.edit_cut_crop_freedom_b, 
			R.drawable.edit_cut_crop_1_1_a, R.drawable.edit_cut_crop_1_1_b, 
			R.drawable.edit_cut_crop_2_3_a, R.drawable.edit_cut_crop_2_3_b, 
			R.drawable.edit_cut_crop_3_2_a, R.drawable.edit_cut_crop_3_2_b, 
			R.drawable.edit_cut_crop_3_4_a, R.drawable.edit_cut_crop_3_4_b, 
			R.drawable.edit_cut_crop_4_3_a, R.drawable.edit_cut_crop_4_3_b, 
			R.drawable.edit_cut_crop_16_9_a, R.drawable.edit_cut_crop_16_9_b};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		
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
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		
		
		/*String[] strArray = new String[]{"自由", "1:1", "2:3", "3:2", "3:4", "4:3", "16:9"};
		int[] intArray = new int[]{R.drawable.edit_cut_crop_freedom_b, R.drawable.edit_cut_crop_1_1_a, R.drawable.edit_cut_crop_2_3_a, 
										R.drawable.edit_cut_crop_3_2_a, R.drawable.edit_cut_crop_3_4_a, R.drawable.edit_cut_crop_4_3_a, 
										R.drawable.edit_cut_crop_16_9_a};
		final List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
		for(int i=0; i<strArray.length; i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon", intArray[i]);
			map.put("name", strArray[i]);
			lists.add(map);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, lists , R.layout.activity_home_menu_layout, 
				new String[]{"name" ,"icon"}, 
				new int[]{R.id.activity_home_menu_title_name, R.id.activity_home_menu_title_icon});
		
		
		GridView menuTitle = new GridView(this);
		int width = Util.dip2px(EditActivity.this, 65);
		menuTitle.setColumnWidth(width);
		menuTitle.setNumColumns(GridView.AUTO_FIT);
		menuTitle.setGravity(Gravity.CENTER);
		menuTitle.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		LayoutParams params = new LayoutParams(width*lists.size(), LayoutParams.WRAP_CONTENT);
		menuTitle.setLayoutParams(params);
		
		menuTitle.setAdapter(adapter);
		
		
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.activity_edit_clip_item_layout);
		categoryLayout.addView(menuTitle);*/
		
		
		
		imageView = (CropImageView) findViewById(R.id.activity_edit_image);
//		imageView.setDrawable(new BitmapDrawable(bitmap), bitmap.getWidth(), bitmap.getHeight());// .setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
		imageView.setDrawable(new BitmapDrawable(bitmap), bmpWidth, bmpHeight);
		
		
		
		clipLayout = (RelativeLayout) findViewById(R.id.activity_edit_clip_layout);
		rotateLayout = (RelativeLayout) findViewById(R.id.activity_edit_rotate_layout);
		sharpLayout = (RelativeLayout) findViewById(R.id.activity_edit_sharp_layout);
		
		
		/**编辑菜单(裁剪、旋转、锐化)*/
		findViewById(R.id.activity_edit_clip_btn_image_layout).setOnClickListener(this);
		findViewById(R.id.activity_edit_rotate_btn_image_layout).setOnClickListener(this);
		findViewById(R.id.activity_edit_sharp_btn_image_layout).setOnClickListener(this);
		
		
		/**裁剪子菜单*/
		findViewById(R.id.activity_edit_btn_setsize).setOnClickListener(this);
		
		
		/**旋转子菜单*/
		findViewById(R.id.activity_edit_btn_img_rotate_left).setOnClickListener(this);
		findViewById(R.id.activity_edit_btn_img_rotate_right).setOnClickListener(this);
		findViewById(R.id.activity_edit_btn_img_rotate_vertical).setOnClickListener(this);
		findViewById(R.id.activity_edit_btn_img_rotate_horizontal).setOnClickListener(this);
		
		clipText = (TextView) findViewById(R.id.activity_edit_clip_btn_text);
		rotateText = (TextView) findViewById(R.id.activity_edit_rotate_btn_text);
		sharpText = (TextView) findViewById(R.id.activity_edit_sharp_btn_text);
		
		/**锐化子菜单*/
		
		
		
		/** btn_back, btn_ok */
		findViewById(R.id.activity_edit_btn_back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditActivity.this.finish();
			}
		});
		findViewById(R.id.activity_edit_btn_ok).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
				
				Drawable drawable = imageView.getmDrawable();
				bitmap = Bitmap2DrawableUtil.drawableToBitamp2(drawable);		
				bitmap.compress(CompressFormat.JPEG, 100, baos); 
				
				
				bitmapByte = baos.toByteArray();
				System.out.println("发送2时的length="+bitmapByte.length);
				
//				intent.putExtra("bitmapByte", bitmapByte);
				setResult(HomeActivity.EDIT_ACTIVITY_REQUEST_CODE, intent);
				
				
				// save to sharedpreferences
				String bitmapString = new String(Base64.encodeToString(bitmapByte, Base64.DEFAULT));
				BaseApplication.putString(Constant.BITMAP_TO_STRING, bitmapString);
				
				
				EditActivity.this.finish();
			}
		});
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Matrix matrix = new Matrix(); //接收图片之后放大 2.0倍
		matrix.postScale(2.0f, 2.0f);
		
		
		switch (v.getId()) {
		case R.id.activity_edit_clip_btn_image_layout:
			rotateLayout.setVisibility(View.GONE);
			sharpLayout.setVisibility(View.GONE);
			clipLayout.setVisibility(View.VISIBLE);
			
			clipText.setTextColor(Color.rgb(17, 129, 243));
			rotateText.setTextColor(Color.rgb(168, 168, 168));
			sharpText.setTextColor(Color.rgb(168, 168, 168));
			
			((ImageView)findViewById(R.id.activity_edit_clip_btn_image)).setImageResource(R.drawable.img_edit_tab_clip_b);
			((ImageView)findViewById(R.id.activity_edit_rotate_btn_image)).setImageResource(R.drawable.img_edit_tab_rotate_a);
			((ImageView)findViewById(R.id.activity_edit_sharp_btn_image)).setImageResource(R.drawable.img_edit_tab_sharp_a);
			break;
		case R.id.activity_edit_rotate_btn_image_layout:
			clipLayout.setVisibility(View.GONE);
			sharpLayout.setVisibility(View.GONE);
			rotateLayout.setVisibility(View.VISIBLE);
			
			clipText.setTextColor(Color.rgb(168, 168, 168));
			rotateText.setTextColor(Color.rgb(17, 129, 243));
			sharpText.setTextColor(Color.rgb(168, 168, 168));
			
			((ImageView)findViewById(R.id.activity_edit_rotate_btn_image)).setImageResource(R.drawable.img_edit_tab_rotate_b);
			((ImageView)findViewById(R.id.activity_edit_clip_btn_image)).setImageResource(R.drawable.img_edit_tab_clip_a);
			((ImageView)findViewById(R.id.activity_edit_sharp_btn_image)).setImageResource(R.drawable.img_edit_tab_sharp_a);
			break;
		case R.id.activity_edit_sharp_btn_image_layout:
			clipLayout.setVisibility(View.GONE);
			rotateLayout.setVisibility(View.GONE);
			sharpLayout.setVisibility(View.VISIBLE);
			
			clipText.setTextColor(Color.rgb(168, 168, 168));
			rotateText.setTextColor(Color.rgb(168, 168, 168));
			sharpText.setTextColor(Color.rgb(17, 129, 243));
			
			((ImageView)findViewById(R.id.activity_edit_sharp_btn_image)).setImageResource(R.drawable.img_edit_tab_sharp_b);
			((ImageView)findViewById(R.id.activity_edit_clip_btn_image)).setImageResource(R.drawable.img_edit_tab_clip_a);
			((ImageView)findViewById(R.id.activity_edit_rotate_btn_image)).setImageResource(R.drawable.img_edit_tab_rotate_a);
			
			bitmap = ImageHelper.HandleImagePixelsSharpen(bitmap);
//			imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
			break;
	
			/**clip*/
			/**调整比例按钮，点击后，出现比例选项ScrollView*/
		case R.id.activity_edit_btn_setsize:
			
			showPopupwindow(v);
			
			break;
			
	
			/**rotate*/
		case R.id.activity_edit_btn_img_rotate_left:
			bitmap = ImageHelper.adjustPhotoRotation(bitmap, -90);
			imageView.setDrawable(new BitmapDrawable(bitmap), bitmap.getWidth(), bitmap.getHeight());
//			imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
			break;
		case R.id.activity_edit_btn_img_rotate_right:
			bitmap = ImageHelper.adjustPhotoRotation(bitmap, 90);
			imageView.setDrawable(new BitmapDrawable(bitmap), bitmap.getWidth(), bitmap.getHeight());
//			imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
			break;
		case R.id.activity_edit_btn_img_rotate_vertical:
			bitmap = ImageHelper.convertImage(bitmap, "v");
			imageView.setDrawable(new BitmapDrawable(bitmap), bitmap.getWidth(), bitmap.getHeight());
//			imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
			break;
		case R.id.activity_edit_btn_img_rotate_horizontal:
			bitmap = ImageHelper.convertImage(bitmap, "h");
			imageView.setDrawable(new BitmapDrawable(bitmap), bitmap.getWidth(), bitmap.getHeight());
//			imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
			break;

			/**sharp*/
			
		default:
			break;
		}
	}

	
	
	MyPopupWinow myPopupWinow;
	/**
	 * 显示popupwindow
	 */
	private void showPopupwindow(View v){
		
//		InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
//		im.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		

		myPopupWinow = new MyPopupWinow(EditActivity.this, itemsOnClick);

		int[] location = new int[2];
		v.getLocationOnScreen(location);
		
		Log.e("TAG", "location[1]="+location[1]+"myPopupWinow.getHeight()="+myPopupWinow.getHeight());
		// 显示窗口
		myPopupWinow.showAtLocation(v ,Gravity.NO_GRAVITY, location[0], location[1]-myPopupWinow.getHeight()-200); // 设置layout在PopupWindow中显示的位置

		myPopupWinow .setOnDismissListener(new PopupWindow.OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub

					}
				});
		
		
		
	}
	
	// 为弹出窗口实现监听类
		private OnClickListener itemsOnClick = new OnClickListener() {

			public void onClick(View v) {
				myPopupWinow.dismiss();
			}

		};
	
	
	private class MyPopupWinow extends PopupWindow {

//		private Button btn_take_photo, btn_pick_photo, btn_cancel;
		private View mMenuView;

		public MyPopupWinow(Activity context, OnClickListener itemsOnClick) {
			super(context);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mMenuView = inflater.inflate(R.layout.mypopupwindow, null);
			
			
			
			
			String[] strArray = new String[]{"自由", "1:1", "2:3", "3:2", "3:4", "4:3", "16:9"};
			int[] intArray = new int[]{R.drawable.edit_cut_crop_freedom_b, R.drawable.edit_cut_crop_1_1_a, R.drawable.edit_cut_crop_2_3_a, 
											R.drawable.edit_cut_crop_3_2_a, R.drawable.edit_cut_crop_3_4_a, R.drawable.edit_cut_crop_4_3_a, 
											R.drawable.edit_cut_crop_16_9_a};
			final List<Map<String,Object>> lists = new ArrayList<Map<String,Object>>();
			for(int i=0; i<strArray.length; i++){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("icon", intArray[i]);
				map.put("name", strArray[i]);
				lists.add(map);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(mContext, lists , R.layout.activity_home_menu_layout, 
					new String[]{"name" ,"icon"}, 
					new int[]{R.id.activity_home_menu_title_name, R.id.activity_home_menu_title_icon});
			
			
			GridView menuTitle = new GridView(mContext);
			int width = Util.dip2px(EditActivity.this, 65);
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
						menuTitle = (TextView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_name);
						menuIcon = (ImageView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_icon);
						menuTitle.setTextColor(Color.argb(170, 255, 255, 255));
						menuIcon.setImageResource(menuIcons[i*2]);
					}
					
					menuTitleLayout = (LinearLayout) parent.getChildAt(position);
					menuTitle = (TextView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_name);
					menuIcon = (ImageView) menuTitleLayout.findViewById(R.id.activity_home_menu_title_icon);
					menuTitle.setTextColor(Color.rgb(17, 129, 243));
					menuIcon.setImageResource(menuIcons[position*2+1]);
					
					
					switch (lists.get(position).get("name").toString()) {
					case "自由":
						
						break;
					}
				}
			});
			
			
			LinearLayout categoryLayout = (LinearLayout) mMenuView.findViewById(R.id.activity_edit_clip_item_layout);
			categoryLayout.addView(menuTitle);
			
			
			
			
			
			
			
			// 设置按钮监听
//			btn_pick_photo.setOnClickListener(itemsOnClick);
//			btn_take_photo.setOnClickListener(itemsOnClick);
//			btn_cancel.setOnClickListener(itemsOnClick);
			// 设置SelectPicPopupWindow的View
			this.setContentView(mMenuView);
			// 设置SelectPicPopupWindow弹出窗体的宽
			this.setWidth(LayoutParams.FILL_PARENT);
			// 设置SelectPicPopupWindow弹出窗体的高
			this.setHeight(LayoutParams.WRAP_CONTENT);
			// 设置SelectPicPopupWindow弹出窗体可点击
			this.setFocusable(true);
			// 设置SelectPicPopupWindow弹出窗体动画效果
//			this.setAnimationStyle(R.style.popupAnimation);
			// 实例化一个ColorDrawable颜色为半透明
			ColorDrawable dw = new ColorDrawable(0xb0000000);
			// 设置SelectPicPopupWindow弹出窗体的背景
			this.setBackgroundDrawable(dw);
			// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
			mMenuView.setOnTouchListener(new OnTouchListener() { // 不知道为什么这里一直监听不到

				public boolean onTouch(View v, MotionEvent event) {

					int height = mMenuView.findViewById(R.id.pop_layout).getTop();
					int y = (int) event.getY();
					if (event.getAction() == MotionEvent.ACTION_UP) {
						if (y < height) {
							dismiss();

						}
					}
					return true;
				}
			});

			mMenuView.setOnKeyListener(new OnKeyListener() { // 不知道为什么这里一直监听不到

				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					// TODO Auto-generated method stub
					System.out.println("arg1=" + arg1 + ",KeyEvent.ACTION_DOWN="
							+ KeyEvent.ACTION_DOWN);
					if (arg2.getAction() == KeyEvent.ACTION_DOWN
							&& arg1 == KeyEvent.KEYCODE_BACK) {
						System.out.println("按下了Back键");
						return true;
					}
					return false;
				}
			});

		}

	}
	
}
