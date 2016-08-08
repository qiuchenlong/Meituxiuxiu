package com.meituxiuxiu.android.ui;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.linj.FileOperateUtil;
import com.linj.album.view.FilterImageView;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraContainer.TakePictureListener;
import com.linj.camera.view.CameraView.FlashMode;
import com.meituxiuxiu.android.R;

/**
 * 万能相机(SurfaceView)
 * 
 * @author qiuchenlong on 2016.04.26
 *
 */
public class XiangJiActivity extends Activity implements OnClickListener, TakePictureListener{
	
	private String mSaveRoot;
	
	private ImageView switchCamera, cameraSetting;
	private ImageView btnClose;
	private LinearLayout btnTakePicture, btnPictureFilter;
	
	private FilterImageView mThumbView;
	
	private CameraContainer mContainer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_xiangji);
		
		// Header
		switchCamera = (ImageView) findViewById(R.id.camera_header_bar_btn_switch_camera);
		cameraSetting = (ImageView) findViewById(R.id.camera_header_bar_btn_camera_setting);
		// Bottom
		btnClose = (ImageView) findViewById(R.id.camera_bottom_bar_btn_close);
		mThumbView=(FilterImageView)findViewById(R.id.btn_thumbnail);
		btnTakePicture = (LinearLayout) findViewById(R.id.camera_bottom_bar_btn_take_picture);
		btnPictureFilter = (LinearLayout) findViewById(R.id.camera_bottom_bar_btn_filter);
		// Camera
		mContainer = (CameraContainer) findViewById(R.id.container);
		
		switchCamera.setOnClickListener(this);
		cameraSetting.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		mThumbView.setOnClickListener(this);
		btnTakePicture.setOnClickListener(this);
		btnPictureFilter.setOnClickListener(this);
		
		mSaveRoot="WannengCamera";
		mContainer.setRootPath(mSaveRoot);
		
		// 关闭闪光灯
		if(mContainer.getFlashMode()==FlashMode.ON){
			mContainer.setFlashMode(FlashMode.OFF);
		}
		initThumbnail();
	}

	/**
	 * 加载缩略图
	 */
	private void initThumbnail() {
		String thumbFolder=FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
		List<File> files=FileOperateUtil.listFiles(thumbFolder, ".jpg");
		if(files!=null&&files.size()>0){
			Bitmap thumbBitmap=BitmapFactory.decodeFile(files.get(0).getAbsolutePath());
			if(thumbBitmap!=null){
				mThumbView.setImageBitmap(thumbBitmap);
			}
		}else {
			mThumbView.setImageBitmap(null);
		}

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.camera_header_bar_btn_switch_camera:
			mContainer.switchCamera();
			break;
		case R.id.camera_header_bar_btn_camera_setting:
			mContainer.setWaterMark();
			break;
		case R.id.camera_bottom_bar_btn_close:
			finish();
			break;
		case R.id.camera_bottom_bar_btn_take_picture:
			btnTakePicture.setClickable(false);
			mContainer.takePicture(this);
			break;
		case R.id.camera_bottom_bar_btn_filter:
	
			break;
		}
	}

	@Override
	public void onTakePictureEnd(Bitmap bm) {
		// TODO Auto-generated method stub
		btnTakePicture.setClickable(true);	
	}

	@Override
	public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
		// TODO Auto-generated method stub
		if(bm!=null){
			//生成缩略图
			Bitmap thumbnail=ThumbnailUtils.extractThumbnail(bm, 213, 213);
			mThumbView.setImageBitmap(thumbnail);
		}
	}

}
