package com.meituxiuxiu.android.ui.widget;




import com.meituxiuxiu.android.R;
import com.meituxiuxiu.android.ui.fragment.TuPianFreedomFragment;
import com.meituxiuxiu.android.ui.fragment.TuPianFreedomFragment.DragListAdapter;
import com.meituxiuxiu.android.utils.VibratorUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class DragView extends ListView implements TuPianFreedomFragment.TouchListener{
	private ImageView imageView;	//被拖动的图片
	private int scaledTouchSlop;	//判断拖动的距离
	
	private int dragSrcPosition;	//手指在touch事件触摸时候的原始位置 
	private int dragPosition;		//手指拖动列表项时候的位置
	
	private int dragPoint;		//手指点击位置在当前数据项item中的位置,只有Y坐标
	private int dragOffset;		//当前视图listview在屏幕中的位置，只有Y坐标
	
	private int upScrollBounce;	//向上滑动的边界
	private int downScrollBounce;	//拖动的时候向下滑动的边界
	
	private WindowManager windowManager = null;	//窗口管理类
	//窗口参数类
	private WindowManager.LayoutParams layoutParams = null;
	
	
	
	
	public DragView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//触发移动事件的最小距离
		scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		
		
		TuPianFreedomFragment.setTouchListener(this);
	}


	
	
	//判断是否长按的变量
	private boolean mHasPerformedLongPress; 
	private CheckForLongPress mPendingCheckForLongPress;
	
	//监测长按事件
	class CheckForLongPress implements Runnable {
	        private int mOriginalWindowAttachCount;
	        public void run() {
	            if (/*(mParent != null) &&*/ hasWindowFocus()
	                    && mOriginalWindowAttachCount == getWindowAttachCount()
	                    && !mHasPerformedLongPress) {
	                if (performLongClick() || isActionDown) {
	                    mHasPerformedLongPress = true;
	                    
	                    System.out.println("yesyes...........");
	                    
	                    
	                    
	                    startDrag(bm, y);
	                    
	                }
	                System.out.println("nononononono1...........");
	            }
	            System.out.println("nononononono2...........");
	        }
	        public void rememberWindowAttachCount() {
	            mOriginalWindowAttachCount = getWindowAttachCount();
	        }
	    }
	    private void postCheckForLongClick() {
	        mHasPerformedLongPress = false;
	        if (mPendingCheckForLongPress == null) {
	            mPendingCheckForLongPress = new CheckForLongPress();
	        }
	        mPendingCheckForLongPress.rememberWindowAttachCount();
	        postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout()+500);
	    }
	
	    
	
	    private long lastDownTime, lastUpTime, lastUpUpTime;
	    
	    
	    public static boolean isActionDown = false;
	    
	    
	    
	    private Bitmap bm;
	    private int y;
	    
	    
	//重写于absListView
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		
		//长按的处理，消耗任何触摸事件
        if (mHasPerformedLongPress) {
            mHasPerformedLongPress = false;
          return true;
         }
        
        
        Log.e("TAG", "ev.getAction()="+ev.getAction());
        
          switch (ev.getAction()) {
          case MotionEvent.ACTION_DOWN: {
        	  Log.w("TAG", "postCheckForLongClick...");
              postCheckForLongClick();
              
              
            //获取的该touch事件的x坐标和y坐标，该坐标是相对于组件的左上角的位置
  			int x = (int) ev.getX();
  			y = (int) ev.getY();
  			//赋值手指点击时候的开始坐标
  			dragSrcPosition = dragPosition = this.pointToPosition(x, y);
  			//如果点击在列表之外，也就是不允许的位置
  			if(dragPosition == AdapterView.INVALID_POSITION){
  				//直接执行父类，不做任何操作
  				return super.onInterceptTouchEvent(ev);
  			}
  			
  			/***
  			 * 锁定手指touch的列表item,
  			 * 参数为屏幕的touch坐标减去listview左上角的坐标
  			 * 这里的getChildAt方法参数为相对于组件左上角坐标为00的情况
  			 * 故有下面的这种参数算法
  			 */
  			ViewGroup itemView = (ViewGroup) this.getChildAt(dragPosition-this.getFirstVisiblePosition());
  			/****
  			 * 说明：getX Y为touch点相对于组件左上角的距离
  			 * getRawX 、Y 为touch点相对于屏幕左上角的距离
  			 * 参考http://blog.csdn.net/love_world_/article/details/8164293
  			 */
  			//touch点的view相对于该childitem的top坐标的距离
  			dragPoint = y-itemView.getTop();
  			//为距离屏幕左上角的Y减去距离组件左上角的Y，其实就是
  			//组件上方的view+标题栏+状态栏的Y
  			dragOffset = (int) (ev.getRawY()-y);
  			
  			//拿到拖动的imageview对象
  			View drager = itemView.findViewById(R.id.imageView1);
  			
  			//判断条件为拖动touch图片是否为null和touch的位置，是否符合
  			if(drager != null && x>drager.getLeft()-20){
  				
  				//判断得出向上滑动和向下滑动的值
  				upScrollBounce = Math.min(y-scaledTouchSlop, getHeight()/3);
  				downScrollBounce = Math.max(y+scaledTouchSlop, getHeight()*2/3);
  				//启用绘图缓存
  				itemView.setDrawingCacheEnabled(true);
  				//根据图像缓存拿到对应位图
  				bm = Bitmap.createBitmap(itemView.getDrawingCache());
  				
  			}
              
              
              break;
          }
          case MotionEvent.ACTION_UP:
          case MotionEvent.ACTION_CANCEL:
        	  Log.w("TAG", "ACTION_UP...");
              mHasPerformedLongPress = false;
              if (mPendingCheckForLongPress != null) {
            	  Log.w("TAG", "removeCallbacks...");
                  removeCallbacks(mPendingCheckForLongPress);
              }
              break;
      }
      // Otherwise continue letting touch events fall through to children
      return super.onInterceptTouchEvent(ev);
      
      
      
		
