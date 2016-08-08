package com.meituxiuxiu.android.ui.opengl.surfaceview;

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
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import net.youmi.android.b.a.g.b.t;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.Bitmap.CompressFormat;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.GLToolbox;
import com.meituxiuxiu.android.ui.opengl.utils.DirectDrawer;
import com.muzhi.mtools.filter.GPUImage;

public class CameraGLSurfaceView extends GLSurfaceView implements Renderer, SurfaceTexture.OnFrameAvailableListener{
	
	 private static final String TAG = "yanzi";  
	    Context mContext;  
	    static SurfaceTexture mSurface;  
	    int mTextureID = -1;  
	    DirectDrawer mDirectDrawer;  
	    
	    /*private boolean initialized = false;*/
	    
	   //	Using the Media Effects Framework
	    private EffectContext effectContext;
	    private Effect effect;
	    
	    int[] textures;
	    
	    
	    public static byte[] tempByte;
	    
	    
	    public CameraGLSurfaceView(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        // TODO Auto-generated constructor stub  
	        mContext = context;  
	        setEGLContextClientVersion(2);  
	        setRenderer(this);  
	        setRenderMode(RENDERMODE_WHEN_DIRTY);  
	        
	        /*mRunOnDraw = new LinkedList<>();*/
	    }  
	    
	    public CameraGLSurfaceView(Context context){
	    	super(context);
			// TODO Auto-generated constructor stub
			
	    	setEGLContextClientVersion(2);  
	        setRenderer(this);  
	        setRenderMode(RENDERMODE_WHEN_DIRTY);  
	    }
	    
	    
	    @Override  
	    public void onSurfaceCreated(GL10 gl, EGLConfig config) {  
	        // TODO Auto-generated method stub  
	        Log.i(TAG, "onSurfaceCreated...");  
	        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);  
	        
//	        mTextureID = createTextureID();  
	        createTextureID();
	        mSurface = new SurfaceTexture(textures[0]);  
	        mSurface.setOnFrameAvailableListener(this);  
	        mDirectDrawer = new DirectDrawer(CameraInterface.getInstance().getCameraPosition(), textures[0]);
	        
//	        CameraInterface.getInstance().doOpenCamera(null);  
	    }  
	    
	    
	    
	    @Override  
	    public void onSurfaceChanged(GL10 gl, int width, int height) {  
	        // TODO Auto-generated method stub  
	        Log.i(TAG, "onSurfaceChanged...");  
	        GLES20.glViewport(0, 0, width, height);  
	  
	        if(!CameraInterface.getInstance().isPreviewing()){  
	            CameraInterface.getInstance().doStartPreview(mSurface, 1.33f);  
	        }
	    }  
	    @Override  
	    public void onDrawFrame(GL10 gl) {  
	        // TODO Auto-generated method stub  
	        Log.i(TAG, "onDrawFrame...");  
	        /*if(!initialized){
	            mEffectContext = EffectContext.createWithCurrentGlContext();
	            initialized = true;
	        }*/
	        
	        
	        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);  
	        
	        
	        if(isSwitch){
	        	mDirectDrawer = new DirectDrawer(CameraInterface.getInstance().getCameraPosition(), textures[0]);
	        	isSwitch = false;
	        }
	        
	        
//	        Log.e("TAG", "111...");
//	        if (mCurrentEffect != R.id.none) {
//	        	Log.e("TAG", "111...");
//	            initEffect();
//	            applyEffect();
//	        }
	        
//	        if(effectContext==null) {
//	            effectContext = EffectContext.createWithCurrentGlContext();
//	          }
//	        if(effect!=null){
//	            effect.release();
//	          }
//	          grayScaleEffect();
	          
	        
	        mSurface.updateTexImage();  
	        float[] mtx = new float[16];  
	        mSurface.getTransformMatrix(mtx);  
	        
	        mDirectDrawer.draw(mtx, mTextureID);//, textures[1]); 
	        
	        
	        
	        tempByte = getOpenGLBitmapByte(1080, 1920);
	        
	    }  
	    
	    
	    
	      
	    private void grayScaleEffect(){
	        EffectFactory factory = effectContext.getFactory();
	        effect = factory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
	        effect.apply(textures[0], 1080, 1920, textures[1]);
	    }
	    
	    @Override  
	    public void onPause() {  
	        // TODO Auto-generated method stub  
	        super.onPause();  
	        
	        CameraInterface.getInstance().doStopCamera();  
	    }  
	    
	    private int createTextureID()  
	    {  
	    	textures= new int[2];  
	  
	        GLES20.glGenTextures(2, textures, 0);  
	        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);  
	        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,  
	                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);          
	        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,  
	                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);  
	        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,  
	                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);  
	        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,  
	                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);  
	        
	        
	        int[] texture = new int[1];
	        
	        GLES20.glGenTextures(1, texture , 0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 0);
	        
	        mTextureID = texture[0];
	        
	        return textures[0];  
	    }  
	    
	    public SurfaceTexture _getSurfaceTexture(){  
	        return mSurface;  
	    }  
	    
	    @Override  
	    public void onFrameAvailable(SurfaceTexture surfaceTexture) {  
	        // TODO Auto-generated method stub  
	        Log.i(TAG, "onFrameAvailable...");  
	        this.requestRender();  
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
				
				ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
				   inBitmap.compress(CompressFormat.JPEG, 100, bos); 
				    byte[] bitmapdata = bos.toByteArray();

				 ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

			    final Calendar c=Calendar.getInstance();
			     long mytimestamp=c.getTimeInMillis();
			    String timeStamp=String.valueOf(mytimestamp);
			    String myfile="wanneng_"+timeStamp+".jpeg";

			    dir_image=new File(Environment.getExternalStorageDirectory()+File.separator);
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
			    }

			       Log.v("hari", "screenshots:"+dir_image.toString());
	
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static boolean isSwitch = false;
	public static void switchCamera(){
		isSwitch = true;
		CameraInterface.getInstance().doSwitchCamera(mSurface, 1.33f);  
	}
	    
	
