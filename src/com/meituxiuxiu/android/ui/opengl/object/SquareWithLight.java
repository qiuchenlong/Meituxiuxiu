package com.meituxiuxiu.android.ui.opengl.object;

import static android.opengl.GLES20.*;
import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.util.Log;

import com.meituxiuxiu.android.constant.Constant;
import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;
import com.meituxiuxiu.android.ui.opengl.surfaceview.MyGLSurfaceView;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;
import com.meituxiuxiu.android.ui.opengl.utils.ShaderUtil;



/**
 * 带光照的正方形类
 * 
 * @author qiuchenlong on 2016.04.25
 *
 */
public class SquareWithLight {

	// 投影矩阵
		public static float[] mProjMatrix = new float[16];
		// 摄像机位置朝向的参数矩阵
		public static float[] mVMatrix = new float[16];
		// 总变换后矩阵
		public static float[] mMVPMatrix;
		
		int mProgram;
		
		int muMVPMatrixHandle;
		int maPositionHandle;
		int maTexCoorHandle;
		int muRHandle;
		
		String mVertexShader;
		String mFragmentShader;
		
		// 具体物体的3D变换矩阵，包括旋转，平移，缩放
		static float[] mMMatrix = new float[16];
		
		int muMMatrixHandle;
		
		FloatBuffer mVertexBuffer;
		FloatBuffer mTexCoorBuffer;
		
		ByteBuffer bbf;
		
		int vCount =0;


		float lightOffset;
		
		// Light Normal
		int maNormalHandle;
		int maLightLocationHandle;
//		int maLightDirectionHandle;
		FloatBuffer mNormalBuffer;
		
		int maCameraHandle;
		
		public SquareWithLight(MyGLSurfaceView mv, int width, int height) {
			// TODO Auto-generated constructor stub
			// 初始化顶点数据
			initVertexData(width, height);
			// 初始化着色点数据
			initShader(mv);
		}

		private void initVertexData(int w, int h) {
			// TODO Auto-generated method stub
			vCount = 6 * 1;
			final float UNIT_SIZE = 0.005f;
			float[] vertices = new float[]{
					-w/2*UNIT_SIZE,	h/2*UNIT_SIZE,	0,
					-w/2*UNIT_SIZE,	-h/2*UNIT_SIZE,	0,
					w/2*UNIT_SIZE,	-h/2*UNIT_SIZE,	0,
					
					-w/2*UNIT_SIZE,	h/2*UNIT_SIZE,	0,
					w/2*UNIT_SIZE,	-h/2*UNIT_SIZE,	0,
					w/2*UNIT_SIZE,	h/2*UNIT_SIZE,	0
			};
			
			Log.e("TAG", "w/2="+w/2*UNIT_SIZE);
			Log.e("TAG", "h/2="+h/2*UNIT_SIZE);
			
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
			
			
			
			// Light Normal   (ball's normal == vertices's normal)
			
			float[] normals = new float[]{
				0,0,1,	0,0,1,	0,0,1,	0,0,1,	0,0,1,	0,0,1	
			};
			
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
			muMVPMatrixHandle = glGetUniformLocation(mProgram, "u_MVPMatrix");
			
			maNormalHandle = glGetAttribLocation(mProgram, "a_Normal");
			maLightLocationHandle = glGetUniformLocation(mProgram, "u_LightLocation");
//			maLightDirectionHandle = glGetUniformLocation(mProgram, "u_LightDirection");
			
			
			maCameraHandle = glGetUniformLocation(mProgram, "u_Camera");

			muMMatrixHandle = glGetUniformLocation(mProgram, "u_MMatrix");
		}

		public static float[] getFinalMatrix(float[] spec){
			mMVPMatrix = new float[16];
			multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
			multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
			return mMVPMatrix;
		}
		
		public float yAngle = 0;
		public float xAngle = 0;
		public float zAngle = 0;
		
		public void drawSelf(int texId){
			
			glUseProgram(mProgram);
			
//			setRotateM(mMMatrix, 0, 0, 0, 1, 0);
//			translateM(mMMatrix, 0, 0, 0, 1);
			
//			MatrixState.translate(0, 0, -1);
			
			setRotateM(mMMatrix, 0, 0, 0, 1, 0);
//			translateM(mMMatrix, 0, 0, 0, 1);
			rotateM(mMMatrix, 0, yAngle, 0, 1, 0);
			rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
			rotateM(mMMatrix, 0, zAngle, 0, 0, 1);
//			scaleM(mMMatrix, 0, 2f, 2f, 2f);
			
			
			glUniformMatrix4fv(muMMatrixHandle, 1, false, mMMatrix, 0);
			
			glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);
			
			glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
			
			glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
//			glUniform3fv(maLightDirectionHandle, 1, MatrixState.lightDirectionFB);
			
			glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3*4, mVertexBuffer);
			glVertexAttribPointer(maTexCoorHandle, 2, GL_FLOAT, false, 2*4, mTexCoorBuffer);
			glVertexAttribPointer(maNormalHandle, 3, GL_FLOAT, false, 3 * 4, mNormalBuffer);
			
			glActiveTexture(GL_TEXTURE);
			glBindTexture(GL_TEXTURE_2D, texId);
			
			glEnableVertexAttribArray(maPositionHandle);
			glEnableVertexAttribArray(maTexCoorHandle);
			glEnableVertexAttribArray(maNormalHandle);
			
			
			glDrawArrays(GL_TRIANGLES, 0, vCount);
//			glDrawElements(GL_TRIANGLES, 3*3, GL_UNSIGNED_BYTE, bbf);
		}
	
}
