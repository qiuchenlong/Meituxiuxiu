package com.meituxiuxiu.android.ui.opengl.object;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.ui.opengl.surfaceview.MyGLSurfaceView;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;
import com.meituxiuxiu.android.ui.opengl.utils.ShaderUtil;


/**
 * 立方体
 * @author chlqiu
 *
 */
public class Cube {
	
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
	int maTexCoorHandle;
	// int maColorHandle;

	// 具体物体的3D变换矩阵，包括旋转，平移，缩放
	static float[] mMMatrix = new float[16];

	int muMMatrixHandle;
	
	String mVertexShader;
	String mFragmentShader;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTexCoorBuffer;
	private FloatBuffer mNormalBuffer;

	static int vCount;
	
	public float yAngle = 0;
	public float xAngle = 0;
	public float zAngle = 0;
	float r = 1f;
	
	int UNIT_SIZE = 1;
	
	// Light Nornal
	int maNormalHandle;
	int maLightLocationHandle;

	int maCameraHandle;
	
	public Cube(MyGLSurfaceView mv) {
		initVertexData();
		initShader(mv);
	}


	public void initVertexData(){
		vCount = 6*6;
		
		float[] vertices = new float[]{
				// front
				Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
				Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				// back
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE,-Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				// top
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				// bottom
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				//left
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				-Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				//right
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, -Constant.UNIT_SIZE, Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, -Constant.UNIT_SIZE,
//				Constant.UNIT_SIZE, Constant.UNIT_SIZE, Constant.UNIT_SIZE
		};
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4)
				.order(ByteOrder.nativeOrder());
		
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertices);
		mVertexBuffer.position(0);
		
		
		// TexCoor
					float[] texCoor = new float[]{
							0f,   0f,
							0f,   1f,
							1f,   1f,
							
							0f,   0f,
							1f,   1f,
							1f,   0f
					};
					ByteBuffer tbb = ByteBuffer.allocateDirect(texCoor.length * 4)
							.order(ByteOrder.nativeOrder());
					
					mTexCoorBuffer = tbb.asFloatBuffer();
					mTexCoorBuffer.put(texCoor);
					mTexCoorBuffer.position(0);
		
		
		// Normal
		float[] normals = new float[]{
				0,0,1,	0,0,1, 0,0,1, 0,0,1, 0,0,1, 0,0,1,
//				0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1, 0,0,-1,
//				
//				0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0,
//				0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0, 0,-1,0
//				
//				-1,0,0, -1,0,0, -1,0,0, -1,0,0, -1,0,0, -1,0,0,
//				1,0,0, 1,0,0, 1,0,0, 1,0,0, 1,0,0, 1,0,0,
		};
		
//		float[] normals = vertices;
		
		ByteBuffer nbb = ByteBuffer.allocateDirect(normals.length * 4)
				.order(ByteOrder.nativeOrder());
		
		mNormalBuffer = nbb.asFloatBuffer();
		mNormalBuffer.put(normals);
		mNormalBuffer.position(0);
		
	}
	
	private void initShader(MyGLSurfaceView mv) {
		// TODO Auto-generated method stub
		mVertexShader = ShaderUtil.loadFromAssetsFile("vertex_light.sh", mv.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("frag_light.sh", mv.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		
		maPositionHandle = glGetAttribLocation(mProgram, "a_Position");
		maTexCoorHandle = glGetAttribLocation(mProgram, "a_TexCoor");
//		maColorHandle = glGetAttribLocation(mProgram, "a_Color");
		muMVPMatrixHandle = glGetUniformLocation(mProgram, "u_MVPMatrix");
		muRHandle = glGetUniformLocation(mProgram, "u_R");
		
		maNormalHandle = glGetAttribLocation(mProgram, "a_Normal");
		maLightLocationHandle = glGetUniformLocation(mProgram, "u_LightLocation");
		
		maCameraHandle = glGetUniformLocation(mProgram, "u_Camera");

		muMMatrixHandle = glGetUniformLocation(mProgram, "u_MMatrix");
	}
	
	public void drawSelf(int texId){
		glUseProgram(mProgram);
		
//		System.out.println("yAngle = "+yAngle);
//		System.out.println("xAngle = "+xAngle);
		
//		yAngle = -30 * 180.0f / 320;
//		xAngle = 30 * 180.0f / 320;
//		zAngle = -30 * 180.0f / 320;
		
//		MatrixState.translate(0, 0, -1);
//		MatrixState.scale(0.5f, 0.5f, 0.5f);
		
		setRotateM(mMMatrix, 0, 0, 0, 1, 0);
//		translateM(mMMatrix, 0, 0, -0.5f, 1);
		rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
		rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
		rotateM(mMMatrix, 0, zAngle, 0, 0, 1);
//		scaleM(mMMatrix, 0, 0.5f, 0.5f, 0.5f);
		
		glUniformMatrix4fv(muMMatrixHandle, 1, false, mMMatrix, 0);
		
		glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);
//		glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		
		glUniform1f(muRHandle, r * UNIT_SIZE);
//		glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3 * 4, mVertexBuffer);
//		glEnableVertexAttribArray(maPositionHandle);
//		glDrawArrays(GL_TRIANGLES, 0, vCount);
		
		glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
//		
		glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
		glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3 * 4, mVertexBuffer);
		glVertexAttribPointer(maTexCoorHandle, 2, GL_FLOAT, false, 2*4, mTexCoorBuffer);
		glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, false, 3 * 4, mNormalBuffer);
		
		glActiveTexture(GL_TEXTURE);
		glBindTexture(GL_TEXTURE_2D, texId);
		
		glEnableVertexAttribArray(maPositionHandle);
		glEnableVertexAttribArray(maTexCoorHandle);
		glEnableVertexAttribArray(maNormalHandle);
		glDrawArrays(GL_TRIANGLES, 0, vCount);
	}
	
}
