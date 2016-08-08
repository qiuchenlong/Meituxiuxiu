package com.meituxiuxiu.android.ui;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.surfaceview.renderer.TextureRenderer;

public class EffectsFilterActivity extends Activity {
	private GLSurfaceView mEffectView;

    private TextureRenderer renderer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        renderer = new TextureRenderer();
        renderer.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hemanting));
        renderer.setCurrentEffect(R.id.none);
        
        mEffectView = (GLSurfaceView) findViewById(R.id.effectsview);
        //mEffectView = new GLSurfaceView(this);
        mEffectView.setEGLContextClientVersion(2);
        //mEffectView.setRenderer(this);
        mEffectView.setRenderer(renderer);
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        //setContentView(mEffectView);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("info", "menu create");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        renderer.setCurrentEffect(item.getItemId());
        mEffectView.requestRender();
        return true;
    }
}
