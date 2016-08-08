package com.meituxiuxiu.android.ui.opengl.object;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glReadPixels;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.meituxiuxiu.android.ui.opengl.surfaceview.FrameSurfaceView;
import com.meituxiuxiu.android.ui.opengl.utils.MatrixState;
import com.meituxiuxiu.android.ui.opengl.utils.ShaderUtil;

public class SquareByTexture {
	int mProgram;
	int muMVPMatrixHandle;
	int maPositionHandle;
	int maTexCoorHandle;
	
//	int texId;
	
	String mVertexShader;
	String mFragmentShader;
	
	FloatBuffer mVertexBuffer;
	FloatBuffer mTexCoorBuffer;
	
	int vCount = 0;
	public float xAngle = 0;
	public float yAngle = 0;
	public float zAngle = 0;
	
	
	public static boolean isSaveFlag = false;
	
	
	/**附加属性：纹理的宽、高*/
	public SquareByTexture(FrameSurfaceView mv, int width, int height){
		initVertexData(width, height);
		initShader(mv);
	}

	private void initVertexData(int w, int h) {
		// TODO Auto-generated method stub
		vCount = 3 * 2;
		final float UNIT_SIZE = 0.005f;//0.15f;
		float[] vertices = new float[]{
//				0*UNIT_SIZE,	11*UNIT_SIZE,	0,
//				-11*UNIT_SIZE,	-11*UNIT_SIZE,	0,
//				11*UNIT_SIZE,	-11*UNIT_SIZE,	0,
				
				/*-11*UNIT_SIZE,	11*UNIT_SIZE,	0,
				-11*UNIT_SIZE,	-11*UNIT_SIZE,	0,
				11*UNIT_SIZE,	-11*UNIT_SIZE,	0,
				
				-11*UNIT_SIZE,	11*UNIT_SIZE,	0,
				11*UNIT_SIZE,	-11*UNIT_SIZE,	0,
				11*UNIT_SIZE,	11*UNIT_SIZE,	0,*/
				
				-w/2*UNIT_SIZE,	h/2*UNIT_SIZE,	0,
				-w/2*UNIT_SIZE,	-h/2*UNIT_SIZE,	0,
				w/2*UNIT_SIZE,	-h/2*UNIT_SIZE,	0,
				
				-w/2*UNIT_SIZE,	h/2*UNIT_SIZE,	0,
				w/2*UNIT_SIZE,	-h/2*UNIT_SIZE,	0,
				w/2*UNIT_SIZE,	h/2*UNIT_SIZE,	0,
				
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
//				0.5f,	0,
//				0f,	1.0f,
//				1.0f,	1.0f
				
				/*1f,	0f,
				1f,	1f,
				0f,	1f,
				
				1f,	0f,
				0f,	1f,
				0f,	0f	*/
				
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
		
	}

	private void initShader(FrameSurfaceView mv) {
		// TODO Auto-generated method stub
		mVertexShader = ShaderUtil.loadFromAssetsFile("t_vertex.sh", mv.getResources());
		mFragmentShader = ShaderUtil.loadFromAssetsFile("t_frag.sh", mv.getResources());
		mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
		maPositionHandle = glGetAttribLocation(mProgram, "a_Position");
		maTexCoorHandle = glGetAttribLocation(mProgram, "a_TexCoor");
		muMVPMatrixHandle = glGetUniformLocation(mProgram, "u_MVPMatrix");
		
//		texId = glGetUniformLocation(mProgram, "s_Texture");
	}
	
	// Mouse Rotation
	  private float mAngleX=0, mAngleY=0, mAngleZ=0, mdx=1, mdy=1, mdz=1;
	
	public void drawSelf(int texId){
		glUseProgram(mProgram);
		
		MatrixState.setInitStack();
		MatrixState.translate(0, 0, -1);
		/**平移变换*/
		MatrixState.translate(xAngle, 0, 0);
		MatrixState.translate(0, yAngle, 0);
		/**由于纹理贴图的问题，需要绕着Z轴旋转180度，才会显示正常的图像*/
		
		/*MatrixState.rotate(xAngle, 1, 0, 0);
		MatrixState.rotate(yAngle, 0, 1, 0);
		MatrixState.rotate(zAngle, 0, 0, 1);*/
		
//	    // Handle finger rotate on the object
//		MatrixState.rotate(mAngleX, 0, 1, 0); // Yes there is an inversion between X and Y
//		MatrixState.rotate(mAngleY, 1, 0, 0);
		
		
		
		
		/**放大缩小变换*/
		MatrixState.scale(mdz, mdz, 1);
		
		/**旋转变换*/
		MatrixState.rotate(mAngleZ, 0, 0, 1);
		
		
//		MatrixState.translate(mdx * 0.01f, mdy * 0.01f, mdz * 0.01f);
	
		
		
		glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
		
		glVertexAttribPointer(maPositionHandle, 3, GL_FLOAT, false, 3*4, mVertexBuffer);
		glVertexAttribPointer(maTexCoorHandle, 2, GL_FLOAT, false, 2*4, mTexCoorBuffer);
		
		glEnableVertexAttribArray(maPositionHandle);
		glEnableVertexAttribArray(maTexCoorHandle);
		
		glActiveTexture(GL_TEXTURE);
		glBindTexture(GL_TEXTURE_2D, texId);
		
//		glDrawArrays(GL_TRIANGLES, 0, vCount);
		
		glDrawArrays(GL_TRIANGLE_FAN, 0, vCount);
		
		
//		saveBitmap(1080, 1920);
		
//		if(isSaveFlag){
//			saveBitmap2(1080, 1920);
//			isSaveFlag = false;
//		}
		
	}
	
	/** Rotation called from onTouchEvent */
	  public void rotateXY(float angleX, float angleY) {
	    mAngleX += angleX; 
	    mAngleY += angleY; 
	  }
	  /** Rotation and zoom called from onTouchEvent */
	  public void rotateZoom(float angle, float dx, float dy, float dd) {
	    mAngleZ += angle;
	    mdx += dx;
	    mdy += dy;
	    if(mdz + dd > 0.1f && mdz + dd < 20f)
	    	mdz += dd;
	  }
	  public void setZoom(float dd){
		  mdz = dd;
	  }
	  /** Restore rotation to identity */
	  public void rotateRestore() {
	    mAngleX = mAngleY = mAngleZ = mdx = mdy = mdz = 0;
	  }
	
	/**OpenGL ES 保存 Bitmap*/
	private void saveBitmap(int width, int height){
		IntBuffer PixelBuffer = IntBuffer.allocate(width*height);
		PixelBuffer.position(0);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, PixelBuffer);
		
		PixelBuffer.position(0);
		int pix[] = new int[width * height];
		PixelBuffer.get(pix);
		
		Bitmap bmp = Bitmap.createBitmap(pix, width, height, Bitmap.Config.ARGB_8888);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("/sdcard/screen.png");// 注意app的sdcard读写权限问题
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bmp.compress(CompressFormat.PNG, 100, fos);// 压缩成png,100%显示效果
		try {
			fos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*** 正确的保存Bitmap方法 */
	private void saveBitmap2(int w, int h){
		int bitmapBuffer[] = new int[w * h];
	    int bitmapSource[] = new int[w * h];
	    IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
	    intBuffer.position(0);

	    try {
	        glReadPixels(0, 0, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
	        int offset1, offset2;
	        for (int i = 0; i < h; i++) {
	            offset1 = i * w;
	            offset2 = (h - i - 1) * w;
	            for (int j = 0; j < w; j++) {
	                int texturePixel = bitmapBuffer[offset1 + j];
	                int blue = (texturePixel >> 16) & 0xff;
	                int red = (texturePixel << 16) & 0x00ff0000;
	                int pixel = (texturePixel & 0xff00ff00) | red | blue;
	                bitmapSource[offset2 + j] = pixel;
	            }
	        }
	    } catch (GLException e) {
	        return ;
	    }
		
		Bitmap bmp = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream("/sdcard/screen.png");// 注意app的sdcard读写权限问题
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		bmp.compress(CompressFormat.PNG, 100, fos);// 压缩成png,100%显示效果
		try {
			fos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Bitmap getOpenGLBitmap(int x, int y, int w, int h){
//		IntBuffer PixelBuffer = IntBuffer.allocate(width*height);
//		PixelBuffer.position(0);
//		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, PixelBuffer);
//		
//		PixelBuffer.position(0);
//		int pix[] = new int[width * height];
//		PixelBuffer.get(pix);
		
		int bitmapBuffer[] = new int[w * h];
	    int bitmapSource[] = new int[w * h];
	    IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
	    intBuffer.position(0);

	    try {
	        glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
	        int offset1, offset2;
	        for (int i = 0; i < h; i++) {
	            offset1 = i * w;
	            offset2 = (h - i - 1) * w;
	            for (int j = 0; j < w; j++) {
	                int texturePixel = bitmapBuffer[offset1 + j];
	                int blue = (texturePixel >> 16) & 0xff;
	                int red = (texturePixel << 16) & 0x00ff0000;
	                int pixel = (texturePixel & 0xff00ff00) | red | blue;
	                bitmapSource[offset2 + j] = pixel;
	            }
	        }
	    } catch (GLException e) {
	        return null;
	    }
		
		return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
	}
	
}
