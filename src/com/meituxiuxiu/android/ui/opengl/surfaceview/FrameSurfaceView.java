package com.meituxiuxiu.android.ui.opengl.surfaceview;

/**静态引入package*/

/** OpenGL ES 2.0 API */
import static android.opengl.GLES20.*;
/** OpenGL ES Utils API */
import static android.opengl.GLUtils.*;
/** OpenGL ES Matrix API */
import static android.opengl.Matrix.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.FrameActivity;
import com.meituxiuxiu.android.ui.opengl.object.Square;
import com.meituxiuxiu.android.ui.opengl.object.SquareByTexture;
import com.meituxiuxiu.android.ui.opengl.utils.FontUtil;
import com.meituxiuxiu.android.ui.opengl.utils.Geometry;
import com.meituxiuxiu.android.ui.opengl.utils.Geometry.Point;
import com.meituxiuxiu.android.ui.opengl.utils.Geometry.Ray;
import com.meituxiuxiu.android.ui.opengl.utils.Geometry.Sphere;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class FrameSurfaceView extends GLSurfaceView {

	/**渲染器对象*/
	private static SceneRenderer mRenderer;
	
	/**移动、旋转、缩放比例*/
	private final float TOUCH_SCALE_FACTOR = 0.01f;//180.0f / 320;
	/*private float mPreviousX;
	private float mPreviousY;*/
	
	/**纹理Id*/
	int textureId;
	int textureId2;
	
	private Bitmap bitmap;
	
	
	public static byte[] tempBitmapByte = null;
	
	
	public static boolean isSelected = false;
	public static int selectIndex = 1;
	
	
	public FrameSurfaceView(Context context, Bitmap bitmap) {
		super(context);
		// TODO Auto-generated constructor stub
		this.bitmap = bitmap;
		
		this.setEGLContextClientVersion(2);
		mRenderer = new SceneRenderer();
		this.setRenderer(mRenderer);
		/**强制刷新，不停渲染*/
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		
		
		/*this.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event != null){
					final float normalizedX = (event.getX() / (float) v.getWidth()) * 2 - 1;
					final float normalizedY = (event.getY() / (float) v.getHeight()) * 2 - 1;
					
					if(event.getAction() == MotionEvent.ACTION_DOWN){
						FrameSurfaceView.this.queueEvent(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mRenderer.handleTouchPress(normalizedX, normalizedY);
							}
						});
					}else if(event.getAction() == MotionEvent.ACTION_MOVE){
						FrameSurfaceView.this.queueEvent(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mRenderer.handleTouchDrag(normalizedX, normalizedY);
							}
						});
					}
					return true;
				}
				return false;
			}
		});*/
	}
	
	
	
	private long lastDownTime, lastUpTime, lastUpUpTime;
	private float[] lastXY ={0,0, 0,0};
	private float lastX, lastY;
	
	// 判断是两只手指在做旋转操作
	boolean rotateFlag = false;
	
	public float zoom = 1;
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
/*		float y = event.getY();
		float x = event.getX();

	switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dy = y - mPreviousY;
			float dx = x - mPreviousX;

			mRenderer.square.xAngle += dy * TOUCH_SCALE_FACTOR;
			mRenderer.square.yAngle += dx * TOUCH_SCALE_FACTOR;
			
//			mRenderer.squarebytexture.xAngle += dx * TOUCH_SCALE_FACTOR;
//			mRenderer.squarebytexture.yAngle -= dy * TOUCH_SCALE_FACTOR;
			
//			mRenderer.triangle.zAngle = ;
			break;
		}*/
		
		final float normalizedX = (event.getX() / (float) this.getWidth()) * 2 - 1;
		final float normalizedY = (event.getY() / (float) this.getHeight()) * 2 - 1;
		
		if(event.getPointerCount() == 1){
			Log.e("FrameSurfaceView", "触摸点的数量为1...");
			// Action_Up
			if (event.getAction() == MotionEvent.ACTION_UP){ 
				
//				FontUtil.content = new String[]{"qiuchenlong"};
//				Toast.makeText(getContext(), "换字...", Toast.LENGTH_SHORT).show();
				
				
				// 相当于三次点击效果
				if ((System.currentTimeMillis() - lastUpUpTime) < 500){ 
					Log.e("FrameSurfaceView", "Third Click...");
					
				} 
				// 相当于双击效果，恢复变换前的样子
				else if ((System.currentTimeMillis() - lastUpTime) < 500){
					Log.e("FrameSurfaceView", "Second Click...");
	  				lastUpUpTime = System.currentTimeMillis();
	  				/**恢复到原先位置点*/
//	  				mRenderer.squarebytexture.xAngle = 0;
//	  				mRenderer.squarebytexture.yAngle = 0;
	  			} 
				 // 如果点击的时间小于0.5s，不做任何操作
				else	if ((System.currentTimeMillis() - lastDownTime) < 500){
					Log.e("FrameSurfaceView", "Second Click and Time out...");
	  				lastUpTime = System.currentTimeMillis();

				}
			}
			// Action_Down
			else if(event.getAction() == MotionEvent.ACTION_DOWN){
				lastDownTime = System.currentTimeMillis(); // 获取按下的时间
				
				mRenderer.handleTouchPress(normalizedX, normalizedY);
			}
			// Rotate with one finger
	  		else if ((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE){ // 手指在移动
	  			float dx = (event.getX() - lastX) * 180.0f / 320; // x轴移动的距离
	  			float dy = (event.getY() - lastY) * 180.0f / 320; // y轴移动的距离
//	  			mRenderer.squarebytexture.rotateXY(dx, dy); // dx, dy 对图形进行旋转操作
	  			if(rotateFlag != true){
		  			mRenderer.squarebytexture.xAngle += dx * TOUCH_SCALE_FACTOR;
					mRenderer.squarebytexture.yAngle -= dy * TOUCH_SCALE_FACTOR;
					
					mRenderer.handleTouchDrag(normalizedX, normalizedY);
	  			}
	  			rotateFlag = false;
	  		}
			lastX = event.getX();
			lastY = event.getY();
		}
		// 这里是两只手手指触摸屏幕时触发的事件
		else if(event.getPointerCount() == 2){
			Log.e("FrameSurfaceView", "触摸点的数量为2...");
			// First second pointer down
	  		if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN) {
	  			lastDownTime = System.currentTimeMillis();
	  		}else if (event.getAction() == MotionEvent.ACTION_POINTER_2_UP
	  				&& (System.currentTimeMillis() - lastDownTime) < 500){
//	  			mRenderer.squarebytexture.rotateRestore();
	  		}
	  		// rotateZoom
	  		else{
	  		// Delta distance
	  			float vx0 = lastXY[2] - lastXY[0];
	  			float vy0 = lastXY[3] - lastXY[1];
	  			float vx1 = event.getX(1) - event.getX(0);
	  			float vy1 = event.getY(1) - event.getY(0);
	  			float lastd = (float) Math.sqrt(vx0*vx0+vy0*vy0);
	  			float d = (float) Math.sqrt(vx1*vx1+vy1*vy1); // 两个手指触摸的两点的距离
	  			float dd = (d - lastd)*2; // arbitraire
	  			// Delta Center 两个手指触摸的两点的中心坐标
	  			float dx = ((event.getX(1) + event.getX(0))-(lastXY[2] + lastXY[0])) /2;
	  			float dy = ((event.getY(1) + event.getY(0))-(lastXY[3] + lastXY[1])) /2;
	  			// Delta angle 转的角度
	  			float cz = vx1*vy0-vy1*vx0; // Cross product = v0 v1 sin
	  			float sp = vx0*vx1+vy0*vy1; // Scalar product = v0 v1 cos
	  			float v0v1 = (float) (Math.sqrt(vx0*vx0+vy0*vy0) * Math.sqrt(vx1*vx1+vy1*vy1));
	  			float sin = cz / v0v1;
	  			float cos = sp / v0v1;
	  			if (cos > 1.0f )
	  				cos = 1.0f;
	  			if (cos < -1.0f )
	  				cos = -1.0f;
	  			float angle = (float)(Math.acos(cos) * 180/Math.PI);
	  			if (sin < 0)  
	  				angle = -angle;
	  			
	  			
	  			Log.e("FrameSurfaceView", "angle="+angle+",dx="+dx+",dy="+dy+",dd="+dd);
	  			
//	  			mRenderer.squarebytexture.angle = angle*10;
//	  			mRenderer.squarebytexture.dx = dx;
//	  			mRenderer.squarebytexture.dy = dy;
//	  			mRenderer.squarebytexture.dd = 1;
	  			mRenderer.squarebytexture.rotateZoom(angle, dx, dy, dd * TOUCH_SCALE_FACTOR / 10);
	  			
	  			rotateFlag = true;
	  		}
	  		lastXY[0]=event.getX(0); 
	  		lastXY[1]=event.getY(0);
	  		lastXY[2]=event.getX(1);
	  		lastXY[3]=event.getY(1);
		}
		

		/*mPreviousY = y;
		mPreviousX = x;*/
		return true;
	}
	
	
	public static Bitmap getSurfaceViewBitmap(int width, int height){
		return mRenderer.squarebytexture.getOpenGLBitmap(0, 0, width, height);
	}
	
	
	/**场景渲染器Renderer*/
	private class SceneRenderer implements Renderer{

		SquareByTexture squarebytexture;
		
		SquareByTexture squarebytexture2;
		
		SquareByTexture textRect;
		
		Square square;
		
		
		/**初始化场景*/
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// TODO Auto-generated method stub
			glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			
			Log.e("FrameSurfaceView", "纹理图 宽："+width+", 高："+height);
			
//			squarebytexture = new SquareByTexture(FrameSurfaceView.this, (int)(width*0.8f), (int)(height*0.8f));
			squarebytexture = new SquareByTexture(FrameSurfaceView.this, (int)(800), (int)(height*1.0f/width*800.0f));
			// 背景图片固定宽、高值
			squarebytexture2 = new SquareByTexture(FrameSurfaceView.this, (int)(1000*1.09f), (int)(1500*1.09f));
			
			textRect = new SquareByTexture(FrameSurfaceView.this, (int)(800), 512);
			
			square = new Square(FrameSurfaceView.this, width, height);
			
			glEnable(GL_DEPTH_TEST);
			initTexture(bitmap);
			try {
				initTexture2();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			glDisable(GL_CULL_FACE); // 关闭背面裁剪
			
			
			blueMalletPosition = new Point(0, 0, -1.0f);
		}

		
		float ratio;
		
		/**场景发生变化（横竖屏的切换）*/
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			glViewport(0, 0, width, height);
			
			// 计算屏幕宽高比
			ratio = (float) width / height;
						
			
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 50);
			MatrixState.setCamera(0, 0, 3f, 0, 0, 0f, 0f, 1.0f, 0.0f);
		}


