package com.meituxiuxiu.android.ui.opengl.surfaceview.renderer;

import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glReadPixels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.GLToolbox;

public class AutoMeihuaTextureRenderer implements GLSurfaceView.Renderer{

    private int mProgram;
    private int mTexSamplerHandle;
    private int mTexCoordHandle;
    private int mPosCoordHandle;

    private FloatBuffer mTexVertices;
    private FloatBuffer mPosVertices;

    private int mViewWidth;
    private int mViewHeight;

    private int mTexWidth;
    private int mTexHeight;
    
    private Context mContext;
    private final Queue<Runnable> mRunOnDraw;
    private int[] mTextures = new int[2];
    int mCurrentEffect;
    private EffectContext mEffectContext;
    private Effect mEffect;
    private int mImageWidth;
    private int mImageHeight;
    private boolean initialized = false;

    public static byte[] tempBitmapByte = null;
    
    private static final String VERTEX_SHADER =
        "attribute vec4 a_position;\n" +
        "attribute vec2 a_texcoord;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_Position = a_position;\n" +
        "  v_texcoord = a_texcoord;\n" +
        "}\n";

    private static final String FRAGMENT_SHADER =
        "precision mediump float;\n" +
        "uniform sampler2D tex_sampler;\n" +
        "varying vec2 v_texcoord;\n" +
        "void main() {\n" +
        "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
        "}\n";

    private static final float[] TEX_VERTICES = {
        0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f
    };

    private static final float[] POS_VERTICES = {
        -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f
    };

    private static final int FLOAT_SIZE_BYTES = 4;
    
    public AutoMeihuaTextureRenderer() {
        // TODO Auto-generated constructor stub
        mRunOnDraw = new LinkedList<>();

    }

    public void init() {
        // Create program
        mProgram = GLToolbox.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);

        // Bind attributes and uniforms
        mTexSamplerHandle = GLES20.glGetUniformLocation(mProgram,
                "tex_sampler");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texcoord");
        mPosCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_position");

        // Setup coordinate buffers
        mTexVertices = ByteBuffer.allocateDirect(
                TEX_VERTICES.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexVertices.put(TEX_VERTICES).position(0);
        mPosVertices = ByteBuffer.allocateDirect(
                POS_VERTICES.length * FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPosVertices.put(POS_VERTICES).position(0);
    }

    public void tearDown() {
        GLES20.glDeleteProgram(mProgram);
    }

    public void updateTextureSize(int texWidth, int texHeight) {
        mTexWidth = texWidth;
        mTexHeight = texHeight;
        computeOutputVertices();
    }

    public void updateViewSize(int viewWidth, int viewHeight) {
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
        computeOutputVertices();
    }

    public void renderTexture(int texId) {
        GLES20.glUseProgram(mProgram);
        GLToolbox.checkGlError("glUseProgram");

        GLES20.glViewport(0, 0, mViewWidth, mViewHeight);
        GLToolbox.checkGlError("glViewport");

        GLES20.glDisable(GLES20.GL_BLEND);

        GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mTexVertices);
        GLES20.glEnableVertexAttribArray(mTexCoordHandle);
        GLES20.glVertexAttribPointer(mPosCoordHandle, 2, GLES20.GL_FLOAT, false,
                0, mPosVertices);
        GLES20.glEnableVertexAttribArray(mPosCoordHandle);
        GLToolbox.checkGlError("vertex attribute setup");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLToolbox.checkGlError("glActiveTexture");
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);//把已经处理好的Texture传到GL上面
        GLToolbox.checkGlError("glBindTexture");
        GLES20.glUniform1i(mTexSamplerHandle, 0);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void computeOutputVertices() { //调整AspectRatio 保证landscape和portrait的时候显示比例相同，图片不会被拉伸
        if (mPosVertices != null) {
            float imgAspectRatio = mTexWidth / (float)mTexHeight;
            float viewAspectRatio = mViewWidth / (float)mViewHeight;
            float relativeAspectRatio = viewAspectRatio / imgAspectRatio;
            float x0, y0, x1, y1;
            if (relativeAspectRatio > 1.0f) {
                x0 = -1.0f / relativeAspectRatio;
                y0 = -1.0f;
                x1 = 1.0f / relativeAspectRatio;
                y1 = 1.0f;
            } else {
                x0 = -1.0f;
                y0 = -relativeAspectRatio;
                x1 = 1.0f;
                y1 = relativeAspectRatio;
            }
            float[] coords = new float[] { x0, y0, x1, y0, x0, y1, x1, y1 };
            mPosVertices.put(coords).position(0);
        }
    }
    
    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
        if (mEffect != null) {
            mEffect.release();
        }
        /**
         * Initialize the correct effect based on the selected menu/action item
         */
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
    
