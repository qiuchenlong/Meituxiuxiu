package com.meituxiuxiu.android.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.util.Log;

public class ImageHelper {
	
	
	/**
    	截图（正中、正方形）
	   * @param bitmap      原图
	   * @param edgeLength  希望得到的正方形部分的边长
	   * @return  缩放截取正中部分后的位图。
	   */
	  public static Bitmap centerSquareScaleBitmap(Bitmap bitmap) // , int edgeLength  边长取原图的min(width, height)
	  {
		  int edgeLength;
	   if(null == bitmap ) //|| edgeLength <= 0
	   {
	    return  null;
	   }
	                                                                                 
	   Bitmap result = bitmap;
	   int widthOrg = bitmap.getWidth();
	   int heightOrg = bitmap.getHeight();
	   
	   edgeLength = (widthOrg>heightOrg)?heightOrg:widthOrg;
	                                                                                 
	   if(widthOrg >= edgeLength && heightOrg >= edgeLength)
	   {
	    //压缩到一个最小长度是edgeLength的bitmap
	    int longerEdge = (int)(edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
	    int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
	    int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
	    
	    scaledWidth = scaledWidth / 5 * 6;
	    
	    Bitmap scaledBitmap;
	                                                                                  
	          try{
	           scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
	          }
	          catch(Exception e){
	           return null;
	          }
	                                                                                       
	       //从图中截取正中间的正方形部分。
	       int xTopLeft = (scaledWidth - edgeLength) / 2;
	       int yTopLeft = (scaledHeight - edgeLength) / 2;
	                                                                                     
//	       try{
	        result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
//	        scaledBitmap.recycle();
//	       }
//	       catch(Exception e){
//	        return null;
//	       }       
	   }
	                                                                                      
	   return result;
	  }
	  
	  
	  
	  /**图片旋转*/
	  public static Bitmap adjustPhotoRotation(Bitmap bmp, int orientationDegree){
			Matrix m = new Matrix();
			m.setRotate(orientationDegree, (float) bmp.getWidth() / 2, (float) bmp.getHeight() / 2);
			float targetX, targetY;
			if (orientationDegree == 90) {
				targetX = bmp.getHeight();
				targetY = 0;
			} else {
				targetX = 0;
				targetY = bmp.getWidth();
			}
	
			final float[] values = new float[9];
			m.getValues(values);
	
			float x1 = values[Matrix.MTRANS_X];
			float y1 = values[Matrix.MTRANS_Y];
	
			m.postTranslate(targetX - x1, targetY - y1);
	
		    Bitmap bmpTemp = Bitmap.createBitmap(bmp.getHeight(), bmp.getWidth(), Bitmap.Config.ARGB_8888);
		    Paint paint = new Paint();
		    Canvas canvas = new Canvas(bmpTemp);
		    canvas.drawBitmap(bmp, m, paint);
	
		    return bmpTemp;
	  }
	  
	  
	  public static Bitmap convertImage(Bitmap bmp, String str){
		  int w = bmp.getWidth();
		  int h = bmp.getHeight();
		  Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		  Canvas cv = new Canvas(newb);
		  Matrix m = new Matrix();
		  if(TextUtils.equals("v", str)){
			  m.postScale(1, -1);   //镜像垂直翻转
		  }else{
			  m.postScale(-1, 1);   //镜像水平翻转
		  }
		  Bitmap new2 = Bitmap.createBitmap(bmp, 0, 0, w, h, m, true);
		  cv.drawBitmap(new2, new Rect(0, 0, new2.getWidth(), new2.getHeight()),new Rect(0, 0, w, h), null);
		  return newb;
	  }
	  
	  
	  
	  
	  /**LOMO 算法*/
	  
	  public static Bitmap HandleImageLomoFilter(Bitmap bm){
//		  Image image = new Image(bm);
//		  LomoFilter lomoFilter = new LomoFilter();
//		  image = lomoFilter.process(image);
//		  image.copyPixelsFromBuffer();
//		  Bitmap bmp = image.getImage();
//		  Log.e("TAG", "bmp = "+bmp);
//		  return bmp;
		  return null;
	  }
	  
	  
	  
	

	/**处理图像方法
     * 参数说明：1、原图的bitmap对象
     *           2、需要调整的色调值
     *           3、需要调整的饱和度
     *           4、需要调整的亮度
     *           5、需要调整的对比度*/
    public static Bitmap HandleImageEffect(Bitmap bm, float hue, float saturation, float luminance, float contrast){
        Bitmap bmp = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(), Bitmap.Config.ARGB_8888); // 新建一个bitmap对象，我们不能对传来的bitmap直接进行修改
        Canvas canvas = new Canvas(bmp); // 建立新bitmap的一个画板对象

        /**色调的调整*/
        ColorMatrix hueMatrix = new ColorMatrix();
        hueMatrix.setRotate(0, hue);
        hueMatrix.setRotate(1, hue);
        hueMatrix.setRotate(2, hue);

        /**饱和度调整*/
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        /**亮度调整*/
        ColorMatrix luminanceMatrix = new ColorMatrix();
        luminanceMatrix.setScale(luminance, luminance, luminance, 1); // 设置高亮，三个值都相同，实际上就是将RGB混合成白色，以此来提高亮度值

        /**对比度调整*/
        ColorMatrix contrastMatrix = new ColorMatrix();
        contrastMatrix.set(new float[]{
        		contrast, 0, 0, 0, luminance,
                0, contrast, 0, 0, luminance,
                0, 0, contrast, 0, luminance,
                0, 0, 0, contrast, 0
        });
        
        /**对以上颜色矩阵进行融合*/
        ColorMatrix imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(luminanceMatrix);
        imageMatrix.postConcat(contrastMatrix);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix)); // 设置画笔带有颜色矩阵
        canvas.drawBitmap(bm,0,0,paint); // 在原先的bitmap上，使用新的画笔绘制效果

        return bmp;
    }
    
    
    /**图像算法说明：
    *
    * ①、底片效果
    * rgb
    * r1 = 255 - r0
    * g1 = 255 - g0
    * b1 = 255 - b0
    *
    * */

   public static Bitmap HandleImageNegative(Bitmap bm){
       int width = bm.getWidth();
       int height = bm.getHeight();
       int color;
       int r,g,b,a;

       Bitmap bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

       int[] oldPx = new int[width*height];
       int[] newPx = new int[width*height];
       bm.getPixels(oldPx,0,width,0,0,width,height);

       for(int i=0;i<width*height;i++){
           color = oldPx[i];
           r = Color.red(color);
           g = Color.green(color);
           b = Color.blue(color);
           a = Color.alpha(color);

           r = 255-r;
           g = 255-g;
           b = 255-b;

           r=(r<0)?0:r;
           g=(g<0)?0:g;
           b=(b<0)?0:b;

           newPx[i] = Color.argb(a,r,g,b);

       }
       bmp.setPixels(newPx,0,width,0,0,width,height);

       return bmp;
   }
    
    
    /**图像算法说明：
    *
    * ②、怀旧效果
    * rgb
    * r1 = 0.393 * r + 0.769 * g + 0.189 * b;
    * g1 = 0.349 * r + 0.686 * g + 0.168 * b
    * b1 = 0.272 * r + 0.534 * g + 0.131 * b
    *
    * */
   public static Bitmap HandleImagePixelsOldPhoto(Bitmap bm){
       int width = bm.getWidth();
       int height = bm.getHeight();
       int color;
       int r,g,b,a;

       Bitmap bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

       int[] oldPx = new int[width*height];
       int[] newPx = new int[width*height];
       bm.getPixels(oldPx,0,width,0,0,width,height);

       for(int i=0;i<width*height;i++){
           color = oldPx[i];
           r = Color.red(color);
           g = Color.green(color);
           b = Color.blue(color);
           a = Color.alpha(color);

           r = (int)(0.393 * r + 0.769 * g + 0.189 * b);
           g = (int)(0.349 * r + 0.686 * g + 0.168 * b);
           b = (int)(0.272 * r + 0.534 * g + 0.131 * b);

           r=(r>255)?255:r;
           g=(g>255)?255:g;
           b=(b>255)?255:b;

           newPx[i] = Color.argb(a,r,g,b);

       }
       bmp.setPixels(newPx,0,width,0,0,width,height);

       return bmp;
   }
    
	
   /**图像算法说明：
   *
   * ③、浮雕效果
   * rgb(最终效果的RGB)  r0 g0 b0(前一个点的RGB)    r1 g1 b1(当前点的RGB)
   * r = (r0 - r1 + 127)
   * g = (g0 - g1 + 127)
   * b = (b0 - b1 + 127)
   *
   * */
  public static Bitmap HandleImagePixelsRelief(Bitmap bm){
      int width = bm.getWidth();
      int height = bm.getHeight();
      int color,colorBefore;
      int r,g,b,a;
      int r1,g1,b1,a1;

      Bitmap bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

      int[] oldPx = new int[width*height];
      int[] newPx = new int[width*height];
      bm.getPixels(oldPx,0,width,0,0,width,height);

      for(int i=1;i<width*height;i++){
          colorBefore = oldPx[i-1];
          r = Color.red(colorBefore);
          g = Color.green(colorBefore);
          b = Color.blue(colorBefore);
          a = Color.alpha(colorBefore);

          color = oldPx[i];
          r1 = Color.red(color);
          g1 = Color.green(color);
          b1 = Color.blue(color);
          a1 = Color.alpha(color);

          r = (r-r1+127);
          g = (g-g1+127);
          b = (b-b1+127);

          r=(r>255)?255:r;
          g=(g>255)?255:g;
          b=(b>255)?255:b;

          newPx[i] = Color.argb(a,r,g,b);
      }
      bmp.setPixels(newPx,0,width,0,0,width,height);

      return bmp;
  }


  /**图像算法说明：
   *
   * ④、锐化效果
   * rgb(最终效果的RGB)  r0 g0 b0(前一个点的RGB)    r g b(当前点的RGB)
   * r = r + 0.25 * |r - r0|
   * g = g + 0.25 * |g - g0|
   * b = b + 0.25 * |b - b0|
   *
   * */
  public static Bitmap HandleImagePixelsSharpen(Bitmap bm){
      int width = bm.getWidth();
      int height = bm.getHeight();
      int color,colorBefore;
      int r,g,b,a;
      int r0,g0,b0,a0;

      Bitmap bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);

      int[] oldPx = new int[width*height];
      int[] newPx = new int[width*height];
      bm.getPixels(oldPx,0,width,0,0,width,height);

      for(int i=1;i<width*height;i++){
          colorBefore = oldPx[i-1];
          r0 = Color.red(colorBefore);
          g0 = Color.green(colorBefore);
          b0 = Color.blue(colorBefore);
          a0 = Color.alpha(colorBefore);

          color = oldPx[i];
          r = Color.red(color);
          g = Color.green(color);
          b = Color.blue(color);
          a = Color.alpha(color);

          r = (int) (r + 0.25 * Math.abs(r - r0));
          g = (int) (g + 0.25 * Math.abs(g - g0));
          b = (int) (b + 0.25 * Math.abs(b - b0));

          r=(r>255)?255:r;
          g=(g>255)?255:g;
          b=(b>255)?255:b;

          newPx[i] = Color.argb(a,r,g,b);
      }
      bmp.setPixels(newPx,0,width,0,0,width,height);

      return bmp;
  }
   
  
  /**图像算法说明：
   * 
   * ⑤、黑白效果（不是采用传统的二值化处理，应该是采用了灰度效果处理）
   * 
   * 加权平均值法：即新的颜色值R＝G＝B＝(R ＊ Wr＋G＊Wg＋B＊Wb)
   * 							   一般由于人眼对不同颜色的敏感度不一样，所以三种颜色值的权重不一样，一般来说绿色最高，红色其次，蓝色最低，
   * 							  最合理的取值分别为Wr ＝ 30％，Wg ＝ 59％，Wb ＝ 11％
   * 
   * 参数二（value）：阈值
   * */
  public static Bitmap HandleImageBlackWhite(Bitmap bm, int vlaue){
	  int width = bm.getWidth();
      int height = bm.getHeight();
      int color;
      int r,g,b,a;
      int r1,g1,b1;
      
      float yuzhi = (float)vlaue/100;
      
      System.out.println("阈值："+yuzhi);
      
	  Bitmap bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
	  
	  int[] oldPx = new int[width*height];
      int[] newPx = new int[width*height];
      bm.getPixels(oldPx,0,width,0,0,width,height);

      for(int i=0;i<width*height;i++){
          color = oldPx[i];
          r = Color.red(color);
          g = Color.green(color);
          b = Color.blue(color);
          a = Color.alpha(color);
          
          /** r= g = b = (r*0.3 + g*0.59 +b*0.11) 增加一个控制变量的算法如下：*/

          r1 = (int) (r*0.3*(10/3.0-yuzhi*7/3.0) 			+ g*0.59*yuzhi 													+ b*0.11*yuzhi);
          g1 = (int) (r*0.3*yuzhi 										+ g*0.59*(100/59.0-yuzhi*41/59.0) 				+ b*0.11*yuzhi);
          b1 = (int) (r*0.3*yuzhi 										+ g*0.59*yuzhi 													+ b*0.11*(100/11.0 - yuzhi*89/11.0));
          
/*          r=(r>255)?255:r;
          g=(g>255)?255:g;
          b=(b>255)?255:b;*/

          newPx[i] = Color.argb(a,r1,g1,b1);

      }
      bmp.setPixels(newPx,0,width,0,0,width,height);
	  
	  return bmp;
  }
  
  
  /**图像算法说明：
   * 
   * ⑥、电影海报效果
   * 
   * */
  
  public static Bitmap HandleImageMoviePoster(Bitmap bm){
	  int width = bm.getWidth();
      int height = bm.getHeight();
      int color;
      int r,g,b,a;
      
	  Bitmap bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
	  
	  int[] oldPx = new int[width*height];
      int[] newPx = new int[width*height];
      bm.getPixels(oldPx,0,width,0,0,width,height);

      for(int i=0;i<width*height;i++){
          color = oldPx[i];
          r = Color.red(color);
          g = Color.green(color);
          b = Color.blue(color);
          a = Color.alpha(color);
          
          r = (r >= 128)? 192:64;
          g = (g >= 128)? 192:64;
          b = (b >= 128)? 192:64;
          
          newPx[i] = Color.argb(a,r,g,b);

      }
      bmp.setPixels(newPx,0,width,0,0,width,height);
	  
	  return bmp;
  }
  
  
  
  /**图像算法说明：
   * 
   * ⑦、朦胧柔化效果（高斯模糊）
   * 
   * 
   * RenderScript是Android在API 11之后加入的，用于高效的图片处理，包括模糊、混合、矩阵卷积计算等
   * 
   * 
   * */
  
  public static Bitmap HandleImageGaussianBlur(Context context, Bitmap bm, float value){
      
	  Bitmap bmp = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
	  
	  RenderScript rs = RenderScript.create(context);
	  
	  ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
	  
	  Allocation allIn = Allocation.createFromBitmap(rs, bm);  
      Allocation allOut = Allocation.createFromBitmap(rs, bmp);  
      
      if(value == 0)
    	  value = 1;
    	  
      blurScript.setRadius(value);  //25.f
      
      blurScript.setInput(allIn);  
      blurScript.forEach(allOut);
      
      allOut.copyTo(bmp); 
      
//      bm.recycle();
      
      rs.destroy();
      
	  return bmp;
  }
  
  
}
