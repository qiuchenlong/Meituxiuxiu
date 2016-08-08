package com.meituxiuxiu.android.ui.opengl.surfaceview;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.texImage2D;
import static android.opengl.Matrix.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;







import com.meituxiuxiu.android.ui.opengl.object.Ball;
import com.meituxiuxiu.android.ui.opengl.object.Cube;
import com.meituxiuxiu.android.ui.opengl.object.SquareWithLight;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private SceneRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;
	
	
	private Bitmap bitmap;
	/**纹理Id*/
	int textureId;
	
	public static boolean isOrthoorFrustum = false;
	
	public MyGLSurfaceView(Context context, Bitmap bitmap) {
		super(context);
		// TODO Auto-generated constructor stub
		
		this.bitmap = bitmap;
		
		this.setEGLContextClientVersion(2);
		mRenderer = new SceneRenderer();
		this.setRenderer(mRenderer);
		this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float y = event.getY();
		float x = event.getX();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dy = y - mPreviousY;
			float dx = x - mPreviousX;
			
//			mRenderer.squareWithLight.yAngle += dx * TOUCH_SCALE_FACTOR;
//			mRenderer.squareWithLight.xAngle += dy * TOUCH_SCALE_FACTOR;
			mRenderer.xLocation += dx * TOUCH_SCALE_FACTOR * 0.002;
			
			break;
		}
		
		mPreviousY = y;
		mPreviousX = x;
		
		return true;
	}

	
	private class SceneRenderer implements Renderer{

		SquareWithLight squareWithLight;
		Ball ball;
		Cube cube;
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// TODO Auto-generated method stub
			Log.d("TAG", "onSurfaceCreated()");
			glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			
			Log.e("FrameSurfaceView", "纹理图 宽："+width+", 高："+height);
			
			squareWithLight = new SquareWithLight(MyGLSurfaceView.this, (int)(700), (int)(height*1.0f/width*700.0f));
			
			ball = new Ball(MyGLSurfaceView.this);
			
			cube = new Cube(MyGLSurfaceView.this);
			
			initTexture(bitmap);
			
			glEnable(GL_DEPTH_TEST);
		}

		float ratio;
		
		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// TODO Auto-generated method stub
			glViewport(0, 0, width, height);
			// 计算屏幕宽高比
			ratio = (float) width / height;
			
			
			MatrixState.setInitStack();
			
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 50);
			MatrixState.setCamera2(0, 0, 3f, 
														0, 0, 0f, 
														0f, 1.0f, 0.0f);
			
		}

		float xLocation = 1.0f;
		
		@Override
		public void onDrawFrame(GL10 gl) {
			// TODO Auto-generated method stub
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
//			if(xLocation<-0.4)
//				xLocation+=0.005;
//			else if(xLocation>=-0.4 && xLocation <0.4)
//				xLocation+=0.05;
//			else
//				xLocation+=0.005;
//			if(xLocation <= 2.0f){
				MatrixState.setLightLocation(0, 0, xLocation);
				System.out.println("xLocation = "+xLocation);
//			}
			
			
			
				
			
			MatrixState.pushMatrix();
			
			MatrixState.pushMatrix();
			MatrixState.translate(0f, 0, -1);
			squareWithLight.drawSelf(textureId);
			MatrixState.popMatrix();
			
//			MatrixState.pushMatrix();
//			MatrixState.translate(-1.2f, 0, -2);
////			ball.drawSelf();
//			cube.drawSelf(textureId);
//			MatrixState.popMatrix();
			
//			MatrixState.pushMatrix();
//			MatrixState.translate(1.2f, 0, -2);
//			ball.drawSelf();
//			MatrixState.popMatrix();
			
			MatrixState.popMatrix();
			
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
			
			// 加载纹理进显存
			texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
			
		}
		
	}
	
}
