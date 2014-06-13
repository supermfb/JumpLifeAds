package com.jumplife.ads;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jumplife.ads.entity.AdEntity;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.app.Activity;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AdView extends RelativeLayout {

	private Activity mActivity;
	/*
	 * Adview Type and Size
	 */
	private int adsType;
	
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

	private RelativeLayout rlAdLayout;
	private TextView tvAdView;
	private ImageView ivAdView;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	private OkHttpClient client = new OkHttpClient();

    private Handler adRequestHandler;
    private AdRequestRunnable adRequestRunnable;
    private Handler adRotatorHandler;
    private AdRotatorRunnable adRotatorRunnable;
	
	public AdView(Activity mActivity, int width, int height, int adsType) {
		super(mActivity);
	    
		this.mActivity = mActivity;
		InitView();
		InitSetting();
	    
		HandlerThread adRatotorThread = new HandlerThread("AdRotatorThread");
		adRatotorThread.start();
	    adRotatorHandler = new Handler(adRatotorThread.getLooper());
	    adRotatorRunnable = new AdRotatorRunnable();
		
		HandlerThread adRequestThread = new HandlerThread("AdRequestThread");
		adRequestThread.start();
	    adRequestHandler = new Handler(adRequestThread.getLooper());
	    adRequestRunnable = new AdRequestRunnable();
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

	private void InitView() {
		
		tvAdView = new TextView(mActivity);
		ivAdView = new ImageView(mActivity);
		
	}
	
	/*
	 * Init Setting
	 */
	private void InitSetting() {
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
		
		options = new DisplayImageOptions.Builder()
	    .resetViewBeforeLoading(false)
	    .imageScaleType(ImageScaleType.EXACTLY)
	    .build();
		
		imageLoader = ImageLoader.getInstance();
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
	private void setView () {
		
		if (mActivity != null)
			mActivity.runOnUiThread( new Runnable() {
			    public void run() {
					
					int index = 0;//currentAdEntity.get(adsType);

			    	removeAllViews();
			    	if (rlAdLayout != null)
			    		rlAdLayout.removeAllViews();
			    	
					/*
					rlAdLayout = ContentLayout.getLayout(mActivity, imageLoader, options, adsType, tvAdView, ivAdView
							typeAdEntitys.get(adsType).valueAt(index));
					tvAdView.setText(typeAdEntitys.get(adsType).valueAt(index).getDescription());
					*/
					
					/*
					 * fake data
					 */			    	
					AdEntity tmpAdEntitys = new AdEntity(0, "", "", 0, "", ScaleType.CENTER_CROP);
					rlAdLayout = ContentLayout.getLayout(mActivity, imageLoader, options, adsType, tvAdView, ivAdView, tmpAdEntitys);
					
					tvAdView.setText(tmpAdEntitys.getDescription());
					/*
					 * fake data
					 */
					
					rlAdLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
						}
						
					});
					
					addView(rlAdLayout);
					
					/*int adsImpression = typeAdEntitys.get(adsType).valueAt(currentAdEntity.get(adsType)).getAdsImpression();
					typeAdEntitys.get(adsType).valueAt(currentAdEntity.get(adsType)).setAdsImpression(adsImpression+1);*/
					
					index += 1;
					/*if (index >= currentAdEntity.size())
						index = 0;
					currentAdEntity.put(adsType, index);*/
			    }
			});
	}
	
	class AdRequestRunnable implements Runnable {

		Uri url;
		
		boolean suspended = false;
		
		AdRequestRunnable() {
		}
		
		@Override
		public void run() {
			try {
        		
				synchronized(this) {
					while(suspended)
						wait();
				}
		            
        		String result = null;
        		/*try {
        			result = AdRequest("");
        		} catch (IOException e) {
        			e.printStackTrace();
        		}*/
        		
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
        		//if (typeAdEntitys.get(adsType) != null && typeAdEntitys.get(adsType).size() > 0) {
        			setView();
            		if (mActivity != null) {
                		adRotatorHandler.postDelayed(this, timeRotator);
                		adRequestHandler.postDelayed(this, adsRequestTime);
            		}
        		//}
        		
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

	class AdRotatorRunnable implements Runnable {

		Uri url;
		
		boolean suspended = false;
		
		AdRotatorRunnable() {
		}
		
		@Override
		public void run() {
			try {
        		
				synchronized(this) {
					while(suspended)
						wait();
				}
        		
        		if (mActivity != null) {
        			setView();
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
