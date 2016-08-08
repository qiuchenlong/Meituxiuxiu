package com.meituxiuxiu.android.ui.opengl.utils;

import static android.opengl.GLES20.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.util.Log;

/**
 * 着色器工具类
 * 
 * @author qiuchenlong on 2016.04.01
 *
 */
public class ShaderUtil {
	// 加载着色器
		public static int loadShader(int shaderType, String source){
			// 创建一个shader，并记录其id
			int shader = glCreateShader(shaderType);
			
			if(shader != 0){
				// 加载着色器源代码
				glShaderSource(shader, source);
				// 编译
				glCompileShader(shader);
				int[] compiled = new int[1];
				// 获取shader的编译情况
				glGetShaderiv(shader, GL_COMPILE_STATUS, compiled, 0);
				// 编译失败
				if(compiled[0] == 0){
					Log.w("ES20_ERROR", "Could not compile shader "+shaderType+":");
					Log.w("ES20_ERROR", glGetShaderInfoLog(shader));
					glDeleteShader(shader);
					shader = 0;
				}
			}
			return shader;
		};
		// 创建着色器程序
		public static int createProgram(String vertexSource, String fragmentSource){
			
			int vertexShader = loadShader(GL_VERTEX_SHADER, vertexSource);
			if(vertexShader == 0)
				return 0;
			
			int fragmentShader = loadShader(GL_FRAGMENT_SHADER, fragmentSource);
			if(fragmentShader ==0)
				return 0;
			
			// 创建一个program，并记录其id
			int program = glCreateProgram();
			if(program != 0){
				// 向程序中加入顶点着色器
				glAttachShader(program, vertexShader);
				checkGlError("glAttachShader");
				// 向程序中加入碎片着色器
				glAttachShader(program, fragmentShader);
				checkGlError("glAttachShader");
				// 链接程序（Link）
				glLinkProgram(program);
				
				int[] linkStatus = new int[1];
				glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0);
				
				if(linkStatus[0] == 0){
					Log.w("ES20_ERROR", "Could not link program:");
					Log.w("ES20.ERROR", glGetProgramInfoLog(program));
					glDeleteProgram(program);
					program = 0;
				}
				
			}
			
			return program;
		};
		
		public static void checkGlError(String op){
			int error;
			while((error = glGetError()) != GL_NO_ERROR){
				Log.e("ES20.ERROR", op + ": glError " + error);
				throw new RuntimeException(op+": glError " + error);
			}
		}
		
		public static String loadFromAssetsFile(String fname, Resources r){
			String result = null;
			try {
				InputStream is = r.getAssets().open(fname);
				int ch = 0;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while((ch = is.read()) != -1){
					baos.write(ch);
				}
				byte[] buff = baos.toByteArray();
				baos.close();
				is.close();
				
				result = new String(buff, "UTF-8");
				result = result.replace("\\r\\n", "\n");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			return result;
		}
}