//		Log.e("TAG", "ev.getAction()="+ev.getAction());
//		
//		if(ev.getAction() == MotionEvent.ACTION_DOWN){
//			
//			Log.e("TAG", "ACTION_DOWN....");
//			
//			
//			lastDownTime = System.currentTimeMillis(); // 获取按下的时间
//			
//			
//			//获取的该touch事件的x坐标和y坐标，该坐标是相对于组件的左上角的位置
//			int x = (int) ev.getX();
//			final int y = (int) ev.getY();
//			//赋值手指点击时候的开始坐标
//			dragSrcPosition = dragPosition = this.pointToPosition(x, y);
//			//如果点击在列表之外，也就是不允许的位置
//			if(dragPosition == AdapterView.INVALID_POSITION){
//				//直接执行父类，不做任何操作
//				return super.onInterceptTouchEvent(ev);
//			}
//			
//			/***
//			 * 锁定手指touch的列表item,
//			 * 参数为屏幕的touch坐标减去listview左上角的坐标
//			 * 这里的getChildAt方法参数为相对于组件左上角坐标为00的情况
//			 * 故有下面的这种参数算法
//			 */
//			ViewGroup itemView = (ViewGroup) this.getChildAt(dragPosition-this.getFirstVisiblePosition());
//			/****
//			 * 说明：getX Y为touch点相对于组件左上角的距离
//			 * getRawX 、Y 为touch点相对于屏幕左上角的距离
//			 * 参考http://blog.csdn.net/love_world_/article/details/8164293
//			 */
//			//touch点的view相对于该childitem的top坐标的距离
//			dragPoint = y-itemView.getTop();
//			//为距离屏幕左上角的Y减去距离组件左上角的Y，其实就是
//			//组件上方的view+标题栏+状态栏的Y
//			dragOffset = (int) (ev.getRawY()-y);
//			
//			//拿到拖动的imageview对象
//			View drager = itemView.findViewById(R.id.imageView1);
//			
//			//判断条件为拖动touch图片是否为null和touch的位置，是否符合
//			if(drager != null && x>drager.getLeft()-20){
//				
//				//判断得出向上滑动和向下滑动的值
//				upScrollBounce = Math.min(y-scaledTouchSlop, getHeight()/3);
//				downScrollBounce = Math.max(y+scaledTouchSlop, getHeight()*2/3);
//				//启用绘图缓存
//				itemView.setDrawingCacheEnabled(true);
//				//根据图像缓存拿到对应位图
//				final Bitmap bm = Bitmap.createBitmap(itemView.getDrawingCache());
//				
//				
////				// 相当于三次点击效果
////				if ((System.currentTimeMillis() - lastUpUpTime) < 500){ 
////					Log.e("DragView", "Third Click...");
////				} else if ((System.currentTimeMillis() - lastUpTime) < 2000){
////					Log.e("DragView", "Second Click...");
////	  				lastUpUpTime = System.currentTimeMillis();
//
//
//	  				startDrag(bm, y);
//	  				
//	  				
////	  			} else if ((System.currentTimeMillis() - lastDownTime) < 2000){
////	  				lastUpTime = System.currentTimeMillis();
////				}
//				
//				
//				
//				
//			}
//			return false;
//		}
//		
//		return super.onInterceptTouchEvent(ev);
	}

	
	
	//重写OnTouchEvent,触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(imageView != null && dragPosition != INVALID_POSITION){
			int currentAction = ev.getAction();
			
			switch (currentAction) {
			case MotionEvent.ACTION_UP:
				Log.e("TAG", "ACTION_UP2222....");
				int upY = (int) ev.getY();
				//还有一些操作
				stopDrag();
                onDrop(upY);
				break;
			case MotionEvent.ACTION_MOVE:	
				Log.e("TAG", "ACTION_MOVE2222....");
					

				int moveY = (int) ev.getY();
				onDrag(moveY);
				break;
			default:
				break;
			}
			return true;
		}
		//决定了选中的效果
		return super.onTouchEvent(ev);
	}
	
	
	
	/****
	 * 准备拖动，初始化拖动时的影像，和一些window参数
	 * @param bm	拖动缓存位图
	 * @param y		拖动之前touch的位置
	 */
	public void startDrag(Bitmap bm,int y){
		stopDrag();
		layoutParams = new WindowManager.LayoutParams();
		//设置重力
		layoutParams.gravity = Gravity.TOP;
		//横轴坐标不变
		layoutParams.x = 0;
		/**
		 * 
		 * y轴坐标为   视图相对于自身左上角的Y-touch点在列表项中的y
		 * +视图相对于屏幕左上角的Y，=
		 * 该view相对于屏幕左上角的位置
		 */
		layoutParams.y = y-dragPoint+dragOffset;
		/****
		 * 宽度和高度都为wrapContent
		 */
		layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		
		/****
		 * 设置该layout参数的一些flags参数
		 */
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		//设置该window项是半透明的格式
		layoutParams.format = PixelFormat.TRANSLUCENT;
		//设置没有动画
		layoutParams.windowAnimations = 0;
		
		//配置一个影像ImageView
		ImageView imageViewForDragAni = new ImageView(getContext());
		imageViewForDragAni.setImageBitmap(bm);
		//配置该windowManager
		windowManager = (WindowManager) this.getContext().getSystemService("window");
		windowManager.addView(imageViewForDragAni, layoutParams);
		imageView = imageViewForDragAni;
	}
	
	/***
	 * 停止拖动，去掉拖动时候的影像
	 */
	public void stopDrag(){
		if(imageView != null){
			windowManager.removeView(imageView);
			imageView = null;
		}
	}
	
	
	/****
	 * 拖动方法
	 * @param y
	 */
	public void onDrag(int y){
		
		if(imageView != null){
			//透明度
			layoutParams.alpha = 0.8f;
			layoutParams.y = y-this.dragPoint+this.dragOffset;
			windowManager.updateViewLayout(imageView, layoutParams);
		}
		
		
		//避免拖动到分割线返回-1
		int tempPosition = this.pointToPosition(0, y);
		if(tempPosition != this.INVALID_POSITION){
			this.dragPosition = tempPosition;
		}
		
		 //滚动,暂时不理解
        int scrollHeight = 0;
        if(y<upScrollBounce){
            scrollHeight = 8;//定义向上滚动8个像素，如果可以向上滚动的话
        }else if(y>downScrollBounce){
            scrollHeight = -8;//定义向下滚动8个像素，，如果可以向上滚动的话
        }
		
        if(scrollHeight!=0){
            //真正滚动的方法setSelectionFromTop()
            setSelectionFromTop(dragPosition, getChildAt(dragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
        }
	}
	
	
	/***
	 * 拖动放下的时候
	 * param : y
	 */
	public void onDrop(int y){
		int tempPosition = this.pointToPosition(0, y);
		if(tempPosition != this.INVALID_POSITION){
			this.dragPosition = tempPosition;
		}
		
        //超出边界处理
        if(y<getChildAt(1).getTop()){
            //超出上边界
            dragPosition = 1;
        }else if(y>getChildAt(getChildCount()-1).getBottom()){
            //超出下边界
            dragPosition = getAdapter().getCount()-1;
            //
        }
        
        
        
       //数据交换
        if(dragPosition>0&&dragPosition<getAdapter().getCount()){
            @SuppressWarnings("unchecked")
            DragListAdapter adapter = (DragListAdapter)getAdapter();
            //原始位置的item
            int dragItem = adapter.getItem(dragSrcPosition);
            adapter.remove(dragItem);
            adapter.insert(dragItem, dragPosition);
//            Toast.makeText(getContext(), adapter.getList().toString(), Toast.LENGTH_SHORT).show();
     
        }
	}



	@Override
	public void remove() {
		// TODO Auto-generated method stub
		Log.e("TAG", "remove...");
		mHasPerformedLongPress = false;
        if (mPendingCheckForLongPress != null) {
      	  Log.w("TAG", "removeCallbacks...");
            removeCallbacks(mPendingCheckForLongPress);
        }
	}
}
