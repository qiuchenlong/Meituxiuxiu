package com.meituxiuxiu.android.ui.opengl.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class DirectDrawer {
	private final String vertexShaderCode = "attribute vec4 vPosition;"
			+ "attribute vec2 inputTextureCoordinate;"
			+ "varying vec2 textureCoordinate;" + "void main()" + "{"
			+ "gl_Position = vPosition;"
			+ "textureCoordinate = inputTextureCoordinate;" + "}";

	private final String fragmentShaderCode = "#extension GL_OES_EGL_image_external : require\n"
			+ "precision mediump float;"
			+ "varying vec2 textureCoordinate;\n"
			+ "uniform samplerExternalOES s_texture;\n"
			+ "uniform sampler2D u_texture;\n"
			+ "void main() {"
//			+ "vec2 offset0 = vec2(-1.0, -1.0);"
//			+ "vec2 offset1 = vec2(0.0, -1.0);"
//			+ "vec2 offset2 = vec2(1.0, -1.0);"
//			+ ""
//			+ "vec2 offset3 = vec2(-1.0, 0.0);"
//			+ "vec2 offset4 = vec2(0.0, 0.0);"
//			+ "vec2 offset5 = vec2(1.0, 0.0);"
//			+ ""
//			+ "vec2 offset6 = vec2(-1.0, 1.0);"
//			+ "vec2 offset7 = vec2(0.0, -1.0);"
//			+ "vec2 offset8 = vec2(1.0, 1.0);"
//			+ ""
//			+ "const float scaleFactor = 0.9;"
//			+ ""
//			+ "float kernelValue0 = 0.0;"
//			+ "float kernelValue1 = -1.0;"
//			+ "float kernelValue2 = 0.0;"
//			+ ""
//			+ "float kernelValue3 = -1.0;"
//			+ "float kernelValue4 = 5.0;"
//			+ "float kernelValue5 = -1.0;"
//			+ ""
//			+ "float kernelValue6 = 0.0;"
//			+ "float kernelValue7 = -1.0;"
//			+ "float kernelValue8 = 0.0;"
//			+ ""
//			+ "vec4 sum;"
//			+ ""
//			+ "vec4 cTemp0,cTemp1,cTemp2,cTemp3,cTemp4,cTemp5,cTemp6,cTemp7,cTemp8;"
//			+ ""
//			+ "cTemp0 = texture2D(s_texture, textureCoordinate.st + offset0.xy/512.0);"
//			+ "cTemp1 = texture2D(s_texture, textureCoordinate.st + offset1.xy/512.0);"
//			+ "cTemp2 = texture2D(s_texture, textureCoordinate.st + offset2.xy/512.0);"
//			+ "cTemp3 = texture2D(s_texture, textureCoordinate.st + offset3.xy/512.0);"
//			+ "cTemp4 = texture2D(s_texture, textureCoordinate.st + offset4.xy/512.0);"
//			+ "cTemp5 = texture2D(s_texture, textureCoordinate.st + offset5.xy/512.0);"
//			+ "cTemp6 = texture2D(s_texture, textureCoordinate.st + offset6.xy/512.0);"
//			+ "cTemp7 = texture2D(s_texture, textureCoordinate.st + offset7.xy/512.0);"
//			+ "cTemp8 = texture2D(s_texture, textureCoordinate.st + offset8.xy/512.0);"
//			+ ""
//			+ "sum = kernelValue0*cTemp0"
//			+ "+ kernelValue1*cTemp1"
//			+ "+ kernelValue2*cTemp2"
//			+ "+ kernelValue3*cTemp3"
//			+ "+ kernelValue4*cTemp4"
//			+ "+ kernelValue5*cTemp5"
//			+ "+ kernelValue6*cTemp6"
//			+ "+ kernelValue7*cTemp7"
//			+ "+ kernelValue8*cTemp8;"
//			+ ""
//			+ "	gl_FragColor = sum * scaleFactor;"
//			+ ""
			+ "  gl_FragColor = texture2D( s_texture, textureCoordinate );\n"
			+ ""
//			+ "  gl_FragColor = texture2D( u_texture, textureCoordinate );\n"
			+ ""
//			+ "vec4 color1 = texture2D(s_texture, textureCoordinate);"
//			+ "vec4 color2 = texture2D(u_texture, textureCoordinate);"
//			+ "gl_FragColor = color1*(1.0-0.5)+color2*0.5;"
			+ "}";

	private FloatBuffer vertexBuffer, textureVerticesBuffer;
	private ShortBuffer drawListBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mTextureCoordHandle;

	private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

	// number of coordinates per vertex in this array
	private static final int COORDS_PER_VERTEX = 2;

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															
	// vertex
	float squareCoords[];
	
	// CAMERA_FACING_BACK 后置 顶点坐标
	static float squareCoordsBACK[] = { -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, };
	
	// CAMERA_FACING_FRONT 前置 顶点坐标
	static float squareCoordsFRONT[] = {-1.0f, -1.0f,		-1.0f, 1.0f,	1.0f, 1.0f,	1.0f, -1.0f,};

	static float textureVertices[] = { 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, };

	private int texture;

	// face   true:前置 	false:后置
	public DirectDrawer(boolean face, int texture) {
		if(face){
			squareCoords = squareCoordsFRONT;
		}else{
			squareCoords = squareCoordsBACK;
		}
		
		this.texture = texture;
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);

		ByteBuffer bb2 = ByteBuffer.allocateDirect(textureVertices.length * 4);
		bb2.order(ByteOrder.nativeOrder());
		textureVerticesBuffer = bb2.asFloatBuffer();
		textureVerticesBuffer.put(textureVertices);
		textureVerticesBuffer.position(0);

		int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram); // creates OpenGL ES program executables
	}

	public void draw(float[] mtx, int tempTexture) {
//		if(tempTexture != 0)
//			texture = tempTexture;
		
//		System.out.println("tempTexture="+tempTexture);
			
		GLES20.glUseProgram(mProgram);
		
		int textureHandle = GLES20.glGetUniformLocation(mProgram, "u_texture");
		

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
		
//		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tempTexture);
//		GLES20.glUniform1i(textureHandle, 0);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the <insert shape here> coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

		mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram,
				"inputTextureCoordinate");
		GLES20.glEnableVertexAttribArray(mTextureCoordHandle);

		// textureVerticesBuffer.clear();
		// textureVerticesBuffer.put( transformTextureCoordinates(
		// textureVertices, mtx ));
		// textureVerticesBuffer.position(0);
		GLES20.glVertexAttribPointer(mTextureCoordHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, textureVerticesBuffer);

		GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
				GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
		GLES20.glDisableVertexAttribArray(mTextureCoordHandle);
	}

	private int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}

	private float[] transformTextureCoordinates(float[] coords, float[] matrix) {
		float[] result = new float[coords.length];
		float[] vt = new float[4];

		for (int i = 0; i < coords.length; i += 2) {
			float[] v = { coords[i], coords[i + 1], 0, 1 };
			Matrix.multiplyMV(vt, 0, matrix, 0, v, 0);
			result[i] = vt[0];
			result[i + 1] = vt[1];
		}
		return result;
	}
}
