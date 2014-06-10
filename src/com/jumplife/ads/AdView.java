package com.jumplife.ads;

import java.io.IOException;
import java.util.Random;

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
	 * timeStamp : 紀錄向Server提取新廣告串列的時間
	 * currentAdEntity : 紀錄目前目前播放至哪一個廣告
	 * adEntity : ads set
	 * timeRotator : 輪播的時間間距
	 */
	private static SparseArray<Long> timeStamp;
	private static SparseArray<Integer> currentAdEntity;
	private static SparseArray<SparseArray<AdEntity>> adEntitys;	
	private static int timeRotator = 10000;
	
	private ImageLoader imageLoader;
	private ImageView ivAdView;
	private Bitmap bp;
	
	private OkHttpClient client = new OkHttpClient();

    private Handler adRequestHandler;
    private Runnable adRequestRunnable;
	
	public AdView(Activity context, int width, int height, int adsType) {
		super(context);
	    
		InitSetting(context);
		SetAdsize(context, width, height, adsType);
		
		ivAdView = new ImageView(context);
		ivAdView.setLayoutParams(new LayoutParams(this.width, this.height));
		ivAdView.setScaleType(ScaleType.FIT_CENTER);

		addView(ContentLayout.getLayout(context, adsType, ivAdView));
		
		HandlerThread thread = new HandlerThread("AdRequestThread");
	    thread.start();
	    adRequestHandler = new Handler(thread.getLooper());
	    adRequestRunnable = new AdRequestRunnable(context);
	    adRequestHandler.post(adRequestRunnable);
	}
	
	public void StopLoading() {
		imageLoader.stop();
		
	}

	public void Resume() {
		imageLoader.resume();
		
	}
	
	public void Destroy() {
		imageLoader.destroy();
		adRequestHandler.removeCallbacks(adRequestRunnable);
	}

	/*
	 * Init Setting
	 */
	private void InitSetting(Activity context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
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
	private void SetAdsize(Activity context, int width, int height, int adsType) {
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
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
	
	class AdRequestRunnable implements Runnable {

		Activity context;
		Uri url;
		
		boolean suspended = false;
		
		AdRequestRunnable(Activity context) {
			this.context = context;
		}
		
		@Override
		public void run() {
			try {
        		
				synchronized(this) {
					while(suspended)
						wait();
				}
		            
        		String result = "abc";
        		/*try {
        			result = AdRequest("");
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}*/
        		
        		synchronized(this) {
					while(suspended)
						wait();
				}
        		//if (result != null) {
        			Uri url = Uri.parse(result);
        			setView(context, url);
        		//}
        		adRequestHandler.postDelayed(this, timeRotator);
        		
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
	
	/*
	 * Set ImageView
	 */
	private void setView (Activity context, Uri url) {
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
		
		bp = imageLoader.loadImageSync("drawable://" + imageId);
		
		ivAdView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		if (context != null && ivAdView != null)
			context.runOnUiThread( new Runnable() {
			    public void run() {
			    	ivAdView.setImageBitmap(bp);
			    }
			});
	}
}
