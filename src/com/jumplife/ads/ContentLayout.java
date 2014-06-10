package com.jumplife.ads;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ContentLayout {
	public static RelativeLayout getLayout(Activity mActivity, int adsType, ImageView ivAds) {
		
		RelativeLayout tmpLayout = new RelativeLayout(mActivity);
		
		switch(adsType) {
			case 0:
				tmpLayout.addView(ivAds, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
			
		
		return tmpLayout;	
	}
}
