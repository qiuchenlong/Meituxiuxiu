package com.meituxiuxiu.android.ui.factory;

import android.content.Context;

import com.meituxiuxiu.android.ui.MagicFilterType;
import com.meituxiuxiu.android.ui.advance.common.MagicAmaroFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicAntiqueFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicBeautyFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicBlackCatFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicBrannanFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicBrooklynFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicCalmFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicCoolFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicCrayonFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicEarlyBirdFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicEmeraldFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicEvergreenFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicFairytaleFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicFreudFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicHealthyFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicHefeFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicHudsonFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicInkwellFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicKevinFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicLatteFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicLomoFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicN1977Filter;
import com.meituxiuxiu.android.ui.advance.common.MagicNashvilleFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicNostalgiaFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicPixarFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicRiseFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicRomanceFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSakuraFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSierraFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSketchFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSkinWhitenFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSuMiaoFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSunriseFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSunsetFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSutroFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicSweetsFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicTenderFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicToasterFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicValenciaFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicWaldenFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicWarmFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicWhiteCatFilter;
import com.meituxiuxiu.android.ui.advance.common.MagicXproIIFilter;
import com.meituxiuxiu.android.ui.advance.image.MagicImageAdjustFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageBrightnessFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageContrastFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageExposureFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageHueFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageSaturationFilter;
import com.meituxiuxiu.android.ui.gpuimage.GPUImageSharpenFilter;


public class MagicFilterFactory{
	
	private static int mFilterType = MagicFilterType.NONE;	
	
	public static GPUImageFilter getFilters(int type, Context mContext){
		mFilterType = type;
		switch (type) {
		case MagicFilterType.WHITECAT:
			return new MagicWhiteCatFilter(mContext);
		case MagicFilterType.BLACKCAT:
			return new MagicBlackCatFilter(mContext);
		case MagicFilterType.BEAUTY:
			return new MagicBeautyFilter(mContext);
		case MagicFilterType.SKINWHITEN:
			//List<GPUImageFilter> filters = new ArrayList<GPUImageFilter>();	
			//filters.add(new MagicBilateralFilter(mContext));
			//filters.add(new MagicSkinWhitenFilter(mContext));
			//return new MagicBaseGroupFilter(filters);
			return new MagicSkinWhitenFilter(mContext);
		case MagicFilterType.ROMANCE:
			return new MagicRomanceFilter(mContext);
		case MagicFilterType.SAKURA:
			return new MagicSakuraFilter(mContext);
		case MagicFilterType.AMARO:
			return new MagicAmaroFilter(mContext);
		case MagicFilterType.WALDEN:
			return new MagicWaldenFilter(mContext);
		case MagicFilterType.ANTIQUE:
			return new MagicAntiqueFilter(mContext);
		case MagicFilterType.CALM:
			return new MagicCalmFilter(mContext);
		case MagicFilterType.BRANNAN:
			return new MagicBrannanFilter(mContext);
		case MagicFilterType.BROOKLYN:
			return new MagicBrooklynFilter(mContext);
		case MagicFilterType.EARLYBIRD:
			return new MagicEarlyBirdFilter(mContext);
		case MagicFilterType.FREUD:
			return new MagicFreudFilter(mContext);
		case MagicFilterType.HEFE:
			return new MagicHefeFilter(mContext);
		case MagicFilterType.HUDSON:
			return new MagicHudsonFilter(mContext);
		case MagicFilterType.INKWELL:
			return new MagicInkwellFilter(mContext);
		case MagicFilterType.KEVIN:
			return new MagicKevinFilter(mContext);
		case MagicFilterType.LOMO:
			return new MagicLomoFilter(mContext);
		case MagicFilterType.N1977:
			return new MagicN1977Filter(mContext);
		case MagicFilterType.NASHVILLE:
			return new MagicNashvilleFilter(mContext);
		case MagicFilterType.PIXAR:
			return new MagicPixarFilter(mContext);
		case MagicFilterType.RISE:
			return new MagicRiseFilter(mContext);
		case MagicFilterType.SIERRA:
			return new MagicSierraFilter(mContext);
		case MagicFilterType.SUTRO:
			return new MagicSutroFilter(mContext);
		case MagicFilterType.TOASTER2:
			return new MagicToasterFilter(mContext);
		case MagicFilterType.VALENCIA:
			return new MagicValenciaFilter(mContext);
		case MagicFilterType.XPROII:
			return new MagicXproIIFilter(mContext);
		case MagicFilterType.EVERGREEN:
			return new MagicEvergreenFilter(mContext);
		case MagicFilterType.HEALTHY:
			return new MagicHealthyFilter(mContext);
		case MagicFilterType.COOL:
			return new MagicCoolFilter(mContext);
		case MagicFilterType.EMERALD:
			return new MagicEmeraldFilter(mContext);
		case MagicFilterType.LATTE:
			return new MagicLatteFilter(mContext);
		case MagicFilterType.WARM:
			return new MagicWarmFilter(mContext);
		case MagicFilterType.TENDER:
			return new MagicTenderFilter(mContext);
		case MagicFilterType.SWEETS:
			return new MagicSweetsFilter(mContext);
		case MagicFilterType.NOSTALGIA:
			return new MagicNostalgiaFilter(mContext);
		case MagicFilterType.FAIRYTALE:
			return new MagicFairytaleFilter(mContext);
		case MagicFilterType.SUNRISE:
			return new MagicSunriseFilter(mContext);
		case MagicFilterType.SUNSET:
			return new MagicSunsetFilter(mContext);
		case MagicFilterType.CRAYON:
			return new MagicCrayonFilter(mContext);
		case MagicFilterType.SKETCH:
			return new MagicSketchFilter(mContext);
			
		case MagicFilterType.BRIGHTNESS:
			return new GPUImageBrightnessFilter();
		case MagicFilterType.CONTRAST:
			return new GPUImageContrastFilter();
		case MagicFilterType.EXPOSURE:
			return new GPUImageExposureFilter();
		case MagicFilterType.HUE:
			return new GPUImageHueFilter();
		case MagicFilterType.SATURATION:
			return new GPUImageSaturationFilter();
		case MagicFilterType.SHARPEN:
			return new GPUImageSharpenFilter();
		case MagicFilterType.IMAGE_ADJUST:
			return new MagicImageAdjustFilter();
			
			
			
		case MagicFilterType.SUMIAO:
			return new MagicSuMiaoFilter(mContext);
		default:
			return null;
		}
	}
	
	public int getFilterType(){
		return mFilterType;
	}
}
