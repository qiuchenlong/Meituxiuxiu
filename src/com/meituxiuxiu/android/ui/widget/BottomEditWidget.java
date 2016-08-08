package com.meituxiuxiu.android.ui.widget;


import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.opengl.utils.FontUtil;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class BottomEditWidget extends PopupWindow {

	private TextView btn_submit;
	private ClearEditText edit_text;
	private View mMenuView;
	
	private String content="";
	
	public String getContent(){
		return content;
	}

	public BottomEditWidget(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.widget_bottom_edit, null);
		btn_submit = (TextView) mMenuView.findViewById(R.id.activity_frame_hide_change_word_btn);
		edit_text = (ClearEditText) mMenuView.findViewById(R.id.activity_frame_hide_change_word_edittext);
		
		edit_text.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				Log.e("TAG", "onTextChanged...");
				// content = arg0.toString();
				
				String[] content = new String[1];
				content[0] = arg0.toString();
				FontUtil.setContent(content);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
//		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		// 取消按钮
		// btn_cancel.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // 销毁弹出框
		// dismiss();
		// }
		// });
		// 设置按钮监听
		btn_submit.setOnClickListener(itemsOnClick);
//		btn_take_photo.setOnClickListener(itemsOnClick);
//		btn_cancel.setOnClickListener(itemsOnClick);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
//		this.setAnimationStyle(R.style.popupAnimation);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xbb000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() { // 不知道为什么这里一直监听不到

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.widget_bottom_edit_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();

					}
				}
				return true;
			}
		});

		mMenuView.setOnKeyListener(new OnKeyListener() { // 不知道为什么这里一直监听不到

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				System.out.println("arg1=" + arg1 + ",KeyEvent.ACTION_DOWN="
						+ KeyEvent.ACTION_DOWN);
				if (arg2.getAction() == KeyEvent.ACTION_DOWN
						&& arg1 == KeyEvent.KEYCODE_BACK) {
					System.out.println("按下了Back键");
					return true;
				}
				return false;
			}
		});

	}

}
