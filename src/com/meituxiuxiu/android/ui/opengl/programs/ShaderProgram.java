package com.meituxiuxiu.android.ui.opengl.programs;

import static android.opengl.GLES20.*;

import com.meituxiuxiu.android.ui.opengl.utils.ShaderHelper;
import com.meituxiuxiu.android.ui.opengl.utils.TextResourceReader;

import android.content.Context;


public class ShaderProgram {
	
	
	

	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	// Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	// Shader program
	protected final int program;
	
	
	// Add Particles Constants
	protected static final String U_TIME = "u_Time";
	
	protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";
	protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";
	
	protected ShaderProgram(Context context, int vertexShaderResourceId,
	int fragmentShaderResourceId) {
	// Compile the shaders and link the program.
	program = ShaderHelper.buildProgram(
	TextResourceReader.readTextFileFromResource(
	context, vertexShaderResourceId),
	TextResourceReader.readTextFileFromResource(
	context, fragmentShaderResourceId));
	}
	public void useProgram() {
	// Set the current OpenGL shader program to this program.
	glUseProgram(program);
	}
	
	
	// add a new constant
	protected static final String U_COLOR = "u_Color";

}
