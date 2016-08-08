package com.meituxiuxiu.android.ui.opengl.utils;

import static android.opengl.GLES20.*;
import static android.opengl.GLUtils.*;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * 纹理helper
 * @author Administrator
 *
 */
public class TextureHelper {
	
	private static final String TAG = "TextureHelper";

	public static int loadTexture(Context context, int resourceId){
		int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);
		
		if(textureObjectIds[0] == 0){
			if(LoggerConfig.ON){
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
		
		if(bitmap == null){
			if(LoggerConfig.ON){
				Log.w(TAG, "Resource ID"+resourceId+" could not be decoded.");
			}
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}
		
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		
		bitmap.recycle();
		
		glGenerateMipmap(GL_TEXTURE_2D);
		
		glBindTexture(GL_TEXTURE_2D, 0);
		
		return textureObjectIds[0];
	}
	
}
