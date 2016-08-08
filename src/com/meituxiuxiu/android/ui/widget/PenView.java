package com.meituxiuxiu.android.ui.widget;

import java.util.ArrayList;
import java.util.List;

import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.PenActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public class PenView extends ImageView{

	private Paint paint;
    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Bitmap mbmp;
    private Path path;
    
    public List<Paint> paintList = new ArrayList<Paint>();
    public List<Path> pathList = new ArrayList<Path>();
	
	public PenView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
		
	}
	
	public PenView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public PenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
        paint = new Paint();
        /**打开抗锯齿效果*/
        paint.setAntiAlias(true);
        /**打开图像抖动效果，绘制出来的图片颜色更加平滑、饱和和清晰*/
        paint.setDither(true);
        
        // 设置光源方向
        float[] direction = new float[]{1, 1, 1};
        // 设置环境光亮度
        float light = 0.4f;
        // 设置反射等级
        float specular = 6;
        // 设置模糊度
        float blur = 3.5f;
        // 创建一个MaskFilter对象
        EmbossMaskFilter maskfilter = new EmbossMaskFilter(direction, light, specular, blur);
        /**设置MaskFilter，实现滤镜效果*/
        paint.setMaskFilter(maskfilter);
        
        paint.setPathEffect(new CornerPathEffect(10));
        
        paint.setStrokeWidth(7);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        
        
        // 关闭单个View的硬件加速功能，目的为了使得BlurMaskFilter起作用
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        
        
        path = new Path();
//        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hemanting);
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hemanting);
//        mBitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight());//Config.ARGB_8888
        mbmp = Bitmap.createBitmap(PenActivity.getBitmap().getWidth(), PenActivity.getBitmap().getHeight(), Config.ARGB_8888);
        mCanvas = new Canvas(mbmp);
//        mCanvas.drawColor(Color.WHITE);
        mBitmap = PenActivity.getBitmap();
        mCanvas.drawBitmap(mBitmap, 0, 0, paint);
    }
	
	
	public Bitmap getmBmp(){
		return mbmp;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawPath(path, paint);	
        
        if(paintList.size()>0 && paintList.get(paintList.size()-1) != null){
        	for(int i=0;i<paintList.size();i++){
        		canvas.drawPath(pathList.get(i), paintList.get(i));		
        	}
        }
	}
	
	private boolean isRunning = false;
	private float cur_x, cur_y;
	
	private int colorTemp = -1;
	
	 @Override
     public boolean onTouchEvent(MotionEvent event) {

     	if(!isRunning){
         float x = event.getX();
         float y = event.getY();

         switch (event.getAction()) {
         case MotionEvent.ACTION_DOWN: 
             cur_x = x;
             cur_y = y;
             path.moveTo(cur_x, cur_y);
             
             
             break;
         

         case MotionEvent.ACTION_MOVE: 
             path.quadTo(cur_x, cur_y, x, y);
//             path.lineTo(cur_x, cur_y);
             cur_x = x;
             cur_y = y;
             break;
         

         case MotionEvent.ACTION_UP: 
//        	 if(colorTemp != -1){
//        		 paint.setColor(colorTemp);
//        	 }
        	 
             mCanvas.drawPath(path, paint);
//             path.reset();
             
             PenActivity.undoPaintList.clear();
             PenActivity.undoPathList.clear();
             
             if(paint.getColor() !=Color.TRANSPARENT  && !path.isEmpty()){
//	     		Paint paint1 = new Paint();
//	     		paint1.setAntiAlias(true);
//	     		paint1.setStrokeWidth(3);
//	     		paint1.setStyle(Paint.Style.STROKE);
//	     		paint1.setColor(paint.getColor());
            	 Paint paint1 = paint;
	     		paintList.add(paint1);
	     		
	     		Path path1 = new Path();
	     		path1.addPath(path);
	     		pathList.add(path1);
             }
     		
     		
//             colorTemp = paint.getColor();
             
//     		paint.setColor(Color.TRANSPARENT);
     		path.reset();
             
             break;
         }

         invalidate();
     	}

         return true;
     }
	
//	 public void setPaintColor(int color){
////		Paint paint1 = new Paint();
////		paint1.setAntiAlias(true);
////		paint1.setStrokeWidth(3);
////		paint1.setStyle(Paint.Style.STROKE);
////		paint1.setColor(paint.getColor());
////		paintList.add(paint1);
////		
////		Path path1 = new Path();
////		path1.addPath(path);
////		pathList.add(path1);
//		 
////		 colorTemp = color;
//		 paint.setColor(color);
//		 path.reset();
//	 }
	 
	 public void setPaintObject(Paint paint){
		 this.paint = paint;
	 }
	 
	 public void reflush(){
		 invalidate();
	 }
	 
}
