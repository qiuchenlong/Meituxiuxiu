package com.meituxiuxiu.android.ui.widget;

import com.meituxiuxiu.android.R;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class PopWindowTip extends PopupWindow {
	
	private View view;
	
	public PopWindowTip(Activity context, View.OnClickListener paramOnClickListener, int width, int height){
		super(context);
		
		view = LayoutInflater.from(context).inflate(R.layout.popwindowtip, null);
		
		
		LinearLayout ll1 = (LinearLayout) view.findViewById(R.id.widget_popwindowtip_flash_off);
		LinearLayout ll2 = (LinearLayout) view.findViewById(R.id.widget_popwindowtip_flash_on);
		
		if(paramOnClickListener != null){
			ll1.setOnClickListener(paramOnClickListener);
			ll2.setOnClickListener(paramOnClickListener);
		}
		
		setContentView(view);  
        //设置宽度  
        setWidth(width);  
        //设置高度  
        setHeight(height);  
        //设置显示隐藏动画  
        setAnimationStyle(R.style.AnimTools);  
        //设置背景透明  
        setBackgroundDrawable(new ColorDrawable(0));
	}
	

}
