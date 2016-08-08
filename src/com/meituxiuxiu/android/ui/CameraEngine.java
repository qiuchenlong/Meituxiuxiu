package com.meituxiuxiu.android.ui;

import java.io.IOException;
import java.util.List;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;

public class CameraEngine {
	private static Camera mCamera = null;
	private static int mCameraID = 0;	
	
	public static Camera getCamera(){
		return mCamera;
	}
	
	public static boolean openCamera(){
		if(mCamera == null){
			try{
				mCamera = Camera.open(mCameraID);
				setDefaultParameters();
				return true;
			}catch(RuntimeException e){
				return false;
			}
		}
		return false;
	}
	
	public static void releaseCamera(){
		if(mCamera != null){
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}
	
	public void resumeCamera(){
		openCamera();
	}
	
	public void setParameters(Parameters parameters){
		mCamera.setParameters(parameters);
	}
	
	public Parameters getParameters(){
		if(mCamera != null)
			mCamera.getParameters();
		return null;
	}
	
	private static void setDefaultParameters(){
		Parameters parameters = mCamera.getParameters();
		if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
		Size previewSize = getLargePreviewSize();
		parameters.setPreviewSize(1280, 720);
		Size pictureSize = getLargePictureSize();
		parameters.setPictureSize(pictureSize.width, pictureSize.height);
		mCamera.setParameters(parameters);
	}
	
	private static Size getLargePreviewSize(){
		if(mCamera != null){
			List<Size> sizes = mCamera.getParameters().getSupportedPreviewSizes();
			Size temp = sizes.get(0);
				for(int i = 1;i < sizes.size();i ++){
					if(temp.width < sizes.get(i).width)
						temp = sizes.get(i);
			}
			return temp;
		}
		return null;
	}
	
	private static Size getLargePictureSize(){
		if(mCamera != null){
			List<Size> sizes = mCamera.getParameters().getSupportedPictureSizes();
			Size temp = sizes.get(0);
				for(int i = 1;i < sizes.size();i ++){
					float scale = (float)(sizes.get(i).height) / sizes.get(i).width;
					if(temp.width < sizes.get(i).width && scale < 0.6f && scale > 0.5f)
						temp = sizes.get(i);
			}
			return temp;
		}
		return null;
	}
	
	public static Size getPreviewSize(){
		return mCamera.getParameters().getPreviewSize();	
	}
	
	public static void startPreview(SurfaceTexture surfaceTexture){
		try {
			mCamera.setPreviewTexture(surfaceTexture);
			mCamera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public static void startPreview(){
		if(mCamera != null)
			mCamera.startPreview();
	}
	
	public static void stopPreview(){
		mCamera.stopPreview();
	}
	
	public static CameraInfo getCameraInfo(){
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(mCameraID, cameraInfo);
		return cameraInfo;
	}
	
	public static int getOrientation(){
		CameraInfo cameraInfo = new CameraInfo();
		Camera.getCameraInfo(mCameraID, cameraInfo);
		return cameraInfo.orientation;
	}
	
	public static boolean isFlipHorizontal(){
		return CameraEngine.getCameraInfo().facing == CameraInfo.CAMERA_FACING_FRONT ? true : false;
	}
	
	public static void setRotation(int rotation){
		Camera.Parameters params = mCamera.getParameters();
        params.setRotation(rotation);
        mCamera.setParameters(params);
	}
	
	public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback, 
			Camera.PictureCallback jpegCallback){
		mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
	}
	
	private static boolean cameraPosition = false;
	public static boolean getCameraPosition(){
		return cameraPosition;
	}
	
	private static boolean isPreviewing = false;
	
	public static void doSwitchCamera(SurfaceTexture surface){
		int cameraCount = 0;
		CameraInfo cameraInfo = new CameraInfo();
		cameraCount = mCamera.getNumberOfCameras(); //得到摄像头的个数
		
		for(int i=0; i<cameraCount; i++){
			Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if(cameraPosition == false) {
                //现在是后置，变更为前置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置  
                	mCamera.stopPreview();//停掉原来摄像头的预览
                	mCamera.release();//释放资源
                	mCamera = null;//取消原来摄像头
                	isPreviewing = false;
                	mCamera = Camera.open(i);//打开当前选中的摄像头
                	startPreview(surface);  //通过GLsurfaceview显示取景画面
//                    mCamera.startPreview();//开始预览
                    cameraPosition = true;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置  
                	mCamera.stopPreview();//停掉原来摄像头的预览
                	mCamera.release();//释放资源
                	mCamera = null;//取消原来摄像头
                	isPreviewing = false;
                	mCamera = Camera.open(i);//打开当前选中的摄像头
                	startPreview(surface);//通过GLsurfaceview显示取景画面
//                    mCamera.startPreview();//开始预览
                    cameraPosition = false;
                    break;
                }
            }
		}
	}
	
	
	
	public static void doOnFlash(){
		Parameters p = mCamera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(p);
	}
	
	public static void doOffFlash(){
		Parameters p = mCamera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_OFF);
		mCamera.setParameters(p);
	}
	
}
