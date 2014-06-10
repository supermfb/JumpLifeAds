package com.jumplife.ads;

import com.jumplife.ads.entity.AdEntity;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContentLayout {
	public static RelativeLayout getLayout(Activity mActivity, int adsType, TextView tvAdView, ImageView ivAds, AdEntity adEntity) {
		
		RelativeLayout tmpLayout = new RelativeLayout(mActivity);
		
		switch(adsType) {
			case 0:
				tmpLayout.addView(ivAds, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
			
		
		return tmpLayout;	
	}
}
