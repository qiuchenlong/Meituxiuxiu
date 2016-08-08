package com.meituxiuxiu.android.ui.opengl.object;


import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.meituxiuxiu.android.ui.opengl.surfaceview.MyGLSurfaceView;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;
import com.meituxiuxiu.android.ui.opengl.utils.ShaderUtil;

import android.graphics.Canvas.VertexMode;


/**
 * 球类
 * @author chlqiu
 *
 */
public class Ball {

	
	// 投影矩阵
	public static float[] mProjMatrix = new float[16];
	// 摄像机位置朝向的参数矩阵
	public static float[] mVMatrix = new float[16];
	// 总变换后矩阵
	public static float[] mMVPMatrix;

	private int mProgram;

	int muMVPMatrixHandle;
	int maPositionHandle;
	int muRHandle;
//	int maColorHandle;

	// 具体物体的3D变换矩阵，包括旋转，平移，缩放
	static float[] mMMatrix = new float[16];
				
	int muMMatrixHandle;
	
	String mVertexShader;
	String mFragmentShader;
	FloatBuffer mVertexBuffer;
//	FloatBuffer mColorBuffer;
	int vCount = 0;
	
	public float yAngle = 0;
	public float xAngle = 0;
	public float zAngle = 0;
	float r = 0.8f;
	
	float lightOffset;
	
	int UNIT_SIZE = 1;
	
	// Light Nornal
	int maNormalHandle;
	int maLightLocationHandle;
	FloatBuffer mNormalBuffer;
	
	int maCameraHandle;
	
	
	public Ball(MyGLSurfaceView mv){
		initVertexData();
		initShader(mv);
	}

	private void initVertexData() {
		// TODO Auto-generated method stub
		ArrayList<Float> alVertex = new ArrayList<Float>();
		final int angleSpan = 10;
		for(int vAngle = -90; vAngle < 90; vAngle += angleSpan){
			for(int hAngle = 0; hAngle <= 360; hAngle += angleSpan){
				float x0 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle)));
				float y0 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle)));
				float z0 = (float) (r*UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));
				
				float x1 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.cos(Math.toRadians(hAngle + angleSpan)));
				float y1 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle)) * Math.sin(Math.toRadians(hAngle + angleSpan)));
				float z1 = (float) (r*UNIT_SIZE * Math.sin(Math.toRadians(vAngle)));
				
				float x2 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.cos(Math.toRadians(hAngle + angleSpan)));
				float y2 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle + angleSpan)));
				float z2 = (float) (r*UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));
				
				float x3 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.cos(Math.toRadians(hAngle)));
				float y3 = (float) (r*UNIT_SIZE * Math.cos(Math.toRadians(vAngle + angleSpan)) * Math.sin(Math.toRadians(hAngle)));
				float z3 = (float) (r*UNIT_SIZE * Math.sin(Math.toRadians(vAngle + angleSpan)));
			
				alVertex.add(x1 + lightOffset);alVertex.add(y1);alVertex.add(z1);
				alVertex.add(x3 + lightOffset);alVertex.add(y3);alVertex.add(z3);
				alVertex.add(x0 + lightOffset);alVertex.add(y0);alVertex.add(z0);
				
				alVertex.add(x1 + lightOffset);alVertex.add(y1);alVertex.add(z1);
				alVertex.add(x2 + lightOffset);alVertex.add(y2);alVertex.add(z2);
				alVertex.add(x3 + lightOffset);alVertex.add(y3);alVertex.add(z3);
			}
		}
		
		vCount = alVertex.size() / 3;
		float[] vertices = new float[vCount * 3];
		for(int i = 0 ; i<alVertex.size(); i++){
			vertices[i] = alVertex.get(i);
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4)
				.order(ByteOrder.nativeOrder());
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		
		// Light Normal   (ball's normal == vertices's normal)
		ByteBuffer nbb = ByteBuffer.allocateDirect(vertices.length * 4)
				.order(ByteOrder.nativeOrder());
		mNormalBuffer = nbb.asFloatBuffer();
		mNormalBuffer.put(vertices);
		mNormalBuffer.position(0);
	}

	private void initShader(MyGLSurfaceView mv) {
		// TODO Auto-generated method stub
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_light.sh", mv.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_light.sh", mv.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		
		maPositionHandle = glGetAttribLocation(mProgram, "a_Position");
//		maColorHandle = glGetAttribLocation(mProgram, "a_Color");
		muMVPMatrixHandle = glGetUniformLocation(mProgram, "u_MVPMatrix");
		muRHandle = glGetUniformLocation(mProgram, "u_R");
		
		maNormalHandle = glGetAttribLocation(mProgram, "a_Normal");
		maLightLocationHandle = glGetUniformLocation(mProgram, "u_LightLocation");
		
		maCameraHandle = glGetUniformLocation(mProgram, "u_Camera");

		muMMatrixHandle = glGetUniformLocation(mProgram, "u_MMatrix");
	}
	
	public void drawSelf(){
		lightOffset+=0.1f;
		glUseProgram(mProgram);
		
		setRotateM(mMMatrix, 0, 0, 0, 1, 0);
		translateM(mMMatrix, 0, 0, 0, 1);
//		rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
//		rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
//		rotateM(mMMatrix, 0, zAngle, 0, 0, 1);
		
		glUniformMatrix4fv(muMMatrixHandle, 1, false, mMMatrix, 0);
		
//		glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);
		glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		
		glUniform1f(muRHandle, r * UNIT_SIZE);
//		glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3 * 4, mVertexBuffer);
//		glEnableVertexAttribArray(maPositionHandle);
//		glDrawArrays(GL_TRIANGLES, 0, vCount);
		
		glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
		
		glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
		glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3 * 4, mVertexBuffer);;
		glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, false, 3 * 4, mNormalBuffer);
		glEnableVertexAttribArray(maPositionHandle);
		glEnableVertexAttribArray(maNormalHandle);
		glDrawArrays(GL_TRIANGLES, 0, vCount);
	}
	
}
