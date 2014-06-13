package com.jumplife.ads;

import java.util.Random;

import com.jumplife.ads.entity.AdEntity;
import com.jumplife.jumplifeads.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentLayout {
	public static RelativeLayout getLayout(Activity mActivity, ImageLoader imageLoader, DisplayImageOptions options, 
			int adsType, TextView tvAdView, ImageView ivAds, AdEntity adEntity) {
		
		RelativeLayout tmpLayout = new RelativeLayout(mActivity);
		
		switch(adsType) {
			case 0:				// Banner
				
				/*
				 * fake data
				 */
				Random r = new Random();
				int i1 = r.nextInt(5) + 2;
				int imageId = R.drawable.ads1;
				if ( i1 == 2 )
					imageId = R.drawable.ads2;
				else if ( i1 == 3 )
					imageId = R.drawable.ads3;
				else if ( i1 == 4 )
					imageId = R.drawable.ads4;
				else if ( i1 == 5 )
					imageId = R.drawable.ads5;		
				/*
				 * fake data
				 */
				
				DisplayMetrics metrics = new DisplayMetrics();
				mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
				int width = metrics.widthPixels;
				
				LayoutParams layoutParams = new LayoutParams(width, width * 100 / 640);
				ivAds.setLayoutParams(layoutParams);
				ivAds.setScaleType(adEntity.getScaleType());
				
				imageLoader.displayImage("drawable://" + imageId, ivAds, options);
				
				tmpLayout.addView(ivAds, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
			
		
		return tmpLayout;	
	}
}
