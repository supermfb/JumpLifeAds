package com.jumplife.ads.entity;

import android.widget.ImageView.ScaleType;

public class AdEntity {
	
	private int adsImpression;
	private String imgUrl;
	private String description;
	private int actionType;
	private String actionUrl;
	private ScaleType mScaleType;
	
	
	public AdEntity (int adsImpression, String imgUrl, String description, int actionType, String actionUrl, ScaleType mScaleType) {
		this.adsImpression = adsImpression;
		this.imgUrl = imgUrl;
		this.description = description;
		this.actionType = actionType;
		this.actionUrl = actionUrl;
		this.mScaleType = mScaleType;
	}

	public int getAdsImpression() {
	return adsImpression;
	}

	public void setAdsImpression(int adsImpression) {
	this.adsImpression = adsImpression;
	}
	
	public String getImgUrl() {
	return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
	this.imgUrl = imgUrl;
	}
	
	public String getDescription() {
	return description;
	}

	public void setDescription(String description) {
	this.description = description;
	}

	public int getActionType() {
	return actionType;
	}

	public void setActionType(int actionType) {
	this.actionType = actionType;
	}
	
	public String getActionUrl() {
	return actionUrl;
	}

	public void setActionUrl(String actionUrl) {
	this.actionUrl = actionUrl;
	}
	
	public ScaleType getScaleType() {
	return mScaleType;
	}

	public void setScaleType(ScaleType mScaleType) {
	this.mScaleType = mScaleType;
	}
}
