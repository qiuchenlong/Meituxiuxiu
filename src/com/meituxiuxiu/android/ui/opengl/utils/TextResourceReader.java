package com.meituxiuxiu.android.ui.opengl.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.Resources;

public class TextResourceReader {
	
	public static String readTextFileFromResource(Context context, int resourceId){
		
		StringBuilder body = new StringBuilder();
		
		try {
			InputStream inputStream = context.getResources().openRawResource(resourceId);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String nextLine;
			while((nextLine = bufferedReader.readLine()) != null){
				body.append(nextLine);
				body.append("\n");
			}
		} catch (IOException e) {
			// TODO: handle exception
			throw new RuntimeException(
					"Could not open resoure:" + resourceId, e);
		} catch (Resources.NotFoundException nfe) {
			// TODO: handle exception
			throw new RuntimeException(
					"Could not found:" + resourceId, nfe);
		}
		return body.toString();
	}

}
