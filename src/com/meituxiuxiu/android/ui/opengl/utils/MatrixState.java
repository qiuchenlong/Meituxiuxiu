package com.meituxiuxiu.android.ui.opengl.utils;

import static android.opengl.Matrix.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.R.anim;

public class MatrixState {

	private static float[] mProjMatrix = new float[16];
	private static float[] mVMatrix = new float[16];
	private static float[] mMVPMatrix;

	/**
	 * 设置摄像机位置
	 * @param cx
	 * @param cy
	 * @param cz
	 * @param tx
	 * @param ty
	 * @param tz
	 * @param upx
	 * @param upy
	 * @param upz
	 */
	public static void setCamera(float cx, float cy, float cz, float tx,
			float ty, float tz, float upx, float upy, float upz) {
		// TODO Auto-generated method stub
		setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
	}

	/**
	 * 设置正交投影参数
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 */
	public static void setProjectOrtho(float left, float right, float bottom,
			float top, float near, float far) {
		// TODO Auto-generated method stub
		orthoM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}
	
	/**
	 * 设置透视投影参数
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param near
	 * @param far
	 */
	public static void setProjectFrustum(float left, float right, float bottom,
			float top, float near, float far){
		frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
	}

	/**
	 * 通过一系列矩阵运算，获得最终矩阵
	 * @param spec
	 * @return
	 */
	public static float[] getFinalMatrix(float[] spec) {
		mMVPMatrix = new float[16];
		multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);
		multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}
	
	
	public static float[] getFinalMatrix() {
		mMVPMatrix = new float[16];
		multiplyMM(mMVPMatrix, 0, mVMatrix, 0, currMatrix, 0);
		multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}
	
	
	private static float[] currMatrix;
	static float[][] mStack = new float[10][16];
	static int stackTop = -1;
	
	public static void setInitStack(){
		currMatrix = new float[16];
		setRotateM(currMatrix, 0, 0, 1, 0, 0);
	}
	
	public static void pushMatrix(){
		stackTop ++;
		for(int i=0; i<16; i++){
			mStack[stackTop][i] = currMatrix[i];
		}
	}
	
	public static void popMatrix(){
		for(int i=0; i<16; i++){
			currMatrix[i] = mStack[stackTop][i];
		}
		stackTop--;
	}
	
	/**
	 * 对当前矩阵执行平移动作
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void translate(float x, float y, float z){
		translateM(currMatrix, 0, x, y, z);
	}
	
	
	/**
	 * 对当前矩阵执行旋转动作
	 * @param mAngle
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void rotate(float mAngle, float x, float y, float z){
		rotateM(currMatrix, 0, mAngle, x, y, z);
	}
	
	
	
	/**
	 * 对当前矩阵执行缩放动作
	 * @param x
	 * @param y
	 * @param z
	 */
	public static void scale(float x, float y, float z){
		scaleM(currMatrix, 0, x, y, z);
	}
	
	
	
	
	// 光源
	public static float[] lightLocation = new float[]{0, 0, 0};
	public static FloatBuffer lightPositionFB;
	static ByteBuffer llbbL = ByteBuffer.allocateDirect(3 * 4);
	
	public static void setLightLocation(float x,float y,float z){
		llbbL.clear();
		
		lightLocation[0] = x;
		lightLocation[1] = y;
		lightLocation[2] = z;
		
		llbbL.order(ByteOrder.nativeOrder());
		lightPositionFB = llbbL.asFloatBuffer();
		lightPositionFB.put(lightLocation);
		lightPositionFB.position(0);
	}
	
	
	public static float[] lightDirection = new float[]{0, 0, 1};
	public static FloatBuffer lightDirectionFB;
	
	public static void setLightDirection(float x,float y,float z){
		llbbL.clear();
		
		lightDirection[0] = x;
		lightDirection[1] = y;
		lightDirection[2] = z;
		
		llbbL.order(ByteOrder.nativeOrder());
		lightDirectionFB = llbbL.asFloatBuffer();
		lightDirectionFB.put(lightDirection);
		lightDirectionFB.position(0);
	}

	
	static ByteBuffer llbb = ByteBuffer.allocateDirect(3 * 4);			// 待用的字节缓冲
	static float[] cameraLocation = new float[3];                              // 摄像机位置
	public static FloatBuffer cameraFB;												// 摄像机位置数据缓冲
	
	public static void setCamera2(
			float cx, float cy, float cz,											// 摄像位置的X、Y、Z坐标
			float tx, float ty, float tz,											// 观察目标点X、Y、Z坐标
			float upx, float upy, float upz									// up向量在X、Y、Z轴上的分量
			){
		setLookAtM(mVMatrix, 0, cx, cy, cz, tx, ty, tz, upx, upy, upz);
		
		llbb.clear();
		
		cameraLocation[0]=cx;
		cameraLocation[1]=cy;
		cameraLocation[2]=cz;
		
		llbb.order(ByteOrder.nativeOrder());
		cameraFB = llbb.asFloatBuffer();
		cameraFB.put(cameraLocation);
		cameraFB.position(0);
	}
	
	public static float[] getProjMatrix(){
		return mProjMatrix;
	}
	
}
