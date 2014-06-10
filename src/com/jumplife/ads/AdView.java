package com.jumplife.ads;

import java.io.IOException;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jumplife.ads.entity.AdEntity;
import com.jumplife.jumplifeads.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

public class AdView extends RelativeLayout {

	/*
	 * Adview Type and Size
	 */
	private int adsType;
	private int width;
	private int height;
	
	/*
	 * Ad Data
	 * currentAdEntity : 紀錄目前目前播放至哪一個廣告
	 * adEntity : ads set
	 * timeRotator : 輪播的時間間距
	 */
	private static SparseIntArray currentAdEntity;
	private static SparseArray<SparseArray<AdEntity>> typeAdEntitys;	
	private static int timeRotator = 10000;
	private static int adsRequestTime = 7200000;
	
	private ImageLoader imageLoader;
	private ImageView ivAdView;
	private Bitmap bp;
	
	private OkHttpClient client = new OkHttpClient();

    private Handler adRequestHandler;
    private AdRequestRunnable adRequestRunnable;
    private Handler adRotatorHandler;
    private AdRotatorRunnable adRotatorRunnable;
	
	public AdView(Activity mActivity, int width, int height, int adsType) {
		super(mActivity);
	    
		InitSetting(mActivity);
		SetAdsize(mActivity, width, height, adsType);
	    
	    HandlerThread adRotatorThread = new HandlerThread("AdRotatorThread");
	    adRotatorThread.start();
	    adRotatorHandler = new Handler(adRotatorThread.getLooper());
	    adRotatorRunnable = new AdRotatorRunnable(mActivity);
		
		HandlerThread adRequestThread = new HandlerThread("AdRequestThread");
		adRequestThread.start();
	    adRequestHandler = new Handler(adRequestThread.getLooper());
	    adRequestRunnable = new AdRequestRunnable(mActivity);
	    adRequestHandler.post(adRequestRunnable);
	}
	
	public void StopLoading() {
		imageLoader.stop();
		adRequestRunnable.suspend();
		adRotatorRunnable.suspend();		
	}

	public void Resume() {
		imageLoader.resume();
		adRequestRunnable.resume();
		adRotatorRunnable.resume();
	}
	
	public void Destroy() {
		imageLoader.destroy();
		adRequestHandler.removeCallbacks(adRequestRunnable);
		adRotatorHandler.removeCallbacks(adRotatorRunnable);
	}

