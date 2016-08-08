package com.meituxiuxiu.android.ui.opengl.surfaceview;

import java.io.IOException;
import java.util.List;

import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;

public class CameraInterface {

	private static final String TAG = "CameraInterface";
	
	private static CameraInterface mCameraInterface;
	
	private boolean isPreviewing = false;
	
	private Camera mCamera;  
	 
	private Parameters mParams;
	
	public static synchronized CameraInterface getInstance() {
		// TODO Auto-generated method stub
		if(mCameraInterface == null){  
            mCameraInterface = new CameraInterface();  
        }  
        return mCameraInterface;  
	}
	
	/**使用Surfaceview开启预览 
     * @param holder 
     * @param previewRate 
     */  
	public void doOpenCamera(SurfaceHolder holder){//  , float previewRate
        Log.i(TAG, "doStartPreview...");  
        if(isPreviewing){  
            mCamera.stopPreview();  
            return;  
        }  
        if(mCamera != null){  
            try {  
                mCamera.setPreviewDisplay(holder);  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
//            initCamera();//  previewRate
        }  
  
  
    }  
    /**使用TextureView预览Camera 
     * @param surface 
     * @param previewRate 
     */  
    public void doStartPreview(SurfaceTexture surface, float previewRate){  //
        Log.i(TAG, "doStartPreview...");  
        if(isPreviewing){  
            mCamera.stopPreview();  
            return;  
        }  
        if (mCamera == null) {
        	mCamera = Camera.open();
        	 Log.i(TAG, "Camera.open...");  
        }
        if(mCamera != null){  
            try {  
                mCamera.setPreviewTexture(surface);  
                Log.i(TAG, "setPreviewTexture...");  
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
            initCamera(previewRate);  //
        }  
          
    }

    
    private void initCamera(float previewRate){  //
        if(mCamera != null){  
  
            mParams = mCamera.getParameters();  
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式  
//          CamParaUtil.getInstance().printSupportPictureSize(mParams);  
//          CamParaUtil.getInstance().printSupportPreviewSize(mParams);  
            //设置PreviewSize和PictureSize  
//            Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(  
//                    mParams.getSupportedPictureSizes(),previewRate, 800);  
//            mParams.setPictureSize(pictureSize.width, pictureSize.height);  
//            Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(  
//                    mParams.getSupportedPreviewSizes(), previewRate, 800);  
//            mParams.setPreviewSize(previewSize.width, previewSize.height);  
  
            mCamera.setDisplayOrientation(90);  
  
//          CamParaUtil.getInstance().printSupportFocusMode(mParams);  
            List<String> focusModes = mParams.getSupportedFocusModes();  
            if(focusModes.contains("continuous-video")){  
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);  
            }  
            mCamera.setParameters(mParams);   
            mCamera.startPreview();//开启预览  
  
  
  
            isPreviewing = true;  
//            mPreviwRate = previewRate;  
  
            mParams = mCamera.getParameters(); //重新get一次  
            Log.i(TAG, "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width  
                    + "Height = " + mParams.getPreviewSize().height);  
            Log.i(TAG, "最终设置:PictureSize--With = " + mParams.getPictureSize().width  
                    + "Height = " + mParams.getPictureSize().height);  
        }  
    }

	public boolean isPreviewing() {
		// TODO Auto-generated method stub
		return isPreviewing;
	}
	
	public Camera getmCamera(){
		return mCamera;
	}

	public void doStopCamera() {
		// TODO Auto-generated method stub
		isPreviewing = false;
		mCamera.stopPreview();
//		mCamera.release();
//		mCamera = null;
	}
	
	
	private boolean cameraPosition = false;//true代表前置摄像头，false代表后置摄像头
	
	public boolean getCameraPosition(){
		return cameraPosition;
	}
	
	public void doSwitchCamera(SurfaceTexture surface, float previewRate){
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
                    doStartPreview(surface, previewRate);  //通过GLsurfaceview显示取景画面
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
                    doStartPreview(surface, previewRate);//通过GLsurfaceview显示取景画面
//                    mCamera.startPreview();//开始预览
                    cameraPosition = false;
                    break;
                }
            }
		}
	}
    
}
