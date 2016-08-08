package com.meituxiuxiu.android.ui.opengl.surfaceview;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.object.ParticleShooter;
import com.meituxiuxiu.android.ui.opengl.object.ParticleSystem;
import com.meituxiuxiu.android.ui.opengl.object.Table;
import com.meituxiuxiu.android.ui.opengl.programs.ColorShaderProgram;
import com.meituxiuxiu.android.ui.opengl.programs.ParticleShaderProgram;
import com.meituxiuxiu.android.ui.opengl.programs.TextureShaderProgram;
import com.meituxiuxiu.android.ui.opengl.utils.Geometry.Point;
import com.meituxiuxiu.android.ui.opengl.utils.Geometry.Vector;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixHelper;
import com.meituxiuxiu.android.ui.opengl.utils.TextureHelper;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView.Renderer;

public class AirHockeyRenderer implements Renderer{

	
	private Context context;
	
	private float[] projectionMatrix = new float[16];
	private float[] modelMatrix = new float[16];
	
	private Table table;
	// Program
		private ParticleShaderProgram particleProgram;
		private ParticleSystem particleSystem;
		// red、green、blue   Particle(粒子)
		private ParticleShooter redParticleShooter;
//		private ParticleShooter greenParticleShooter;
//		private ParticleShooter blueParticleShooter;
		// Time
		private long globalStartTime;
	
	private TextureShaderProgram textureShaderProgram;
	private ColorShaderProgram colorShaderProgram;
	
	private int texture;
	
	
	// add a few new matrix definitions
	private float[] viewMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	private float[] modelViewProjectionMatrix = new float[16];
	
	
	private float floatX = 0f;
	private float floatY = 0f;
	private float floatZ = 0f;
	
	
	public AirHockeyRenderer(Context context) {
		this.context = context;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
		
		
		table = new Table();
		
		particleProgram = new ParticleShaderProgram(context);
		particleSystem = new ParticleSystem(10000);
		globalStartTime = System.nanoTime(); // 返回系统时间，以纳秒为单位，提供更加精确的时间点
		final Vector particleDirection = new Vector(0f, 0.5f, 0f);
		
		textureShaderProgram = new TextureShaderProgram(context);
		colorShaderProgram = new ColorShaderProgram(context);
		
		texture = TextureHelper.loadTexture(context, R.drawable.hemanting);
		
		
		final float angleVarianceInDegrees = 5f;
		final float speedVariance = 1f;
		
		redParticleShooter = new ParticleShooter(
				new Point(floatX, floatY, floatZ),
				particleDirection,
				Color.rgb(255, 50, 5),
				angleVarianceInDegrees,
				speedVariance);
//		greenParticleShooter = new ParticleShooter(
//				new Point(0f, 0f, 0f),
//				particleDirection,
//				Color.rgb(25, 255, 25),
//				angleVarianceInDegrees,
//				speedVariance);
//		blueParticleShooter = new ParticleShooter(
//				new Point(0.25f, 0f, 0f),
//				particleDirection,
//				Color.rgb(5, 50, 255),
//				angleVarianceInDegrees,
//				speedVariance);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		glViewport(0, 0, width, height);
		
		
		MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width
				/ (float) height, 1f, 10f);
		
		setIdentityM(viewMatrix, 0);
		translateM(viewMatrix, 0, 0f, 0f, -3f);
		/* 变换叠加: 可通过变换矩阵相乘得出想要的变换矩阵：
		参数一：存放结果的总变换矩阵
		参数二：结果矩阵偏移量
		参数三：左矩阵
		参数四：左矩阵偏移量
		参数五：右矩阵
		参数六：右矩阵偏移量
		
		变换技巧： 利用栈存储变换矩阵的算法可实现复杂场景的图形变换和提高渲染效率。
        变换顺序： 矩阵相乘是讲究顺序的， 比如先平移再缩放 和 先缩放再平移的效果是不同的。
		*/
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		glClear(GL_COLOR_BUFFER_BIT);
		
		
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		
		
		// Draw the table
		positionTableInScene();
		textureShaderProgram.useProgram();
//		textureShaderProgram.setUniforms(projectionMatrix, texture);
		textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
		table.bindData(textureShaderProgram);
		table.draw();
		
		
		
		float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
		redParticleShooter.addParticles(particleSystem, currentTime, 5);
//		greenParticleShooter.addParticles(particleSystem, currentTime, 5);
//		blueParticleShooter.addParticles(particleSystem, currentTime, 5);
		particleProgram.useProgram();
		particleProgram.setUniforms(viewProjectionMatrix, currentTime);
		particleSystem.bindData(particleProgram);
		particleSystem.draw();
				
				
	}
	
	private void positionTableInScene() {
		// The table is defined in terms of X & Y coordinates, so we rotate it
		// 90 degrees to lie flat on the XZ plane.
		setIdentityM(modelMatrix, 0);
//		rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
		multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
		0, modelMatrix, 0);
		}
	

}