    public void setCurrentEffect(int effect) {
        mCurrentEffect = effect;
    }

    
    public void setImageBitmap(final Bitmap bmp){
        runOnDraw(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                loadTexture(bmp);
//                updateViewSize(bmp.getWidth(), bmp.getHeight());
            }
        });
    }
    
    private void loadTexture(Bitmap bmp){
        GLES20.glGenTextures(2, mTextures , 0);

        updateTextureSize(bmp.getWidth(), bmp.getHeight());
        
        mImageWidth = bmp.getWidth();
        mImageHeight = bmp.getHeight();

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        GLToolbox.initTexParams();
    }
    
    private void applyEffect() {
        if(mEffect == null){
            Log.i("info","apply Effect null mEffect");
        }
        System.out.println("mEffect="+mEffect);
        System.out.println("mTextures[0]="+mTextures[0] +"mImageWidth="+mImageWidth+"mImageHeight="+mImageHeight+"mTextures[1]="+mTextures[1]);
        mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
    }

    private void renderResult() {
        if (mCurrentEffect != R.id.none) {
            renderTexture(mTextures[1]);
        } else {
            renderTexture(mTextures[0]);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        if(!initialized){
            init();
            mEffectContext = EffectContext.createWithCurrentGlContext();
            initialized = true;
        }
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        synchronized (mRunOnDraw) {
            while (!mRunOnDraw.isEmpty()) {
                mRunOnDraw.poll().run();
            }
        }
        
        if (mCurrentEffect != R.id.none) {
            initEffect();
            applyEffect();
        }
        renderResult();
        
        Log.e("TAG", "--->"+tempBitmapByte);
        
        
        Log.e("TAG", "mImageWidth="+mImageWidth+"mImageHeight="+mImageHeight);
        Log.e("TAG", "mTexWidth="+mTexWidth+"mTexHeight="+mTexHeight);
        Log.e("TAG", "mViewWidth="+mViewWidth+"mViewHeight="+mViewHeight);
        
         // ??
//        updateViewSize(mImageWidth, mImageHeight);
     // 宽度和高度是根据保存后的图片大小来确定的
     tempBitmapByte = getOpenGLBitmapByte(mViewWidth, mViewHeight);
        
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        updateViewSize(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        
    }
    
    protected void runOnDraw(final Runnable runnable) {
        synchronized (mRunOnDraw) {
            mRunOnDraw.add(runnable);
        }
    }
    
    
    public static boolean printOptionEnable = false;
    /**获得bitmap的byte数据*/
	public byte[] getOpenGLBitmapByte(int width_surface, int height_surface){
		try {

		    if ( printOptionEnable ) 
		    {
//		    	updateViewSize(mImageWidth, mImageHeight);
//		    	width_surface = mImageWidth;
//		    	height_surface = mImageHeight;
		    	
		        printOptionEnable = false ;
		    Log.i("hari", "printOptionEnable if condition:"+printOptionEnable);
		    int w = width_surface ;
		    int h = height_surface  ;
		    
		    int wTemp = 0;
		    int hTemp = 0;
		    // 判断宽大，还是高大
		    if(mImageHeight * w > mImageWidth * h){
		    	wTemp = (int) ((float)mImageWidth/ (float)mImageHeight * h);
		    	Log.e("TAG", "wTemp:"+wTemp);
		    	int temp = wTemp;
		    	wTemp = (w-wTemp)/2;
		    	w = temp;
		    } else {//if(w > h)
		    	Log.e("TAG", "w:"+w);
		    	hTemp = (int) ((float)mImageHeight/(float) mImageWidth * w);
		    	Log.e("TAG", "hTemp:"+hTemp);
		    	int temp = hTemp;
		    	hTemp = (h-hTemp)/2;
		    	h = temp;
		    }
//		    if(wTemp < 0){
//		    	wTemp = 0;
//		    	w = width_surface;
//		    }
//		    if(hTemp < 0){
//		    	hTemp = 0;
//		    	h = height_surface;
//		    }
		    

		    int b[]=new int[(int) (w*h)];
		    int bt[]=new int[(int) (w*h)];
		    IntBuffer buffer=IntBuffer.wrap(b);
		    buffer.position(0);
		    glReadPixels(wTemp, hTemp, w, h,GL_RGBA,GL_UNSIGNED_BYTE, buffer);
		    for(int i=0; i<h; i++)
		    {
		     //remember, that OpenGL bitmap is incompatible with Android bitmap
		     //and so, some correction need.        
		         for(int j=0; j<w; j++)
		         {
		              int pix=b[i*w+j];
		              int pb=(pix>>16)&0xff;
		              int pr=(pix<<16)&0x00ff0000;
		              int pix1=(pix&0xff00ff00) | pr | pb;
		              bt[(h-i-1)*w+j]=pix1;
		         }
		      }           
		    Bitmap inBitmap = null ;
		    if (inBitmap == null || !inBitmap.isMutable()
		         || inBitmap.getWidth() != w || inBitmap.getHeight() != h) {
		           inBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		      }
		    inBitmap.copyPixelsFromBuffer(buffer);
		   inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);

		   inBitmap = resizeBitmap(inBitmap, mImageWidth, mImageHeight);
//		   inBitmap = Bitmap.createScaledBitmap(inBitmap, mImageWidth, mImageHeight, true);
		   
		   ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		   inBitmap.compress(CompressFormat.JPEG, 100, bos); 
		    byte[] bitmapdata = bos.toByteArray();
		    return bitmapdata;
		    }
		    }catch(Exception e) {
		        e.printStackTrace() ;
		    }
		return null;
	}
	
	
	// 重新设置bitmap的大小
	public static Bitmap resizeBitmap(Bitmap bmp, float newWidth, float newHeight){
		 // 获取这个图片的宽和高 
        float width = bmp.getWidth(); 
        float height = bmp.getHeight(); 
        // 创建操作图片用的matrix对象 
        Matrix matrix = new Matrix(); 
        // 计算宽高缩放率 
        float scaleWidth = ((float) newWidth) / width; 
        float scaleHeight = ((float) newHeight) / height; 
        Log.e("TAG", "scaleWidth="+scaleWidth);
        Log.e("TAG", "scaleHeight="+scaleHeight);
        // 缩放图片动作 
        matrix.postScale(scaleWidth, scaleHeight); 
        Bitmap newBmp = Bitmap.createBitmap(bmp, 0, 0, (int) width, (int) height, matrix, true); 
		return newBmp;
	}
    
}