//		TextRect
		int wlWidth = (int)(800);
		int wlHeight = 512;
		long timeStamp = System.currentTimeMillis();
		int texIdByWord = -1;
		
		/**每一帧的绘制操作*/
		@Override
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			
			invertM(invertedViewProjectionMatrix, 0, MatrixState.getProjMatrix(), 0);

			
			long tts = System.currentTimeMillis();
			if(tts - timeStamp >500){
				timeStamp = tts;
				FontUtil.cIndex = (FontUtil.cIndex + 1)%FontUtil.content.length;
//				FontUtil.updateRGB();
			}
			if (texIdByWord != -1) {
				glDeleteTextures(1, new int[] { texIdByWord }, 0);
			}
			/** 绘制文本纹理图 */
//			Bitmap bm = FontUtil.generateWLT(getContext() ,FontUtil.getContent(FontUtil.cIndex, FontUtil.content), wlWidth, wlHeight);
			Bitmap bm = FontUtil.generateWLT(getContext() ,FontUtil.getContent(FontUtil.cIndex, FontUtil.content), FrameActivity.wordsWidth, FrameActivity.wordsHeight);
//			Log.e("TAG", "cIndex="+FontUtil.cIndex);
			texIdByWord = initTextureToId(bm);
			
			Log.e("TAG", "文字纹理的bitmap ---> width = "+bm.getWidth());
			Log.e("TAG", "文字纹理的bitmap ---> height = "+bm.getHeight());

