package com.meituxiuxiu.android.ui;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glReadPixels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.meituxiuxiu.android.utils.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.Log;



/**
 * MagicCameraDisplay is used for camera preview
 */
public class MagicCameraDisplay extends MagicDisplay{	
	/**
	 * 用于绘制相机预览数据，当无滤镜及mFilters为Null或者大小为0时，绘制到屏幕中，
	 * 否则，绘制到FrameBuffer中纹理
	 */
	private final MagicCameraInputFilter mCameraInputFilter;
	
	/**
	 * Camera预览数据接收层，必须和OpenGL绑定
	 * 过程见{@link OpenGLUtils.getExternalOESTextureID()};
	 */
	private static SurfaceTexture mSurfaceTexture;
    
	public MagicCameraDisplay(Context context, GLSurfaceView glSurfaceView){
		super(context, glSurfaceView);
		mCameraInputFilter = new MagicCameraInputFilter();
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0,0,0,0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);
        MagicFilterParam.initMagicFilterParam(gl);
        mCameraInputFilter.init();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		mSurfaceWidth = width;
		mSurfaceHeight = height;
		onFilterChanged();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);	
		mSurfaceTexture.updateTexImage();
		float[] mtx = new float[16];
		mSurfaceTexture.getTransformMatrix(mtx);
		mCameraInputFilter.setTextureTransformMatrix(mtx);
//		Log.e("TAG", "onDrawFrame...");
		if(mFilters == null){
			mCameraInputFilter.onDrawFrame(mTextureId, mGLCubeBuffer, mGLTextureBuffer);
		}else{
			int textureID = mCameraInputFilter.onDrawToTexture(mTextureId);	
			mFilters.onDrawFrame(textureID, mGLCubeBuffer, mGLTextureBuffer);
		}
		
		
		getOpenGLBitmapByte(1080, 1920);
	}
	
	
	public static boolean printOptionEnable = false;
    
    public File dir_image;
    
