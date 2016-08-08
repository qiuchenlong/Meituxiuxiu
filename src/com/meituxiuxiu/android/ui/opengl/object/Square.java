package com.meituxiuxiu.android.ui.opengl.object;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setRotateM;
import static android.opengl.Matrix.translateM;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;
import com.meituxiuxiu.android.ui.opengl.utils.ShaderUtil;



public class Square {

	// 投影矩阵
		public static float[] mProjMatrix = new float[16];
		// 摄像机位置朝向的参数矩阵
		public static float[] mVMatrix = new float[16];
		// 总变换后矩阵
		public static float[] mMVPMatrix;
		
		int mProgram;
		
		int muMVPMatrixHandle;
		int maPositionHandle;
		int maColorHandle;
		
		String mVertexShader;
		String mFragmentShader;
		
		// 具体物体的3D变换矩阵，包括旋转，平移，缩放
		static float[] mMMatrix = new float[16];
		
		FloatBuffer mVertexBuffer;
		FloatBuffer mColorBuffer;
		
		ByteBuffer bbf;
		
		int vCount =0;
		public float xAngle = 0;
		
		public Square(FrameSurfaceView mv, int width, int height) {
			// TODO Auto-generated constructor stub
			// 初始化顶点数据
			initVertexData(width, height);
			// 初始化着色点数据
			initShader(mv);
		}

		private void initVertexData(int w, int h) {
			// TODO Auto-generated method stub
			vCount = 3;
			final float UNIT_SIZE = 0.2f;
			float[] vertices = new float[]{
					-4*UNIT_SIZE, 0, 0,
					0, -4*UNIT_SIZE, 0,
					4*UNIT_SIZE, 0, 0
			};
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4)
					.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			mVertexBuffer.put(vertices);
			mVertexBuffer.position(0);
			
			float[] colors = new float[]{
				1, 1, 1, 0,
				0, 0, 1, 0,
				0, 1, 0, 0
			};
			ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4)
					.order(ByteOrder.nativeOrder());
			mColorBuffer = cbb.asFloatBuffer();
			mColorBuffer.put(colors);
			mColorBuffer.position(0);		
			
			byte[] index = new byte[]{
					0, 1, 2
			};
			
			bbf = ByteBuffer.allocateDirect(index.length * 4)
					.order(ByteOrder.nativeOrder()).put(index);
			bbf.position(0);
		}

		private void initShader(FrameSurfaceView mv) {
			// TODO Auto-generated method stub
			mVertexShader = ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
			mFragmentShader = ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());
			mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
			maPositionHandle = glGetAttribLocation(mProgram, "a_Position");
			maColorHandle = glGetAttribLocation(mProgram, "a_Color");
			muMVPMatrixHandle = glGetUniformLocation(mProgram, "u_MVPMatrix");
		}

		public static float[] getFinalMatrix(float[] spec){
			mMVPMatrix = new float[16];
			multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
			multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
			return mMVPMatrix;
		}
		
		public void drawSelf(){
			
			glUseProgram(mProgram);
			
			setRotateM(mMMatrix, 0, 0, 0, 1, 0);
			translateM(mMMatrix, 0, 0, 0, 1);
			rotateM(mMMatrix, 0, xAngle, 1, 0, 0);
			
			glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);
			
			glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3*4, mVertexBuffer);
			
			glVertexAttribPointer(maColorHandle, 4, GL_FLOAT, false, 4*4, mColorBuffer);
			
			glEnableVertexAttribArray(maPositionHandle);
			glEnableVertexAttribArray(maColorHandle);
			glDrawArrays(GL_TRIANGLES, 0, vCount);
//			glDrawElements(GL_TRIANGLES, 3*3, GL_UNSIGNED_BYTE, bbf);
		}
	
}