/*	private final Queue<Runnable> mRunOnDraw;
	private int[] mTextures = new int[2];
	 static int mCurrentEffect;
	 private EffectContext mEffectContext;
	 private Effect mEffect;
	 private int mImageWidth = 1080;
	 private int mImageHeight = 1920;
	    
	 
	
	 private void initEffect() {
		 
		 
	        EffectFactory effectFactory = mEffectContext.getFactory();
	        if (mEffect != null) {
	            mEffect.release();
	        }
	        *//**
	         * Initialize the correct effect based on the selected menu/action item
	         *//*
	        switch (mCurrentEffect) {

	        case R.id.none:
	            break;

	        case R.id.autofix:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_AUTOFIX);
	            mEffect.setParameter("scale", 0.5f);
	            break;

	        case R.id.bw:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BLACKWHITE);
	            mEffect.setParameter("black", .1f);
	            mEffect.setParameter("white", .7f);
	            break;

	        case R.id.brightness:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_BRIGHTNESS);
	            mEffect.setParameter("brightness", 2.0f);
	            break;

	        case R.id.contrast:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CONTRAST);
	            mEffect.setParameter("contrast", 1.4f);
	            break;

	        case R.id.crossprocess:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
	            break;

	        case R.id.documentary:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DOCUMENTARY);
	            break;

	        case R.id.duotone:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_DUOTONE);
	            mEffect.setParameter("first_color", Color.YELLOW);
	            mEffect.setParameter("second_color", Color.DKGRAY);
	            break;

	        case R.id.filllight:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
	            mEffect.setParameter("strength", .8f);
	            break;

	        case R.id.fisheye:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FISHEYE);
	            mEffect.setParameter("scale", .5f);
	            break;

	        case R.id.flipvert:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
	            mEffect.setParameter("vertical", true);
	            break;

	        case R.id.fliphor:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FLIP);
	            mEffect.setParameter("horizontal", true);
	            break;

	        case R.id.grain:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAIN);
	            mEffect.setParameter("strength", 1.0f);
	            break;

	        case R.id.grayscale:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_GRAYSCALE);
	            break;

	        case R.id.lomoish:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_LOMOISH);
	            break;

	        case R.id.negative:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_NEGATIVE);
	            break;

	        case R.id.posterize:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_POSTERIZE);
	            break;

	        case R.id.rotate:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_ROTATE);
	            mEffect.setParameter("angle", 180);
	            break;

	        case R.id.saturate:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
	            mEffect.setParameter("scale", .5f);
	            break;

	        case R.id.sepia:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SEPIA);
	            break;

	        case R.id.sharpen:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SHARPEN);
	            break;

	        case R.id.temperature:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
	            mEffect.setParameter("scale", .9f);
	            break;

	        case R.id.tint:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TINT);
	            mEffect.setParameter("tint", Color.MAGENTA);
	            break;

	        case R.id.vignette:
	            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE);
	            mEffect.setParameter("scale", .5f);
	            break;

	        default:
	            break;

	        }
	    }
	 
	public static void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }
	
	private void applyEffect() {
        if(mEffect == null){
            Log.i("info","apply Effect null mEffect");
        }
        System.out.println("mEffect="+mEffect);
        System.out.println("mTextures[0]="+mTextures[0] +"mImageWidth="+mImageWidth+"mImageHeight="+mImageHeight+"mTextures[1]="+mTextures[1]);
        if(mEffect != null){
        	mEffect.apply(mTextureID, mImageWidth, mImageHeight, mTextures[1]);
        }
    }*/
	
	
	public int onDrawToTexture() {
		
		createTextureID();
        mSurface = new SurfaceTexture(textures[0]);  
        mSurface.setOnFrameAvailableListener(this);  
        mDirectDrawer = new DirectDrawer(CameraInterface.getInstance().getCameraPosition(), textures[0]);
		
		 if(mSurface!=null){
        float[] mtx = new float[16];  
        mSurface.getTransformMatrix(mtx);  
        mDirectDrawer.draw(mtx, mTextureID);
		 }
		return textures[0];
	}
	
}