public byte[] getOpenGLBitmapByte(int width_surface, int height_surface) {
	try {
		if (printOptionEnable) {
			printOptionEnable = false;
			Log.i("hari", "printOptionEnable if condition:"
					+ printOptionEnable);
			int w = width_surface;
			int h = height_surface;

			Log.i("hari", "w:" + w + "-----h:" + h);

			int b[] = new int[(int) (w * h)];
			int bt[] = new int[(int) (w * h)];
			IntBuffer buffer = IntBuffer.wrap(b);
			buffer.position(0);
			glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			for (int i = 0; i < h; i++) {
				// remember, that OpenGL bitmap is incompatible with Android
				// bitmap
				// and so, some correction need.
				for (int j = 0; j < w; j++) {
					int pix = b[i * w + j];
					int pb = (pix >> 16) & 0xff;
					int pr = (pix << 16) & 0x00ff0000;
					int pix1 = (pix & 0xff00ff00) | pr | pb;
					bt[(h - i - 1) * w + j] = pix1;
				}
			}
			Bitmap inBitmap = null;
			if (inBitmap == null || !inBitmap.isMutable()
					|| inBitmap.getWidth() != w
					|| inBitmap.getHeight() != h) {
				inBitmap = Bitmap.createBitmap(w, h,
						Bitmap.Config.ARGB_8888);
			}
			inBitmap.copyPixelsFromBuffer(buffer);
			inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
			
			try {
				Util.saveFile(inBitmap, "wanneng_"+System.currentTimeMillis()+".jpg");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
			   inBitmap.compress(CompressFormat.JPEG, 100, bos); 
			    byte[] bitmapdata = bos.toByteArray();

			 ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

		    final Calendar c=Calendar.getInstance();
		     long mytimestamp=c.getTimeInMillis();
		    String timeStamp=String.valueOf(mytimestamp);
		    String myfile="wanneng_"+timeStamp+".jpeg";

		    dir_image=new File(Environment.getExternalStorageDirectory()+File.separator+"meituxiuxiu"+File.separator);
		    dir_image.mkdirs();

		    try {
		        File tmpFile = new File(dir_image,myfile); 
		        FileOutputStream fos = new FileOutputStream(tmpFile);

		         byte[] buf = new byte[1024];
		            int len;
		            while ((len = fis.read(buf)) > 0) {
		                fos.write(buf, 0, len);
		            }
		                fis.close();
		                fos.close();
		    } catch (FileNotFoundException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }*/

		       Log.v("hari", "screenshots:"+dir_image.toString());

			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}
	
	
	private OnFrameAvailableListener mOnFrameAvailableListener = new OnFrameAvailableListener() {
		
		@Override
		public void onFrameAvailable(SurfaceTexture surfaceTexture) {
			// TODO Auto-generated method stub
			mGLSurfaceView.requestRender();
		}
	};
	
	private void setUpCamera(){
		mGLSurfaceView.queueEvent(new Runnable() {
       		
            @Override
            public void run() {
            	if(mTextureId == OpenGLUtils.NO_TEXTURE){
        			mTextureId = OpenGLUtils.getExternalOESTextureID();	
        			mSurfaceTexture = new SurfaceTexture(mTextureId);
    				mSurfaceTexture.setOnFrameAvailableListener(mOnFrameAvailableListener);   
            	}
            	Size size = CameraEngine.getPreviewSize();
    			int orientation = CameraEngine.getOrientation();
    			if(orientation == 90 || orientation == 270){
    				mImageWidth = size.height;
    				mImageHeight = size.width;
    			}else{
    				mImageWidth = size.width;
    				mImageHeight = size.height;
    			} 
    			mCameraInputFilter.onOutputSizeChanged(mImageWidth, mImageHeight);
//            	CameraEngine.startPreview(mSurfaceTexture);
    			CameraEngine.doSwitchCamera(mSurfaceTexture);
            }
        });
    }
	
	protected void onFilterChanged(){
		super.onFilterChanged();
		mCameraInputFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
		if(mFilters != null)
			mCameraInputFilter.initCameraFrameBuffer(mImageWidth, mImageHeight);
		else
			mCameraInputFilter.destroyFramebuffers();
	}
	
	public void onResume(){
		super.onResume();
		if(CameraEngine.getCamera() == null)
        	CameraEngine.openCamera();
		if(CameraEngine.getCamera() != null){
			boolean flipHorizontal = CameraEngine.isFlipHorizontal();
			adjustPosition(CameraEngine.getOrientation(),flipHorizontal,!flipHorizontal);
		}
		setUpCamera();
	}
	
	public void onPause(){
		super.onPause();
		CameraEngine.releaseCamera();
	}

	public void onDestroy(){
		super.onDestroy();
	}

//	public void onTakePicture(File file, onPictureSaveListener listener,ShutterCallback shutterCallback){
//		CameraEngine.setRotation(90);
//		mSaveTask = new SaveTask(mContext, file, listener);
//		CameraEngine.takePicture(shutterCallback, null, mPictureCallback);
//	}
	
//	private PictureCallback mPictureCallback = new PictureCallback() {
//		
//		@Override
//		public void onPictureTaken(final byte[] data,Camera camera) {
//			Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//			if(mFilters != null){
//				getBitmapFromGL(bitmap, true);
//			}else{
//				mSaveTask.execute(bitmap);   
//			}
//		}
//	};
	
//	protected void onGetBitmapFromGL(Bitmap bitmap){
//		mSaveTask.execute(bitmap);
//	}
	
	private void adjustPosition(int orientation, boolean flipHorizontal,boolean flipVertical) {
        Rotation mRotation = Rotation.fromInt(orientation);
        float[] textureCords = TextureRotationUtil.getRotation(mRotation, flipHorizontal, flipVertical);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);
    }			
	
	
	public static void switchCamera(){
		CameraEngine.doSwitchCamera(mSurfaceTexture);
	}
	
	public static void setFlashOn(){
		CameraEngine.doOnFlash();
	}
	
	public static void setFlashOff(){
		CameraEngine.doOffFlash();
	}
}