////			textRect.xAngle = 0.75f;
//			textRect.yAngle = -3.25f;
			textRect.xAngle = FrameActivity.wordsStrideX;
			textRect.yAngle = FrameActivity.wordsStrideY;
			textRect.drawSelf(texIdByWord);
			
			/**Frame background*/
			squarebytexture2.drawSelf(textureId2);
			/**Frame image*/
			squarebytexture.drawSelf(textureId);
			
			
//			square.drawSelf();
			
			
			// 宽度和高度是根据保存后的图片大小来确定的
			tempBitmapByte = getOpenGLBitmapByte((int)(1000*0.9f), (int)(1500*0.88f));
			
			
			if(isSelected){ // 如果选择被的边框，将重新加载纹理图片
				try {
					squarebytexture.setZoom(zoom);
					
					initTexture2();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				isSelected = !isSelected;
			}
			
		}
		
		
		/**初始化纹理数据*/
		public void initTexture(Bitmap bitmap){
			int[] textures = new int[1];
			glGenTextures(1, textures, 0);
			textureId = textures[0];
			glBindTexture(GL_TEXTURE_2D, textureId);
			// 设置纹理参数
			// 设置MIN采样方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			// 设置MAG采样方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			// 设置S轴拉伸方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			// 设置T轴拉伸方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			
			/*// 加载图片
			InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
			
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			// 加载纹理进显存
			texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
			
//			bitmap.recycle();
			
		}
		
		
		
		
		
		/**初始化纹理数据  frame
		 * @throws IOException */
		public void initTexture2() throws IOException{
			int[] textures = new int[1];
			glGenTextures(1, textures, 0);
			textureId2 = textures[0];
			glBindTexture(GL_TEXTURE_2D, textureId2);
			// 设置纹理参数
			// 设置MIN采样方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			// 设置MAG采样方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			// 设置S轴拉伸方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			// 设置T轴拉伸方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			
			// 加载图片
//			InputStream is = getResources().openRawResource(R.drawable.frame001);

			InputStream is = getResources().getAssets().open("frame/frame00"+selectIndex+".png");
			
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// 加载纹理进显存
			texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
			
			bitmap.recycle();
			
		}
		
		
		
		/**初始化纹理数据*/
		public int initTextureToId(Bitmap bitmap){
			int[] textures = new int[1];
			glGenTextures(1, textures, 0);
			int textureIds = textures[0];
			glBindTexture(GL_TEXTURE_2D, textureIds);
			// 设置纹理参数
			// 设置MIN采样方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			// 设置MAG采样方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			// 设置S轴拉伸方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			// 设置T轴拉伸方式
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			
			/*// 加载图片
			InputStream is = getResources().openRawResource(R.drawable.ic_launcher);
			
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			// 加载纹理进显存
			texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
			
//			bitmap.recycle();
			
			return textureIds;
		}
		
		
//		private boolean ImagePressed = false; // 图片是否被按下
//		private Point ImagePosition; // 图片的位置
		
		private boolean malletPressed = false;
		private Point blueMalletPosition;
		
		private boolean wordPressed = false;
		
		/**
		 * 处理手指触摸事件
		 * 
		 * @param normalizedX
		 * @param normalizedY
		 */
		public void handleTouchPress(float normalizedX, float normalizedY){
			Log.w("TAG", "Press..."+"normalizedX="+normalizedX+",normalizedY="+normalizedY);
			Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
			// Now test if this ray intersects with the mallet by creating a
			// bounding sphere that wraps the mallet.
			Sphere malletBoundingSphere = new Sphere(new Point(
					blueMalletPosition.x,
					blueMalletPosition.y,
					blueMalletPosition.z),
					0.1f);
			Log.e("TAG", "x="+blueMalletPosition.x + "y="+blueMalletPosition.y + "z="+blueMalletPosition.z);
			// If the ray intersects (if the user touched a part of the screen that
			// intersects the mallet's bounding sphere), then set malletPressed =
			// true.
//			ImagePressed = Geometry.intersects(malletBoundingSphere, ray);
			malletPressed = Geometry.intersects(malletBoundingSphere, ray);
			
			
			/**
			 * 文字边框边界 宽∈[-1, 1]   长∈[0.5, 0.7]
			 * */
			if((normalizedX >= FrameActivity.wordsFrameLeft && normalizedX <= FrameActivity.wordsFrameRight) && (normalizedY >= FrameActivity.wordsFrameBottom && normalizedY <= FrameActivity.wordsFrameTop)){
				wordPressed = true;
			}else{
				wordPressed = false;
			}
			
			Log.e("TAG", "wordPressed="+wordPressed);
			if(wordPressed){
//				Toast.makeText(getContext(), "我选中了文字边框", Toast.LENGTH_SHORT).show();
//				FrameActivity.show();
				if(stateOnClickListener != null){ // 如果设置监听器成功，将弹出底部编辑插件
					stateOnClickListener.showEditWidget();
				}
			}
			
//			malletPressed 
		}
		
		/**
		 * 处理手指拖拽事件
		 * 
		 * @param normalizedX
		 * @param normalizedY
		 */
		public void handleTouchDrag(float normalizedX, float normalizedY){
			Log.w("TAG", "Drag..."+"normalizedX="+normalizedX+",normalizedY="+normalizedY);
//			Log.e("TAG", "malletPressed="+malletPressed);
			
		}
		
		private final float[] invertedViewProjectionMatrix = new float[16];
		
		private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY){
			final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
			final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};
			
			final float[] nearPointWorld = new float[4];
			final float[] farPointWorld = new float[4];
			
			multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
			multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
			
			divideByW(nearPointWorld);
			divideByW(farPointWorld);
			
			Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
			Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
			return new Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
		}
		
		private void divideByW(float[] vector) {
			vector[0] /= vector[3];
			vector[1] /= vector[3];
			vector[2] /= vector[3];
		}
		
	}

	
	
	
	public static boolean printOptionEnable = false;
	
	public File dir_image;
	
	public byte[] getOpenGLBitmapByte(int width_surface, int height_surface){
		try {

		    if ( printOptionEnable ) 
		    {
		        printOptionEnable = false ;
		    Log.i("hari", "printOptionEnable if condition:"+printOptionEnable);
		    int w = width_surface ;
		    int h = height_surface  ;

		    Log.i("hari", "w:"+w+"-----h:"+h);

		    int b[]=new int[(int) (w*h)];
		    int bt[]=new int[(int) (w*h)];
		    IntBuffer buffer=IntBuffer.wrap(b);
		    buffer.position(0);
		    glReadPixels(0, 0, w, h,GL_RGBA,GL_UNSIGNED_BYTE, buffer);
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
		     //Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		    inBitmap.copyPixelsFromBuffer(buffer);
		    //return inBitmap ;
		   // return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
		   inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);

		   ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
		   inBitmap.compress(CompressFormat.JPEG, 100, bos); 
		    byte[] bitmapdata = bos.toByteArray();
		    return bitmapdata;
		    /*ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

		    final Calendar c=Calendar.getInstance();
		     long mytimestamp=c.getTimeInMillis();
		    String timeStamp=String.valueOf(mytimestamp);
		    String myfile="hari"+timeStamp+".jpeg";

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
*/
		    }
		    }catch(Exception e) {
		        e.printStackTrace() ;
		    }
		return null;
	}
	
	
	private static wordStateOnClickListener stateOnClickListener;
	
	public static void setWordOnClickListener(wordStateOnClickListener onClickListener){
		stateOnClickListener = onClickListener;
	}
	
	// 为观察者模式声明接口定义
	public interface  wordStateOnClickListener{
		public void showEditWidget();
	}
	
	
}
