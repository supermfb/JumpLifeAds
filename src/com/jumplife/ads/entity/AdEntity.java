package com.jumplife.ads.entity;

public class AdEntity {
	
	private int type;
	private String link;
	
	
	public AdEntity (int type, String link) {
		this.type = type;
		this.link = link;
	}
	
	public String getLink() {
	return link;
	}

	public void setLink(String link) {
	this.link = link;
	}

	public int getType() {
	return type;
	}

	public void setType(int type) {
	this.type = type;
	}
}
