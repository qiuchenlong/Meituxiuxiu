package com.meituxiuxiu.android.ui.advance.common;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.OpenGLUtils;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageFilter;

import android.content.Context;
import android.opengl.GLES20;

public class MagicHefeFilter extends GPUImageFilter{
	private int[] inputTextureHandles = {-1,-1,-1,-1};
	private int[] inputTextureUniformLocations = {-1,-1,-1,-1};
	private Context mContext;
	public MagicHefeFilter(Context context){
		super(NO_FILTER_VERTEX_SHADER,OpenGLUtils.readShaderFromRawResource(context, R.raw.hefe));
		mContext = context;
	}
	
	protected void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
        	inputTextureHandles[i] = -1;
    }
	
	protected void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGLUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	protected void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGLUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	protected void onInit(){
		super.onInit();
		for(int i=0; i < inputTextureUniformLocations.length; i++){
			inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
		}
	}
	
	protected void onInitialized(){
		super.onInitialized();
	    runOnDraw(new Runnable(){
		    public void run(){
		    	inputTextureHandles[0] = OpenGLUtils.loadTexture(mContext, "filter/edgeburn.png");
				inputTextureHandles[1] = OpenGLUtils.loadTexture(mContext, "filter/hefemap.png");
				inputTextureHandles[2] = OpenGLUtils.loadTexture(mContext, "filter/hefemetal.png");
				inputTextureHandles[3] = OpenGLUtils.loadTexture(mContext, "filter/hefesoftlight.png");
		    }
	    });
	}
}