	/*
	 * Init Setting
	 */
	private void InitSetting(Activity mActivity) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity)
        .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
        .denyCacheImageMultipleSizesInMemory()
        .memoryCache(new WeakMemoryCache())
        .memoryCacheSize(2 * 1024 * 1024)
        .discCacheSize(50 * 1024 * 1024)
        .discCacheFileCount(50)
        .writeDebugLogs()
        .build();
		ImageLoader.getInstance().init(config);
		imageLoader = ImageLoader.getInstance();
	}
	
	/*
	 * InitAdSize
	 */
	private void SetAdsize(Activity mActivity, int width, int height, int adsType) {
		DisplayMetrics metrics = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		this.width = width;
		this.height = height;
		
		if (width == LayoutParams.MATCH_PARENT)
			this.width = metrics.widthPixels;
		
		if (height == LayoutParams.MATCH_PARENT)
			this.height = metrics.heightPixels;
	}
	
	/*
	 * OkHttp request Ads
	 */
	private synchronized String AdRequest (String link) throws IOException {
		 
		client = new OkHttpClient();
		
		Request request = new Request.Builder()
        .url(link)
        .build();

		// Execute the request and retrieve the response.
		Response response = client.newCall(request).execute();

		return response.body().string();
	}
	
	/*
	 * Set ImageView
	 */
	private void setView (Activity mActivity) {
		
		ivAdView = new ImageView(mActivity);
		ivAdView.setLayoutParams(new LayoutParams(this.width, this.height));
		ivAdView.setScaleType(typeAdEntitys.get(adsType).valueAt(currentAdEntity.get(adsType)).getScaleType());

		addView(ContentLayout.getLayout(mActivity, adsType, ivAdView, typeAdEntitys.get(adsType).valueAt(currentAdEntity.get(adsType))));
	}
	
	class AdRequestRunnable implements Runnable {

		Activity mActivity;
		Uri url;
		
		boolean suspended = false;
		
		AdRequestRunnable(Activity mActivity) {
			this.mActivity = mActivity;
		}
		
		@Override
		public void run() {
			try {
        		
				synchronized(this) {
					while(suspended)
						wait();
				}
		            
        		String result = null;
        		try {
        			result = AdRequest("");
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        		
        		if (result != null) {
        			/*
        			 * Init Sparse Array
        			 */
        			typeAdEntitys = new SparseArray<SparseArray<AdEntity>>();
        			currentAdEntity = new SparseIntArray();
        			
        			JSONObject jsonObject = new JSONObject(result.toString());
        			JSONArray jsonArray = jsonObject.getJSONArray("");
        			SparseArray<AdEntity> adEntitys= new SparseArray<AdEntity>();
        			for (int i=0; i<jsonArray.length(); i++) {
        				JSONObject tmpObject = jsonArray.getJSONObject(i);
        				int adsID = 0;
        				if (tmpObject.has(""))
        					adsID = tmpObject.getInt("");
        				int adsImpression = 0;
        				if (tmpObject.has(""))
        					adsImpression = tmpObject.getInt("");
        				String imgUrl = "";
        				if (tmpObject.has(""))
        					imgUrl = tmpObject.getString("");
        				String description = "";
        				if (tmpObject.has(""))
        					description = tmpObject.getString("");
        				int actionType = 0;
        				if (tmpObject.has(""))
        					actionType = tmpObject.getInt("");
        				String actionUrl = "";
        				if (tmpObject.has(""))
        					actionUrl = tmpObject.getString("");
        				ScaleType mScaleType = ScaleType.CENTER_CROP;
        				if (tmpObject.has(""))
        					mScaleType = (ScaleType) tmpObject.get("");
        				AdEntity tmpAdEntity = new AdEntity(adsImpression, imgUrl, description, actionType, actionUrl, mScaleType);
        				adEntitys.append(adsID, tmpAdEntity);
        			}
        			typeAdEntitys.append(adsType, adEntitys);
        			currentAdEntity.append(adsType, 0);
        			timeRotator = jsonObject.getInt("");
        		}
        		
        		synchronized(this) {
					while(suspended)
						wait();
				}
        		if (result != null) {
        			setView(mActivity);
        			displayAds(mActivity);
        		}
        		if (mActivity != null && ivAdView != null) {
            		adRotatorHandler.postDelayed(this, timeRotator);
            		adRequestHandler.postDelayed(this, adsRequestTime);
        		}
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        		adRequestHandler.removeCallbacks(adRequestRunnable);
        	}
		}
		
		public void suspend() {
			suspended = true;
		}
		
		public synchronized void resume() {
			suspended = false;
			notify();
		}
		
	}
	
	private void displayAds (Activity mActivity) {
		/*Random r = new Random();
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
		
		bp = imageLoader.loadImageSync("drawable://" + imageId);*/
		bp = imageLoader.loadImageSync(typeAdEntitys.get(adsType).valueAt(currentAdEntity.get(adsType)).getImgUrl());
		
		ivAdView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		if (mActivity != null && ivAdView != null)
			mActivity.runOnUiThread( new Runnable() {
			    public void run() {
			    	ivAdView.setImageBitmap(bp);
			    }
			});
	}

	class AdRotatorRunnable implements Runnable {

		Activity mActivity;
		Uri url;
		
		boolean suspended = false;
		
		AdRotatorRunnable(Activity mActivity) {
			this.mActivity = mActivity;
		}
		
		@Override
		public void run() {
			try {
        		
				synchronized(this) {
					while(suspended)
						wait();
				}
        		
        		if (mActivity != null && ivAdView != null) {
        			displayAds(mActivity);
        			adRotatorHandler.postDelayed(this, timeRotator);
        		}
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        		adRotatorHandler.removeCallbacks(adRotatorRunnable);
        	}
		}
		
		public void suspend() {
			suspended = true;
		}
		
		public synchronized void resume() {
			suspended = false;
			notify();
		}
		
	}
}
