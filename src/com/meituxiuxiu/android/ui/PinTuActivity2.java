package com.meituxiuxiu.android.ui;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.fragment.OneFragment;
import com.meituxiuxiu.android.ui.fragment.TuPianFreedomFragment;
import com.meituxiuxiu.android.ui.fragment.TuPianPosterFragment;
import com.meituxiuxiu.android.ui.fragment.TuPianSpliceFragment;
import com.meituxiuxiu.android.ui.fragment.TuPianTemplateFragment;
import com.meituxiuxiu.android.ui.fragment.TwoFragment;

public class PinTuActivity2 extends FragmentActivity implements OnClickListener{

	private static final String TAG = PinTuActivity2.class.getName();
	

	
	// 当前Fragment的对象
	private static Fragment Contentfrom = null;
	
	
	
	private Fragment f1 = new TuPianTemplateFragment(); // 模板
	private Fragment f2 = new TuPianFreedomFragment(); // 自由
	private Fragment f3 = new TuPianPosterFragment(); // 海报
	private Fragment f4 = new TuPianSpliceFragment(); // 拼接
	
	
	
	
	private TextView templateText, freedomText, posterText, spliceText;
	



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pintu);
		
		
		
//		addContentView(sl, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		
//		sl.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				getBitmapByView(sl);
//			}
//		});
		
		
		/**set title bar*/
		setTitleBar(R.drawable.icon_back_selector, "返回", "图片拼接", "保存与分享", R.drawable.icon_next_selector);
		
	
		
		findViewById(R.id.activity_pintu_template_btn_image_layout).setOnClickListener(this);
		findViewById(R.id.activity_pintu_freedom_btn_image_layout).setOnClickListener(this);
		findViewById(R.id.activity_pintu_poster_btn_image_layout).setOnClickListener(this);
		findViewById(R.id.activity_pintu_splice_btn_image_layout).setOnClickListener(this);
		
		
		
		templateText = (TextView) findViewById(R.id.activity_pintu_template_btn_text);
		freedomText = (TextView) findViewById(R.id.activity_pintu_freedom_btn_text);
		posterText = (TextView) findViewById(R.id.activity_pintu_poster_btn_text);
		spliceText = (TextView) findViewById(R.id.activity_pintu_splice_btn_text);
		
		
		getSupportFragmentManager().beginTransaction().add(R.id.activity_pintu_fragment_contain, f1).commit();
		Contentfrom = f1;
		
		
		
		
		
		
		
		
	}
	
	
	
	/**
	   * 切换页面的重载，优化了fragment的切换
	   * 
	   * @param f
	   * @param descString
	   */
	  public void switchFragment(Fragment to) {
		  
		  
	    if (Contentfrom == null || to == null || Contentfrom == to)
	      return;
	    FragmentTransaction transaction = getSupportFragmentManager()
	        .beginTransaction();//.setCustomAnimations(R.anim.tran_pre_in,R.anim.tran_pre_out);
	    
	    if (!to.isAdded()) {
	    	// 如果要加的fragment没有被加载过
	        // 隐藏当前的fragment，add下一个到Activity中
	        transaction.hide(Contentfrom).add(R.id.activity_pintu_fragment_contain, to).commit();
	      } else {
	        // 隐藏当前的fragment，显示下一个
	        transaction.hide(Contentfrom).show(to).commit();
	      }
	    
	    Contentfrom = to;
	    
	  }
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	
	/**
     * 截取scrollview的屏幕
     * **/
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取listView实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
//            scrollView.getChildAt(i).setBackgroundResource(R.drawable.ic_launcher); // 可用于数字水印
            scrollView.getChildAt(i).setBackgroundColor(Color.WHITE);
        }
        Log.d(TAG, "实际高度:" + h);
        Log.d(TAG, " 高度:" + scrollView.getHeight());
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        // 测试输出
        FileOutputStream out = null;
        try {
            out = new FileOutputStream("/sdcard/screen_test.png");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (null != out) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            // TODO: handle exception
        }
        return bitmap;
    }
    
    
    
    
    private void setTitleBar(int leftResId, String leftStr, String titleStr, String rightStr, int rightResId){
		ImageView leftIcon = (ImageView) findViewById(R.id.widget_common_top_title_layout_left_icon);
		TextView leftWord = (TextView) findViewById(R.id.widget_common_top_title_layout_left_word);
		TextView title = (TextView) findViewById(R.id.widget_common_top_title_layout_title);
		TextView rightWord = (TextView) findViewById(R.id.widget_common_top_title_layout_right_word);
		ImageView rightIcon = (ImageView) findViewById(R.id.widget_common_top_title_layout_right_icon);
		
		LinearLayout leftLayout = (LinearLayout) findViewById(R.id.widget_common_top_title_layout_left_layout);
		LinearLayout rightLayout = (LinearLayout) findViewById(R.id.widget_common_top_title_layout_right_layout);
		
		/**判断是否要显示控件*/
		if(leftResId == 0){
			leftIcon.setVisibility(View.GONE);
		}else{
			leftIcon.setImageResource(leftResId);
		}
		
		if("".equals(leftStr) || leftStr == null){
			leftWord.setVisibility(View.GONE);
		}else{
			leftWord.setText(leftStr);
		}
		
		if("".equals(titleStr) || titleStr == null){
			title.setVisibility(View.GONE);
		}else{
			title.setText(titleStr);
		}
		
		if("".equals(rightStr) || rightStr == null){
			rightWord.setVisibility(View.GONE);
		}else{
			rightWord.setText(rightStr);
		}
		
		if(rightResId == 0){
			rightIcon.setVisibility(View.GONE);
		}else{
			rightIcon.setImageResource(rightResId);
		}
		
		
		if(leftIcon.getVisibility() == View.VISIBLE || leftWord.getVisibility() == View.VISIBLE){
			leftLayout.setOnClickListener(this);
		}
		if(rightIcon.getVisibility() == View.VISIBLE || rightWord.getVisibility() == View.VISIBLE){
			rightLayout.setOnClickListener(this);
		}
		
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.widget_common_top_title_layout_left_layout:
			finish();
			break;



			
		case R.id.activity_pintu_template_btn_image_layout:
			switchFragment(f1);
			
			
			
			
			templateText.setTextColor(Color.rgb(17, 129, 243));
			freedomText.setTextColor(Color.rgb(168, 168, 168));
			posterText.setTextColor(Color.rgb(168, 168, 168));
			spliceText.setTextColor(Color.rgb(168, 168, 168));
			
			
			((ImageView)findViewById(R.id.activity_pintu_template_btn_image)).setImageResource(R.drawable.icon_template_puzzle_b);
			((ImageView)findViewById(R.id.activity_pintu_freedom_btn_image)).setImageResource(R.drawable.icon_freedom_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_poster_btn_image)).setImageResource(R.drawable.icon_poster_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_splice_btn_image)).setImageResource(R.drawable.icon_splice_puzzle_a);
			
			
			break;
		case R.id.activity_pintu_freedom_btn_image_layout:
			switchFragment(f2);
			
			
			
			templateText.setTextColor(Color.rgb(168, 168, 168));
			freedomText.setTextColor(Color.rgb(17, 129, 243));
			posterText.setTextColor(Color.rgb(168, 168, 168));
			spliceText.setTextColor(Color.rgb(168, 168, 168));
			
			
			((ImageView)findViewById(R.id.activity_pintu_template_btn_image)).setImageResource(R.drawable.icon_template_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_freedom_btn_image)).setImageResource(R.drawable.icon_freedom_puzzle_b);
			((ImageView)findViewById(R.id.activity_pintu_poster_btn_image)).setImageResource(R.drawable.icon_poster_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_splice_btn_image)).setImageResource(R.drawable.icon_splice_puzzle_a);
			
			break;
		case R.id.activity_pintu_poster_btn_image_layout:
			switchFragment(f3);
			
			
			
			templateText.setTextColor(Color.rgb(168, 168, 168));
			freedomText.setTextColor(Color.rgb(168, 168, 168));
			posterText.setTextColor(Color.rgb(17, 129, 243));
			spliceText.setTextColor(Color.rgb(168, 168, 168));
			
			
			((ImageView)findViewById(R.id.activity_pintu_template_btn_image)).setImageResource(R.drawable.icon_template_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_freedom_btn_image)).setImageResource(R.drawable.icon_freedom_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_poster_btn_image)).setImageResource(R.drawable.icon_poster_puzzle_b);
			((ImageView)findViewById(R.id.activity_pintu_splice_btn_image)).setImageResource(R.drawable.icon_splice_puzzle_a);
			
			break;
		case R.id.activity_pintu_splice_btn_image_layout:
			switchFragment(f4);
			
			
			
			
			templateText.setTextColor(Color.rgb(168, 168, 168));
			freedomText.setTextColor(Color.rgb(168, 168, 168));
			posterText.setTextColor(Color.rgb(168, 168, 168));
			spliceText.setTextColor(Color.rgb(17, 129, 243));
			
			
			((ImageView)findViewById(R.id.activity_pintu_template_btn_image)).setImageResource(R.drawable.icon_template_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_freedom_btn_image)).setImageResource(R.drawable.icon_freedom_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_poster_btn_image)).setImageResource(R.drawable.icon_poster_puzzle_a);
			((ImageView)findViewById(R.id.activity_pintu_splice_btn_image)).setImageResource(R.drawable.icon_splice_puzzle_b);
			
			break;
			
			
			
		}
	}
    
	
}
